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
package ru.aleshin.features.templates.impl.domain.interactors

import kotlinx.coroutines.flow.first
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.schedules.fetchAllTimeTasks
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.entities.template.convertToTimeTask
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Constants.Date.NEXT_REPEAT_LIMIT_DAYS
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.toRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.templates.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.templates.impl.domain.entities.TemplatesFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
internal interface RepeatTaskInteractor {

    suspend fun addRepeatsTemplate(template: Template, repeatTimes: List<RepeatTime>): DomainResult<TemplatesFailures, List<TimeTask>>
    suspend fun updateRepeatTemplate(oldTemplate: Template, template: Template): DomainResult<TemplatesFailures, List<TimeTask>>
    suspend fun deleteRepeatsTemplates(template: Template, repeatTimes: List<RepeatTime>): DomainResult<TemplatesFailures, List<TimeTask>>

    class Base @Inject constructor(
        private val timeTaskRepository: TimeTaskRepository,
        private val scheduleRepository: ScheduleRepository,
        private val eitherWrapper: HomeEitherWrapper,
        private val overlayManager: TimeOverlayManager,
        private val dateManager: DateManager,
    ) : RepeatTaskInteractor {

        override suspend fun addRepeatsTemplate(
            template: Template,
            repeatTimes: List<RepeatTime>,
        ) = eitherWrapper.wrap {
            if (!template.repeatEnabled || repeatTimes.isEmpty()) return@wrap emptyList()

            val repeatScheduleScope = fetchOrCreateRepeatSchedules(repeatTimes)
            val repeatTimeTasks = createRepeatTasksByTemplate(
                schedules = repeatScheduleScope.schedules,
                template = template,
                repeatTimes = repeatTimes,
                repeatDates = repeatScheduleScope.repeatDates,
            )

            if (repeatTimeTasks.isNotEmpty()) {
                timeTaskRepository.addOrUpdateTimeTasks(repeatTimeTasks)
            }

            return@wrap repeatTimeTasks
        }

        override suspend fun updateRepeatTemplate(
            oldTemplate: Template,
            template: Template,
        ) = eitherWrapper.wrap {
            val schedules = fetchOverlaySchedules()
            val deletableTasks = findRepeatTasksByTemplate(
                schedules = schedules,
                template = oldTemplate,
                repeatTimes = oldTemplate.repeatTimes,
            )
            val repeatScheduleScope = fetchOrCreateRepeatSchedules(template.repeatTimes)
            val updatedTasks = when {
                template.repeatEnabled -> createRepeatTasksByTemplate(
                    schedules = repeatScheduleScope.schedules,
                    template = template,
                    repeatTimes = template.repeatTimes,
                    repeatDates = repeatScheduleScope.repeatDates,
                    replaceableTasks = deletableTasks,
                )
                else -> emptyList()
            }

            if (deletableTasks.isNotEmpty()) {
                timeTaskRepository.deleteTimeTasksByIds(deletableTasks.map { timeTask -> timeTask.key })
            }
            if (updatedTasks.isNotEmpty()) {
                timeTaskRepository.addOrUpdateTimeTasks(updatedTasks)
            }

            return@wrap deletableTasks + updatedTasks
        }

        override suspend fun deleteRepeatsTemplates(
            template: Template,
            repeatTimes: List<RepeatTime>,
        ) = eitherWrapper.wrap {
            val schedules = fetchOverlaySchedules()
            val repeatTimeTasks = findRepeatTasksByTemplate(
                schedules = schedules,
                template = template,
                repeatTimes = repeatTimes,
            )

            if (repeatTimeTasks.isNotEmpty()) {
                timeTaskRepository.deleteTimeTasksByIds(repeatTimeTasks.map { timeTask -> timeTask.key })
            }

            return@wrap repeatTimeTasks
        }

        private suspend fun fetchOrCreateRepeatSchedules(repeatTimes: List<RepeatTime>): RepeatScheduleScope {
            if (repeatTimes.isEmpty()) return RepeatScheduleScope(emptyList(), emptySet())

            val currentDate = dateManager.fetchBeginningCurrentDay()
            val repeatDates = (0..NEXT_REPEAT_LIMIT_DAYS.toInt())
                .map { shift -> currentDate.shiftDay(shift).startThisDay() }
                .filter { date -> repeatTimes.any { repeatTime -> repeatTime.checkDateIsRepeat(date) } }
            val repeatDateKeys = repeatDates.map { date -> date.time }.toSet()

            val schedules = fetchOverlaySchedules()
            val schedulesByDate = schedules.associateBy { schedule -> schedule.date.startThisDay().time }
            val missingSchedules = repeatDates
                .filter { date -> schedulesByDate[date.time] == null }
                .map { date -> BaseDailySchedule(date) }

            if (missingSchedules.isNotEmpty()) {
                scheduleRepository.addOrUpdateSchedules(missingSchedules)
            }

            val actualSchedules = when (missingSchedules.isNotEmpty()) {
                true -> fetchOverlaySchedules()
                false -> schedules
            }
            return RepeatScheduleScope(actualSchedules, repeatDateKeys)
        }

        private suspend fun fetchOverlaySchedules(): List<Schedule> {
            val currentDate = dateManager.fetchBeginningCurrentDay()
            val startDate = currentDate
            val endDate = currentDate.shiftDay(NEXT_REPEAT_LIMIT_DAYS.toInt() + 1)
            return scheduleRepository.fetchSchedulesByRange(startDate toRange endDate).first()
        }

        private fun findRepeatTasksByTemplate(
            schedules: List<Schedule>,
            template: Template,
            repeatTimes: List<RepeatTime>,
        ): List<TimeTask> {
            val currentTime = dateManager.fetchCurrentDate()
            val existingTasks = schedules.fetchAllTimeTasks().distinctBy { timeTask -> timeTask.key }

            return existingTasks.filter { timeTask ->
                timeTask.timeRange.from > currentTime &&
                timeTask.linkedTemplateId == template.templateId &&
                (repeatTimes.isEmpty() || repeatTimes.any { repeatTime -> repeatTime.checkDateIsRepeat(timeTask.date) })
            }
        }

        private fun createRepeatTasksByTemplate(
            schedules: List<Schedule>,
            template: Template,
            repeatTimes: List<RepeatTime>,
            repeatDates: Set<Long>,
            replaceableTasks: List<TimeTask> = emptyList(),
        ): List<TimeTask> {
            val currentTime = dateManager.fetchCurrentDate()

            val replaceableKeys = replaceableTasks.map { timeTask -> timeTask.key }.toSet()
            val replaceableTasksByDate = replaceableTasks.associateBy { timeTask -> timeTask.date.startThisDay().time }

            val existingTasks = schedules.fetchAllTimeTasks().distinctBy { timeTask -> timeTask.key }

            val createdTasks = mutableListOf<TimeTask>()

            schedules.forEach { schedule ->
                val scheduleDate = schedule.date.startThisDay()
                if (scheduleDate.time !in repeatDates) return@forEach
                if (repeatTimes.none { repeatTime -> repeatTime.checkDateIsRepeat(scheduleDate) }) return@forEach

                val oldTask = replaceableTasksByDate[scheduleDate.time]
                val repeatTimeTask = template.convertToTimeTask(
                    date = scheduleDate,
                    key = oldTask?.key ?: generateUniqueKey(),
                    createdAt = scheduleDate,
                )
                if (repeatTimeTask.timeRange.from < currentTime) return@forEach
                val overlayRanges = existingTasks
                    .filter { timeTask -> timeTask.key !in replaceableKeys }
                    .map { timeTask -> timeTask.timeRange } + createdTasks.map { timeTask -> timeTask.timeRange }

                if (!overlayManager.isOverlay(repeatTimeTask.timeRange, overlayRanges).isOverlay) {
                    createdTasks.add(repeatTimeTask)
                }
            }

            return createdTasks
        }

        private data class RepeatScheduleScope(
            val schedules: List<Schedule>,
            val repeatDates: Set<Long>,
        )
    }
}

