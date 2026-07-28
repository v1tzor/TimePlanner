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
package ru.aleshin.features.home.impl.presentation.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import ru.aleshin.core.domain.entities.settings.CalendarButtonBehavior
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEvent
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeState
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeCalendarPane
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeDateChooser
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeExpandedTopAppBar
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeViewTabs
import ru.aleshin.features.home.impl.presentation.ui.home.views.agenda.AgendaTab
import ru.aleshin.features.home.impl.presentation.ui.home.views.timeline.TimelineTab
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold
import ru.aleshin.timeplanner.core.ui.views.ViewToggle

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
@Composable
internal fun HomeLayout(
    modifier: Modifier = Modifier,
    state: HomeState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    layoutMode: HomeLayoutMode,
    calendarIconBehavior: CalendarButtonBehavior,
    onOpenCalendar: () -> Unit,
    onSettingsClick: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
) {
    when (layoutMode) {
        HomeLayoutMode.COMPACT -> HomeCompactLayout(
            modifier = modifier,
            state = state,
            onEvent = onEvent,
        )
        HomeLayoutMode.MEDIUM -> HomeMediumLayout(
            modifier = modifier,
            state = state,
            onEvent = onEvent,
        )
        HomeLayoutMode.EXPANDED -> HomeExpandedLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            calendarIconBehavior = calendarIconBehavior,
            onOpenCalendar = onOpenCalendar,
            onSettingsClick = onSettingsClick,
            onEvent = onEvent,
        )
        HomeLayoutMode.BOOK -> HomeBookLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            calendarIconBehavior = calendarIconBehavior,
            onOpenCalendar = onOpenCalendar,
            onSettingsClick = onSettingsClick,
            onEvent = onEvent,
        )
        HomeLayoutMode.TABLETOP -> HomeTabletopLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun HomeCompactLayout(
    modifier: Modifier = Modifier,
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
) {
    HomeScheduleContent(
        modifier = modifier,
        state = state,
        contentMaxWidth = null,
        onEvent = onEvent,
    )
}

@Composable
private fun HomeMediumLayout(
    modifier: Modifier = Modifier,
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
) {
    HomeConstrainedLayout(
        modifier = modifier,
        state = state,
        agendaMaxWidth = AdaptiveLayoutDefaults.MediumContentMaxWidth,
        timelineMaxWidth = AdaptiveLayoutDefaults.HomeTimelineMaxWidth,
        onEvent = onEvent,
    )
}

@Composable
private fun HomeExpandedLayout(
    modifier: Modifier = Modifier,
    state: HomeState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    calendarIconBehavior: CalendarButtonBehavior,
    onOpenCalendar: () -> Unit,
    onSettingsClick: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPane = {
            HomeExpandedMainPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                contentMaxWidth = if (state.homeViewMode == HomeViewMode.AGENDA) {
                    AdaptiveLayoutDefaults.HomeAgendaMaxWidth
                } else {
                    AdaptiveLayoutDefaults.HomeTimelineMaxWidth
                },
                calendarIconBehavior = calendarIconBehavior,
                onOpenCalendar = onOpenCalendar,
                onSettingsClick = onSettingsClick,
                onEvent = onEvent,
            )
        },
        supportingPane = {
            HomeExpandedCalendarPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onEvent = onEvent,
            )
        },
    )
}

@Composable
private fun HomeBookLayout(
    modifier: Modifier = Modifier,
    state: HomeState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    calendarIconBehavior: CalendarButtonBehavior,
    onOpenCalendar: () -> Unit,
    onSettingsClick: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPane = {
            HomeExpandedMainPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                contentMaxWidth = null,
                calendarIconBehavior = calendarIconBehavior,
                onOpenCalendar = onOpenCalendar,
                onSettingsClick = onSettingsClick,
                onEvent = onEvent,
            )
        },
        supportingPane = {
            HomeExpandedCalendarPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onEvent = onEvent,
            )
        },
    )
}

@Composable
private fun HomeTabletopLayout(
    modifier: Modifier = Modifier,
    state: HomeState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onEvent: (HomeEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPane = {
            HomeScheduleContent(
                modifier = Modifier.fillMaxSize(),
                state = state,
                contentMaxWidth = null,
                onEvent = onEvent,
            )
        },
        supportingPane = {
            HomeFoldControls(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onEvent = onEvent,
            )
        },
    )
}

