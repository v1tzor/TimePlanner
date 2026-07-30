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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import ru.aleshin.features.home.impl.presentation.theme.tokens.HomeLayoutDefaults
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEvent
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeState
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeDatePane
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeMainPaneTopAppBar
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeSchedulePane
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun HomeSupportingPaneLayout(
    modifier: Modifier = Modifier,
    state: HomeState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    contentMaxWidth: Dp?,
    onOpenCalendar: () -> Unit,
    onSettingsClick: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        supportingPanePreferredWidth = HomeLayoutDefaults.SupportingPanePreferredWidth,
        mainPane = {
            HomeMainPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                contentMaxWidth = contentMaxWidth,
                onOpenCalendar = onOpenCalendar,
                onSettingsClick = onSettingsClick,
                onEvent = onEvent,
            )
        },
        supportingPane = {
            HomeDatePane(
                modifier = Modifier.fillMaxSize(),
                selectedDate = state.selectedDate,
                selectedMode = state.homeViewMode,
                toggleState = state.taskViewStatus,
                onDateChange = { date ->
                    onEvent(HomeEvent.LoadSchedule(date))
                },
                onOpenCalendar = onOpenCalendar,
                onModeChange = { mode ->
                    onEvent(HomeEvent.ChangeHomeViewMode(mode))
                },
                onToggleChange = { status ->
                    onEvent(HomeEvent.PressViewToggleButton(status))
                },
            )
        },
    )
}

@Composable
private fun HomeMainPane(
    modifier: Modifier = Modifier,
    state: HomeState,
    contentMaxWidth: Dp?,
    onOpenCalendar: () -> Unit,
    onSettingsClick: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
) {
    Column(modifier = modifier) {
        HomeMainPaneTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            selectedDate = state.selectedDate,
            calendarIconBehavior = state.calendarButtonBehavior,
            onSettingsIconClick = onSettingsClick,
            onOpenCalendar = onOpenCalendar,
            onGoToToday = { onEvent(HomeEvent.SelectedCurrentDate) },
        )
        HomeSchedulePane(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = state,
            contentMaxWidth = contentMaxWidth,
            timelineTaskMaxWidth = HomeLayoutDefaults.TimelineTaskMaxWidth,
            showTabs = false,
            onEvent = onEvent,
        )
    }
}
