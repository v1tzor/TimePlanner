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
package ru.aleshin.core.domain.common

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.fetchAllTimeTasks
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.template.convertToTimeTask
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
interface RecurringScheduleManager {

    suspend fun createMissingSchedules(targetDates: List<Date>)

    class Base @Inject constructor(
        private val scheduleRepository: ScheduleRepository,
        private val timeTaskRepository: TimeTaskRepository,
        private val templatesRepository: TemplatesRepository,
        private val overlayManager: TimeOverlayManager,
        private val dateManager: DateManager,
    ) : RecurringScheduleManager {

        private val creationMutex = Mutex()

        override suspend fun createMissingSchedules(targetDates: List<Date>) = creationMutex.withLock {
            val templates = templatesRepository.fetchAllTemplates().first().filter { it.repeatEnabled }
            if (templates.isEmpty()) return@withLock

            val currentDate = dateManager.fetchBeginningCurrentDay()
            val plannedDates = targetDates
                .asSequence()
                .map { it.startThisDay() }
                .filter { it >= currentDate }
                .filter { date ->
                    templates.any { template ->
                        template.repeatTimes.any { repeatTime -> repeatTime.checkDateIsRepeat(date) }
                    }
                }
                .distinctBy { it.time }
                .sortedBy { it.time }
                .toList()
            if (plannedDates.isEmpty()) return@withLock

            val timeRange = TimeRange(
                from = plannedDates.first().shiftDay(-1),
                to = plannedDates.last().shiftDay(1),
            )
            val schedules = scheduleRepository.fetchSchedulesByRange(timeRange).first()
            val existingDates = schedules.map { it.date.startThisDay().time }.toSet()
            val missingDates = plannedDates.filter { it.time !in existingDates }
            if (missingDates.isEmpty()) return@withLock

            val timeRanges = schedules.fetchAllTimeTasks()
                .distinctBy { it.key }
                .map { it.timeRange }
                .toMutableList()
            val generatedTasks = mutableListOf<TimeTask>()

            missingDates.forEach { date ->
                templates
                    .filter { template ->
                        template.repeatTimes.any { repeatTime -> repeatTime.checkDateIsRepeat(date) }
                    }
                    .map { template -> template.convertToTimeTask(date = date, createdAt = date) }
                    .sortedBy { it.timeRange.from.time }
                    .forEach { timeTask ->
                        if (!overlayManager.isOverlay(timeTask.timeRange, timeRanges).isOverlay) {
                            generatedTasks.add(timeTask)
                            timeRanges.add(timeTask.timeRange)
                        }
                    }
            }

            scheduleRepository.addOrUpdateSchedules(
                schedules = missingDates.map { BaseDailySchedule(date = it) },
            )
            if (generatedTasks.isNotEmpty()) {
                timeTaskRepository.addOrUpdateTimeTasks(generatedTasks)
            }
        }
    }
}
