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
package ru.aleshin.features.home.impl.presentation.ui.overview.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author Stanislav Aleshin on 04.11.2023.
 */
@Composable
internal fun DeadlineChooser(
    modifier: Modifier = Modifier,
    deadline: Date?,
    onChooseDeadline: (Date?) -> Unit,
) {
    var openDateChooserDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.duration),
            contentDescription = HomeThemeRes.strings.subCategoryLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = HomeThemeRes.strings.deadlineLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (deadline != null) {
                DeadlineDateView(
                    deadline = deadline,
                    onClick = { openDateChooserDialog = true },
                )
                IconButton(modifier = Modifier.size(32.dp), onClick = { onChooseDeadline(null) }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {
                SuggestionChip(
                    label = { Text(text = HomeThemeRes.strings.specifyDeadlineTitle) },
                    onClick = { openDateChooserDialog = true },
                )
            }
        }
    }
    DeadlineDatePicker(
        isOpenDialog = openDateChooserDialog,
        onDismiss = { openDateChooserDialog = false },
        onSelectedDate = {
            onChooseDeadline(it)
            openDateChooserDialog = false
        },
    )
}

@Composable
internal fun DeadlineDateView(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    deadline: Date,
    onClick: () -> Unit,
) {
    val dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM)

    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            text = dateFormat.format(deadline),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun DeadlineDatePicker(
    modifier: Modifier = Modifier,
    isOpenDialog: Boolean,
    onDismiss: () -> Unit,
    onSelectedDate: (Date) -> Unit,
) {
    if (isOpenDialog) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled by remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        val dateMillis = datePickerState.selectedDateMillis
                        val date = dateMillis?.mapToDate() ?: return@TextButton
                        onSelectedDate.invoke(date.endThisDay())
                    },
                    enabled = confirmEnabled,
                ) {
                    Text(text = TimePlannerRes.strings.alertDialogSelectConfirmTitle)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = TimePlannerRes.strings.alertDialogDismissTitle)
                }
            },
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        modifier = Modifier.padding(start = 24.dp, top = 24.dp),
                        text = HomeThemeRes.strings.dateDialogPickerTitle,
                    )
                },
                headline = {
                    Text(
                        modifier = Modifier.padding(start = 24.dp),
                        text = HomeThemeRes.strings.dateDialogPickerHeadline,
                    )
                },
            )
        }
    }
}
