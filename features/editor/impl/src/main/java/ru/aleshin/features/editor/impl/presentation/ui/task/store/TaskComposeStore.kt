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

import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditUi
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskAction
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskEffect
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskEvent
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskInput
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskOutput
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskState
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.CategoryValidator
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.TimeRangeValidator
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal class TaskComposeStore @Inject constructor(
    private val timeTaskWorkProcessor: TimeTaskWorkProcessor,
    private val editorWorkProcessor: EditorWorkProcessor,
    private val timeRangeValidator: TimeRangeValidator,
    private val categoryValidator: CategoryValidator,
    stateCommunicator: StateCommunicator<TaskState>,
    effectCommunicator: EffectCommunicator<TaskEffect>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<TaskState, TaskEvent, TaskAction, TaskEffect, TaskInput, TaskOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: TaskInput, isRestore: Boolean) {
        dispatchEvent(TaskEvent.Init(input, isRestore))
    }

    override suspend fun WorkScope<TaskState, TaskAction, TaskEffect, TaskOutput>.handleEvent(
        event: TaskEvent,
    ) {
        when (event) {
            is TaskEvent.Init -> with(event) {
                if (!isRestore || state.editModel == null) {
                    launchBackgroundWork(BackgroundKey.LOAD_MODEL) {
                        val command = TimeTaskWorkCommand.SetupEditModel(
                            timeTaskId = input.timeTaskId,
                            timeRange = input.timeRange,
                            date = input.date,
                            undefinedTaskId = input.undefinedTaskId
                        )
                        timeTaskWorkProcessor.work(command).collectAndHandleWork()
                    }
                }
                launchBackgroundWork(BackgroundKey.LOAD_TEMPLATES) {
                    val command = EditorWorkCommand.LoadTemplates
                    editorWorkProcessor.work(command).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_CATEGORIES) {
                    val command = EditorWorkCommand.LoadCategories
                    editorWorkProcessor.work(command).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_SETTINGS) {
                    val command = EditorWorkCommand.LoadTasksSettings
                    editorWorkProcessor.work(command).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_UNDEFINED_TASKS) {
                    val command = EditorWorkCommand.LoadUndefinedTasks
                    editorWorkProcessor.work(command).collectAndHandleWork()
                }
            }
            is TaskEvent.UpdateDurationPresets -> {
                launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                    val command = EditorWorkCommand.UpdateDurationPresets(event.presets)
                    editorWorkProcessor.work(command).collectAndHandleWork()
                }
            }
            is TaskEvent.AddSubCategory -> with(state) {
                if (editModel != null) {
                    launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                        val command = EditorWorkCommand.AddSubCategory(event.name, editModel.mainCategory)
                        editorWorkProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is TaskEvent.CreateTemplate -> with(state) {
                if (editModel != null) {
                    launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                        val command = EditorWorkCommand.AddTemplate(editModel)
                        editorWorkProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is TaskEvent.PressUnlinkTemplateButton -> with(state) {
                if (editModel != null) {
                    launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                        val command = EditorWorkCommand.UnlinkTemplate(editModel)
                        editorWorkProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is TaskEvent.ApplyTemplate -> with(state) {
                if (editModel != null) {
                    launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                        val command = EditorWorkCommand.ApplyTemplate(event.template, editModel)
                        editorWorkProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is TaskEvent.ApplyUndefinedTask -> with(state) {
                if (editModel != null) {
                    launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                        val command = EditorWorkCommand.ApplyUndefinedTask(event.task, editModel)
                        editorWorkProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is TaskEvent.PressDeleteButton -> with(state){
                if (editModel != null) {
                    launchBackgroundWork(BackgroundKey.MODIFY_MODEL) {
                        val command = TimeTaskWorkCommand.DeleteModel(editModel)
                        timeTaskWorkProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is TaskEvent.PressSaveButton -> with(state) {
                launchBackgroundWork(BackgroundKey.MODIFY_MODEL) {
                    if (editModel != null) {
                        val timeValidate = timeRangeValidator.validate(editModel.timeRange)
                        val categoryValidate = categoryValidator.validate(editModel.mainCategory)

                        if (timeValidate.isValid && categoryValidate.isValid) {
                            val command = TimeTaskWorkCommand.AddOrSaveModel(editModel)
                            timeTaskWorkProcessor.work(command).collectAndHandleWork()
                        } else {
                            val action = TaskAction.SetValidError(timeValidate.validError, categoryValidate.validError)
                            sendAction(action)
                        }
                    }
                }
            }
            is TaskEvent.ChangeCategories -> updateEditModel {
                copy(mainCategory = event.category, subCategory = event.subCategory)
            }
            is TaskEvent.ChangeTime -> updateEditModel {
                copy(timeRange = event.timeRange, duration = duration(event.timeRange))
            }
            is TaskEvent.ChangeParameters -> updateEditModel {
                copy(parameters = event.parameters)
            }
            is TaskEvent.ChangeNote -> updateEditModel {
                copy(note = event.note)
            }
            is TaskEvent.NavigateToCategoryEditor -> {
                val config = EditorConfig.Categories(event.category.id)
                consumeOutput(TaskOutput.NavigateToCategories(config))
            }
            is TaskEvent.NavigateToSubCategoryEditor -> {
                val config = EditorConfig.Categories(event.category.mainCategoryId)
                consumeOutput(TaskOutput.NavigateToCategories(config))
            }
            is TaskEvent.PressControlTemplateButton -> {
                consumeOutput(TaskOutput.NavigateToTemplates)
            }
            is TaskEvent.PressBackButton -> {
                consumeOutput(TaskOutput.NavigateToBack)
            }
        }
    }

    override suspend fun reduce(
        action: TaskAction,
        currentState: TaskState,
    ) = when (action) {
        is TaskAction.UpdateEditModel -> currentState.copy(
            editModel = action.editModel
        )
        is TaskAction.UpdateCategories -> currentState.copy(
            categories = action.categories
        )
        is TaskAction.UpdateTemplates -> currentState.copy(
            templates = action.templates
        )
        is TaskAction.UpdateDurationPresets -> currentState.copy(
            durationPresets = action.presets
        )
        is TaskAction.UpdateUndefinedTasks -> currentState.copy(
            undefinedTasks = action.tasks
        )
        is TaskAction.SetValidError -> currentState.copy(
            timeRangeValid = action.timeRange,
            categoryValid = action.category
        )
    }

    private suspend fun WorkScope<TaskState, TaskAction, TaskEffect, TaskOutput>.updateEditModel(
        onTransform: TimeTaskEditUi.() -> TimeTaskEditUi,
    ) {
        val editModel = checkNotNull(state().editModel)
        sendAction(TaskAction.UpdateEditModel(onTransform(editModel)))
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_MODEL, LOAD_TEMPLATES, LOAD_CATEGORIES, LOAD_SETTINGS, LOAD_UNDEFINED_TASKS, MODIFY_MODEL, DATA_ACTION
    }

    class Factory @Inject constructor(
        private val timeTaskWorkProcessor: TimeTaskWorkProcessor,
        private val editorWorkProcessor: EditorWorkProcessor,
        private val timeRangeValidator: TimeRangeValidator,
        private val categoryValidator: CategoryValidator,
        private val coroutineManager: CoroutineManager,
    ) : BaseComposeStore.Factory<TaskComposeStore, TaskState> {

        override fun create(savedState: TaskState): TaskComposeStore {
            return TaskComposeStore(
                timeTaskWorkProcessor = timeTaskWorkProcessor,
                editorWorkProcessor = editorWorkProcessor,
                timeRangeValidator = timeRangeValidator,
                categoryValidator = categoryValidator,
                stateCommunicator = StateCommunicator.Default(savedState),
                effectCommunicator = EffectCommunicator.Default(),
                coroutineManager = coroutineManager,
            )
        }
    }
}
