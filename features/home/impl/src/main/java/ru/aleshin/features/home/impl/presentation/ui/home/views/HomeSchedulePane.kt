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
package ru.aleshin.features.home.impl.presentation.ui.home.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEvent
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeState
import ru.aleshin.features.home.impl.presentation.ui.home.views.agenda.AgendaTab
import ru.aleshin.features.home.impl.presentation.ui.home.views.timeline.TimelineTab

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun HomeSchedulePane(
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
                onModeChange = { mode ->
                    onEvent(HomeEvent.ChangeHomeViewMode(mode))
                },
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
                    else -> {
                        if ((targetState.second ?: 0L) > (initialState.second ?: 0L)) 1 else -1
                    }
                }
                (fadeIn() + slideInHorizontally { width -> width / 6 * direction }).togetherWith(
                    fadeOut() + slideOutHorizontally { width -> -width / 6 * direction },
                )
            },
            label = "HomeSchedulePane",
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
                    onTimeTaskEdit = { timeTask ->
                        onEvent(HomeEvent.PressEditTimeTaskButton(timeTask))
                    },
                    onTaskDoneChange = { timeTask ->
                        onEvent(HomeEvent.ChangeTaskDoneStateButton(timeTask))
                    },
                    onTimeTaskAdd = { startTime, endTime ->
                        onEvent(HomeEvent.PressAddTimeTaskButton(startTime, endTime))
                    },
                    onTimeTaskIncrease = { timeTask ->
                        onEvent(HomeEvent.TimeTaskShiftUp(timeTask))
                    },
                    onTimeTaskReduce = { timeTask ->
                        onEvent(HomeEvent.TimeTaskShiftDown(timeTask))
                    },
                )
                HomeViewMode.TIMELINE -> {
                    val timelineSchedule = state.timelineSchedule

                    if (timelineSchedule != null) {
                        TimelineTab(
                            modifier = contentModifier,
                            schedule = timelineSchedule,
                            currentTime = state.currentTime,
                            pendingTimeTaskUpdate = state.pendingTimelineTaskUpdate,
                            failedTimeTaskUpdate = state.failedTimelineTaskUpdate,
                            taskMaxWidth = timelineTaskMaxWidth,
                            onTimeTaskEdit = { timeTaskId ->
                                onEvent(HomeEvent.PressEditTimelineTimeTaskButton(timeTaskId))
                            },
                            onTaskDoneChange = { timeTask ->
                                onEvent(HomeEvent.ChangeTimelineTaskDoneStateButton(timeTask))
                            },
                            onTimeTaskAdd = { startTime, endTime ->
                                onEvent(HomeEvent.PressAddTimeTaskButton(startTime, endTime))
                            },
                            onTimeTaskUpdate = { request ->
                                onEvent(HomeEvent.UpdateTimelineTimeTask(request))
                            },
                            onAddClick = { onEvent(HomeEvent.PressAddTimeTaskFab) },
                        )
                    }
                }
            }
        }
    }
}
