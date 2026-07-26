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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsKeyMetricsUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.formatAnalyticsCivilDate
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun AnalyticsKeyMetricsSection(
    modifier: Modifier = Modifier,
    metrics: AnalyticsKeyMetricsUi,
) {
    val formatter = rememberAnalyticsValueFormatter()
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }

    AnalyticsSection(
        title = AnalyticsThemeRes.strings.keyMetricsTitle,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        verticalSpacing = 0.dp,
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(150.dp)) {
            MainMetricCell(
                iconResource = TimePlannerRes.icons.plannedTask,
                value = formatter.formatDuration(
                    durationMillis = metrics.importantDurationMillis,
                    hourSymbol = strings.hourShort,
                    minuteSymbol = strings.minuteShort,
                ),
                label = "${strings.important} · ${
                    formatter.formatPercent(
                        value = metrics.importantShare,
                        locale = locale,
                    )
                }",
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            MainMetricCell(
                iconResource = TimePlannerRes.icons.schedulerIcon,
                value = formatter.formatDuration(
                    durationMillis = metrics.weekendDurationMillis,
                    hourSymbol = strings.hourShort,
                    minuteSymbol = strings.minuteShort,
                ),
                label = "${strings.weekends} · ${
                    formatter.formatPercent(
                        value = metrics.weekendShare,
                        locale = locale,
                    )
                }",
                modifier = Modifier.weight(1f),
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(modifier = Modifier.fillMaxWidth().height(155.dp)) {
            MainMetricCell(
                iconResource = TimePlannerRes.icons.time,
                value = metrics.longestBlock?.let { range ->
                    formatter.formatDuration(
                        durationMillis = (range.to.time - range.from.time).coerceAtLeast(0L),
                        hourSymbol = strings.hourShort,
                        minuteSymbol = strings.minuteShort,
                    )
                } ?: strings.unavailableValue,
                label = strings.longestBlock,
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            MainMetricCell(
                iconResource = TimePlannerRes.icons.analyticsTab,
                value = metrics.busiestDay?.formatAnalyticsCivilDate(
                    pattern = BUSIEST_DAY_PATTERN,
                    locale = locale,
                ) ?: strings.unavailableValue,
                label = "${strings.busiestDay} · ${
                    formatter.formatDuration(
                        durationMillis = metrics.busiestDayDurationMillis,
                        hourSymbol = strings.hourShort,
                        minuteSymbol = strings.minuteShort,
                    )
                }",
                modifier = Modifier.weight(1f),
                valueColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun MainMetricCell(
    iconResource: Int,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.primary,
) {
    Column(
        modifier = modifier.heightIn(min = 136.dp).padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconResource),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            maxLines = 2,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private const val BUSIEST_DAY_PATTERN = "EEE, d MMM"
