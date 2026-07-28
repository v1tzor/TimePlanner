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
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsState
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
internal fun AnalyticsLayout(
    modifier: Modifier = Modifier,
    state: AnalyticsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onOpenCalendar: () -> Unit,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    when {
        adaptiveLayoutInfo.isCompactWidth -> {
            AnalyticsCompactLayout(
                modifier = modifier,
                state = state,
                onOpenCalendar = onOpenCalendar,
                onEvent = onEvent,
            )
        }
        adaptiveLayoutInfo.isBookPosture -> {
            AnalyticsBookLayout(
                modifier = modifier,
                state = state,
                adaptiveLayoutInfo = adaptiveLayoutInfo,
                onOpenCalendar = onOpenCalendar,
                onEvent = onEvent,
            )
        }
        else -> {
            AnalyticsGridLayout(
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
private fun AnalyticsCompactLayout(
    modifier: Modifier = Modifier,
    state: AnalyticsState,
    onOpenCalendar: () -> Unit,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = AnalyticsLayoutDefaults.CompactContentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        state.range?.let { range ->
            item(key = RANGE_SELECTOR_KEY) {
                AnalyticsRangeSection(
                    modifier = Modifier.fillMaxWidth().widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth),
                    range = range,
                    onOpenCalendar = onOpenCalendar,
                    onEvent = onEvent,
                )
            }
            item(key = RANGE_SPACER_KEY) {
                Spacer(modifier = Modifier.height(AnalyticsLayoutDefaults.RangeSpacing))
            }
        }
        AnalyticsCompactState(
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun AnalyticsGridLayout(
    modifier: Modifier = Modifier,
    state: AnalyticsState,
    isExpanded: Boolean,
    onOpenCalendar: () -> Unit,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    val columnCount = AnalyticsGridSpec.fetchColumnCount(isExpanded = isExpanded)
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
                    AnalyticsRangeSection(
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
            AnalyticsGridState(
                state = state,
                isExpanded = isExpanded,
                columnCount = columnCount,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun AnalyticsBookLayout(
    modifier: Modifier = Modifier,
    state: AnalyticsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onOpenCalendar: () -> Unit,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    val overview = state.overview
    val hasContent = overview != null && overview.summary.allTaskCount > 0

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
                        AnalyticsRangeSection(
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
                    state.isError && state.overview == null -> {
                        item(key = ERROR_CONTENT_KEY) {
                            AnalyticsErrorContent(
                                modifier = Modifier.fillMaxWidth(),
                                onRetry = { onEvent(AnalyticsEvent.Retry) },
                            )
                        }
                    }
                    !hasContent -> {
                        item(key = EMPTY_CONTENT_KEY) {
                            AnalyticsEmptyContent(modifier = Modifier.fillMaxWidth())
                        }
                    }
                    else -> {
                        AnalyticsBookMainSections(
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
                if (hasContent) {
                    AnalyticsBookSupportingSections(
                        state = state,
                        onEvent = onEvent,
                    )
                }
            }
        },
    )
}

private fun LazyListScope.AnalyticsCompactState(
    state: AnalyticsState,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    when {
        state.isError && state.overview == null -> {
            item(key = ERROR_CONTENT_KEY) {
                AnalyticsErrorContent(
                    modifier = Modifier.fillMaxWidth().widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth),
                    onRetry = { onEvent(AnalyticsEvent.Retry) },
                )
            }
        }
        state.overview == null || state.overview.summary.allTaskCount == 0 -> {
            item(key = EMPTY_CONTENT_KEY) {
                AnalyticsEmptyContent(
                    modifier = Modifier.fillMaxWidth().widthIn(max = AnalyticsLayoutDefaults.ContentMaxWidth),
                )
            }
        }
        else -> {
            AnalyticsCompactSections(
                state = state,
                onEvent = onEvent,
            )
        }
    }
}

private fun LazyGridScope.AnalyticsGridState(
    state: AnalyticsState,
    isExpanded: Boolean,
    columnCount: Int,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    when {
        state.isError && state.overview == null -> {
            item(
                key = ERROR_CONTENT_KEY,
                span = { GridItemSpan(columnCount) },
            ) {
                AnalyticsErrorContent(
                    modifier = Modifier.fillMaxWidth(),
                    onRetry = { onEvent(AnalyticsEvent.Retry) },
                )
            }
        }
        state.overview == null || state.overview.summary.allTaskCount == 0 -> {
            item(
                key = EMPTY_CONTENT_KEY,
                span = { GridItemSpan(columnCount) },
            ) {
                AnalyticsEmptyContent(modifier = Modifier.fillMaxWidth())
            }
        }
        else -> {
            AnalyticsGridSections(
                state = state,
                isExpanded = isExpanded,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun AnalyticsRangeSection(
    modifier: Modifier = Modifier,
    range: AnalyticsRangeUi,
    onOpenCalendar: () -> Unit,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    AnalyticsRangeSelector(
        modifier = modifier,
        range = range,
        onSelectPeriod = { period -> onEvent(AnalyticsEvent.SelectPeriod(period)) },
        onMoveToCurrent = { onEvent(AnalyticsEvent.MoveToCurrent) },
        onPrevious = { onEvent(AnalyticsEvent.PreviousPeriod) },
        onNext = { onEvent(AnalyticsEvent.NextPeriod) },
        onOpenCalendar = onOpenCalendar,
    )
}

@Composable
private fun AnalyticsErrorContent(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    AnalyticsErrorCard(
        modifier = modifier,
        text = AnalyticsThemeRes.strings.error,
        retryTitle = AnalyticsThemeRes.strings.retry,
        onRetry = onRetry,
    )
}

@Composable
private fun AnalyticsEmptyContent(
    modifier: Modifier = Modifier,
) {
    AnalyticsEmptyCard(
        modifier = modifier,
        text = AnalyticsThemeRes.strings.noData,
        supportingText = AnalyticsThemeRes.strings.noDataSupporting,
    )
}

private const val RANGE_SELECTOR_KEY = "analytics-range"
private const val RANGE_SPACER_KEY = "analytics-range-spacer"
private const val EMPTY_CONTENT_KEY = "analytics-empty"
private const val ERROR_CONTENT_KEY = "analytics-error"
