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
package ru.aleshin.features.overview.impl.presentation.ui.goal.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryEvent
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryState
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.views.GoalHistoryItem
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun GoalsHistoryLayout(
    modifier: Modifier = Modifier,
    state: GoalsHistoryState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onEvent: (GoalsHistoryEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        GoalsHistoryList(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.MediumContentMaxWidth)
                .fillMaxWidth(),
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun GoalsHistoryList(
    modifier: Modifier = Modifier,
    state: GoalsHistoryState,
    onEvent: (GoalsHistoryEvent) -> Unit,
) {
    val listState = rememberLazyListState()
    val shouldLoadMore by remember(state.history.size, state.canLoadMore, state.isLoadingMore) {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            state.canLoadMore && !state.isLoadingMore && state.history.isNotEmpty() && lastVisibleIndex >= state.history.lastIndex - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onEvent(GoalsHistoryEvent.LoadMore)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (state.isLoading) {
            items(5) {
                PlaceholderBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(116.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                )
            }
        } else if (state.history.isEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(vertical = 40.dp),
                    text = OverviewThemeRes.goalStrings.emptyHistoryTitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            items(
                items = state.history,
                key = { history -> history.id },
            ) { history ->
                GoalHistoryItem(history = history)
            }
            if (state.isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
