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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.config.BarChartConfig
import com.himanshoe.charty.bar.data.BarData
import com.himanshoe.charty.color.ChartyColor
import com.himanshoe.charty.combo.ComboChart
import com.himanshoe.charty.combo.config.ComboChartConfig
import com.himanshoe.charty.combo.data.ComboChartData
import com.himanshoe.charty.common.config.Animation
import com.himanshoe.charty.common.config.ChartScaffoldConfig
import com.himanshoe.charty.common.config.CornerRadius
import com.himanshoe.charty.common.tooltip.TooltipConfig
import com.himanshoe.charty.line.MultilineChart
import com.himanshoe.charty.line.config.LineChartConfig
import com.himanshoe.charty.line.data.LineGroup
import com.himanshoe.charty.pie.PieChart
import com.himanshoe.charty.pie.config.InteractionConfig
import com.himanshoe.charty.pie.config.LabelConfig
import com.himanshoe.charty.pie.config.PieChartConfig
import com.himanshoe.charty.pie.config.PieChartStyle
import com.himanshoe.charty.pie.data.PieData
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsChartPointUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsDonutSliceUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsLineSeriesUi
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun AnalyticsDualAxisChart(
    points: List<AnalyticsChartPointUi>,
    modifier: Modifier = Modifier,
    tooltipFormatter: (AnalyticsChartPointUi) -> String,
    primaryAxisLabel: (Float) -> String,
    selectedKey: Long? = null,
    onSelect: (Long) -> Unit,
) {
    val hasValues = remember(points) { points.any { it.primaryValue > 0f || it.secondaryValue > 0f } }
    if (points.isEmpty() || !hasValues) {
        AnalyticsNeutralChartTrack(modifier.semantics(mergeDescendants = true) {})
        return
    }
    val primaryMax = remember(points) { points.maxOf { it.primaryValue }.coerceAtLeast(0f) }
    val primaryScaleMax = remember(primaryMax) { primaryMax.coerceAtLeast(1f) }
    val secondaryValueMax = remember(points) { points.maxOf { it.secondaryValue }.coerceAtLeast(1f) }
    val secondaryMax = remember(points) { points.maxOf { it.secondaryValue }.toInt().coerceAtLeast(0) }
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val density = LocalDensity.current
    val barColor = remember(primary) { ChartyColor.Solid(primary) }
    val lineColor = remember(tertiary) { ChartyColor.Solid(tertiary) }
    val scaffoldConfig = rememberAnalyticsChartScaffold(showLabels = false)
    val tooltipConfig = rememberAnalyticsTooltipConfig()

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth().height(224.dp).semantics(mergeDescendants = true) {
            contentDescription = buildString {
                points.find { it.key == selectedKey }?.let { point ->
                    append(tooltipFormatter(point))
                }
            }
            liveRegion = LiveRegionMode.Polite
        },
    ) {
        val axisScale = density.fontScale.coerceIn(1f, MAX_AXIS_FONT_SCALE)
        val primaryAxisWidth = PRIMARY_AXIS_WIDTH * axisScale
        val secondaryAxisWidth = SECONDARY_AXIS_WIDTH * axisScale
        val horizontalAxisHeight = HORIZONTAL_AXIS_HEIGHT * axisScale
        val textMeasurer = rememberTextMeasurer()
        val labelStyle = MaterialTheme.typography.labelSmall
        val labelSlotWidthPx = remember(points, labelStyle, density) {
            points.maxOf { point ->
                textMeasurer.measure(point.label, labelStyle).size.width
            } + with(density) { LINE_LABEL_GAP.toPx() }
        }.coerceAtLeast(1f)
        val chartWidthPx = with(density) {
            (maxWidth - primaryAxisWidth - secondaryAxisWidth).coerceAtLeast(1.dp).toPx()
        }
        val plotWidthPx = (chartWidthPx - CHARTY_PLOT_PADDING_PX * 2f).coerceAtLeast(1f)
        val maxLabelCount = (plotWidthPx / labelSlotWidthPx).toInt().coerceAtLeast(2)
        val stride = ceil((points.size - 1).toDouble() / (maxLabelCount - 1)).toInt().coerceAtLeast(1)
        val displayLabels = remember(points, stride, selectedKey) {
            buildAnalyticsChartLabels(points, stride, selectedKey)
        }
        val data = remember(points, displayLabels, primaryScaleMax, secondaryValueMax) {
            points.mapIndexed { index, point ->
                ComboChartData(
                    displayLabels[index],
                    normalizeAnalyticsChartValue(point.primaryValue, primaryScaleMax),
                    normalizeAnalyticsChartValue(point.secondaryValue, secondaryValueMax),
                )
            }
        }
        val rawPoints = remember(points, displayLabels) {
            displayLabels.mapIndexed { index, label -> label to points[index] }.toMap()
        }
        val lineWidth = with(density) { 3.dp.toPx() }
        val pointRadius = with(density) { 3.dp.toPx() }
        val comboConfig = remember(rawPoints, tooltipFormatter, lineWidth, pointRadius, tooltipConfig) {
            ComboChartConfig(
                barWidthFraction = 0.56f,
                barCornerRadius = CornerRadius.Medium,
                lineWidth = lineWidth,
                showPoints = true,
                pointRadius = pointRadius,
                animation = ANALYTICS_CHART_ANIMATION,
                tooltipConfig = tooltipConfig,
                tooltipFormatter = { dataPoint ->
                    rawPoints[dataPoint.label]?.let(tooltipFormatter) ?: dataPoint.label
                },
            )
        }
        ComboChart(
            modifier = Modifier.fillMaxSize().padding(
                start = primaryAxisWidth,
                end = secondaryAxisWidth,
                bottom = horizontalAxisHeight,
            ),
            data = { data },
            barColor = barColor,
            lineColor = lineColor,
            comboConfig = comboConfig,
            scaffoldConfig = scaffoldConfig,
        )
        Box(
            Modifier
                .fillMaxSize()
                .padding(
                    start = primaryAxisWidth,
                    end = secondaryAxisWidth,
                    bottom = horizontalAxisHeight,
                )
                .pointerInput(points, onSelect) {
                    detectTapGestures { offset ->
                        val pointIndex = calculateAnalyticsChartPointIndex(
                            x = offset.x,
                            width = size.width.toFloat(),
                            pointCount = points.size,
                        )
                        onSelect(points[pointIndex].key)
                    }
                },
        )
        VerticalAxis(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(primaryAxisWidth)
                .padding(bottom = horizontalAxisHeight),
            maxValue = primaryMax,
            valueLabel = primaryAxisLabel,
            textAlignment = TextAlign.Start,
        )
        VerticalAxis(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(secondaryAxisWidth)
                .padding(bottom = horizontalAxisHeight),
            maxValue = secondaryMax.toFloat(),
            valueLabel = { it.roundToInt().toString() },
            textAlignment = TextAlign.End,
        )
        HorizontalAxisLabels(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(horizontalAxisHeight)
                .padding(start = primaryAxisWidth, end = secondaryAxisWidth),
            labels = points.mapIndexed { index, point ->
                point.label.takeIf {
                    index == 0 || index == points.lastIndex || point.key == selectedKey || index % stride == 0
                }.orEmpty()
            },
            isCenteredInSlot = true,
        )
    }
}

