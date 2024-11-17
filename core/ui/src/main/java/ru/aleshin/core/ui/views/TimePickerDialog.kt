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
package ru.aleshin.core.ui.views

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults.ContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.utils.functional.TimeFormat
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 03.03.2023.
 */
@Composable
fun BasicTimePickerDialog(
    modifier: Modifier = Modifier,
    title: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    onCurrentTimeChoose: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column(
                modifier = Modifier.padding(top = 24.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                Box(modifier = Modifier.padding(horizontal = 24.dp)) { content() }
                TimePickerActions(
                    modifier = Modifier.fillMaxWidth(),
                    paddingValues = PaddingValues(start = 16.dp, end = 8.dp),
                    onDismissClick = onCancel,
                    toggle = toggle,
                    onCurrentTimeChoose = onCurrentTimeChoose,
                    onConfirmClick = onConfirm
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MultiTimePickerDialog(
    headerTitle: String,
    initTime: Date,
    onDismissRequest: () -> Unit,
    onSelectedTime: (Date) -> Unit,
) {
    val calendar = remember { Calendar.getInstance().apply { time = initTime } }
    val hour = remember { calendar.get(Calendar.HOUR_OF_DAY) }
    val minute = remember { calendar.get(Calendar.MINUTE) }
    val state = rememberTimePickerState(initialHour = hour, initialMinute = minute)
    var showingPicker by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current

    BasicTimePickerDialog(
        title = headerTitle,
        onCancel = onDismissRequest,
        onConfirm = {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, state.hour)
            cal.set(Calendar.MINUTE, state.minute)
            cal.set(Calendar.SECOND, 0)
            onSelectedTime(cal.time)
        },
        onCurrentTimeChoose = {
            val currentTime = Calendar.getInstance().apply { add(Calendar.MINUTE, 1) }
            state.hour = currentTime.get(Calendar.HOUR_OF_DAY)
            state.minute = currentTime.get(Calendar.MINUTE)
        },
        toggle = {
            if (configuration.screenHeightDp > 400) {
                IconButton(
                    modifier = Modifier.size(36.dp),
                    onClick = { showingPicker = !showingPicker }
                ) {
                    Icon(
                        painter = if (showingPicker) {
                            painterResource(TimePlannerRes.icons.keyboard)
                        } else {
                            painterResource(TimePlannerRes.icons.schedulerIcon)
                        },
                        contentDescription = null,
                    )
                }
            }
        }
    ) {
        if (showingPicker && configuration.screenHeightDp > 400) {
            TimePicker(state = state)
        } else {
            TimeInput(state = state)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@Deprecated("Use Material 3 Time Picker")
fun TimeInputPickerDialog(
    modifier: Modifier = Modifier,
    headerTitle: String,
    initTime: Date,
    onDismissRequest: () -> Unit,
    onSelectedTime: (Date) -> Unit,
) {
    val calendar = remember { Calendar.getInstance().apply { time = initTime } }
    val is24Format = DateFormat.is24HourFormat(LocalContext.current)
    val hour = remember { calendar.get(Calendar.HOUR_OF_DAY) }
    val minute = remember { calendar.get(Calendar.MINUTE) }
    var format by remember { mutableStateOf(if (hour > 11) TimeFormat.PM else TimeFormat.AM) }
    var editableHour by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        val formatHour = if (is24Format) hour else hour.mapHour24ToAmPm().second
        mutableStateOf(TextFieldValue(formatHour.toString().padStart(2, '0')))
    }
    var editableMinute by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(minute.toString().padStart(2, '0')))
    }

    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.width(if (is24Format) 243.dp else 348.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.End,
            ) {
                TimePickerHeader(title = headerTitle)
                TimePickerHourMinuteSelector(
                    hour = editableHour,
                    minute = editableMinute,
                    format = format,
                    is24Format = is24Format,
                    onHourChanges = { value -> editableHour = value },
                    onMinuteChanges = { value -> editableMinute = value },
                    onChangeFormat = { format = it },
                )
                TimePickerActions(
                    onDismissClick = onDismissRequest,
                    onCurrentTimeChoose = {
                        val currentTime = Calendar.getInstance().apply { add(Calendar.MINUTE, 1) }
                        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
                        val currentMinute = currentTime.get(Calendar.MINUTE)
                        if (is24Format) {
                            editableHour = TextFieldValue(currentHour.toString().padStart(2, '0'))
                            editableMinute = TextFieldValue(currentMinute.toString().padStart(2, '0'))
                        } else {
                            val amPmHour = currentHour.mapHour24ToAmPm()
                            format = amPmHour.first
                            editableHour = TextFieldValue(amPmHour.second.toString().padStart(2, '0'))
                            editableMinute = TextFieldValue(currentMinute.toString().padStart(2, '0'))
                        }
                    },
                    onConfirmClick = {
                        val time = calendar.apply {
                            if (is24Format) {
                                set(Calendar.HOUR_OF_DAY, editableHour.text.toInt())
                                set(Calendar.MINUTE, editableMinute.text.toInt())
                            } else {
                                set(Calendar.HOUR_OF_DAY, editableHour.text.toInt().mapHourAmPmTo24(format))
                                set(Calendar.MINUTE, editableMinute.text.toInt())
                            }
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onSelectedTime.invoke(time.time)
                    },
                )
            }
        }
    }
}

@Composable
internal fun TimePickerHeader(
    modifier: Modifier = Modifier,
    title: String,
) = Box(
    modifier = modifier
        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
        .fillMaxWidth(),
) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
internal fun TimePickerHourMinuteSelector(
    modifier: Modifier = Modifier,
    hour: TextFieldValue,
    minute: TextFieldValue,
    isEnableSupportText: Boolean = false,
    is24Format: Boolean,
    format: TimeFormat,
    onHourChanges: (TextFieldValue) -> Unit,
    onMinuteChanges: (TextFieldValue) -> Unit,
    onChangeFormat: (TimeFormat) -> Unit,
) = Row(
    modifier = modifier.padding(horizontal = 24.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    var isRequestedFirstFocus by rememberSaveable { mutableStateOf(false) }
    val hourRequester = remember { FocusRequester() }
    val minuteRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = Modifier
            .weight(1f)
            .focusRequester(hourRequester),
        value = hour,
        onValueChange = {
            val onLimitAction = { char: Char ->
                onMinuteChanges(minute.endLimitCharTransition(char, 0..59, minuteRequester))
            }
            if (is24Format) {
                onHourChanges(hour.changeTwoDigitNumber(it, 0..23, onLimitAction))
            } else {
                onHourChanges(hour.changeTwoDigitNumber(it, 0..12, onLimitAction))
            }
        },
        textStyle = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center),
        shape = MaterialTheme.shapes.large,
        supportingText = if (isEnableSupportText) {
            {
                Text(TimePlannerRes.strings.hoursTitle)
            }
        } else {
            null
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
    )
    Text(
        modifier = Modifier.width(24.dp),
        text = TimePlannerRes.strings.separator,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
    OutlinedTextField(
        modifier = Modifier
            .weight(1f)
            .focusRequester(minuteRequester),
        value = minute,
        onValueChange = {
            onMinuteChanges(minute.changeTwoDigitNumber(it, 0..59))
        },
        textStyle = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center),
        shape = MaterialTheme.shapes.large,
        supportingText = if (isEnableSupportText) {
            {
                Text(TimePlannerRes.strings.minutesTitle)
            }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
    )
    TimeFormatSelector(
        modifier = Modifier
            .size(height = 80.dp, width = 52.dp)
            .offset(x = 12.dp),
        isVisible = !is24Format,
        format = format,
        onChangeFormat = onChangeFormat,
    )
    LaunchedEffect(Unit) {
        if (!isRequestedFirstFocus) {
            hourRequester.requestFocus()
            isRequestedFirstFocus = true
        }
    }
}

@Composable
internal fun TimePickerActions(
    modifier: Modifier = Modifier,
    enabledConfirm: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(bottom = 20.dp, start = 16.dp, end = 24.dp),
    toggle: @Composable () -> Unit = {},
    onDismissClick: () -> Unit,
    onCurrentTimeChoose: () -> Unit,
    onConfirmClick: () -> Unit,
) = Row(
    modifier = modifier.padding(paddingValues),
    verticalAlignment = Alignment.CenterVertically,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        toggle()
        IconButton(
            modifier = Modifier.size(36.dp),
            onClick = onCurrentTimeChoose
        ) {
            Icon(
                painter = painterResource(TimePlannerRes.icons.time),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
    Spacer(modifier = Modifier.weight(1f))
    TextButton(
        onClick = onDismissClick,
        contentPadding = PaddingValues(
            start = 6.dp,
            top = ContentPadding.calculateTopPadding(),
            end = 6.dp,
            bottom = ContentPadding.calculateBottomPadding()
        ),
    ) {
        Text(text = TimePlannerRes.strings.cancelTitle)
    }
    TextButton(enabled = enabledConfirm, onClick = onConfirmClick) {
        Text(text = TimePlannerRes.strings.okConfirmTitle)
    }
}

fun Int.mapHourAmPmTo24(format: TimeFormat): Int {
    return when (format) {
        TimeFormat.PM -> if (this != 12) this + 12 else 12
        TimeFormat.AM -> if (this != 12) this else 0
    }
}

fun Int.mapHour24ToAmPm(): Pair<TimeFormat, Int> {
    return Pair(
        first = if (this in 0..11) TimeFormat.AM else TimeFormat.PM,
        second = when (this@mapHour24ToAmPm) {
            0 -> 12
            in 1..12 -> this@mapHour24ToAmPm
            else -> this@mapHour24ToAmPm - 12
        },
    )
}

fun TextFieldValue.changeTwoDigitNumber(
    newValue: TextFieldValue,
    restrict: IntRange = Int.MIN_VALUE..Int.MAX_VALUE,
    onLimit: ((Char) -> Unit)? = null,
): TextFieldValue {
    val cursor = selection.min
    val oldText = text
    val newText = newValue.text.filter { it.isDigit() }.take(2)
    val finalText = if (newText.length < oldText.length) {
        if (cursor == 2) {
            newText + "0"
        } else {
            newText.padStart(2, '0')
        }
    } else {
        if (cursor == 0) {
            newText.first().toString() + oldText.last().toString()
        } else {
            newText.padStart(2, '0')
        }
    }
    val newCursor = minOf(newValue.selection.start, finalText.length)
    if (cursor == 2 && newCursor == 2 && newText == finalText) {
        onLimit?.invoke(newValue.text.last())
    }
    return if (finalText.toInt() in restrict) {
        TextFieldValue(text = finalText, selection = TextRange(newCursor))
    } else {
        this@changeTwoDigitNumber
    }
}

fun TextFieldValue.endLimitCharTransition(
    char: Char,
    restrict: IntRange = Int.MIN_VALUE..Int.MAX_VALUE,
    requester: FocusRequester,
): TextFieldValue {
    requester.requestFocus()
    val newText = char.toString() + (text.lastOrNull()?.toString() ?: "")
    return if (newText.toIntOrNull() in restrict) {
        TextFieldValue(
            text = newText,
            selection = TextRange(1),
        )
    } else {
        TextFieldValue(
            text = text,
            selection = TextRange(0),
        )
    }
}