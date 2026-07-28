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

package ru.aleshin.features.analytics.impl.presentation.ui.analytics

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsLayoutDefaults
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsState
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsCategoriesSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsCreationSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsDurationSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsHoursSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsKeyMetricsSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsLoadSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsPlanSourceSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsRegularitySection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsSummarySection
import ru.aleshin.features.analytics.impl.presentation.ui.common.analyticsGridSection
import ru.aleshin.features.analytics.impl.presentation.ui.common.analyticsListSection
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsSectionRow

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal fun LazyListScope.AnalyticsCompactSections(
    state: AnalyticsState,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    val overview = state.overview ?: return
    val range = state.range ?: return

    item(key = SUMMARY_SECTION_KEY) {
        AnalyticsSummarySection(
            modifier = Modifier.analyticsListSection(),
            selectedPeriod = range.period,
            summary = overview.summary,
        )
    }
    item(key = CATEGORIES_SECTION_KEY) {
        AnalyticsCategoriesSection(
            modifier = Modifier.analyticsListSection(bottomPadding = 0.dp),
            categories = overview.categories,
            categorySortType = state.categorySort,
            isExpanded = state.isCategoriesExpanded,
            onChangeSort = { sort -> onEvent(AnalyticsEvent.ChangeCategorySort(sort)) },
            onToggle = { onEvent(AnalyticsEvent.ToggleCategories) },
            onOpenCategory = { categoryId ->
                onEvent(AnalyticsEvent.ClickCategoryItem(categoryId))
            },
        )
    }
    item(key = LOAD_SECTION_KEY) {
        AnalyticsLoadSection(
            modifier = Modifier.analyticsListSection(),
            loadAnalytics = overview.load,
            selectedKey = state.selectedChartKey,
            onSelect = { key -> onEvent(AnalyticsEvent.SelectChartItem(key)) },
        )
    }
    item(key = CREATION_SECTION_KEY) {
        AnalyticsCreationSection(
            modifier = Modifier.analyticsListSection(),
            creationAnalytics = overview.creation,
            selectedBucketKey = state.selectedCreationBucketKey,
            onSelect = { key -> onEvent(AnalyticsEvent.SelectCreationBucket(key)) },
        )
    }
    item(key = DURATION_SECTION_KEY) {
        AnalyticsDurationSection(
            modifier = Modifier.analyticsListSection(),
            durationsAnalytics = overview.durations,
        )
    }
    item(key = SOURCE_SECTION_KEY) {
        AnalyticsPlanSourceSection(
            modifier = Modifier.analyticsListSection(),
            planSource = overview.planSource,
        )
    }
    item(key = KEY_METRICS_SECTION_KEY) {
        AnalyticsKeyMetricsSection(
            modifier = Modifier.analyticsListSection(),
            metrics = overview.keyMetrics,
        )
    }
    item(key = REGULARITY_SECTION_KEY) {
        AnalyticsRegularitySection(
            modifier = Modifier.analyticsListSection(),
            range = range,
            regularity = overview.regularity,
        )
    }
    item(key = HOURS_SECTION_KEY) {
        AnalyticsHoursSection(
            modifier = Modifier.analyticsListSection(bottomPadding = 0.dp),
            weekdayHourLoad = overview.weekdayHourLoad,
        )
    }
}

internal fun LazyListScope.AnalyticsBookMainSections(
    state: AnalyticsState,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    val overview = state.overview ?: return
    val range = state.range ?: return

    item(key = SUMMARY_SECTION_KEY) {
        AnalyticsSummarySection(
            modifier = Modifier.analyticsListSection(),
            selectedPeriod = range.period,
            summary = overview.summary,
        )
    }
    item(key = CATEGORIES_SECTION_KEY) {
        AnalyticsCategoriesSection(
            modifier = Modifier.fillMaxWidth(),
            categories = overview.categories,
            categorySortType = state.categorySort,
            isExpanded = state.isCategoriesExpanded,
            onChangeSort = { sort -> onEvent(AnalyticsEvent.ChangeCategorySort(sort)) },
            onToggle = { onEvent(AnalyticsEvent.ToggleCategories) },
            onOpenCategory = { categoryId ->
                onEvent(AnalyticsEvent.ClickCategoryItem(categoryId))
            },
        )
    }
}

