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
package ru.aleshin.features.home.impl.presentation.ui.templates.views

import android.text.format.DateFormat
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.DialogButtons
import ru.aleshin.core.ui.views.ExpandedIcon
import ru.aleshin.core.ui.views.TimeFormatSelector
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.extensions.setHoursAndMinutes
import ru.aleshin.core.utils.functional.TimeFormat
import ru.aleshin.features.home.api.domains.entities.categories.Categories
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.entities.template.Template
import ru.aleshin.features.home.api.presentation.mappers.fetchNameByLanguage
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import java.util.Calendar

/**
 * @author Stanislav Aleshin on 04.06.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TemplateEditorDialog(
    modifier: Modifier = Modifier,
    categories: List<Categories>,
    editTemplateModel: Template?,
    onDismiss: () -> Unit,
    onConfirm: (Template) -> Unit,
) {
    val scrollState = rememberScrollState()
    var mainCategory by remember {
        mutableStateOf(editTemplateModel?.category ?: MainCategory.absent())
    }
    var isEnableNotification by remember {
        mutableStateOf(editTemplateModel?.isEnableNotification ?: true)
    }
    var isConsiderInStatistics by remember {
        mutableStateOf(editTemplateModel?.isConsiderInStatistics ?: true)
    }
    var subCategory by remember { mutableStateOf(editTemplateModel?.subCategory) }
    var timeStartHours by remember { mutableStateOf(editTemplateModel?.startTime?.hours) }
    var timeStartMinutes by remember { mutableStateOf(editTemplateModel?.startTime?.minutes) }
    var timeEndHours by remember { mutableStateOf(editTemplateModel?.endTime?.hours) }
    var timeEndMinutes by remember { mutableStateOf(editTemplateModel?.endTime?.minutes) }
    var isImportance by remember { mutableStateOf(editTemplateModel?.isImportant ?: false) }

    AlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .width(328.dp)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = TimePlannerRes.elevations.levelThree,
        ) {
            Column {
                TemplateEditorDialogHeader()
                Divider(Modifier.fillMaxWidth())
                Column(
                    modifier = Modifier
                        .height(400.dp)
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 16.dp,
                            bottom = 0.dp,
                        )
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TemplateEditorCategoryChooser(
                        allCategories = categories,
                        selectedCategory = mainCategory,
                        onCategoryChange = { mainCategory = it; subCategory = null },
                    )
                    TemplateEditorSubCategoryChooser(
                        allCategories = categories,
                        selectedMainCategory = mainCategory,
                        selectedSubCategory = subCategory,
                        onSubCategoryChange = { subCategory = it },
                    )
                    TemplateEditorStartTimeChooser(
                        hours = timeStartHours,
                        minutes = timeStartMinutes,
                        onTimeChange = { hours, minutes ->
                            timeStartHours = hours
                            timeStartMinutes = minutes
                        },
                    )
                    TemplateEditorEndTimeChooser(
                        hours = timeEndHours,
                        minutes = timeEndMinutes,
                        onTimeChange = { hours, minutes ->
                            timeEndHours = hours
                            timeEndMinutes = minutes
                        },
                    )
                    TemplateEditorNotificationChooser(
                        isEnableNotification = isEnableNotification,
                        onNotificationEnabledChange = { isEnableNotification = it },
                    )
                    TemplateEditorStatisticsChooser(
                        isConsiderInStatistics = isConsiderInStatistics,
                        onStatisticsConsiderChange = { isConsiderInStatistics = it },
                    )
                    TemplateEditorImportanceChooser(
                        isImportance = isImportance,
                        onImportanceChange = { isImportance = it },
                    )
                }

                val isEnabled = if (timeStartHours != null && timeStartMinutes != null &&
                    timeEndHours != null && timeEndMinutes != null
                ) {
                    val startTimeInMinutes = timeStartHours!! * 60 + timeStartMinutes!!
                    val endTimeInMinutes = timeEndHours!! * 60 + timeEndMinutes!!
                    endTimeInMinutes - startTimeInMinutes > 0
                } else {
                    false
                }
                DialogButtons(
                    isConfirmEnabled = isEnabled,
                    confirmTitle = when (editTemplateModel != null) {
                        true -> TimePlannerRes.strings.alertDialogOkConfirmTitle
                        false -> HomeThemeRes.strings.dialogCreateTitle
                    },
                    onConfirmClick = {
                        if (isEnabled) {
                            val calendar = Calendar.getInstance()
                            val template = Template(
                                templateId = editTemplateModel?.templateId
                                    ?: generateUniqueKey().toInt(),
                                startTime = calendar.setHoursAndMinutes(
                                    hours = timeStartHours!!,
                                    minutes = timeStartMinutes!!,
                                ).time,
                                endTime = calendar.setHoursAndMinutes(
                                    hours = timeEndHours!!,
                                    minutes = timeEndMinutes!!,
                                ).time,
                                category = mainCategory,
                                subCategory = subCategory,
                                isEnableNotification = isEnableNotification,
                                isConsiderInStatistics = isConsiderInStatistics,
                                isImportant = isImportance,
                            )
                            onConfirm(template)
                        }
                    },
                    onCancelClick = onDismiss,
                )
            }
        }
    }
}

@Composable
internal fun TemplateEditorCategoryChooser(
    modifier: Modifier = Modifier,
    allCategories: List<Categories>,
    selectedCategory: MainCategory,
    onCategoryChange: (MainCategory) -> Unit,
) {
    var isCategoryMenuOpen by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.category),
            contentDescription = HomeThemeRes.strings.mainCategoryLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = selectedCategory.fetchNameByLanguage(),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = HomeThemeRes.strings.mainCategoryLabel) },
            trailingIcon = { ExpandedIcon(isExpanded = isCategoryMenuOpen) },
            interactionSource = interactionSource,
        )
        Box(contentAlignment = Alignment.TopEnd) {
            MainCategoriesChooseMenu(
                isExpanded = isCategoryMenuOpen,
                mainCategories = allCategories.map { it.mainCategory },
                onDismiss = { isCategoryMenuOpen = false },
                onChoose = { mainCategory ->
                    isCategoryMenuOpen = false
                    onCategoryChange(mainCategory)
                },
            )
        }
    }

    LaunchedEffect(key1 = isPressed) {
        if (isPressed) {
            isCategoryMenuOpen = !isCategoryMenuOpen
        }
    }
}

@Composable
internal fun MainCategoriesChooseMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    mainCategories: List<MainCategory>,
    onDismiss: () -> Unit,
    onChoose: (MainCategory) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 200.dp),
        offset = DpOffset(0.dp, 6.dp),
    ) {
        mainCategories.forEach { category ->
            DropdownMenuItem(
                onClick = { onChoose(category) },
                text = {
                    Text(
                        text = category.fetchNameByLanguage(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
            )
        }
    }
}

@Composable
internal fun TemplateEditorSubCategoryChooser(
    modifier: Modifier = Modifier,
    allCategories: List<Categories>,
    selectedMainCategory: MainCategory,
    selectedSubCategory: SubCategory?,
    onSubCategoryChange: (SubCategory?) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isSubCategoryMenuOpen by remember { mutableStateOf(false) }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()
    val subCategories =
        allCategories.find { it.mainCategory == selectedMainCategory }?.subCategories ?: emptyList()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.subCategory),
            contentDescription = HomeThemeRes.strings.subCategoryLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = selectedSubCategory?.fetchNameByLanguage()
                ?: HomeThemeRes.strings.subCategoryEmptyTitle,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = HomeThemeRes.strings.subCategoryLabel) },
            trailingIcon = { ExpandedIcon(isExpanded = isSubCategoryMenuOpen) },
            interactionSource = interactionSource,
        )
        Box(contentAlignment = Alignment.TopEnd) {
            SubCategoriesChooseMenu(
                isExpanded = isSubCategoryMenuOpen,
                subCategories = subCategories.toMutableList().apply {
                    add(SubCategory.absentSubCategory(MainCategory.absent()))
                },
                onDismiss = { isSubCategoryMenuOpen = false },
                onChoose = { subCategory ->
                    isSubCategoryMenuOpen = false
                    onSubCategoryChange(subCategory)
                },
            )
        }
    }

    LaunchedEffect(key1 = isPressed) {
        if (isPressed) {
            isSubCategoryMenuOpen = !isSubCategoryMenuOpen
        }
    }
}

@Composable
internal fun SubCategoriesChooseMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    subCategories: List<SubCategory>,
    onDismiss: () -> Unit,
    onChoose: (SubCategory?) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 200.dp),
        offset = DpOffset(0.dp, 6.dp),
    ) {
        subCategories.forEach { subCategory ->
            DropdownMenuItem(
                onClick = {
                    if (subCategory.id == -1) onChoose(null) else onChoose(subCategory)
                },
                text = {
                    Text(
                        text = subCategory.fetchNameByLanguage(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
            )
        }
    }
}

@Composable
internal fun TemplateEditorStartTimeChooser(
    modifier: Modifier = Modifier,
    hours: Int?,
    minutes: Int?,
    onTimeChange: (hours: Int?, minutes: Int?) -> Unit,
) {
    val is24Format = DateFormat.is24HourFormat(LocalContext.current)
    var format by remember {
        mutableStateOf(if (hours != null && hours > 11) TimeFormat.PM else TimeFormat.AM)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.startTime),
            contentDescription = HomeThemeRes.strings.startTimeLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = if (is24Format) {
                    hours?.toString() ?: ""
                } else {
                    when {
                        hours == 0 && format == TimeFormat.AM -> "12"
                        hours == 0 && format == TimeFormat.PM -> "12"
                        format == TimeFormat.PM && hours != 12 -> hours?.minus(12)?.toString() ?: ""
                        else -> hours?.toString() ?: ""
                    }
                },
                onValueChange = {
                    val time = it.toIntOrNull()
                    if (time != null && is24Format && time in 0..23) {
                        onTimeChange(time, minutes)
                    } else if (time != null && !is24Format && time in 1..12) {
                        val formatTime = when (format) {
                            TimeFormat.PM -> if (time != 12) time + 12 else 12
                            TimeFormat.AM -> if (time != 12) time else 0
                        }
                        onTimeChange(formatTime, minutes)
                    } else if (it.isBlank()) {
                        onTimeChange(null, minutes)
                    }
                },
                label = { Text(text = HomeThemeRes.strings.startTimeLabel) },
            )
            Text(
                text = ":",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                modifier = Modifier.weight(1f).align(Alignment.Bottom),
                value = minutes?.toString() ?: "",
                onValueChange = {
                    val time = it.toIntOrNull()
                    if (time != null && time in 0..59) {
                        onTimeChange(hours, time)
                    } else if (it.isBlank()) {
                        onTimeChange(hours, null)
                    }
                },
            )
        }
        TimeFormatSelector(
            modifier = Modifier.size(height = 60.dp, width = 45.dp).align(Alignment.Bottom),
            isVisible = !is24Format,
            format = format,
            onChangeFormat = {
                onTimeChange(null, minutes)
                format = it
            },
        )
    }
}

@Composable
internal fun TemplateEditorEndTimeChooser(
    modifier: Modifier = Modifier,
    hours: Int?,
    minutes: Int?,
    onTimeChange: (hours: Int?, minutes: Int?) -> Unit,
) {
    val is24Format = DateFormat.is24HourFormat(LocalContext.current)
    var format by remember {
        mutableStateOf(if (hours != null && hours > 11) TimeFormat.PM else TimeFormat.AM)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.endTime),
            contentDescription = HomeThemeRes.strings.endTimeLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = if (is24Format) {
                    hours?.toString() ?: ""
                } else {
                    when {
                        hours == 0 && format == TimeFormat.AM -> "12"
                        hours == 0 && format == TimeFormat.PM -> "12"
                        format == TimeFormat.PM && hours != 12 -> hours?.minus(12)?.toString() ?: ""
                        else -> hours?.toString() ?: ""
                    }
                },
                onValueChange = {
                    val time = it.toIntOrNull()
                    if (time != null && is24Format && time in 0..23) {
                        onTimeChange(time, minutes)
                    } else if (time != null && !is24Format && time in 1..12) {
                        if ((time in 1..12 && format == TimeFormat.PM) ||
                            (time in 1..11 && format == TimeFormat.AM)
                        ) {
                            val formatTime = when (format) {
                                TimeFormat.PM -> if (time != 12) time + 12 else 12
                                TimeFormat.AM -> if (time != 12) time else 0
                            }
                            onTimeChange(formatTime, minutes)
                        }
                    } else if (it.isBlank()) {
                        onTimeChange(null, minutes)
                    }
                },
                label = { Text(text = HomeThemeRes.strings.endTimeLabel) },
            )
            Text(
                text = ":",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                modifier = Modifier.weight(1f).align(Alignment.Bottom),
                value = minutes?.toString() ?: "",
                onValueChange = {
                    val time = it.toIntOrNull()
                    if (time != null && time in 0..59) {
                        onTimeChange(hours, time)
                    } else if (it.isBlank()) onTimeChange(hours, null)
                },
            )
        }
        TimeFormatSelector(
            modifier = Modifier.size(height = 60.dp, width = 45.dp).align(Alignment.Bottom),
            isVisible = !is24Format,
            format = format,
            onChangeFormat = {
                onTimeChange(null, minutes)
                format = it
            },
        )
    }
}

@Composable
internal fun TemplateEditorNotificationChooser(
    modifier: Modifier = Modifier,
    isEnableNotification: Boolean,
    onNotificationEnabledChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.notification),
            contentDescription = HomeThemeRes.strings.notificationLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = HomeThemeRes.strings.notificationLabel,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Switch(checked = isEnableNotification, onCheckedChange = onNotificationEnabledChange)
        }
    }
}

@Composable
internal fun TemplateEditorStatisticsChooser(
    modifier: Modifier = Modifier,
    isConsiderInStatistics: Boolean,
    onStatisticsConsiderChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.info),
            contentDescription = HomeThemeRes.strings.statisticsLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = HomeThemeRes.strings.statisticsLabel,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Switch(checked = isConsiderInStatistics, onCheckedChange = onStatisticsConsiderChange)
        }
    }
}

@Composable
internal fun TemplateEditorImportanceChooser(
    modifier: Modifier = Modifier,
    isImportance: Boolean,
    onImportanceChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.importance),
            contentDescription = HomeThemeRes.strings.importanceLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = HomeThemeRes.strings.importanceLabel,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Switch(checked = isImportance, onCheckedChange = onImportanceChange)
        }
    }
}

@Composable
internal fun TemplateEditorDialogHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(top = 24.dp, bottom = 12.dp, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = HomeThemeRes.strings.templateEditorHeader,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
