/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.aleshin.features.editor.impl.presentation.ui.editor.store

import kotlinx.coroutines.flow.flow
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.presentation.mappers.mapToDomain
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.core.presentation.notifications.TimeTaskAlarmManager
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.OutputResult
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.functional.rightOrNull
import ru.aleshin.core.utils.functional.toRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.editor.api.EditorOutput
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TimeTaskInteractor
import ru.aleshin.features.editor.impl.domain.interactors.UndefinedTasksInteractor
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.convertToEditModel
import ru.aleshin.features.editor.impl.presentation.models.tasks.convertToTimeTask
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorAction
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal interface TimeTaskWorkProcessor :
    FlowWorkProcessor<TimeTaskWorkCommand, EditorAction, EditorEffect, EditorOutput> {

    class Base @Inject constructor(
        private val timeTaskInteractor: TimeTaskInteractor,
        private val templatesInteractor: TemplatesInteractor,
        private val undefinedTasksInteractor: UndefinedTasksInteractor,
        private val timeTaskAlarmManager: TimeTaskAlarmManager,
        private val dateManager: DateManager,
    ) : TimeTaskWorkProcessor {

        override suspend fun work(command: TimeTaskWorkCommand) = when (command) {
            is TimeTaskWorkCommand.SetupEditModel -> setupEditModelWork(
                timeTaskId = command.timeTaskId,
                timeRange = command.timeRange,
                date = command.date,
                undefinedTaskId = command.undefinedTaskId
            )
            is TimeTaskWorkCommand.AddOrSaveModel -> saveOrAddModelWork(
                editModel = command.editModel
            )
            is TimeTaskWorkCommand.DeleteModel -> deleteModelWork(
                editModel = command.editModel
            )
        }

        private fun setupEditModelWork(
            timeTaskId: Long?,
            timeRange: TimeRange?,
            date: Date?,
            undefinedTaskId: Long?,
        ) = flow {
            val currentTime = dateManager.fetchCurrentDate()
            val scheduleDate = (date ?: currentTime).startThisDay()
            val timeRange = timeRange ?: (scheduleDate toRange scheduleDate)

            val editModel = if (timeTaskId != null) {
                timeTaskInteractor.fetchTimeTaskById(timeTaskId)
                    .rightOrNull { emit(EffectResult(EditorEffect.ShowError(it))) }
                    ?.mapToUi()
                    ?.convertToEditModel()
            } else if (undefinedTaskId != null) {
                undefinedTasksInteractor.fetchUndefinedTaskById(undefinedTaskId)
                    .rightOrNull { emit(EffectResult(EditorEffect.ShowError(it))) }
                    ?.mapToUi()
                    ?.convertToEditModel(
                        createdDate = currentTime,
                        scheduleDate = scheduleDate,
                        timeRange = timeRange
                    )
            } else {
                TimeTaskEditUi.create(
                    createdDate = currentTime,
                    scheduleDate = scheduleDate,
                    timeRange = timeRange
                )
            }

            emit(ActionResult(EditorAction.UpdateEditModel(editModel)))
        }

        private fun saveOrAddModelWork(editModel: TimeTaskEditUi) = flow {
            val timeTask = editModel.convertToTimeTask().mapToDomain()
            val saveResult = when (timeTask.key != 0L) {
                true -> timeTaskInteractor.updateTimeTask(timeTask).mapRight { timeTask.key }
                false -> timeTaskInteractor.addTimeTask(timeTask)
            }
            saveResult.handle(
                onLeftAction = { failures ->
                    val effect = when (failures is EditorFailures.TimeOverlayError) {
                        true -> EditorEffect.ShowOverlayError(editModel.timeRange, failures)
                        false -> EditorEffect.ShowError(failures)
                    }
                    emit(EffectResult(effect))
                },
                onRightAction = { key ->
                    notifyUpdateOrAdd(timeTask.copy(key = key))
                    if (editModel.undefinedTaskId != null) {
                        undefinedTasksInteractor.deleteUndefinedTask(editModel.undefinedTaskId).rightOrNull {
                            emit(EffectResult(EditorEffect.ShowError(it)))
                        }
                    }
                    emit(OutputResult(EditorOutput.NavigateToBack))
                }
            )
        }

        private fun deleteModelWork(editModel: TimeTaskEditUi) = flow {
            if (editModel.key != 0L) {
                timeTaskInteractor.deleteTimeTaskById(editModel.key).handle(
                    onLeftAction = { emit(EffectResult(EditorEffect.ShowError(it))) },
                    onRightAction = {
                        val timeTask = editModel.convertToTimeTask().mapToDomain()
                        timeTaskAlarmManager.deleteNotifyAlarm(timeTask)
                    }
                )
            }
            emit(OutputResult(EditorOutput.NavigateToBack))
        }

        private fun notifyUpdateOrAdd(timeTask: TimeTask) {
            if (timeTask.isEnableNotification) {
                timeTaskAlarmManager.deleteNotifyAlarm(timeTask)
                timeTaskAlarmManager.addOrUpdateNotifyAlarm(timeTask)
            } else {
                timeTaskAlarmManager.deleteNotifyAlarm(timeTask)
            }
        }
    }
}

internal sealed class TimeTaskWorkCommand : WorkCommand {
    data class SetupEditModel(
        val timeTaskId: Long? = null,
        val timeRange: TimeRange? = null,
        val date: Date? = null,
        val undefinedTaskId: Long? = null
    ) : TimeTaskWorkCommand()
    data class AddOrSaveModel(val editModel: TimeTaskEditUi) : TimeTaskWorkCommand()
    data class DeleteModel(val editModel: TimeTaskEditUi) : TimeTaskWorkCommand()
}


internal typealias TimeTaskWorkResult = WorkResult<EditorAction, EditorEffect, EditorOutput>