internal fun LazyListScope.AnalyticsBookSupportingSections(
    state: AnalyticsState,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    val overview = state.overview ?: return
    val range = state.range ?: return

    item(key = LOAD_SECTION_KEY) {
        AnalyticsLoadSection(
            modifier = Modifier.analyticsListSection(),
            loadAnalytics = overview.load,
            selectedKey = state.selectedChartKey,
            onSelect = { key -> onEvent(AnalyticsEvent.SelectChartItem(key)) },
        )
    }
    item(key = KEY_METRICS_SECTION_KEY) {
        AnalyticsKeyMetricsSection(
            modifier = Modifier.analyticsListSection(),
            metrics = overview.keyMetrics,
        )
    }
    item(key = CREATION_SECTION_KEY) {
        AnalyticsCreationSection(
            modifier = Modifier.analyticsListSection(),
            creationAnalytics = overview.creation,
            selectedBucketKey = state.selectedCreationBucketKey,
            onSelect = { key -> onEvent(AnalyticsEvent.SelectCreationBucket(key)) },
        )
    }
    item(key = DURATION_SECTION_KEY) {
        AnalyticsDurationSection(
            modifier = Modifier.analyticsListSection(),
            durationsAnalytics = overview.durations,
        )
    }
    item(key = SOURCE_SECTION_KEY) {
        AnalyticsPlanSourceSection(
            modifier = Modifier.analyticsListSection(),
            planSource = overview.planSource,
        )
    }
    item(key = REGULARITY_SECTION_KEY) {
        AnalyticsRegularitySection(
            modifier = Modifier.analyticsListSection(),
            range = range,
            regularity = overview.regularity,
        )
    }
    item(key = HOURS_SECTION_KEY) {
        AnalyticsHoursSection(
            modifier = Modifier.analyticsListSection(bottomPadding = 0.dp),
            weekdayHourLoad = overview.weekdayHourLoad,
        )
    }
}

