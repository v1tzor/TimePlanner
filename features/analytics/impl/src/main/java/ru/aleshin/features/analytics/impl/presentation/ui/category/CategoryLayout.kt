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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsLayoutDefaults
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEvent
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryState
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsEmptyCard
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsErrorCard
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsRangeSelector
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun CategoryLayout(
    modifier: Modifier = Modifier,
    state: CategoryState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onOpenCalendar: () -> Unit,
    onEvent: (CategoryEvent) -> Unit,
) {
    when {
        adaptiveLayoutInfo.isCompactWidth -> {
            CategoryCompactLayout(
                modifier = modifier,
                state = state,
                onOpenCalendar = onOpenCalendar,
                onEvent = onEvent,
            )
        }
        adaptiveLayoutInfo.isBookPosture -> {
            CategoryBookLayout(
                modifier = modifier,
                state = state,
                adaptiveLayoutInfo = adaptiveLayoutInfo,
                onOpenCalendar = onOpenCalendar,
                onEvent = onEvent,
            )
        }
        else -> {
            CategoryGridLayout(
                modifier = modifier,
                state = state,
                isExpanded = adaptiveLayoutInfo.useExpandedLayout,
                onOpenCalendar = onOpenCalendar,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun CategoryCompactLayout(
    modifier: Modifier = Modifier,
    state: CategoryState,
    onOpenCalendar: () -> Unit,
    onEvent: (CategoryEvent) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = AnalyticsLayoutDefaults.CompactContentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        state.range?.let { range ->
            item(key = RANGE_SELECTOR_KEY) {
                CategoryRangeSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth),
                    range = range,
                    onOpenCalendar = onOpenCalendar,
                    onEvent = onEvent,
                )
            }
            item(key = RANGE_SPACER_KEY) {
                Spacer(modifier = Modifier.height(AnalyticsLayoutDefaults.RangeSpacing))
            }
        }
        CategoryCompactState(
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun CategoryGridLayout(
    modifier: Modifier = Modifier,
    state: CategoryState,
    isExpanded: Boolean,
    onOpenCalendar: () -> Unit,
    onEvent: (CategoryEvent) -> Unit,
) {
    val columnCount = CategoryGridSpec.fetchColumnCount(isExpanded = isExpanded)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.ExpandedContentMaxWidth)
                .fillMaxSize(),
            contentPadding = AnalyticsLayoutDefaults.AdaptiveContentPadding,
            horizontalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.GridSpacing),
        ) {
            state.range?.let { range ->
                item(
                    key = RANGE_SELECTOR_KEY,
                    span = { GridItemSpan(columnCount) },
                ) {
                    CategoryRangeSection(
                        modifier = Modifier.fillMaxWidth(),
                        range = range,
                        onOpenCalendar = onOpenCalendar,
                        onEvent = onEvent,
                    )
                }
                item(
                    key = RANGE_SPACER_KEY,
                    span = { GridItemSpan(columnCount) },
                ) {
                    Spacer(modifier = Modifier.height(AnalyticsLayoutDefaults.RangeSpacing))
                }
            }
            CategoryGridState(
                state = state,
                isExpanded = isExpanded,
                columnCount = columnCount,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun CategoryBookLayout(
    modifier: Modifier = Modifier,
    state: CategoryState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onOpenCalendar: () -> Unit,
    onEvent: (CategoryEvent) -> Unit,
) {
    val analytics = state.analytics
    val hasContent = !analytics?.taskRows.isNullOrEmpty()
    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPane = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = AnalyticsLayoutDefaults.AdaptiveContentPadding,
            ) {
                state.range?.let { range ->
                    item(key = RANGE_SELECTOR_KEY) {
                        CategoryRangeSection(
                            modifier = Modifier.fillMaxWidth(),
                            range = range,
                            onOpenCalendar = onOpenCalendar,
                            onEvent = onEvent,
                        )
                    }
                    item(key = RANGE_SPACER_KEY) {
                        Spacer(modifier = Modifier.height(AnalyticsLayoutDefaults.RangeSpacing))
                    }
                }
                when {
                    state.isError && analytics == null -> {
                        item(key = ERROR_CONTENT_KEY) {
                            CategoryErrorContent(
                                modifier = Modifier.fillMaxWidth(),
                                onRetry = { onEvent(CategoryEvent.Retry) },
                            )
                        }
                    }
                    state.isUnavailable -> {
                        item(key = UNAVAILABLE_CONTENT_KEY) {
                            CategoryUnavailableContent(
                                modifier = Modifier.fillMaxWidth(),
                                onNavigateBack = { onEvent(CategoryEvent.NavigateBack) },
                            )
                        }
                    }
                    !hasContent -> {
                        item(key = EMPTY_CONTENT_KEY) {
                            CategoryEmptyContent(modifier = Modifier.fillMaxWidth())
                        }
                    }
                    else -> {
                        CategoryBookMainSections(
                            state = state,
                            onEvent = onEvent,
                        )
                    }
                }
            }
        },
        supportingPane = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = AnalyticsLayoutDefaults.AdaptiveContentPadding,
            ) {
                if (hasContent && !state.isUnavailable) {
                    CategoryBookSupportingSections(
                        state = state,
                        onEvent = onEvent,
                    )
                }
            }
        },
    )
}

