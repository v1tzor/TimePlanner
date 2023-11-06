/*
 * Copyright 2023 Stanislav Aleshin
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

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import ru.aleshin.core.ui.views.ErrorSnackbar
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.managers.LocalDrawerManager
import ru.aleshin.core.utils.platform.screen.ScreenContent
import ru.aleshin.features.home.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEvent
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeViewState
import ru.aleshin.features.home.impl.presentation.ui.home.screenModel.rememberHomeScreenModel
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeDatePicker
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeTopAppBar
import java.util.Date

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
internal class HomeScreen : Screen {

    @Composable
    override fun Content() = ScreenContent(
        screenModel = rememberHomeScreenModel(),
        initialState = HomeViewState(),
    ) { state ->
        val scope = rememberCoroutineScope()
        val snackbarState = remember { SnackbarHostState() }
        var isDateDialogShow by rememberSaveable { mutableStateOf(false) }
        val drawerManager = LocalDrawerManager.current
        val strings = HomeThemeRes.strings

        Scaffold(
            content = { paddingValues ->
                HomeContent(
                    state = state,
                    modifier = Modifier.padding(paddingValues),
                    onChangeDate = { date -> dispatchEvent(HomeEvent.LoadSchedule(date)) },
                    onTimeTaskEdit = { dispatchEvent(HomeEvent.PressEditTimeTaskButton(it)) },
                    onTaskDoneChange = { dispatchEvent(HomeEvent.ChangeTaskDoneStateButton(it)) },
                    onTimeTaskAdd = { start, end -> dispatchEvent(HomeEvent.PressAddTimeTaskButton(start, end)) },
                    onCreateSchedule = { dispatchEvent(HomeEvent.CreateSchedule) },
                    onTimeTaskIncrease = { dispatchEvent(HomeEvent.TimeTaskShiftUp(it)) },
                    onTimeTaskReduce = { dispatchEvent(HomeEvent.TimeTaskShiftDown(it)) },
                    onChangeToggleStatus = { dispatchEvent(HomeEvent.PressViewToggleButton(it)) },
                )
            },
            topBar = {
                HomeTopAppBar(
                    calendarIconBehavior = state.calendarButtonBehavior,
                    onMenuIconClick = { scope.launch { drawerManager?.openDrawer() } },
                    onOverviewIconClick = { dispatchEvent(HomeEvent.PressOverviewButton) },
                    onOpenCalendar = { isDateDialogShow = true },
                    onGoToToday = { dispatchEvent(HomeEvent.LoadSchedule(Date().startThisDay())) },
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarState) { snackbarData ->
                    ErrorSnackbar(snackbarData)
                }
            },
        )

        HomeDatePicker(
            isOpenDialog = isDateDialogShow,
            onDismiss = { isDateDialogShow = false },
            onSelectedDate = {
                isDateDialogShow = false
                dispatchEvent(HomeEvent.LoadSchedule(it))
            },
        )

        handleEffect { effect ->
            when (effect) {
                is HomeEffect.ShowError -> snackbarState.showSnackbar(
                    message = effect.failures.mapToMessage(strings),
                    withDismissAction = true,
                )
            }
        }

        LaunchedEffect(Unit) {
            dispatchEvent(HomeEvent.LoadSchedule(state.currentDate))
        }
    }
}
