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
package ru.aleshin.features.analytics.impl.presentation.ui.common.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AnalyticsDateRangeDialog(
    initialFrom: Long,
    initialTo: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long, Long) -> Unit,
) {
    val pickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialFrom,
        initialSelectedEndDateMillis = initialTo,
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = pickerState.selectedStartDateMillis != null,
                onClick = {
                    val from = pickerState.selectedStartDateMillis ?: return@TextButton
                    onConfirm(from, pickerState.selectedEndDateMillis ?: from)
                },
            ) {
                Text(text = AnalyticsThemeRes.strings.confirm)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = AnalyticsThemeRes.strings.cancel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
    ) {
        DateRangePicker(
            state = pickerState,
            title = null,
            headline = {
                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = AnalyticsThemeRes.strings.selectDateRange,
                )
            }
        )
    }
}
