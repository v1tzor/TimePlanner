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
 * imitations under the License.
 */
package ru.aleshin.features.analytics.impl.presenatiton.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.axis.AxisConfig
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.toMinutesAndHoursTitle
import ru.aleshin.core.utils.extensions.toDaysTitle
import ru.aleshin.core.utils.extensions.toMonthTitle
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.domain.entities.WorkLoadMap
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsViewState
import ru.aleshin.features.analytics.impl.presenatiton.ui.views.TimeSelectorAndRefresh

/**
 * @author Stanislav Aleshin on 20.04.2023.
 */
@Composable
internal fun WorkLoadTab(
    state: AnalyticsViewState,
    onTimePeriodChanged: (TimePeriod) -> Unit,
    onRefresh: () -> Unit,
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        TimeSelectorAndRefresh(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 24.dp, bottom = 6.dp),
            timePeriod = state.timePeriod,
            title = AnalyticsThemeRes.strings.timeSelectorTitle,
            isRefresh = state.scheduleAnalytics?.categoriesAnalytics == null,
            onTimePeriodChanged = onTimePeriodChanged,
            onRefresh = onRefresh,
        )
        Divider(Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
        val analytics = state.scheduleAnalytics
        if (analytics != null) {
            WorkLoadAnalyticsChart(
                workLoadMap = analytics.dateWorkLoadMap,
                period = checkNotNull(state.timePeriod),
            )
            Divider(Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StatisticInfoView(
                    icon = AnalyticsThemeRes.icons.numberedList,
                    name = AnalyticsThemeRes.strings.totalCountTaskTitle,
                    value = analytics.totalTasksCount.toString(),
                )
                StatisticInfoView(
                    icon = AnalyticsThemeRes.icons.numericOneCircle,
                    name = AnalyticsThemeRes.strings.averageCountTaskTitle,
                    value = analytics.averageDayLoad.toString(),
                )
                StatisticInfoView(
                    icon = AnalyticsThemeRes.icons.timeComplete,
                    name = AnalyticsThemeRes.strings.totalTimeTaskTitle,
                    value = analytics.totalTasksTime.toMinutesAndHoursTitle(),
                )
                StatisticInfoView(
                    icon = AnalyticsThemeRes.icons.timeCheck,
                    name = AnalyticsThemeRes.strings.averageTimeTaskTitle,
                    value = analytics.averageTaskTime.toMinutesAndHoursTitle(),
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
internal fun WorkLoadAnalyticsChart(
    modifier: Modifier = Modifier,
    workLoadMap: WorkLoadMap,
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
    Surface(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Column(modifier = Modifier) {
            Text(
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp, start = 16.dp),
                text = AnalyticsThemeRes.strings.planningStatisticsTitle,
                style = MaterialTheme.typography.titleSmall,
            )
            BarChart(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp)
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
    }
}

@Composable
internal fun StatisticInfoView(
    modifier: Modifier = Modifier,
    icon: Int,
    name: String,
    value: String,
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Row(
            modifier = Modifier.fillMaxHeight().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = name,
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
