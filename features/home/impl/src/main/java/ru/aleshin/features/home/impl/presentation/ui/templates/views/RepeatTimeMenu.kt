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

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.mappers.mapToString
import ru.aleshin.core.ui.views.BackMenuItem
import ru.aleshin.core.ui.views.CheckedMenuItem
import ru.aleshin.core.ui.views.NavMenuItem
import ru.aleshin.core.utils.functional.Month
import ru.aleshin.core.utils.functional.WeekDay
import ru.aleshin.features.home.api.domain.entities.template.RepeatTime
import ru.aleshin.features.home.api.domain.entities.template.RepeatTimeType
import ru.aleshin.features.home.api.presentation.mappers.mapToString
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
@Composable
internal fun RepeatTimeMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    selectedTimes: List<RepeatTime>,
    onDismiss: () -> Unit,
    onAddRepeat: (RepeatTime) -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
) {
    var repeatCategory by remember {
        mutableStateOf(if (selectedTimes.isEmpty()) null else selectedTimes.first().repeatType)
    }
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 200.dp),
        offset = DpOffset(0.dp, 6.dp),
    ) {
        when (repeatCategory) {
            RepeatTimeType.WEEK_DAY -> WeekDayMenuItems(
                selectedTimes = selectedTimes,
                onBackClick = { repeatCategory = null },
                onAddRepeat = onAddRepeat,
                onDeleteRepeat = onDeleteRepeat,
            )
            RepeatTimeType.WEEK_DAY_IN_MONTH -> WeekDayInMonthMenuItems(
                selectedTimes = selectedTimes,
                onBackClick = { repeatCategory = null },
                onAddRepeat = onAddRepeat,
                onDeleteRepeat = onDeleteRepeat,
            )
            RepeatTimeType.MONTH_DAY -> MonthDayMenuItems(
                selectedTimes = selectedTimes,
                onBackClick = { repeatCategory = null },
                onAddRepeat = onAddRepeat,
                onDeleteRepeat = onDeleteRepeat,
            )
            RepeatTimeType.YEAR_DAY -> YearDayMenuItems(
                selectedTimes = selectedTimes,
                onBackClick = { repeatCategory = null },
                onAddRepeat = onAddRepeat,
                onDeleteRepeat = onDeleteRepeat,
            )
            null -> RepeatTimeType.values().forEach { type ->
                NavMenuItem(text = type.mapToString(), onClick = { repeatCategory = type })
            }
        }
    }
}

@Composable
internal fun WeekDayMenuItems(
    selectedTimes: List<RepeatTime>,
    onBackClick: () -> Unit,
    onAddRepeat: (RepeatTime) -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
) {
    BackMenuItem(
        enabled = selectedTimes.isEmpty(),
        onClick = onBackClick,
        title = HomeThemeRes.strings.navToBackTitle,
    )
    WeekDay.values().forEach { day ->
        CheckedMenuItem(
            text = day.mapToString(),
            check = selectedTimes.contains(RepeatTime.WeekDays(day)),
            onCheckedChange = {
                val repeat = RepeatTime.WeekDays(day)
                if (it) onAddRepeat(repeat) else onDeleteRepeat(repeat)
            },
        )
    }
}

@Composable
internal fun MonthDayMenuItems(
    selectedTimes: List<RepeatTime>,
    onBackClick: () -> Unit,
    onAddRepeat: (RepeatTime) -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
) {
    BackMenuItem(
        enabled = selectedTimes.isEmpty(),
        onClick = onBackClick,
        title = HomeThemeRes.strings.navToBackTitle,
    )
    for (dayNumber in 1..31) {
        CheckedMenuItem(
            text = dayNumber.toString(),
            check = selectedTimes.contains(RepeatTime.MonthDay(dayNumber)),
            onCheckedChange = {
                val repeat = RepeatTime.MonthDay(dayNumber)
                if (it) onAddRepeat(repeat) else onDeleteRepeat(repeat)
            },
        )
    }
}

@Composable
internal fun WeekDayInMonthMenuItems(
    selectedTimes: List<RepeatTime>,
    onBackClick: () -> Unit,
    onAddRepeat: (RepeatTime) -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
) {
    var selectedWeekNumber by remember { mutableStateOf<Int?>(null) }
    var isOpenSubMenu by remember { mutableStateOf(false) }

    if (isOpenSubMenu) {
        BackMenuItem(
            onClick = { isOpenSubMenu = false; selectedWeekNumber = null },
            title = HomeThemeRes.strings.navToBackTitle,
        )
        WeekDay.values().forEach { day ->
            CheckedMenuItem(
                text = day.mapToString(),
                check = selectedTimes.contains(RepeatTime.WeekDayInMonth(day, selectedWeekNumber!!)),
                onCheckedChange = {
                    val repeat = RepeatTime.WeekDayInMonth(day, selectedWeekNumber!!)
                    if (it) onAddRepeat(repeat) else onDeleteRepeat(repeat)
                },
            )
        }
    } else {
        BackMenuItem(
            enabled = selectedTimes.isEmpty(),
            onClick = onBackClick,
            title = HomeThemeRes.strings.navToBackTitle,
        )
        for (weekNumber in 1..5) {
            NavMenuItem(
                text = weekNumber.toString(),
                onClick = {
                    selectedWeekNumber = weekNumber
                    isOpenSubMenu = true
                },
            )
        }
    }
}

@Composable
internal fun YearDayMenuItems(
    selectedTimes: List<RepeatTime>,
    onBackClick: () -> Unit,
    onAddRepeat: (RepeatTime) -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
) {
    var selectedMonth by remember { mutableStateOf<Month?>(null) }
    var isOpenSubMenu by remember { mutableStateOf(false) }

    if (isOpenSubMenu) {
        BackMenuItem(
            onClick = { isOpenSubMenu = false; selectedMonth = null },
            title = HomeThemeRes.strings.navToBackTitle,
        )
        for (day in 1..31) {
            CheckedMenuItem(
                text = day.toString(),
                check = selectedTimes.contains(RepeatTime.YearDay(month = selectedMonth!!, dayNumber = day)),
                onCheckedChange = {
                    val repeat = RepeatTime.YearDay(month = selectedMonth!!, dayNumber = day)
                    if (it) onAddRepeat(repeat) else onDeleteRepeat(repeat)
                },
            )
        }
    } else {
        BackMenuItem(
            enabled = selectedTimes.isEmpty(),
            onClick = onBackClick,
            title = HomeThemeRes.strings.navToBackTitle,
        )
        Month.values().forEach { month ->
            NavMenuItem(
                text = month.mapToString(),
                onClick = {
                    selectedMonth = month
                    isOpenSubMenu = true
                },
            )
        }
    }
}
