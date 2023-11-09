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

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import ru.aleshin.core.ui.views.ViewToggleStatus
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.functional.handleAndGet
import ru.aleshin.core.utils.functional.rightOrElse
import ru.aleshin.core.utils.functional.rightOrError
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.platform.screenmodel.work.*
import ru.aleshin.features.editor.api.presentation.TimeTaskAlarmManager
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTaskStatus
import ru.aleshin.features.home.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.home.impl.domain.interactors.SettingsInteractor
import ru.aleshin.features.home.impl.domain.interactors.TimeShiftInteractor
import ru.aleshin.features.home.impl.presentation.common.TimeTaskStatusController
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.ScheduleDomainToUiMapper
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.mapToDomain
import ru.aleshin.features.home.impl.presentation.models.schedules.ScheduleUi
import ru.aleshin.features.home.impl.presentation.models.schedules.TimeTaskUi
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeAction
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import java.util.*
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal interface ScheduleWorkProcessor : FlowWorkProcessor<ScheduleWorkCommand, HomeAction, HomeEffect> {

    class Base @Inject constructor(
        private val scheduleInteractor: ScheduleInteractor,
        private val timeShiftInteractor: TimeShiftInteractor,
        private val settingsInteractor: SettingsInteractor,
        private val mapperToUi: ScheduleDomainToUiMapper,
        private val statusController: TimeTaskStatusController,
        private val dateManager: DateManager,
        private val timeTaskAlarmManager: TimeTaskAlarmManager,
    ) : ScheduleWorkProcessor {

        override suspend fun work(command: ScheduleWorkCommand) = when (command) {
            is ScheduleWorkCommand.SetupSettings -> setupSettings()
            is ScheduleWorkCommand.LoadScheduleByDate -> loadScheduleByDateWork(command.date)
            is ScheduleWorkCommand.ChangeTaskDoneState -> changeTaskDoneStateWork(command.date, command.key)
            is ScheduleWorkCommand.ChangeTaskViewStatus -> changeTaskViewStatus(command.status)
            is ScheduleWorkCommand.CreateSchedule -> createScheduleWork(command.date)
            is ScheduleWorkCommand.TimeTaskShiftDown -> shiftDownTimeWork(command.timeTask)
            is ScheduleWorkCommand.TimeTaskShiftUp -> shiftUpTimeWork(command.timeTask)
        }

        private fun setupSettings() = flow {
            settingsInteractor.fetchTasksSettings().collectAndHandle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
                onRightAction = { emit(ActionResult(HomeAction.SetupSettings(it))) },
            )
        }

        private suspend fun loadScheduleByDateWork(date: Date?) = flow {
            val sendDate = scheduleInteractor.fetchFeatureScheduleDate()
            val scheduleDate = sendDate ?: date ?: dateManager.fetchBeginningCurrentDay()
            scheduleInteractor.fetchScheduleByDate(scheduleDate.time).collectAndHandle(
                onLeftAction = { error -> emit(EffectResult(HomeEffect.ShowError(error))) },
                onRightAction = { schedule ->
                    if (schedule != null) {
                        refreshScheduleState(schedule.map(mapperToUi))
                    } else {
                        emit(ActionResult(HomeAction.SetEmptySchedule(scheduleDate, null)))
                    }
                },
            )
        }
        
        private suspend fun FlowCollector<WorkResult<HomeAction, HomeEffect>>.refreshScheduleState(
            schedule: ScheduleUi,
        ) {
            var oldTimeTasks = schedule.timeTasks
            var isWorking = true
            while (isWorking) {
                val newTimeTasks = oldTimeTasks.map { statusController.updateStatus(it) }
                if (newTimeTasks != oldTimeTasks || schedule.timeTasks == oldTimeTasks) {
                    oldTimeTasks = newTimeTasks
                    val newSchedule = schedule.copy(timeTasks = oldTimeTasks)
                    emit(ActionResult(HomeAction.UpdateSchedule(newSchedule)))
                    if (newTimeTasks.find { it.executionStatus != TimeTaskStatus.COMPLETED } != null) {
                        scheduleInteractor.updateSchedule(newSchedule.mapToDomain())
                    }
                }
                if (isWorking) delay(Constants.Delay.CHECK_STATUS)
                isWorking = oldTimeTasks.find { it.executionStatus != TimeTaskStatus.COMPLETED } != null
            }
        }

        private fun changeTaskDoneStateWork(date: Date, key: Long) = flow {
            val schedule = scheduleInteractor.fetchScheduleByDate(date.time).firstOrNull()?.rightOrElse(null)
            if (schedule != null) {
                val timeTasks = schedule.timeTasks.toMutableList().apply {
                    val oldTimeTaskIndex = indexOfFirst { it.key == key }
                    val oldTimeTask = get(oldTimeTaskIndex)
                    val newTimeTask = oldTimeTask.copy(isCompleted = !oldTimeTask.isCompleted)
                    set(oldTimeTaskIndex, newTimeTask)
                }
                val newSchedule = schedule.copy(timeTasks = timeTasks)
                scheduleInteractor.updateSchedule(newSchedule).handle(
                    onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
                )
            }
        }

        private fun changeTaskViewStatus(status: ViewToggleStatus) = flow {
            val oldSettings = settingsInteractor.fetchTasksSettings().first().rightOrError("Error get tasks settings!")
            val newSettings = oldSettings.copy(taskViewStatus = status)
            settingsInteractor.updateTasksSettings(newSettings).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }

        private suspend fun createScheduleWork(date: Date) = flow {
            scheduleInteractor.createSchedule(date).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }

        private suspend fun shiftUpTimeWork(timeTask: TimeTaskUi) = flow {
            val shiftValue = Constants.Date.SHIFT_MINUTE_VALUE
            timeShiftInteractor.shiftUpTimeTask(timeTask.mapToDomain(), shiftValue).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
                onRightAction = { nextShiftTask ->
                    if (nextShiftTask != null) notifyUpdate(nextShiftTask)
                },
            )
        }

        private suspend fun shiftDownTimeWork(timeTask: TimeTaskUi) = flow {
            val shiftValue = Constants.Date.SHIFT_MINUTE_VALUE
            timeShiftInteractor.shiftDownTimeTask(timeTask.mapToDomain(), shiftValue).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }

        private fun notifyUpdate(timeTask: TimeTask) {
            if (timeTask.isEnableNotification) {
                val currentTime = dateManager.fetchCurrentDate()
                if (timeTask.timeRange.from > currentTime) {
                    timeTaskAlarmManager.deleteNotifyAlarm(timeTask)
                    timeTaskAlarmManager.addOrUpdateNotifyAlarm(timeTask)
                }
            }
        }
    }
}

internal sealed class ScheduleWorkCommand : WorkCommand {
    object SetupSettings : ScheduleWorkCommand()
    data class LoadScheduleByDate(val date: Date?) : ScheduleWorkCommand()
    data class CreateSchedule(val date: Date) : ScheduleWorkCommand()
    data class ChangeTaskDoneState(val date: Date, val key: Long) : ScheduleWorkCommand()
    data class ChangeTaskViewStatus(val status: ViewToggleStatus) : ScheduleWorkCommand()
    data class TimeTaskShiftUp(val timeTask: TimeTaskUi) : ScheduleWorkCommand()
    data class TimeTaskShiftDown(val timeTask: TimeTaskUi) : ScheduleWorkCommand()
}