internal fun LazyGridScope.AnalyticsGridSections(
    state: AnalyticsState,
    isExpanded: Boolean,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    val overview = state.overview ?: return
    val range = state.range ?: return
    val fullSpan = AnalyticsGridSpec.fetchColumnCount(isExpanded = isExpanded)

    if (isExpanded) {
        item(
            key = SUMMARY_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            AnalyticsSectionRow(
                modifier = Modifier.analyticsGridSection(),
                height = AnalyticsLayoutDefaults.SummaryRowHeight,
            ) {
                AnalyticsSummarySection(
                    modifier = Modifier
                        .weight(
                            AnalyticsGridSpec.fetchSpan(
                                section = AnalyticsGridSection.SUMMARY,
                                isExpanded = true,
                            ).toFloat(),
                        )
                        .fillMaxHeight(),
                    selectedPeriod = range.period,
                    summary = overview.summary,
                    fillAvailableHeight = true,
                    showTitleInside = false,
                )
                AnalyticsKeyMetricsSection(
                    modifier = Modifier
                        .weight(
                            AnalyticsGridSpec.fetchSpan(
                                section = AnalyticsGridSection.KEY_METRICS,
                                isExpanded = true,
                            ).toFloat(),
                        )
                        .fillMaxHeight(),
                    metrics = overview.keyMetrics,
                    fillAvailableHeight = true,
                )
            }
        }
    } else {
        item(
            key = SUMMARY_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            AnalyticsSummarySection(
                modifier = Modifier.analyticsGridSection(),
                selectedPeriod = range.period,
                summary = overview.summary,
            )
        }
        item(
            key = KEY_METRICS_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            AnalyticsKeyMetricsSection(
                modifier = Modifier.analyticsGridSection(),
                metrics = overview.keyMetrics,
            )
        }
    }
    item(
        key = CATEGORIES_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        AnalyticsCategoriesSection(
            modifier = Modifier.analyticsGridSection(bottomPadding = 0.dp),
            categories = overview.categories,
            categorySortType = state.categorySort,
            isExpanded = state.isCategoriesExpanded,
            onChangeSort = { sort -> onEvent(AnalyticsEvent.ChangeCategorySort(sort)) },
            onToggle = { onEvent(AnalyticsEvent.ToggleCategories) },
            onOpenCategory = { categoryId ->
                onEvent(AnalyticsEvent.ClickCategoryItem(categoryId))
            },
        )
    }
    item(
        key = LOAD_SECTION_KEY,
        span = { GridItemSpan(fullSpan) },
    ) {
        AnalyticsLoadSection(
            modifier = Modifier.analyticsGridSection(),
            loadAnalytics = overview.load,
            selectedKey = state.selectedChartKey,
            onSelect = { key -> onEvent(AnalyticsEvent.SelectChartItem(key)) },
        )
    }
    if (isExpanded) {
        item(
            key = CREATION_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            AnalyticsSectionRow(
                modifier = Modifier.analyticsGridSection(),
                height = AnalyticsLayoutDefaults.CreationRegularityRowHeight,
            ) {
                AnalyticsCreationSection(
                    modifier = Modifier
                        .weight(
                            AnalyticsGridSpec.fetchSpan(
                                section = AnalyticsGridSection.CREATION,
                                isExpanded = true,
                            ).toFloat(),
                        )
                        .fillMaxHeight(),
                    creationAnalytics = overview.creation,
                    selectedBucketKey = state.selectedCreationBucketKey,
                    fillAvailableHeight = true,
                    onSelect = { key ->
                        onEvent(AnalyticsEvent.SelectCreationBucket(key))
                    },
                )
                AnalyticsRegularitySection(
                    modifier = Modifier
                        .weight(
                            AnalyticsGridSpec.fetchSpan(
                                section = AnalyticsGridSection.REGULARITY,
                                isExpanded = true,
                            ).toFloat(),
                        )
                        .fillMaxHeight(),
                    range = range,
                    regularity = overview.regularity,
                    fillAvailableHeight = true,
                )
            }
        }
        item(
            key = HOURS_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            AnalyticsSectionRow(
                modifier = Modifier.analyticsGridSection()
            ) {
                AnalyticsHoursSection(
                    modifier = Modifier
                        .weight(
                            AnalyticsGridSpec.fetchSpan(
                                section = AnalyticsGridSection.HOURS,
                                isExpanded = true,
                            ).toFloat(),
                        )
                        .fillMaxHeight(),
                    weekdayHourLoad = overview.weekdayHourLoad,
                    fillAvailableHeight = true,
                )
                AnalyticsDurationSection(
                    modifier = Modifier
                        .weight(
                            AnalyticsGridSpec.fetchSpan(
                                section = AnalyticsGridSection.DURATION,
                                isExpanded = true,
                            ).toFloat(),
                        )
                        .fillMaxHeight(),
                    durationsAnalytics = overview.durations,
                    fillAvailableHeight = true,
                )
            }
        }
    } else {
        item(
            key = CREATION_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            AnalyticsCreationSection(
                modifier = Modifier.analyticsGridSection(),
                creationAnalytics = overview.creation,
                selectedBucketKey = state.selectedCreationBucketKey,
                onSelect = { key ->
                    onEvent(AnalyticsEvent.SelectCreationBucket(key))
                },
            )
        }
        item(
            key = REGULARITY_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            AnalyticsRegularitySection(
                modifier = Modifier.analyticsGridSection(),
                range = range,
                regularity = overview.regularity,
            )
        }
        item(
            key = HOURS_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            AnalyticsHoursSection(
                modifier = Modifier.analyticsGridSection(),
                weekdayHourLoad = overview.weekdayHourLoad,
            )
        }
        item(
            key = DURATION_SECTION_KEY,
            span = { GridItemSpan(fullSpan) },
        ) {
            AnalyticsDurationSection(
                modifier = Modifier.analyticsGridSection(),
                durationsAnalytics = overview.durations,
            )
        }
    }
    item(
        key = SOURCE_SECTION_KEY,
        span = {
            GridItemSpan(
                AnalyticsGridSpec.fetchSpan(
                    section = AnalyticsGridSection.SOURCE,
                    isExpanded = isExpanded,
                ),
            )
        },
    ) {
        AnalyticsPlanSourceSection(
            modifier = Modifier.analyticsGridSection(bottomPadding = 0.dp),
            planSource = overview.planSource,
        )
    }
}

private const val CATEGORIES_SECTION_KEY = "analytics-categories"
private const val SUMMARY_SECTION_KEY = "analytics-summary"
private const val LOAD_SECTION_KEY = "analytics-load"
private const val CREATION_SECTION_KEY = "analytics-creation"
private const val DURATION_SECTION_KEY = "analytics-duration"
private const val SOURCE_SECTION_KEY = "analytics-source"
private const val KEY_METRICS_SECTION_KEY = "analytics-key-metrics"
private const val REGULARITY_SECTION_KEY = "analytics-regularity"
private const val HOURS_SECTION_KEY = "analytics-hours"
