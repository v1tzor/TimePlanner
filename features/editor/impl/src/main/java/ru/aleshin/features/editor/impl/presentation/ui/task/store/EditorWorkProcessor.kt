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
package ru.aleshin.features.editor.impl.presentation.ui.task.store

import kotlinx.coroutines.flow.flow
import ru.aleshin.core.presentation.mappers.mapToDomain
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.editor.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.SettingsInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.UndefinedTasksInteractor
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.convertToEditModel
import ru.aleshin.features.editor.impl.presentation.models.tasks.convertToTemplate
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskAction
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskEffect
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 26.03.2023.
 */
internal interface EditorWorkProcessor :
    FlowWorkProcessor<EditorWorkCommand, TaskAction, TaskEffect, TaskOutput> {

    class Base @Inject constructor(
        private val categoriesInteractor: CategoriesInteractor,
        private val undefinedTasksInteractor: UndefinedTasksInteractor,
        private val templatesInteractor: TemplatesInteractor,
        private val settingsInteractor: SettingsInteractor,
        private val dateManager: DateManager,
    ) : EditorWorkProcessor {

        override suspend fun work(command: EditorWorkCommand) = when (command) {
            is EditorWorkCommand.LoadCategories -> loadCategoriesWork()
            is EditorWorkCommand.LoadTasksSettings -> loadTasksSettingsWork()
            is EditorWorkCommand.LoadTemplates -> loadTemplatesWork()
            is EditorWorkCommand.LoadUndefinedTasks -> loadUndefinedTasksWork()
            is EditorWorkCommand.UpdateDurationPresets -> updateDurationPresetsWork(command.presets)
            is EditorWorkCommand.AddSubCategory -> addSubCategoryWork(command.name, command.mainCategory)
            is EditorWorkCommand.AddTemplate -> addTemplateWork(command.editModel)
            is EditorWorkCommand.UnlinkTemplate -> unlinkTemplateWork(command.editModel)
            is EditorWorkCommand.ApplyTemplate -> applyTemplateWork(command.template, command.model)
            is EditorWorkCommand.ApplyUndefinedTask -> applyUndefinedTaskWork(command.task, command.model)
        }

        private fun loadCategoriesWork() = flow {
            categoriesInteractor.fetchCategories().collectAndHandle(
                onLeftAction = { emit(EffectResult(TaskEffect.ShowError(it))) },
                onRightAction = { categories ->
                    val categories = categories.map { it.mapToUi() }
                    emit(ActionResult(TaskAction.UpdateCategories(categories)))
                }
            )
        }

        private fun loadTemplatesWork() = flow {
            templatesInteractor.fetchAllTemplates().collectAndHandle(
                onLeftAction = { emit(EffectResult(TaskEffect.ShowError(it))) },
                onRightAction = { templates ->
                    val templates = templates.map { it.mapToUi() }
                    emit(ActionResult(TaskAction.UpdateTemplates(templates)))
                }
            )
        }

        private fun loadUndefinedTasksWork() = flow {
            undefinedTasksInteractor.fetchAllUndefinedTasks().collectAndHandle(
                onLeftAction = { emit(EffectResult(TaskEffect.ShowError(it))) },
                onRightAction = { undefinedTasks ->
                    val undefinedTasks = undefinedTasks.map { it.mapToUi() }
                    emit(ActionResult(TaskAction.UpdateUndefinedTasks(undefinedTasks)))
                }
            )
        }

        private fun loadTasksSettingsWork() = flow {
            settingsInteractor.fetchTasksSettings().collectAndHandle(
                onLeftAction = { emit(EffectResult(TaskEffect.ShowError(it))) },
                onRightAction = { settings ->
                    emit(ActionResult(TaskAction.UpdateDurationPresets(settings.durationPresets)))
                }
            )
        }

        private fun updateDurationPresetsWork(presets: List<Long>) = flow {
            settingsInteractor.fetchTasksSettings().collectAndHandle(
                onLeftAction = { emit(EffectResult(TaskEffect.ShowError(it))) },
                onRightAction = { settings ->
                    settingsInteractor.updateTasksSettings(settings.copy(durationPresets = presets)).handle(
                        onLeftAction = { emit(EffectResult(TaskEffect.ShowError(it))) }
                    )
                }
            )
        }

        private fun addSubCategoryWork(name: String, mainCategory: MainCategoryUi) = flow {
            val subCategory = SubCategoryUi(id = 0, name = name, mainCategoryId = mainCategory.id)

            categoriesInteractor.addSubCategory(subCategory.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(TaskEffect.ShowError(it))) }
            )
        }

        private fun addTemplateWork(editModel: TimeTaskEditUi) = flow {
            val template = editModel.convertToTemplate().mapToDomain()
            templatesInteractor.addOrUpdateTemplate(template).handle(
                onLeftAction = { emit(EffectResult(TaskEffect.ShowError(it))) },
                onRightAction = { templateId ->
                    val updatedEditModel = editModel.copy(linkedTemplateId = templateId, isRepeat = false)
                    emit(ActionResult(TaskAction.UpdateEditModel(updatedEditModel)))
                }
            )
        }

        private fun unlinkTemplateWork(editModel: TimeTaskEditUi) = flow {
            val updatedEditModel = editModel.copy(linkedTemplateId = null, isRepeat = false)
            emit(ActionResult(TaskAction.UpdateEditModel(updatedEditModel)))
        }

        private fun applyTemplateWork(template: TemplateUi, model: TimeTaskEditUi) = flow {
            val updatedEditModel = template.convertToEditModel(
                scheduleDate = model.date,
                createdDate = model.createdAt ?: dateManager.fetchCurrentDate()
            ).copy(
                key = model.key,
                isCompleted = model.isCompleted
            )
            emit(ActionResult(TaskAction.UpdateEditModel(updatedEditModel)))
        }

        private fun applyUndefinedTaskWork(task: UndefinedTaskUi, model: TimeTaskEditUi) = flow {
            val currentTime = dateManager.fetchCurrentDate()
            val editModel = task.convertToEditModel(
                createdDate = currentTime,
                scheduleDate = model.date,
                timeRange = model.timeRange,
            ).copy(
                key = model.key
            )
            emit(ActionResult(TaskAction.UpdateEditModel(editModel)))
        }
    }
}

internal sealed class EditorWorkCommand : WorkCommand {
    data object LoadCategories : EditorWorkCommand()
    data object LoadTasksSettings : EditorWorkCommand()
    data object LoadTemplates : EditorWorkCommand()
    data object LoadUndefinedTasks : EditorWorkCommand()
    data class UpdateDurationPresets(val presets: List<Long>) : EditorWorkCommand()
    data class AddSubCategory(val name: String, val mainCategory: MainCategoryUi) : EditorWorkCommand()
    data class AddTemplate(val editModel: TimeTaskEditUi) : EditorWorkCommand()
    data class UnlinkTemplate(val editModel: TimeTaskEditUi) : EditorWorkCommand()
    data class ApplyTemplate(val template: TemplateUi, val model: TimeTaskEditUi) : EditorWorkCommand()
    data class ApplyUndefinedTask(val task: UndefinedTaskUi, val model: TimeTaskEditUi) : EditorWorkCommand()
}

internal typealias EditorWorkResult = WorkResult<TaskAction, TaskEffect, TaskOutput>
