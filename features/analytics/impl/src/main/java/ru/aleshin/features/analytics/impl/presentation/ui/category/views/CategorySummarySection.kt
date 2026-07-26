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
package ru.aleshin.features.analytics.impl.presentation.ui.category.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.charts.CategoryColorsDefaults
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparisonState
import ru.aleshin.features.analytics.impl.presentation.models.category.CategorySummaryUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsComparisonLabel
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsSectionTitle
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
internal fun CategorySummarySection(
    categoryId: Long,
    summary: CategorySummaryUi,
    modifier: Modifier = Modifier,
) {
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }
    val formatter = rememberAnalyticsValueFormatter()

    AnalyticsSurfaceCard(
        modifier = modifier,
        verticalSpacing = 0.dp,
    ) {
        AnalyticsSectionTitle(title = strings.categorySummaryTitle)
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = formatter.formatDuration(
                    durationMillis = summary.durationMillis,
                    hourSymbol = strings.hourShort,
                    minuteSymbol = strings.minuteShort,
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = strings.categoryShareFormat.format(
                    formatter.formatPercent(
                        value = summary.share,
                        locale = locale,
                    )
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { summary.share.toFloat().coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(MaterialTheme.shapes.extraLarge),
            color = CategoryColorsDefaults.fetchColor(categoryId = categoryId),
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        Spacer(modifier = Modifier.height(12.dp))
        AnalyticsComparisonLabel(
            value = summary.formatComparison(
                formatter = formatter,
                locale = locale,
                strings = strings,
            ),
            directionUp = summary.comparison.changePercent
                ?.takeIf { it != 0.0 }
                ?.let { it > 0.0 },
        )
    }
}

private fun CategorySummaryUi.formatComparison(
    formatter: AnalyticsValueFormatter,
    locale: Locale,
    strings: AnalyticsStrings,
) = when (comparison.state) {
    AnalyticsComparisonState.VALUE -> {
        val change = comparison.changePercent ?: 0.0
        when {
            change > 0.0 -> strings.categoryTimeIncreaseFormat.format(
                formatter.formatPercent(
                    value = change,
                    locale = locale,
                )
            )
            change < 0.0 -> strings.categoryTimeDecreaseFormat.format(
                formatter.formatPercent(
                    value = abs(change),
                    locale = locale,
                )
            )
            else -> strings.comparisonUnchanged
        }
    }
    AnalyticsComparisonState.UNCHANGED -> strings.comparisonUnchanged
    AnalyticsComparisonState.PREVIOUS_ZERO -> strings.categoryPreviousUnused
    AnalyticsComparisonState.UNAVAILABLE -> strings.comparisonUnavailable
}
