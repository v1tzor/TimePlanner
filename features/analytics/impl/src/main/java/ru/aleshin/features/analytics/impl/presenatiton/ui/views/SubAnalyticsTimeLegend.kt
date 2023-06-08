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
 * imitations under the License.
 */
package ru.aleshin.features.analytics.impl.presenatiton.ui.views

import androidx.compose.foundation.BorderStroke
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
import ru.aleshin.core.ui.views.toMinutesAndHoursTitle
import ru.aleshin.features.analytics.impl.domain.entities.CategoryAnalytic
import ru.aleshin.features.home.api.presentation.mappers.fetchNameByLanguage

@Composable
internal fun SubAnalyticsTimeLegend(
    modifier: Modifier = Modifier,
    analytics: List<CategoryAnalytic>,
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

            items(subCategoryAnalytic) {
                val percent = it.duration.toFloat() / allSubCategoryAnalytics.duration * 100
                SubAnalyticsTimeLegendItem(
                    number = subCategoryAnalytic.indexOf(it).inc(),
                    name = it.subCategory.fetchNameByLanguage(),
                    duration = it.duration,
                    percent = percent,
                )
            }
        } else {
            val otherAnalytics = analytics.subList(5, analytics.lastIndex)
            items(otherAnalytics) {
                val percent = it.duration.toFloat() / otherAnalytics.sumOf { it.duration } * 100

                SubAnalyticsTimeLegendItem(
                    number = otherAnalytics.indexOf(it).inc(),
                    name = it.mainCategory.fetchNameByLanguage(),
                    duration = it.duration,
                    percent = percent,
                )
            }
        }
    }
}

@Composable
internal fun SubAnalyticsTimeLegendItem(
    modifier: Modifier = Modifier,
    number: Int,
    name: String,
    duration: Long,
    percent: Float,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = number.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleLarge,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.onSurface,
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
