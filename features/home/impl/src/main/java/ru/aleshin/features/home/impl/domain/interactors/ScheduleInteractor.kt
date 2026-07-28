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
package ru.aleshin.features.home.impl.domain.interactors

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformWhile
import ru.aleshin.core.domain.common.RecurringScheduleManager
import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.common.TimeTaskStatusChecker
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.ScheduleDetails
import ru.aleshin.core.domain.entities.schedules.convertToDetails
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.domain.entities.tasks.mapToDetails
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.daysToMillis
import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.functional.Constants.Date.NEXT_REPEAT_LIMIT_DAYS
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal interface ScheduleInteractor {

    suspend fun addOrUpdateTimeTask(timeTask: TimeTask): DomainResult<HomeFailures, Long>
    suspend fun createSchedule(requiredDay: Date): DomainResult<HomeFailures, Long>
    suspend fun fetchScheduleDetailsByDate(date: Long): FlowDomainResult<HomeFailures, ScheduleDetails?>

    class Base @Inject constructor(
        private val scheduleRepository: ScheduleRepository,
        private val timeTaskRepository: TimeTaskRepository,
        private val recurringScheduleManager: RecurringScheduleManager,
        private val scheduleStatusChecker: ScheduleStatusChecker,
        private val timeTaskStatusChecker: TimeTaskStatusChecker,
        private val dateManager: DateManager,
        private val eitherWrapper: HomeEitherWrapper,
    ) : ScheduleInteractor {

        override suspend fun createSchedule(requiredDay: Date) = eitherWrapper.wrap {
            scheduleRepository.addOrUpdateSchedule(BaseDailySchedule(date = requiredDay))
        }

        override suspend fun addOrUpdateTimeTask(timeTask: TimeTask) = eitherWrapper.wrap {
            timeTaskRepository.addOrUpdateTimeTask(timeTask)
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        override suspend fun fetchScheduleDetailsByDate(date: Long) = eitherWrapper.wrapFlow<ScheduleDetails?> {
            val requiredDate = date.mapToDate()
            val currentDate = dateManager.fetchBeginningCurrentDay()
            val repeatLimit = NEXT_REPEAT_LIMIT_DAYS.daysToMillis()
            val shouldCreateRepeat = date >= currentDate.time && date - currentDate.time <= repeatLimit

            scheduleRepository.fetchScheduleByDate(requiredDate).onStart {
                if (!shouldCreateRepeat) return@onStart

                recurringScheduleManager.createMissingSchedules(listOf(requiredDate))
            }.flatMapLatest { schedule ->
                if (schedule == null) return@flatMapLatest flowOf(null)

                dateManager.fetchTicker().map {
                    schedule.convertToDetails(
                        dateStatus = scheduleStatusChecker.fetchStatus(schedule.date),
                        progress = scheduleStatusChecker.fetchProgress(schedule.allTimeTasks),
                        timeTaskMapper = { timeTask ->
                            when (val status = timeTaskStatusChecker.fetchStatus(timeTask.timeRange)) {
                                TimeTaskStatus.COMPLETED -> timeTask.mapToDetails(
                                    executionStatus = status,
                                    progress = 1f,
                                    leftTime = 0,
                                )
                                TimeTaskStatus.PLANNED -> timeTask.mapToDetails(
                                    executionStatus = status,
                                    progress = 0f,
                                    leftTime = -1,
                                )
                                TimeTaskStatus.RUNNING -> timeTask.mapToDetails(
                                    executionStatus = status,
                                    progress = dateManager.calculateProgress(timeTask.timeRange.from, timeTask.timeRange.to),
                                    leftTime = dateManager.calculateLeftTime(timeTask.timeRange.to),
                                )
                            }
                        },
                    )
                }.distinctUntilChanged().transformWhile { schedule ->
                    emit(schedule)
                    !schedule.isCompleted()
                }
            }
        }

    }
}
