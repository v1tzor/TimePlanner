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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.goals.Goal
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalProgressStatus
import ru.aleshin.core.domain.entities.goals.GoalScopeType
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
class GoalProgressManagerTest {

    private val manager = GoalProgressManager.Base()
    private val timeZone = TimeZone.getTimeZone("UTC")

    @Test
    fun calculateDeduplicatesTasksAndSeparatesActualFromPlanned() {
        val currentDate = createDate(2026, Calendar.JULY, 29, 12)
        val goal = createGoal(targetValue = 2L * MILLIS_IN_HOUR)
        val completedTask = createTask(
            key = 100L,
            from = createDate(2026, Calendar.JULY, 28, 9),
            to = createDate(2026, Calendar.JULY, 28, 10),
        )
        val futureTask = createTask(
            key = 101L,
            from = createDate(2026, Calendar.JULY, 30, 9),
            to = createDate(2026, Calendar.JULY, 30, 11),
        )

        val progress = manager.calculate(
            goals = listOf(goal),
            tasks = listOf(completedTask, completedTask, futureTask),
            currentDate = currentDate,
            timeZone = timeZone,
        ).single()

        assertEquals(MILLIS_IN_HOUR, progress.actualValue)
        assertEquals(3L * MILLIS_IN_HOUR, progress.plannedValue)
        assertEquals(MILLIS_IN_HOUR, progress.remainingValue)
        assertEquals(0.5f, progress.progressFraction)
        assertEquals(GoalProgressStatus.IN_PROGRESS, progress.status)
    }

    @Test
    fun calculateClipsDurationToCreationAndCurrentDay() {
        val goal = createGoal(
            targetValue = 2L * MILLIS_IN_HOUR,
            createdAt = createDate(2026, Calendar.JULY, 28, 12),
            deadline = createDate(2026, Calendar.JULY, 29, 0),
        )
        val beforeCreation = createTask(
            key = 1L,
            from = createDate(2026, Calendar.JULY, 28, 11),
            to = createDate(2026, Calendar.JULY, 28, 13),
        )
        val afterCurrentDay = createTask(
            key = 2L,
            from = createDate(2026, Calendar.JULY, 31, 23),
            to = createDate(2026, Calendar.AUGUST, 1, 1),
        )

        val progress = manager.calculate(
            goals = listOf(goal),
            tasks = listOf(beforeCreation, afterCurrentDay),
            currentDate = createDate(2026, Calendar.JULY, 31, 12),
            timeZone = timeZone,
        ).single()

        assertEquals(MILLIS_IN_HOUR, progress.actualValue)
        assertEquals(2L * MILLIS_IN_HOUR, progress.plannedValue)
        assertEquals(GoalProgressStatus.EXPIRED, progress.status)
    }

    @Test
    fun calculateKeepsSkippedPastTaskOnlyInPlannedValue() {
        val skippedTask = createTask(
            key = 1L,
            from = createDate(2026, Calendar.JULY, 28, 9),
            to = createDate(2026, Calendar.JULY, 28, 10),
            isCompleted = false,
        )

        val progress = manager.calculate(
            goals = listOf(createGoal(targetValue = MILLIS_IN_HOUR)),
            tasks = listOf(skippedTask),
            currentDate = createDate(2026, Calendar.JULY, 29, 12),
            timeZone = timeZone,
        ).single()

        assertEquals(0L, progress.actualValue)
        assertEquals(MILLIS_IN_HOUR, progress.plannedValue)
    }

    @Test
    fun calculateFiltersTasksByCategoryScope() {
        val targetCategory = MainCategory(id = 1L)
        val goal = createGoal(
            scopeType = GoalScopeType.MAIN_CATEGORY,
            mainCategory = targetCategory,
            metric = GoalMetric.TASK_COUNT,
            targetValue = 2L,
        )
        val tasks = listOf(
            createTask(
                key = 1L,
                category = targetCategory,
                from = createDate(2026, Calendar.JULY, 28, 9),
                to = createDate(2026, Calendar.JULY, 28, 10),
            ),
            createTask(
                key = 2L,
                category = MainCategory(id = 2L),
                from = createDate(2026, Calendar.JULY, 28, 10),
                to = createDate(2026, Calendar.JULY, 28, 11),
            ),
        )

        val progress = manager.calculate(
            goals = listOf(goal),
            tasks = tasks,
            currentDate = createDate(2026, Calendar.JULY, 29, 12),
            timeZone = timeZone,
        ).single()

        assertEquals(1L, progress.actualValue)
        assertEquals(1L, progress.plannedValue)
    }

