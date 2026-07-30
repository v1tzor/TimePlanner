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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.extensions.shiftDay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun HomeDateChooser(
    modifier: Modifier = Modifier,
    selectedDate: Date?,
    enabled: Boolean = true,
    onDateChange: (Date) -> Unit,
    onOpenCalendar: () -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("EEE, d MMM", Locale.getDefault()) }
    val dateTitle = remember(selectedDate) {
        selectedDate?.let(dateFormat::format).orEmpty()
    }

    DateChooser(
        modifier = modifier,
        dateTitle = dateTitle,
        enabled = enabled,
        onNext = {
            selectedDate?.let { date ->
                onDateChange(date.shiftDay(amount = 1))
            }
        },
        onPrevious = {
            selectedDate?.let { date ->
                onDateChange(date.shiftDay(amount = -1))
            }
        },
        onChooseDate = onOpenCalendar,
    )
}
