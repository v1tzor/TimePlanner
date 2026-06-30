/*
 * Copyright 2025 Stanislav Aleshin
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
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkCommand.ChangeTaskViewStatus
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
                launchBackgroundWork(BackgroundKey.SETUP_SETTINGS) {
                    val setupCommand = ScheduleWorkCommand.SetupSettings
                    scheduleWorkProcessor.work(setupCommand).collectAndHandleWork()
                }
                if (!isRestore) {
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
                val date = checkNotNull(state().selectedDate)
                val changeStatusCommand = ChangeTaskDoneState(date, event.timeTask.key)
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
            is HomeEvent.PressEditTimeTaskButton -> {
                val navCommand = NavigateToEditor(timeTask = event.timeTask)
                navigationWorkProcessor.work(navCommand).handleWork()
            }
            is HomeEvent.PressAddTimeTaskButton -> {
                val navCommand = NavigateToEditorCreator(
                    currentDate = checkNotNull(state().selectedDate),
                    timeRange = TimeRange(event.startTime, event.endTime),
                )
                navigationWorkProcessor.work(navCommand).handleWork()
            }
            is HomeEvent.PressOverviewButton -> {
                val navCommand = NavigationWorkCommand.NavigateToOverview
                navigationWorkProcessor.work(navCommand).handleWork()
            }
        }
    }

    override suspend fun reduce(
        action: HomeAction,
        currentState: HomeState,
    ) = when (action) {
        is HomeAction.Navigate -> currentState.copy()
        is HomeAction.SetupSettings -> currentState.copy(
            taskViewStatus = action.settings.taskViewStatus,
            calendarButtonBehavior = action.settings.calendarButtonBehavior,
        )
        is HomeAction.SetEmptySchedule -> currentState.copy(
            timeTasks = emptyList(),
            selectedDate = action.date,
            dateStatus = action.status,
        )
        is HomeAction.UpdateSchedule -> currentState.copy(
            timeTasks = action.schedule.timeTasks,
            selectedDate = action.schedule.date,
            dateStatus = action.schedule.dateStatus,
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_SCHEDULE, SETUP_SETTINGS, CREATE_SCHEDULE, DATA_ACTION
    }

     class Factory @Inject constructor(
         private val scheduleWorkProcessor: ScheduleWorkProcessor,
         private val navigationWorkProcessor: NavigationWorkProcessor,
         private val dateManager: DateManager,
         private val coroutineManager: CoroutineManager,
     ) : BaseComposeStore.Factory<HomeComposeStore, HomeState> {

         override fun create(savedState: HomeState): HomeComposeStore {
             return HomeComposeStore(
                 dateManager = dateManager,
                 scheduleWorkProcessor = scheduleWorkProcessor,
                 navigationWorkProcessor = navigationWorkProcessor,
                 stateCommunicator = StateCommunicator.Default(savedState),
                 effectCommunicator = EffectCommunicator.Default(),
                 coroutineManager = coroutineManager,
             )
         }
     }
}