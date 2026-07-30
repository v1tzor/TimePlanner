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
package ru.aleshin.features.overview.impl.presentation.ui.overview

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewEvent
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewState
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.sections.GoalsSection
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.sections.SelectedDaySection
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.sections.UndefinedTaskSection
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.sections.WeekTimelineSection
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun OverviewSupportingPaneLayout(
    modifier: Modifier = Modifier,
    state: OverviewState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    useTwoPanesOnMediumWidth: Boolean = false,
    showPaneExpansionDragHandle: Boolean = false,
    onEvent: (OverviewEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPaneMinWidth = AdaptiveLayoutDefaults.OverviewMainPaneMinWidth,
        supportingPaneMinWidth = AdaptiveLayoutDefaults.OverviewSupportingPaneMinWidth,
        supportingPanePreferredWidth = AdaptiveLayoutDefaults.SupportingPanePreferredWidth,
        useTwoPanesOnMediumWidth = useTwoPanesOnMediumWidth,
        showPaneExpansionDragHandle = showPaneExpansionDragHandle,
        mainPane = {
            OverviewMainPane(
                state = state,
                scrollState = mainScrollState,
                onEvent = onEvent,
            )
        },
        supportingPane = {
            OverviewSupportingPane(
                state = state,
                scrollState = supportingScrollState,
                onEvent = onEvent,
            )
        },
    )
}

@Composable
private fun OverviewMainPane(
    modifier: Modifier = Modifier,
    state: OverviewState,
    scrollState: ScrollState,
    onEvent: (OverviewEvent) -> Unit,
) {
    val weekOverview = state.weekOverview

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.OverviewContentMaxWidth)
                .fillMaxSize()
                .verticalScroll(
                    state = scrollState,
                    enabled = !state.isLoading,
                )
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            GoalsSection(
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isLoading,
                goals = state.goals,
                horizontalPadding = 16.dp,
                onGoalClick = { goalId -> onEvent(OverviewEvent.OpenGoal(goalId)) },
                onHistoryClick = { onEvent(OverviewEvent.OpenGoalsHistory) },
                onAddClick = { onEvent(OverviewEvent.CreateGoal) },
            )
            WeekTimelineSection(
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isLoading,
                selectedDate = state.selectedDate,
                schedules = weekOverview.schedules,
                weekTasksCount = weekOverview.tasksCount,
                horizontalPadding = 16.dp,
                useCompactSize = false,
                onSelectSchedule = { date -> onEvent(OverviewEvent.SelectSchedule(date)) },
            )
            SelectedDaySection(
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isLoading,
                selectedDate = state.selectedDate,
                schedules = weekOverview.schedules,
                horizontalPadding = 16.dp,
                useParentScroll = true,
                onOpenTimeTask = { task -> onEvent(OverviewEvent.OpenTimeTask(task)) },
            )
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun OverviewSupportingPane(
    modifier: Modifier = Modifier,
    state: OverviewState,
    scrollState: ScrollState,
    onEvent: (OverviewEvent) -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(
                end = 16.dp,
                bottom = 16.dp,
            ),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = scrollState,
                    enabled = !state.isLoading,
                )
                .padding(vertical = 16.dp),
        ) {
            UndefinedTaskSection(
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isLoading,
                categories = state.categories,
                tasks = state.undefinedTasks,
                horizontalPadding = 16.dp,
                onAddOrUpdateTask = { task ->
                    onEvent(OverviewEvent.CreateOrUpdateUndefinedTask(task))
                },
                onExecuteTask = { date, task ->
                    onEvent(OverviewEvent.ExecuteUndefinedTask(date, task))
                },
            )
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
