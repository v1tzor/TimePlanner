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

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskEvent
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskState
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.CategoryValidateError
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.TimeRangeError
import ru.aleshin.features.editor.impl.presentation.ui.task.views.TemplatesChooserContent
import ru.aleshin.features.editor.impl.presentation.ui.task.views.UndefinedTasksChooserContent
import ru.aleshin.features.editor.impl.presentation.ui.task.views.sections.TaskActionButtonsSection
import ru.aleshin.features.editor.impl.presentation.ui.task.views.sections.TaskCategoriesSection
import ru.aleshin.features.editor.impl.presentation.ui.task.views.sections.TaskDateTimeSection
import ru.aleshin.features.editor.impl.presentation.ui.task.views.sections.TaskNoteField
import ru.aleshin.features.editor.impl.presentation.ui.task.views.sections.TaskParametersSection
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun TaskSupportingPaneLayout(
    modifier: Modifier = Modifier,
    state: TaskState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    isTemplatesChooserOpen: Boolean,
    isUndefinedTasksChooserOpen: Boolean,
    onChooseTemplate: (TemplateUi) -> Unit,
    onChooseUndefinedTask: (UndefinedTaskUi) -> Unit,
    onEvent: (TaskEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier.fillMaxSize(),
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPane = {
            TaskFormPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                scrollState = mainScrollState,
                onEvent = onEvent,
            )
        },
        supportingPane = {
            TaskParametersPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                scrollState = supportingScrollState,
                isTemplatesChooserOpen = isTemplatesChooserOpen,
                isUndefinedTasksChooserOpen = isUndefinedTasksChooserOpen,
                onChooseTemplate = onChooseTemplate,
                onChooseUndefinedTask = onChooseUndefinedTask,
                onEvent = onEvent,
            )
        },
    )
}

@Composable
internal fun TaskFormPane(
    modifier: Modifier = Modifier,
    state: TaskState,
    scrollState: ScrollState,
    onEvent: (TaskEvent) -> Unit,
) {
    val editModel = state.editModel
    if (editModel != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
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
                showNote = false,
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
        }
    } else {
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TaskParametersPane(
    modifier: Modifier = Modifier,
    state: TaskState,
    scrollState: ScrollState,
    isTemplatesChooserOpen: Boolean,
    isUndefinedTasksChooserOpen: Boolean,
    onChooseTemplate: (TemplateUi) -> Unit,
    onChooseUndefinedTask: (UndefinedTaskUi) -> Unit,
    onEvent: (TaskEvent) -> Unit,
) {
    val contentState = when {
        state.editModel == null -> TaskSupportingPaneContentState.LOADING
        isTemplatesChooserOpen -> TaskSupportingPaneContentState.TEMPLATES
        isUndefinedTasksChooserOpen -> TaskSupportingPaneContentState.UNDEFINED_TASKS
        else -> TaskSupportingPaneContentState.PARAMETERS
    }

    AnimatedContent(
        modifier = modifier,
        targetState = contentState,
        contentKey = { targetState -> targetState },
        label = "TaskParametersPane",
    ) { currentContentState ->
        when (currentContentState) {
            TaskSupportingPaneContentState.LOADING -> {
                Box(modifier = Modifier.fillMaxSize())
            }
            TaskSupportingPaneContentState.TEMPLATES -> {
                TemplatesChooserContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    templates = state.templates,
                    currentTemplateId = state.editModel?.linkedTemplateId,
                    onControlClick = {
                        onEvent(TaskEvent.PressControlTemplateButton)
                    },
                    onChooseTemplate = onChooseTemplate,
                )
            }
            TaskSupportingPaneContentState.UNDEFINED_TASKS -> {
                UndefinedTasksChooserContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    undefinedTasks = state.undefinedTasks,
                    currentUndefinedTaskId = state.editModel?.undefinedTaskId,
                    onChooseUndefinedTask = onChooseUndefinedTask,
                )
            }
            TaskSupportingPaneContentState.PARAMETERS -> {
                val editModel = state.editModel
                if (editModel != null) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(scrollState)
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            TaskNoteField(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                note = editModel.note,
                                onNoteChange = { note ->
                                    onEvent(TaskEvent.ChangeNote(note))
                                },
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
                }
            }
        }
    }
}

private enum class TaskSupportingPaneContentState {
    LOADING,
    TEMPLATES,
    UNDEFINED_TASKS,
    PARAMETERS,
}
