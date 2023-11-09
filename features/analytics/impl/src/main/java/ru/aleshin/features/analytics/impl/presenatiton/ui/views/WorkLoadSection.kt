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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.common.axis.AxisConfig
import ru.aleshin.core.utils.extensions.toDaysTitle
import ru.aleshin.core.utils.extensions.toMonthTitle
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presenatiton.models.analytics.WorkLoadMapUi
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes

/**
 * @author Stanislav Aleshin on 27.10.2023.
 */
@Composable
internal fun WorkLoadSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    timePeriod: TimePeriod?,
    workLoadMap: WorkLoadMapUi?,
    onTimePeriodChanged: (TimePeriod) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TimeSelectorSection(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            timePeriod = timePeriod,
            title = AnalyticsThemeRes.strings.workLoadAnalyticsTitle,
            onTimePeriodChanged = onTimePeriodChanged,
        )
        AnimatedContent(
            targetState = isLoading,
            label = "Workload analytics",
            transitionSpec = {
                fadeIn(animationSpec = tween(220, delayMillis = 90)).togetherWith(
                    fadeOut(animationSpec = tween(90)),
                )
            },
        ) { loading ->
            if (!loading && workLoadMap != null && timePeriod != null) {
                WorkLoadAnalyticsChart(
                    workLoadMap = workLoadMap,
                    period = timePeriod,
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
