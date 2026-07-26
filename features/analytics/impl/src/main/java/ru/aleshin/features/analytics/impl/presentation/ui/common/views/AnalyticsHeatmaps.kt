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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.domain.entities.CategoryDayPart
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsWeekdayHourCellUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsWeekdayHourLoadUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryDayPartCellUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryDayPartSummaryUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.fetchAnalyticsChartColors
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.fetchCategoryHeatmapLevels
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun AnalyticsWeekdayHourHeatmap(
    modifier: Modifier = Modifier,
    weekdayHourLoad: AnalyticsWeekdayHourLoadUi,
) {
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }

    val levels = MaterialTheme.colorScheme.fetchAnalyticsChartColors().heatmapLevels
    val rows = weekdayHourLoad.rows
    val headerCells = remember(rows) { rows.firstOrNull()?.cells.orEmpty() }
    var selectedCell by remember(weekdayHourLoad) { mutableStateOf<AnalyticsWeekdayHourCellUi?>(null) }
    if (headerCells.isEmpty()) return

    Column(modifier) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Spacer(Modifier.width(DAY_LABEL_WIDTH + DAY_LABEL_GAP))
            AnalyticsHeatmapHourLabels(
                modifier = Modifier.weight(1f),
                cells = headerCells,
            )
        }
        Spacer(Modifier.height(8.dp))
        rows.forEachIndexed { index, row ->
            val dayOfWeek = row.dayOfWeek
            val dayCells = row.cells
            val busiestCell = row.busiestCellIndex?.let(dayCells::getOrNull)
            val dayTitle = remember(dayOfWeek, locale) { dayName(dayOfWeek, locale, "EE") }
            val fullDayTitle = remember(dayOfWeek, locale) { dayName(dayOfWeek, locale, "EEEE") }
            val rowDescription = if (busiestCell != null) {
                strings.heatmapRowDescFormat.format(
                    fullDayTitle,
                    busiestCell.fromHour,
                    busiestCell.toHour,
                    busiestCell.averageMinutes.roundToInt(),
                )
            } else {
                "$fullDayTitle: ${strings.sectionNoData}"
            }
            Row(
                modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) {
                    contentDescription = rowDescription
                    if (busiestCell != null) {
                        customActions = listOf(
                            CustomAccessibilityAction(strings.heatmapDetailsAction) {
                                selectedCell = busiestCell
                                true
                            },
                        )
                    }
                },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.width(DAY_LABEL_WIDTH),
                    text = dayTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
                Spacer(Modifier.width(DAY_LABEL_GAP))
                HeatmapRow(
                    modifier = Modifier.weight(1f),
                    levels = levels,
                    itemCount = dayCells.size,
                    itemLevels = remember(dayCells) { dayCells.map { it.level } },
                    maxCellWidth = HOUR_CELL_MAX_WIDTH,
                    cellHeight = HOUR_CELL_HEIGHT,
                    maxGap = HOUR_CELL_MAX_GAP,
                    cornerRadius = HOUR_CELL_CORNER_RADIUS,
                    onSelect = { index -> selectedCell = dayCells.getOrNull(index) },
                )
            }
            if (index != rows.lastIndex) Spacer(Modifier.height(8.dp))
        }
        selectedCell?.let { cell ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = strings.heatmapCellFormat.format(
                    dayName(cell.dayOfWeek, locale, "EEEE"),
                    cell.fromHour,
                    cell.toHour,
                    cell.averageMinutes.roundToInt(),
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(16.dp))
        AnalyticsHeatmapLegend(levels, strings)
    }
}

