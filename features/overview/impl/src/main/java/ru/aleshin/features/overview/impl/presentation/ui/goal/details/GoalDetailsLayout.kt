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
package ru.aleshin.features.overview.impl.presentation.ui.goal.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsEvent
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsState
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.views.GoalMetricsSection
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.views.GoalSummarySection
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.views.GoalTaskItem
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.views.GoalTasksEmptyCard
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun GoalDetailsLayout(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onEvent: (GoalDetailsEvent) -> Unit,
) {
    when {
        adaptiveLayoutInfo.isCompactWidth -> GoalDetailsCompactLayout(
            modifier = modifier,
            state = state,
            onEvent = onEvent,
        )
        adaptiveLayoutInfo.isMediumWidth -> GoalDetailsMediumLayout(
            modifier = modifier,
            state = state,
            onEvent = onEvent,
        )
        adaptiveLayoutInfo.isBookPosture -> GoalDetailsBookLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onEvent = onEvent,
        )
        adaptiveLayoutInfo.isTabletopPosture -> GoalDetailsTabletopLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onEvent = onEvent,
        )
        else -> GoalDetailsExpandedLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun GoalDetailsCompactLayout(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
    onEvent: (GoalDetailsEvent) -> Unit,
) {
    GoalDetailsList(
        modifier = modifier,
        state = state,
        onEvent = onEvent,
    )
}

@Composable
private fun GoalDetailsMediumLayout(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
    onEvent: (GoalDetailsEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        GoalDetailsList(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.MediumContentMaxWidth)
                .fillMaxWidth(),
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun GoalDetailsExpandedLayout(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onEvent: (GoalDetailsEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        GoalDetailsExpandedContent(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.OverviewContentMaxWidth)
                .fillMaxSize(),
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            showPaneExpansionDragHandle = true,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun GoalDetailsBookLayout(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onEvent: (GoalDetailsEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        GoalDetailsExpandedContent(
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            useTwoPanesOnMediumWidth = true,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun GoalDetailsTabletopLayout(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onEvent: (GoalDetailsEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        GoalDetailsExpandedContent(
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun GoalDetailsExpandedContent(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    useTwoPanesOnMediumWidth: Boolean = false,
    showPaneExpansionDragHandle: Boolean = false,
    onEvent: (GoalDetailsEvent) -> Unit,
) {
    val details = state.details

    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPaneMinWidth = AdaptiveLayoutDefaults.GoalDetailsMainPaneMinWidth,
        supportingPaneMinWidth = AdaptiveLayoutDefaults.GoalDetailsSupportingPaneMinWidth,
        supportingPanePreferredWidth = AdaptiveLayoutDefaults.SupportingPanePreferredWidth,
        useTwoPanesOnMediumWidth = useTwoPanesOnMediumWidth,
        showPaneExpansionDragHandle = showPaneExpansionDragHandle,
        mainPane = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (state.isLoading) {
                    items(2) {
                        PlaceholderBox(
                            modifier = Modifier.fillMaxWidth().height(112.dp),
                            shape = MaterialTheme.shapes.extraLarge,
                        )
                    }
                } else if (details != null) {
                    item(key = "summary") {
                        GoalSummarySection(details = details)
                    }
                    item(key = "metrics") {
                        GoalMetricsSection(details = details)
                    }
                }
            }
        },
        supportingPane = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (state.isLoading) {
                    items(3) {
                        PlaceholderBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(88.dp),
                            shape = MaterialTheme.shapes.extraLarge,
                        )
                    }
                } else if (details != null) {
                    item(key = "tasks_title") {
                        Text(
                            text = OverviewThemeRes.strings.schedulesHeader,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    if (details.contributingTasks.isEmpty()) {
                        item(key = "tasks_empty") {
                            GoalTasksEmptyCard()
                        }
                    } else {
                        items(
                            items = details.contributingTasks,
                            key = { task -> task.key },
                        ) { task ->
                            GoalTaskItem(
                                task = task,
                                onClick = { onEvent(GoalDetailsEvent.PressTask(task)) },
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun GoalDetailsList(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
    onEvent: (GoalDetailsEvent) -> Unit,
) {
    val details = state.details
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (state.isLoading) {
            items(4) {
                PlaceholderBox(
                    modifier = Modifier.fillMaxWidth().height(96.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                )
            }
        } else if (details != null) {
            item(key = "summary") {
                GoalSummarySection(details = details)
            }
            item(key = "metrics") {
                GoalMetricsSection(details = details)
            }
            item(key = "tasks_title") {
                Text(
                    text = OverviewThemeRes.strings.schedulesHeader,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            if (details.contributingTasks.isEmpty()) {
                item(key = "tasks_empty") {
                    GoalTasksEmptyCard()
                }
            } else {
                items(
                    items = details.contributingTasks,
                    key = { task -> task.key },
                ) { task ->
                    GoalTaskItem(
                        task = task,
                        onClick = { onEvent(GoalDetailsEvent.PressTask(task)) },
                    )
                }
            }
        }
    }
}
