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

import kotlinx.coroutines.flow.first
import ru.aleshin.core.domain.common.TimeOverlayException
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.entities.schedules.ScheduleDetails
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.tasks.TimeTaskDetails
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.shiftHours
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.TimeShiftException
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.domain.entities.TimelineSchedule
import ru.aleshin.features.home.impl.domain.entities.TimelineTaskUpdate
import ru.aleshin.features.home.impl.domain.entities.TimelineTimeTask
import java.util.Date
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
internal interface TimelineInteractor {

    fun fetchTimelineSchedule(date: Date, schedule: ScheduleDetails?): TimelineSchedule

    suspend fun updateTimeTask(timeTaskId: Long, timeRange: TimeRange): DomainResult<HomeFailures, TimelineTaskUpdate>

    suspend fun undoTimeTaskUpdate(update: TimelineTaskUpdate): DomainResult<HomeFailures, TimelineTaskUpdate>

    class Base @Inject constructor(
        private val scheduleRepository: ScheduleRepository,
        private val timeTaskRepository: TimeTaskRepository,
        private val overlayManager: TimeOverlayManager,
        private val dateManager: DateManager,
        private val eitherWrapper: HomeEitherWrapper,
    ) : TimelineInteractor {

        override fun fetchTimelineSchedule(
            date: Date,
            schedule: ScheduleDetails?,
        ): TimelineSchedule {
            val dayStart = date.startThisDay()
            val dayEnd = dayStart.shiftDay(1)
            val dayTimeRange = TimeRange(dayStart, dayEnd)
            val timeTasks = schedule?.timeTasks.orEmpty()
                .filter { timeTask -> timeTask.startTime < dayEnd && timeTask.endTime > dayStart }
                .sortedBy { timeTask -> timeTask.startTime }
            val visibleTimeRanges = timeTasks.mapNotNull { timeTask ->
                timeTask.timeRange.intersect(dayTimeRange)
            }

            return TimelineSchedule(
                date = date,
                dayTimeRange = dayTimeRange,
                initialTime = fetchInitialTime(date, dayTimeRange, timeTasks),
                timeStep = TIME_STEP,
                minimumTaskDuration = MINIMUM_TASK_DURATION,
                timeTasks = timeTasks.mapIndexed { index, timeTask ->
                    timeTask.fetchTimelineTimeTask(
                        visibleTimeRange = checkNotNull(visibleTimeRanges.getOrNull(index)),
                        previousTimeRange = visibleTimeRanges.getOrNull(index - 1),
                        nextTimeRange = visibleTimeRanges.getOrNull(index + 1),
                        dayTimeRange = dayTimeRange,
                    )
                },
                freeTimeRanges = visibleTimeRanges.fetchFreeTimeRanges(dayTimeRange),
            )
        }

        override suspend fun updateTimeTask(
            timeTaskId: Long,
            timeRange: TimeRange,
        ) = eitherWrapper.wrap {
            val previousTimeTask = checkNotNull(timeTaskRepository.fetchTimeTaskById(timeTaskId))
            validateTimeRange(previousTimeTask, timeRange)

            val updatedTimeTask = previousTimeTask.copy(
                timeRange = timeRange,
                linkedTemplateId = null,
            )
            timeTaskRepository.addOrUpdateTimeTask(updatedTimeTask)

            TimelineTaskUpdate(
                previousTimeTask = previousTimeTask,
                updatedTimeTask = updatedTimeTask,
            )
        }

        override suspend fun undoTimeTaskUpdate(
            update: TimelineTaskUpdate,
        ) = eitherWrapper.wrap {
            val currentTimeTask = checkNotNull(
                timeTaskRepository.fetchTimeTaskById(update.updatedTimeTask.key),
            )
            validateTimeRange(currentTimeTask, update.previousTimeTask.timeRange)
            timeTaskRepository.addOrUpdateTimeTask(update.previousTimeTask)

            TimelineTaskUpdate(
                previousTimeTask = currentTimeTask,
                updatedTimeTask = update.previousTimeTask,
            )
        }

        private fun fetchInitialTime(
            date: Date,
            dayTimeRange: TimeRange,
            timeTasks: List<TimeTaskDetails>,
        ): Date {
            val currentTime = dateManager.fetchCurrentDate()
            val initialTime = when {
                date.isCurrentDay(currentTime) -> currentTime
                timeTasks.isNotEmpty() -> timeTasks.first().startTime.shiftHours(-1)
                else -> dayTimeRange.from.shiftHours(DEFAULT_INITIAL_HOUR)
            }
            return Date(initialTime.time.coerceIn(dayTimeRange.from.time, dayTimeRange.to.time))
        }

        private fun TimeTaskDetails.fetchTimelineTimeTask(
            visibleTimeRange: TimeRange,
            previousTimeRange: TimeRange?,
            nextTimeRange: TimeRange?,
            dayTimeRange: TimeRange,
        ): TimelineTimeTask {
            return TimelineTimeTask(
                timeTask = TimeTask(
                    key = key,
                    date = date,
                    createdAt = createdAt,
                    timeRange = timeRange,
                    category = mainCategory,
                    subCategory = subCategory,
                    linkedTemplateId = linkedTemplateId,
                    isCompleted = isCompleted,
                    priority = priority,
                    isEnableNotification = isEnableNotification,
                    taskNotifications = taskNotifications,
                    isConsiderInStatistics = isConsiderInStatistics,
                    note = note,
                ),
                executionStatus = executionStatus,
                visibleTimeRange = visibleTimeRange,
                minimumStartTime = previousTimeRange?.to ?: dayTimeRange.from,
                maximumEndTime = nextTimeRange?.from ?: dayTimeRange.to,
                canMove = startTime >= dayTimeRange.from && endTime <= dayTimeRange.to,
                canResizeStart = startTime >= dayTimeRange.from,
                canResizeEnd = endTime <= dayTimeRange.to,
            )
        }

        private suspend fun validateTimeRange(
            timeTask: TimeTask,
            timeRange: TimeRange,
        ) {
            if (timeRange.to.time - timeRange.from.time < MINIMUM_TASK_DURATION) {
                throw TimeShiftException()
            }
            val scheduleRange = TimeRange(timeTask.date.shiftDay(-1), timeTask.date.shiftDay(2))
            val timeRanges = scheduleRepository.fetchSchedulesByRange(scheduleRange).first()
                .asSequence()
                .flatMap { schedule -> schedule.allTimeTasks.asSequence() }
                .distinctBy { otherTimeTask -> otherTimeTask.key }
                .filter { otherTimeTask -> otherTimeTask.key != timeTask.key }
                .map { otherTimeTask -> otherTimeTask.timeRange }
                .toList()
            val overlayResult = overlayManager.isOverlay(timeRange, timeRanges)

            if (overlayResult.isOverlay) {
                throw TimeOverlayException(
                    startOverlay = overlayResult.leftTimeBorder,
                    endOverlay = overlayResult.rightTimeBorder,
                )
            }
        }

        private fun List<TimeRange>.fetchFreeTimeRanges(
            dayTimeRange: TimeRange,
        ): List<TimeRange> {
            val freeTimeRanges = mutableListOf<TimeRange>()
            var currentTime = dayTimeRange.from.time

            sortedBy { timeRange -> timeRange.from }.forEach { timeRange ->
                val startTime = max(timeRange.from.time, dayTimeRange.from.time)
                val endTime = min(timeRange.to.time, dayTimeRange.to.time)
                if (startTime > currentTime) {
                    freeTimeRanges.add(TimeRange(Date(currentTime), Date(startTime)))
                }
                currentTime = max(currentTime, endTime)
            }
            if (currentTime < dayTimeRange.to.time) {
                freeTimeRanges.add(TimeRange(Date(currentTime), dayTimeRange.to))
            }
            return freeTimeRanges
        }

        private fun TimeRange.intersect(other: TimeRange): TimeRange? {
            val startTime = max(from.time, other.from.time)
            val endTime = min(to.time, other.to.time)
            return if (startTime < endTime) TimeRange(Date(startTime), Date(endTime)) else null
        }
    }
}

private const val DEFAULT_INITIAL_HOUR = 8
private const val TIME_STEP_MINUTES = 5L
private const val TIME_STEP = TIME_STEP_MINUTES * Constants.Date.MILLIS_IN_MINUTE
private const val MINIMUM_TASK_DURATION = TIME_STEP
