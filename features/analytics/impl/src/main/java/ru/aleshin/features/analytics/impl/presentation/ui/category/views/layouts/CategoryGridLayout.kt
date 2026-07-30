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
import ru.aleshin.features.analytics.impl.presentation.ui.category.CategoryExpandedGridSections
import ru.aleshin.features.analytics.impl.presentation.ui.category.CategoryGridSpec
import ru.aleshin.features.analytics.impl.presentation.ui.category.CategoryMediumGridSections
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEvent
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryState
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryContentPlaceholder
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryEmptyState
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryErrorState
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryUnavailableState
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsRangeSelector
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.animations.AnimatedLoadingContent

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun CategoryGridLayout(
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
        AnimatedLoadingContent(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.ExpandedContentMaxWidth)
                .fillMaxSize(),
            isLoading = state.isLoading,
            targetValue = state,
            label = "CategoryGrid",
        ) { contentState ->
            if (contentState == null || contentState.isLoading) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    userScrollEnabled = false,
                ) {
                    CategoryContentPlaceholder()
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
                                    onEvent(CategoryEvent.SelectPeriod(period))
                                },
                                onMoveToCurrent = {
                                    onEvent(CategoryEvent.MoveToCurrent)
                                },
                                onPrevious = { onEvent(CategoryEvent.PreviousPeriod) },
                                onNext = { onEvent(CategoryEvent.NextPeriod) },
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

                    val analytics = contentState.analytics
                    when {
                        contentState.isError && analytics == null -> {
                            item(
                                key = ERROR_CONTENT_KEY,
                                span = { GridItemSpan(columnCount) },
                            ) {
                                CategoryErrorState(
                                    modifier = Modifier.fillMaxWidth(),
                                    onRetryClick = { onEvent(CategoryEvent.Retry) },
                                )
                            }
                        }
                        contentState.isUnavailable -> {
                            item(
                                key = UNAVAILABLE_CONTENT_KEY,
                                span = { GridItemSpan(columnCount) },
                            ) {
                                CategoryUnavailableState(
                                    modifier = Modifier.fillMaxWidth(),
                                    onBackClick = { onEvent(CategoryEvent.NavigateBack) },
                                )
                            }
                        }
                        analytics?.taskRows.isNullOrEmpty() -> {
                            item(
                                key = EMPTY_CONTENT_KEY,
                                span = { GridItemSpan(columnCount) },
                            ) {
                                CategoryEmptyState(modifier = Modifier.fillMaxWidth())
                            }
                        }
                        else -> {
                            if (isExpanded) {
                                CategoryExpandedGridSections(
                                    state = contentState,
                                    onEvent = onEvent,
                                )
                            } else {
                                CategoryMediumGridSections(
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

private const val RANGE_SELECTOR_KEY = "category-range"
private const val RANGE_SPACER_KEY = "category-range-spacer"
private const val ERROR_CONTENT_KEY = "category-error"
private const val UNAVAILABLE_CONTENT_KEY = "category-unavailable"
private const val EMPTY_CONTENT_KEY = "category-empty"
