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
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.utils.functional.TimeFormat
import java.util.*

/**
 * @author Stanislav Aleshin on 03.03.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerDialog(
    modifier: Modifier = Modifier,
    headerTitle: String,
    initTime: Date,
    onDismissRequest: () -> Unit,
    onSelectedTime: (Date) -> Unit,
) {
    val calendar = Calendar.getInstance().apply { time = initTime }
    val is24Format = DateFormat.is24HourFormat(LocalContext.current)
    var hours by rememberSaveable { mutableStateOf<Int?>(calendar.get(Calendar.HOUR_OF_DAY)) }
    var minutes by rememberSaveable { mutableStateOf<Int?>(calendar.get(Calendar.MINUTE)) }
    var format by remember {
        mutableStateOf(if (hours != null && hours!! > 11) TimeFormat.PM else TimeFormat.AM)
    }

    AlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.width(if (is24Format) 243.dp else 348.dp),
            tonalElevation = TimePlannerRes.elevations.levelThree,
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.End,
            ) {
                TimePickerHeader(title = headerTitle)
                TimePickerHourMinuteSelector(
                    hours = hours,
                    minutes = minutes,
                    format = format,
                    is24Format = is24Format,
                    onHoursChanges = { value -> hours = value },
                    onMinutesChanges = { value -> minutes = value },
                    onChangeFormat = {
                        hours = if (format == TimeFormat.PM) hours?.minus(12) else hours?.plus(12)
                        format = it
                    },
                )
                TimePickerActions(
                    enabledConfirm = minutes in 0..59 && hours in 0..23,
                    onDismissClick = onDismissRequest,
                    onCurrentTimeChoose = {
                        val currentTime = Calendar.getInstance()
                        currentTime.add(Calendar.MINUTE, 1)
                        hours = currentTime.get(Calendar.HOUR_OF_DAY)
                        minutes = currentTime.get(Calendar.MINUTE)
                        if (!is24Format) {
                            format = if (hours in 0..11) TimeFormat.AM else TimeFormat.PM
                        }
                    },
                    onConfirmClick = {
                        val time = calendar.apply {
                            set(Calendar.HOUR_OF_DAY, checkNotNull(hours))
                            set(Calendar.MINUTE, checkNotNull(minutes))
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.time
                        onSelectedTime.invoke(time)
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
    modifier = modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp).fillMaxWidth(),
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
    hours: Int?,
    minutes: Int?,
    isEnableSupportText: Boolean = false,
    is24Format: Boolean,
    format: TimeFormat,
    onHoursChanges: (Int?) -> Unit,
    onMinutesChanges: (Int?) -> Unit,
    onChangeFormat: (TimeFormat) -> Unit,
) = Row(
    modifier = modifier.padding(horizontal = 24.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    val requester = remember { FocusRequester() }
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
                onHoursChanges(time)
            } else if (time != null && !is24Format && time in 1..12) {
                val formatTime = when (format) {
                    TimeFormat.PM -> if (time != 12) time + 12 else 12
                    TimeFormat.AM -> if (time != 12) time else 0
                }
                onHoursChanges(formatTime)
            } else if (it.isBlank()) {
                onHoursChanges(null)
            }
        },
        textStyle = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center),
        shape = MaterialTheme.shapes.small,
        supportingText = if (isEnableSupportText) { {
            Text(TimePlannerRes.strings.hoursTitle)
        } } else {
            null
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
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
        modifier = Modifier.weight(1f).focusRequester(requester),
        value = minutes?.toString() ?: "",
        onValueChange = {
            val time = it.toIntOrNull()
            if (time != null && time in 0..59) {
                onMinutesChanges(time)
            } else if (it.isBlank()) {
                onMinutesChanges(null)
            }
        },
        textStyle = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center),
        shape = MaterialTheme.shapes.small,
        supportingText = if (isEnableSupportText) { {
            Text(TimePlannerRes.strings.minutesTitle)
        } } else {
            null
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    )
    TimeFormatSelector(
        modifier = Modifier.size(height = 80.dp, width = 52.dp).offset(x = 12.dp),
        isVisible = !is24Format,
        format = format,
        onChangeFormat = onChangeFormat,
    )
}

@Composable
internal fun TimePickerActions(
    modifier: Modifier = Modifier,
    enabledConfirm: Boolean = true,
    onDismissClick: () -> Unit,
    onCurrentTimeChoose: () -> Unit,
    onConfirmClick: () -> Unit,
) = Row(
    modifier = modifier.padding(bottom = 20.dp, start = 16.dp, end = 24.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    IconButton(onClick = onCurrentTimeChoose) {
        Icon(
            painter = painterResource(TimePlannerRes.icons.time),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
    Spacer(modifier = Modifier.weight(1f))
    TextButton(onClick = onDismissClick) {
        Text(text = TimePlannerRes.strings.alertDialogDismissTitle)
    }
    TextButton(enabled = enabledConfirm, onClick = onConfirmClick) {
        Text(text = TimePlannerRes.strings.alertDialogSelectConfirmTitle)
    }
}
