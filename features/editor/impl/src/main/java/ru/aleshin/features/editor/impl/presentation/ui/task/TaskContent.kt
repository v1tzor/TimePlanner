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
package ru.aleshin.features.editor.impl.presentation.ui.task

import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.presentation.mappers.mapToMessage
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskEffect
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskEvent
import ru.aleshin.features.editor.impl.presentation.ui.task.store.TaskComponent
import ru.aleshin.features.editor.impl.presentation.ui.task.views.EditorTopAppBar
import ru.aleshin.features.editor.impl.presentation.ui.task.views.TemplatesBottomSheet
import ru.aleshin.features.editor.impl.presentation.ui.task.views.UndefinedTasksBottomSheet
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo
import android.view.KeyEvent as AndroidKeyEvent

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TaskContent(
    modifier: Modifier = Modifier,
    component: TaskComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = component.store
    val state by store.stateAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isTemplatesChooserOpen by rememberSaveable { mutableStateOf(false) }
    var isUndefinedTasksChooserOpen by rememberSaveable { mutableStateOf(false) }
    val strings = EditorThemeRes.strings

    Scaffold(
        modifier = modifier.onPreviewKeyEvent { event ->
            if (
                event.type == KeyEventType.KeyUp &&
                (event.isCtrlPressed || event.isMetaPressed) &&
                event.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_S
            ) {
                store.dispatchEvent(TaskEvent.PressSaveButton)
                true
            } else {
                false
            }
        },
        topBar = {
            EditorTopAppBar(
                isCompact = adaptiveLayoutInfo.isCompactWidth,
                actionsEnabled = state.editModel?.linkedTemplateId == null,
                countUndefinedTasks = state.undefinedTasks?.size ?: 0,
                onBackIconClick = { store.dispatchEvent(TaskEvent.PressBackButton) },
                onDeleteActionClick = { store.dispatchEvent(TaskEvent.PressDeleteButton) },
                onOpenUndefinedTasks = {
                    isTemplatesChooserOpen = false
                    isUndefinedTasksChooserOpen = !isUndefinedTasksChooserOpen
                },
                onTemplatesActionClick = {
                    isUndefinedTasksChooserOpen = false
                    isTemplatesChooserOpen = !isTemplatesChooserOpen
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                ErrorSnackbar(snackbarData = snackbarData)
            }
        },
    ) { contentPadding ->
        TaskLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            isTemplatesChooserOpen = isTemplatesChooserOpen,
            isUndefinedTasksChooserOpen = isUndefinedTasksChooserOpen,
            onChooseTemplate = { template ->
                store.dispatchEvent(TaskEvent.ApplyTemplate(template))
                isTemplatesChooserOpen = false
            },
            onChooseUndefinedTask = { task ->
                store.dispatchEvent(TaskEvent.ApplyUndefinedTask(task))
                isUndefinedTasksChooserOpen = false
            },
            onEvent = store::dispatchEvent,
        )
    }

    if (!adaptiveLayoutInfo.useTaskPaneChooser) {
        TemplatesBottomSheet(
            isShow = isTemplatesChooserOpen,
            templates = state.templates,
            currentTemplateId = state.editModel?.linkedTemplateId,
            onDismiss = { isTemplatesChooserOpen = false },
            onControlClick = {
                store.dispatchEvent(TaskEvent.PressControlTemplateButton)
            },
            onChooseTemplate = { template ->
                store.dispatchEvent(TaskEvent.ApplyTemplate(template))
                isTemplatesChooserOpen = false
            },
        )
        UndefinedTasksBottomSheet(
            isShow = isUndefinedTasksChooserOpen,
            undefinedTasks = state.undefinedTasks,
            currentUndefinedTaskId = state.editModel?.undefinedTaskId,
            onDismiss = { isUndefinedTasksChooserOpen = false },
            onChooseUndefinedTask = { task ->
                store.dispatchEvent(TaskEvent.ApplyUndefinedTask(task))
                isUndefinedTasksChooserOpen = false
            },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is TaskEffect.ShowError -> {
                snackbarHostState.showSnackbar(
                    message = effect.failures.mapToMessage(strings),
                )
            }
            is TaskEffect.ShowOverlayError -> {
                val result = snackbarHostState.showSnackbar(
                    message = effect.failures.mapToMessage(strings),
                    withDismissAction = true,
                    actionLabel = strings.correctOverlayTitle,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    val currentTimeRange = effect.currentTimeRange
                    val start = effect.failures.startOverlay ?: currentTimeRange.from
                    val end = effect.failures.endOverlay ?: currentTimeRange.to
                    store.dispatchEvent(TaskEvent.ChangeTime(TimeRange(start, end)))
                }
            }
        }
    }
}
