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
package ru.aleshin.features.editor.impl.presentation.ui.editor.store

import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.editor.api.EditorOutput
import ru.aleshin.features.editor.impl.domain.common.convertToEditModel
import ru.aleshin.features.editor.impl.presentation.mappers.mapToUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditModelUi
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorAction
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEvent
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorInput
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorState
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.EditorWorkCommand
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.EditorWorkProcessor
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.TimeTaskWorkCommand
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.TimeTaskWorkProcessor
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal class EditorComposeStore @Inject constructor(
    private val timeTaskWorkProcessor: TimeTaskWorkProcessor,
    private val editorWorkProcessor: EditorWorkProcessor,
    private val timeRangeValidator: TimeRangeValidator,
    private val categoryValidator: CategoryValidator,
    stateCommunicator: StateCommunicator<EditorState>,
    effectCommunicator: EffectCommunicator<EditorEffect>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<EditorState, EditorEvent, EditorAction, EditorEffect, EditorInput, EditorOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: EditorInput, isRestore: Boolean) {
        dispatchEvent(EditorEvent.Init(input, isRestore))
    }

    override suspend fun WorkScope<EditorState, EditorAction, EditorEffect, EditorOutput>.handleEvent(
        event: EditorEvent,
    ) {
        when (event) {
            is EditorEvent.Init -> with(event) {
                if (!isRestore) {
                    val editModel = input.timeTask.convertToEditModel(
                        template = input.template,
                        undefinedTaskId = input.undefinedTaskId,
                    )
                    val command = EditorWorkCommand.SetupEditModel(editModel.mapToUi())
                    editorWorkProcessor.work(command).handleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_TEMPLATES) {
                    val templatesCommand = EditorWorkCommand.LoadTemplates
                    editorWorkProcessor.work(templatesCommand).handleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_SETTINGS) {
                    val settingsCommand = EditorWorkCommand.LoadTasksSettings
                    editorWorkProcessor.work(settingsCommand).handleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_UNDEFINED_TASKS) {
                    val tasksCommand = TimeTaskWorkCommand.LoadUndefinedTasks
                    timeTaskWorkProcessor.work(tasksCommand).handleWork()
                }
            }
            is EditorEvent.ChangeTime -> updateEditModel {
                copy(timeRange = event.timeRange, duration = duration(event.timeRange))
            }
            is EditorEvent.ChangeParameters -> updateEditModel {
                copy(parameters = event.parameters)
            }
            is EditorEvent.UpdateDurationPresets -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val command = EditorWorkCommand.UpdateDurationPresets(event.presets)
                editorWorkProcessor.work(command).handleWork()
            }
            is EditorEvent.ChangeCategories -> updateEditModel {
                copy(mainCategory = event.category, subCategory = event.subCategory)
            }
            is EditorEvent.ChangeNote -> updateEditModel {
                copy(note = event.note)
            }
            is EditorEvent.AddSubCategory -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val mainCategory = checkNotNull(state().editModel?.mainCategory)
                val command = EditorWorkCommand.AddSubCategory(event.name, mainCategory)
                editorWorkProcessor.work(command).handleWork()
            }
            is EditorEvent.CreateTemplate -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val command = EditorWorkCommand.AddTemplate(checkNotNull(state().editModel))
                editorWorkProcessor.work(command).handleWork()
            }
            is EditorEvent.ApplyTemplate -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val command = EditorWorkCommand.ApplyTemplate(event.template, checkNotNull(state().editModel))
                editorWorkProcessor.work(command).handleWork()
            }
            is EditorEvent.ApplyUndefinedTask -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val command = EditorWorkCommand.ApplyUndefinedTask(event.task, checkNotNull(state().editModel))
                editorWorkProcessor.work(command).handleWork()
            }
            is EditorEvent.PressDeleteButton -> launchBackgroundWork(BackgroundKey.DELETE_MODEL) {
                val command = TimeTaskWorkCommand.DeleteModel(checkNotNull(state().editModel))
                timeTaskWorkProcessor.work(command).handleWork()
            }
            is EditorEvent.PressSaveButton -> launchBackgroundWork(BackgroundKey.SAVE_MODEL) {
                with(checkNotNull(state().editModel)) {
                    val timeValidate = timeRangeValidator.validate(timeRange)
                    val categoryValidate = categoryValidator.validate(mainCategory)
                    if (timeValidate.isValid && categoryValidate.isValid) {
                        val command = TimeTaskWorkCommand.AddOrSaveModel(this)
                        timeTaskWorkProcessor.work(command).handleWork()
                    } else {
                        val action = EditorAction.SetValidError(timeValidate.validError, categoryValidate.validError)
                        sendAction(action)
                    }
                }
            }
            is EditorEvent.NavigateToCategoryEditor -> {
                consumeOutput(EditorOutput.NavigateToCategories(event.category.id))
            }
            is EditorEvent.NavigateToSubCategoryEditor -> {
                consumeOutput(EditorOutput.NavigateToCategories(event.category.mainCategory.id))
            }
            is EditorEvent.PressControlTemplateButton -> {
                consumeOutput(EditorOutput.NavigateToTemplates)
            }
            is EditorEvent.PressBackButton -> {
                consumeOutput(EditorOutput.NavigateToBack)
            }
        }
    }

    override suspend fun reduce(
        action: EditorAction,
        currentState: EditorState,
    ) = when (action) {
        is EditorAction.Navigate -> currentState.copy()
        is EditorAction.SetUp -> currentState.copy(
            editModel = action.editModel,
            categories = action.categories,
            timeRangeValid = null,
            categoryValid = null,
        )
        is EditorAction.UpdateCategories -> currentState.copy(
            categories = action.categories,
        )
        is EditorAction.UpdateTemplates -> currentState.copy(
            templates = action.templates,
        )
        is EditorAction.UpdateDurationPresets -> currentState.copy(
            durationPresets = action.presets,
        )
        is EditorAction.UpdateUndefinedTasks -> currentState.copy(
            undefinedTasks = action.tasks,
        )
        is EditorAction.UpdateEditModel -> currentState.copy(
            editModel = action.editModel,
        )
        is EditorAction.UpdateTemplateId -> currentState.copy(
            editModel = currentState.editModel?.copy(templateId = action.templateId),
        )
        is EditorAction.SetValidError -> currentState.copy(
            timeRangeValid = action.timeRange,
            categoryValid = action.category,
        )
    }

    private suspend fun WorkScope<EditorState, EditorAction, EditorEffect, EditorOutput>.updateEditModel(
        onTransform: EditModelUi.() -> EditModelUi,
    ) {
        val editModel = checkNotNull(state().editModel)
        sendAction(EditorAction.UpdateEditModel(onTransform(editModel)))
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_TEMPLATES, LOAD_SETTINGS, LOAD_UNDEFINED_TASKS, SAVE_MODEL, DELETE_MODEL, DATA_ACTION
    }

    class Factory @Inject constructor(
        private val timeTaskWorkProcessor: TimeTaskWorkProcessor,
        private val editorWorkProcessor: EditorWorkProcessor,
        private val timeRangeValidator: TimeRangeValidator,
        private val categoryValidator: CategoryValidator,
        private val coroutineManager: CoroutineManager,
    ) : BaseComposeStore.Factory<EditorComposeStore, EditorState> {

        override fun create(savedState: EditorState): EditorComposeStore {
            return EditorComposeStore(
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
