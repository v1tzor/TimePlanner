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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.core.utils.extensions.changeDay
import ru.aleshin.core.utils.extensions.fetchHourOfDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.shiftMillis
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.presentation.mappers.convertToItem
import ru.aleshin.features.editor.impl.presentation.mappers.convertToModel
import ru.aleshin.features.editor.impl.presentation.models.tasks.TaskPriorityItemUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditParametersUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskEvent
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskState
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.CategoryValidateError
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.TimeRangeError
import ru.aleshin.features.editor.impl.presentation.ui.task.views.DurationTitle
import ru.aleshin.features.editor.impl.presentation.ui.task.views.EndTimeField
import ru.aleshin.features.editor.impl.presentation.ui.task.views.MainCategoryChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.ParameterChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.SegmentedParametersChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.StartTimeField
import ru.aleshin.features.editor.impl.presentation.ui.task.views.SubCategoryChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.TaskNotificationsMenu
import ru.aleshin.features.editor.impl.presentation.ui.task.views.TemplatesChooserContent
import ru.aleshin.features.editor.impl.presentation.ui.task.views.TimeRangeSlider
import ru.aleshin.features.editor.impl.presentation.ui.task.views.UndefinedTasksChooserContent
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold
import ru.aleshin.timeplanner.core.ui.views.CustomLargeTextField
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo
import java.util.Date

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TaskLayout(
    modifier: Modifier = Modifier,
    state: TaskState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
    isTemplatesChooserOpen: Boolean = false,
    isUndefinedTasksChooserOpen: Boolean = false,
    onChooseTemplate: (TemplateUi) -> Unit = {},
    onChooseUndefinedTask: (UndefinedTaskUi) -> Unit = {},
    onEvent: (TaskEvent) -> Unit,
) {
    val mainScrollState = rememberScrollState()
    val supportingScrollState = rememberScrollState()
    val useFoldSupportingPane = adaptiveLayoutInfo.isBookPosture || adaptiveLayoutInfo.isTabletopPosture

    AnimatedVisibility(
        visible = state.editModel != null,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        val editModel = state.editModel
        if (editModel != null) {
            if (useFoldSupportingPane) {
                AdaptiveSupportingPaneScaffold(
                    adaptiveLayoutInfo = adaptiveLayoutInfo,
                    modifier = modifier.fillMaxSize().animateContentSize(),
                    mainPane = {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(mainScrollState)
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            TaskMainPaneSections(
                                state = state,
                                showNote = false,
                                onCategoriesChange = { main, sub ->
                                    onEvent(TaskEvent.ChangeCategories(main, sub))
                                },
                                onNoteChange = { note -> onEvent(TaskEvent.ChangeNote(note)) },
                                onAddSubCategory = { name ->
                                    onEvent(TaskEvent.AddSubCategory(name))
                                },
                                onAddCategory = {
                                    onEvent(TaskEvent.PressAddCategory)
                                },
                                onTimeRangeChange = { range ->
                                    onEvent(TaskEvent.ChangeTime(range))
                                },
                                onDurationPresetsChange = { presets ->
                                    onEvent(TaskEvent.UpdateDurationPresets(presets))
                                },
                                onEditCategory = { category ->
                                    onEvent(TaskEvent.NavigateToCategoryEditor(category))
                                },
                                onEditSubCategory = { category ->
                                    onEvent(TaskEvent.NavigateToSubCategoryEditor(category))
                                },
                            )
                        }
                    },
                    supportingPane = {
                        TaskSupportingPaneContent(
                            modifier = Modifier.fillMaxSize(),
                            state = state,
                            scrollState = supportingScrollState,
                            isTemplatesChooserOpen = isTemplatesChooserOpen,
                            isUndefinedTasksChooserOpen = isUndefinedTasksChooserOpen,
                            onChooseTemplate = onChooseTemplate,
                            onChooseUndefinedTask = onChooseUndefinedTask,
                            onNoteChange = { note -> onEvent(TaskEvent.ChangeNote(note)) },
                            onChangeParameters = { parameters ->
                                onEvent(TaskEvent.ChangeParameters(parameters))
                            },
                            onUnlinkTemplate = {
                                onEvent(TaskEvent.PressUnlinkTemplateButton)
                            },
                            onControlTemplate = {
                                onEvent(TaskEvent.PressControlTemplateButton)
                            },
                            onCreateTemplate = { onEvent(TaskEvent.CreateTemplate) },
                            onCancelClick = { onEvent(TaskEvent.PressBackButton) },
                            onSaveClick = { onEvent(TaskEvent.PressSaveButton) },
                        )
                    },
                )
            } else if (adaptiveLayoutInfo.useExpandedLayout) {
                TaskExpandedContent(
                    modifier = modifier.fillMaxSize(),
                    state = state,
                    mainScrollState = mainScrollState,
                    supportingScrollState = supportingScrollState,
                    isTemplatesChooserOpen = isTemplatesChooserOpen,
                    isUndefinedTasksChooserOpen = isUndefinedTasksChooserOpen,
                    onChooseTemplate = onChooseTemplate,
                    onChooseUndefinedTask = onChooseUndefinedTask,
                    onCategoriesChange = { main, sub ->
                        onEvent(TaskEvent.ChangeCategories(main, sub))
                    },
                    onNoteChange = { note -> onEvent(TaskEvent.ChangeNote(note)) },
                    onAddSubCategory = { name -> onEvent(TaskEvent.AddSubCategory(name)) },
                    onAddCategory = { onEvent(TaskEvent.PressAddCategory) },
                    onTimeRangeChange = { range -> onEvent(TaskEvent.ChangeTime(range)) },
                    onChangeParameters = { parameters ->
                        onEvent(TaskEvent.ChangeParameters(parameters))
                    },
                    onDurationPresetsChange = { presets ->
                        onEvent(TaskEvent.UpdateDurationPresets(presets))
                    },
                    onEditCategory = { category ->
                        onEvent(TaskEvent.NavigateToCategoryEditor(category))
                    },
                    onEditSubCategory = { category ->
                        onEvent(TaskEvent.NavigateToSubCategoryEditor(category))
                    },
                    onUnlinkTemplate = { onEvent(TaskEvent.PressUnlinkTemplateButton) },
                    onControlTemplate = { onEvent(TaskEvent.PressControlTemplateButton) },
                    onCreateTemplate = { onEvent(TaskEvent.CreateTemplate) },
                    onSaveClick = { onEvent(TaskEvent.PressSaveButton) },
                    onCancelClick = { onEvent(TaskEvent.PressBackButton) },
                )
            } else {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    TaskSinglePaneContent(
                        modifier = if (adaptiveLayoutInfo.isMediumWidth) {
                            Modifier
                                .widthIn(max = AdaptiveLayoutDefaults.MediumContentMaxWidth)
                                .fillMaxSize()
                        } else {
                            Modifier.fillMaxSize()
                        },
                        state = state,
                        scrollState = mainScrollState,
                        onCategoriesChange = { main, sub ->
                            onEvent(TaskEvent.ChangeCategories(main, sub))
                        },
                        onNoteChange = { note -> onEvent(TaskEvent.ChangeNote(note)) },
                        onAddSubCategory = { name ->
                            onEvent(TaskEvent.AddSubCategory(name))
                        },
                        onAddCategory = {
                            onEvent(TaskEvent.PressAddCategory)
                        },
                        onTimeRangeChange = { range ->
                            onEvent(TaskEvent.ChangeTime(range))
                        },
                        onChangeParameters = { parameters ->
                            onEvent(TaskEvent.ChangeParameters(parameters))
                        },
                        onDurationPresetsChange = { presets ->
                            onEvent(TaskEvent.UpdateDurationPresets(presets))
                        },
                        onEditCategory = { category ->
                            onEvent(TaskEvent.NavigateToCategoryEditor(category))
                        },
                        onEditSubCategory = { category ->
                            onEvent(TaskEvent.NavigateToSubCategoryEditor(category))
                        },
                        onUnlinkTemplate = {
                            onEvent(TaskEvent.PressUnlinkTemplateButton)
                        },
                        onControlTemplate = {
                            onEvent(TaskEvent.PressControlTemplateButton)
                        },
                        onCreateTemplate = { onEvent(TaskEvent.CreateTemplate) },
                        onSaveClick = { onEvent(TaskEvent.PressSaveButton) },
                        onCancelClick = { onEvent(TaskEvent.PressBackButton) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskExpandedContent(
    modifier: Modifier = Modifier,
    state: TaskState,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    isTemplatesChooserOpen: Boolean,
    isUndefinedTasksChooserOpen: Boolean,
    onChooseTemplate: (TemplateUi) -> Unit,
    onChooseUndefinedTask: (UndefinedTaskUi) -> Unit,
    onCategoriesChange: (MainCategoryUi, SubCategoryUi?) -> Unit,
    onNoteChange: (String?) -> Unit,
    onAddCategory: () -> Unit,
    onAddSubCategory: (String) -> Unit,
    onTimeRangeChange: (TimeRange) -> Unit,
    onChangeParameters: (TimeTaskEditParametersUi) -> Unit,
    onDurationPresetsChange: (List<Long>) -> Unit,
    onEditCategory: (MainCategoryUi) -> Unit,
    onEditSubCategory: (SubCategoryUi) -> Unit,
    onUnlinkTemplate: () -> Unit,
    onControlTemplate: () -> Unit,
    onCreateTemplate: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = AdaptiveLayoutDefaults.EditorContentMaxWidth)
                .padding(horizontal = AdaptiveLayoutDefaults.ExpandedHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.PaneSpacing),
        ) {
            Column(
                modifier = Modifier
                    .weight(1.4f)
                    .fillMaxSize()
                    .verticalScroll(mainScrollState)
                    .padding(top = AdaptiveLayoutDefaults.SpaceLarge),
                verticalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.SpaceLarge),
            ) {
                TaskMainPaneSections(
                    state = state,
                    showNote = false,
                    onCategoriesChange = onCategoriesChange,
                    onNoteChange = onNoteChange,
                    onAddCategory = onAddCategory,
                    onAddSubCategory = onAddSubCategory,
                    onTimeRangeChange = onTimeRangeChange,
                    onDurationPresetsChange = onDurationPresetsChange,
                    onEditCategory = onEditCategory,
                    onEditSubCategory = onEditSubCategory,
                )
            }
            TaskSupportingPaneContent(
                modifier = Modifier.weight(1f).fillMaxSize(),
                state = state,
                scrollState = supportingScrollState,
                isTemplatesChooserOpen = isTemplatesChooserOpen,
                isUndefinedTasksChooserOpen = isUndefinedTasksChooserOpen,
                onChooseTemplate = onChooseTemplate,
                onChooseUndefinedTask = onChooseUndefinedTask,
                onNoteChange = onNoteChange,
                onChangeParameters = onChangeParameters,
                onUnlinkTemplate = onUnlinkTemplate,
                onControlTemplate = onControlTemplate,
                onCreateTemplate = onCreateTemplate,
                onCancelClick = onCancelClick,
                onSaveClick = onSaveClick,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TaskSupportingPaneContent(
    modifier: Modifier = Modifier,
    state: TaskState,
    scrollState: ScrollState,
    isTemplatesChooserOpen: Boolean,
    isUndefinedTasksChooserOpen: Boolean,
    onChooseTemplate: (TemplateUi) -> Unit,
    onChooseUndefinedTask: (UndefinedTaskUi) -> Unit,
    onNoteChange: (String?) -> Unit,
    onChangeParameters: (TimeTaskEditParametersUi) -> Unit,
    onUnlinkTemplate: () -> Unit,
    onControlTemplate: () -> Unit,
    onCreateTemplate: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val editModel = state.editModel ?: return
    when {
        isTemplatesChooserOpen -> {
            TemplatesChooserContent(
                modifier = modifier.padding(top = AdaptiveLayoutDefaults.SpaceLarge),
                templates = state.templates,
                currentTemplateId = editModel.linkedTemplateId,
                onControlClick = onControlTemplate,
                onChooseTemplate = onChooseTemplate,
            )
        }
        isUndefinedTasksChooserOpen -> {
            UndefinedTasksChooserContent(
                modifier = modifier.padding(top = AdaptiveLayoutDefaults.SpaceLarge),
                undefinedTasks = state.undefinedTasks,
                currentUndefinedTaskId = editModel.undefinedTaskId,
                onChooseUndefinedTask = onChooseUndefinedTask,
            )
        }
        else -> {
            Column(modifier = modifier) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(top = AdaptiveLayoutDefaults.SpaceLarge),
                    verticalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.SpaceLarge),
                ) {
                    TaskNoteSection(
                        note = editModel.note,
                        onNoteChange = onNoteChange,
                    )
                    ParametersSection(
                        enabled = editModel.linkedTemplateId == null,
                        parameters = editModel.parameters,
                        onChangeParameters = onChangeParameters,
                    )
                }
                TaskActionButtons(
                    state = state,
                    onUnlinkTemplate = onUnlinkTemplate,
                    onControlTemplate = onControlTemplate,
                    onCreateTemplate = onCreateTemplate,
                    onCancelClick = onCancelClick,
                    onSaveClick = onSaveClick,
                )
            }
        }
    }
}

@Composable
private fun TaskSinglePaneContent(
    modifier: Modifier = Modifier,
    state: TaskState,
    scrollState: ScrollState,
    onCategoriesChange: (MainCategoryUi, SubCategoryUi?) -> Unit,
    onNoteChange: (String?) -> Unit,
    onAddCategory: () -> Unit,
    onAddSubCategory: (String) -> Unit,
    onTimeRangeChange: (TimeRange) -> Unit,
    onChangeParameters: (TimeTaskEditParametersUi) -> Unit,
    onDurationPresetsChange: (List<Long>) -> Unit,
    onEditCategory: (MainCategoryUi) -> Unit,
    onEditSubCategory: (SubCategoryUi) -> Unit,
    onUnlinkTemplate: () -> Unit,
    onControlTemplate: () -> Unit,
    onCreateTemplate: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    val editModel = state.editModel ?: return
    Column(modifier = modifier.animateContentSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TaskMainPaneSections(
                state = state,
                showNote = true,
                onCategoriesChange = onCategoriesChange,
                onNoteChange = onNoteChange,
                onAddCategory = onAddCategory,
                onAddSubCategory = onAddSubCategory,
                onTimeRangeChange = onTimeRangeChange,
                onDurationPresetsChange = onDurationPresetsChange,
                onEditCategory = onEditCategory,
                onEditSubCategory = onEditSubCategory,
            )
            HorizontalDivider(Modifier.padding(horizontal = 32.dp))
            ParametersSection(
                enabled = editModel.linkedTemplateId == null,
                parameters = editModel.parameters,
                onChangeParameters = onChangeParameters,
            )
        }
        TaskActionButtons(
            state = state,
            onUnlinkTemplate = onUnlinkTemplate,
            onControlTemplate = onControlTemplate,
            onCreateTemplate = onCreateTemplate,
            onCancelClick = onCancelClick,
            onSaveClick = onSaveClick,
        )
    }
}

@Composable
private fun TaskMainPaneSections(
    state: TaskState,
    showNote: Boolean,
    onCategoriesChange: (MainCategoryUi, SubCategoryUi?) -> Unit,
    onNoteChange: (String?) -> Unit,
    onAddCategory: () -> Unit,
    onAddSubCategory: (String) -> Unit,
    onTimeRangeChange: (TimeRange) -> Unit,
    onDurationPresetsChange: (List<Long>) -> Unit,
    onEditCategory: (MainCategoryUi) -> Unit,
    onEditSubCategory: (SubCategoryUi) -> Unit,
) {
    val editModel = state.editModel ?: return
    CategoriesSection(
        enabledCategories = editModel.linkedTemplateId == null,
        isMainCategoryValidError = state.categoryValid is CategoryValidateError.EmptyCategoryError,
        mainCategory = editModel.mainCategory,
        subCategory = editModel.subCategory,
        allCategories = state.categories,
        note = editModel.note,
        showNote = showNote,
        onEditCategory = onEditCategory,
        onEditSubCategory = onEditSubCategory,
        onCategoriesChange = onCategoriesChange,
        onAddCategory = onAddCategory,
        onAddSubCategory = onAddSubCategory,
        onNoteChange = onNoteChange,
    )
    HorizontalDivider(Modifier.padding(horizontal = 32.dp))
    DateTimeSection(
        enabled = editModel.linkedTemplateId == null,
        isTimeValidError = state.timeRangeValid is TimeRangeError.DurationError,
        scheduleDate = editModel.date,
        timeRanges = editModel.timeRange,
        duration = editModel.duration,
        durationPresets = state.durationPresets,
        onTimeRangeChange = onTimeRangeChange,
        onDurationPresetsChange = onDurationPresetsChange,
    )
}

@Composable
private fun TaskActionButtons(
    state: TaskState,
    onUnlinkTemplate: () -> Unit,
    onControlTemplate: () -> Unit,
    onCreateTemplate: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val editModel = state.editModel ?: return
    ActionButtonsSection(
        isCreateMode = editModel.key == 0L,
        isTemplate = editModel.linkedTemplateId != null,
        onUnlinkTemplate = onUnlinkTemplate,
        onControlTemplate = onControlTemplate,
        onCreateTemplate = onCreateTemplate,
        onCancelClick = onCancelClick,
        onSaveClick = onSaveClick,
    )
}

@Composable
internal fun CategoriesSection(
    modifier: Modifier = Modifier,
    enabledCategories: Boolean = true,
    enabledNote: Boolean = true,
    showNote: Boolean = true,
    isMainCategoryValidError: Boolean,
    mainCategory: MainCategoryUi?,
    subCategory: SubCategoryUi?,
    allCategories: List<MainCategoryDetailsUi>,
    note: String?,
    onEditCategory: (MainCategoryUi) -> Unit,
    onEditSubCategory: (SubCategoryUi) -> Unit,
    onCategoriesChange: (MainCategoryUi, SubCategoryUi?) -> Unit,
    onAddCategory: () -> Unit,
    onAddSubCategory: (String) -> Unit,
    onNoteChange: (String?) -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            MainCategoryChooser(
                enabled = enabledCategories,
                modifier = Modifier.fillMaxWidth(),
                isError = isMainCategoryValidError,
                currentCategory = mainCategory,
                allCategories = remember(allCategories) {
                    allCategories.map { it.mainCategory }
                },
                onEditCategory = onEditCategory,
                onAddCategory = onAddCategory,
                onChangeCategory = { newMainCategory ->
                    onCategoriesChange(newMainCategory, null)
                },
            )
            if (isMainCategoryValidError) {
                Text(
                    text = EditorThemeRes.strings.categoryValidateError,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        val findCategories = remember(allCategories, mainCategory) {
            allCategories.find { it.mainCategory == mainCategory }
        }
        SubCategoryChooser(
            enabled = enabledCategories,
            modifier = Modifier.fillMaxWidth(),
            mainCategory = mainCategory,
            allSubCategories = findCategories?.subCategories ?: emptyList(),
            currentSubCategory = subCategory,
            onAddSubCategory = onAddSubCategory,
            onEditSubCategory = onEditSubCategory,
            onChangeSubCategory = { newSubCategory ->
                if (mainCategory != null) onCategoriesChange(mainCategory, newSubCategory)
            },
        )
        if (showNote) {
            TaskNoteField(
                enabled = enabledNote,
                note = note,
                onNoteChange = onNoteChange,
            )
        }
    }
}

@Composable
private fun TaskNoteSection(
    modifier: Modifier = Modifier,
    note: String?,
    onNoteChange: (String?) -> Unit,
) {
    TaskNoteField(
        modifier = modifier.padding(horizontal = 16.dp),
        note = note,
        onNoteChange = onNoteChange,
    )
}

@Composable
private fun TaskNoteField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    note: String?,
    onNoteChange: (String?) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val noteInteractionSource = remember { MutableInteractionSource() }
    var editableNote by remember(note) {
        mutableStateOf(TextFieldValue(text = note ?: ""))
    }

    CustomLargeTextField(
        modifier = modifier,
        enabled = enabled,
        text = editableNote,
        onTextChange = {
            if (it.text.length <= Constants.Text.MAX_NOTE_LENGTH) {
                editableNote = it
                onNoteChange(editableNote.text.ifEmpty { null })
            }
        },
        label = { Text(text = EditorThemeRes.strings.noteLabel) },
        placeholder = { Text(text = EditorThemeRes.strings.notePlaceholder) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = EditorThemeRes.icons.notesField),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        maxLines = 4,
        trailingIcon = if (noteInteractionSource.collectIsFocusedAsState().value) {
            {
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = focusManager::clearFocus,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        } else {
            null
        },
        interactionSource = noteInteractionSource,
    )
}

@Composable
internal fun DateTimeSection(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isTimeValidError: Boolean,
    scheduleDate: Date,
    timeRanges: TimeRange,
    duration: Long,
    durationPresets: List<Long>?,
    onTimeRangeChange: (TimeRange) -> Unit,
    onDurationPresetsChange: (List<Long>) -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StartTimeField(
                enabled = enabled,
                modifier = Modifier.weight(1f),
                currentTime = timeRanges.from,
                isError = isTimeValidError,
                onChangeTime = { newStartTime ->
                    val timeRange = if (newStartTime.fetchHourOfDay() <= timeRanges.to.fetchHourOfDay()) {
                        timeRanges.copy(from = newStartTime, to = timeRanges.to.changeDay(scheduleDate))
                    } else {
                        timeRanges.copy(from = newStartTime, to = timeRanges.to.changeDay(scheduleDate.shiftDay(1)))
                    }
                    onTimeRangeChange(timeRange)
                },
            )
            EndTimeField(
                enabled = enabled,
                modifier = Modifier.weight(1f),
                currentTime = timeRanges.to,
                isError = isTimeValidError,
                onChangeTime = { newEndTime ->
                    val newTime = if (timeRanges.from.fetchHourOfDay() <= newEndTime.fetchHourOfDay()) {
                        newEndTime.changeDay(scheduleDate)
                    } else {
                        newEndTime.changeDay(scheduleDate.shiftDay(1))
                    }
                    onTimeRangeChange(timeRanges.copy(to = newTime))
                },
            )
            DurationTitle(
                enabled = enabled,
                duration = duration,
                startTime = timeRanges.from,
                durationPresets = durationPresets,
                isError = isTimeValidError,
                onChangeDuration = { duration ->
                    onTimeRangeChange(timeRanges.copy(to = timeRanges.from.shiftMillis(duration.toInt())))
                },
                onDurationPresetsChange = onDurationPresetsChange,
            )
        }
        TimeRangeSlider(
            enabled = enabled,
            isError = isTimeValidError,
            scheduleDate = scheduleDate,
            timeRange = timeRanges,
            onTimeRangeChange = onTimeRangeChange,
        )
    }
}

