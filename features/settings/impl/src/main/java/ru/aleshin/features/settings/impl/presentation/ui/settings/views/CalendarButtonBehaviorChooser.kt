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
package ru.aleshin.features.settings.impl.presentation.ui.settings.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.CalendarButtonBehavior
import ru.aleshin.core.ui.views.CalendarButtonBehavior.*
import ru.aleshin.core.ui.views.SegmentedButtonItem
import ru.aleshin.core.ui.views.SegmentedButtons
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes

/**
 * @author Stanislav Aleshin on 23.10.2023.
 */
@Composable
internal fun CalendarButtonBehaviorChooser(
    modifier: Modifier = Modifier,
    calendarButtonBehavior: CalendarButtonBehavior,
    onUpdateCalendarBehavior: (CalendarButtonBehavior) -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = TimePlannerRes.elevations.levelTwo,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = SettingsThemeRes.strings.calendarButtonBehaviorTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            SegmentedButtons(
                modifier = Modifier.fillMaxWidth(),
                items = CalendarButtonBehaviorSegmentedItems.values(),
                selectedItem = calendarButtonBehavior.toSegmentedItem(),
                onItemClick = { onUpdateCalendarBehavior.invoke(it.toCalendarButtonBehavior()) },
            )
        }
    }
}

internal enum class CalendarButtonBehaviorSegmentedItems : SegmentedButtonItem {
    OPEN_CALENDAR {
        override val title: String @Composable get() = SettingsThemeRes.strings.selectDayCalendarBehavior
    },
    SET_CURRENT_DATE {
        override val title: String @Composable get() = SettingsThemeRes.strings.currentDayCalendarBehavior
    },
}

internal fun CalendarButtonBehavior.toSegmentedItem() = when (this) {
    OPEN_CALENDAR -> CalendarButtonBehaviorSegmentedItems.OPEN_CALENDAR
    SET_CURRENT_DATE -> CalendarButtonBehaviorSegmentedItems.SET_CURRENT_DATE
}

internal fun CalendarButtonBehaviorSegmentedItems.toCalendarButtonBehavior() = when (this) {
    CalendarButtonBehaviorSegmentedItems.OPEN_CALENDAR -> OPEN_CALENDAR
    CalendarButtonBehaviorSegmentedItems.SET_CURRENT_DATE -> SET_CURRENT_DATE
}