internal fun buildAnalyticsChartLabels(
    points: List<AnalyticsChartPointUi>,
    labelStride: Int,
    selectedKey: Long?,
): List<String> {
    require(labelStride > 0)
    return points.mapIndexed { index, point ->
        val isVisible = index == 0 || index == points.lastIndex || point.key == selectedKey || index % labelStride == 0
        val identitySuffix = WORD_JOINER.repeat(index + 1)
        if (isVisible) point.label + identitySuffix else identitySuffix
    }
}

@Composable
internal fun AnalyticsBarChart(
    modifier: Modifier = Modifier,
    points: List<AnalyticsChartPointUi>,
    colors: List<Color>,
    pointDescription: (AnalyticsChartPointUi) -> String,
    valueLabel: (AnalyticsChartPointUi) -> String,
    axisValueLabel: (Float) -> String,
    onSelect: ((Long) -> Unit)? = null,
) {
    val hasValues = remember(points) { points.any { it.primaryValue > 0f } }
    if (points.isEmpty() || !hasValues) {
        AnalyticsNeutralChartTrack(modifier.semantics(mergeDescendants = true) {})
        return
    }
    val fallbackColor = MaterialTheme.colorScheme.primary
    val primaryMax = remember(points) { points.maxOf { it.primaryValue }.coerceAtLeast(1f) }
    val data = remember(points, colors, fallbackColor, primaryMax) {
        points.mapIndexed { index, point ->
            BarData(
                label = point.label,
                value = normalizeAnalyticsChartValue(point.primaryValue, primaryMax),
                color = ChartyColor.Solid(colors.getOrElse(index) { fallbackColor }),
            )
        }
    }
    val pointKeys = remember(points) { points.associate { it.label to it.key } }
    val rawPoints = remember(points) { points.associateBy(AnalyticsChartPointUi::label) }
    val barColor = remember(fallbackColor) { ChartyColor.Solid(fallbackColor) }
    val tooltipConfig = rememberAnalyticsTooltipConfig()
    val barConfig = remember(rawPoints, pointDescription, tooltipConfig) {
        BarChartConfig(
            barWidthFraction = 0.56f,
            cornerRadius = CornerRadius.Medium,
            animation = ANALYTICS_CHART_ANIMATION,
            tooltipConfig = tooltipConfig,
            tooltipFormatter = { dataPoint ->
                rawPoints[dataPoint.label]?.let(pointDescription) ?: dataPoint.label
            },
        )
    }
    val scaffoldConfig = rememberAnalyticsChartScaffold(showLabels = false)
    val density = LocalDensity.current
    val axisScale = density.fontScale.coerceIn(1f, MAX_AXIS_FONT_SCALE)
    val primaryAxisWidth = PRIMARY_AXIS_WIDTH * axisScale
    val horizontalAxisHeight = HORIZONTAL_AXIS_HEIGHT * axisScale
    Box(
        modifier = modifier.fillMaxWidth().height(184.dp).semantics(mergeDescendants = true) {
            contentDescription = buildString {
                points.forEach { point ->
                    append(". ")
                    append(pointDescription(point))
                }
            }
        },
    ) {
        if (onSelect == null) {
            BarChart(
                modifier = Modifier.fillMaxSize().padding(start = primaryAxisWidth, bottom = horizontalAxisHeight),
                data = { data },
                color = barColor,
                barConfig = barConfig,
                scaffoldConfig = scaffoldConfig,
            )
        } else {
            BarChart(
                modifier = Modifier.fillMaxSize().padding(start = primaryAxisWidth, bottom = horizontalAxisHeight),
                data = { data },
                color = barColor,
                barConfig = barConfig,
                scaffoldConfig = scaffoldConfig,
                onBarClick = { dataPoint -> pointKeys[dataPoint.label]?.let(onSelect) },
            )
        }
        BarValueLabels(
            modifier = Modifier.fillMaxSize().padding(start = primaryAxisWidth, bottom = horizontalAxisHeight),
            points = points,
            valueLabel = valueLabel,
        )
        VerticalAxis(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(primaryAxisWidth)
                .padding(bottom = horizontalAxisHeight),
            maxValue = primaryMax,
            valueLabel = axisValueLabel,
            textAlignment = TextAlign.Start,
        )
        HorizontalAxisLabels(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(horizontalAxisHeight)
                .padding(start = primaryAxisWidth),
            labels = points.map(AnalyticsChartPointUi::label),
            isCenteredInSlot = true,
        )
    }
}

