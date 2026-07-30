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
internal fun LazyListScope.CategoryContentPlaceholder() {
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
        CategorySummaryPlaceholder(
            modifier = Modifier.placeholderSectionItem(),
        )
    }
    item(key = METRICS_PLACEHOLDER_KEY) {
        CategoryMetricsPlaceholder(
            modifier = Modifier.placeholderSectionItem(),
        )
    }
    item(key = CHART_PLACEHOLDER_KEY) {
        CategoryChartPlaceholder(
            modifier = Modifier.placeholderSectionItem(),
        )
    }
    item(key = TASKS_PLACEHOLDER_KEY) {
        CategoryTasksPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth),
        )
    }
}

@Composable
private fun CategorySummaryPlaceholder(
    modifier: Modifier = Modifier,
) {
    CategoryPlaceholderSurface(modifier = modifier) {
        PlaceholderBox(
            modifier = Modifier
                .width(168.dp)
                .height(24.dp),
            shape = MaterialTheme.shapes.small,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PlaceholderBox(
                modifier = Modifier
                    .width(112.dp)
                    .height(28.dp),
                shape = MaterialTheme.shapes.small,
            )
            PlaceholderBox(
                modifier = Modifier
                    .width(152.dp)
                    .height(16.dp),
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            shape = CircleShape,
        )
        PlaceholderBox(
            modifier = Modifier
                .width(136.dp)
                .height(16.dp),
            shape = MaterialTheme.shapes.extraSmall,
        )
    }
}

@Composable
private fun CategoryMetricsPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PlaceholderBox(
            modifier = Modifier
                .width(136.dp)
                .height(24.dp),
            shape = MaterialTheme.shapes.small,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column {
                repeat(2) { rowIndex ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CategoryMetricPlaceholder(modifier = Modifier.weight(1f))
                        CategoryMetricPlaceholder(modifier = Modifier.weight(1f))
                    }
                    if (rowIndex == 0) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryMetricPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(136.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PlaceholderBox(
            modifier = Modifier.size(40.dp),
            shape = MaterialTheme.shapes.medium,
        )
        PlaceholderBox(
            modifier = Modifier
                .width(72.dp)
                .height(22.dp),
            shape = MaterialTheme.shapes.extraSmall,
        )
        PlaceholderBox(
            modifier = Modifier
                .width(96.dp)
                .height(14.dp),
            shape = MaterialTheme.shapes.extraSmall,
        )
    }
}

@Composable
private fun CategoryChartPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PlaceholderBox(
            modifier = Modifier
                .width(148.dp)
                .height(24.dp),
            shape = MaterialTheme.shapes.small,
        )
        CategoryPlaceholderSurface {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                PlaceholderBox(
                    modifier = Modifier.size(136.dp),
                    shape = CircleShape,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    repeat(4) { index ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PlaceholderBox(
                                modifier = Modifier.size(8.dp),
                                shape = CircleShape,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            PlaceholderBox(
                                modifier = Modifier
                                    .width(if (index % 2 == 0) 96.dp else 72.dp)
                                    .height(14.dp),
                                shape = MaterialTheme.shapes.extraSmall,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTasksPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PlaceholderBox(
            modifier = Modifier
                .width(132.dp)
                .height(24.dp),
            shape = MaterialTheme.shapes.small,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column {
                repeat(3) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PlaceholderBox(
                            modifier = Modifier.size(36.dp),
                            shape = MaterialTheme.shapes.medium,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            PlaceholderBox(
                                modifier = Modifier
                                    .width(if (index % 2 == 0) 128.dp else 96.dp)
                                    .height(16.dp),
                                shape = MaterialTheme.shapes.extraSmall,
                            )
                            PlaceholderBox(
                                modifier = Modifier
                                    .width(144.dp)
                                    .height(12.dp),
                                shape = MaterialTheme.shapes.extraSmall,
                            )
                        }
                        PlaceholderBox(
                            modifier = Modifier
                                .width(56.dp)
                                .height(18.dp),
                            shape = MaterialTheme.shapes.extraSmall,
                        )
                    }
                    if (index < 2) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 64.dp),
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPlaceholderSurface(
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

private fun Modifier.placeholderSectionItem() = fillMaxWidth()
    .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth)
    .padding(bottom = 24.dp)

private const val RANGE_PLACEHOLDER_KEY = "category-placeholder-range"
private const val SUMMARY_PLACEHOLDER_KEY = "category-placeholder-summary"
private const val METRICS_PLACEHOLDER_KEY = "category-placeholder-metrics"
private const val CHART_PLACEHOLDER_KEY = "category-placeholder-chart"
private const val TASKS_PLACEHOLDER_KEY = "category-placeholder-tasks"