    @Test
    fun calculateMarksExceededAtMostGoal() {
        val goal = createGoal(
            metric = GoalMetric.TASK_COUNT,
            direction = GoalDirection.AT_MOST,
            targetValue = 1L,
        )
        val tasks = listOf(
            createTask(
                key = 1L,
                from = createDate(2026, Calendar.JULY, 28, 9),
                to = createDate(2026, Calendar.JULY, 28, 10),
            ),
            createTask(
                key = 2L,
                from = createDate(2026, Calendar.JULY, 28, 10),
                to = createDate(2026, Calendar.JULY, 28, 11),
            ),
        )

        val progress = manager.calculate(
            goals = listOf(goal),
            tasks = tasks,
            currentDate = createDate(2026, Calendar.JULY, 29, 12),
            timeZone = timeZone,
        ).single()

        assertEquals(GoalProgressStatus.EXCEEDED, progress.status)
        assertEquals(2f, progress.progressFraction)
    }

    @Test
    fun calculateUsesFullDeadlineDay() {
        val goal = createGoal(
            metric = GoalMetric.TASK_COUNT,
            targetValue = 1L,
            deadline = createDate(2026, Calendar.JULY, 29, 0),
        )

        val beforeBoundary = manager.calculate(
            goals = listOf(goal),
            tasks = emptyList(),
            currentDate = createDate(2026, Calendar.JULY, 29, 23),
            timeZone = timeZone,
        ).single()
        val atBoundary = manager.calculate(
            goals = listOf(goal),
            tasks = emptyList(),
            currentDate = createDate(2026, Calendar.JULY, 30, 0),
            timeZone = timeZone,
        ).single()

        assertEquals(GoalProgressStatus.IN_PROGRESS, beforeBoundary.status)
        assertEquals(GoalProgressStatus.EXPIRED, atBoundary.status)
    }

    @Test
    fun calculateIncludesCompletedTaskAfterDeadlineThroughCurrentDay() {
        val goal = createGoal(
            targetValue = MILLIS_IN_HOUR,
            deadline = createDate(2026, Calendar.JULY, 28, 0),
        )
        val lateCompletedTask = createTask(
            key = 1L,
            from = createDate(2026, Calendar.JULY, 30, 9),
            to = createDate(2026, Calendar.JULY, 30, 10),
        )

        val progress = manager.calculate(
            goals = listOf(goal),
            tasks = listOf(lateCompletedTask),
            currentDate = createDate(2026, Calendar.JULY, 30, 12),
            timeZone = timeZone,
        ).single()

        assertEquals(MILLIS_IN_HOUR, progress.actualValue)
        assertEquals(0L, progress.remainingValue)
        assertEquals(GoalProgressStatus.ACHIEVED, progress.status)
    }

    @Test
    fun calculateExcludesTaskAfterCurrentDayForExpiredGoal() {
        val goal = createGoal(
            targetValue = MILLIS_IN_HOUR,
            deadline = createDate(2026, Calendar.JULY, 28, 0),
        )
        val futureTask = createTask(
            key = 1L,
            from = createDate(2026, Calendar.JULY, 31, 9),
            to = createDate(2026, Calendar.JULY, 31, 10),
        )

        val progress = manager.calculate(
            goals = listOf(goal),
            tasks = listOf(futureTask),
            currentDate = createDate(2026, Calendar.JULY, 30, 12),
            timeZone = timeZone,
        ).single()

        assertEquals(0L, progress.actualValue)
        assertEquals(0L, progress.plannedValue)
        assertEquals(GoalProgressStatus.EXPIRED, progress.status)
    }

    @Test
    fun calculateSortsGoalsByStatusDeadlineAndId() {
        val goals = listOf(
            createGoal(id = 4L, deadline = createDate(2026, Calendar.AUGUST, 2, 0)),
            createGoal(id = 3L, deadline = createDate(2026, Calendar.JULY, 27, 0)),
            createGoal(id = 2L, deadline = createDate(2026, Calendar.AUGUST, 1, 0)),
            createGoal(id = 1L, deadline = createDate(2026, Calendar.AUGUST, 1, 0)),
        )

        val progress = manager.calculate(
            goals = goals,
            tasks = emptyList(),
            currentDate = createDate(2026, Calendar.JULY, 29, 12),
            timeZone = timeZone,
        )

        assertEquals(listOf(3L, 1L, 2L, 4L), progress.map { item -> item.goal.id })
    }

