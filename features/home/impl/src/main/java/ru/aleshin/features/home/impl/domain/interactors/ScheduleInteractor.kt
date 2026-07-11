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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformWhile
import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.common.TimeTaskStatusChecker
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.OverviewSchedule
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.schedules.ScheduleDetails
import ru.aleshin.core.domain.entities.schedules.convertToDetails
import ru.aleshin.core.domain.entities.schedules.convertToOverview
import ru.aleshin.core.domain.entities.schedules.fetchAllTimeTasks
import ru.aleshin.core.domain.entities.schedules.isCompleted
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.tasks.TimeTaskDetails
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.domain.entities.tasks.mapToDetails
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.entities.template.convertToTimeTask
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.daysToMillis
import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
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

    suspend fun createSchedule(requiredDay: Date): DomainResult<HomeFailures, Long>
    suspend fun addOrUpdateTimeTask(timeTask: TimeTask): DomainResult<HomeFailures, Long>
    suspend fun fetchScheduleDetailsByDate(date: Long): FlowDomainResult<HomeFailures, ScheduleDetails?>
    suspend fun fetchOverviewSchedules(timeRange: TimeRange): FlowDomainResult<HomeFailures, List<OverviewSchedule>>
    suspend fun fetchActiveTimeTaskDetails(): FlowDomainResult<HomeFailures, TimeTaskDetails?>

    class Base @Inject constructor(
        private val scheduleRepository: ScheduleRepository,
        private val timeTaskRepository: TimeTaskRepository,
        private val templatesRepository: TemplatesRepository,
        private val scheduleStatusChecker: ScheduleStatusChecker,
        private val timeTaskStatusChecker: TimeTaskStatusChecker,
        private val dateManager: DateManager,
        private val overlayManager: TimeOverlayManager,
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

                createMissingRecurringSchedules(
                    targetDates = listOf(requiredDate),
                    templates = fetchRepeatTemplates(),
                )
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

        @OptIn(ExperimentalCoroutinesApi::class)
        override suspend fun fetchOverviewSchedules(timeRange: TimeRange) = eitherWrapper.wrapFlow {
            val overviewDates = timeRange.periodDates()

            scheduleRepository.fetchSchedulesByRange(timeRange).onStart {
                createMissingRecurringSchedules(overviewDates, fetchRepeatTemplates())
            }.flatMapLatest { schedules ->
                dateManager.fetchTicker().map {
                    val schedulesByDate = schedules.associateBy { schedule -> schedule.date.time }

                    overviewDates.map { date ->
                        val schedule = schedulesByDate[date.time] ?: Schedule(date = date)
                        val timeTasks = schedule.allTimeTasks
                        val taskStatuses = timeTasks.map { timeTask ->
                            timeTask to timeTaskStatusChecker.fetchStatus(timeTask.timeRange)
                        }

                        schedule.convertToOverview(
                            dateStatus = scheduleStatusChecker.fetchStatus(date),
                            unexecutedTask = timeTasks.count { timeTask -> !timeTask.isCompleted },
                            completedTask = taskStatuses.count { (timeTask, status) ->
                                status == TimeTaskStatus.COMPLETED && timeTask.isCompleted
                            },
                            plannedTask = taskStatuses.count { (_, status) ->
                                status != TimeTaskStatus.COMPLETED
                            },
                            progress = scheduleStatusChecker.fetchProgress(timeTasks),
                        )
                    }
                }.distinctUntilChanged().transformWhile { schedule ->
                    emit(schedule)
                    !schedule.isCompleted()
                }
            }
        }

        override suspend fun fetchActiveTimeTaskDetails() = eitherWrapper.wrapFlow {
            val currentDate = dateManager.fetchCurrentDate()
            val timeRange = TimeRange(
                from = currentDate.startThisDay().shiftDay(-1),
                to = currentDate.startThisDay().shiftDay(1),
            )

            combine(
                scheduleRepository.fetchSchedulesByRange(timeRange),
                dateManager.fetchTicker(),
            ) { schedules, ticker ->
                val timeTask = schedules
                    .asSequence()
                    .flatMap { schedule -> schedule.allTimeTasks.asSequence() }
                    .distinctBy { timeTask -> timeTask.key }
                    .filter { timeTask -> timeTask.isRunning(ticker) }
                    .minByOrNull { timeTask -> timeTask.timeRange.to.time }

                timeTask?.mapToDetails(
                    executionStatus = TimeTaskStatus.RUNNING,
                    progress = dateManager.calculateProgress(timeTask.timeRange.from, timeTask.timeRange.to),
                    leftTime = dateManager.calculateLeftTime(timeTask.timeRange.to),
                )
            }.distinctUntilChanged()
        }

        private suspend fun fetchRepeatTemplates(): List<Template> {
            return templatesRepository.fetchAllTemplates().first().filter { template -> template.repeatEnabled }
        }

        private suspend fun createMissingRecurringSchedules(
            targetDates: List<Date>,
            templates: List<Template>,
        ) {
            if (templates.isEmpty()) return

            val currentDate = dateManager.fetchBeginningCurrentDay()
            val plannedDates = targetDates
                .asSequence()
                .map { date -> date.startThisDay() }
                .filter { date -> date >= currentDate }
                .filter { date ->
                    templates.any { template ->
                        template.repeatTimes.any { repeatTime -> repeatTime.checkDateIsRepeat(date) }
                    }
                }
                .distinctBy { date -> date.time }
                .sortedBy { date -> date.time }
                .toList()
            if (plannedDates.isEmpty()) return

            val plannedTimeRange = TimeRange(
                from = plannedDates.first().shiftDay(-1),
                to = plannedDates.last().shiftDay(1),
            )
            val schedules = scheduleRepository.fetchSchedulesByRange(plannedTimeRange).first()
            val existingScheduleDates = schedules.map { schedule -> schedule.date.startThisDay().time }.toSet()
            val missingDates = plannedDates.filter { date -> date.time !in existingScheduleDates }
            if (missingDates.isEmpty()) return

            val timeRanges = schedules.fetchAllTimeTasks()
                .distinctBy { timeTask -> timeTask.key }
                .map { timeTask -> timeTask.timeRange }.toMutableList()
            val generatedTasks = mutableListOf<TimeTask>()

            missingDates.forEach { date ->
                templates
                    .filter { template ->
                        template.repeatTimes.any { repeatTime -> repeatTime.checkDateIsRepeat(date) }
                    }
                    .map { template ->
                        template.convertToTimeTask(date = date, createdAt = date)
                    }
                    .sortedBy { timeTask -> timeTask.timeRange.from }
                    .forEach { timeTask ->
                        if (!overlayManager.isOverlay(timeTask.timeRange, timeRanges).isOverlay) {
                            generatedTasks.add(timeTask)
                            timeRanges.add(timeTask.timeRange)
                        }
                    }
            }

            scheduleRepository.addOrUpdateSchedules(
                schedules = missingDates.map { date -> BaseDailySchedule(date = date) },
            )
            if (generatedTasks.isNotEmpty()) {
                timeTaskRepository.addOrUpdateTimeTasks(generatedTasks)
            }
        }
    }
}
