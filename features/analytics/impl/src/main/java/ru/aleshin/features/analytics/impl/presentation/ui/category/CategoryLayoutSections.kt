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
package ru.aleshin.features.analytics.impl.presentation.ui.category

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.charts.CategoryColorsDefaults
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsLayoutDefaults
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEvent
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryState
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategoryDayPartsSection
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategoryExpandedDayPartsSection
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategoryExpandedMetricsSection
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategoryExpandedSubCategoriesSection
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategoryExpandedSummarySection
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategoryLoadSection
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategoryMetricsSection
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategoryObservationSection
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategorySubCategoriesSection
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategorySummarySection
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections.CategoryTasksSection
import ru.aleshin.features.analytics.impl.presentation.ui.common.analyticsGridSection
import ru.aleshin.features.analytics.impl.presentation.ui.common.analyticsListSection
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsFixedHeightSectionRow

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
internal fun LazyListScope.CategoryCompactSections(
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

    item(key = SUMMARY_SECTION_KEY) {
        CategorySummarySection(
            modifier = Modifier.analyticsListSection(),
            categoryId = category.id,
            summary = summary,
        )
    }
    item(key = METRICS_SECTION_KEY) {
        CategoryMetricsSection(
            modifier = Modifier.analyticsListSection(),
            metrics = metrics,
        )
    }
    item(key = SUBCATEGORIES_SECTION_KEY) {
        CategorySubCategoriesSection(
            modifier = Modifier.analyticsListSection(),
            categoryId = category.id,
            categoryDurationMillis = summary.durationMillis,
            distribution = subCategories,
            selectedBucketKey = state.selectedSubCategoryBucketKey,
            onSelect = { key -> onEvent(CategoryEvent.SelectSubCategoryBucket(key = key)) },
        )
    }
    item(key = LOAD_SECTION_KEY) {
        CategoryLoadSection(
            modifier = Modifier.analyticsListSection(),
            categoryId = category.id,
            categoryName = category.fetchName().orEmpty(),
            load = load,
            range = range,
            selectedBucketIndex = state.selectedLoadBucketIndex,
            onSelect = { index -> onEvent(CategoryEvent.SelectLoadBucket(index = index)) },
        )
    }
    item(key = DAY_PARTS_SECTION_KEY) {
        CategoryDayPartsSection(
            modifier = Modifier.analyticsListSection(),
            categoryId = category.id,
            cells = analytics.dayParts,
            summaries = analytics.dayPartSummaries,
        )
    }
    item(key = TASKS_SECTION_KEY) {
        CategoryTasksSection(
            modifier = Modifier.analyticsListSection(bottomPadding = 0.dp),
            taskRows = analytics.taskRows,
            metrics = metrics,
            isExpanded = state.isTasksExpanded,
            hasFollowingSection = analytics.observation != null,
            onToggle = { onEvent(CategoryEvent.ToggleTasksExpanded) },
        )
    }
    analytics.observation?.let { observation ->
        item(key = OBSERVATION_SECTION_KEY) {
            CategoryObservationSection(
                modifier = Modifier.analyticsListSection(bottomPadding = 0.dp),
                observation = observation,
                categoryColor = CategoryColorsDefaults.fetchColor(categoryId = category.id),
            )
        }
    }
}

internal fun LazyListScope.CategoryBookMainSections(
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
) {
    val analytics = state.analytics ?: return
    val category = analytics.category ?: return
    val summary = analytics.summary ?: return
    val subCategories = analytics.subCategories ?: return

    item(key = SUMMARY_SECTION_KEY) {
        CategorySummarySection(
            modifier = Modifier.analyticsListSection(),
            categoryId = category.id,
            summary = summary,
        )
    }
    item(key = SUBCATEGORIES_SECTION_KEY) {
        CategorySubCategoriesSection(
            modifier = Modifier.analyticsListSection(bottomPadding = 0.dp),
            categoryId = category.id,
            categoryDurationMillis = summary.durationMillis,
            distribution = subCategories,
            selectedBucketKey = state.selectedSubCategoryBucketKey,
            onSelect = { key -> onEvent(CategoryEvent.SelectSubCategoryBucket(key = key)) },
        )
    }
}

