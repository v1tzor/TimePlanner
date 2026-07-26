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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun AnalyticsRegularityCalendar(
    range: AnalyticsRangeUi,
    activeDates: List<Date>,
    locale: Locale,
    strings: AnalyticsStrings,
    modifier: Modifier = Modifier,
    today: Date = Date(),
) {
    val firstMonth = remember(range.from, locale) { range.from.toMonthStart(locale) }
    val lastMonth = remember(range.to, locale) { range.to.toMonthStart(locale) }
    val anchorMonth = remember(range.anchorDate, firstMonth, lastMonth, locale) {
        range.anchorDate.toMonthStart(locale).coerceMonth(firstMonth, lastMonth, locale)
    }
    var displayedMonth by remember(range.from, range.to, range.anchorDate) { mutableStateOf(anchorMonth) }
    val monthTitle = remember(displayedMonth, locale) {
        SimpleDateFormat("LLLL yyyy", locale).apply { timeZone = CIVIL_TIME_ZONE }.format(displayedMonth).replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(locale) else char.toString()
        }
    }
    val cells = remember(displayedMonth, range, activeDates, today, locale) {
        buildMonthCells(displayedMonth, range, activeDates, today, locale)
    }
    val weekdayNames = remember(locale) { calendarWeekdayNames(locale) }
    val formatter = rememberAnalyticsValueFormatter()
    val monthActiveDayCount = cells.count { it.isActive }
    val monthRangeDayCount = cells.count { it.isInRange }
    val monthDayUnit = remember(monthRangeDayCount, locale, strings.dayUnit) {
        formatter.formatDayUnit(monthRangeDayCount, locale, strings.dayUnit)
    }
    val canShowPrevious = displayedMonth.after(firstMonth)
    val canShowNext = displayedMonth.before(lastMonth)
    val layoutDirection = LocalLayoutDirection.current
    val swipeThreshold = with(LocalDensity.current) { MONTH_SWIPE_THRESHOLD.toPx() }

    Column(
        modifier = modifier
            .pointerInput(displayedMonth, firstMonth, lastMonth, layoutDirection) {
                var dragAmount = 0f
                detectHorizontalDragGestures(
                    onDragStart = { dragAmount = 0f },
                    onDragEnd = {
                        val logicalDrag = if (layoutDirection == LayoutDirection.Ltr) dragAmount else -dragAmount
                        when {
                            logicalDrag > swipeThreshold && canShowPrevious -> {
                                displayedMonth = displayedMonth.shiftMonth(-1, locale)
                            }
                            logicalDrag < -swipeThreshold && canShowNext -> {
                                displayedMonth = displayedMonth.shiftMonth(1, locale)
                            }
                        }
                    },
                    onHorizontalDrag = { change, amount ->
                        change.consume()
                        dragAmount += amount
                    },
                )
            }
            .semantics(mergeDescendants = true) {
                customActions = buildList {
                    if (canShowPrevious) {
                        add(
                            CustomAccessibilityAction(strings.previousMonthDesc) {
                                displayedMonth = displayedMonth.shiftMonth(-1, locale)
                                true
                            },
                        )
                    }
                    if (canShowNext) {
                        add(
                            CustomAccessibilityAction(strings.nextMonthDesc) {
                                displayedMonth = displayedMonth.shiftMonth(1, locale)
                                true
                            },
                        )
                    }
                }
            },
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().heightIn(min = 20.dp),
            text = monthTitle,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            weekdayNames.forEach { name ->
                Text(
                    modifier = Modifier.weight(1f),
                    text = name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
        CalendarGrid(
            cells = cells,
            contentDescription = "$monthTitle, ${strings.activeDaysFormat.format(monthActiveDayCount, monthRangeDayCount, monthDayUnit)}",
        )
    }
}

@Composable
private fun CalendarGrid(
    cells: List<CalendarCell>,
    contentDescription: String,
) {
    val cellBackground = MaterialTheme.colorScheme.surfaceContainer
    val activeBackground = MaterialTheme.colorScheme.primaryContainer
    val inRangeText = MaterialTheme.colorScheme.onSurface
    val activeText = MaterialTheme.colorScheme.onPrimaryContainer
    val outsideText = MaterialTheme.colorScheme.onSurfaceVariant
    val todayOutline = MaterialTheme.colorScheme.primary
    Layout(
        modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {
            this.contentDescription = contentDescription
        },
        content = {
            cells.forEach { cell ->
                val background = when {
                    cell.isActive -> activeBackground
                    cell.isInRange -> cellBackground
                    else -> Color.Transparent
                }
                val textColor = when {
                    cell.isActive -> activeText
                    cell.isInRange -> inRangeText
                    else -> outsideText
                }
                Box(
                    modifier = Modifier
                        .size(MAX_CALENDAR_CELL)
                        .background(background, RoundedCornerShape(6.dp))
                        .then(
                            if (cell.isToday) {
                                Modifier.border(1.dp, todayOutline, RoundedCornerShape(6.dp))
                            } else {
                                Modifier
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = cell.dayNumber?.toString().orEmpty(),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
    ) { measurables, constraints ->
        val maxCellPx = MAX_CALENDAR_CELL.roundToPx()
        val maxGapPx = MAX_CALENDAR_GAP.roundToPx()
        val cellPx = minOf(maxCellPx, (constraints.maxWidth - maxGapPx * (DAYS_IN_WEEK - 1)) / DAYS_IN_WEEK)
            .coerceAtLeast(1)
        val gapPx = minOf(maxGapPx, (constraints.maxWidth - cellPx * DAYS_IN_WEEK) / (DAYS_IN_WEEK - 1))
            .coerceAtLeast(0)
        val placeables = measurables.map { measurable ->
            measurable.measure(androidx.compose.ui.unit.Constraints.fixed(cellPx, cellPx))
        }
        val height = cellPx * CALENDAR_ROWS + gapPx * (CALENDAR_ROWS - 1)
        layout(constraints.maxWidth, height) {
            placeables.forEachIndexed { index, placeable ->
                val row = index / DAYS_IN_WEEK
                val column = index % DAYS_IN_WEEK
                placeable.placeRelative(column * (cellPx + gapPx), row * (cellPx + gapPx))
            }
        }
    }
}

private fun buildMonthCells(
    displayedMonth: Date,
    range: AnalyticsRangeUi,
    activeDates: List<Date>,
    today: Date,
    locale: Locale,
): List<CalendarCell> {
    val month = displayedMonth.toCalendar(locale)
    val firstDayOffset = (month[Calendar.DAY_OF_WEEK] - month.firstDayOfWeek + DAYS_IN_WEEK) % DAYS_IN_WEEK
    val maxDay = month.getActualMaximum(Calendar.DAY_OF_MONTH)
    val rangeFrom = range.from.civilKey(locale)
    val rangeTo = range.to.civilKey(locale)
    val todayKey = today.localCivilKey(locale)
    val activeKeys = activeDates.mapTo(hashSetOf()) { it.civilKey(locale) }
    return List(DAYS_IN_WEEK * CALENDAR_ROWS) { index ->
        val day = index - firstDayOffset + 1
        if (day !in 1..maxDay) {
            CalendarCell(null, false, false, false)
        } else {
            val key = Calendar.getInstance(CIVIL_TIME_ZONE, locale).apply {
                clear()
                set(month[Calendar.YEAR], month[Calendar.MONTH], day)
            }.time.civilKey(locale)
            CalendarCell(
                dayNumber = day,
                isInRange = key in rangeFrom..rangeTo,
                isActive = key in activeKeys,
                isToday = key == todayKey,
            )
        }
    }
}

private fun Date.toMonthStart(locale: Locale): Date = toCalendar(locale).apply {
    set(Calendar.DAY_OF_MONTH, 1)
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.time

private fun Date.shiftMonth(value: Int, locale: Locale): Date = toCalendar(locale).apply {
    add(Calendar.MONTH, value)
}.time

private fun Date.coerceMonth(min: Date, max: Date, locale: Locale): Date {
    val currentKey = monthKey(locale)
    return when {
        currentKey < min.monthKey(locale) -> min
        currentKey > max.monthKey(locale) -> max
        else -> this
    }
}

private fun Date.monthKey(locale: Locale): Int = toCalendar(locale).let { calendar ->
    calendar[Calendar.YEAR] * 12 + calendar[Calendar.MONTH]
}

private fun Date.civilKey(locale: Locale): Int = toCalendar(locale).let { calendar ->
    calendar[Calendar.YEAR] * 1000 + calendar[Calendar.DAY_OF_YEAR]
}

private fun Date.toCalendar(locale: Locale): Calendar = Calendar.getInstance(CIVIL_TIME_ZONE, locale).apply { time = this@toCalendar }

private fun Date.localCivilKey(locale: Locale): Int = Calendar.getInstance(locale).apply { time = this@localCivilKey }.let { calendar ->
    calendar[Calendar.YEAR] * 1000 + calendar[Calendar.DAY_OF_YEAR]
}

private fun calendarWeekdayNames(locale: Locale): List<String> {
    val calendar = Calendar.getInstance(locale)
    return List(DAYS_IN_WEEK) { index ->
        val day = ((calendar.firstDayOfWeek - 1 + index) % DAYS_IN_WEEK) + 1
        SimpleDateFormat("EE", locale).format(
            Calendar.getInstance(locale).apply { set(Calendar.DAY_OF_WEEK, day) }.time,
        )
    }
}

private data class CalendarCell(
    val dayNumber: Int?,
    val isInRange: Boolean,
    val isActive: Boolean,
    val isToday: Boolean,
)

private val MAX_CALENDAR_CELL = 24.dp
private val MAX_CALENDAR_GAP = 2.dp
private val MONTH_SWIPE_THRESHOLD = 48.dp
private const val DAYS_IN_WEEK = 7
private const val CALENDAR_ROWS = 6
private val CIVIL_TIME_ZONE: TimeZone = TimeZone.getTimeZone("UTC")