    @Test
    fun calculateDetailsReturnsOnlyContributingTasks() {
        val goal = createGoal(
            scopeType = GoalScopeType.MAIN_CATEGORY,
            mainCategory = MainCategory(id = 1L),
            metric = GoalMetric.TASK_COUNT,
            targetValue = 1L,
        )
        val contributingTask = createTask(
            key = 1L,
            category = MainCategory(id = 1L),
            from = createDate(2026, Calendar.JULY, 28, 9),
            to = createDate(2026, Calendar.JULY, 28, 10),
        )
        val unrelatedTask = createTask(
            key = 2L,
            category = MainCategory(id = 2L),
            from = createDate(2026, Calendar.JULY, 28, 10),
            to = createDate(2026, Calendar.JULY, 28, 11),
        )

        val details = manager.calculateDetails(
            goal = goal,
            tasks = listOf(unrelatedTask, contributingTask, contributingTask),
            currentDate = createDate(2026, Calendar.JULY, 29, 12),
            timeZone = timeZone,
        )

        assertEquals(listOf(contributingTask), details.contributingTasks)
        assertEquals(1L, details.progress.actualValue)
    }

    @Test
    fun calculateHistoryCreatesDenormalizedSnapshot() {
        val category = MainCategory(id = 2L)
        val goal = createGoal(
            id = 7L,
            title = "Training",
            scopeType = GoalScopeType.MAIN_CATEGORY,
            mainCategory = category,
            metric = GoalMetric.TASK_COUNT,
            direction = GoalDirection.AT_MOST,
            targetValue = 2L,
        )
        val task = createTask(
            key = 1L,
            category = category,
            from = createDate(2026, Calendar.JULY, 28, 9),
            to = createDate(2026, Calendar.JULY, 28, 10),
        )

        val history = manager.calculateHistory(
            goals = listOf(goal),
            tasks = listOf(task, task),
            currentDate = createDate(2026, Calendar.AUGUST, 4, 0),
            timeZone = timeZone,
        ).single()

        assertEquals(7L, history.goalId)
        assertEquals("Training", history.goalTitle)
        assertEquals(1L, history.actualValue)
        assertEquals(2L, history.targetValue)
        assertTrue(history.isAchieved)
    }

    @Test
    fun calculateHistoryUsesCurrentDayForLateAchievement() {
        val goal = createGoal(
            targetValue = MILLIS_IN_HOUR,
            deadline = createDate(2026, Calendar.JULY, 28, 0),
        )
        val lateCompletedTask = createTask(
            key = 1L,
            from = createDate(2026, Calendar.JULY, 30, 9),
            to = createDate(2026, Calendar.JULY, 30, 10),
        )

        val history = manager.calculateHistory(
            goals = listOf(goal),
            tasks = listOf(lateCompletedTask),
            currentDate = createDate(2026, Calendar.JULY, 30, 12),
            timeZone = timeZone,
        ).single()

        assertEquals(createDate(2026, Calendar.JULY, 30, 0), history.periodEnd)
        assertEquals(MILLIS_IN_HOUR, history.actualValue)
        assertTrue(history.isAchieved)
    }

    @Test
    fun fetchRangeIncludesDeadlineDayAcrossDst() {
        val berlinTimeZone = TimeZone.getTimeZone("Europe/Berlin")
        val goal = createGoal(
            createdAt = createDate(2026, Calendar.MARCH, 27, 12, berlinTimeZone),
            deadline = createDate(2026, Calendar.MARCH, 29, 0, berlinTimeZone),
        )

        val range = manager.fetchRange(
            goal = goal,
            currentDate = createDate(2026, Calendar.MARCH, 28, 12, berlinTimeZone),
            timeZone = berlinTimeZone,
        )
        val deadlineEnd = Calendar.getInstance(berlinTimeZone).apply { time = range.to }

        assertEquals(goal.createdAt, range.from)
        assertEquals(Calendar.MARCH, deadlineEnd[Calendar.MONTH])
        assertEquals(30, deadlineEnd[Calendar.DAY_OF_MONTH])
        assertEquals(0, deadlineEnd[Calendar.HOUR_OF_DAY])
    }

    @Test
    fun fetchRangeExtendsThroughCurrentLocalDayAcrossDst() {
        val berlinTimeZone = TimeZone.getTimeZone("Europe/Berlin")
        val goal = createGoal(
            createdAt = createDate(2026, Calendar.MARCH, 26, 12, berlinTimeZone),
            deadline = createDate(2026, Calendar.MARCH, 27, 0, berlinTimeZone),
        )

        val range = manager.fetchRange(
            goal = goal,
            currentDate = createDate(2026, Calendar.MARCH, 29, 12, berlinTimeZone),
            timeZone = berlinTimeZone,
        )
        val reviewEnd = Calendar.getInstance(berlinTimeZone).apply { time = range.to }

        assertEquals(Calendar.MARCH, reviewEnd[Calendar.MONTH])
        assertEquals(30, reviewEnd[Calendar.DAY_OF_MONTH])
        assertEquals(0, reviewEnd[Calendar.HOUR_OF_DAY])
    }

