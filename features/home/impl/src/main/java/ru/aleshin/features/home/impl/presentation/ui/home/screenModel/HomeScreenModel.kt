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
package ru.aleshin.features.home.impl.presentation.ui.home.screenModel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.ui.views.ViewToggleStatus
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.work.BackgroundWorkKey
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.features.home.impl.di.holder.HomeComponentHolder
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeAction
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEvent
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeViewState
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
internal class HomeScreenModel @Inject constructor(
    private val scheduleWorkProcessor: ScheduleWorkProcessor,
    private val navigationWorkProcessor: NavigationWorkProcessor,
    stateCommunicator: HomeStateCommunicator,
    effectCommunicator: HomeEffectCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<HomeViewState, HomeEvent, HomeAction, HomeEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun init() {
        if (!isInitialize.get()) {
            dispatchEvent(HomeEvent.Init)
            super.init()
        }
    }

    override suspend fun WorkScope<HomeViewState, HomeAction, HomeEffect>.handleEvent(
        event: HomeEvent,
    ) {
        when (event) {
            is HomeEvent.Init -> launchBackgroundWork(HomeWorkKey.SETUP_SETTINGS) {
                val setupCommand = ScheduleWorkCommand.SetupSettings
                scheduleWorkProcessor.work(setupCommand).collectAndHandleWork()
            }
            is HomeEvent.LoadSchedule -> loadSchedule(event.date)
            is HomeEvent.CreateSchedule -> {
                val currentDate = checkNotNull(state().currentDate)
                val createCommand = ScheduleWorkCommand.CreateSchedule(currentDate)
                scheduleWorkProcessor.work(createCommand).collectAndHandleWork()
            }
            is HomeEvent.PressEditTimeTaskButton -> {
                val navCommand = NavigationWorkCommand.NavigateToEditor(timeTask = event.timeTask)
                navigationWorkProcessor.work(navCommand).handleWork()
            }
            is HomeEvent.PressAddTimeTaskButton -> {
                val navCommand = NavigationWorkCommand.NavigateToEditorCreator(
                    currentDate = checkNotNull(state().currentDate),
                    timeRange = TimeRange(event.startTime, event.endTime),
                )
                navigationWorkProcessor.work(navCommand).handleWork()
            }
            is HomeEvent.PressOverviewButton -> {
                val navCommand = NavigationWorkCommand.NavigateToOverview
                navigationWorkProcessor.work(navCommand).handleWork()
            }
            is HomeEvent.TimeTaskShiftUp -> {
                val shiftUpCommand = ScheduleWorkCommand.TimeTaskShiftUp(event.timeTask)
                scheduleWorkProcessor.work(shiftUpCommand).collectAndHandleWork()
                loadSchedule(checkNotNull(state().currentDate))
            }
            is HomeEvent.TimeTaskShiftDown -> {
                val shiftDownCommand = ScheduleWorkCommand.TimeTaskShiftDown(event.timeTask)
                scheduleWorkProcessor.work(shiftDownCommand).collectAndHandleWork()
                loadSchedule(checkNotNull(state().currentDate))
            }
            is HomeEvent.ChangeTaskDoneStateButton -> {
                val date = checkNotNull(state().currentDate)
                val changeStatusCommand = ScheduleWorkCommand.ChangeTaskDoneState(date, event.timeTask.key)
                scheduleWorkProcessor.work(changeStatusCommand).collectAndHandleWork()
                loadSchedule(checkNotNull(state().currentDate))
            }
            is HomeEvent.PressViewToggleButton -> {
                val status = when (event.status) {
                    ViewToggleStatus.EXPANDED -> ViewToggleStatus.COMPACT
                    ViewToggleStatus.COMPACT -> ViewToggleStatus.EXPANDED
                }
                val changeCommand = ScheduleWorkCommand.ChangeTaskViewStatus(status)
                scheduleWorkProcessor.work(changeCommand).collectAndHandleWork()
            }
        }
    }

    override suspend fun reduce(
        action: HomeAction,
        currentState: HomeViewState,
    ) = when (action) {
        is HomeAction.Navigate -> currentState.copy()
        is HomeAction.SetupSettings -> currentState.copy(
            taskViewStatus = action.settings.taskViewStatus,
            calendarButtonBehavior = action.settings.calendarButtonBehavior,
        )
        is HomeAction.SetEmptySchedule -> currentState.copy(
            timeTasks = emptyList(),
            currentDate = action.date,
            dateStatus = action.status,
        )
        is HomeAction.UpdateSchedule -> currentState.copy(
            timeTasks = action.schedule.timeTasks,
            currentDate = action.schedule.date,
            dateStatus = action.schedule.dateStatus,
        )
    }

    override fun onDispose() {
        super.onDispose()
        HomeComponentHolder.clear()
    }

    private suspend fun WorkScope<HomeViewState, HomeAction, HomeEffect>.loadSchedule(date: Date?) {
        val loadCommand = ScheduleWorkCommand.LoadScheduleByDate(date)
        launchBackgroundWork(HomeWorkKey.SUBSCRIBE_SCHEDULE) {
            scheduleWorkProcessor.work(loadCommand).collectAndHandleWork()
        }
    }
}

internal enum class HomeWorkKey : BackgroundWorkKey {
    SUBSCRIBE_SCHEDULE, SETUP_SETTINGS
}

@Composable
internal fun Screen.rememberHomeScreenModel() = rememberScreenModel {
    HomeComponentHolder.fetchComponent().fetchHomeScreenModel()
}