@Composable
private fun BarValueLabels(
    points: List<AnalyticsChartPointUi>,
    valueLabel: (AnalyticsChartPointUi) -> String,
    modifier: Modifier = Modifier,
) {
    val labelColor = MaterialTheme.colorScheme.onSurface
    Layout(
        modifier = modifier,
        content = {
            points.forEach { point ->
                Text(
                    text = valueLabel(point),
                    style = MaterialTheme.typography.labelMedium,
                    color = labelColor,
                )
            }
        },
    ) { measurables, constraints ->
        val pointCount = points.size.coerceAtLeast(1)
        val plotPadding = CHARTY_PLOT_PADDING_PX.roundToInt()
        val plotWidth = (constraints.maxWidth - plotPadding * 2).coerceAtLeast(1)
        val plotHeight = (constraints.maxHeight - plotPadding * 2).coerceAtLeast(1)
        val slotWidth = plotWidth.toFloat() / pointCount
        val labelGap = BAR_LABEL_GAP.roundToPx()
        val maximum = points.maxOf { it.primaryValue }.coerceAtLeast(1f)
        val placeables = measurables.map { measurable ->
            measurable.measure(
                Constraints(
                    maxWidth = ceil(slotWidth).toInt().coerceAtLeast(1),
                    maxHeight = constraints.maxHeight,
                ),
            )
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEachIndexed { index, placeable ->
                val point = points[index]
                val center = calculateAnalyticsChartPointCenter(
                    width = constraints.maxWidth.toFloat(),
                    pointCount = pointCount,
                    index = index,
                ).roundToInt()
                val x = center - placeable.width / 2
                val barTop = plotPadding + (plotHeight * (1f - point.primaryValue / maximum)).roundToInt()
                val y = (barTop - placeable.height - labelGap).coerceAtLeast(0)
                placeable.place(x.coerceAtLeast(0), y)
            }
        }
    }
}

