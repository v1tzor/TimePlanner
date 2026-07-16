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
package ru.aleshin.features.overview.impl.domain.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.common.TimeTaskStatusChecker
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.schedules.fetchAllTimeTasks
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.tasks.TimeTaskDetails
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.domain.entities.tasks.mapToDetails
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.entities.template.convertToTimeTask
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper
import ru.aleshin.features.overview.impl.domain.entities.DaySummary
import ru.aleshin.features.overview.impl.domain.entities.OverviewFailures
import ru.aleshin.features.overview.impl.domain.entities.WeekOverview
import ru.aleshin.features.overview.impl.domain.entities.WeekSchedule
import java.util.Date
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal interface ScheduleInteractor {

    suspend fun fetchWeekOverview(): FlowDomainResult<OverviewFailures, WeekOverview>
    suspend fun fetchActiveTimeTaskDetails(): FlowDomainResult<OverviewFailures, TimeTaskDetails?>

    class Base @Inject constructor(
        private val scheduleRepository: ScheduleRepository,
        private val timeTaskRepository: TimeTaskRepository,
        private val templatesRepository: TemplatesRepository,
        private val scheduleStatusChecker: ScheduleStatusChecker,
        private val timeTaskStatusChecker: TimeTaskStatusChecker,
        private val dateManager: DateManager,
        private val overlayManager: TimeOverlayManager,
        private val eitherWrapper: OverviewEitherWrapper,
    ) : ScheduleInteractor {


        override suspend fun fetchWeekOverview() = eitherWrapper.wrapFlow {
            val currentDate = dateManager.fetchBeginningCurrentDay()
            val timeRange = TimeRange(
                from = currentDate,
                to = currentDate.shiftDay(WEEK_DAYS_COUNT - 1),
            )
            val overviewDates = timeRange.periodDates()

            combine(
                fetchSchedules(timeRange),
                dateManager.fetchTicker(),
            ) { schedules, _ ->
                val schedulesByDate = schedules.associateBy { schedule -> schedule.date.time }
                val weekSchedules = overviewDates.map { date ->
                    val schedule = schedulesByDate[date.time] ?: Schedule(date = date)
                    val timeTasks = schedule.allTimeTasks
                        .distinctBy { timeTask -> timeTask.key }
                        .sortedBy { timeTask -> timeTask.timeRange.from }

                    WeekSchedule(
                        date = date,
                        timeTasks = timeTasks,
                        summary = fetchDaySummary(date, timeTasks),
                    )
                }

                WeekOverview(
                    tasksCount = weekSchedules
                        .flatMap { schedule -> schedule.timeTasks }
                        .distinctBy { timeTask -> timeTask.key }
                        .size,
                    schedules = weekSchedules,
                )
            }.distinctUntilChanged()
        }

        private fun fetchDaySummary(
            date: Date,
            timeTasks: List<TimeTask>,
        ): DaySummary {
            val dayStart = date.startThisDay()
            val dayEnd = dayStart.shiftDay(1)
            val workload = timeTasks.fetchWorkload(dayStart, dayEnd)

            return DaySummary(
                freeTime = (dayEnd.time - dayStart.time - workload).coerceAtLeast(0L),
                workload = workload,
                progress = scheduleStatusChecker.fetchProgress(timeTasks),
            )
        }

        private fun List<TimeTask>.fetchWorkload(
            dayStart: Date,
            dayEnd: Date,
        ): Long {
            val intervals = mapNotNull { timeTask -> timeTask.fetchIntersection(dayStart, dayEnd) }
                .sortedBy { interval -> interval.first }
            var workload = 0L
            var currentEnd = dayStart.time

            intervals.forEach { interval ->
                workload += when {
                    interval.second <= currentEnd -> 0L
                    interval.first < currentEnd -> interval.second - currentEnd
                    else -> interval.second - interval.first
                }
                currentEnd = max(currentEnd, interval.second)
            }
            return workload
        }

        private fun TimeTask.fetchIntersection(from: Date, to: Date): Pair<Long, Long>? {
            val start = max(timeRange.from.time, from.time)
            val end = min(timeRange.to.time, to.time)
            return if (start < end) start to end else null
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

        private suspend fun fetchSchedules(timeRange: TimeRange): Flow<List<Schedule>> {
            return scheduleRepository.fetchSchedulesByRange(timeRange).onStart {
                createMissingRecurringSchedules(timeRange.periodDates(), fetchRepeatTemplates())
            }
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

private const val WEEK_DAYS_COUNT = 7
