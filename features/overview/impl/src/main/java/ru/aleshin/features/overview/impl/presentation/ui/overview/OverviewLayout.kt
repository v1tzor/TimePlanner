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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun OverviewLayout(
    modifier: Modifier = Modifier,
    state: OverviewState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onRefresh: () -> Unit,
    onEvent: (OverviewEvent) -> Unit,
) {
    val mainScrollState = rememberScrollState()
    val supportingScrollState = rememberScrollState()
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = modifier,
        state = pullToRefreshState,
        isRefreshing = state.isLoading,
        onRefresh = onRefresh,
    ) {
        when (OverviewLayoutMode.from(adaptiveLayoutInfo)) {
            OverviewLayoutMode.COMPACT -> OverviewSinglePaneLayout(
                modifier = Modifier.fillMaxSize(),
                state = state,
                scrollState = mainScrollState,
                onEvent = onEvent,
            )
            OverviewLayoutMode.MEDIUM -> OverviewMediumLayout(
                modifier = Modifier.fillMaxSize(),
                state = state,
                scrollState = mainScrollState,
                onEvent = onEvent,
            )
            OverviewLayoutMode.EXPANDED -> OverviewSupportingPaneLayout(
                modifier = Modifier.fillMaxSize(),
                state = state,
                adaptiveLayoutInfo = adaptiveLayoutInfo,
                mainScrollState = mainScrollState,
                supportingScrollState = supportingScrollState,
                showPaneExpansionDragHandle = true,
                onEvent = onEvent,
            )
            OverviewLayoutMode.BOOK -> OverviewSupportingPaneLayout(
                modifier = Modifier.fillMaxSize(),
                state = state,
                adaptiveLayoutInfo = adaptiveLayoutInfo,
                mainScrollState = mainScrollState,
                supportingScrollState = supportingScrollState,
                useTwoPanesOnMediumWidth = true,
                onEvent = onEvent,
            )
            OverviewLayoutMode.TABLETOP -> OverviewSupportingPaneLayout(
                modifier = Modifier.fillMaxSize(),
                state = state,
                adaptiveLayoutInfo = adaptiveLayoutInfo,
                mainScrollState = mainScrollState,
                supportingScrollState = supportingScrollState,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun OverviewMediumLayout(
    modifier: Modifier = Modifier,
    state: OverviewState,
    scrollState: ScrollState,
    onEvent: (OverviewEvent) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        OverviewSinglePaneLayout(
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
private fun OverviewSinglePaneLayout(
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
            isLoading = state.isLoading,
            goals = state.goals,
            onGoalClick = { goalId -> onEvent(OverviewEvent.OpenGoal(goalId)) },
            onHistoryClick = { onEvent(OverviewEvent.OpenGoalsHistory) },
            onAddClick = { onEvent(OverviewEvent.CreateGoal) },
        )
        WeekTimelineSection(
            isLoading = state.isLoading,
            selectedDate = state.selectedDate,
            schedules = weekOverview.schedules,
            weekTasksCount = weekOverview.tasksCount,
            onSelectSchedule = { date -> onEvent(OverviewEvent.SelectSchedule(date)) },
        )
        SelectedDaySection(
            isLoading = state.isLoading,
            selectedDate = state.selectedDate,
            schedules = weekOverview.schedules,
            onOpenTimeTask = { task -> onEvent(OverviewEvent.OpenTimeTask(task)) },
        )
        UndefinedTaskSection(
            isLoading = state.isLoading,
            categories = state.categories,
            tasks = state.undefinedTasks,
            isPaneSection = false,
            onAddOrUpdateTask = { task ->
                onEvent(OverviewEvent.CreateOrUpdateUndefinedTask(task))
            },
            onExecuteTask = { date, task ->
                onEvent(OverviewEvent.ExecuteUndefinedTask(date, task))
            },
            onDeleteTask = { taskId ->
                onEvent(OverviewEvent.DeleteUndefinedTask(taskId))
            },
        )
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Immutable
private enum class OverviewLayoutMode {
    COMPACT,
    MEDIUM,
    EXPANDED,
    BOOK,
    TABLETOP;

    companion object {

        fun from(adaptiveLayoutInfo: AdaptiveLayoutInfo): OverviewLayoutMode = when {
            adaptiveLayoutInfo.isTabletopPosture -> TABLETOP
            adaptiveLayoutInfo.isBookPosture -> BOOK
            adaptiveLayoutInfo.isCompactWidth -> COMPACT
            adaptiveLayoutInfo.isMediumWidth -> MEDIUM
            else -> EXPANDED
        }
    }
}
