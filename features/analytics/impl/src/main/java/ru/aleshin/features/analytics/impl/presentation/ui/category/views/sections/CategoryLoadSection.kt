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
package ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.charts.CategoryColorsDefaults
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsLineSeriesUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryLoadBucketUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryLoadDistributionUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsLineChart
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsValueFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsBucketDatePattern
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.formatAnalyticsCivilDate
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsBucketFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsRangeFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import java.util.Locale
import kotlin.math.roundToLong

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
@Composable
internal fun CategoryLoadSection(
    modifier: Modifier = Modifier,
    categoryId: Long,
    categoryName: String,
    load: CategoryLoadDistributionUi,
    range: AnalyticsRangeUi,
    selectedBucketIndex: Int?,
    onSelect: (Int?) -> Unit,
) {
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }
    val rangeFormatter = rememberAnalyticsRangeFormatter()
    val rangeTitle = remember(range, locale, rangeFormatter) {
        rangeFormatter.format(range = range, locale = locale)
    }
    val categoryColor = CategoryColorsDefaults.fetchColor(categoryId = categoryId)
    val allPlanColor = MaterialTheme.colorScheme.onSurfaceVariant
    val formatter = rememberAnalyticsValueFormatter()
    val bucketFormatter = rememberAnalyticsBucketFormatter()
    val series = remember(load, categoryName, strings.allPlan) {
        listOf(
            AnalyticsLineSeriesUi(
                label = categoryName,
                values = load.buckets.map { bucket ->
                    bucket.categoryDurationMillis / Constants.Date.MILLIS_IN_HOUR.toFloat()
                },
            ),
            AnalyticsLineSeriesUi(
                label = strings.allPlan,
                values = load.buckets.map { bucket ->
                    bucket.allPlanDurationMillis / Constants.Date.MILLIS_IN_HOUR.toFloat()
                },
            ),
        )
    }
    val includeYear = remember(load) {
        fetchAnalyticsBucketDatePattern(
            dates = load.buckets.flatMap { bucket -> listOf(bucket.from, bucket.to) },
        ).contains(YEAR_PATTERN)
    }
    val labels = remember(load, locale, includeYear, bucketFormatter) {
        load.buckets.map { bucket ->
            bucketFormatter.format(
                from = bucket.from,
                to = bucket.to,
                granularity = load.granularity,
                locale = locale,
                includeYear = includeYear,
            )
        }
    }
    val colors = remember(categoryColor, allPlanColor) {
        listOf(categoryColor, allPlanColor)
    }
    val formatBucket = remember(
        categoryName,
        locale,
        strings,
        formatter,
        load.granularity,
        includeYear,
        bucketFormatter,
    ) {
        { bucket: CategoryLoadBucketUi ->
            val bucketTitle = bucketFormatter.format(
                from = bucket.from,
                to = bucket.to,
                granularity = load.granularity,
                locale = locale,
                includeYear = includeYear,
            )
            strings.categoryLoadTooltipFormat.format(
                bucketTitle,
                categoryName,
                formatter.formatDuration(
                    durationMillis = bucket.categoryDurationMillis,
                    hourSymbol = strings.hourShort,
                    minuteSymbol = strings.minuteShort,
                ),
                formatter.formatDuration(
                    durationMillis = bucket.allPlanDurationMillis,
                    hourSymbol = strings.hourShort,
                    minuteSymbol = strings.minuteShort,
                ),
            )
        }
    }

    CategorySection(
        title = strings.categoryLoadTitle,
        modifier = modifier,
    ) {
        CategoryLoadLegend(
            categoryName = categoryName,
            categoryColor = categoryColor,
            allPlanColor = allPlanColor,
            strings = strings,
        )
        AnalyticsLineChart(
            series = series,
            labels = labels,
            colors = colors,
            summary = "${strings.categoryLoadTitle}, $rangeTitle",
            pointDescription = { index ->
                load.buckets.getOrNull(index)?.let(formatBucket).orEmpty()
            },
            axisValueLabel = { hours ->
                formatter.formatDuration(
                    durationMillis = (
                        hours * Constants.Date.MINUTES_IN_HOUR.toFloat()
                    ).roundToLong() * Constants.Date.MILLIS_IN_MINUTE,
                    hourSymbol = strings.hourShort,
                    minuteSymbol = strings.minuteShort,
                )
            },
            selectedIndex = selectedBucketIndex,
            onSelect = onSelect,
        )
        selectedBucketIndex?.let(load.buckets::getOrNull)?.let { bucket ->
            Text(
                text = formatBucket(bucket),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        CategoryBusiestDay(
            load = load,
            locale = locale,
            formatter = formatter,
            strings = strings,
        )
    }
}

@Composable
private fun CategoryLoadLegend(
    categoryName: String,
    categoryColor: androidx.compose.ui.graphics.Color,
    allPlanColor: androidx.compose.ui.graphics.Color,
    strings: AnalyticsStrings,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = categoryColor,
                    shape = CircleShape,
                ),
        )
        Text(
            text = categoryName,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(2.dp)
                .background(
                    color = allPlanColor,
                    shape = RoundedCornerShape(2.dp),
                ),
        )
        Text(
            text = strings.allPlan,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CategoryBusiestDay(
    load: CategoryLoadDistributionUi,
    locale: Locale,
    formatter: AnalyticsValueFormatter,
    strings: AnalyticsStrings,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = strings.busiestDay,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = load.busiestDay?.let {
                strings.busiestDayValueFormat.format(
                    it.formatAnalyticsCivilDate(
                        pattern = BUSIEST_DAY_PATTERN,
                        locale = locale,
                    ),
                    formatter.formatDuration(
                        durationMillis = load.busiestDayDurationMillis,
                        hourSymbol = strings.hourShort,
                        minuteSymbol = strings.minuteShort,
                    ),
                )
            } ?: strings.unavailableValue,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

private const val BUSIEST_DAY_PATTERN = "EEE, d MMM"
private const val YEAR_PATTERN = "yyyy"
