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
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.SelectedDaySection
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.GoalsSection
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.UndefinedTaskSection
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.WeekTimelineSection
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
@Composable
internal fun OverviewLayout(
    modifier: Modifier = Modifier,
    state: OverviewState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    layoutMode: OverviewLayoutMode,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    onEvent: (OverviewEvent) -> Unit,
) {
    when (layoutMode) {
        OverviewLayoutMode.COMPACT -> OverviewCompactLayout(
            modifier = modifier,
            state = state,
            scrollState = mainScrollState,
            onEvent = onEvent,
        )
        OverviewLayoutMode.MEDIUM -> OverviewMediumLayout(
            modifier = modifier,
            state = state,
            scrollState = mainScrollState,
            onEvent = onEvent,
        )
        OverviewLayoutMode.EXPANDED -> OverviewExpandedLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            mainScrollState = mainScrollState,
            supportingScrollState = supportingScrollState,
            onEvent = onEvent,
        )
        OverviewLayoutMode.BOOK -> OverviewBookLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            mainScrollState = mainScrollState,
            supportingScrollState = supportingScrollState,
            onEvent = onEvent,
        )
        OverviewLayoutMode.TABLETOP -> OverviewTabletopLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            mainScrollState = mainScrollState,
            supportingScrollState = supportingScrollState,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun OverviewCompactLayout(
    modifier: Modifier = Modifier,
    state: OverviewState,
    scrollState: ScrollState,
    onEvent: (OverviewEvent) -> Unit,
) {
    OverviewSingleColumn(
        modifier = modifier,
        state = state,
        scrollState = scrollState,
        onEvent = onEvent,
    )
}

@Composable
private fun OverviewMediumLayout(
    modifier: Modifier = Modifier,
    state: OverviewState,
    scrollState: ScrollState,
    onEvent: (OverviewEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        OverviewSingleColumn(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.MediumContentMaxWidth)
                .fillMaxWidth(),
            state = state,
            scrollState = scrollState,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun OverviewExpandedLayout(
    modifier: Modifier = Modifier,
    state: OverviewState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    onEvent: (OverviewEvent) -> Unit,
) {
    OverviewSupportingLayout(
        modifier = modifier,
        state = state,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainScrollState = mainScrollState,
        supportingScrollState = supportingScrollState,
        showPaneExpansionDragHandle = true,
        onEvent = onEvent,
    )
}

@Composable
private fun OverviewBookLayout(
    modifier: Modifier = Modifier,
    state: OverviewState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    onEvent: (OverviewEvent) -> Unit,
) {
    OverviewSupportingLayout(
        modifier = modifier,
        state = state,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainScrollState = mainScrollState,
        supportingScrollState = supportingScrollState,
        useTwoPanesOnMediumWidth = true,
        onEvent = onEvent,
    )
}

@Composable
private fun OverviewTabletopLayout(
    modifier: Modifier = Modifier,
    state: OverviewState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    onEvent: (OverviewEvent) -> Unit,
) {
    OverviewSupportingLayout(
        modifier = modifier,
        state = state,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainScrollState = mainScrollState,
        supportingScrollState = supportingScrollState,
        onEvent = onEvent,
    )
}

@Composable
private fun OverviewSupportingLayout(
    modifier: Modifier = Modifier,
    state: OverviewState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    useTwoPanesOnMediumWidth: Boolean = false,
    showPaneExpansionDragHandle: Boolean = false,
    onEvent: (OverviewEvent) -> Unit,
) {
    val weekOverview = state.weekOverview

    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPaneMinWidth = AdaptiveLayoutDefaults.OverviewMainPaneMinWidth,
        supportingPaneMinWidth = AdaptiveLayoutDefaults.OverviewSupportingPaneMinWidth,
        supportingPanePreferredWidth = AdaptiveLayoutDefaults.SupportingPanePreferredWidth,
        useTwoPanesOnMediumWidth = useTwoPanesOnMediumWidth,
        showPaneExpansionDragHandle = showPaneExpansionDragHandle,
        mainPane = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = AdaptiveLayoutDefaults.OverviewContentMaxWidth)
                        .fillMaxSize()
                        .verticalScroll(
                            state = mainScrollState,
                            enabled = !state.isLoading,
                        )
                        .padding(top = AdaptiveLayoutDefaults.SpaceLarge),
                    verticalArrangement = Arrangement.spacedBy(
                        AdaptiveLayoutDefaults.SpaceExtraLarge,
                    ),
                ) {
                    GoalsSection(
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = state.isGoalsLoading,
                        goals = state.goals,
                        horizontalPadding = AdaptiveLayoutDefaults.SpaceLarge,
                        onGoalClick = { goalId ->
                            onEvent(OverviewEvent.OpenGoal(goalId))
                        },
                        onHistoryClick = {
                            onEvent(OverviewEvent.OpenGoalsHistory)
                        },
                        onAddClick = {
                            onEvent(OverviewEvent.CreateGoal)
                        },
                    )
                    WeekTimelineSection(
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = state.isLoading,
                        selectedDate = state.selectedDate,
                        schedules = weekOverview.schedules,
                        weekTasksCount = weekOverview.tasksCount,
                        useCompactSize = false,
                        onSelectSchedule = { date ->
                            onEvent(OverviewEvent.SelectSchedule(date))
                        },
                    )
                    SelectedDaySection(
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = state.isLoading,
                        selectedDate = state.selectedDate,
                        schedules = weekOverview.schedules,
                        useParentScroll = true,
                        onOpenTimeTask = { task ->
                            onEvent(OverviewEvent.OpenTimeTask(task))
                        },
                    )
                    Spacer(modifier = Modifier.height(AdaptiveLayoutDefaults.SpaceScreen))
                }
            }
        },
        supportingPane = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        end = AdaptiveLayoutDefaults.SpaceLarge,
                        bottom = AdaptiveLayoutDefaults.SpaceLarge,
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            state = supportingScrollState,
                            enabled = !state.isLoading,
                        )
                        .padding(vertical = AdaptiveLayoutDefaults.SpaceLarge),
                ) {
                    UndefinedTaskSection(
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = state.isLoading,
                        categories = state.categories,
                        tasks = state.undefinedTasks,
                        horizontalPadding = AdaptiveLayoutDefaults.SpaceLarge,
                        onAddOrUpdateTask = { task ->
                            onEvent(OverviewEvent.CreateOrUpdateUndefinedTask(task))
                        },
                        onExecuteTask = { date, task ->
                            onEvent(OverviewEvent.ExecuteUndefinedTask(date, task))
                        },
                    )
                    Spacer(modifier = Modifier.height(AdaptiveLayoutDefaults.SpaceScreen))
                }
            }
        },
    )
}

