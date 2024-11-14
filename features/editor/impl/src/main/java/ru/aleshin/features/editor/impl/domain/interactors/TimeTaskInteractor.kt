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
package ru.aleshin.features.editor.impl.domain.interactors

import kotlinx.coroutines.flow.first
import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.extractAllItem
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.managers.TimeOverlayException
import ru.aleshin.core.utils.managers.TimeOverlayManager
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
internal interface TimeTaskInteractor {

    suspend fun addTimeTask(timeTask: TimeTask): DomainResult<EditorFailures, Long>
    suspend fun updateTimeTask(timeTask: TimeTask): DomainResult<EditorFailures, Long>
    suspend fun deleteTimeTask(key: Long): DomainResult<EditorFailures, Unit>

    class Base @Inject constructor(
        private val scheduleRepository: ScheduleRepository,
        private val timeTaskRepository: TimeTaskRepository,
        private val statusChecker: ScheduleStatusChecker,
        private val overlayManager: TimeOverlayManager,
        private val dateManager: DateManager,
        private val eitherWrapper: EditorEitherWrapper,
    ) : TimeTaskInteractor {

        override suspend fun addTimeTask(timeTask: TimeTask) = eitherWrapper.wrap {
            val timeRange = TimeRange(timeTask.date.shiftDay(-1), timeTask.date.shiftDay(1))
            val schedules = scheduleRepository.fetchSchedulesByRange(timeRange).first().let {
                it.ifEmpty {
                    val createdSchedule = Schedule(
                        date = timeTask.date.time,
                        status = statusChecker.fetchState(timeTask.date, dateManager.fetchBeginningCurrentDay()),
                    ).apply {
                        scheduleRepository.createSchedules(listOf(this))
                    }
                    listOf(createdSchedule)
                }
            }
            val allTimeTask = schedules.map { it.timeTasks }.extractAllItem()
            val key = generateUniqueKey()

            checkIsOverlay(allTimeTask.map { it.timeRange }, timeTask.timeRange) {
                timeTaskRepository.addTimeTasks(listOf(timeTask.copy(key = key)))
            }
            return@wrap key
        }

        override suspend fun updateTimeTask(timeTask: TimeTask) = eitherWrapper.wrap {
            val timeRange = TimeRange(timeTask.date.shiftDay(-1), timeTask.date.shiftDay(1))
            val schedules = scheduleRepository.fetchSchedulesByRange(timeRange).first()
            val allTimeTask = schedules.map { it.timeTasks }.extractAllItem().toMutableList().apply {
                removeAll { it.key == timeTask.key }
            }

            checkIsOverlay(allTimeTask.map { it.timeRange }, timeTask.timeRange) {
                timeTaskRepository.updateTimeTask(timeTask)
            }
            return@wrap timeTask.key
        }

        override suspend fun deleteTimeTask(key: Long) = eitherWrapper.wrap {
            timeTaskRepository.deleteTimeTasks(listOf(key))
        }

        private suspend fun checkIsOverlay(
            allRanges: List<TimeRange>,
            range: TimeRange,
            block: suspend () -> Unit,
        ) = overlayManager.isOverlay(range, allRanges).let { result ->
            if (result.isOverlay) {
                throw TimeOverlayException(result.leftTimeBorder, result.rightTimeBorder)
            } else {
                block()
            }
        }
    }
}
