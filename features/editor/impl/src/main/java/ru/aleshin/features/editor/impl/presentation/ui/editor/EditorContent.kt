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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
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
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.CustomLargeTextField
import ru.aleshin.core.utils.extensions.shiftMillis
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditParameters
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorViewState
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.CategoryValidateError
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.TimeRangeError
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.*
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.EndTimeField
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.StartTimeField
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.SubCategoryChooser

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
internal fun EditorContent(
    state: EditorViewState,
    modifier: Modifier = Modifier,
    onCategoriesChange: (MainCategoryUi, SubCategoryUi?) -> Unit,
    onNoteChange: (String?) -> Unit,
    onAddSubCategory: (String) -> Unit,
    onTimeRangeChange: (TimeRange) -> Unit,
    onChangeParameters: (EditParameters) -> Unit,
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
                    modifier = Modifier.weight(1f).verticalScroll(scrollState).padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CategoriesSection(
                        enabled = !state.editModel.checkDateIsRepeat(),
                        isMainCategoryValidError = state.categoryValid is CategoryValidateError.EmptyCategoryError,
                        mainCategory = state.editModel.mainCategory,
                        subCategory = state.editModel.subCategory,
                        allCategories = state.categories,
                        note = state.editModel.note,
                        onCategoriesChange = onCategoriesChange,
                        onAddSubCategory = onAddSubCategory,
                        onNoteChange = onNoteChange,
                    )
                    Divider(Modifier.padding(horizontal = 32.dp))
                    DateTimeSection(
                        enabled = !state.editModel.checkDateIsRepeat(),
                        isTimeValidError = state.timeRangeValid is TimeRangeError.DurationError,
                        timeRanges = state.editModel.timeRange,
                        duration = state.editModel.duration,
                        onTimeRangeChange = onTimeRangeChange,
                    )
                    Divider(Modifier.padding(horizontal = 32.dp))
                    ParametersSection(
                        enabled = !state.editModel.checkDateIsRepeat(),
                        parameters = state.editModel.parameters,
                        onChangeParameters = onChangeParameters,
                    )
                }
                ActionButtonsSection(
                    enableTemplateSelector = state.editModel.key != 0L,
                    isRepeatTemplate = state.editModel.checkDateIsRepeat(),
                    isTemplateSelect = state.editModel.templateId != null,
                    onCancelClick = onCancelClick,
                    onControl = onControlTemplate,
                    onCreateTemplate = onCreateTemplate,
                    onSaveClick = onSaveClick,
                )
            }
        }
    }
}

@Composable
internal fun CategoriesSection(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isMainCategoryValidError: Boolean,
    mainCategory: MainCategoryUi?,
    subCategory: SubCategoryUi?,
    allCategories: List<CategoriesUi>,
    note: String?,
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
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                isError = isMainCategoryValidError,
                currentCategory = mainCategory,
                allCategories = allCategories.map { it.mainCategory },
                onChange = { newMainCategory ->
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
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            mainCategory = mainCategory,
            allSubCategories = findCategories?.subCategories ?: emptyList(),
            currentSubCategory = subCategory,
            onAddSubCategory = onAddSubCategory,
            onChangeCategory = { newSubCategory ->
                if (mainCategory != null) onCategoriesChange(mainCategory, newSubCategory)
            },
        )
        CustomLargeTextField(
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
            trailingIcon = if (noteInteractionSource.collectIsFocusedAsState().value) { {
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
            } } else {
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
    timeRanges: TimeRange,
    duration: Long,
    onTimeRangeChange: (TimeRange) -> Unit,
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
            onChangeTime = { newStartTime -> onTimeRangeChange(timeRanges.copy(from = newStartTime)) },
        )
        EndTimeField(
            enabled = enabled,
            modifier = Modifier.weight(1f),
            currentTime = timeRanges.to,
            isError = isTimeValidError,
            onChangeTime = { newEndTime -> onTimeRangeChange(timeRanges.copy(to = newEndTime)) },
        )
        DurationTitle(
            enabled = enabled,
            duration = duration,
            startTime = timeRanges.from,
            isError = isTimeValidError,
            onChangeDuration = { duration ->
                onTimeRangeChange(timeRanges.copy(to = timeRanges.from.shiftMillis(duration.toInt())))
            },
        )
    }
}

@Composable
internal fun ParametersSection(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    parameters: EditParameters,
    onChangeParameters: (EditParameters) -> Unit,
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
            optionsButton = if (parameters.isEnableNotification) { {
                Box {
                    IconButton(
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
            } } else {
                null
            },
            onChangeSelected = { notification ->
                onChangeParameters(parameters.copy(isEnableNotification = notification))
            },
        )
        ParameterChooser(
            enabled = enabled,
            selected = parameters.isImportant,
            leadingIcon = painterResource(id = EditorThemeRes.icons.priority),
            title = EditorThemeRes.strings.importantParameterTitle,
            description = EditorThemeRes.strings.importantParameterDesc,
            onChangeSelected = { isImportant ->
                onChangeParameters(parameters.copy(isImportant = isImportant))
            },
        )
    }
}

@Composable
internal fun ActionButtonsSection(
    modifier: Modifier = Modifier,
    enableTemplateSelector: Boolean,
    isRepeatTemplate: Boolean,
    isTemplateSelect: Boolean,
    onControl: () -> Unit,
    onCreateTemplate: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomStart) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                enabled = !isRepeatTemplate,
                onClick = onSaveClick,
                content = { Text(text = EditorThemeRes.strings.saveTaskButtonTitle) },
            )
            Spacer(modifier = Modifier.weight(1f))
            if (enableTemplateSelector) {
                TemplateSelector(
                    isSelect = isTemplateSelect,
                    isRepeat = isRepeatTemplate,
                    onControl = onControl,
                    onCreateTemplate = onCreateTemplate,
                )
            }
        }
    }
}

@Composable
internal fun TemplateSelector(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSelect: Boolean,
    isRepeat: Boolean,
    onControl: () -> Unit,
    onCreateTemplate: () -> Unit,
) {
    IconButton(
        onClick = { if (isSelect) onControl() else onCreateTemplate() },
        modifier = modifier.size(40.dp).background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(40.dp),
        ),
        enabled = enabled,
    ) {
        val templatesButton = when (isSelect) {
            true -> when (isRepeat) {
                true -> EditorThemeRes.icons.repeat
                false -> TimePlannerRes.icons.enabledSettingsIcon
            }
            false -> EditorThemeRes.icons.unFavorite
        }
        Icon(
            painter = painterResource(id = templatesButton),
            contentDescription = EditorThemeRes.strings.templateIconDesc,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
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