@Composable
internal fun ParametersSection(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    parameters: TimeTaskEditParametersUi,
    onChangeParameters: (TimeTaskEditParametersUi) -> Unit,
) {
    var openTaskNotificationMenu by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SegmentedParametersChooser(
            enabled = enabled,
            parameters = TaskPriorityItemUi.entries.toTypedArray(),
            selected = parameters.priority.convertToItem(),
            leadingIcon = painterResource(id = EditorThemeRes.icons.priority),
            title = EditorThemeRes.strings.priorityParameterTitle,
            onChangeSelected = { priority ->
                onChangeParameters(parameters.copy(priority = priority.convertToModel()))
            },
        )
        ParameterChooser(
            enabled = enabled,
            selected = parameters.isEnableNotification,
            leadingIcon = painterResource(id = EditorThemeRes.icons.notifications),
            title = EditorThemeRes.strings.notifyParameterTitle,
            description = EditorThemeRes.strings.notifyParameterDesc,
            optionsButton = if (parameters.isEnableNotification) {
                {
                    Box {
                        IconButton(
                            enabled = enabled,
                            modifier = Modifier.size(32.dp),
                            onClick = { openTaskNotificationMenu = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        ) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
                        }
                        TaskNotificationsMenu(
                            isExpanded = openTaskNotificationMenu,
                            taskNotification = parameters.taskNotifications,
                            onDismiss = { openTaskNotificationMenu = false },
                            onUpdate = { onChangeParameters(parameters.copy(taskNotifications = it)) },
                        )
                    }
                }
            } else {
                null
            },
            onChangeSelected = { notification ->
                onChangeParameters(parameters.copy(isEnableNotification = notification))
            },
        )
        ParameterChooser(
            enabled = enabled,
            selected = parameters.isConsiderInStatistics,
            leadingIcon = painterResource(id = EditorThemeRes.icons.statistics),
            title = EditorThemeRes.strings.statisticsParameterTitle,
            description = EditorThemeRes.strings.statisticsParameterDesc,
            onChangeSelected = { isConsider ->
                onChangeParameters(parameters.copy(isConsiderInStatistics = isConsider))
            },
        )
    }
}

