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

import kotlinx.coroutines.flow.flow
import ru.aleshin.core.domain.common.TimeTaskProgressManager
import ru.aleshin.core.domain.entities.settings.ViewToggleStatus
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.presentation.mappers.mapToDomain
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.core.presentation.models.tasks.TimeTaskDetailsUi
import ru.aleshin.core.presentation.notifications.TimeTaskAlarmManager
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.firstRightOrNull
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.home.impl.domain.interactors.SettingsInteractor
import ru.aleshin.features.home.impl.domain.interactors.TimeShiftInteractor
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeAction
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeOutput
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal interface ScheduleWorkProcessor :
    FlowWorkProcessor<ScheduleWorkCommand, HomeAction, HomeEffect, HomeOutput> {

    class Base @Inject constructor(
        private val scheduleInteractor: ScheduleInteractor,
        private val timeShiftInteractor: TimeShiftInteractor,
        private val settingsInteractor: SettingsInteractor,
        private val statusController: TimeTaskProgressManager,
        private val dateManager: DateManager,
        private val timeTaskAlarmManager: TimeTaskAlarmManager,
    ) : ScheduleWorkProcessor {

        override suspend fun work(command: ScheduleWorkCommand) = when (command) {
            is ScheduleWorkCommand.SetupSettings -> setupSettings()
            is ScheduleWorkCommand.LoadScheduleByDate -> loadScheduleByDateWork(command.date)
            is ScheduleWorkCommand.CreateSchedule -> createScheduleWork(command.date)
            is ScheduleWorkCommand.ChangeTaskDoneState -> changeTaskDoneStateWork(command.timeTask)
            is ScheduleWorkCommand.TimeTaskShiftDown -> shiftDownTimeWork(command.timeTask)
            is ScheduleWorkCommand.TimeTaskShiftUp -> shiftUpTimeWork(command.timeTask)
            is ScheduleWorkCommand.ChangeTaskViewStatus -> changeTaskViewStatus(command.status)
        }

        private fun setupSettings() = flow {
            settingsInteractor.fetchTasksSettings().collectAndHandle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
                onRightAction = { emit(ActionResult(HomeAction.UpdateSettings(it))) }
            )
        }

        private fun loadScheduleByDateWork(date: Date?) = flow {
            val scheduleDate = date ?: dateManager.fetchBeginningCurrentDay()

            scheduleInteractor.fetchScheduleDetailsByDate(scheduleDate.time).collectAndHandle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
                onRightAction = { schedule ->
                    emit(ActionResult(HomeAction.UpdateSchedule(scheduleDate, schedule?.mapToUi())))
                }
            )
        }

        private fun createScheduleWork(date: Date) = flow {
            scheduleInteractor.createSchedule(date).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }

        private fun changeTaskDoneStateWork(timeTask: TimeTaskDetailsUi) = flow {
            val newTimeTask = timeTask.copy(isCompleted = !timeTask.isCompleted)
            scheduleInteractor.addOrUpdateTimeTask(newTimeTask.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }

        private fun shiftUpTimeWork(timeTask: TimeTaskDetailsUi) = flow {
            val shiftValue = Constants.Date.SHIFT_MINUTE_VALUE
            timeShiftInteractor.shiftUpTimeTask(timeTask.mapToDomain(), shiftValue).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
                onRightAction = { updatedTasks -> updatedTasks.forEach { notifyUpdate(it) } },
            )
        }

        private fun shiftDownTimeWork(timeTask: TimeTaskDetailsUi) = flow {
            val shiftValue = Constants.Date.SHIFT_MINUTE_VALUE
            timeShiftInteractor.shiftDownTimeTask(timeTask.mapToDomain(), shiftValue).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
                onRightAction = { notifyUpdate(it) },
            )
        }

        private fun changeTaskViewStatus(status: ViewToggleStatus) = flow {
            val oldSettings = settingsInteractor.fetchTasksSettings().firstRightOrNull {
                emit(EffectResult(HomeEffect.ShowError(it)))
            }
            val newSettings = oldSettings?.copy(taskViewStatus = status) ?: return@flow
            settingsInteractor.updateTasksSettings(newSettings).handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }

        private fun notifyUpdate(timeTask: TimeTask) {
            if (timeTask.isEnableNotification) {
                timeTaskAlarmManager.deleteNotifyAlarm(timeTask)
                timeTaskAlarmManager.addOrUpdateNotifyAlarm(timeTask)
            }
        }
    }
}

internal sealed class ScheduleWorkCommand : WorkCommand {
    data object SetupSettings : ScheduleWorkCommand()
    data class LoadScheduleByDate(val date: Date?) : ScheduleWorkCommand()
    data class CreateSchedule(val date: Date) : ScheduleWorkCommand()
    data class ChangeTaskDoneState(val timeTask: TimeTaskDetailsUi) : ScheduleWorkCommand()
    data class TimeTaskShiftUp(val timeTask: TimeTaskDetailsUi) : ScheduleWorkCommand()
    data class TimeTaskShiftDown(val timeTask: TimeTaskDetailsUi) : ScheduleWorkCommand()
    data class ChangeTaskViewStatus(val status: ViewToggleStatus) : ScheduleWorkCommand()
}