internal fun LazyListScope.CategoryBookSupportingSections(
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
) {
    val analytics = state.analytics ?: return
    val range = state.range ?: return
    val category = analytics.category ?: return
    val metrics = analytics.keyMetrics ?: return
    val load = analytics.load ?: return

    item(key = METRICS_SECTION_KEY) {
        CategoryMetricsSection(
            modifier = Modifier.analyticsListSection(),
            metrics = metrics,
        )
    }
    item(key = LOAD_SECTION_KEY) {
        CategoryLoadSection(
            modifier = Modifier.analyticsListSection(),
            categoryId = category.id,
            categoryName = category.fetchName().orEmpty(),
            load = load,
            range = range,
            selectedBucketIndex = state.selectedLoadBucketIndex,
            onSelect = { index -> onEvent(CategoryEvent.SelectLoadBucket(index = index)) },
        )
    }
    item(key = DAY_PARTS_SECTION_KEY) {
        CategoryDayPartsSection(
            modifier = Modifier.analyticsListSection(),
            categoryId = category.id,
            cells = analytics.dayParts,
            summaries = analytics.dayPartSummaries,
        )
    }
    item(key = TASKS_SECTION_KEY) {
        CategoryTasksSection(
            modifier = Modifier.analyticsListSection(bottomPadding = 0.dp),
            taskRows = analytics.taskRows,
            metrics = metrics,
            isExpanded = state.isTasksExpanded,
            hasFollowingSection = analytics.observation != null,
            onToggle = { onEvent(CategoryEvent.ToggleTasksExpanded) },
        )
    }
    analytics.observation?.let { observation ->
        item(key = OBSERVATION_SECTION_KEY) {
            CategoryObservationSection(
                modifier = Modifier.analyticsListSection(bottomPadding = 0.dp),
                observation = observation,
                categoryColor = CategoryColorsDefaults.fetchColor(categoryId = category.id),
            )
        }
    }
}

internal fun LazyGridScope.CategoryMediumGridSections(
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
    val fullSpan = CategoryGridSpec.MEDIUM_COLUMN_COUNT

    item(
        key = SUMMARY_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        CategorySummarySection(
            modifier = Modifier.analyticsGridSection(),
            categoryId = category.id,
            summary = summary,
        )
    }
    item(
        key = METRICS_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        CategoryMetricsSection(
            modifier = Modifier.analyticsGridSection(),
            metrics = metrics,
        )
    }
    item(
        key = LOAD_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        CategoryLoadSection(
            modifier = Modifier.analyticsGridSection(),
            categoryId = category.id,
            categoryName = category.fetchName().orEmpty(),
            load = load,
            range = range,
            selectedBucketIndex = state.selectedLoadBucketIndex,
            onSelect = { index -> onEvent(CategoryEvent.SelectLoadBucket(index = index)) },
        )
    }
    item(
        key = SUBCATEGORIES_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        CategorySubCategoriesSection(
            modifier = Modifier.analyticsGridSection(),
            categoryId = category.id,
            categoryDurationMillis = summary.durationMillis,
            distribution = subCategories,
            selectedBucketKey = state.selectedSubCategoryBucketKey,
            onSelect = { key -> onEvent(CategoryEvent.SelectSubCategoryBucket(key = key)) },
        )
    }
    item(
        key = DAY_PARTS_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        CategoryDayPartsSection(
            modifier = Modifier.analyticsGridSection(),
            categoryId = category.id,
            cells = analytics.dayParts,
            summaries = analytics.dayPartSummaries,
        )
    }
    item(
        key = TASKS_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        CategoryTasksSection(
            modifier = Modifier.analyticsGridSection(bottomPadding = 0.dp),
            taskRows = analytics.taskRows,
            metrics = metrics,
            isExpanded = state.isTasksExpanded,
            hasFollowingSection = analytics.observation != null,
            onToggle = { onEvent(CategoryEvent.ToggleTasksExpanded) },
        )
    }
    analytics.observation?.let { observation ->
        item(
            key = OBSERVATION_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            CategoryObservationSection(
                modifier = Modifier.analyticsGridSection(bottomPadding = 0.dp),
                observation = observation,
                categoryColor = CategoryColorsDefaults.fetchColor(categoryId = category.id),
            )
        }
    }
}