@Composable
internal fun ActionButtonsSection(
    modifier: Modifier = Modifier,
    isCreateMode: Boolean,
    isTemplate: Boolean,
    onUnlinkTemplate: () -> Unit,
    onControlTemplate: () -> Unit,
    onCreateTemplate: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomStart) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FilledTonalButton(
                onClick = onCancelClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                content = { Text(text = EditorThemeRes.strings.cancelButtonTitle) },
            )
            Button(
                onClick = onSaveClick,
                content = { Text(text = EditorThemeRes.strings.saveTaskButtonTitle) },
            )
            Spacer(modifier = Modifier.weight(1f))
            TemplateSelector(
                isCreateMode = isCreateMode,
                isTemplate = isTemplate,
                onUnlink = onUnlinkTemplate,
                onControl = onControlTemplate,
                onCreateTemplate = onCreateTemplate,
            )
        }
    }
}

@Composable
internal fun TemplateSelector(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isCreateMode: Boolean,
    isTemplate: Boolean,
    onUnlink: () -> Unit,
    onControl: () -> Unit,
    onCreateTemplate: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isTemplate) {
            IconButton(
                onClick = onUnlink,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(40.dp),
                    ),
                enabled = enabled,
            ) {
                Icon(
                    painter = painterResource(EditorThemeRes.icons.unlink),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        if (!isCreateMode && !isTemplate) {
            IconButton(
                onClick = onCreateTemplate,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                    ),
                enabled = enabled,
            ) {
                Icon(
                    painter = painterResource(EditorThemeRes.icons.unFavorite),
                    contentDescription = EditorThemeRes.strings.templateIconDesc,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