    @Test
    fun fetchTaskSourceRangeIncludesPreviousScheduleDay() {
        val goal = createGoal(
            createdAt = createDate(2026, Calendar.JULY, 28, 12),
            deadline = createDate(2026, Calendar.JULY, 30, 0),
        )

        val range = checkNotNull(
            manager.fetchTaskSourceRange(
                goals = listOf(goal),
                currentDate = createDate(2026, Calendar.JULY, 29, 12),
                timeZone = timeZone,
            ),
        )

        assertEquals(createDate(2026, Calendar.JULY, 27, 12), range.from)
        assertEquals(createDate(2026, Calendar.JULY, 30, 0), range.to)
    }

    @Test
    fun fetchTaskSourceRangeExtendsThroughCurrentDayAfterDeadline() {
        val goal = createGoal(
            createdAt = createDate(2026, Calendar.JULY, 26, 12),
            deadline = createDate(2026, Calendar.JULY, 28, 0),
        )

        val range = checkNotNull(
            manager.fetchTaskSourceRange(
                goals = listOf(goal),
                currentDate = createDate(2026, Calendar.JULY, 30, 12),
                timeZone = timeZone,
            ),
        )

        assertEquals(createDate(2026, Calendar.JULY, 25, 12), range.from)
        assertEquals(createDate(2026, Calendar.JULY, 30, 0), range.to)
    }

    @Test
    fun calculateHandlesReleaseLikeFixtureInOneTaskPass() {
        val categories = List(CATEGORY_COUNT) { index ->
            MainCategory(id = index.toLong() + 1L, customName = "Category $index")
        }
        val goals = List(GOAL_COUNT) { index ->
            createGoal(
                id = index.toLong() + 1L,
                scopeType = GoalScopeType.MAIN_CATEGORY,
                mainCategory = categories[index % categories.size],
                metric = GoalMetric.TASK_COUNT,
                targetValue = TASK_COUNT_PER_CATEGORY.toLong(),
            )
        }
        val tasks = List(TASK_COUNT) { index ->
            createTask(
                key = index.toLong() + 1L,
                category = categories[index % categories.size],
                from = createDate(2026, Calendar.JULY, 28, 9),
                to = createDate(2026, Calendar.JULY, 28, 10),
            )
        }

        val progress = manager.calculate(
            goals = goals,
            tasks = tasks,
            currentDate = createDate(2026, Calendar.JULY, 29, 12),
            timeZone = timeZone,
        )

        assertEquals(GOAL_COUNT, progress.size)
        progress.forEach { item ->
            assertEquals(TASK_COUNT_PER_CATEGORY.toLong(), item.actualValue)
            assertEquals(GoalProgressStatus.ACHIEVED, item.status)
        }
    }

    private fun createGoal(
        id: Long = 1L,
        title: String = "Goal",
        scopeType: GoalScopeType = GoalScopeType.ALL,
        mainCategory: MainCategory? = null,
        metric: GoalMetric = GoalMetric.DURATION,
        direction: GoalDirection = GoalDirection.AT_LEAST,
        targetValue: Long = 1L,
        createdAt: Date = createDate(2026, Calendar.JULY, 26, 0),
        deadline: Date = createDate(2026, Calendar.AUGUST, 2, 0),
    ) = Goal(
        id = id,
        title = title,
        scopeType = scopeType,
        mainCategory = mainCategory,
        metric = metric,
        direction = direction,
        targetValue = targetValue,
        createdAt = createdAt,
        deadline = deadline,
    )

    private fun createTask(
        key: Long,
        category: MainCategory = MainCategory(id = 1L),
        from: Date,
        to: Date,
        isCompleted: Boolean = true,
    ) = TimeTask(
        key = key,
        date = from,
        timeRange = TimeRange(from, to),
        category = category,
        isCompleted = isCompleted,
        isConsiderInStatistics = true,
    )

    private fun createDate(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        timeZone: TimeZone = this.timeZone,
    ) = Calendar.getInstance(timeZone).apply {
        clear()
        set(year, month, day, hour, 0, 0)
    }.time
}

private const val MILLIS_IN_HOUR = 60L * 60L * 1000L
private const val CATEGORY_COUNT = 10
private const val GOAL_COUNT = 100
private const val TASK_COUNT = 10_000
private const val TASK_COUNT_PER_CATEGORY = TASK_COUNT / CATEGORY_COUNT