internal fun LazyGridScope.CategoryExpandedGridSections(
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
    val fullSpan = CategoryGridSpec.EXPANDED_COLUMN_COUNT

    item(
        key = SUMMARY_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        AnalyticsFixedHeightSectionRow(
            modifier = Modifier.analyticsGridSection(),
            height = AnalyticsLayoutDefaults.CategorySummaryRowHeight,
        ) {
            CategoryExpandedSummarySection(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxHeight(),
                categoryId = category.id,
                summary = summary,
            )
            CategoryExpandedMetricsSection(
                modifier = Modifier
                    .weight(8f)
                    .fillMaxHeight(),
                metrics = metrics,
            )
        }
    }
    item(
        key = LOAD_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        CategoryLoadSection(
            modifier = Modifier.analyticsGridSection(),
            categoryId = category.id,
            categoryName = category.fetchName().orEmpty(),
            load = load,
            range = range,
            selectedBucketIndex = state.selectedLoadBucketIndex,
            onSelect = { index -> onEvent(CategoryEvent.SelectLoadBucket(index = index)) },
        )
    }
    item(
        key = SUBCATEGORIES_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        AnalyticsFixedHeightSectionRow(
            modifier = Modifier.analyticsGridSection(),
            height = AnalyticsLayoutDefaults.CategoryDistributionRowHeight,
        ) {
            CategoryExpandedSubCategoriesSection(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxHeight(),
                categoryId = category.id,
                categoryDurationMillis = summary.durationMillis,
                distribution = subCategories,
                selectedBucketKey = state.selectedSubCategoryBucketKey,
                onSelect = { key ->
                    onEvent(CategoryEvent.SelectSubCategoryBucket(key = key))
                },
            )
            CategoryExpandedDayPartsSection(
                modifier = Modifier
                    .weight(7f)
                    .fillMaxHeight(),
                categoryId = category.id,
                cells = analytics.dayParts,
                summaries = analytics.dayPartSummaries,
            )
        }
    }
    item(
        key = TASKS_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        CategoryTasksSection(
            modifier = Modifier.analyticsGridSection(bottomPadding = 0.dp),
            taskRows = analytics.taskRows,
            metrics = metrics,
            isExpanded = state.isTasksExpanded,
            hasFollowingSection = analytics.observation != null,
            onToggle = { onEvent(CategoryEvent.ToggleTasksExpanded) },
        )
    }
    analytics.observation?.let { observation ->
        item(
            key = OBSERVATION_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            CategoryObservationSection(
                modifier = Modifier.analyticsGridSection(bottomPadding = 0.dp),
                observation = observation,
                categoryColor = CategoryColorsDefaults.fetchColor(categoryId = category.id),
            )
        }
    }
}

private const val SUMMARY_SECTION_KEY = "category-summary"
private const val METRICS_SECTION_KEY = "category-metrics"
private const val SUBCATEGORIES_SECTION_KEY = "category-subcategories"
private const val LOAD_SECTION_KEY = "category-load"
private const val DAY_PARTS_SECTION_KEY = "category-day-parts"
private const val TASKS_SECTION_KEY = "category-tasks"
private const val OBSERVATION_SECTION_KEY = "category-observation"
