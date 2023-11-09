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
package ru.aleshin.features.home.impl.domain.interactors

import kotlinx.coroutines.flow.first
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.managers.TimeOverlayManager
import ru.aleshin.features.home.api.domain.entities.schedules.Schedule
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domain.entities.schedules.fetchAllTimeTasks
import ru.aleshin.features.home.api.domain.entities.template.RepeatTime
import ru.aleshin.features.home.api.domain.entities.template.Template
import ru.aleshin.features.home.api.domain.repository.ScheduleRepository
import ru.aleshin.features.home.api.domain.repository.TimeTaskRepository
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.common.convertToTimeTask
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
internal interface RepeatTaskInteractor {

    suspend fun updateRepeatTemplate(
        oldTemplate: Template,
        template: Template,
    ): Either<HomeFailures, List<TimeTask>>
    suspend fun addRepeatsTemplate(
        template: Template, 
        repeatTimes: List<RepeatTime>,
    ): Either<HomeFailures, List<TimeTask>>
    suspend fun deleteRepeatsTemplates(
        template: Template, 
        repeatTimes: List<RepeatTime>,
    ): Either<HomeFailures, List<TimeTask>>

    class Base @Inject constructor(
        private val timeTaskRepository: TimeTaskRepository,
        private val scheduleRepository: ScheduleRepository,
        private val eitherWrapper: HomeEitherWrapper,
        private val overlayManager: TimeOverlayManager,
        private val dateManager: DateManager,
    ) : RepeatTaskInteractor {

        override suspend fun updateRepeatTemplate(
            oldTemplate: Template,
            template: Template,
        ) = eitherWrapper.wrap {
            val schedules = filteredSchedules()
            val updatedTasks = mutableListOf<TimeTask>()
            val deletableTasksId = mutableListOf<Long>()
            template.repeatTimes.forEach { repeatTime ->
                deletableTasksId.addAll(findRepeatTasksByTemplate(schedules, oldTemplate, repeatTime).map { it.key })
                updatedTasks.addAll(createRepeatTasksByTemplate(schedules, template, repeatTime, deletableTasksId))
            }
            return@wrap updatedTasks.apply {
                timeTaskRepository.deleteTimeTasks(deletableTasksId)
                timeTaskRepository.addTimeTasks(this)
            }
        }

        override suspend fun addRepeatsTemplate(
            template: Template, 
            repeatTimes: List<RepeatTime>,
        ) = eitherWrapper.wrap {
            val repeatTimeTasks = mutableListOf<TimeTask>()
            repeatTimes.forEach { repeatTime ->
                val timeTasks = createRepeatTasksByTemplate(filteredSchedules(), template, repeatTime).apply {
                    timeTaskRepository.addTimeTasks(this)
                }
                repeatTimeTasks.addAll(timeTasks)
            }
            return@wrap repeatTimeTasks
        }

        override suspend fun deleteRepeatsTemplates(
            template: Template, 
            repeatTimes: List<RepeatTime>,
        ) = eitherWrapper.wrap {
            val repeatTimeTasks = mutableListOf<TimeTask>()
            repeatTimes.forEach { repeatTime ->
                val timeTasks = findRepeatTasksByTemplate(filteredSchedules(), template, repeatTime).apply {
                    timeTaskRepository.deleteTimeTasks(map { timeTask -> timeTask.key })
                }
                repeatTimeTasks.addAll(timeTasks)
            }
            return@wrap repeatTimeTasks
        }
        
        private suspend fun filteredSchedules(): List<Schedule> {
            val currentDate = dateManager.fetchBeginningCurrentDay()
            return scheduleRepository.fetchSchedulesByRange(null).first().filter { schedule ->
                schedule.date > currentDate.time 
            }
        }

        private fun findRepeatTasksByTemplate(
            schedules: List<Schedule>,
            template: Template,
            repeatTime: RepeatTime,
        ) = mutableListOf<TimeTask>().apply {
            schedules.fetchAllTimeTasks().filter { timeTask ->
                repeatTime.checkDateIsRepeat(timeTask.date) 
            }.forEach { timeTask ->
                if (template.equalsIsTemplate(timeTask)) add(timeTask)
            }
        }

        private fun createRepeatTasksByTemplate(
            schedules: List<Schedule>,
            template: Template,
            repeatTime: RepeatTime,
            keyList: List<Long> = emptyList(),
        ) = mutableListOf<TimeTask>().apply {
            schedules.forEach { schedule ->
                val scheduleTimeTasks = schedule.timeTasks
                val scheduleDate = schedule.date.mapToDate().startThisDay()

                if (repeatTime.checkDateIsRepeat(scheduleDate)) {
                    val existedTimeTask = schedule.timeTasks.find { keyList.contains(it.key) }
                    val timeTaskKey = when (existedTimeTask != null) {
                        true -> existedTimeTask.key
                        false -> generateUniqueKey()
                    }
                    val repeatTimeTask = template.convertToTimeTask(scheduleDate, timeTaskKey, scheduleDate)
                    val scheduleTimeRanges = scheduleTimeTasks.filter { !keyList.contains(it.key) }.map { it.timeRange }
                    overlayManager.isOverlay(repeatTimeTask.timeRange, scheduleTimeRanges).let { overlayResult ->
                        if (!overlayResult.isOverlay) add(repeatTimeTask)
                    }
                }
            }
        }
    }
}
