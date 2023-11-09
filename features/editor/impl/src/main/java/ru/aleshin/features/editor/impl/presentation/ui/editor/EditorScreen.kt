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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.ui.views.*
import ru.aleshin.core.ui.views.Scaffold
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.platform.screen.ScreenContent
import ru.aleshin.features.editor.impl.presentation.mappers.mapToMessage
import ru.aleshin.features.editor.impl.presentation.theme.EditorTheme
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEvent
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorViewState
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.rememberEditorScreenModel
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.EditorTopAppBar
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.TemplatesBottomSheet
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.UndefinedTasksBottomSheet
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal class EditorScreen @Inject constructor() : Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() = ScreenContent(
        screenModel = rememberEditorScreenModel(),
        initialState = EditorViewState(),
    ) { state ->
        EditorTheme {
            val hostState = remember { SnackbarHostState() }
            var isTemplatesSheetOpen by rememberSaveable { mutableStateOf(false) }
            var isUndefinedTasksSheetOpen by rememberSaveable { mutableStateOf(false) }
            val strings = EditorThemeRes.strings

            Scaffold(
                content = { paddingValues ->
                    EditorContent(
                        state = state,
                        modifier = Modifier.padding(paddingValues),
                        onCategoriesChange = { main, sub -> dispatchEvent(EditorEvent.ChangeCategories(main, sub)) },
                        onNoteChange = { dispatchEvent(EditorEvent.ChangeNote(it)) },
                        onAddSubCategory = { dispatchEvent(EditorEvent.AddSubCategory(it)) },
                        onTimeRangeChange = { dispatchEvent(EditorEvent.ChangeTime(it)) },
                        onChangeParameters = { dispatchEvent(EditorEvent.ChangeParameters(it)) },
                        onControlTemplate = { dispatchEvent(EditorEvent.PressControlTemplateButton) },
                        onCreateTemplate = { dispatchEvent(EditorEvent.CreateTemplate) },
                        onSaveClick = { dispatchEvent(EditorEvent.PressSaveButton) },
                        onCancelClick = { dispatchEvent(EditorEvent.PressBackButton) },
                    )
                },
                topBar = {
                    EditorTopAppBar(
                        actionsEnabled = !(state.editModel?.checkDateIsRepeat() ?: false),
                        countUndefinedTasks = state.undefinedTasks?.size ?: 0,
                        onBackIconClick = { dispatchEvent(EditorEvent.PressBackButton) },
                        onDeleteActionClick = { dispatchEvent(EditorEvent.PressDeleteButton) },
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
                onControlClick = { dispatchEvent(EditorEvent.PressControlTemplateButton) },
                onChooseTemplate = { template ->
                    dispatchEvent(EditorEvent.ApplyTemplate(template))
                    isTemplatesSheetOpen = false
                },
            )

            UndefinedTasksBottomSheet(
                isShow = isUndefinedTasksSheetOpen,
                undefinedTasks = state.undefinedTasks,
                currentUndefinedTaskId = state.editModel?.undefinedTaskId,
                onDismiss = { isUndefinedTasksSheetOpen = false },
                onChooseUndefinedTask = {
                    dispatchEvent(EditorEvent.ApplyUndefinedTask(it))
                    isUndefinedTasksSheetOpen = false
                },
            )

            handleEffect { effect ->
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
                            dispatchEvent(EditorEvent.ChangeTime(TimeRange(start, end)))
                        }
                    }
                }
            }
        }
    }
}
