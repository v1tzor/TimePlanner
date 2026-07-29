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
package ru.aleshin.features.editor.impl.presentation.ui.goal.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalEditUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.goal.validators.GoalValidationError
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
@Composable
internal fun GoalDeadlineSection(
    modifier: Modifier = Modifier,
    goal: GoalEditUi,
    errors: Set<GoalValidationError>,
    onDeadlineChange: (Date) -> Unit,
) {
    val strings = EditorThemeRes.goalStrings
    val dateFormat = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    val isError = GoalValidationError.DEADLINE in errors
    var isDatePickerOpen by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 68.dp),
            onClick = { isDatePickerOpen = true },
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = if (isError) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            } else {
                null
            },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(EditorThemeRes.icons.deadline),
                    contentDescription = null,
                    tint = if (isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.deadlineTitle,
                        color = if (isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = dateFormat.format(goal.deadline),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Icon(
                    painter = painterResource(EditorThemeRes.icons.showDialog),
                    contentDescription = strings.deadlineTitle,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        if (isError) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = strings.deadlineError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
    GoalDeadlinePicker(
        isOpen = isDatePickerOpen,
        initialDate = goal.deadline,
        earliestDate = goal.createdAt,
        onDismiss = { isDatePickerOpen = false },
        onConfirm = { date ->
            isDatePickerOpen = false
            onDeadlineChange(date)
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GoalDeadlinePicker(
    isOpen: Boolean,
    initialDate: Date,
    earliestDate: Date,
    onDismiss: () -> Unit,
    onConfirm: (Date) -> Unit,
) {
    if (isOpen) {
        val earliestDateMillis = remember(earliestDate) {
            earliestDate.startThisDay().toGoalDatePickerMillis()
        }
        val selectableDates = remember(earliestDateMillis) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= earliestDateMillis
                }
            }
        }
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDate.toGoalDatePickerMillis(),
            selectableDates = selectableDates,
        )
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = pickerState.selectedDateMillis ?: return@TextButton
                        onConfirm(selectedDate.toLocalGoalDate())
                    },
                ) {
                    Text(TimePlannerRes.strings.confirmTitle)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(TimePlannerRes.strings.cancelTitle)
                }
            },
        ) {
            DatePicker(
                state = pickerState,
                title = null,
            )
        }
    }
}

private fun Date.toGoalDatePickerMillis(): Long {
    val localCalendar = Calendar.getInstance().apply { time = this@toGoalDatePickerMillis }
    return Calendar.getInstance(DATE_PICKER_TIME_ZONE).apply {
        clear()
        set(
            localCalendar[Calendar.YEAR],
            localCalendar[Calendar.MONTH],
            localCalendar[Calendar.DAY_OF_MONTH],
        )
    }.timeInMillis
}

private fun Long.toLocalGoalDate(): Date {
    val pickerCalendar = Calendar.getInstance(DATE_PICKER_TIME_ZONE).apply {
        timeInMillis = this@toLocalGoalDate
    }
    return Calendar.getInstance().apply {
        clear()
        set(
            pickerCalendar[Calendar.YEAR],
            pickerCalendar[Calendar.MONTH],
            pickerCalendar[Calendar.DAY_OF_MONTH],
        )
    }.time.startThisDay()
}

private val DATE_PICKER_TIME_ZONE: TimeZone = TimeZone.getTimeZone("UTC")
