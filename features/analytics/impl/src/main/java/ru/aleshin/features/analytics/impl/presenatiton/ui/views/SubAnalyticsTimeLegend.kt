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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.toMinutesAndHoursTitle
import ru.aleshin.features.analytics.impl.presenatiton.models.analytics.CategoryAnalyticUi

@Composable
internal fun SubAnalyticsTimeLegend(
    modifier: Modifier = Modifier,
    analytics: List<CategoryAnalyticUi>,
    selectedItem: Int,
) {
    LazyColumn(
        modifier = modifier,
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (selectedItem < 5) {
            val allSubCategoryAnalytics = analytics[selectedItem]
            val subCategoryAnalytic = allSubCategoryAnalytics.subCategoriesInfo

            items(subCategoryAnalytic) { analytic ->
                val percent = analytic.duration.toFloat() / allSubCategoryAnalytics.duration * 100

                SubAnalyticsTimeLegendItem(
                    name = analytic.subCategory.name ?: TimePlannerRes.strings.categoryEmptyTitle,
                    duration = analytic.duration,
                    percent = percent,
                )
            }
        } else {
            val otherAnalytics = analytics.subList(5, analytics.lastIndex)
            items(otherAnalytics) { analytic ->
                val percent = analytic.duration.toFloat() / otherAnalytics.sumOf { it.duration } * 100

                SubAnalyticsTimeLegendItem(
                    name = analytic.mainCategory.fetchName() ?: "*",
                    duration = analytic.duration,
                    percent = percent,
                )
            }
        }
    }
}

@Composable
internal fun SubAnalyticsTimeLegendItem(
    modifier: Modifier = Modifier,
    name: String,
    duration: Long,
    percent: Float,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = duration.toMinutesAndHoursTitle(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${percent.toInt()}%",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}
