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
package ru.aleshin.features.editor.impl.presentation.ui.editor

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.aleshin.core.ui.views.ErrorSnackbar
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.features.editor.impl.presentation.mappers.mapToMessage
import ru.aleshin.features.editor.impl.presentation.theme.EditorTheme
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEvent
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.InternalEditorFeatureComponent
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.EditorTopAppBar
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.TemplatesBottomSheet
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.UndefinedTasksBottomSheet

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal class EditorContentProvider(
    private val editorComponent: InternalEditorFeatureComponent
) : FeatureContentProvider {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun invoke(modifier: Modifier) {
        EditorTheme {
            val store = editorComponent.store
            val state by store.stateAsState()
            val hostState = remember { SnackbarHostState() }
            var isTemplatesSheetOpen by rememberSaveable { mutableStateOf(false) }
            var isUndefinedTasksSheetOpen by rememberSaveable { mutableStateOf(false) }
            val strings = EditorThemeRes.strings

            Scaffold(
                content = { paddingValues ->
                    EditorContent(
                        state = state,
                        modifier = modifier.padding(paddingValues),
                        onCategoriesChange = { main, sub -> store.dispatchEvent(EditorEvent.ChangeCategories(main, sub)) },
                        onNoteChange = { store.dispatchEvent(EditorEvent.ChangeNote(it)) },
                        onAddSubCategory = { store.dispatchEvent(EditorEvent.AddSubCategory(it)) },
                        onTimeRangeChange = { store.dispatchEvent(EditorEvent.ChangeTime(it)) },
                        onChangeParameters = { store.dispatchEvent(EditorEvent.ChangeParameters(it)) },
                        onEditCategory = { store.dispatchEvent(EditorEvent.NavigateToCategoryEditor(it)) },
                        onEditSubCategory = { store.dispatchEvent(EditorEvent.NavigateToSubCategoryEditor(it)) },
                        onControlTemplate = { store.dispatchEvent(EditorEvent.PressControlTemplateButton) },
                        onCreateTemplate = { store.dispatchEvent(EditorEvent.CreateTemplate) },
                        onSaveClick = { store.dispatchEvent(EditorEvent.PressSaveButton) },
                        onCancelClick = { store.dispatchEvent(EditorEvent.PressBackButton) },
                    )
                },
                topBar = {
                    EditorTopAppBar(
                        actionsEnabled = !(state.editModel?.checkDateIsRepeat() ?: false),
                        countUndefinedTasks = state.undefinedTasks?.size ?: 0,
                        onBackIconClick = { store.dispatchEvent(EditorEvent.PressBackButton) },
                        onDeleteActionClick = { store.dispatchEvent(EditorEvent.PressDeleteButton) },
                        onOpenUndefinedTasks = {
                            isUndefinedTasksSheetOpen = true
                        },
                        onTemplatesActionClick = {
                            isTemplatesSheetOpen = true
                        },
                    )
                },
                snackbarHost = {
                    SnackbarHost(hostState = hostState) {
                        ErrorSnackbar(snackbarData = it)
                    }
                },
            )

            TemplatesBottomSheet(
                isShow = isTemplatesSheetOpen,
                templates = state.templates,
                currentTemplateId = state.editModel?.templateId,
                onDismiss = { isTemplatesSheetOpen = false },
                onControlClick = { store.dispatchEvent(EditorEvent.PressControlTemplateButton) },
                onChooseTemplate = { template ->
                    store.dispatchEvent(EditorEvent.ApplyTemplate(template))
                    isTemplatesSheetOpen = false
                },
            )

            UndefinedTasksBottomSheet(
                isShow = isUndefinedTasksSheetOpen,
                undefinedTasks = state.undefinedTasks,
                currentUndefinedTaskId = state.editModel?.undefinedTaskId,
                onDismiss = { isUndefinedTasksSheetOpen = false },
                onChooseUndefinedTask = {
                    store.dispatchEvent(EditorEvent.ApplyUndefinedTask(it))
                    isUndefinedTasksSheetOpen = false
                },
            )

            store.handleEffects { effect ->
                when (effect) {
                    is EditorEffect.ShowError -> {
                        hostState.showSnackbar(message = effect.failures.mapToMessage(strings))
                    }
                    is EditorEffect.ShowOverlayError -> {
                        val result = hostState.showSnackbar(
                            message = effect.failures.mapToMessage(strings),
                            withDismissAction = true,
                            actionLabel = strings.correctOverlayTitle,
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            val currentTimeRange = effect.currentTimeRange
                            val start = effect.failures.startOverlay ?: currentTimeRange.from
                            val end = effect.failures.endOverlay ?: currentTimeRange.to
                            store.dispatchEvent(EditorEvent.ChangeTime(TimeRange(start, end)))
                        }
                    }
                }
            }
        }
    }
}