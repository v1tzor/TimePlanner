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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexAlignSelf
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparisonState
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryKeyMetricsUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsComparisonLabel
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
internal fun CategoryMetricsSection(
    modifier: Modifier = Modifier,
    metrics: CategoryKeyMetricsUi,
) {
    CategorySection(
        modifier = modifier,
        title = AnalyticsThemeRes.strings.keyMetricsTitle,
        contentPadding = PaddingValues(0.dp),
        verticalSpacing = 0.dp,
    ) {
        CategoryMetricsContent(
            metrics = metrics,
            isExpandedLayout = false,
        )
    }
}

@Composable
internal fun CategoryExpandedMetricsSection(
    modifier: Modifier = Modifier,
    metrics: CategoryKeyMetricsUi,
) {
    CategoryExpandedSection(
        modifier = modifier,
        title = AnalyticsThemeRes.strings.keyMetricsTitle,
        contentPadding = PaddingValues(0.dp),
        verticalSpacing = 0.dp,
    ) {
        CategoryMetricsContent(
            metrics = metrics,
            isExpandedLayout = true,
        )
    }
}

@Composable
private fun ColumnScope.CategoryMetricsContent(
    metrics: CategoryKeyMetricsUi,
    isExpandedLayout: Boolean,
) {
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }
    val formatter = rememberAnalyticsValueFormatter()
    val rowModifier = if (isExpandedLayout) {
        Modifier
            .fillMaxWidth()
            .weight(1f)
    } else {
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    }
    val minimumCellHeight = if (isExpandedLayout) Dp.Unspecified else 160.dp

    Row(modifier = rowModifier) {
        CategoryMetricCell(
            modifier = Modifier.weight(1f),
            minimumHeight = minimumCellHeight,
            useHorizontalDetails = isExpandedLayout,
            iconResource = TimePlannerRes.icons.plannedTask,
            value = metrics.taskCount.toString(),
            label = strings.tasksMetric,
            comparison = metrics.taskCountDelta.formatCountDelta(strings = strings),
        )
        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        CategoryMetricCell(
            modifier = Modifier.weight(1f),
            minimumHeight = minimumCellHeight,
            useHorizontalDetails = isExpandedLayout,
            iconResource = TimePlannerRes.icons.time,
            value = metrics.averageDurationMillis?.let {
                formatter.formatDuration(
                    durationMillis = it,
                    hourSymbol = strings.hourShort,
                    minuteSymbol = strings.minuteShort,
                )
            } ?: strings.unavailableValue,
            label = strings.averageDuration,
            comparison = metrics.averageDurationDeltaMillis?.formatDurationDelta(
                formatter = formatter,
                strings = strings,
            ),
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Row(modifier = rowModifier) {
        CategoryMetricCell(
            modifier = Modifier.weight(1f),
            minimumHeight = minimumCellHeight,
            useHorizontalDetails = isExpandedLayout,
            iconResource = TimePlannerRes.icons.check,
            value = strings.completedCountFormat.format(
                metrics.completedTaskCount,
                metrics.allTaskCount,
            ),
            label = strings.completed,
            comparison = metrics.completedCountDelta.formatCompletedDelta(strings = strings),
        )
        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        CategoryMetricCell(
            modifier = Modifier.weight(1f),
            minimumHeight = minimumCellHeight,
            useHorizontalDetails = isExpandedLayout,
            iconResource = TimePlannerRes.icons.analyticsTab,
            value = formatter.formatPercent(
                value = metrics.completionShare,
                locale = locale,
            ),
            label = strings.completionShare,
            comparison = metrics.formatCompletionComparison(
                formatter = formatter,
                locale = locale,
                strings = strings,
            ),
            isPositive = metrics.completionComparison.changePercent
                ?.takeIf {
                    metrics.completionComparison.state == AnalyticsComparisonState.VALUE && it != 0.0
                }
                ?.let { it > 0.0 },
        )
    }
}

@Composable
@OptIn(ExperimentalFlexBoxApi::class)
private fun CategoryMetricCell(
    modifier: Modifier = Modifier,
    minimumHeight: Dp,
    useHorizontalDetails: Boolean,
    iconResource: Int,
    value: String,
    label: String,
    comparison: String?,
    isPositive: Boolean? = null,
) {
    Column(
        modifier = modifier
            .heightIn(min = minimumHeight)
            .padding(16.dp),
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
                painter = painterResource(id = iconResource),
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
            maxLines = 2,
        )
        Spacer(modifier = Modifier.height(4.dp))
        FlexBox(
            config = {
                direction(if (useHorizontalDetails) FlexDirection.Row else FlexDirection.Column)
                gap(8.dp)
                wrap(FlexWrap.Wrap)
            }
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
            comparison?.let {
                AnalyticsComparisonLabel(
                    modifier = Modifier.flex {
                        if (useHorizontalDetails) alignSelf(FlexAlignSelf.Center)
                    },
                    value = it,
                    isPositive = isPositive,
                )
            }
        }
    }
}

private fun CategoryKeyMetricsUi.formatCompletionComparison(
    formatter: AnalyticsValueFormatter,
    locale: Locale,
    strings: AnalyticsStrings,
) = when (completionComparison.state) {
    AnalyticsComparisonState.PREVIOUS_ZERO -> strings.completionPreviousZero
    AnalyticsComparisonState.UNAVAILABLE -> null
    AnalyticsComparisonState.UNCHANGED -> strings.comparisonUnchanged
    AnalyticsComparisonState.VALUE -> completionComparison.changePercent?.let {
        val value = formatter.formatPercent(
            value = abs(it),
            locale = locale,
        )
        if (it >= 0.0) {
            strings.completionIncreaseFormat.format(value)
        } else {
            strings.completionDecreaseFormat.format(value)
        }
    }
}

private fun Long.formatDurationDelta(
    formatter: AnalyticsValueFormatter,
    strings: AnalyticsStrings,
): String {
    val value = formatter.formatDuration(
        durationMillis = abs(this),
        hourSymbol = strings.hourShort,
        minuteSymbol = strings.minuteShort,
    )
    return when {
        this > 0L -> strings.metricDurationIncreaseFormat.format(value)
        this < 0L -> strings.metricDurationDecreaseFormat.format(value)
        else -> strings.comparisonUnchanged
    }
}

private fun Int.formatCountDelta(
    strings: AnalyticsStrings,
) = when {
    this > 0 -> strings.taskCountIncreaseFormat.format(this)
    this < 0 -> strings.taskCountDecreaseFormat.format(abs(this))
    else -> strings.comparisonUnchanged
}

private fun Int.formatCompletedDelta(
    strings: AnalyticsStrings,
) = when {
    this > 0 -> strings.completedCountIncreaseFormat.format(this)
    this < 0 -> strings.completedCountDecreaseFormat.format(abs(this))
    else -> strings.comparisonUnchanged
}
