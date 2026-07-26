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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.charts.CategoryColorsDefaults
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEvent
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryState

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
internal fun LazyListScope.CategoryMainSections(
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
) {
    val analytics = state.analytics ?: return
    val range = state.range ?: return
    val category = analytics.category ?: return
    val summary = analytics.summary ?: return
    val metrics = analytics.keyMetrics ?: return
    val subCategories = analytics.subCategories ?: return
    val load = analytics.load ?: return

    item(key = SUMMARY_KEY) {
        CategorySummarySection(
            categoryId = category.id,
            summary = summary,
            modifier = Modifier.categorySectionItem(),
        )
    }
    item(key = METRICS_KEY) {
        CategoryMetricsSection(
            metrics = metrics,
            modifier = Modifier.categorySectionItem(),
        )
    }
    item(key = SUBCATEGORIES_KEY) {
        CategorySubCategoriesSection(
            categoryId = category.id,
            categoryDurationMillis = summary.durationMillis,
            distribution = subCategories,
            selectedBucketKey = state.selectedSubCategoryBucketKey,
            onSelect = { key ->
                onEvent(CategoryEvent.SelectSubCategoryBucket(key = key))
            },
            modifier = Modifier.categorySectionItem(),
        )
    }
    item(key = LOAD_KEY) {
        CategoryLoadSection(
            categoryId = category.id,
            categoryName = category.fetchName().orEmpty(),
            load = load,
            range = range,
            selectedBucketIndex = state.selectedLoadBucketIndex,
            onSelect = { index ->
                onEvent(CategoryEvent.SelectLoadBucket(index = index))
            },
            modifier = Modifier.categorySectionItem(),
        )
    }
    item(key = DAY_PARTS_KEY) {
        CategoryDayPartsSection(
            categoryId = category.id,
            cells = analytics.dayParts,
            summaries = analytics.dayPartSummaries,
            modifier = Modifier.categorySectionItem(),
        )
    }
    CategoryTasksSection(
        taskRows = analytics.taskRows,
        metrics = metrics,
        isExpanded = state.isTasksExpanded,
        hasFollowingSection = analytics.observation != null,
        onToggle = {
            onEvent(CategoryEvent.ToggleTasksExpanded)
        },
    )
    analytics.observation?.let { observation ->
        item(key = OBSERVATION_KEY) {
            CategoryObservationSection(
                observation = observation,
                categoryColor = CategoryColorsDefaults.fetchColor(categoryId = category.id),
                modifier = Modifier.categorySectionItem(bottomPadding = 0.dp),
            )
        }
    }
}

private fun Modifier.categorySectionItem(
    bottomPadding: Dp = 24.dp,
) = fillMaxWidth()
    .widthIn(max = MAX_CONTENT_WIDTH)
    .padding(bottom = bottomPadding)

private val MAX_CONTENT_WIDTH = 680.dp
private const val SUMMARY_KEY = "category-summary"
private const val METRICS_KEY = "category-metrics"
private const val SUBCATEGORIES_KEY = "category-subcategories"
private const val LOAD_KEY = "category-load"
private const val DAY_PARTS_KEY = "category-day-parts"
private const val OBSERVATION_KEY = "category-observation"
