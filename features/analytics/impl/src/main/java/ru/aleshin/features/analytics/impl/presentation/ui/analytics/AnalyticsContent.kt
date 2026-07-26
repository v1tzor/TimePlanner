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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.analytics.impl.presentation.mappers.mapToMessage
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEffect
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsState
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.store.AnalyticsComponent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsCategoriesSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsContentPlaceholder
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsCreationSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsDurationSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsHoursSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsKeyMetricsSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsLoadSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsPlanSourceSection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsRegularitySection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsSummarySection
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsTopAppBar
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsDateRangeDialog
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsEmptyCard
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsErrorCard
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsRangeSelector
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.Scaffold
import ru.aleshin.timeplanner.core.ui.views.animations.AnimatedLoadingContent

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AnalyticsContent(
    analyticsComponent: AnalyticsComponent,
    modifier: Modifier = Modifier,
) {
    val store = analyticsComponent.store
    val state by store.stateAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val strings = AnalyticsThemeRes.strings
    var isOpenCalendar by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = { AnalyticsTopAppBar() },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { ErrorSnackbar(snackbarData = it) }
        },
    ) { paddingValues ->
        AnimatedLoadingContent(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            isLoading = state.isLoading,
            targetValue = state,
        ) { contentState ->
            if (contentState != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    contentState.range?.let {
                        item(key = RANGE_SELECTOR_KEY) {
                            AnalyticsRangeSelector(
                                modifier = Modifier.fillMaxWidth().widthIn(max = 680.dp),
                                range = it,
                                onSelectPeriod = { store.dispatchEvent(AnalyticsEvent.SelectPeriod(it)) },
                                onMoveToCurrent = { store.dispatchEvent(AnalyticsEvent.MoveToCurrent) },
                                onPrevious = { store.dispatchEvent(AnalyticsEvent.PreviousPeriod) },
                                onNext = { store.dispatchEvent(AnalyticsEvent.NextPeriod) },
                                onOpenCalendar = { isOpenCalendar = true },
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    if (contentState.isError && contentState.overview == null) {
                        item(key = ERROR_CONTENT_KEY) {
                            AnalyticsErrorCard(
                                modifier = Modifier.fillMaxWidth().widthIn(max = 680.dp),
                                text = AnalyticsThemeRes.strings.error,
                                retryTitle = AnalyticsThemeRes.strings.retry,
                                onRetry = { store.dispatchEvent(AnalyticsEvent.Retry) },
                            )
                        }
                    } else if (contentState.overview == null || contentState.overview.summary.allTaskCount == 0) {
                        item(key = EMPTY_CONTENT_KEY) {
                            AnalyticsEmptyCard(
                                modifier = Modifier.fillMaxWidth().widthIn(max = 680.dp),
                                text = AnalyticsThemeRes.strings.noData,
                                supportingText = AnalyticsThemeRes.strings.noDataSupporting,
                            )
                        }
                    } else {
                        AnalyticsMainSections(
                            state = contentState,
                            onEvent = store::dispatchEvent,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    userScrollEnabled = false,
                ) {
                    AnalyticsContentPlaceholder()
                }
            }
        }
    }

    val range = state.range
    if (isOpenCalendar && range != null) {
        AnalyticsDateRangeDialog(
            initialFrom = range.from.time,
            initialTo = range.to.time,
            onDismiss = { isOpenCalendar = false },
            onConfirm = { from, to ->
                isOpenCalendar = false
                store.dispatchEvent(AnalyticsEvent.ConfirmCalendar(from, to))
            }
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is AnalyticsEffect.ShowFailure -> snackbarHostState.showSnackbar(
                message = effect.failure.mapToMessage(strings),
                withDismissAction = true,
            )
        }
    }
}

private fun LazyListScope.AnalyticsMainSections(
    state: AnalyticsState,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    val overview = state.overview ?: return
    val range = state.range ?: return

    item(key = SUMMARY_SECTION_KEY) {
        AnalyticsSummarySection(
            modifier = Modifier.analyticsSectionItem(),
            selectedPeriod = range.period,
            summary = overview.summary,
        )
    }
    AnalyticsCategoriesSection(
        categories = overview.categories,
        categorySortType = state.categorySort,
        isExpanded = state.isCategoriesExpanded,
        onChangeSort = { sort ->
            onEvent(AnalyticsEvent.ChangeCategorySort(sort))
        },
        onToggle = {
            onEvent(AnalyticsEvent.ToggleCategories)
        },
        onOpenCategory = { categoryId ->
            onEvent(AnalyticsEvent.ClickCategoryItem(categoryId))
        },
    )
    item(key = LOAD_SECTION_KEY) {
        AnalyticsLoadSection(
            modifier = Modifier.analyticsSectionItem(),
            loadAnalytics = overview.load,
            selectedKey = state.selectedChartKey,
            onSelect = { key -> onEvent(AnalyticsEvent.SelectChartItem(key)) },
        )
    }
    item(key = CREATION_SECTION_KEY) {
        AnalyticsCreationSection(
            modifier = Modifier.analyticsSectionItem(),
            creationAnalytics = overview.creation,
            selectedBucketKey = state.selectedCreationBucketKey,
            onSelect = { key -> onEvent(AnalyticsEvent.SelectCreationBucket(key)) },
        )
    }
    item(key = DURATION_SECTION_KEY) {
        AnalyticsDurationSection(
            modifier = Modifier.analyticsSectionItem(),
            durationsAnalytics = overview.durations,
        )
    }
    item(key = SOURCE_SECTION_KEY) {
        AnalyticsPlanSourceSection(
            modifier = Modifier.analyticsSectionItem(),
            planSource = overview.planSource,
        )
    }
    item(key = KEY_METRICS_SECTION_KEY) {
        AnalyticsKeyMetricsSection(
            modifier = Modifier.analyticsSectionItem(),
            metrics = overview.keyMetrics,
        )
    }
    item(key = REGULARITY_SECTION_KEY) {
        AnalyticsRegularitySection(
            modifier = Modifier.analyticsSectionItem(),
            range = range,
            regularity = overview.regularity,
        )
    }
    item(key = HOURS_SECTION_KEY) {
        AnalyticsHoursSection(
            modifier = Modifier.analyticsSectionItem(bottomPadding = 0.dp),
            weekdayHourLoad = overview.weekdayHourLoad,
        )
    }
}

private fun Modifier.analyticsSectionItem(
    bottomPadding: Dp = 24.dp,
) = fillMaxWidth()
    .widthIn(max = 680.dp)
    .padding(bottom = bottomPadding)


private const val RANGE_SELECTOR_KEY = "analytics-range"
private const val EMPTY_CONTENT_KEY = "analytics-empty"
private const val ERROR_CONTENT_KEY = "analytics-error"
private const val SUMMARY_SECTION_KEY = "analytics-summary"
private const val LOAD_SECTION_KEY = "analytics-load"
private const val CREATION_SECTION_KEY = "analytics-creation"
private const val DURATION_SECTION_KEY = "analytics-duration"
private const val SOURCE_SECTION_KEY = "analytics-source"
private const val KEY_METRICS_SECTION_KEY = "analytics-key-metrics"
private const val REGULARITY_SECTION_KEY = "analytics-regularity"
private const val HOURS_SECTION_KEY = "analytics-hours"
