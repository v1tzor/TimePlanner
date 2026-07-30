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
package ru.aleshin.features.analytics.impl.presentation.ui.category.views.layouts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.ui.category.CategoryBookMainSections
import ru.aleshin.features.analytics.impl.presentation.ui.category.CategoryBookSupportingSections
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEvent
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryState
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryContentPlaceholder
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryEmptyState
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryErrorState
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryUnavailableState
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsRangeSelector
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold
import ru.aleshin.timeplanner.core.ui.views.animations.AnimatedLoadingContent

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun CategoryBookLayout(
    modifier: Modifier = Modifier,
    state: CategoryState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onOpenCalendar: () -> Unit,
    onEvent: (CategoryEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier.fillMaxSize(),
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPane = {
            AnimatedLoadingContent(
                modifier = Modifier.fillMaxSize(),
                isLoading = state.isLoading,
                targetValue = state,
                label = "CategoryBookMainPane",
            ) { contentState ->
                if (contentState == null || contentState.isLoading) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp),
                        userScrollEnabled = false,
                    ) {
                        CategoryContentPlaceholder()
                    }
                } else {
                    val analytics = contentState.analytics
                    val hasContent = !analytics?.taskRows.isNullOrEmpty()

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
                                        onEvent(CategoryEvent.SelectPeriod(period))
                                    },
                                    onMoveToCurrent = {
                                        onEvent(CategoryEvent.MoveToCurrent)
                                    },
                                    onPrevious = {
                                        onEvent(CategoryEvent.PreviousPeriod)
                                    },
                                    onNext = { onEvent(CategoryEvent.NextPeriod) },
                                    onOpenCalendar = onOpenCalendar,
                                )
                            }
                            item(key = RANGE_SPACER_KEY) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        when {
                            contentState.isError && analytics == null -> {
                                item(key = ERROR_CONTENT_KEY) {
                                    CategoryErrorState(
                                        modifier = Modifier.fillMaxWidth(),
                                        onRetryClick = {
                                            onEvent(CategoryEvent.Retry)
                                        },
                                    )
                                }
                            }
                            contentState.isUnavailable -> {
                                item(key = UNAVAILABLE_CONTENT_KEY) {
                                    CategoryUnavailableState(
                                        modifier = Modifier.fillMaxWidth(),
                                        onBackClick = {
                                            onEvent(CategoryEvent.NavigateBack)
                                        },
                                    )
                                }
                            }
                            !hasContent -> {
                                item(key = EMPTY_CONTENT_KEY) {
                                    CategoryEmptyState(modifier = Modifier.fillMaxWidth())
                                }
                            }
                            else -> {
                                CategoryBookMainSections(
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
                label = "CategoryBookSupportingPane",
            ) { contentState ->
                if (contentState == null || contentState.isLoading) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp),
                        userScrollEnabled = false,
                    ) {
                        CategoryContentPlaceholder()
                    }
                } else {
                    val analytics = contentState.analytics
                    val hasContent = !analytics?.taskRows.isNullOrEmpty()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp),
                    ) {
                        if (hasContent && !contentState.isUnavailable) {
                            CategoryBookSupportingSections(
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

private const val RANGE_SELECTOR_KEY = "category-range"
private const val RANGE_SPACER_KEY = "category-range-spacer"
private const val ERROR_CONTENT_KEY = "category-error"
private const val UNAVAILABLE_CONTENT_KEY = "category-unavailable"
private const val EMPTY_CONTENT_KEY = "category-empty"
