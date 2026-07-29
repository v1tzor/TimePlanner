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

import ru.aleshin.core.domain.entities.goals.Goal
import ru.aleshin.core.domain.entities.goals.GoalDetails
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalHistory
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalProgress
import ru.aleshin.core.domain.entities.goals.GoalProgressStatus
import ru.aleshin.core.domain.entities.goals.GoalScopeType
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
interface GoalProgressManager {

    fun fetchRange(
        goal: Goal,
        currentDate: Date,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): TimeRange

    fun fetchTaskSourceRange(
        goals: List<Goal>,
        currentDate: Date,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): TimeRange?

    fun isDeadlinePassed(
        goal: Goal,
        currentDate: Date,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): Boolean

    fun calculate(
        goals: List<Goal>,
        tasks: List<TimeTask>,
        currentDate: Date,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): List<GoalProgress>

    fun calculateDetails(
        goal: Goal,
        tasks: List<TimeTask>,
        currentDate: Date,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): GoalDetails

    fun calculateHistory(
        goals: List<Goal>,
        tasks: List<TimeTask>,
        currentDate: Date,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): List<GoalHistory>

    class Base @Inject constructor() : GoalProgressManager {

        override fun fetchRange(
            goal: Goal,
            currentDate: Date,
            timeZone: TimeZone,
        ): TimeRange {
            val reviewEnd = fetchReviewEnd(goal, currentDate, timeZone)
            return TimeRange(
                from = goal.createdAt,
                to = reviewEnd.shiftCalendarDay(days = 1, timeZone = timeZone),
            )
        }

        override fun fetchTaskSourceRange(
            goals: List<Goal>,
            currentDate: Date,
            timeZone: TimeZone,
        ): TimeRange? {
            if (goals.isEmpty()) return null

            val rangeFrom = checkNotNull(goals.minByOrNull { goal -> goal.createdAt.time }).createdAt
            val rangeTo = checkNotNull(
                goals.maxOfOrNull { goal -> fetchRange(goal, currentDate, timeZone).to },
            )
            val sourceFrom = rangeFrom.shiftCalendarDay(days = -1, timeZone = timeZone)
            val sourceTo = rangeTo.shiftCalendarDay(days = -1, timeZone = timeZone)

            return TimeRange(
                from = Date(max(sourceFrom.time, 0L)),
                to = sourceTo,
            )
        }

        override fun isDeadlinePassed(
            goal: Goal,
            currentDate: Date,
            timeZone: TimeZone,
        ): Boolean {
            val deadlineEnd = goal.deadline.shiftCalendarDay(days = 1, timeZone = timeZone)
            return currentDate.time >= deadlineEnd.time
        }

        override fun calculate(
            goals: List<Goal>,
            tasks: List<TimeTask>,
            currentDate: Date,
            timeZone: TimeZone,
        ): List<GoalProgress> {
            val distinctTasks = tasks.distinctBy { task -> task.key }
            val allGoals = goals.filter { goal -> goal.scopeType == GoalScopeType.ALL }
            val mainCategoryGoals = goals
                .filter { goal ->
                    goal.scopeType == GoalScopeType.MAIN_CATEGORY && goal.mainCategory != null
                }
                .groupBy { goal -> checkNotNull(goal.mainCategory).id }
            val subCategoryGoals = goals
                .filter { goal ->
                    goal.scopeType == GoalScopeType.SUB_CATEGORY && goal.subCategory != null
                }
                .groupBy { goal -> checkNotNull(goal.subCategory).id }
            val actualValues = goals.associate { goal -> goal.id to 0L }.toMutableMap()
            val plannedValues = goals.associate { goal -> goal.id to 0L }.toMutableMap()
            val ranges = goals.associateWith { goal -> fetchRange(goal, currentDate, timeZone) }

            distinctTasks.forEach { task ->
                if (!task.isConsiderInStatistics) return@forEach
                fun accumulate(goal: Goal) {
                    val range = checkNotNull(ranges[goal])
                    val contribution = task.fetchContribution(goal, range)
                    if (contribution == 0L) return
                    if (task.timeRange.to.time <= currentDate.time && task.isCompleted) {
                        actualValues[goal.id] = checkNotNull(actualValues[goal.id]) + contribution
                    }
                    plannedValues[goal.id] = checkNotNull(plannedValues[goal.id]) + contribution
                }
                allGoals.forEach(::accumulate)
                mainCategoryGoals[task.category.id].orEmpty().forEach(::accumulate)
                task.subCategory?.id?.let { subCategoryId ->
                    subCategoryGoals[subCategoryId].orEmpty().forEach(::accumulate)
                }
            }

            return goals
                .map { goal ->
                    goal.createProgress(
                        actualValue = checkNotNull(actualValues[goal.id]),
                        plannedValue = checkNotNull(plannedValues[goal.id]),
                        range = checkNotNull(ranges[goal]),
                        currentDate = currentDate,
                        timeZone = timeZone,
                    )
                }
                .sortedWith(compareBy({ progress -> progress.goal.deadline.time }, { it.goal.id }))
        }

