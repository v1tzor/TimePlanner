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
package ru.aleshin.features.home.impl.presentation.ui.templates.views

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.schedules.TaskPriority
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.DialogButtons
import ru.aleshin.core.ui.views.TimeFormatSelector
import ru.aleshin.core.ui.views.changeTwoDigitNumber
import ru.aleshin.core.ui.views.endLimitCharTransition
import ru.aleshin.core.ui.views.mapHour24ToAmPm
import ru.aleshin.core.ui.views.mapHourAmPmTo24
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.extensions.setHoursAndMinutes
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.TimeFormat
import ru.aleshin.features.home.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.home.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.home.impl.presentation.models.templates.TemplateUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.common.CompactCategoryChooser
import ru.aleshin.features.home.impl.presentation.ui.common.CompactSubCategoryChooser
import ru.aleshin.features.home.impl.presentation.ui.common.PriorityChooser
import java.util.Calendar

/**
 * @author Stanislav Aleshin on 04.06.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TemplateEditorDialog(
    modifier: Modifier = Modifier,
    categories: List<CategoriesUi>,
    model: TemplateUi?,
    onDismiss: () -> Unit,
    onConfirm: (TemplateUi) -> Unit,
) {
    val scrollState = rememberScrollState()
    var mainCategory by remember { mutableStateOf(model?.category ?: MainCategoryUi()) }
    var isEnableNotification by remember { mutableStateOf(model?.isEnableNotification ?: true) }
    var isConsiderInStatistics by remember { mutableStateOf(model?.isConsiderInStatistics ?: true) }
    var subCategory by remember { mutableStateOf(model?.subCategory) }

    val is24Format = DateFormat.is24HourFormat(LocalContext.current)

    val startHour = remember { model?.startTime?.hours ?: 0 }
    val startMinute = remember { model?.startTime?.minutes ?: 0 }
    var startFormat by remember { mutableStateOf(if (startHour > 11) TimeFormat.PM else TimeFormat.AM) }
    var editableStartHour by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        val formatHour = if (is24Format) startHour else startHour.mapHour24ToAmPm().second
        mutableStateOf(TextFieldValue(formatHour.toString().padStart(2, '0')))
    }
    var editableStartMinute by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(startMinute.toString().padStart(2, '0')))
    }

    val endHour = remember { model?.endTime?.hours ?: 0 }
    val endMinute = remember { model?.endTime?.minutes ?: 0 }
    var endFormat by remember { mutableStateOf(if (endHour > 11) TimeFormat.PM else TimeFormat.AM) }
    var editableEndHour by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        val formatHour = if (is24Format) endHour else endHour.mapHour24ToAmPm().second
        mutableStateOf(TextFieldValue(formatHour.toString().padStart(2, '0')))
    }
    var editableEndMinute by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(endMinute.toString().padStart(2, '0')))
    }

    var priority by remember { mutableStateOf(model?.priority ?: TaskPriority.STANDARD) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.width(328.dp).wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column {
                TemplateEditorDialogHeader()
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .height(400.dp)
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 0.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CompactCategoryChooser(
                        allCategories = categories,
                        selectedCategory = mainCategory,
                        onCategoryChange = { mainCategory = it; subCategory = null },
                    )
                    CompactSubCategoryChooser(
                        allCategories = categories,
                        selectedMainCategory = mainCategory,
                        selectedSubCategory = subCategory,
                        onSubCategoryChange = { subCategory = it },
                    )
                    TemplateEditorStartTimeChooser(
                        hour = editableStartHour,
                        minute = editableStartMinute,
                        is24Format = is24Format,
                        format = startFormat,
                        onHourChange = { editableStartHour = it },
                        onMinuteChange = { editableStartMinute = it },
                        onChangeFormat = { startFormat = it },
                    )
                    TemplateEditorEndTimeChooser(
                        hour = editableEndHour,
                        minute = editableEndMinute,
                        is24Format = is24Format,
                        format = endFormat,
                        onHourChange = { editableEndHour = it },
                        onMinuteChange = { editableEndMinute = it },
                        onChangeFormat = { endFormat = it },
                    )
                    TemplateEditorNotificationChooser(
                        isEnableNotification = isEnableNotification,
                        onNotificationEnabledChange = { isEnableNotification = it },
                    )
                    TemplateEditorStatisticsChooser(
                        isConsiderInStatistics = isConsiderInStatistics,
                        onStatisticsConsiderChange = { isConsiderInStatistics = it },
                    )
                    PriorityChooser(
                        priority = priority,
                        onPriorityChange = { priority = it },
                    )
                }

                DialogButtons(
                    enabledConfirm = mainCategory.id != 0,
                    confirmTitle = when (model != null) {
                        true -> TimePlannerRes.strings.okConfirmTitle
                        false -> HomeThemeRes.strings.dialogCreateTitle
                    },
                    onConfirmClick = {
                        val calendar = Calendar.getInstance()
                        val startTime = if (is24Format) {
                            calendar.setHoursAndMinutes(
                                hour = editableStartHour.text.toInt(),
                                minute = editableStartMinute.text.toInt()
                            ).time
                        } else {
                            calendar.setHoursAndMinutes(
                                hour = editableStartHour.text.toInt().mapHourAmPmTo24(startFormat),
                                minute = editableStartMinute.text.toInt()
                            ).time
                        }
                        val endTime = if (is24Format) {
                            calendar.setHoursAndMinutes(
                                hour = editableEndHour.text.toInt(),
                                minute = editableEndMinute.text.toInt()
                            ).time
                        } else {
                            calendar.setHoursAndMinutes(
                                hour = editableEndHour.text.toInt().mapHourAmPmTo24(endFormat),
                                minute = editableEndMinute.text.toInt()
                            ).time
                        }.let { endTime ->
                            if (endTime > startTime) endTime else endTime.shiftDay(1)
                        }
                        val template = TemplateUi(
                            templateId = model?.templateId ?: generateUniqueKey().toInt(),
                            startTime = startTime,
                            endTime = endTime,
                            category = mainCategory,
                            subCategory = subCategory,
                            isEnableNotification = isEnableNotification,
                            isConsiderInStatistics = isConsiderInStatistics,
                            priority = priority,
                            repeatEnabled = model?.repeatEnabled ?: false,
                            repeatTimes = model?.repeatTimes ?: emptyList(),
                        )
                        onConfirm(template)
                    },
                    onCancelClick = onDismiss,
                )
            }
        }
    }
}

@Composable
internal fun TemplateEditorStartTimeChooser(
    modifier: Modifier = Modifier,
    hour: TextFieldValue,
    minute: TextFieldValue,
    is24Format: Boolean,
    format: TimeFormat,
    onHourChange: (TextFieldValue) -> Unit,
    onMinuteChange: (TextFieldValue) -> Unit,
    onChangeFormat: (TimeFormat) -> Unit,
) {
    val minuteRequester = remember { FocusRequester() }
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
                value = hour,
                onValueChange = {
                    val onLimitAction = { char: Char ->
                        onMinuteChange(minute.endLimitCharTransition(char, 0..59, minuteRequester))
                    }
                    if (is24Format) {
                        onHourChange(hour.changeTwoDigitNumber(it, 0..23, onLimitAction))
                    } else {
                        onHourChange(hour.changeTwoDigitNumber(it, 0..12, onLimitAction))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = MaterialTheme.shapes.large,
                label = { Text(text = HomeThemeRes.strings.startTimeLabel) },
            )
            Text(
                text = ":",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                modifier = Modifier.weight(1f).align(Alignment.Bottom).focusRequester(minuteRequester),
                value = minute,
                onValueChange = {
                    onMinuteChange(minute.changeTwoDigitNumber(it, 0..59))
                },
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        TimeFormatSelector(
            modifier = Modifier.size(height = 60.dp, width = 45.dp).align(Alignment.Bottom),
            isVisible = !is24Format,
            format = format,
            onChangeFormat = { onChangeFormat(it) },
        )
    }
}

@Composable
internal fun TemplateEditorEndTimeChooser(
    modifier: Modifier = Modifier,
    hour: TextFieldValue,
    minute: TextFieldValue,
    is24Format: Boolean,
    format: TimeFormat,
    onHourChange: (TextFieldValue) -> Unit,
    onMinuteChange: (TextFieldValue) -> Unit,
    onChangeFormat: (TimeFormat) -> Unit,
) {
    val minuteRequester = remember { FocusRequester() }
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
                value = hour,
                onValueChange = {
                    val onLimitAction = { char: Char ->
                        onMinuteChange(minute.endLimitCharTransition(char, 0..59, minuteRequester))
                    }
                    if (is24Format) {
                        onHourChange(hour.changeTwoDigitNumber(it, 0..23, onLimitAction))
                    } else {
                        onHourChange(hour.changeTwoDigitNumber(it, 0..12, onLimitAction))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = MaterialTheme.shapes.large,
                label = { Text(text = HomeThemeRes.strings.endTimeLabel) },
            )
            Text(
                text = ":",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                modifier = Modifier.weight(1f).align(Alignment.Bottom).focusRequester(minuteRequester),
                value = minute,
                onValueChange = {
                    onMinuteChange(minute.changeTwoDigitNumber(it, 0..59))
                },
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        TimeFormatSelector(
            modifier = Modifier.size(height = 60.dp, width = 45.dp).align(Alignment.Bottom),
            isVisible = !is24Format,
            format = format,
            onChangeFormat = { onChangeFormat(it) },
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
internal fun TemplateEditorDialogHeader(
    modifier: Modifier = Modifier,
) {
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
