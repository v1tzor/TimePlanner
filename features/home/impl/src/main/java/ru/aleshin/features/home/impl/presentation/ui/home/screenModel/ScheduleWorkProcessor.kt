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
 * imitations under the License.
 */
package ru.aleshin.features.home.impl.presentation.ui.home.screenModel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.platform.screenmodel.work.*
import ru.aleshin.features.home.api.domains.common.TimeTaskStatusManager
import ru.aleshin.features.home.api.domains.entities.schedules.status.TimeTaskStatus
import ru.aleshin.features.home.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.home.impl.domain.interactors.TimeShiftInteractor
import ru.aleshin.features.home.impl.presentation.mapppers.ScheduleDomainToUiMapper
import ru.aleshin.features.home.impl.presentation.mapppers.TimeTaskUiToDomainMapper
import ru.aleshin.features.home.impl.presentation.models.TimeTaskUi
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeAction
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import java.util.*
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal interface ScheduleWorkProcessor : FlowWorkProcessor<ScheduleWorkCommand, HomeAction, HomeEffect> {

    suspend fun loadScheduleByDate(date: Date): FlowWorkResult<HomeAction, HomeEffect>
    suspend fun createSchedule(date: Date): FlowWorkResult<HomeAction, HomeEffect>
    suspend fun shiftDownTimeTask(timeTask: TimeTaskUi): FlowWorkResult<HomeAction, HomeEffect>
    suspend fun shiftUpTimeTask(timeTask: TimeTaskUi): FlowWorkResult<HomeAction, HomeEffect>

    class Base @Inject constructor(
        private val scheduleInteractor: ScheduleInteractor,
        private val timeShiftInteractor: TimeShiftInteractor,
        private val mapperToUi: ScheduleDomainToUiMapper,
        private val mapperToDomain: TimeTaskUiToDomainMapper,
        private val statusManager: TimeTaskStatusManager,
        private val dateManager: DateManager,
    ) : ScheduleWorkProcessor {

        override suspend fun createSchedule(date: Date) = work(
            command = ScheduleWorkCommand.CreateSchedule(date),
        )

        override suspend fun loadScheduleByDate(date: Date) = work(
            command = ScheduleWorkCommand.LoadScheduleByDate(date),
        )

        override suspend fun shiftDownTimeTask(timeTask: TimeTaskUi) = work(
            command = ScheduleWorkCommand.TimeTaskShiftDown(timeTask),
        )

        override suspend fun shiftUpTimeTask(timeTask: TimeTaskUi) = work(
            command = ScheduleWorkCommand.TimeTaskShiftUp(timeTask),
        )

        override suspend fun work(command: ScheduleWorkCommand) = when (command) {
            is ScheduleWorkCommand.LoadScheduleByDate -> loadScheduleByDateWork(command.date)
            is ScheduleWorkCommand.CreateSchedule -> createScheduleWork(command.date)
            is ScheduleWorkCommand.TimeTaskShiftDown -> shiftDownTimeWork(command.timeTask)
            is ScheduleWorkCommand.TimeTaskShiftUp -> shiftUpTimeWork(command.timeTask)
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
                            emit(ActionResult(HomeAction.UpdateSchedule(schedule.copy(timeTasks = timeTasks))))
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
                )
                TimeTaskStatus.PLANNED -> copy(
                    executionStatus = status,
                    progress = 0f,
                    leftTime = -1,
                )
                TimeTaskStatus.RUNNING -> copy(
                    executionStatus = status,
                    progress = dateManager.calculateProgress(startTime, endTime),
                    leftTime = dateManager.calculateLeftTime(endTime),
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
            timeShiftInteractor.shiftUpTimeTask(timeTask.map(mapperToDomain), shiftValue).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }

        private suspend fun shiftDownTimeWork(timeTask: TimeTaskUi) = flow {
            val shiftValue = 5
            timeShiftInteractor.shiftDownTimeTask(timeTask.map(mapperToDomain), shiftValue).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }
    }
}

internal sealed class ScheduleWorkCommand : WorkCommand {
    data class LoadScheduleByDate(val date: Date) : ScheduleWorkCommand()
    data class CreateSchedule(val date: Date) : ScheduleWorkCommand()
    data class TimeTaskShiftUp(val timeTask: TimeTaskUi) : ScheduleWorkCommand()
    data class TimeTaskShiftDown(val timeTask: TimeTaskUi) : ScheduleWorkCommand()
}
