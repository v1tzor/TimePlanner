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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
internal fun LazyListScope.AnalyticsContentPlaceholder() {
    item(key = RANGE_PLACEHOLDER_KEY) {
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth)
                .padding(bottom = 16.dp)
                .height(40.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
        )
    }
    item(key = SUMMARY_PLACEHOLDER_KEY) {
        AnalyticsSummaryPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth)
                .padding(bottom = 24.dp),
        )
    }
    item(key = CATEGORIES_PLACEHOLDER_KEY) {
        AnalyticsCategoriesPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth)
                .padding(bottom = 24.dp),
        )
    }
    item(key = LOAD_PLACEHOLDER_KEY) {
        AnalyticsChartPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth)
                .padding(bottom = 24.dp),
        )
    }
    item(key = METRICS_PLACEHOLDER_KEY) {
        AnalyticsMetricsPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth),
        )
    }
}

@Composable
private fun AnalyticsSummaryPlaceholder(
    modifier: Modifier = Modifier,
) {
    PlaceholderSurface(modifier = modifier) {
        PlaceholderBox(
            modifier = Modifier
                .width(176.dp)
                .height(24.dp),
            shape = MaterialTheme.shapes.small,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MetricPlaceholder()
            MetricPlaceholder()
        }
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            shape = CircleShape,
        )
        repeat(3) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlaceholderBox(
                    modifier = Modifier.size(8.dp),
                    shape = CircleShape,
                )
                Spacer(modifier = Modifier.width(8.dp))
                PlaceholderBox(
                    modifier = Modifier
                        .width(if (index == 1) 96.dp else 120.dp)
                        .height(14.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                )
                Spacer(modifier = Modifier.weight(1f))
                PlaceholderBox(
                    modifier = Modifier
                        .width(64.dp)
                        .height(14.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                )
            }
        }
        PlaceholderBox(
            modifier = Modifier
                .width(148.dp)
                .height(16.dp),
            shape = MaterialTheme.shapes.extraSmall,
        )
    }
}

@Composable
private fun AnalyticsCategoriesPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlaceholderBox(
                modifier = Modifier
                    .width(132.dp)
                    .height(24.dp),
                shape = MaterialTheme.shapes.small,
            )
            Spacer(modifier = Modifier.weight(1f))
            PlaceholderBox(
                modifier = Modifier
                    .width(104.dp)
                    .height(32.dp),
                shape = MaterialTheme.shapes.large,
            )
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column {
                repeat(4) { index ->
                    CategoryRowPlaceholder(index = index)
                    if (index < 3) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryRowPlaceholder(
    index: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaceholderBox(
            modifier = Modifier
                .width(20.dp)
                .height(14.dp),
            shape = MaterialTheme.shapes.extraSmall,
        )
        Spacer(modifier = Modifier.width(8.dp))
        PlaceholderBox(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PlaceholderBox(
                modifier = Modifier
                    .width(if (index % 2 == 0) 136.dp else 104.dp)
                    .height(16.dp),
                shape = MaterialTheme.shapes.extraSmall,
            )
            PlaceholderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                shape = CircleShape,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        PlaceholderBox(
            modifier = Modifier
                .width(52.dp)
                .height(16.dp),
            shape = MaterialTheme.shapes.extraSmall,
        )
    }
}

@Composable
private fun AnalyticsChartPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PlaceholderBox(
            modifier = Modifier
                .width(144.dp)
                .height(24.dp),
            shape = MaterialTheme.shapes.small,
        )
        PlaceholderSurface {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                PlaceholderBox(
                    modifier = Modifier
                        .width(92.dp)
                        .height(16.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                )
                PlaceholderBox(
                    modifier = Modifier
                        .width(76.dp)
                        .height(16.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                )
            }
            PlaceholderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(184.dp),
                shape = MaterialTheme.shapes.medium,
            )
        }
    }
}

@Composable
private fun AnalyticsMetricsPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PlaceholderBox(
            modifier = Modifier
                .width(168.dp)
                .height(24.dp),
            shape = MaterialTheme.shapes.small,
        )
        PlaceholderSurface {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricPlaceholder(modifier = Modifier.weight(1f))
                MetricPlaceholder(modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricPlaceholder(modifier = Modifier.weight(1f))
                MetricPlaceholder(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MetricPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        PlaceholderBox(
            modifier = Modifier
                .width(88.dp)
                .height(24.dp),
            shape = MaterialTheme.shapes.small,
        )
        PlaceholderBox(
            modifier = Modifier
                .width(112.dp)
                .height(14.dp),
            shape = MaterialTheme.shapes.extraSmall,
        )
    }
}

@Composable
private fun PlaceholderSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

private const val RANGE_PLACEHOLDER_KEY = "analytics-placeholder-range"
private const val SUMMARY_PLACEHOLDER_KEY = "analytics-placeholder-summary"
private const val CATEGORIES_PLACEHOLDER_KEY = "analytics-placeholder-categories"
private const val LOAD_PLACEHOLDER_KEY = "analytics-placeholder-load"
private const val METRICS_PLACEHOLDER_KEY = "analytics-placeholder-metrics"
