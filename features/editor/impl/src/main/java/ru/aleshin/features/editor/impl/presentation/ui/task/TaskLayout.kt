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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.features.editor.impl.presentation.theme.tokens.EditorLayoutDefaults
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskEvent
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskState
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.CategoryValidateError
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.TimeRangeError
import ru.aleshin.features.editor.impl.presentation.ui.task.views.sections.TaskActionButtonsSection
import ru.aleshin.features.editor.impl.presentation.ui.task.views.sections.TaskCategoriesSection
import ru.aleshin.features.editor.impl.presentation.ui.task.views.sections.TaskDateTimeSection
import ru.aleshin.features.editor.impl.presentation.ui.task.views.sections.TaskParametersSection
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
internal fun TaskLayout(
    modifier: Modifier = Modifier,
    state: TaskState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    isTemplatesChooserOpen: Boolean,
    isUndefinedTasksChooserOpen: Boolean,
    onChooseTemplate: (TemplateUi) -> Unit,
    onChooseUndefinedTask: (UndefinedTaskUi) -> Unit,
    onEvent: (TaskEvent) -> Unit,
) {
    val mainScrollState = rememberScrollState()
    val supportingScrollState = rememberScrollState()

    when {
        adaptiveLayoutInfo.isBookPosture || adaptiveLayoutInfo.isTabletopPosture -> TaskSupportingPaneLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            mainScrollState = mainScrollState,
            supportingScrollState = supportingScrollState,
            isTemplatesChooserOpen = isTemplatesChooserOpen,
            isUndefinedTasksChooserOpen = isUndefinedTasksChooserOpen,
            onChooseTemplate = onChooseTemplate,
            onChooseUndefinedTask = onChooseUndefinedTask,
            onEvent = onEvent,
        )
        adaptiveLayoutInfo.useExpandedLayout -> TaskExpandedLayout(
            modifier = modifier,
            state = state,
            mainScrollState = mainScrollState,
            supportingScrollState = supportingScrollState,
            isTemplatesChooserOpen = isTemplatesChooserOpen,
            isUndefinedTasksChooserOpen = isUndefinedTasksChooserOpen,
            onChooseTemplate = onChooseTemplate,
            onChooseUndefinedTask = onChooseUndefinedTask,
            onEvent = onEvent,
        )
        else -> TaskSinglePaneLayout(
            modifier = modifier,
            state = state,
            scrollState = mainScrollState,
            maxContentWidth = if (adaptiveLayoutInfo.isMediumWidth) {
                EditorLayoutDefaults.MediumContentMaxWidth
            } else {
                null
            },
            onEvent = onEvent,
        )
    }
}

@Composable
private fun TaskSinglePaneLayout(
    modifier: Modifier = Modifier,
    state: TaskState,
    scrollState: ScrollState,
    maxContentWidth: Dp?,
    onEvent: (TaskEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val editModel = state.editModel
        if (editModel != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (maxContentWidth != null) Modifier.widthIn(max = maxContentWidth) else Modifier)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    TaskCategoriesSection(
                        mainCategory = editModel.mainCategory,
                        subCategory = editModel.subCategory,
                        allCategories = state.categories,
                        note = editModel.note,
                        isEnabled = editModel.linkedTemplateId == null,
                        isError = state.categoryValid is CategoryValidateError.EmptyCategoryError,
                        showNote = true,
                        onEditCategory = { category ->
                            onEvent(TaskEvent.NavigateToCategoryEditor(category))
                        },
                        onEditSubCategory = { category ->
                            onEvent(TaskEvent.NavigateToSubCategoryEditor(category))
                        },
                        onCategoriesChange = { mainCategory, subCategory ->
                            onEvent(TaskEvent.ChangeCategories(mainCategory, subCategory))
                        },
                        onAddCategory = { onEvent(TaskEvent.PressAddCategory) },
                        onAddSubCategory = { name ->
                            onEvent(TaskEvent.AddSubCategory(name))
                        },
                        onNoteChange = { note -> onEvent(TaskEvent.ChangeNote(note)) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )
                    TaskDateTimeSection(
                        scheduleDate = editModel.date,
                        timeRange = editModel.timeRange,
                        duration = editModel.duration,
                        durationPresets = state.durationPresets,
                        isEnabled = editModel.linkedTemplateId == null,
                        isError = state.timeRangeValid is TimeRangeError.DurationError,
                        onTimeRangeChange = { timeRange ->
                            onEvent(TaskEvent.ChangeTime(timeRange))
                        },
                        onDurationPresetsChange = { presets ->
                            onEvent(TaskEvent.UpdateDurationPresets(presets))
                        },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )
                    TaskParametersSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        parameters = editModel.parameters,
                        isEnabled = editModel.linkedTemplateId == null,
                        onParametersChange = { parameters ->
                            onEvent(TaskEvent.ChangeParameters(parameters))
                        },
                    )
                }
                TaskActionButtonsSection(
                    modifier = Modifier.padding(16.dp),
                    isCreateMode = editModel.key == 0L,
                    isLinkedToTemplate = editModel.linkedTemplateId != null,
                    onUnlinkTemplate = {
                        onEvent(TaskEvent.PressUnlinkTemplateButton)
                    },
                    onCreateTemplate = { onEvent(TaskEvent.CreateTemplate) },
                    onCancelClick = { onEvent(TaskEvent.PressBackButton) },
                    onSaveClick = { onEvent(TaskEvent.PressSaveButton) },
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

internal val AdaptiveLayoutInfo.useTaskPaneChooser: Boolean
    get() = useExpandedLayout || isBookPosture || isTabletopPosture

@Composable
private fun TaskExpandedLayout(
    modifier: Modifier = Modifier,
    state: TaskState,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    isTemplatesChooserOpen: Boolean,
    isUndefinedTasksChooserOpen: Boolean,
    onChooseTemplate: (TemplateUi) -> Unit,
    onChooseUndefinedTask: (UndefinedTaskUi) -> Unit,
    onEvent: (TaskEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = EditorLayoutDefaults.ExpandedContentMaxWidth)
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(EditorLayoutDefaults.PaneSpacing),
        ) {
            TaskFormPane(
                modifier = Modifier.weight(1.4f),
                state = state,
                scrollState = mainScrollState,
                onEvent = onEvent,
            )
            TaskParametersPane(
                modifier = Modifier.weight(1f),
                state = state,
                scrollState = supportingScrollState,
                isTemplatesChooserOpen = isTemplatesChooserOpen,
                isUndefinedTasksChooserOpen = isUndefinedTasksChooserOpen,
                onChooseTemplate = onChooseTemplate,
                onChooseUndefinedTask = onChooseUndefinedTask,
                onEvent = onEvent,
            )
        }
    }
}
