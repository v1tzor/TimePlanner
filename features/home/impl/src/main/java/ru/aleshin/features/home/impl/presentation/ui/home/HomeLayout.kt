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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.features.home.impl.presentation.theme.tokens.HomeLayoutDefaults
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEvent
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeState
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeDateChooser
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeSchedulePane
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
    onOpenCalendar: () -> Unit,
    onSettingsClick: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
) {
    when (layoutMode) {
        HomeLayoutMode.COMPACT -> HomeSchedulePane(
            modifier = modifier,
            state = state,
            contentMaxWidth = null,
            onEvent = onEvent,
        )
        HomeLayoutMode.MEDIUM -> HomeSchedulePane(
            modifier = modifier,
            state = state,
            contentMaxWidth = when (state.homeViewMode) {
                HomeViewMode.AGENDA -> HomeLayoutDefaults.MediumAgendaMaxWidth
                HomeViewMode.TIMELINE -> HomeLayoutDefaults.TimelineMaxWidth
            },
            onEvent = onEvent,
        )
        HomeLayoutMode.SUPPORTING_PANE -> HomeSupportingPaneLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            contentMaxWidth = when (state.homeViewMode) {
                HomeViewMode.AGENDA -> HomeLayoutDefaults.ExpandedAgendaMaxWidth
                HomeViewMode.TIMELINE -> HomeLayoutDefaults.TimelineMaxWidth
            },
            onOpenCalendar = onOpenCalendar,
            onSettingsClick = onSettingsClick,
            onEvent = onEvent,
        )
        HomeLayoutMode.BOOK -> HomeSupportingPaneLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            contentMaxWidth = null,
            onOpenCalendar = onOpenCalendar,
            onSettingsClick = onSettingsClick,
            onEvent = onEvent,
        )
        HomeLayoutMode.TABLETOP -> HomeTabletopLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onOpenCalendar = onOpenCalendar,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun HomeTabletopLayout(
    modifier: Modifier = Modifier,
    state: HomeState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onOpenCalendar: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPane = {
            HomeSchedulePane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                contentMaxWidth = null,
                onEvent = onEvent,
            )
        },
        supportingPane = {
            HomeTabletopControlsPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onOpenCalendar = onOpenCalendar,
                onEvent = onEvent,
            )
        },
    )
}

@Composable
private fun HomeTabletopControlsPane(
    modifier: Modifier = Modifier,
    state: HomeState,
    onOpenCalendar: () -> Unit,
    onEvent: (HomeEvent) -> Unit,
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.widthIn(max = HomeLayoutDefaults.SupportingPanePreferredWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            HomeDateChooser(
                modifier = Modifier.fillMaxWidth(),
                selectedDate = state.selectedDate,
                onDateChange = { date ->
                    onEvent(HomeEvent.LoadSchedule(date))
                },
                onOpenCalendar = onOpenCalendar,
            )
            AnimatedVisibility(
                visible = state.homeViewMode == HomeViewMode.AGENDA,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut(),
            ) {
                ViewToggle(
                    status = state.taskViewStatus,
                    onStatusChange = { status ->
                        onEvent(HomeEvent.PressViewToggleButton(status))
                    },
                )
            }
        }
    }
}
