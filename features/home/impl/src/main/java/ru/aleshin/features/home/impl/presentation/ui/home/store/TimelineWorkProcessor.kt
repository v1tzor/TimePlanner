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

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.presentation.notifications.TimeTaskAlarmManager
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.impl.domain.interactors.TimelineInteractor
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeAction
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
internal interface TimelineWorkProcessor :
    FlowWorkProcessor<TimelineWorkCommand, HomeAction, HomeEffect, HomeOutput> {

    class Base @Inject constructor(
        private val timelineInteractor: TimelineInteractor,
        private val dateManager: DateManager,
        private val timeTaskAlarmManager: TimeTaskAlarmManager,
    ) : TimelineWorkProcessor {

        private val mutationMutex = Mutex()

        override suspend fun work(command: TimelineWorkCommand) = when (command) {
            is TimelineWorkCommand.ObserveCurrentTime -> observeCurrentTimeWork()
            is TimelineWorkCommand.UpdateTimeTask -> updateTimeTaskWork(
                timeTaskId = command.timeTaskId,
                timeRange = command.timeRange,
            )
        }

        private fun observeCurrentTimeWork() = flow {
            dateManager.fetchMinuteTicker()
                .onStart { emit(dateManager.fetchCurrentDate()) }
                .collect { currentTime -> emit(ActionResult(HomeAction.UpdateCurrentTime(currentTime))) }
        }

        private fun updateTimeTaskWork(
            timeTaskId: Long,
            timeRange: TimeRange,
        ) = flow {
            val result = withContext(NonCancellable) {
                mutationMutex.withLock {
                    timelineInteractor.updateTimeTask(timeTaskId, timeRange).also { result ->
                        if (result is Either.Right) {
                            updateNotifications(
                                previousTimeTask = result.data.previousTimeTask,
                                updatedTimeTask = result.data.updatedTimeTask,
                            )
                        }
                    }
                }
            }
            result.handle(
                onLeftAction = { emit(EffectResult(HomeEffect.ShowError(it))) },
            )
        }

        private fun updateNotifications(
            previousTimeTask: TimeTask,
            updatedTimeTask: TimeTask,
        ) {
            timeTaskAlarmManager.deleteNotifyAlarm(previousTimeTask)
            if (updatedTimeTask.isEnableNotification) {
                timeTaskAlarmManager.addOrUpdateNotifyAlarm(updatedTimeTask)
            }
        }
    }
}

internal sealed class TimelineWorkCommand : WorkCommand {
    data object ObserveCurrentTime : TimelineWorkCommand()
    data class UpdateTimeTask(val timeTaskId: Long, val timeRange: TimeRange) : TimelineWorkCommand()
}
