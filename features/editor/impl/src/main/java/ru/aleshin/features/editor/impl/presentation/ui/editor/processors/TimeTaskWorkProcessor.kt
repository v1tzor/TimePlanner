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

import kotlinx.coroutines.delay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.platform.screenmodel.work.*
import ru.aleshin.features.editor.api.presentation.TimeTaskAlarmManager
import ru.aleshin.features.editor.impl.domain.common.convertToTemplate
import ru.aleshin.features.editor.impl.domain.common.convertToTimeTask
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TimeTaskInteractor
import ru.aleshin.features.editor.impl.navigation.NavigationManager
import ru.aleshin.features.editor.impl.presentation.mappers.mapToDomain
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
        private val templatesInteractor: TemplatesInteractor,
        private val timeTaskAlarmManager: TimeTaskAlarmManager,
        private val navigationManager: NavigationManager,
        private val dateManager: DateManager,
    ) : TimeTaskWorkProcessor {

        override suspend fun work(command: TimeTaskWorkCommand) = when (command) {
            is TimeTaskWorkCommand.LoadTemplateTimeTasks -> loadTemplates()
            is TimeTaskWorkCommand.AddOrSaveModel -> saveOrAddModel(command.editModel)
            is TimeTaskWorkCommand.DeleteModel -> deleteModel(command.editModel)
        }

        private suspend fun loadTemplates(): WorkResult<EditorAction, EditorEffect> {
            delay(Constants.Delay.LOAD_ANIMATION)

            return when (val templates = templatesInteractor.fetchTemplates()) {
                is Either.Right -> ActionResult(EditorAction.UpdateTemplates(templates.data.map { it.mapToDomain() }))
                is Either.Left -> EffectResult(EditorEffect.ShowError(templates.data))
            }
        }

        private suspend fun saveOrAddModel(editModel: EditModelUi): WorkResult<EditorAction, EditorEffect> {
            val domainModel = editModel.mapToDomain()
            val timeTask = domainModel.convertToTimeTask()
            val saveResult = when (timeTask.key != 0L) {
                true -> timeTaskInteractor.updateTimeTask(timeTask)
                false -> timeTaskInteractor.addTimeTask(timeTask)
            }
            return when (saveResult) {
                is Either.Right -> notifyUpdateOrAdd(timeTask).let {
                    navigationManager.navigateToHomeScreen()
                    ActionResult(EditorAction.Navigate)
                }
                is Either.Left -> with(saveResult.data) {
                    val effect = when (this is EditorFailures.TimeOverlayError) {
                        true -> EditorEffect.ShowOverlayError(editModel.timeRanges, this)
                        false -> EditorEffect.ShowError(this)
                    }
                    EffectResult(effect)
                }
            }
        }

        private suspend fun deleteModel(editModel: EditModelUi): WorkResult<EditorAction, EditorEffect> {
            val domainModel = editModel.mapToDomain()
            if (domainModel.key != 0L) {
                val deleteResult = timeTaskInteractor.deleteTimeTask(domainModel.key)
                if (deleteResult is Either.Left) {
                    return EffectResult(EditorEffect.ShowError(deleteResult.data))
                } else {
                    timeTaskAlarmManager.deleteNotifyAlarm(domainModel.convertToTimeTask())
                }
            }
            return navigationManager.navigateToPreviousFeature().let {
                ActionResult(EditorAction.Navigate)
            }
        }

        private fun notifyUpdateOrAdd(timeTask: TimeTask) {
            if (timeTask.isEnableNotification) {
                val currentTime = dateManager.fetchCurrentDate()
                if (timeTask.timeRanges.from > currentTime) {
                    when (timeTask.key != 0L) {
                        true -> timeTaskAlarmManager.updateNotifyAlarm(timeTask)
                        false -> timeTaskAlarmManager.addNotifyAlarm(timeTask)
                    }
                }
            }
        }
    }
}

internal sealed class TimeTaskWorkCommand : WorkCommand {
    data class AddOrSaveModel(val editModel: EditModelUi) : TimeTaskWorkCommand()
    data class DeleteModel(val editModel: EditModelUi) : TimeTaskWorkCommand()
    object LoadTemplateTimeTasks : TimeTaskWorkCommand()
}
