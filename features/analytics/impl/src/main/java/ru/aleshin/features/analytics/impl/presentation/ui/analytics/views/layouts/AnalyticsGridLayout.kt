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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.AnalyticsExpandedGridSections
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.AnalyticsGridSpec
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.AnalyticsMediumGridSections
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsState
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsContentPlaceholder
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsEmptyState
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsErrorState
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsRangeSelector
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.animations.AnimatedLoadingContent

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun AnalyticsGridLayout(
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
        AnimatedLoadingContent(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.ExpandedContentMaxWidth)
                .fillMaxSize(),
            isLoading = state.isLoading,
            targetValue = state,
            label = "AnalyticsGrid",
        ) { contentState ->
            if (contentState == null || contentState.isLoading) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    userScrollEnabled = false,
                ) {
                    AnalyticsContentPlaceholder()
                }
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(columnCount),
                    contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    val range = contentState.range
                    if (range != null) {
                        item(
                            key = RANGE_SELECTOR_KEY,
                            span = { GridItemSpan(columnCount) },
                        ) {
                            AnalyticsRangeSelector(
                                modifier = Modifier.fillMaxWidth(),
                                range = range,
                                onSelectPeriod = { period ->
                                    onEvent(AnalyticsEvent.SelectPeriod(period))
                                },
                                onMoveToCurrent = { onEvent(AnalyticsEvent.MoveToCurrent) },
                                onPrevious = { onEvent(AnalyticsEvent.PreviousPeriod) },
                                onNext = { onEvent(AnalyticsEvent.NextPeriod) },
                                onOpenCalendar = onOpenCalendar,
                            )
                        }
                        item(
                            key = RANGE_SPACER_KEY,
                            span = { GridItemSpan(columnCount) },
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    when {
                        contentState.isError && contentState.overview == null -> {
                            item(
                                key = ERROR_CONTENT_KEY,
                                span = { GridItemSpan(columnCount) },
                            ) {
                                AnalyticsErrorState(
                                    modifier = Modifier.fillMaxWidth(),
                                    onRetryClick = { onEvent(AnalyticsEvent.Retry) },
                                )
                            }
                        }
                        contentState.overview == null ||
                            contentState.overview.summary.allTaskCount == 0 -> {
                            item(
                                key = EMPTY_CONTENT_KEY,
                                span = { GridItemSpan(columnCount) },
                            ) {
                                AnalyticsEmptyState(modifier = Modifier.fillMaxWidth())
                            }
                        }
                        else -> {
                            if (isExpanded) {
                                AnalyticsExpandedGridSections(
                                    state = contentState,
                                    onEvent = onEvent,
                                )
                            } else {
                                AnalyticsMediumGridSections(
                                    state = contentState,
                                    onEvent = onEvent,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private const val RANGE_SELECTOR_KEY = "analytics-range"
private const val RANGE_SPACER_KEY = "analytics-range-spacer"
private const val EMPTY_CONTENT_KEY = "analytics-empty"
private const val ERROR_CONTENT_KEY = "analytics-error"
