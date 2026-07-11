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
package ru.aleshin.features.analytics.impl.presenatiton.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.axis.AxisConfig
import ru.aleshin.timeplanner.core.ui.views.toMinutesOrHoursTitle
import ru.aleshin.core.utils.extensions.toMinutes
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presenatiton.models.analytics.HourlyWorkLoadAnalyticUi
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes

/**
 * @author Stanislav Aleshin on 03.07.2026.
 */
@Composable
internal fun HourlyWorkLoadSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    timePeriod: TimePeriod?,
    hourlyWorkLoadAnalytics: List<HourlyWorkLoadAnalyticUi>?,
    onTimePeriodChanged: (TimePeriod) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val peakAnalytic = remember(hourlyWorkLoadAnalytics) {
            hourlyWorkLoadAnalytics?.maxByOrNull { it.duration }?.takeIf { it.duration > 0L }
        }
        TimeSelectorSection(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            timePeriod = timePeriod,
            title = AnalyticsThemeRes.strings.hourlyWorkLoadAnalyticsTitle,
            subTitle = if (peakAnalytic != null) {
                val peakDuration = peakAnalytic.duration.toMinutesOrHoursTitle()
                val peakTime = remember(peakAnalytic.fromHour, peakAnalytic.toHour) {
                    peakAnalytic.formatHourRangeTitle()
                }
                AnalyticsThemeRes.strings.hourlyWorkLoadPeakFormat.format(peakTime, peakDuration)
            } else {
                null
            },
            onTimePeriodChanged = onTimePeriodChanged,
        )
        AnimatedContent(
            targetState = isLoading,
            label = "Hourly workload analytics",
            transitionSpec = {
                fadeIn(animationSpec = tween(220, delayMillis = 90)).togetherWith(
                    fadeOut(animationSpec = tween(90)),
                )
            },
        ) { loading ->
            if (!loading && hourlyWorkLoadAnalytics != null) {
                HourlyWorkLoadAnalyticsChart(
                    hourlyWorkLoadAnalytics = hourlyWorkLoadAnalytics
                )
            } else {
                Surface(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.background,
                    content = { Box(Modifier.fillMaxWidth().height(264.dp)) },
                )
            }
        }
    }
}

@Composable
internal fun HourlyWorkLoadAnalyticsChart(
    modifier: Modifier = Modifier,
    hourlyWorkLoadAnalytics: List<HourlyWorkLoadAnalyticUi>,
) {
    val barData = remember(hourlyWorkLoadAnalytics) {
        hourlyWorkLoadAnalytics.map { analytics ->
            BarData(
                xValue = analytics.formatHourTitle(),
                yValue = analytics.duration.toMinutes() / MINUTES_IN_HOUR,
            )
        }
    }
    val maxDuration = remember(barData) { barData.maxOfOrNull { it.yValue } ?: 0f }
    val hasData = remember(maxDuration) { maxDuration > 0f }
    val chartBarData = remember(barData, hasData) {
        if (hasData) barData else barData.map { BarData(it.xValue, ZERO_BAR_VALUE) }
    }

    BarChart(
        modifier = modifier
            .padding(horizontal = 36.dp, vertical = 32.dp)
            .fillMaxWidth()
            .height(200.dp),
        barData = chartBarData,
        onBarClick = {},
        color = MaterialTheme.colorScheme.primary.copy(alpha = if (hasData) 1f else 0f),
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

private fun Int.formatHourTitle(): String {
    return "%02d".format(this)
}

private fun HourlyWorkLoadAnalyticUi.formatHourTitle(): String {
    return "${fromHour.formatHourTitle()}-${toHour.formatHourTitle()}"
}

private fun HourlyWorkLoadAnalyticUi.formatHourRangeTitle(): String {
    return "${fromHour.formatHourTitle()}:00-${toHour.formatHourTitle()}:00"
}

private const val MINUTES_IN_HOUR = 60f
private const val ZERO_BAR_VALUE = 0.0001f
