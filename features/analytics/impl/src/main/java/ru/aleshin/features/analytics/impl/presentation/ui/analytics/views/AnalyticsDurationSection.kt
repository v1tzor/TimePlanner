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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparison
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparisonState
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsDurationBucketType
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsChartPointUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsDurationDistributionUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsBarChart
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsValueFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import java.util.Locale

@Composable
internal fun AnalyticsDurationSection(
    modifier: Modifier = Modifier,
    durationsAnalytics: AnalyticsDurationDistributionUi,
) {
    val formatter = rememberAnalyticsValueFormatter()
    val colors = analyticsDurationColors()
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }

    val points = remember(durationsAnalytics, strings) {
        durationsAnalytics.buckets.map { bucket ->
            AnalyticsChartPointUi(
                key = bucket.type.ordinal.toLong(),
                label = bucket.type.title(strings = strings),
                primaryValue = (bucket.share * 100).toFloat(),
            )
        }
    }

    AnalyticsSection(
        title = strings.durationStructureTitle,
        modifier = modifier,
    ) {
        AnalyticsBarChart(
            points = points,
            colors = colors,
            pointDescription = { point ->
                "${point.label}: ${
                    formatter.formatPercent(
                        value = point.primaryValue / 100.0,
                        locale = locale,
                    )
                }"
            },
            valueLabel = { point ->
                formatter.formatPercent(
                    value = point.primaryValue / 100.0,
                    locale = locale,
                )
            },
            axisValueLabel = { value ->
                formatter.formatPercent(
                    value = value / 100.0,
                    locale = locale,
                )
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnalyticsDurationMetricCell(
                modifier = Modifier.weight(1f),
                value = durationsAnalytics.averageDurationMillis?.let {
                    formatter.formatDuration(
                        durationMillis = it,
                        hourSymbol = strings.hourShort,
                        minuteSymbol = strings.minuteShort,
                    )
                } ?: strings.unavailableValue,
                label = strings.averageDuration,
                comparison = durationsAnalytics.averageComparison,
                formatter = formatter,
                locale = locale,
                strings = strings,
            )
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            AnalyticsDurationMetricCell(
                modifier = Modifier.weight(1f),
                value = durationsAnalytics.medianDurationMillis?.let {
                    formatter.formatDuration(
                        durationMillis = it,
                        hourSymbol = strings.hourShort,
                        minuteSymbol = strings.minuteShort,
                    )
                } ?: strings.unavailableValue,
                label = strings.medianDuration,
                comparison = durationsAnalytics.medianComparison,
                formatter = formatter,
                locale = locale,
                strings = strings,
            )
        }
    }
}

@Composable
private fun AnalyticsDurationMetricCell(
    value: String,
    label: String,
    comparison: AnalyticsComparison,
    formatter: AnalyticsValueFormatter,
    locale: Locale,
    strings: AnalyticsStrings,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        if (comparison.state != AnalyticsComparisonState.UNAVAILABLE) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Text(
                    text = buildString {
                        append(
                            comparison.formatAnalyticsComparison(
                                formatter = formatter,
                                locale = locale,
                                strings = strings,
                            ),
                        )
                        if (comparison.state == AnalyticsComparisonState.VALUE) {
                            append(' ')
                            append(strings.previousPeriod)
                        }
                    },
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp,
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun analyticsDurationColors() = listOf(
    MaterialTheme.colorScheme.secondary,
    MaterialTheme.colorScheme.primary,
    MaterialTheme.colorScheme.tertiary,
)

private fun AnalyticsDurationBucketType.title(
    strings: AnalyticsStrings,
) = when (this) {
    AnalyticsDurationBucketType.SHORT -> strings.shortTasks
    AnalyticsDurationBucketType.MEDIUM -> strings.mediumTasks
    AnalyticsDurationBucketType.LONG -> strings.longTasks
}
