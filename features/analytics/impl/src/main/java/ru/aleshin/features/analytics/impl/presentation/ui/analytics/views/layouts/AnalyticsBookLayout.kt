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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.AnalyticsBookMainSections
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.AnalyticsBookSupportingSections
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsState
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsContentPlaceholder
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsEmptyState
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsErrorState
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsRangeSelector
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold
import ru.aleshin.timeplanner.core.ui.views.animations.AnimatedLoadingContent

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun AnalyticsBookLayout(
    modifier: Modifier = Modifier,
    state: AnalyticsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onOpenCalendar: () -> Unit,
    onEvent: (AnalyticsEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier.fillMaxSize(),
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPane = {
            AnimatedLoadingContent(
                modifier = Modifier.fillMaxSize(),
                isLoading = state.isLoading,
                targetValue = state,
                label = "AnalyticsBookMainPane",
            ) { contentState ->
                if (contentState == null || contentState.isLoading) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp),
                        userScrollEnabled = false,
                    ) {
                        AnalyticsContentPlaceholder()
                    }
                } else {
                    val overview = contentState.overview
                    val hasContent = overview != null && overview.summary.allTaskCount > 0

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp),
                    ) {
                        val range = contentState.range
                        if (range != null) {
                            item(key = RANGE_SELECTOR_KEY) {
                                AnalyticsRangeSelector(
                                    modifier = Modifier.fillMaxWidth(),
                                    range = range,
                                    onSelectPeriod = { period ->
                                        onEvent(AnalyticsEvent.SelectPeriod(period))
                                    },
                                    onMoveToCurrent = {
                                        onEvent(AnalyticsEvent.MoveToCurrent)
                                    },
                                    onPrevious = {
                                        onEvent(AnalyticsEvent.PreviousPeriod)
                                    },
                                    onNext = { onEvent(AnalyticsEvent.NextPeriod) },
                                    onOpenCalendar = onOpenCalendar,
                                )
                            }
                            item(key = RANGE_SPACER_KEY) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        when {
                            contentState.isError && overview == null -> {
                                item(key = ERROR_CONTENT_KEY) {
                                    AnalyticsErrorState(
                                        modifier = Modifier.fillMaxWidth(),
                                        onRetryClick = {
                                            onEvent(AnalyticsEvent.Retry)
                                        },
                                    )
                                }
                            }
                            !hasContent -> {
                                item(key = EMPTY_CONTENT_KEY) {
                                    AnalyticsEmptyState(modifier = Modifier.fillMaxWidth())
                                }
                            }
                            else -> {
                                AnalyticsBookMainSections(
                                    state = contentState,
                                    onEvent = onEvent,
                                )
                            }
                        }
                    }
                }
            }
        },
        supportingPane = {
            AnimatedLoadingContent(
                modifier = Modifier.fillMaxSize(),
                isLoading = state.isLoading,
                targetValue = state,
                label = "AnalyticsBookSupportingPane",
            ) { contentState ->
                if (contentState == null || contentState.isLoading) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp),
                        userScrollEnabled = false,
                    ) {
                        AnalyticsContentPlaceholder()
                    }
                } else {
                    val overview = contentState.overview
                    val hasContent = overview != null && overview.summary.allTaskCount > 0

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp),
                    ) {
                        if (hasContent) {
                            AnalyticsBookSupportingSections(
                                state = contentState,
                                onEvent = onEvent,
                            )
                        }
                    }
                }
            }
        },
    )
}

private const val RANGE_SELECTOR_KEY = "analytics-range"
private const val RANGE_SPACER_KEY = "analytics-range-spacer"
private const val EMPTY_CONTENT_KEY = "analytics-empty"
private const val ERROR_CONTENT_KEY = "analytics-error"
