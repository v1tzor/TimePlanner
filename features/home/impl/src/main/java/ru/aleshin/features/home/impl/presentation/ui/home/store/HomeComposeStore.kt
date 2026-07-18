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
package ru.aleshin.features.home.impl.presentation.ui.home.store

import ru.aleshin.core.domain.entities.settings.ViewToggleStatus
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeAction
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEvent
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeInput
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeOutput
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeState
import ru.aleshin.features.home.impl.presentation.ui.home.store.NavigationWorkCommand.NavigateToEditor
import ru.aleshin.features.home.impl.presentation.ui.home.store.NavigationWorkCommand.NavigateToEditorCreator
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkCommand.ChangeTaskDoneState
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkCommand.ChangeTimelineTaskDoneState
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkCommand.ChangeTaskViewStatus
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkCommand.ChangeHomeViewMode
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkCommand.CreateSchedule
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkCommand.LoadScheduleByDate
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkCommand.TimeTaskShiftDown
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkCommand.TimeTaskShiftUp
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
internal class HomeComposeStore @Inject constructor(
    private val scheduleWorkProcessor: ScheduleWorkProcessor,
    private val navigationWorkProcessor: NavigationWorkProcessor,
    private val timelineWorkProcessor: TimelineWorkProcessor,
    private val dateManager: DateManager,
    stateCommunicator: StateCommunicator<HomeState>,
    effectCommunicator: EffectCommunicator<HomeEffect>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<HomeState, HomeEvent, HomeAction, HomeEffect, HomeInput, HomeOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: HomeInput, isRestore: Boolean) {
        dispatchEvent(HomeEvent.Init(input, isRestore))
    }

    override suspend fun WorkScope<HomeState, HomeAction, HomeEffect, HomeOutput>.handleEvent(
        event: HomeEvent,
    ) {
        when (event) {
            is HomeEvent.Init -> with(event) {
                launchBackgroundWork(BackgroundKey.CURRENT_TIME) {
                    val command = TimelineWorkCommand.ObserveCurrentTime
                    timelineWorkProcessor.work(command).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.SETUP_SETTINGS) {
                    val setupCommand = ScheduleWorkCommand.SetupSettings
                    scheduleWorkProcessor.work(setupCommand).collectAndHandleWork()
                }
                if (!isRestore || state.selectedDate == null) {
                    launchBackgroundWork(BackgroundKey.LOAD_SCHEDULE) {
                        val date = input.scheduleDate ?: dateManager.fetchBeginningCurrentDay()
                        val command = LoadScheduleByDate(date)
                        scheduleWorkProcessor.work(command).collectAndHandleWork()
                    }
                } else {
                    launchBackgroundWork(BackgroundKey.LOAD_SCHEDULE) {
                        val date = state.selectedDate
                        val command = LoadScheduleByDate(date)
                        scheduleWorkProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is HomeEvent.LoadSchedule -> launchBackgroundWork(BackgroundKey.LOAD_SCHEDULE) {
                val command = LoadScheduleByDate(event.date)
                scheduleWorkProcessor.work(command).collectAndHandleWork()
            }
            is HomeEvent.SelectedCurrentDate -> launchBackgroundWork(BackgroundKey.LOAD_SCHEDULE) {
                val date = dateManager.fetchBeginningCurrentDay()
                val command = LoadScheduleByDate(date)
                scheduleWorkProcessor.work(command).collectAndHandleWork()
            }
            is HomeEvent.CreateSchedule -> launchBackgroundWork(BackgroundKey.CREATE_SCHEDULE){
                val currentDate = checkNotNull(state().selectedDate)
                val createCommand = CreateSchedule(currentDate)
                scheduleWorkProcessor.work(createCommand).collectAndHandleWork()
            }
            is HomeEvent.TimeTaskShiftUp -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val shiftUpCommand = TimeTaskShiftUp(event.timeTask)
                scheduleWorkProcessor.work(shiftUpCommand).collectAndHandleWork()
            }
            is HomeEvent.TimeTaskShiftDown -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val shiftDownCommand = TimeTaskShiftDown(event.timeTask)
                scheduleWorkProcessor.work(shiftDownCommand).collectAndHandleWork()
            }
            is HomeEvent.ChangeTaskDoneStateButton -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val changeStatusCommand = ChangeTaskDoneState(event.timeTask)
                scheduleWorkProcessor.work(changeStatusCommand).collectAndHandleWork()
            }
            is HomeEvent.ChangeTimelineTaskDoneStateButton -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val changeStatusCommand = ChangeTimelineTaskDoneState(event.timeTask)
                scheduleWorkProcessor.work(changeStatusCommand).collectAndHandleWork()
            }
            is HomeEvent.PressViewToggleButton -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val status = when (event.status) {
                    ViewToggleStatus.EXPANDED -> ViewToggleStatus.COMPACT
                    ViewToggleStatus.COMPACT -> ViewToggleStatus.EXPANDED
                }
                val changeCommand = ChangeTaskViewStatus(status)
                scheduleWorkProcessor.work(changeCommand).collectAndHandleWork()
            }
            is HomeEvent.ChangeHomeViewMode -> launchBackgroundWork(BackgroundKey.SETTINGS_ACTION) {
                val command = ChangeHomeViewMode(event.mode)
                scheduleWorkProcessor.work(command).collectAndHandleWork()
            }
            is HomeEvent.UpdateTimelineTimeTask -> launchBackgroundWork(BackgroundKey.DATA_ACTION) {
                val command = TimelineWorkCommand.UpdateTimeTask(
                    timeTaskId = event.timeTaskId,
                    timeRange = event.timeRange,
                )
                timelineWorkProcessor.work(command).collectAndHandleWork()
            }
            is HomeEvent.PressEditTimeTaskButton -> {
                val navCommand = NavigateToEditor(timeTaskId = event.timeTask.key)
                navigationWorkProcessor.work(navCommand).handleWork()
            }
            is HomeEvent.PressEditTimelineTimeTaskButton -> {
                val navCommand = NavigateToEditor(timeTaskId = event.timeTaskId)
                navigationWorkProcessor.work(navCommand).handleWork()
            }
            is HomeEvent.PressAddTimeTaskButton -> {
                val navCommand = NavigateToEditorCreator(
                    currentDate = checkNotNull(state().selectedDate),
                    timeRange = TimeRange(event.startTime, event.endTime),
                )
                navigationWorkProcessor.work(navCommand).handleWork()
            }
            is HomeEvent.PressAddTimeTaskFab -> {
                val navCommand = NavigateToEditorCreator(
                    currentDate = checkNotNull(state().selectedDate),
                    timeRange = null,
                )
                navigationWorkProcessor.work(navCommand).handleWork()
            }
            is HomeEvent.PressSettingsButton -> {
                val navCommand = NavigationWorkCommand.NavigateToSettings
                navigationWorkProcessor.work(navCommand).handleWork()
            }
        }
    }

    override suspend fun reduce(
        action: HomeAction,
        currentState: HomeState,
    ) = when (action) {
        is HomeAction.UpdateSettings -> currentState.copy(
            taskViewStatus = action.settings.taskViewStatus,
            homeViewMode = action.settings.homeViewMode,
            calendarButtonBehavior = action.settings.calendarButtonBehavior,
        )
        is HomeAction.UpdateCurrentTime -> currentState.copy(
            currentTime = action.currentTime,
        )
        is HomeAction.UpdateSchedule -> currentState.copy(
            schedule = action.schedule,
            timelineSchedule = action.timelineSchedule,
            selectedDate = action.date
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_SCHEDULE, SETUP_SETTINGS, SETTINGS_ACTION, CREATE_SCHEDULE, DATA_ACTION, CURRENT_TIME
    }

     class Factory @Inject constructor(
         private val scheduleWorkProcessor: ScheduleWorkProcessor,
         private val navigationWorkProcessor: NavigationWorkProcessor,
         private val timelineWorkProcessor: TimelineWorkProcessor,
         private val dateManager: DateManager,
         private val coroutineManager: CoroutineManager,
     ) : BaseComposeStore.Factory<HomeComposeStore, HomeState> {

         override fun create(savedState: HomeState): HomeComposeStore {
             return HomeComposeStore(
                 dateManager = dateManager,
                 scheduleWorkProcessor = scheduleWorkProcessor,
                 navigationWorkProcessor = navigationWorkProcessor,
                 timelineWorkProcessor = timelineWorkProcessor,
                 stateCommunicator = StateCommunicator.Default(savedState),
                 effectCommunicator = EffectCommunicator.Default(),
                 coroutineManager = coroutineManager,
             )
         }
     }
}
