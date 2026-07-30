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
package ru.aleshin.features.home.impl.presentation.ui.home.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import java.util.Date

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun HomeDatePicker(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onDateSelect: (Date) -> Unit,
) {
    val datePickerState = rememberDatePickerState()
    val confirmEnabled by remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = confirmEnabled,
                onClick = {
                    val dateMillis = datePickerState.selectedDateMillis
                    val date = dateMillis?.mapToDate() ?: return@TextButton
                    onDateSelect(date.startThisDay())
                },
            ) {
                Text(text = TimePlannerRes.strings.confirmTitle)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = TimePlannerRes.strings.cancelTitle)
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