@Composable
private fun AnalyticsHeatmapHourLabels(
    cells: List<AnalyticsWeekdayHourCellUi>,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            cells.forEach { cell ->
                Text(
                    text = "${cell.fromHour}–${cell.toHour}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        },
    ) { measurables, constraints ->
        val cellSize = calculateHeatmapCellSize(
            width = constraints.maxWidth.toFloat(),
            itemCount = cells.size,
            maxCellSize = HOUR_CELL_MAX_WIDTH.toPx(),
            maxGap = HOUR_CELL_MAX_GAP.toPx(),
        )
        val gap = calculateHeatmapGap(
            width = constraints.maxWidth.toFloat(),
            itemCount = cells.size,
            cellSize = cellSize,
            maxGap = HOUR_CELL_MAX_GAP.toPx(),
        )
        val labelWidth = (cellSize + gap).roundToInt().coerceAtLeast(1)
        val placeables = measurables.map { measurable ->
            measurable.measure(
                Constraints(
                    maxWidth = labelWidth,
                    maxHeight = constraints.maxHeight,
                ),
            )
        }
        val height = placeables.maxOfOrNull { it.height } ?: 0
        layout(constraints.maxWidth, height) {
            placeables.forEachIndexed { index, placeable ->
                val center = calculateHeatmapItemCenter(
                    index = index,
                    cellSize = cellSize,
                    gap = gap,
                ).roundToInt()
                val x = (center - placeable.width / 2).coerceIn(
                    0,
                    (constraints.maxWidth - placeable.width).coerceAtLeast(0),
                )
                placeable.placeRelative(x, 0)
            }
        }
    }
}

@Composable
internal fun CategoryDayPartHeatmap(
    cells: List<CategoryDayPartCellUi>,
    summaries: List<CategoryDayPartSummaryUi>,
    categoryColor: Color,
    locale: Locale,
    strings: AnalyticsStrings,
    modifier: Modifier = Modifier,
) {
    val levels = MaterialTheme.colorScheme.fetchCategoryHeatmapLevels(categoryColor)
    val weekdays = remember(locale) { localeWeekdays(locale) }
    val cellsByPart = remember(cells) { cells.groupBy { it.dayPart } }
    val summariesByPart = remember(summaries) { summaries.associateBy { it.dayPart } }

    Column(modifier) {
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.width(DAY_PART_LABEL_WIDTH))
            CategoryDayPartWeekdayLabels(
                modifier = Modifier.weight(1f),
                weekdays = weekdays,
                locale = locale,
            )
        }
        Spacer(Modifier.height(8.dp))
        DAY_PART_ORDER.forEachIndexed { index, dayPart ->
            val rowCells = remember(cellsByPart, dayPart) {
                cellsByPart[dayPart].orEmpty().associateBy { it.dayOfWeek }
            }
            val itemLevels = remember(rowCells, weekdays) {
                weekdays.map { rowCells[it]?.level ?: 0 }
            }
            val busiest = summariesByPart[dayPart]
            val rowTitle = dayPart.title(strings)
            val rowDescription = if (busiest != null) {
                strings.dayPartRowDescFormat.format(
                    rowTitle,
                    dayName(busiest.busiestDayOfWeek, locale, "EEEE"),
                    busiest.busiestAverageMinutes.toInt(),
                )
            } else {
                "$rowTitle: ${strings.sectionNoData}"
            }
            Row(
                modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) { contentDescription = rowDescription },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.width(DAY_PART_LABEL_WIDTH),
                    text = rowTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
                HeatmapRow(
                    modifier = Modifier.weight(1f),
                    levels = levels,
                    itemCount = WEEKDAYS_IN_WEEK,
                    itemLevels = itemLevels,
                    maxCellWidth = DAY_PART_CELL_MAX_SIZE,
                    cellHeight = DAY_PART_CELL_MAX_SIZE,
                    maxGap = DAY_PART_CELL_MAX_GAP,
                    cornerRadius = DAY_PART_CELL_CORNER_RADIUS,
                )
            }
            if (index != DAY_PART_ORDER.lastIndex) Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(16.dp))
        AnalyticsHeatmapLegend(levels, strings)
    }
}

