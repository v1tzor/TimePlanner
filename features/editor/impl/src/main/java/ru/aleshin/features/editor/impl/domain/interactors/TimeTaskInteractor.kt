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
package ru.aleshin.features.editor.impl.domain.interactors

import kotlinx.coroutines.flow.first
import ru.aleshin.core.domain.common.TimeOverlayException
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.convertToDetails
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.extractAllItem
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
internal interface TimeTaskInteractor {

    suspend fun addTimeTask(timeTask: TimeTask): DomainResult<EditorFailures, Long>
    suspend fun fetchTimeTaskById(timeTaskId: Long): DomainResult<EditorFailures, TimeTask?>
    suspend fun updateTimeTask(timeTask: TimeTask): DomainResult<EditorFailures, Long>
    suspend fun deleteTimeTaskById(key: Long): DomainResult<EditorFailures, Unit>

    class Base @Inject constructor(
        private val scheduleRepository: ScheduleRepository,
        private val timeTaskRepository: TimeTaskRepository,
        private val overlayManager: TimeOverlayManager,
        private val eitherWrapper: EditorEitherWrapper,
    ) : TimeTaskInteractor {

        override suspend fun addTimeTask(timeTask: TimeTask) = eitherWrapper.wrap {
            val timeRange = TimeRange(timeTask.date.shiftDay(-1), timeTask.date.shiftDay(1))
            val fetchedSchedules = scheduleRepository.fetchSchedulesByRange(timeRange).first()
            val schedules = fetchedSchedules.let { schedules ->
                if (schedules.none { schedule -> schedule.date == timeTask.date }) {
                    val createdSchedule = BaseDailySchedule(date = timeTask.date)
                    scheduleRepository.addOrUpdateSchedule(createdSchedule)
                    schedules + createdSchedule.convertToDetails()
                } else {
                    schedules
                }
            }
            val allTimeTask = schedules.map { it.timeTasks }.extractAllItem()
            val key = generateUniqueKey()

            checkIsOverlay(allTimeTask.map { it.timeRange }, timeTask.timeRange) {
                timeTaskRepository.addOrUpdateTimeTasks(listOf(timeTask.copy(key = key, linkedTemplateId = null)))
            }
            return@wrap key
        }

        override suspend fun fetchTimeTaskById(timeTaskId: Long) = eitherWrapper.wrap {
            timeTaskRepository.fetchTimeTaskById(timeTaskId)
        }

        override suspend fun updateTimeTask(timeTask: TimeTask) = eitherWrapper.wrap {
            val timeRange = TimeRange(timeTask.date.shiftDay(-1), timeTask.date.shiftDay(1))
            val schedules = scheduleRepository.fetchSchedulesByRange(timeRange).first()
            val allTimeTask = schedules.map { it.timeTasks }.extractAllItem().toMutableList().apply {
                removeAll { it.key == timeTask.key }
            }

            checkIsOverlay(allTimeTask.map { it.timeRange }, timeTask.timeRange) {
                timeTaskRepository.addOrUpdateTimeTask(timeTask)
            }
            return@wrap timeTask.key
        }

        override suspend fun deleteTimeTaskById(key: Long) = eitherWrapper.wrap {
            timeTaskRepository.deleteTimeTasksByIds(listOf(key))
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