@Composable
private fun OverviewSingleColumn(
    modifier: Modifier = Modifier,
    state: OverviewState,
    scrollState: ScrollState,
    onEvent: (OverviewEvent) -> Unit,
) {
    val weekOverview = state.weekOverview
    Column(
        modifier = modifier
            .verticalScroll(
                state = scrollState,
                enabled = !state.isLoading,
            )
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        GoalsSection(
            isLoading = state.isGoalsLoading,
            goals = state.goals,
            onGoalClick = { goalId ->
                onEvent(OverviewEvent.OpenGoal(goalId))
            },
            onHistoryClick = {
                onEvent(OverviewEvent.OpenGoalsHistory)
            },
            onAddClick = {
                onEvent(OverviewEvent.CreateGoal)
            },
        )
        WeekTimelineSection(
            isLoading = state.isLoading,
            selectedDate = state.selectedDate,
            schedules = weekOverview.schedules,
            weekTasksCount = weekOverview.tasksCount,
            onSelectSchedule = { date ->
                onEvent(OverviewEvent.SelectSchedule(date))
            },
        )
        SelectedDaySection(
            isLoading = state.isLoading,
            selectedDate = state.selectedDate,
            schedules = weekOverview.schedules,
            onOpenTimeTask = { task ->
                onEvent(OverviewEvent.OpenTimeTask(task))
            },
        )
        UndefinedTaskSection(
            isLoading = state.isLoading,
            categories = state.categories,
            tasks = state.undefinedTasks,
            onAddOrUpdateTask = { task ->
                onEvent(OverviewEvent.CreateOrUpdateUndefinedTask(task))
            },
            onExecuteTask = { date, task ->
                onEvent(OverviewEvent.ExecuteUndefinedTask(date, task))
            },
        )
        Spacer(modifier = Modifier.height(60.dp))
    }
}