@Composable
internal fun AnalyticsDonutChart(
    slices: List<AnalyticsDonutSliceUi>,
    sliceDescription: (AnalyticsDonutSliceUi) -> String,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    selectedKey: Long? = null,
    onSelect: (Long) -> Unit,
) {
    val displayedSlices = remember(slices) { filterAnalyticsDonutSlices(slices) }
    if (displayedSlices.isEmpty()) {
        AnalyticsNeutralChartTrack(modifier.size(size).semantics(mergeDescendants = true) {})
        return
    }
    val data = remember(displayedSlices) {
        displayedSlices.map { PieData(it.key.toString(), it.value, it.color) }
    }
    val sliceKeys = remember(displayedSlices) {
        displayedSlices.associate { it.key.toString() to it.key }
    }
    val pieConfig = remember {
        PieChartConfig(
            style = PieChartStyle.DONUT,
            donutHoleRatio = 0.62f,
            startAngleDegrees = -90f,
            labelConfig = LabelConfig(shouldShowLabels = false),
            interactionConfig = ANALYTICS_PIE_INTERACTION,
            animation = ANALYTICS_CHART_ANIMATION,
            sliceSpacingDegrees = 0f,
            shouldShowCenterText = false,
        )
    }
    PieChart(
        modifier = modifier.size(size).semantics(mergeDescendants = true) {
            contentDescription = buildString {
                displayedSlices.find { it.key == selectedKey }?.let { slice ->
                    append(". ")
                    append(sliceDescription(slice))
                }
            }
            liveRegion = LiveRegionMode.Polite
        },
        data = { data },
        config = pieConfig,
        onSliceClick = { dataPoint, _ -> sliceKeys[dataPoint.label]?.let(onSelect) },
    )
}

