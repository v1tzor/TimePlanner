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
package ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.features.editor.impl.di.holder.EditorComponentHolder
import ru.aleshin.features.editor.impl.presentation.models.EditModelUi
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorAction
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEvent
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorViewState
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.EditorWorkCommand
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.EditorWorkProcessor
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.TimeTaskWorkCommand
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.TimeTaskWorkProcessor
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal class EditorScreenModel @Inject constructor(
    private val timeTaskWorkProcessor: TimeTaskWorkProcessor,
    private val editorWorkProcessor: EditorWorkProcessor,
    private val timeRangeValidator: TimeRangeValidator,
    private val categoryValidator: CategoryValidator,
    stateCommunicator: EditorStateCommunicator,
    effectCommunicator: EditorEffectCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<EditorViewState, EditorEvent, EditorAction, EditorEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {
    override fun init() {
        if (!isInitialize.get()) {
            dispatchEvent(EditorEvent.Init)
            super.init()
        }
    }

    override suspend fun WorkScope<EditorViewState, EditorAction, EditorEffect>.handleEvent(
        event: EditorEvent,
    ) = when (event) {
        is EditorEvent.Init -> {
            editorWorkProcessor.work(EditorWorkCommand.LoadSendEditModel).handleWork()
        }
        is EditorEvent.ChangeTime -> with(event.timeRange) {
            editorWorkProcessor.work(EditorWorkCommand.ChangeTimeRange(this)).handleWork()
        }
        is EditorEvent.ChangeIsTemplate -> with(checkNotNull(state().editModel)) {
            editorWorkProcessor.work(EditorWorkCommand.ChangeIsTemplate(this)).handleWork()
        }
        is EditorEvent.ChangeParameters -> updateEditModel {
            copy(parameters = event.parameters)
        }
        is EditorEvent.ChangeCategories -> updateEditModel {
            copy(mainCategory = event.category, subCategory = event.subCategory)
        }
        is EditorEvent.PressDeleteButton -> with(checkNotNull(state().editModel)) {
            timeTaskWorkProcessor.work(TimeTaskWorkCommand.DeleteModel(this)).handleWork()
        }
        is EditorEvent.PressSaveButton -> with(checkNotNull(state().editModel)) {
            val timeValidate = timeRangeValidator.validate(timeRanges)
            val categoryValidate = categoryValidator.validate(mainCategory)
            if (timeValidate.isValid && categoryValidate.isValid) {
                val command = TimeTaskWorkCommand.AddOrSaveModel(this, event.isTemplateUpdate)
                timeTaskWorkProcessor.work(command).handleWork()
            } else {
                val action = EditorAction.SetValidError(timeValidate.validError, categoryValidate.validError)
                sendAction(action)
            }
        }
        is EditorEvent.PressControlTemplateButton -> {
            editorWorkProcessor.work(EditorWorkCommand.GoTemplates).handleWork()
        }
        is EditorEvent.PressBackButton -> {
            editorWorkProcessor.work(EditorWorkCommand.GoBack).handleWork()
        }
        is EditorEvent.PressManageCategoriesButton -> {
            editorWorkProcessor.work(EditorWorkCommand.ManageCategories).handleWork()
        }
        is EditorEvent.LoadTemplates -> {
            timeTaskWorkProcessor.work(TimeTaskWorkCommand.LoadTemplateTimeTasks).handleWork()
        }
        is EditorEvent.ApplyTemplate -> {
            val model = checkNotNull(state().editModel)
            editorWorkProcessor.work(EditorWorkCommand.ApplyTemplate(event.template, model)).handleWork()
        }
    }

    override suspend fun reduce(
        action: EditorAction,
        currentState: EditorViewState,
    ) = when (action) {
        is EditorAction.SetUp -> currentState.copy(
            editModel = action.editModel,
            categories = action.categories,
            timeRangeValid = null,
            categoryValid = null,
        )
        is EditorAction.UpdateEditModel -> currentState.copy(
            editModel = action.editModel,
        )
        is EditorAction.UpdateTimeRange -> currentState.copy(
            editModel = currentState.editModel?.copy(
                timeRanges = action.timeRange,
                duration = action.duration,
            ),
            timeRangeValid = null,
        )
        is EditorAction.UpdateTemplateId -> currentState.copy(
            editModel = currentState.editModel?.copy(templateId = action.templateId),
        )
        is EditorAction.SetValidError -> currentState.copy(
            timeRangeValid = action.timeRange,
            categoryValid = action.category,
        )
        is EditorAction.UpdateTemplates -> currentState.copy(
            templates = action.templates,
        )
        is EditorAction.Navigate -> currentState.copy(
            templates = null,
        )
    }

    private suspend fun WorkScope<EditorViewState, EditorAction, EditorEffect>.updateEditModel(
        onTransform: EditModelUi.() -> EditModelUi,
    ) {
        val editModel = checkNotNull(state().editModel)
        sendAction(EditorAction.UpdateEditModel(onTransform(editModel)))
    }

    override fun onDispose() {
        super.onDispose()
        EditorComponentHolder.clear()
    }
}

@Composable
internal fun Screen.rememberEditorScreenModel(): EditorScreenModel {
    return rememberScreenModel { EditorComponentHolder.fetchComponent().fetchEditorScreenModel() }
}
