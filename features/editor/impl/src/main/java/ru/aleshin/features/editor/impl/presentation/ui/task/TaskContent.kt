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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.extensions.changeDay
import ru.aleshin.core.utils.extensions.fetchHourOfDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.shiftMillis
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.presentation.mappers.convertToItem
import ru.aleshin.features.editor.impl.presentation.mappers.convertToModel
import ru.aleshin.features.editor.impl.presentation.mappers.mapToMessage
import ru.aleshin.features.editor.impl.presentation.models.tasks.TaskPriorityItemUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditParametersUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskEffect
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskEvent
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskState
import ru.aleshin.features.editor.impl.presentation.ui.task.store.TaskComponent
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.CategoryValidateError
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.TimeRangeError
import ru.aleshin.features.editor.impl.presentation.ui.task.views.DurationTitle
import ru.aleshin.features.editor.impl.presentation.ui.task.views.EditorTopAppBar
import ru.aleshin.features.editor.impl.presentation.ui.task.views.EndTimeField
import ru.aleshin.features.editor.impl.presentation.ui.task.views.MainCategoryChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.ParameterChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.SegmentedParametersChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.StartTimeField
import ru.aleshin.features.editor.impl.presentation.ui.task.views.SubCategoryChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.TaskNotificationsMenu
import ru.aleshin.features.editor.impl.presentation.ui.task.views.TemplatesBottomSheet
import ru.aleshin.features.editor.impl.presentation.ui.task.views.UndefinedTasksBottomSheet
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.views.CustomLargeTextField
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import java.util.Date

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TaskContent(
    taskComponent: TaskComponent,
    modifier: Modifier = Modifier
) {
    val store = taskComponent.store
    val state by store.stateAsState()
    val hostState = remember { SnackbarHostState() }
    var isTemplatesSheetOpen by rememberSaveable { mutableStateOf(false) }
    var isUndefinedTasksSheetOpen by rememberSaveable { mutableStateOf(false) }
    val strings = EditorThemeRes.strings

    Scaffold(
        content = { paddingValues ->
            BaseEditorContent(
                state = state,
                modifier = modifier.padding(paddingValues),
                onCategoriesChange = { main, sub -> store.dispatchEvent(TaskEvent.ChangeCategories(main, sub)) },
                onNoteChange = { store.dispatchEvent(TaskEvent.ChangeNote(it)) },
                onAddSubCategory = { store.dispatchEvent(TaskEvent.AddSubCategory(it)) },
                onTimeRangeChange = { store.dispatchEvent(TaskEvent.ChangeTime(it)) },
                onChangeParameters = { store.dispatchEvent(TaskEvent.ChangeParameters(it)) },
                onDurationPresetsChange = { store.dispatchEvent(TaskEvent.UpdateDurationPresets(it)) },
                onEditCategory = { store.dispatchEvent(TaskEvent.NavigateToCategoryEditor(it)) },
                onEditSubCategory = { store.dispatchEvent(TaskEvent.NavigateToSubCategoryEditor(it)) },
                onUnlinkTemplate = { store.dispatchEvent(TaskEvent.PressUnlinkTemplateButton) },
                onControlTemplate = { store.dispatchEvent(TaskEvent.PressControlTemplateButton) },
                onCreateTemplate = { store.dispatchEvent(TaskEvent.CreateTemplate) },
                onSaveClick = { store.dispatchEvent(TaskEvent.PressSaveButton) },
                onCancelClick = { store.dispatchEvent(TaskEvent.PressBackButton) },
            )
        },
        topBar = {
            EditorTopAppBar(
                actionsEnabled = state.editModel?.linkedTemplateId == null,
                countUndefinedTasks = state.undefinedTasks?.size ?: 0,
                onBackIconClick = { store.dispatchEvent(TaskEvent.PressBackButton) },
                onDeleteActionClick = { store.dispatchEvent(TaskEvent.PressDeleteButton) },
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
        currentTemplateId = state.editModel?.linkedTemplateId,
        onDismiss = { isTemplatesSheetOpen = false },
        onControlClick = {
            store.dispatchEvent(TaskEvent.PressControlTemplateButton)
        },
        onChooseTemplate = { template ->
            store.dispatchEvent(TaskEvent.ApplyTemplate(template))
            isTemplatesSheetOpen = false
        },
    )

    UndefinedTasksBottomSheet(
        isShow = isUndefinedTasksSheetOpen,
        undefinedTasks = state.undefinedTasks,
        currentUndefinedTaskId = state.editModel?.undefinedTaskId,
        onDismiss = { isUndefinedTasksSheetOpen = false },
        onChooseUndefinedTask = {
            store.dispatchEvent(TaskEvent.ApplyUndefinedTask(it))
            isUndefinedTasksSheetOpen = false
        },
    )

    store.handleEffects { effect ->
        when (effect) {
            is TaskEffect.ShowError -> {
                hostState.showSnackbar(message = effect.failures.mapToMessage(strings))
            }
            is TaskEffect.ShowOverlayError -> {
                val result = hostState.showSnackbar(
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

@Composable
internal fun BaseEditorContent(
    state: TaskState,
    modifier: Modifier = Modifier,
    onCategoriesChange: (MainCategoryUi, SubCategoryUi?) -> Unit,
    onNoteChange: (String?) -> Unit,
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
    val scrollState = rememberScrollState()
    AnimatedVisibility(
        visible = state.editModel != null,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(modifier = modifier.fillMaxSize().animateContentSize()) {
            if (state.editModel != null) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CategoriesSection(
                        enabledCategories = state.editModel.linkedTemplateId == null,
                        isMainCategoryValidError = state.categoryValid is CategoryValidateError.EmptyCategoryError,
                        mainCategory = state.editModel.mainCategory,
                        subCategory = state.editModel.subCategory,
                        allCategories = state.categories,
                        note = state.editModel.note,
                        onEditCategory = onEditCategory,
                        onEditSubCategory = onEditSubCategory,
                        onCategoriesChange = onCategoriesChange,
                        onAddSubCategory = onAddSubCategory,
                        onNoteChange = onNoteChange,
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 32.dp))
                    DateTimeSection(
                        enabled = state.editModel.linkedTemplateId == null,
                        isTimeValidError = state.timeRangeValid is TimeRangeError.DurationError,
                        scheduleDate = state.editModel.date,
                        timeRanges = state.editModel.timeRange,
                        duration = state.editModel.duration,
                        durationPresets = state.durationPresets,
                        onTimeRangeChange = {
                            onTimeRangeChange(it)
                        },
                        onDurationPresetsChange = onDurationPresetsChange,
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 32.dp))
                    ParametersSection(
                        enabled = state.editModel.linkedTemplateId == null,
                        parameters = state.editModel.parameters,
                        onChangeParameters = onChangeParameters,
                    )
                }
                ActionButtonsSection(
                    isCreateMode = state.editModel.key == 0L,
                    isTemplate = state.editModel.linkedTemplateId != null,
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
internal fun CategoriesSection(
    modifier: Modifier = Modifier,
    enabledCategories: Boolean = true,
    enabledNote: Boolean = true,
    isMainCategoryValidError: Boolean,
    mainCategory: MainCategoryUi?,
    subCategory: SubCategoryUi?,
    allCategories: List<MainCategoryDetailsUi>,
    note: String?,
    onEditCategory: (MainCategoryUi) -> Unit,
    onEditSubCategory: (SubCategoryUi) -> Unit,
    onCategoriesChange: (MainCategoryUi, SubCategoryUi?) -> Unit,
    onAddSubCategory: (String) -> Unit,
    onNoteChange: (String?) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val noteInteractionSource = remember { MutableInteractionSource() }
    var editableNote by remember {
        mutableStateOf(TextFieldValue(text = note ?: ""))
    }

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
                allCategories = allCategories.map { it.mainCategory },
                onEditCategory = onEditCategory,
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
        val findCategories = allCategories.find { it.mainCategory == mainCategory }
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
        CustomLargeTextField(
            enabled = enabledNote,
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
                        onClick = { focusManager.clearFocus(); },
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
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
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
        if (!isCreateMode) {
            IconButton(
                onClick = { if (isTemplate) onControl() else onCreateTemplate() },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(40.dp),
                    ),
                enabled = enabled,
            ) {
                val templatesButton = when (isTemplate) {
                    true -> TimePlannerRes.icons.enabledSettingsIcon
                    false -> EditorThemeRes.icons.unFavorite
                }
                Icon(
                    painter = painterResource(id = templatesButton),
                    contentDescription = EditorThemeRes.strings.templateIconDesc,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

/* ----------------------- Release Preview -----------------------
@Composable
@Preview(showBackground = true)
internal fun EditContent_Light_Preview() {
    TimePlannerTheme(dynamicColor = false, themeColorsType = ThemeColorsUiType.LIGHT) {
        EditorTheme {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                val currentTime = Calendar.getInstance().time
                val timeTask = EditModel(
                    date = currentTime,
                    timeRanges = TimeRange(currentTime, currentTime),
                )
                EditorContent(
                    state = EditorViewState(timeTask),
                    onCategoryChoose = {},
                    onSubCategoryChoose = {},
                    onAddSubCategory = {},
                    onTimeRangeChange = { _, _ -> },
                    onChangeParameters = { _, _ -> },
                    onChangeTemplate = {},
                    onSaveClick = {},
                    onCancelClick = {},
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
internal fun EditContent_Dark_Preview() {
    TimePlannerTheme(dynamicColor = false, themeColorsType = ThemeColorsUiType.DARK) {
        EditorTheme {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                val currentTime = Calendar.getInstance().time
                val timeTask = EditModel(
                    date = currentTime,
                    timeRanges = TimeRange(currentTime, currentTime),
                )
                EditorContent(
                    state = EditorViewState(timeTask),
                    onCategoryChoose = {},
                    onSubCategoryChoose = {},
                    onAddSubCategory = {},
                    onTimeRangeChange = { _, _ -> },
                    onChangeParameters = { _, _ -> },
                    onChangeTemplate = {},
                    onSaveClick = {},
                    onCancelClick = {},
                )
            }
        }
    }
}
*/