        override fun calculateDetails(
            goal: Goal,
            tasks: List<TimeTask>,
            currentDate: Date,
            timeZone: TimeZone,
        ): GoalDetails {
            val range = fetchRange(goal, currentDate, timeZone)
            val contributingTasks = tasks
                .asSequence()
                .distinctBy { task -> task.key }
                .filter { task -> task.isConsiderInStatistics }
                .filter { task -> task.matches(goal) }
                .filter { task -> task.fetchContribution(goal, range) > 0L }
                .sortedByDescending { task -> task.timeRange.from.time }
                .toList()
            val progress = calculate(
                goals = listOf(goal),
                tasks = contributingTasks,
                currentDate = currentDate,
                timeZone = timeZone,
            ).single()

            return GoalDetails(
                progress = progress,
                contributingTasks = contributingTasks,
            )
        }

        override fun calculateHistory(
            goals: List<Goal>,
            tasks: List<TimeTask>,
            currentDate: Date,
            timeZone: TimeZone,
        ): List<GoalHistory> {
            return calculate(goals, tasks, currentDate, timeZone).map { progress ->
                val goal = progress.goal
                val isAchieved = when (goal.direction) {
                    GoalDirection.AT_LEAST -> progress.actualValue >= goal.targetValue
                    GoalDirection.AT_MOST -> progress.actualValue <= goal.targetValue
                }
                val periodEnd = if (
                    goal.direction == GoalDirection.AT_LEAST &&
                    isAchieved &&
                    isDeadlinePassed(goal, currentDate, timeZone)
                ) {
                    fetchReviewEnd(goal, currentDate, timeZone)
                } else {
                    goal.deadline
                }
                GoalHistory(
                    goalId = goal.id,
                    goalTitle = goal.title,
                    metric = goal.metric,
                    direction = goal.direction,
                    targetValue = goal.targetValue,
                    actualValue = progress.actualValue,
                    periodStart = goal.createdAt,
                    periodEnd = periodEnd,
                    isAchieved = isAchieved,
                    createdAt = currentDate,
                )
            }
        }

        private fun TimeTask.matches(goal: Goal): Boolean {
            return when (goal.scopeType) {
                GoalScopeType.ALL -> true
                GoalScopeType.MAIN_CATEGORY -> goal.mainCategory?.id == category.id
                GoalScopeType.SUB_CATEGORY -> goal.subCategory?.id == subCategory?.id
            }
        }

        private fun TimeTask.fetchContribution(
            goal: Goal,
            range: TimeRange,
        ): Long {
            val intersectionStart = max(timeRange.from.time, range.from.time)
            val intersectionEnd = min(timeRange.to.time, range.to.time)
            if (intersectionStart >= intersectionEnd) return 0L

            return when (goal.metric) {
                GoalMetric.DURATION -> intersectionEnd - intersectionStart
                GoalMetric.TASK_COUNT -> 1L
            }
        }

        private fun Goal.createProgress(
            actualValue: Long,
            plannedValue: Long,
            range: TimeRange,
            currentDate: Date,
            timeZone: TimeZone,
        ): GoalProgress {
            val isScopeAvailable = when (scopeType) {
                GoalScopeType.ALL -> true
                GoalScopeType.MAIN_CATEGORY -> mainCategory != null
                GoalScopeType.SUB_CATEGORY -> subCategory != null
            }
            val deadlinePassed = isDeadlinePassed(this, currentDate, timeZone)
            val status = when {
                !isScopeAvailable -> GoalProgressStatus.UNAVAILABLE
                direction == GoalDirection.AT_LEAST && actualValue >= targetValue -> {
                    GoalProgressStatus.ACHIEVED
                }
                direction == GoalDirection.AT_MOST && actualValue > targetValue -> {
                    GoalProgressStatus.EXCEEDED
                }
                deadlinePassed && direction == GoalDirection.AT_MOST -> {
                    GoalProgressStatus.ACHIEVED
                }
                deadlinePassed -> GoalProgressStatus.EXPIRED
                else -> GoalProgressStatus.IN_PROGRESS
            }
            val progress = if (targetValue > 0L) {
                (actualValue.toDouble() / targetValue.toDouble()).toFloat()
            } else {
                0f
            }
            return GoalProgress(
                goal = this,
                actualValue = actualValue,
                plannedValue = plannedValue,
                remainingValue = (targetValue - actualValue).coerceAtLeast(0L),
                progressFraction = progress,
                goalRange = range,
                status = status,
            )
        }

        private fun fetchReviewEnd(
            goal: Goal,
            currentDate: Date,
            timeZone: TimeZone,
        ): Date {
            val currentDay = currentDate.fetchStartCalendarDay(timeZone)
            return Date(max(goal.deadline.time, currentDay.time))
        }

        private fun Date.fetchStartCalendarDay(timeZone: TimeZone): Date {
            return Calendar.getInstance(timeZone).apply {
                time = this@fetchStartCalendarDay
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

        private fun Date.shiftCalendarDay(
            days: Int,
            timeZone: TimeZone,
        ): Date {
            return Calendar.getInstance(timeZone).apply {
                time = this@shiftCalendarDay
                add(Calendar.DAY_OF_YEAR, days)
            }.time
        }
    }
}