private fun LazyListScope.CategoryCompactState(
    state: CategoryState,
    onEvent: (CategoryEvent) -> Unit,
) {
    val analytics = state.analytics
    when {
        state.isError && analytics == null -> {
            item(key = ERROR_CONTENT_KEY) {
                CategoryErrorContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth),
                    onRetry = { onEvent(CategoryEvent.Retry) },
                )
            }
        }
        state.isUnavailable -> {
            item(key = UNAVAILABLE_CONTENT_KEY) {
                CategoryUnavailableContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth),
                    onNavigateBack = { onEvent(CategoryEvent.NavigateBack) },
                )
            }
        }
        analytics?.taskRows.isNullOrEmpty() -> {
            item(key = EMPTY_CONTENT_KEY) {
                CategoryEmptyContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth),
                )
            }
        }
        else -> {
            CategoryCompactSections(
                state = state,
                onEvent = onEvent,
            )
        }
    }
}

private fun LazyGridScope.CategoryGridState(
    state: CategoryState,
    isExpanded: Boolean,
    columnCount: Int,
    onEvent: (CategoryEvent) -> Unit,
) {
    val analytics = state.analytics
    when {
        state.isError && analytics == null -> {
            item(
                key = ERROR_CONTENT_KEY,
                span = { GridItemSpan(columnCount) },
            ) {
                CategoryErrorContent(
                    modifier = Modifier.fillMaxWidth(),
                    onRetry = { onEvent(CategoryEvent.Retry) },
                )
            }
        }
        state.isUnavailable -> {
            item(
                key = UNAVAILABLE_CONTENT_KEY,
                span = { GridItemSpan(columnCount) },
            ) {
                CategoryUnavailableContent(
                    modifier = Modifier.fillMaxWidth(),
                    onNavigateBack = { onEvent(CategoryEvent.NavigateBack) },
                )
            }
        }
        analytics?.taskRows.isNullOrEmpty() -> {
            item(
                key = EMPTY_CONTENT_KEY,
                span = { GridItemSpan(columnCount) },
            ) {
                CategoryEmptyContent(modifier = Modifier.fillMaxWidth())
            }
        }
        else -> {
            CategoryGridSections(
                state = state,
                isExpanded = isExpanded,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun CategoryRangeSection(
    modifier: Modifier = Modifier,
    range: AnalyticsRangeUi,
    onOpenCalendar: () -> Unit,
    onEvent: (CategoryEvent) -> Unit,
) {
    AnalyticsRangeSelector(
        modifier = modifier,
        range = range,
        onSelectPeriod = { period -> onEvent(CategoryEvent.SelectPeriod(period)) },
        onMoveToCurrent = { onEvent(CategoryEvent.MoveToCurrent) },
        onPrevious = { onEvent(CategoryEvent.PreviousPeriod) },
        onNext = { onEvent(CategoryEvent.NextPeriod) },
        onOpenCalendar = onOpenCalendar,
    )
}

@Composable
private fun CategoryErrorContent(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    AnalyticsErrorCard(
        modifier = modifier,
        text = AnalyticsThemeRes.strings.categoryError,
        retryTitle = AnalyticsThemeRes.strings.retry,
        onRetry = onRetry,
    )
}

@Composable
private fun CategoryUnavailableContent(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    AnalyticsEmptyCard(
        modifier = modifier,
        text = AnalyticsThemeRes.strings.categoryUnavailable,
        supportingText = AnalyticsThemeRes.strings.categoryUnavailableSupporting,
        actionTitle = AnalyticsThemeRes.strings.navigateBackDesc,
        onAction = onNavigateBack,
        isProminent = true,
    )
}

@Composable
private fun CategoryEmptyContent(
    modifier: Modifier = Modifier,
) {
    AnalyticsEmptyCard(
        modifier = modifier,
        text = AnalyticsThemeRes.strings.categoryNoData,
        supportingText = AnalyticsThemeRes.strings.categoryNoDataSupporting,
    )
}

private const val RANGE_SELECTOR_KEY = "category-range"
private const val RANGE_SPACER_KEY = "category-range-spacer"
private const val ERROR_CONTENT_KEY = "category-error"
private const val UNAVAILABLE_CONTENT_KEY = "category-unavailable"
private const val EMPTY_CONTENT_KEY = "category-empty"
