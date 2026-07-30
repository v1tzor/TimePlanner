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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.home.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEvent
import ru.aleshin.features.home.impl.presentation.ui.home.store.HomeComponent
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeDatePicker
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeTopAppBar
import ru.aleshin.features.home.impl.presentation.ui.home.views.sections.HomeDateControlsSection
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Composable
internal fun HomeContent(
    modifier: Modifier = Modifier,
    component: HomeComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = component.store
    val state by store.stateAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isDatePickerOpen by rememberSaveable { mutableStateOf(false) }
    val strings = HomeThemeRes.strings
    val layoutMode = adaptiveLayoutInfo.fetchHomeLayoutMode()
    val pendingTimelineTaskUpdate = state.pendingTimelineTaskUpdate

    LaunchedEffect(state.timelineSchedule, pendingTimelineTaskUpdate) {
        val pendingRequest = pendingTimelineTaskUpdate ?: return@LaunchedEffect
        val persistedTimeRange = state.timelineSchedule
            ?.timeTasks
            ?.find { timeTask -> timeTask.timeTask.key == pendingRequest.timeTaskId }
            ?.timeTask
            ?.timeRanges
        if (persistedTimeRange == pendingRequest.timeRange) {
            store.dispatchEvent(HomeEvent.ConfirmTimelineTimeTaskUpdate(pendingRequest))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (layoutMode.showScreenTopAppBar) {
                HomeTopAppBar(
                    isCompact = adaptiveLayoutInfo.isCompactWidth,
                    calendarIconBehavior = state.calendarButtonBehavior,
                    onSettingsIconClick = { store.dispatchEvent(HomeEvent.PressSettingsButton) },
                    onOpenCalendar = { isDatePickerOpen = true },
                    onGoToToday = { store.dispatchEvent(HomeEvent.SelectedCurrentDate) },
                )
            }
        },
        bottomBar = {
            if (layoutMode.showDateBottomBar) {
                HomeDateControlsSection(
                    selectedDate = state.selectedDate,
                    toggleState = state.taskViewStatus,
                    isToggleVisible = state.homeViewMode == HomeViewMode.AGENDA,
                    onDateChange = { date ->
                        store.dispatchEvent(HomeEvent.LoadSchedule(date))
                    },
                    onOpenCalendar = { isDatePickerOpen = true },
                    onViewToggleChange = { status ->
                        store.dispatchEvent(HomeEvent.PressViewToggleButton(status))
                    },
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                ErrorSnackbar(snackbarData = snackbarData)
            }
        },
        contentWindowInsets = WindowInsets(),
    ) { contentPadding ->
        HomeLayout(
            modifier = Modifier.padding(contentPadding),
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            layoutMode = layoutMode,
            onOpenCalendar = { isDatePickerOpen = true },
            onSettingsClick = { store.dispatchEvent(HomeEvent.PressSettingsButton) },
            onEvent = store::dispatchEvent,
        )
    }

    if (isDatePickerOpen) {
        HomeDatePicker(
            onDismiss = { isDatePickerOpen = false },
            onDateSelect = { date ->
                isDatePickerOpen = false
                store.dispatchEvent(HomeEvent.LoadSchedule(date))
            },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is HomeEffect.ShowError -> snackbarHostState.showSnackbar(
                message = effect.failures.mapToMessage(strings),
                withDismissAction = true,
            )
        }
    }
}