@Composable
private fun CategoryDayPartWeekdayLabels(
    weekdays: List<Int>,
    locale: Locale,
    modifier: Modifier = Modifier,
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val maxCellSize = with(density) { DAY_PART_CELL_MAX_SIZE.toPx() }
    val maxGap = with(density) { DAY_PART_CELL_MAX_GAP.toPx() }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Layout(
        modifier = modifier,
        content = {
            weekdays.forEach { day ->
                Text(
                    text = dayName(day, locale, "EE"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        },
    ) { measurables, constraints ->
        val cellSize = calculateHeatmapCellSize(
            width = constraints.maxWidth.toFloat(),
            itemCount = WEEKDAYS_IN_WEEK,
            maxCellSize = maxCellSize,
            maxGap = maxGap,
        )
        val gap = calculateHeatmapGap(
            width = constraints.maxWidth.toFloat(),
            itemCount = WEEKDAYS_IN_WEEK,
            cellSize = cellSize,
            maxGap = maxGap,
        )
        val labelWidth = (cellSize + gap).roundToInt().coerceAtLeast(1)
        val placeables = measurables.map { measurable ->
            measurable.measure(
                Constraints(
                    maxWidth = labelWidth,
                    maxHeight = constraints.maxHeight,
                ),
            )
        }
        val height = placeables.maxOfOrNull { it.height } ?: 0
        layout(constraints.maxWidth, height) {
            placeables.forEachIndexed { index, placeable ->
                val physicalIndex = if (isRtl) WEEKDAYS_IN_WEEK - index - 1 else index
                val center = calculateHeatmapItemCenter(
                    index = physicalIndex,
                    cellSize = cellSize,
                    gap = gap,
                ).roundToInt()
                val x = (center - placeable.width / 2).coerceIn(
                    minimumValue = 0,
                    maximumValue = (constraints.maxWidth - placeable.width).coerceAtLeast(0),
                )
                placeable.place(x, 0)
            }
        }
    }
}

@Composable
private fun HeatmapRow(
    levels: List<Color>,
    itemCount: Int,
    itemLevels: List<Int>,
    maxCellWidth: androidx.compose.ui.unit.Dp,
    cellHeight: androidx.compose.ui.unit.Dp,
    maxGap: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    onSelect: ((Int) -> Unit)? = null,
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val maxCellWidthPx = with(density) { maxCellWidth.toPx() }
    val cellHeightPx = with(density) { cellHeight.toPx() }
    val maxGapPx = with(density) { maxGap.toPx() }
    val cornerRadiusPx = with(density) { cornerRadius.toPx() }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val currentOnSelect by rememberUpdatedState(onSelect)
    Box(
        modifier = modifier
            .height(cellHeight)
            .then(
                if (onSelect != null) {
                    Modifier
                        .pointerInput(itemLevels, itemCount, maxCellWidthPx, maxGapPx, isRtl) {
                            detectTapGestures { offset ->
                                val cellSize = calculateHeatmapCellSize(size.width.toFloat(), itemCount, maxCellWidthPx, maxGapPx)
                                val gap = calculateHeatmapGap(size.width.toFloat(), itemCount, cellSize, maxGapPx)
                                val index = heatmapLogicalIndex(offset.x, itemCount, cellSize, gap, isRtl)
                                currentOnSelect?.invoke(index)
                            }
                        }
                        .pointerInput(itemLevels, itemCount, maxCellWidthPx, maxGapPx, isRtl) {
                            detectDragGestures { change, _ ->
                                change.consume()
                                val offset = change.position
                                val cellSize = calculateHeatmapCellSize(size.width.toFloat(), itemCount, maxCellWidthPx, maxGapPx)
                                val gap = calculateHeatmapGap(size.width.toFloat(), itemCount, cellSize, maxGapPx)
                                val index = heatmapLogicalIndex(offset.x, itemCount, cellSize, gap, isRtl)
                                currentOnSelect?.invoke(index)
                            }
                        }
                } else {
                    Modifier
                },
            )
            .drawWithCache {
                val cellSize = calculateHeatmapCellSize(size.width, itemCount, maxCellWidthPx, maxGapPx)
                val gap = calculateHeatmapGap(size.width, itemCount, cellSize, maxGapPx)
                onDrawBehind {
                    repeat(itemCount) { index ->
                        val physicalIndex = if (isRtl) itemCount - index - 1 else index
                        drawRoundRect(
                            color = levels[itemLevels.getOrElse(index) { 0 }.coerceIn(levels.indices)],
                            topLeft = Offset(physicalIndex * (cellSize + gap), 0f),
                            size = Size(cellSize, cellHeightPx),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx),
                        )
                    }
                }
            },
    )
}

private fun calculateHeatmapCellSize(
    width: Float,
    itemCount: Int,
    maxCellSize: Float,
    maxGap: Float,
): Float {
    return minOf(maxCellSize, (width - maxGap * (itemCount - 1)) / itemCount).coerceAtLeast(1f)
}

private fun calculateHeatmapGap(
    width: Float,
    itemCount: Int,
    cellSize: Float,
    maxGap: Float,
): Float {
    return minOf(maxGap, (width - cellSize * itemCount) / (itemCount - 1).coerceAtLeast(1)).coerceAtLeast(0f)
}

internal fun heatmapLogicalIndex(
    x: Float,
    itemCount: Int,
    cellSize: Float,
    gap: Float,
    isRtl: Boolean,
): Int {
    val physicalIndex = floor((x + gap / 2f) / (cellSize + gap)).toInt().coerceIn(0, itemCount - 1)
    return if (isRtl) itemCount - physicalIndex - 1 else physicalIndex
}

internal fun calculateHeatmapItemCenter(
    index: Int,
    cellSize: Float,
    gap: Float,
): Float {
    return index * (cellSize + gap) + cellSize / 2f
}

@Composable
private fun AnalyticsHeatmapLegend(levels: List<Color>, strings: AnalyticsStrings) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            levels.forEach { color ->
                Box(Modifier.weight(1f).height(8.dp).background(color, RoundedCornerShape(2.dp)))
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(strings.less, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(strings.more, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun localeWeekdays(locale: Locale): List<Int> {
    val firstDay = Calendar.getInstance(locale).firstDayOfWeek
    return List(WEEKDAYS_IN_WEEK) { index -> ((firstDay - 1 + index) % WEEKDAYS_IN_WEEK) + 1 }
}

private fun dayName(dayOfWeek: Int, locale: Locale, pattern: String): String {
    return SimpleDateFormat(pattern, locale).format(
        Calendar.getInstance(locale).apply { set(Calendar.DAY_OF_WEEK, dayOfWeek) }.time,
    )
}

private fun CategoryDayPart.title(strings: AnalyticsStrings) = when (this) {
    CategoryDayPart.MORNING -> strings.dayPartMorning
    CategoryDayPart.DAY -> strings.dayPartDay
    CategoryDayPart.EVENING -> strings.dayPartEvening
    CategoryDayPart.NIGHT -> strings.dayPartNight
}

private const val WEEKDAYS_IN_WEEK = 7
private val HOUR_CELL_MAX_WIDTH = 36.dp
private val HOUR_CELL_HEIGHT = 16.dp
private val HOUR_CELL_MAX_GAP = 4.dp
private val HOUR_CELL_CORNER_RADIUS = 5.dp
private val DAY_LABEL_WIDTH = 24.dp
private val DAY_LABEL_GAP = 8.dp
private val DAY_PART_LABEL_WIDTH = 80.dp
private val DAY_PART_CELL_MAX_SIZE = 28.dp
private val DAY_PART_CELL_MAX_GAP = 4.dp
private val DAY_PART_CELL_CORNER_RADIUS = 6.dp
private val DAY_PART_ORDER = listOf(
    CategoryDayPart.MORNING,
    CategoryDayPart.DAY,
    CategoryDayPart.EVENING,
    CategoryDayPart.NIGHT,
)
