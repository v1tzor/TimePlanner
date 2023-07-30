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
package ru.aleshin.features.analytics.impl.presenatiton.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.axis.AxisConfig
import com.himanshoe.charty.line.LineChart
import com.himanshoe.charty.line.config.LineConfig
import com.himanshoe.charty.line.model.LineData
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.toMinutesAndHoursTitle
import ru.aleshin.core.utils.extensions.toDaysTitle
import ru.aleshin.core.utils.extensions.toMonthTitle
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presenatiton.models.analytics.WorkLoadMapUi
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsViewState
import ru.aleshin.features.analytics.impl.presenatiton.ui.views.TimeSelectorSection
import java.math.RoundingMode

/**
 * @author Stanislav Aleshin on 20.04.2023.
 */
@Composable
internal fun WorkLoadTab(
    state: AnalyticsViewState,
    onTimePeriodChanged: (TimePeriod) -> Unit,
    onRefresh: () -> Unit,
) {
    // Pullrefresh not available for Material Design 3
    val refreshState = rememberSwipeRefreshState(
        isRefreshing = state.scheduleAnalytics?.categoriesAnalytics == null,
    )
    SwipeRefresh(state = refreshState, onRefresh = onRefresh) {
        Column(
            Modifier.padding(vertical = 8.dp).fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            val analytics = state.scheduleAnalytics
            if (analytics != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TimeSelectorSection(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                        timePeriod = state.timePeriod,
                        title = AnalyticsThemeRes.strings.planningStatisticsTitle,
                        onTimePeriodChanged = onTimePeriodChanged,
                    )
                    WorkLoadAnalyticsChart(
                        workLoadMap = analytics.dateWorkLoadMap,
                        period = checkNotNull(state.timePeriod),
                    )
                }
                Divider(Modifier.padding(vertical = 8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TimeSelectorSection(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                        timePeriod = state.timePeriod,
                        title = AnalyticsThemeRes.strings.executedStatisticsTitle,
                        onTimePeriodChanged = onTimePeriodChanged,
                    )
                    ExecutedAnalyticsChart(
                        workLoadMap = analytics.dateWorkLoadMap,
                        period = checkNotNull(state.timePeriod),
                    )
                }
                Divider(Modifier.padding(top = 8.dp, bottom = 16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        StatisticInfoView(
                            modifier = Modifier.weight(1f),
                            icon = AnalyticsThemeRes.icons.numberedList,
                            name = AnalyticsThemeRes.strings.totalCountTaskTitle,
                            value = analytics.totalTasksCount.toString(),
                        )
                    }
                    item {
                        StatisticInfoView(
                            modifier = Modifier.weight(1f),
                            icon = AnalyticsThemeRes.icons.numericOneCircle,
                            name = AnalyticsThemeRes.strings.averageCountTaskTitle,
                            value = "~ ${analytics.averageDayLoad}",
                        )
                    }
                    item {
                        StatisticInfoView(
                            modifier = Modifier.weight(1f),
                            icon = AnalyticsThemeRes.icons.timeComplete,
                            name = AnalyticsThemeRes.strings.totalTimeTaskTitle,
                            value = analytics.totalTasksTime.toMinutesAndHoursTitle(),
                        )
                    }
                    item {
                        StatisticInfoView(
                            modifier = Modifier.weight(1f),
                            icon = AnalyticsThemeRes.icons.timeCheck,
                            name = AnalyticsThemeRes.strings.averageTimeTaskTitle,
                            value = analytics.averageTaskTime.toMinutesAndHoursTitle(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun WorkLoadAnalyticsChart(
    modifier: Modifier = Modifier,
    workLoadMap: WorkLoadMapUi,
    period: TimePeriod,
) {
    val barData = mutableListOf<BarData>().apply {
        workLoadMap.forEach { (timeRange, timeTasks) ->
            val xValue = when (period == TimePeriod.YEAR || period == TimePeriod.HALF_YEAR) {
                true -> timeRange.toMonthTitle()
                false -> timeRange.toDaysTitle()
            }
            add(BarData(xValue, timeTasks.size.toFloat()))
        }
    }
    BarChart(
        modifier = modifier
            .padding(horizontal = 36.dp, vertical = 32.dp)
            .fillMaxWidth()
            .height(200.dp),
        barData = barData,
        onBarClick = {},
        color = MaterialTheme.colorScheme.secondary,
        axisConfig = AxisConfig(
            xAxisColor = MaterialTheme.colorScheme.onSurfaceVariant,
            showAxis = true,
            isAxisDashed = true,
            showUnitLabels = true,
            showXLabels = true,
            yAxisColor = MaterialTheme.colorScheme.onSurfaceVariant,
            textColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Composable
internal fun ExecutedAnalyticsChart(
    modifier: Modifier = Modifier,
    workLoadMap: WorkLoadMapUi,
    period: TimePeriod,
) {
    val lineData = mutableListOf<LineData>().apply {
        workLoadMap.forEach { (timeRange, timeTasks) ->
            val xValue = when (period == TimePeriod.YEAR || period == TimePeriod.HALF_YEAR) {
                true -> timeRange.toMonthTitle()
                false -> timeRange.toDaysTitle()
            }
            val allTimeTasks = timeTasks.size.let { if (it == 0) 1 else it }
            val yValue = timeTasks.count { it.isCompleted } / allTimeTasks.toFloat()
            add(LineData(xValue, yValue.toBigDecimal().setScale(1, RoundingMode.UP).toFloat() * 100f))
        }
    }
    LineChart(
        modifier = modifier.height(200.dp).fillMaxWidth().padding(horizontal = 36.dp, vertical = 32.dp),
        lineData = lineData,
        color = MaterialTheme.colorScheme.secondary,
        axisConfig = AxisConfig(
            xAxisColor = MaterialTheme.colorScheme.secondary,
            showAxis = true,
            isAxisDashed = false,
            showUnitLabels = true,
            showXLabels = true,
            yAxisColor = MaterialTheme.colorScheme.secondary,
            textColor = MaterialTheme.colorScheme.onSurface,
        ),
        lineConfig = LineConfig(
            hasSmoothCurve = true,
            hasDotMarker = true,
        ),
    )
}

@Composable
internal fun StatisticInfoView(
    modifier: Modifier = Modifier,
    icon: Int,
    name: String,
    value: String,
) {
    Surface(
        modifier = modifier.height(144.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = name,
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}
