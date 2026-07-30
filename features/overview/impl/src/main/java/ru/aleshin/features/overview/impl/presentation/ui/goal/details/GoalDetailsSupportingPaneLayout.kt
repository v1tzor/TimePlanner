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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsEvent
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsState
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.views.GoalTaskItem
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.views.sections.GoalMetricsSection
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.views.sections.GoalSummarySection
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.views.sections.GoalTasksEmptyCard
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun GoalDetailsSupportingPaneLayout(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    useTwoPanesOnMediumWidth: Boolean = false,
    showPaneExpansionDragHandle: Boolean = false,
    onEvent: (GoalDetailsEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPaneMinWidth = AdaptiveLayoutDefaults.GoalDetailsMainPaneMinWidth,
        supportingPaneMinWidth = AdaptiveLayoutDefaults.GoalDetailsSupportingPaneMinWidth,
        supportingPanePreferredWidth = AdaptiveLayoutDefaults.SupportingPanePreferredWidth,
        useTwoPanesOnMediumWidth = useTwoPanesOnMediumWidth,
        showPaneExpansionDragHandle = showPaneExpansionDragHandle,
        mainPane = {
            GoalDetailsMainPane(state = state)
        },
        supportingPane = {
            GoalDetailsSupportingPane(
                state = state,
                onEvent = onEvent,
            )
        },
    )
}

@Composable
private fun GoalDetailsMainPane(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
) {
    val details = state.details

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            bottom = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (state.isLoading) {
            items(
                count = 2,
                key = { index -> "$MAIN_PANE_PLACEHOLDER_KEY_PREFIX$index" },
            ) {
                PlaceholderBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                )
            }
        } else if (details != null) {
            item(key = SUMMARY_SECTION_KEY) {
                GoalSummarySection(details = details)
            }
            item(key = METRICS_SECTION_KEY) {
                GoalMetricsSection(details = details)
            }
        }
    }
}

@Composable
private fun GoalDetailsSupportingPane(
    modifier: Modifier = Modifier,
    state: GoalDetailsState,
    onEvent: (GoalDetailsEvent) -> Unit,
) {
    val details = state.details

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (state.isLoading) {
            items(
                count = 3,
                key = { index -> "$SUPPORTING_PANE_PLACEHOLDER_KEY_PREFIX$index" },
            ) {
                PlaceholderBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                )
            }
        } else if (details != null) {
            item(key = TASKS_HEADER_KEY) {
                Text(
                    text = OverviewThemeRes.strings.schedulesHeader,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            if (details.contributingTasks.isEmpty()) {
                item(key = TASKS_EMPTY_STATE_KEY) {
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

private const val MAIN_PANE_PLACEHOLDER_KEY_PREFIX = "main_pane_placeholder_"
private const val SUPPORTING_PANE_PLACEHOLDER_KEY_PREFIX = "supporting_pane_placeholder_"
private const val SUMMARY_SECTION_KEY = "summary_section"
private const val METRICS_SECTION_KEY = "metrics_section"
private const val TASKS_HEADER_KEY = "tasks_header"
private const val TASKS_EMPTY_STATE_KEY = "tasks_empty_state"
