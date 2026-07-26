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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparison
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparisonState
import ru.aleshin.features.analytics.impl.presentation.mappers.toSummaryTitle
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsSummaryUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsComparisonLabel
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsMetricCell
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsSurfaceCard
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsValueFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import java.util.Locale
import kotlin.math.abs

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
@Composable
internal fun AnalyticsSummarySection(
    modifier: Modifier = Modifier,
    selectedPeriod: TimePeriod,
    summary: AnalyticsSummaryUi
) {
    val formatter = rememberAnalyticsValueFormatter()
    val language = TimePlannerRes.language
    val strings = AnalyticsThemeRes.strings
    val locale = remember(language) { language.fetchAnalyticsLocale() }

    AnalyticsSurfaceCard(modifier = modifier) {
        Text(
            text = selectedPeriod.toSummaryTitle(strings = strings),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AnalyticsMetricCell(
                    modifier = Modifier.weight(1f),
                    value = formatter.formatDuration(
                        durationMillis = summary.plannedDurationMillis,
                        hourSymbol = strings.hourShort,
                        minuteSymbol = strings.minuteShort,
                    ),
                    label = strings.planned,
                    isValueAccent = true,
                )
                AnalyticsMetricCell(
                    value = strings.completedCountFormat.format(summary.completedTaskCount, summary.allTaskCount),
                    label = strings.completed,
                    horizontalAlignment = Alignment.End
                )
            }
            AnalyticsDurationTrack(
                summary = summary
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AnalyticsLegendRow(
                    label = strings.completed,
                    duration = summary.completedDurationMillis,
                    color = MaterialTheme.colorScheme.primary,
                    formatter = formatter,
                    strings = strings,
                )
                AnalyticsLegendRow(
                    label = strings.skipped,
                    duration = summary.skippedDurationMillis,
                    color = MaterialTheme.colorScheme.error,
                    formatter = formatter,
                    strings = strings,
                )
                AnalyticsLegendRow(
                    label = strings.unfinished,
                    duration = summary.unfinishedDurationMillis,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    formatter = formatter,
                    strings = strings,
                )
            }
        }
        AnalyticsComparisonLabel(
            value = summary.completionComparison.formatCompletion(
                formatter = formatter,
                locale = locale,
                strings = strings,
            ),
            isPositive = summary.completionComparison.changePercent?.takeIf { changePercent ->
                summary.completionComparison.state == AnalyticsComparisonState.VALUE && changePercent != 0.0
            }?.let { changePercent ->
                changePercent > 0.0
            },
        )
    }
}

@Composable
private fun AnalyticsDurationTrack(
    modifier: Modifier = Modifier,
    summary: AnalyticsSummaryUi,
) {
    val totalDuration = summary.plannedDurationMillis

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(CircleShape),
    ) {
        if (totalDuration <= 0L) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            )
        } else {
            if (summary.completedDurationMillis > 0L) {
                Box(
                    modifier = Modifier
                        .weight(summary.completedDurationMillis.toFloat())
                        .height(12.dp)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
            if (summary.skippedDurationMillis > 0L) {
                Box(
                    modifier = Modifier
                        .weight(summary.skippedDurationMillis.toFloat())
                        .height(12.dp)
                        .background(MaterialTheme.colorScheme.error),
                )
            }
            if (summary.unfinishedDurationMillis > 0L) {
                Box(
                    modifier = Modifier
                        .weight(summary.unfinishedDurationMillis.toFloat())
                        .height(12.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                )
            }
        }
    }
}

@Composable
private fun AnalyticsLegendRow(
    modifier: Modifier = Modifier,
    label: String,
    duration: Long,
    color: Color,
    formatter: AnalyticsValueFormatter,
    strings: AnalyticsStrings,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = formatter.formatDuration(
                durationMillis = duration,
                hourSymbol = strings.hourShort,
                minuteSymbol = strings.minuteShort,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun AnalyticsComparison.formatCompletion(
    formatter: AnalyticsValueFormatter,
    locale: Locale,
    strings: AnalyticsStrings,
) = when (state) {
    AnalyticsComparisonState.VALUE -> {
        val change = changePercent ?: 0.0
        val value = formatter.formatPercent(
            value = abs(change),
            locale = locale,
        )
        if (change > 0.0) {
            strings.completionIncreaseFormat.format(value)
        } else {
            strings.completionDecreaseFormat.format(value)
        }
    }
    AnalyticsComparisonState.UNCHANGED -> strings.comparisonUnchanged
    AnalyticsComparisonState.PREVIOUS_ZERO -> strings.completionPreviousZero
    AnalyticsComparisonState.UNAVAILABLE -> strings.comparisonUnavailable
}
