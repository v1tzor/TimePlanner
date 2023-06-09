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
package ru.aleshin.core.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.utils.extensions.hoursToMillis
import ru.aleshin.core.utils.extensions.minutesToMillis
import ru.aleshin.core.utils.extensions.toHorses
import ru.aleshin.core.utils.extensions.toMinutesInHours
import ru.aleshin.core.utils.extensions.toStringOrEmpty
import ru.aleshin.core.utils.functional.Constants
import java.util.Calendar
import java.util.Date

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DurationPickerDialog(
    modifier: Modifier = Modifier,
    headerTitle: String,
    startTime: Date,
    duration: Long,
    onDismissRequest: () -> Unit,
    onSelectedTime: (Long) -> Unit,
) {
    val startTimeCalendar = Calendar.getInstance().apply { time = startTime }
    val maxHours =
        Constants.Date.HOURS_IN_DAY.toInt() - startTimeCalendar.get(Calendar.HOUR_OF_DAY) - 1
    val maxMinutes =
        Constants.Date.MINUTES_IN_HOUR.toInt() - startTimeCalendar.get(Calendar.MINUTE) - 1

    var hours by rememberSaveable { mutableStateOf<Int?>(duration.toHorses().toInt()) }
    var minutes by rememberSaveable { mutableStateOf<Int?>(duration.toMinutesInHours().toInt()) }

    AlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.width(243.dp),
            tonalElevation = TimePlannerRes.elevations.levelThree,
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.End,
            ) {
                TimePickerHeader(title = headerTitle)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DurationPickerHourMinuteSelector(
                        hours = hours.toStringOrEmpty(),
                        minutes = minutes.toStringOrEmpty(),
                        isEnableSupportText = true,
                        onMinutesChanges = { value ->
                            if (value.isEmpty()) {
                                hours = null
                            } else if (value.toIntOrNull() != null && value.length <= 2) {
                                hours = value.toIntOrNull()
                            }
                        },
                        onHoursChanges = { value ->
                            if (value.isEmpty()) {
                                minutes = null
                            } else if (value.toIntOrNull() != null && value.length <= 2) {
                                minutes = value.toIntOrNull()
                            }
                        },
                    )
                    LazyRow(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(DurationTemplate.values()) {
                            AssistChip(
                                onClick = {
                                    hours = it.hours
                                    minutes = it.minutes
                                },
                                label = {
                                    val millis =
                                        it.hours.hoursToMillis() + it.minutes.minutesToMillis()
                                    Text(text = millis.toMinutesOrHoursTitle())
                                },
                                border = AssistChipDefaults.assistChipBorder(
                                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                                ),
                            )
                        }
                    }
                }
                TimePickerActions(
                    enabledConfirm = hours != null && minutes != null && (hours!! * 60 + minutes!!) <= (maxHours * 60 + maxMinutes),
                    onDismissClick = onDismissRequest,
                    onCurrentTimeChoose = {
                        hours = maxHours
                        minutes = maxMinutes
                    },
                    onConfirmClick = {
                        val hoursInMillis = checkNotNull(hours).hoursToMillis()
                        val time = hoursInMillis + checkNotNull(minutes).minutesToMillis()
                        onSelectedTime.invoke(time)
                    },
                )
            }
        }
    }
}

@Composable
internal fun DurationPickerHourMinuteSelector(
    modifier: Modifier = Modifier,
    hours: String,
    minutes: String,
    isEnableSupportText: Boolean = false,
    onMinutesChanges: (String) -> Unit,
    onHoursChanges: (String) -> Unit,
) = Row(
    modifier = modifier.padding(horizontal = 24.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    val requester = remember { FocusRequester() }
    OutlinedTextField(
        modifier = Modifier.weight(1f),
        value = hours,
        textStyle = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center),
        onValueChange = { value ->
            onMinutesChanges(value)
            if (value.length == 2 && value.toIntOrNull() in 0..23) requester.requestFocus()
        },
        shape = MaterialTheme.shapes.small,
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
        value = minutes,
        textStyle = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center),
        onValueChange = onHoursChanges,
        shape = MaterialTheme.shapes.small,
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
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    )
}
