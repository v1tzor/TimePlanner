/*
 * Copyright 2023 Stanislav Aleshin
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
package ru.aleshin.features.editor.impl.presentation.ui.editor.processors

import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.platform.screenmodel.work.*
import ru.aleshin.features.editor.api.presentation.TimeTaskAlarmManager
import ru.aleshin.features.editor.impl.domain.common.convertToTimeTask
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.interactors.TimeTaskInteractor
import ru.aleshin.features.editor.impl.domain.interactors.UndefinedTasksInteractor
import ru.aleshin.features.editor.impl.navigation.NavigationManager
import ru.aleshin.features.editor.impl.presentation.mappers.mapToDomain
import ru.aleshin.features.editor.impl.presentation.mappers.mapToUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditModelUi
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorAction
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal interface TimeTaskWorkProcessor : WorkProcessor<TimeTaskWorkCommand, EditorAction, EditorEffect> {

    class Base @Inject constructor(
        private val timeTaskInteractor: TimeTaskInteractor,
        private val undefinedTasksInteractor: UndefinedTasksInteractor,
        private val timeTaskAlarmManager: TimeTaskAlarmManager,
        private val navigationManager: NavigationManager,
    ) : TimeTaskWorkProcessor {

        override suspend fun work(command: TimeTaskWorkCommand) = when (command) {
            is TimeTaskWorkCommand.LoadUndefinedTasks -> loadUndefinedTasksWork()
            is TimeTaskWorkCommand.AddOrSaveModel -> saveOrAddModelWork(command.editModel)
            is TimeTaskWorkCommand.DeleteModel -> deleteModelWork(command.editModel)
        }

        private suspend fun loadUndefinedTasksWork(): WorkResult<EditorAction, EditorEffect> {
            return when (val tasks = undefinedTasksInteractor.fetchAllUndefinedTasks()) {
                is Either.Right -> ActionResult(EditorAction.UpdateUndefinedTasks(tasks.data.map { it.mapToUi() }))
                is Either.Left -> EffectResult(EditorEffect.ShowError(tasks.data))
            }
        }

        private suspend fun saveOrAddModelWork(editModel: EditModelUi): WorkResult<EditorAction, EditorEffect> {
            val domainModel = editModel.mapToDomain()
            val timeTask = domainModel.convertToTimeTask()
            val saveResult = when (timeTask.key != 0L) {
                true -> timeTaskInteractor.updateTimeTask(timeTask)
                false -> timeTaskInteractor.addTimeTask(timeTask)
            }
            return when (saveResult) {
                is Either.Right -> notifyUpdateOrAdd(timeTask.copy(key = saveResult.data)).let {
                    if (editModel.undefinedTaskId != null) {
                        undefinedTasksInteractor.deleteUndefinedTask(editModel.undefinedTaskId)
                    }
                    navigationManager.navigateToHomeScreen()
                    ActionResult(EditorAction.Navigate)
                }
                is Either.Left -> with(saveResult.data) {
                    val effect = when (this is EditorFailures.TimeOverlayError) {
                        true -> EditorEffect.ShowOverlayError(editModel.timeRange, this)
                        false -> EditorEffect.ShowError(this)
                    }
                    EffectResult(effect)
                }
            }
        }

        private suspend fun deleteModelWork(editModel: EditModelUi): WorkResult<EditorAction, EditorEffect> {
            val domainModel = editModel.mapToDomain()
            if (domainModel.key != 0L) {
                val deleteResult = timeTaskInteractor.deleteTimeTask(domainModel.key)
                if (deleteResult is Either.Left) {
                    return EffectResult(EditorEffect.ShowError(deleteResult.data))
                } else {
                    timeTaskAlarmManager.deleteNotifyAlarm(domainModel.convertToTimeTask())
                }
            }
            return navigationManager.navigateToBack().let {
                ActionResult(EditorAction.Navigate)
            }
        }

        private fun notifyUpdateOrAdd(timeTask: TimeTask) = if (timeTask.isEnableNotification) {
            timeTaskAlarmManager.deleteNotifyAlarm(timeTask)
            timeTaskAlarmManager.addOrUpdateNotifyAlarm(timeTask)
        } else {
            timeTaskAlarmManager.deleteNotifyAlarm(timeTask) 
        }
    }
}

internal sealed class TimeTaskWorkCommand : WorkCommand {
    object LoadUndefinedTasks : TimeTaskWorkCommand()
    data class AddOrSaveModel(val editModel: EditModelUi) : TimeTaskWorkCommand()
    data class DeleteModel(val editModel: EditModelUi) : TimeTaskWorkCommand()
}
