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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.analytics.impl.presentation.mappers.mapToMessage
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEffect
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEvent
import ru.aleshin.features.analytics.impl.presentation.ui.category.store.CategoryComponent
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryContentPlaceholder
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryMainSections
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryTopAppBar
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
internal fun CategoryContent(
    component: CategoryComponent,
    modifier: Modifier = Modifier,
) {
    val store = component.store
    val state by store.stateAsState()
    val strings = AnalyticsThemeRes.strings
    val range = state.range
    val categoryTitle = state.category?.fetchName().orEmpty()
    val snackbarHostState = remember { SnackbarHostState() }
    var isOpenCalendar by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CategoryTopAppBar(
                title = categoryTitle,
                categoryId = state.category?.id,
                defaultType = state.category?.defaultType,
                isLoading = state.isLoading && state.category == null,
                onBack = { store.dispatchEvent(CategoryEvent.NavigateBack) },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                ErrorSnackbar(snackbarData = it)
            }
        },
    ) { paddingValues ->
        AnimatedLoadingContent(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            isLoading = state.isLoading,
            targetValue = state,
        ) { contentState ->
            if (contentState != null) {
                val contentAnalytics = contentState.analytics
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    contentState.range?.let {
                        item(key = RANGE_SELECTOR_KEY) {
                            AnalyticsRangeSelector(
                                modifier = Modifier.fillMaxWidth().widthIn(max = MAX_CONTENT_WIDTH),
                                range = it,
                                onSelectPeriod = { period ->
                                    store.dispatchEvent(CategoryEvent.SelectPeriod(period))
                                },
                                onMoveToCurrent = {
                                    store.dispatchEvent(CategoryEvent.MoveToCurrent)
                                },
                                onPrevious = {
                                    store.dispatchEvent(CategoryEvent.PreviousPeriod)
                                },
                                onNext = {
                                    store.dispatchEvent(CategoryEvent.NextPeriod)
                                },
                                onOpenCalendar = {
                                    isOpenCalendar = true
                                },
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    when {
                        contentState.isError && contentAnalytics == null -> {
                            item(key = ERROR_CONTENT_KEY) {
                                AnalyticsErrorCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .widthIn(max = MAX_CONTENT_WIDTH),
                                    text = strings.categoryError,
                                    retryTitle = strings.retry,
                                    onRetry = {
                                        store.dispatchEvent(CategoryEvent.Retry)
                                    },
                                )
                            }
                        }
                        contentState.isUnavailable -> {
                            item(key = UNAVAILABLE_CONTENT_KEY) {
                                AnalyticsEmptyCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .widthIn(max = MAX_CONTENT_WIDTH),
                                    text = strings.categoryUnavailable,
                                    supportingText = strings.categoryUnavailableSupporting,
                                    actionTitle = strings.navigateBackDesc,
                                    onAction = {
                                        store.dispatchEvent(CategoryEvent.NavigateBack)
                                    },
                                    isProminent = true,
                                )
                            }
                        }
                        contentAnalytics?.taskRows.isNullOrEmpty() -> {
                            item(key = EMPTY_CONTENT_KEY) {
                                AnalyticsEmptyCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .widthIn(max = MAX_CONTENT_WIDTH),
                                    text = strings.categoryNoData,
                                    supportingText = strings.categoryNoDataSupporting,
                                )
                            }
                        }
                        else -> {
                            CategoryMainSections(
                                state = contentState,
                                onEvent = store::dispatchEvent,
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    userScrollEnabled = false,
                ) {
                    CategoryContentPlaceholder()
                }
            }
        }
    }

    if (isOpenCalendar && range != null) {
        AnalyticsDateRangeDialog(
            initialFrom = range.from.time,
            initialTo = range.to.time,
            onDismiss = {
                isOpenCalendar = false
            },
            onConfirm = { fromPickerToken, toPickerToken ->
                isOpenCalendar = false
                store.dispatchEvent(
                    CategoryEvent.ConfirmCalendar(
                        fromPickerToken = fromPickerToken,
                        toPickerToken = toPickerToken,
                    )
                )
            },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is CategoryEffect.ShowFailure -> snackbarHostState.showSnackbar(
                message = effect.failure.mapToMessage(strings = strings),
                withDismissAction = true,
            )
        }
    }
}

private val MAX_CONTENT_WIDTH = 680.dp
private const val RANGE_SELECTOR_KEY = "category-range"
private const val ERROR_CONTENT_KEY = "category-error"
private const val UNAVAILABLE_CONTENT_KEY = "category-unavailable"
private const val EMPTY_CONTENT_KEY = "category-empty"