@Composable
private fun HomeConstrainedLayout(
    modifier: Modifier = Modifier,
    state: HomeState,
    agendaMaxWidth: Dp,
    timelineMaxWidth: Dp,
    onEvent: (HomeEvent) -> Unit,
) {
    val contentMaxWidth = if (state.homeViewMode == HomeViewMode.AGENDA) {
        agendaMaxWidth
    } else {
        timelineMaxWidth
    }
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        HomeScheduleContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            contentMaxWidth = contentMaxWidth,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun HomeExpandedMainPane(
    modifier: Modifier = Modifier,
    state: HomeState,
    contentMaxWidth: Dp?,
    calendarIconBehavior: CalendarButtonBehavior,
    onOpenCalendar: () -> Unit,
    onSettingsClick: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeExpandedTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            selectedDate = state.selectedDate,
            calendarIconBehavior = calendarIconBehavior,
            onSettingsIconClick = onSettingsClick,
            onOpenCalendar = onOpenCalendar,
            onGoToToday = { onEvent(HomeEvent.SelectedCurrentDate) },
        )
        HomeScheduleContent(
            modifier = Modifier.fillMaxWidth().weight(1f),
            state = state,
            contentMaxWidth = contentMaxWidth,
            timelineTaskMaxWidth = AdaptiveLayoutDefaults.HomeTimelineTaskMaxWidth,
            showTabs = false,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun HomeExpandedCalendarPane(
    modifier: Modifier = Modifier,
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
) {
    HomeCalendarPane(
        modifier = modifier,
        selectedDate = state.selectedDate,
        selectedMode = state.homeViewMode,
        toggleState = state.taskViewStatus,
        onDateChange = { onEvent(HomeEvent.LoadSchedule(it)) },
        onModeChange = { onEvent(HomeEvent.ChangeHomeViewMode(it)) },
        onToggleChange = { onEvent(HomeEvent.PressViewToggleButton(it)) },
    )
}

@Composable
private fun HomeFoldControls(
    modifier: Modifier = Modifier,
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
) {
    Box(
        modifier = modifier.padding(AdaptiveLayoutDefaults.MediumHorizontalPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.widthIn(max = AdaptiveLayoutDefaults.SupportingPanePreferredWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.PaneSpacing),
        ) {
            HomeDateChooser(
                modifier = Modifier.fillMaxWidth(),
                selectedDate = state.selectedDate,
                onChangeDate = { onEvent(HomeEvent.LoadSchedule(it)) },
            )
            AnimatedVisibility(
                visible = state.homeViewMode == HomeViewMode.AGENDA,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut(),
            ) {
                ViewToggle(
                    status = state.taskViewStatus,
                    onStatusChange = { onEvent(HomeEvent.PressViewToggleButton(it)) },
                )
            }
        }
    }
}

@Composable
private fun HomeScheduleContent(
    modifier: Modifier = Modifier,
    state: HomeState,
    contentMaxWidth: Dp?,
    timelineTaskMaxWidth: Dp? = null,
    showTabs: Boolean = true,
    onEvent: (HomeEvent) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (showTabs) {
            HomeViewTabs(
                modifier = Modifier.fillMaxWidth(),
                selectedMode = state.homeViewMode,
                onModeChange = { onEvent(HomeEvent.ChangeHomeViewMode(it)) },
            )
        }
        AnimatedContent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            targetState = state.homeViewMode to state.selectedDate?.time,
            contentAlignment = Alignment.TopCenter,
            transitionSpec = {
                val direction = when {
                    targetState.first != initialState.first -> {
                        if (targetState.first.ordinal > initialState.first.ordinal) 1 else -1
                    }
                    else -> if ((targetState.second ?: 0L) > (initialState.second ?: 0L)) 1 else -1
                }
                (fadeIn() + slideInHorizontally { width -> width / 6 * direction }).togetherWith(
                    fadeOut() + slideOutHorizontally { width -> -width / 6 * direction },
                )
            },
            label = "HomeContent",
        ) { (mode, _) ->
            val contentModifier = if (contentMaxWidth == null) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .fillMaxHeight()
                    .widthIn(max = contentMaxWidth)
                    .fillMaxWidth()
            }
            when (mode) {
                HomeViewMode.AGENDA -> AgendaTab(
                    modifier = contentModifier,
                    state = state,
                    onCreateSchedule = { onEvent(HomeEvent.CreateSchedule) },
                    onTimeTaskEdit = { onEvent(HomeEvent.PressEditTimeTaskButton(it)) },
                    onTaskDoneChange = { onEvent(HomeEvent.ChangeTaskDoneStateButton(it)) },
                    onTimeTaskAdd = { start, end ->
                        onEvent(HomeEvent.PressAddTimeTaskButton(start, end))
                    },
                    onTimeTaskIncrease = { onEvent(HomeEvent.TimeTaskShiftUp(it)) },
                    onTimeTaskReduce = { onEvent(HomeEvent.TimeTaskShiftDown(it)) },
                )
                HomeViewMode.TIMELINE -> state.timelineSchedule?.let { timelineSchedule ->
                    TimelineTab(
                        modifier = contentModifier,
                        schedule = timelineSchedule,
                        currentTime = state.currentTime,
                        pendingTimeTaskUpdate = state.pendingTimelineTaskUpdate,
                        failedTimeTaskUpdate = state.failedTimelineTaskUpdate,
                        taskMaxWidth = timelineTaskMaxWidth,
                        onTimeTaskEdit = {
                            onEvent(HomeEvent.PressEditTimelineTimeTaskButton(it))
                        },
                        onTaskDoneChange = {
                            onEvent(HomeEvent.ChangeTimelineTaskDoneStateButton(it))
                        },
                        onTimeTaskAdd = { start, end ->
                            onEvent(HomeEvent.PressAddTimeTaskButton(start, end))
                        },
                        onTimeTaskUpdate = {
                            onEvent(HomeEvent.UpdateTimelineTimeTask(it))
                        },
                        onAddClick = { onEvent(HomeEvent.PressAddTimeTaskFab) },
                    )
                }
            }
        }
    }
}
