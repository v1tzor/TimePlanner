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
import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.functional.rightOrElse
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.platform.screenmodel.work.*
import ru.aleshin.features.home.api.domain.common.TimeTaskStatusManager
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTaskStatus
import ru.aleshin.features.home.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.home.impl.domain.interactors.TimeShiftInteractor
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.ScheduleDomainToUiMapper
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.mapToDomain
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
        private val mapperToUi: ScheduleDomainToUiMapper,
        private val statusManager: TimeTaskStatusManager,
        private val dateManager: DateManager,
    ) : ScheduleWorkProcessor {

        override suspend fun work(command: ScheduleWorkCommand) = when (command) {
            is ScheduleWorkCommand.LoadScheduleByDate -> loadScheduleByDateWork(command.date)
            is ScheduleWorkCommand.CreateSchedule -> createScheduleWork(command.date)
            is ScheduleWorkCommand.TimeTaskShiftDown -> shiftDownTimeWork(command.timeTask)
            is ScheduleWorkCommand.TimeTaskShiftUp -> shiftUpTimeWork(command.timeTask)
            is ScheduleWorkCommand.ChangeDoneState -> changeDoneStateWork(command.date, command.key)
        }

        private fun changeDoneStateWork(date: Date, key: Long) = flow {
            val schedule = scheduleInteractor.fetchScheduleByDate(date.time).rightOrElse(null)
            if (schedule != null) {
                val timeTasks = schedule.timeTasks.toMutableList().apply {
                    val index = indexOfFirst { it.key == key }
                    val changedTimeTask = get(index)
                    val newTimeTask = changedTimeTask.copy(isCompleted = !changedTimeTask.isCompleted)
                    set(index, newTimeTask)
                }
                val newSchedule = schedule.copy(timeTasks = timeTasks)
                scheduleInteractor.updateSchedule(newSchedule).handle(
                    onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
                )
            }
        }

        private suspend fun loadScheduleByDateWork(date: Date) = flow {
            scheduleInteractor.fetchScheduleByDate(date.time).handle(
                onRightAction = { domainSchedule ->
                    if (domainSchedule != null) {
                        val schedule = domainSchedule.map(mapperToUi)
                        var timeTasks = schedule.timeTasks
                        var isWorking = true
                        while (isWorking) {
                            timeTasks = timeTasks.map { it.updateTimeTask() }
                            val newSchedule = schedule.copy(timeTasks = timeTasks)
                            emit(ActionResult(HomeAction.UpdateSchedule(newSchedule)))
                            scheduleInteractor.updateSchedule(newSchedule.mapToDomain())
                            isWorking = timeTasks.find { it.executionStatus != TimeTaskStatus.COMPLETED } != null
                            if (isWorking) delay(Constants.Delay.CHECK_STATUS)
                        }
                    } else {
                        emit(ActionResult(HomeAction.UpdateDate(date, null)))
                    }
                },
                onLeftAction = { error -> emit(EffectResult(HomeEffect.ShowError(error))) },
            )
        }

        private fun TimeTaskUi.updateTimeTask(): TimeTaskUi {
            val currentTime = dateManager.fetchCurrentDate()
            val timeRange = TimeRange(startTime, endTime)
            return when (val status = statusManager.fetchStatus(timeRange, currentTime)) {
                TimeTaskStatus.COMPLETED -> copy(
                    executionStatus = status,
                    progress = 1f,
                    leftTime = 0,
                    isCompleted = !(executionStatus == TimeTaskStatus.COMPLETED && !isCompleted),
                )
                TimeTaskStatus.PLANNED -> copy(
                    executionStatus = status,
                    progress = 0f,
                    leftTime = -1,
                    isCompleted = true,
                )
                TimeTaskStatus.RUNNING -> copy(
                    executionStatus = status,
                    progress = dateManager.calculateProgress(startTime, endTime),
                    leftTime = dateManager.calculateLeftTime(endTime),
                    isCompleted = true,
                )
            }
        }

        private suspend fun createScheduleWork(date: Date) = flow {
            scheduleInteractor.createSchedule(date).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }

        private suspend fun shiftUpTimeWork(timeTask: TimeTaskUi) = flow {
            val shiftValue = 5
            timeShiftInteractor.shiftUpTimeTask(timeTask.mapToDomain(), shiftValue).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }

        private suspend fun shiftDownTimeWork(timeTask: TimeTaskUi) = flow {
            val shiftValue = 5
            timeShiftInteractor.shiftDownTimeTask(timeTask.mapToDomain(), shiftValue).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }
    }
}

internal sealed class ScheduleWorkCommand : WorkCommand {
    data class LoadScheduleByDate(val date: Date) : ScheduleWorkCommand()
    data class CreateSchedule(val date: Date) : ScheduleWorkCommand()
    data class ChangeDoneState(val date: Date, val key: Long) : ScheduleWorkCommand()
    data class TimeTaskShiftUp(val timeTask: TimeTaskUi) : ScheduleWorkCommand()
    data class TimeTaskShiftDown(val timeTask: TimeTaskUi) : ScheduleWorkCommand()
}