@Composable
internal fun AnalyticsDonutCenterLabel(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(DONUT_CENTER_CONTENT_SIZE).clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = value,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

internal fun filterAnalyticsDonutSlices(slices: List<AnalyticsDonutSliceUi>) = slices.filter { it.value > 0f }

@Composable
internal fun AnalyticsLineChart(
    series: List<AnalyticsLineSeriesUi>,
    labels: List<String>,
    colors: List<Color>,
    summary: String,
    pointDescription: (Int) -> String,
    axisValueLabel: (Float) -> String,
    modifier: Modifier = Modifier,
    selectedIndex: Int? = null,
    onSelect: (Int) -> Unit,
) {
    val pointCount = remember(series, labels) {
        minOf(labels.size, series.minOfOrNull { it.values.size } ?: 0)
    }
    val hasValues = remember(series, pointCount) {
        series.any { line -> line.values.take(pointCount).any { it > 0f } }
    }
    if (series.isEmpty() || pointCount == 0 || !hasValues) {
        AnalyticsNeutralChartTrack(modifier.semantics(mergeDescendants = true) { contentDescription = summary })
        return
    }
    val primaryMax = remember(series, pointCount) {
        series.maxOf { line -> line.values.take(pointCount).maxOrNull() ?: 0f }.coerceAtLeast(1f)
    }
    val normalizedSeries = remember(series, pointCount, primaryMax) {
        series.map { line ->
            line.copy(
                values = normalizeAnalyticsLineValues(
                    values = line.values.take(pointCount),
                    maximum = primaryMax,
                ),
            )
        }
    }
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth().height(224.dp).semantics(mergeDescendants = true) {
            contentDescription = buildString {
                append(summary)
                selectedIndex?.takeIf { it in 0 until pointCount }?.let { index ->
                    append(". ")
                    append(pointDescription(index))
                }
            }
            liveRegion = LiveRegionMode.Polite
        },
    ) {
        val density = LocalDensity.current
        val axisScale = density.fontScale.coerceIn(1f, MAX_AXIS_FONT_SCALE)
        val primaryAxisWidth = PRIMARY_AXIS_WIDTH * axisScale
        val horizontalAxisHeight = HORIZONTAL_AXIS_HEIGHT * axisScale
        val textMeasurer = rememberTextMeasurer()
        val labelStyle = MaterialTheme.typography.labelSmall
        val labelSlotWidthPx = remember(labels, pointCount, labelStyle, density) {
            labels.take(pointCount).maxOf { label ->
                textMeasurer.measure(label, labelStyle).size.width
            } + with(density) { LINE_LABEL_GAP.toPx() }
        }.coerceAtLeast(1f)
        val chartWidthPx = with(density) { (maxWidth - primaryAxisWidth).coerceAtLeast(1.dp).toPx() }
        val maxWidthPx = (chartWidthPx - CHARTY_PLOT_PADDING_PX * 2f).coerceAtLeast(1f)
        val maxLabelCount = (maxWidthPx / labelSlotWidthPx).toInt().coerceAtLeast(2)
        val stride = ceil((pointCount - 1).toDouble() / (maxLabelCount - 1)).toInt().coerceAtLeast(1)
        val data = remember(normalizedSeries, labels, pointCount, stride, selectedIndex) {
            buildAnalyticsLineGroupsBySeries(normalizedSeries, labels, stride, selectedIndex)
        }
        val fallbackColor = MaterialTheme.colorScheme.primary
        val chartColors = remember(colors, fallbackColor, series.size) {
            series.indices.map { index -> ChartyColor.Solid(colors.getOrElse(index) { fallbackColor }) }
        }
        val categoryLineWidth = with(density) { 3.dp.toPx() }
        val categoryPointRadius = with(density) { 3.dp.toPx() }
        val allPlanLineWidth = with(density) { 2.dp.toPx() }
        val allPlanPointRadius = with(density) { 2.dp.toPx() }
        val tooltipConfig = rememberAnalyticsTooltipConfig()
        val categoryLineConfig = remember(categoryLineWidth, categoryPointRadius, tooltipConfig) {
            LineChartConfig(
                lineWidth = categoryLineWidth,
                smoothCurve = true,
                showPoints = true,
                pointRadius = categoryPointRadius,
                animation = ANALYTICS_CHART_ANIMATION,
                tooltipConfig = tooltipConfig,
            )
        }
        val allPlanLineConfig = remember(allPlanLineWidth, allPlanPointRadius, tooltipConfig) {
            LineChartConfig(
                lineWidth = allPlanLineWidth,
                smoothCurve = true,
                showPoints = true,
                pointRadius = allPlanPointRadius,
                animation = ANALYTICS_CHART_ANIMATION,
                tooltipConfig = tooltipConfig,
            )
        }
        val scaffoldConfig = rememberAnalyticsChartScaffold(showLabels = false)
        val overlayScaffoldConfig = rememberAnalyticsChartScaffold(
            showAxis = false,
            showGrid = false,
            showLabels = false,
        )
        data.forEachIndexed { index, seriesData ->
            MultilineChart(
                modifier = Modifier.fillMaxSize().padding(start = primaryAxisWidth, bottom = horizontalAxisHeight),
                data = { seriesData },
                colors = chartColors[index],
                lineConfig = if (index == 0) categoryLineConfig else allPlanLineConfig,
                scaffoldConfig = if (index == 0) scaffoldConfig else overlayScaffoldConfig,
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(start = primaryAxisWidth, bottom = horizontalAxisHeight)
                .pointerInput(pointCount, onSelect) {
                    detectTapGestures { offset ->
                        onSelect(calculateAnalyticsChartPointIndex(offset.x, size.width.toFloat(), pointCount))
                    }
                },
        )
        VerticalAxis(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(primaryAxisWidth)
                .padding(bottom = horizontalAxisHeight),
            maxValue = primaryMax,
            valueLabel = axisValueLabel,
            textAlignment = TextAlign.Start,
        )
        HorizontalAxisLabels(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(horizontalAxisHeight)
                .padding(start = primaryAxisWidth),
            labels = labels.take(pointCount).mapIndexed { index, label ->
                label.takeIf {
                    index == 0 || index == pointCount - 1 || index == selectedIndex || index % stride == 0
                }.orEmpty()
            },
            isCenteredInSlot = true,
        )
    }
}

internal fun buildAnalyticsLineGroups(
    series: List<AnalyticsLineSeriesUi>,
    labels: List<String>,
    labelStride: Int,
    selectedIndex: Int?,
): List<LineGroup> {
    require(labelStride > 0)
    val pointCount = minOf(labels.size, series.minOfOrNull { it.values.size } ?: 0)
    return List(pointCount) { index ->
        val shouldShowLabel = index == 0 || index == pointCount - 1 || index == selectedIndex || index % labelStride == 0
        LineGroup(
            label = labels[index].takeIf { shouldShowLabel }.orEmpty(),
            values = series.map { it.values[index] },
        )
    }
}

internal fun buildAnalyticsLineGroupsBySeries(
    series: List<AnalyticsLineSeriesUi>,
    labels: List<String>,
    labelStride: Int,
    selectedIndex: Int?,
): List<List<LineGroup>> {
    val pointCount = minOf(labels.size, series.minOfOrNull { it.values.size } ?: 0)
    val sharedLabels = labels.take(pointCount)
    return series.map { line ->
        buildAnalyticsLineGroups(listOf(line), sharedLabels, labelStride, selectedIndex)
    }
}

internal fun calculateAnalyticsChartPointIndex(
    x: Float,
    width: Float,
    pointCount: Int,
): Int {
    require(pointCount > 0)
    if (width <= 0f) return 0
    val plotWidth = (width - CHARTY_PLOT_PADDING_PX * 2f).coerceAtLeast(1f)
    val plotX = (x - CHARTY_PLOT_PADDING_PX).coerceIn(0f, plotWidth)
    return (plotX / plotWidth * pointCount).toInt().coerceIn(0, pointCount - 1)
}

internal fun calculateAnalyticsChartPointCenter(
    width: Float,
    pointCount: Int,
    index: Int,
): Float {
    require(pointCount > 0)
    require(index in 0 until pointCount)
    val plotWidth = (width - CHARTY_PLOT_PADDING_PX * 2f).coerceAtLeast(1f)
    return CHARTY_PLOT_PADDING_PX + (index + 0.5f) * plotWidth / pointCount
}

internal fun normalizeAnalyticsChartValue(value: Float, maximum: Float): Float {
    if (maximum <= 0f) return 0f
    return value.coerceAtLeast(0f) / maximum * CHARTY_EXCLUSIVE_SCALE_MAX
}

internal fun normalizeAnalyticsLineValues(
    values: List<Float>,
    maximum: Float,
): List<Float> {
    val normalizedValues = values.map { normalizeAnalyticsChartValue(it, maximum) }
    return when (normalizedValues.any { it > 0f }) {
        true -> normalizedValues
        false -> List(normalizedValues.size) { CHARTY_SCALE_SENTINEL }
    }
}

@Composable
private fun VerticalAxis(
    maxValue: Float,
    valueLabel: (Float) -> String,
    textAlignment: TextAlign,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val plotPadding = with(density) { CHARTY_PLOT_PADDING_PX.toDp() }
    Column(
        modifier = modifier.padding(vertical = plotPadding),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        listOf(maxValue, maxValue / 2f, 0f).forEach { value ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = valueLabel(value),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = textAlignment,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun HorizontalAxisLabels(
    labels: List<String>,
    isCenteredInSlot: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val labelGap = with(density) { AXIS_LABEL_GAP.toPx() }
    val visibleLabels = remember(labels) {
        labels.mapIndexedNotNull { index, label -> (index to label).takeIf { label.isNotEmpty() } }
    }
    Layout(
        modifier = modifier,
        content = {
            visibleLabels.forEach { (_, label) ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
    ) { measurables, constraints ->
        val plotPadding = CHARTY_PLOT_PADDING_PX.roundToInt()
        val plotWidth = (constraints.maxWidth - plotPadding * 2).coerceAtLeast(1)
        val centers = visibleLabels.map { (index, _) ->
            if (isCenteredInSlot || labels.size == 1) {
                calculateAnalyticsChartPointCenter(
                    width = constraints.maxWidth.toFloat(),
                    pointCount = labels.size,
                    index = index,
                )
            } else {
                plotPadding + index * plotWidth.toFloat() / (labels.size - 1)
            }
        }
        val labelWidths = calculateAnalyticsChartLabelWidths(
            centers = centers,
            totalWidth = constraints.maxWidth.toFloat(),
            gap = labelGap,
        )
        val placeables = measurables.mapIndexed { index, measurable ->
            measurable.measure(
                Constraints(
                    maxWidth = ceil(labelWidths[index]).toInt().coerceAtLeast(1),
                    maxHeight = constraints.maxHeight,
                ),
            )
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            centers.zip(placeables).forEach { (center, placeable) ->
                val maximumX = (constraints.maxWidth - placeable.width)
                    .coerceAtLeast(0)
                    .toFloat()
                val x = (center - placeable.width / 2f).coerceIn(
                    minimumValue = 0f,
                    maximumValue = maximumX,
                )
                placeable.place(x.roundToInt(), 0)
            }
        }
    }
}

internal fun calculateAnalyticsChartLabelWidths(
    centers: List<Float>,
    totalWidth: Float,
    gap: Float,
): List<Float> {
    require(totalWidth >= 0f)
    require(gap >= 0f)
    return centers.mapIndexed { index, center ->
        val leftBoundary = centers.getOrNull(index - 1)
            ?.let { previous -> (previous + center) / 2f }
            ?: 0f
        val rightBoundary = centers.getOrNull(index + 1)
            ?.let { next -> (center + next) / 2f }
            ?: totalWidth
        val centeredWidth = 2f * minOf(
            center - leftBoundary,
            rightBoundary - center,
        )
        (centeredWidth - gap).coerceAtLeast(1f)
    }
}

@Composable
private fun AnalyticsNeutralChartTrack(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(8.dp)),
    )
}

private val DONUT_CENTER_CONTENT_SIZE = 72.dp

@Composable
private fun rememberAnalyticsChartScaffold(
    showAxis: Boolean = true,
    showGrid: Boolean = true,
    showLabels: Boolean = true,
): ChartScaffoldConfig {
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelTextStyle = MaterialTheme.typography.labelSmall.copy(color = axisColor)
    val density = LocalDensity.current
    val strokeWidth = with(density) { 1.dp.toPx() }
    return remember(axisColor, gridColor, labelTextStyle, showAxis, showGrid, showLabels, strokeWidth) {
        ChartScaffoldConfig(
            showAxis = showAxis,
            showGrid = showGrid,
            showLabels = showLabels,
            axisColor = axisColor,
            gridColor = gridColor,
            axisThickness = strokeWidth,
            gridThickness = strokeWidth,
            labelTextStyle = labelTextStyle,
        )
    }
}

@Composable
private fun rememberAnalyticsTooltipConfig(): TooltipConfig {
    val shape = MaterialTheme.shapes.small
    val backgroundColor = MaterialTheme.colorScheme.inverseSurface
    val textStyle = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.inverseOnSurface,
    )
    return remember(shape, backgroundColor, textStyle) {
        TooltipConfig(
            shape = shape,
            backgroundColor = backgroundColor,
            textStyle = textStyle,
        )
    }
}

private val SECONDARY_AXIS_WIDTH = 28.dp
private val PRIMARY_AXIS_WIDTH = 36.dp
private val HORIZONTAL_AXIS_HEIGHT = 28.dp
private val LINE_LABEL_GAP = 8.dp
private val BAR_LABEL_GAP = 2.dp
private val AXIS_LABEL_GAP = 4.dp
private const val WORD_JOINER = "\u2060"
private const val MAX_AXIS_FONT_SCALE = 2f
private const val CHARTY_EXCLUSIVE_SCALE_MAX = 9.999f
private const val CHARTY_SCALE_SENTINEL = 0.0001f
private const val CHARTY_PLOT_PADDING_PX = 20f
private const val PIE_SELECTION_ANIMATION_DURATION_MS = 200
private val ANALYTICS_CHART_ANIMATION = Animation.Disabled
private val ANALYTICS_PIE_INTERACTION = InteractionConfig(
    isEnabled = true,
    selectedScaleMultiplier = 1f,
    selectedSlicePullOutDistance = 0f,
    selectionAnimationDurationMs = PIE_SELECTION_ANIMATION_DURATION_MS,
    enableHoverEffect = false,
    unselectedSliceOpacity = 1f,
)
