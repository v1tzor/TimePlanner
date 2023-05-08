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
 * imitations under the License.
 */
package ru.aleshin.features.editor.impl.presentation.ui.editor.processors

import kotlinx.coroutines.delay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.platform.screenmodel.work.*
import ru.aleshin.features.editor.api.domain.EditModel
import ru.aleshin.features.editor.api.domain.convertToTemplate
import ru.aleshin.features.editor.api.presentation.TimeTaskAlarmManager
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TimeTaskInteractor
import ru.aleshin.features.editor.impl.navigation.NavigationManager
import ru.aleshin.features.editor.impl.presentation.mappers.EditModelToTimeTaskMapper
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorAction
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
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
        private val mapperToTimeTask: EditModelToTimeTaskMapper,
    ) : TimeTaskWorkProcessor {

        override suspend fun work(command: TimeTaskWorkCommand) = when (command) {
            is TimeTaskWorkCommand.DeleteModel -> deleteModel(command.editModel)
            is TimeTaskWorkCommand.AddOrSaveModel -> saveOrAddModel(command.editModel, command.isTemplateUpdate)
            is TimeTaskWorkCommand.LoadTemplateTimeTasks -> loadTemplates()
        }

        private suspend fun deleteModel(editModel: EditModel): WorkResult<EditorAction, EditorEffect> {
            val timeTask = editModel.map(mapperToTimeTask)
            if (editModel.key != 0L) {
                val deleteResult = timeTaskInteractor.deleteTimeTask(editModel.key)
                if (deleteResult is Either.Left) {
                    return EffectResult(EditorEffect.ShowError(deleteResult.data))
                } else {
                    timeTaskAlarmManager.deleteNotifyAlarm(timeTask)
                }
            }
            return navigationManager.navigateToPreviousFeature().let {
                ActionResult(EditorAction.Navigate)
            }
        }

        private suspend fun saveOrAddModel(
            editModel: EditModel,
            isTemplateUpdate: Boolean,
        ): WorkResult<EditorAction, EditorEffect> {
            val timeTask = mapperToTimeTask.map(editModel)
            val templateId = editModel.templateId
            if (templateId != null && isTemplateUpdate) {
                templatesInteractor.updateTemplate(editModel.convertToTemplate(templateId))
            }
            val saveResult = if (timeTask.key != 0L) {
                timeTaskInteractor.updateTimeTask(timeTask)
            } else {
                timeTaskInteractor.addTimeTask(timeTask)
            }

            return when (saveResult) {
                is Either.Right -> {
                    updateOrAddNotify(timeTask)
                    navigationManager.navigateToHomeScreen().let {
                        ActionResult(EditorAction.Navigate)
                    }
                }
                is Either.Left -> with(saveResult.data) {
                    val effect = if (this is EditorFailures.TimeOverlayError) {
                        EditorEffect.ShowOverlayError(editModel.timeRanges, this)
                    } else {
                        EditorEffect.ShowError(this)
                    }
                    EffectResult(effect)
                }
            }
        }

        private fun updateOrAddNotify(timeTask: TimeTask) {
            if (timeTask.isEnableNotification) {
                val currentTime = dateManager.fetchCurrentDate()
                if (timeTask.timeRanges.from > currentTime) {
                    if (timeTask.key != 0L) {
                        timeTaskAlarmManager.updateNotifyAlarm(timeTask)
                    } else {
                        timeTaskAlarmManager.addNotifyAlarm(timeTask)
                    }
                }
            }
        }

        private suspend fun loadTemplates(): WorkResult<EditorAction, EditorEffect> {
            delay(Constants.Delay.LOAD_ANIMATION)

            return when (val templates = templatesInteractor.fetchTemplates()) {
                is Either.Right -> ActionResult(EditorAction.UpdateTemplates(templates.data))
                is Either.Left -> EffectResult(EditorEffect.ShowError(templates.data))
            }
        }
    }
}

internal sealed class TimeTaskWorkCommand : WorkCommand {
    data class AddOrSaveModel(val editModel: EditModel, val isTemplateUpdate: Boolean) : TimeTaskWorkCommand()
    data class DeleteModel(val editModel: EditModel) : TimeTaskWorkCommand()
    object LoadTemplateTimeTasks : TimeTaskWorkCommand()
}
