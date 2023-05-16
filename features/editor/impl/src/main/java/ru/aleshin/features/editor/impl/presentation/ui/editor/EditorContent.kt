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
package ru.aleshin.features.editor.impl.presentation.ui.editor

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.extensions.shiftMillis
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.presentation.models.EditParameters
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorViewState
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.CategoryValidateError
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.TimeRangeError
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.*
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.EndTimeField
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.StartTimeField
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.SubCategoryChooser
import ru.aleshin.features.home.api.domains.entities.categories.Categories
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
internal fun EditorContent(
    state: EditorViewState,
    modifier: Modifier = Modifier,
    onCategoriesChange: (MainCategory, SubCategory?) -> Unit,
    onManageCategories: () -> Unit,
    onTimeRangeChange: (TimeRange) -> Unit,
    onChangeParameters: (EditParameters) -> Unit,
    onChangeTemplate: (Boolean) -> Unit,
    onSaveClick: (isTemplateUpdate: Boolean) -> Unit,
    onCancelClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var isManageWarningDialog by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxSize().animateContentSize()) {
        if (state.editModel != null) {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(scrollState).padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CategoriesSection(
                    isMainCategoryValid = state.categoryValid is CategoryValidateError.EmptyCategoryError,
                    mainCategory = state.editModel.mainCategory,
                    subCategory = state.editModel.subCategory,
                    allCategories = state.categories,
                    onCategoriesChange = onCategoriesChange,
                    onSubCategoriesManage = { isManageWarningDialog = true },
                )
                Divider(Modifier.padding(horizontal = 32.dp))
                DateTimeSection(
                    isTimeValid = state.timeRangeValid is TimeRangeError.DurationError,
                    timeRanges = state.editModel.timeRanges,
                    duration = state.editModel.duration,
                    onTimeRangeChange = onTimeRangeChange,
                )
                Divider(Modifier.padding(horizontal = 32.dp))
                ParametersSection(
                    parameters = state.editModel.parameters,
                    onChangeParameters = onChangeParameters,
                )
            }
            ActionButtonsSection(
                enableTemplateSelector = state.editModel.key != 0L,
                isTemplateSelect = state.editModel.templateId != null,
                onChangeTemplate = onChangeTemplate,
                onCancelClick = onCancelClick,
                onSaveClick = onSaveClick,
            )
        }
    }
    if (isManageWarningDialog) {
        CategoriesManageWarningDialog(
            onDismiss = { isManageWarningDialog = false },
            onAction = { isManageWarningDialog = false; onManageCategories() },
        )
    }
}

@Composable
internal fun CategoriesSection(
    modifier: Modifier = Modifier,
    isMainCategoryValid: Boolean,
    mainCategory: MainCategory?,
    subCategory: SubCategory?,
    allCategories: List<Categories>,
    onCategoriesChange: (MainCategory, SubCategory?) -> Unit,
    onSubCategoriesManage: () -> Unit,
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
                modifier = Modifier.fillMaxWidth(),
                isError = isMainCategoryValid,
                currentCategory = mainCategory,
                allMainCategories = allCategories.map { it.mainCategory },
                onCategoryChange = { newMainCategory ->
                    onCategoriesChange(newMainCategory, null)
                },
            )
            if (isMainCategoryValid) {
                Text(
                    text = EditorThemeRes.strings.categoryValidateError,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        val findCategories = allCategories.find { it.mainCategory == mainCategory }
        SubCategoryChooser(
            modifier = Modifier.fillMaxWidth(),
            mainCategory = mainCategory,
            allSubCategories = findCategories?.subCategories ?: emptyList(),
            currentSubCategory = subCategory,
            onSubCategoryChange = { newSubCategory ->
                if (mainCategory != null) onCategoriesChange(mainCategory, newSubCategory)
            },
            onManageCategories = onSubCategoriesManage,
        )
    }
}

@Composable
internal fun DateTimeSection(
    modifier: Modifier = Modifier,
    isTimeValid: Boolean,
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
            modifier = Modifier.weight(1f),
            currentTime = timeRanges.from,
            isError = isTimeValid,
            onChangeTime = { newStartTime -> onTimeRangeChange(timeRanges.copy(from = newStartTime)) },
        )
        EndTimeField(
            modifier = Modifier.weight(1f),
            currentTime = timeRanges.to,
            isError = isTimeValid,
            onChangeTime = { newEndTime -> onTimeRangeChange(timeRanges.copy(to = newEndTime)) },
        )
        DurationTitle(
            duration = duration,
            startTime = timeRanges.from,
            isError = isTimeValid,
            onChangeDuration = { duration ->
                onTimeRangeChange(timeRanges.copy(to = timeRanges.from.shiftMillis(duration.toInt())))
            },
        )
    }
}

@Composable
internal fun ParametersSection(
    modifier: Modifier = Modifier,
    parameters: EditParameters,
    onChangeParameters: (EditParameters) -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ParameterChooser(
            modifier = Modifier,
            enabled = parameters.isConsiderInStatistics,
            title = EditorThemeRes.strings.statisticsParameterTitle,
            description = EditorThemeRes.strings.statisticsParameterDesc,
            onChangeEnabled = { isConsider ->
                onChangeParameters(parameters.copy(isConsiderInStatistics = isConsider))
            },
        )
        ParameterChooser(
            modifier = Modifier,
            enabled = parameters.isEnableNotification,
            title = EditorThemeRes.strings.notifyParameterTitle,
            description = EditorThemeRes.strings.notifyParameterDesc,
            onChangeEnabled = { notification ->
                onChangeParameters(parameters.copy(isEnableNotification = notification))
            },
        )
        ParameterChooser(
            modifier = Modifier,
            enabled = parameters.isImportant,
            title = EditorThemeRes.strings.importantParameterTitle,
            description = EditorThemeRes.strings.importantParameterDesc,
            onChangeEnabled = { isImportant ->
                onChangeParameters(parameters.copy(isImportant = isImportant))
            },
        )
    }
}

@Composable
internal fun ActionButtonsSection(
    modifier: Modifier = Modifier,
    enableTemplateSelector: Boolean,
    isTemplateSelect: Boolean,
    onChangeTemplate: (Boolean) -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: (isTemplateUpdate: Boolean) -> Unit,
) {
    var isWarningDialogOpen by rememberSaveable { mutableStateOf(false) }
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
                onClick = {
                    when (enableTemplateSelector && isTemplateSelect) {
                        true -> isWarningDialogOpen = true
                        false -> onSaveClick(false)
                    }
                },
                content = { Text(text = EditorThemeRes.strings.saveTaskButtonTitle) },
            )
            Spacer(modifier = Modifier.weight(1f))
            if (enableTemplateSelector) {
                TemplateSelector(
                    isSelect = isTemplateSelect,
                    onSelectChanges = onChangeTemplate,
                )
            }
        }
    }
    if (isWarningDialogOpen) {
        TemplateSaveWarningDialog(
            onDismiss = { isWarningDialogOpen = false },
            onAction = { isSave ->
                onSaveClick(isSave)
                isWarningDialogOpen = false
            },
        )
    }
}

@Composable
internal fun TemplateSelector(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSelect: Boolean,
    onSelectChanges: (Boolean) -> Unit,
) {
    IconButton(
        onClick = { onSelectChanges.invoke(!isSelect) },
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        enabled = enabled,
    ) {
        val templatesButton = when (isSelect) {
            true -> Icons.Default.Favorite
            false -> Icons.Default.FavoriteBorder
        }
        Icon(
            imageVector = templatesButton,
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
