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
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.views.DurationPickerDialog
import ru.aleshin.core.ui.views.toMinutesAndHoursTitle
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.shiftMillis
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorViewState
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.CategoryValidateError
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.TimeRangeError
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.*
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.EndTimeField
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.StartTimeField
import ru.aleshin.features.editor.impl.presentation.ui.editor.views.SubCategoryChooser
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import java.util.*

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
internal fun EditorContent(
    state: EditorViewState,
    modifier: Modifier = Modifier,
    onCategoryChoose: (MainCategory) -> Unit,
    onSubCategoryChoose: (SubCategory?) -> Unit,
    onAddSubCategory: () -> Unit,
    onTimeRangeChange: (start: Date, end: Date) -> Unit,
    onChangeParameters: (notification: Boolean, statistics: Boolean) -> Unit,
    onChangeTemplate: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxSize().animateContentSize()) {
        if (state.editModel != null) {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Divider(
                    Modifier.padding(vertical = 4.dp, horizontal = 16.dp).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                )
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column(
                        modifier = Modifier.animateContentSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        val isError = state.categoryValid is CategoryValidateError.EmptyCategoryError
                        MainCategoryChooser(
                            modifier = Modifier.fillMaxWidth(),
                            isError = isError,
                            currentCategory = state.editModel.mainCategory,
                            allMainCategories = state.categories.map { it.mainCategory },
                            onCategoryChoose = onCategoryChoose,
                        )
                        if (isError) {
                            Text(
                                text = EditorThemeRes.strings.categoryValidateError,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                    val mainCategory = state.editModel.mainCategory
                    val categories = state.categories.find { it.mainCategory == mainCategory }
                    SubCategoryChooser(
                        modifier = Modifier.fillMaxWidth(),
                        mainCategory = state.editModel.mainCategory,
                        allSubCategories = categories?.subCategories ?: emptyList(),
                        currentSubCategory = state.editModel.subCategory,
                        onSubCategoryChoose = onSubCategoryChoose,
                        onAddSubCategory = onAddSubCategory,
                    )
                }
                Divider(Modifier.padding(horizontal = 32.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StartTimeField(
                        modifier = Modifier.weight(1f),
                        currentTime = state.editModel.timeRanges.from,
                        isError = state.timeRangeValid is TimeRangeError.DurationError,
                        onChangeTime = { onTimeRangeChange(it, state.editModel.timeRanges.to) },
                    )
                    EndTimeField(
                        modifier = Modifier.weight(1f),
                        currentTime = state.editModel.timeRanges.to,
                        isError = state.timeRangeValid is TimeRangeError.DurationError,
                        onChangeTime = { onTimeRangeChange(state.editModel.timeRanges.from, it) },
                    )
                    DurationTitle(
                        duration = state.editModel.duration,
                        isError = state.timeRangeValid is TimeRangeError.DurationError,
                        onChangeDuration = { duration ->
                            val start = state.editModel.timeRanges.from
                            val end = Calendar.getInstance().apply { time = start }.time.shiftMillis(
                                duration.toInt(),
                            )
                            if (end.isCurrentDay(state.editModel.date)) {
                                onTimeRangeChange(start, end)
                            }
                        },
                    )
                }
                Divider(Modifier.padding(horizontal = 32.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ParameterChooser(
                        modifier = Modifier,
                        enabled = state.editModel.isEnableNotification,
                        title = EditorThemeRes.strings.notifyParameterTitle,
                        description = EditorThemeRes.strings.notifyParameterDesc,
                        onChangeEnabled = {
                            onChangeParameters(it, state.editModel.isConsiderInStatistics)
                        },
                    )
                    ParameterChooser(
                        modifier = Modifier,
                        enabled = state.editModel.isConsiderInStatistics,
                        title = EditorThemeRes.strings.statisticsParameterTitle,
                        description = EditorThemeRes.strings.statisticsParameterDesc,
                        onChangeEnabled = {
                            onChangeParameters(state.editModel.isEnableNotification, it)
                        },
                    )
                }
            }
            Box(modifier = Modifier, contentAlignment = Alignment.BottomStart) {
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
                    Button(onClick = onSaveClick) {
                        Text(text = EditorThemeRes.strings.saveTaskButtonTitle)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (state.editModel.key != 0L) {
                        TemplateSelector(
                            isSelect = state.editModel.templateId != null,
                            onSelectChanges = onChangeTemplate,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun DurationTitle(
    modifier: Modifier = Modifier,
    duration: Long,
    isError: Boolean = false,
    onChangeDuration: (Long) -> Unit,
) {
    var isOpenDurationDialog by remember { mutableStateOf(false) }
    val correctDuration = if (duration < 0L) 0L else duration
    val titleColor = when (isError) {
        true -> MaterialTheme.colorScheme.error
        false -> MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = modifier.clip(MaterialTheme.shapes.small).clickable {
            isOpenDurationDialog = true
        },
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = correctDuration.toMinutesAndHoursTitle(),
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            color = titleColor,
        )
    }
    if (isOpenDurationDialog) {
        DurationPickerDialog(
            headerTitle = EditorThemeRes.strings.durationPickerTitle,
            duration = duration,
            onDismissRequest = { isOpenDurationDialog = false },
            onSelectedTime = {
                onChangeDuration(it)
                isOpenDurationDialog = false
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
