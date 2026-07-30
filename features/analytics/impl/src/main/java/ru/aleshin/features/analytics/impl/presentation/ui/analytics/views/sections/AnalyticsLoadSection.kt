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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.functional.Constants.Date.MILLIS_IN_HOUR
import ru.aleshin.core.utils.functional.Constants.Date.MILLIS_IN_MINUTE
import ru.aleshin.core.utils.functional.Constants.Date.MINUTES_IN_HOUR
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsBucketGranularity
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsChartPointUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsLoadDistributionUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsDualAxisChart
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsBucketDatePattern
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.formatAnalyticsCivilDate
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsBucketFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import kotlin.math.roundToLong

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun AnalyticsLoadSection(
    modifier: Modifier = Modifier,
    loadAnalytics: AnalyticsLoadDistributionUi,
    selectedKey: Long?,
    onSelect: (Long) -> Unit,
) {
    val language = TimePlannerRes.language
    val locale = language.fetchAnalyticsLocale()
    val formatter = rememberAnalyticsValueFormatter()
    val bucketFormatter = rememberAnalyticsBucketFormatter()
    val includeYear = remember(loadAnalytics) {
        fetchAnalyticsBucketDatePattern(
            dates = loadAnalytics.buckets.flatMap { bucket -> listOf(bucket.from, bucket.to) },
        ).contains(YEAR_PATTERN)
    }
    val tooltipDatePattern = remember(includeYear) {
        if (includeYear) {
            FULL_TOOLTIP_DATE_PATTERN
        } else {
            TOOLTIP_DATE_PATTERN
        }
    }
    val points = remember(loadAnalytics, locale, includeYear, tooltipDatePattern, bucketFormatter) {
        loadAnalytics.buckets.map { bucket ->
            val label = bucketFormatter.format(
                from = bucket.from,
                to = bucket.to,
                granularity = loadAnalytics.granularity,
                locale = locale,
                includeYear = includeYear,
            )
            AnalyticsChartPointUi(
                key = bucket.from.time,
                label = label,
                primaryValue = bucket.durationMillis / MILLIS_IN_HOUR.toFloat(),
                secondaryValue = bucket.taskCount.toFloat(),
                primaryRawValue = bucket.durationMillis,
                secondaryRawValue = bucket.taskCount.toLong(),
                tooltipLabel = if (loadAnalytics.granularity == AnalyticsBucketGranularity.DAY) {
                    bucket.from.formatAnalyticsCivilDate(
                        pattern = tooltipDatePattern,
                        locale = locale,
                    )
                } else {
                    label
                },
            )
        }
    }

    AnalyticsSection(
        modifier = modifier,
        title = AnalyticsThemeRes.strings.loadTitle,
        verticalSpacing = 12.dp,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = AnalyticsThemeRes.strings.timeLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.tertiary, CircleShape),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = AnalyticsThemeRes.strings.tasksLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        val strings = AnalyticsThemeRes.strings
        AnalyticsDualAxisChart(
            points = points,
            tooltipFormatter = { point ->
                strings.loadTooltipFormat.format(
                    point.tooltipLabel,
                    formatter.formatDuration(
                        durationMillis = checkNotNull(point.primaryRawValue),
                        hourSymbol = strings.hourShort,
                        minuteSymbol = strings.minuteShort,
                    ),
                    strings.tasksFormat.format(checkNotNull(point.secondaryRawValue)),
                )
            },
            primaryAxisLabel = { hours ->
                formatter.formatDuration(
                    durationMillis = (hours * MINUTES_IN_HOUR.toFloat()).roundToLong() * MILLIS_IN_MINUTE,
                    hourSymbol = strings.hourShort,
                    minuteSymbol = strings.minuteShort,
                )
            },
            selectedKey = selectedKey,
            onSelect = onSelect,
        )
        loadAnalytics.buckets.find { it.from.time == selectedKey }?.let { bucket ->
            val bucketTitle = if (loadAnalytics.granularity == AnalyticsBucketGranularity.DAY) {
                bucket.from.formatAnalyticsCivilDate(
                    pattern = tooltipDatePattern,
                    locale = locale,
                )
            } else {
                bucketFormatter.format(
                    from = bucket.from,
                    to = bucket.to,
                    granularity = loadAnalytics.granularity,
                    locale = locale,
                    includeYear = includeYear,
                )
            }
            Text(
                text = strings.loadTooltipFormat.format(
                    bucketTitle,
                    formatter.formatDuration(
                        durationMillis = bucket.durationMillis,
                        hourSymbol = strings.hourShort,
                        minuteSymbol = strings.minuteShort,
                    ),
                    strings.tasksFormat.format(bucket.taskCount),
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private const val YEAR_PATTERN = "yyyy"
private const val TOOLTIP_DATE_PATTERN = "EEE, d MMM"
private const val FULL_TOOLTIP_DATE_PATTERN = "EEE, d MMM yyyy"
