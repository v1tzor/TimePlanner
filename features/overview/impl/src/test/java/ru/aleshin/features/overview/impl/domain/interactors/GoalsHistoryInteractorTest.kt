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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.domain.common.GoalProgressManager
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.goals.Goal
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalHistory
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalScopeType
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.repository.GoalHistoryRepository
import ru.aleshin.core.domain.repository.GoalRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper
import ru.aleshin.features.overview.impl.domain.common.OverviewErrorHandler
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
internal class GoalsHistoryInteractorTest {

    @Test
    fun syncCompletedGoalsAddsOneSuccessfulSnapshotAfterLateAchievement() = runBlocking {
        val deadline = createDate(2026, Calendar.JULY, 28, 0)
        val currentDate = createDate(2026, Calendar.JULY, 30, 12)
        val goal = Goal(
            id = GOAL_ID,
            title = "Study",
            scopeType = GoalScopeType.ALL,
            metric = GoalMetric.DURATION,
            direction = GoalDirection.AT_LEAST,
            targetValue = MILLIS_IN_HOUR,
            createdAt = createDate(2026, Calendar.JULY, 26, 0),
            deadline = deadline,
        )
        val lateTask = TimeTask(
            key = 1L,
            date = createDate(2026, Calendar.JULY, 30, 0),
            timeRange = TimeRange(
                from = createDate(2026, Calendar.JULY, 30, 9),
                to = createDate(2026, Calendar.JULY, 30, 10),
            ),
            category = MainCategory(id = 1L),
            isCompleted = true,
            isConsiderInStatistics = true,
        )
        val historyRepository = FakeGoalHistoryRepository(
            initialHistory = listOf(
                GoalHistory(
                    id = 1L,
                    goalId = goal.id,
                    goalTitle = goal.title,
                    metric = goal.metric,
                    direction = goal.direction,
                    targetValue = goal.targetValue,
                    actualValue = 0L,
                    periodStart = goal.createdAt,
                    periodEnd = deadline,
                    isAchieved = false,
                    createdAt = createDate(2026, Calendar.JULY, 29, 0),
                ),
            ),
        )
        val interactor = GoalsHistoryInteractor.Base(
            goalRepository = FakeGoalRepository(goal),
            goalHistoryRepository = historyRepository,
            timeTaskRepository = FakeGoalTimeTaskRepository(lateTask),
            progressManager = GoalProgressManager.Base(),
            dateManager = FakeGoalDateManager(currentDate),
            eitherWrapper = OverviewEitherWrapper.Base(OverviewErrorHandler.Base()),
        )

        val firstResult = interactor.syncCompletedGoals()
        val secondResult = interactor.syncCompletedGoals()
        val latestHistory = historyRepository.history.maxBy { history -> history.periodEnd.time }

        assertTrue(firstResult.isRight)
        assertTrue(secondResult.isRight)
        assertEquals(2, historyRepository.history.size)
        assertTrue(latestHistory.isAchieved)
        assertEquals(MILLIS_IN_HOUR, latestHistory.actualValue)
        assertEquals(createDate(2026, Calendar.JULY, 30, 0), latestHistory.periodEnd)
    }

    private fun createDate(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
    ): Date {
        return Calendar.getInstance().apply {
            clear()
            set(year, month, day, hour, 0, 0)
        }.time
    }
}

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
private class FakeGoalRepository(
    private val goal: Goal,
) : GoalRepository {

    override suspend fun addOrUpdateGoal(goal: Goal): Long = goal.id

    override suspend fun addOrUpdateGoals(goals: List<Goal>) = Unit

    override suspend fun fetchAllGoals(): Flow<List<Goal>> = flowOf(listOf(goal))

    override suspend fun fetchGoalById(goalId: Long): Flow<Goal?> {
        return flowOf(goal.takeIf { goal.id == goalId })
    }

    override suspend fun fetchGoalByIdOnce(goalId: Long): Goal? {
        return goal.takeIf { goal.id == goalId }
    }

    override suspend fun deleteGoalById(goalId: Long) = Unit

    override suspend fun deleteAllGoals(): List<Goal> = listOf(goal)
}

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
private class FakeGoalHistoryRepository(
    initialHistory: List<GoalHistory>,
) : GoalHistoryRepository {

    val history = initialHistory.toMutableList()

    override suspend fun addGoalHistory(history: GoalHistory): Long {
        val id = (this.history.maxOfOrNull { item -> item.id } ?: 0L) + 1L
        this.history.add(history.copy(id = id))
        return id
    }

    override suspend fun addGoalsHistory(history: List<GoalHistory>) {
        history.forEach { item -> addGoalHistory(item) }
    }

    override suspend fun fetchGoalHistoryPage(
        beforePeriodEnd: Long?,
        beforeId: Long?,
        pageSize: Int,
    ): List<GoalHistory> {
        return history.sortedByDescending { item -> item.periodEnd.time }.take(pageSize)
    }

    override suspend fun fetchGoalHistoryByGoalId(goalId: Long): Flow<List<GoalHistory>> {
        return flowOf(history.filter { item -> item.goalId == goalId })
    }

    override suspend fun fetchLatestGoalHistory(goalId: Long): GoalHistory? {
        return history
            .filter { item -> item.goalId == goalId }
            .maxWithOrNull(compareBy({ item -> item.periodEnd.time }, { item -> item.id }))
    }

    override suspend fun fetchLatestGoalsHistory(): List<GoalHistory> {
        return history
            .groupBy { item -> item.goalId }
            .mapNotNull { (_, items) ->
                items.maxWithOrNull(compareBy({ item -> item.periodEnd.time }, { item -> item.id }))
            }
    }

    override suspend fun fetchAllGoalsHistory(): List<GoalHistory> = history

    override suspend fun deleteAllGoalsHistory(): List<GoalHistory> {
        return history.toList().also { history.clear() }
    }
}

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
private class FakeGoalTimeTaskRepository(
    private val task: TimeTask,
) : TimeTaskRepository {

    override suspend fun addOrUpdateTimeTask(timeTask: TimeTask): Long = timeTask.key

    override suspend fun addOrUpdateTimeTasks(timeTasks: List<TimeTask>) = Unit

    override suspend fun fetchAllTimeTasksByDate(date: Date): Flow<List<TimeTask>> {
        return flowOf(listOf(task))
    }

    override suspend fun fetchTimeTasksByScheduleDateRange(timeRange: TimeRange): Flow<List<TimeTask>> {
        return flowOf(listOf(task))
    }

    override suspend fun fetchTimeTaskById(id: Long): TimeTask? = task.takeIf { task.key == id }

    override suspend fun fetchTimeTaskByTemplate(templateId: Long, date: Date): TimeTask? = null

    override suspend fun deleteTimeTasksByIds(ids: List<Long>) = Unit
}

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
private class FakeGoalDateManager(
    private val currentDate: Date,
) : DateManager {

    override fun fetchCurrentDate(): Date = currentDate

    override fun fetchBeginningCurrentDay(): Date = currentDate.startDay()

    override fun fetchEndCurrentDay(): Date {
        return Calendar.getInstance().apply {
            time = currentDate
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time
    }

    override fun fetchTicker(): Flow<Date> = flowOf(currentDate)

    override fun fetchMinuteTicker(): Flow<Date> = flowOf(currentDate)

    override fun calculateLeftTime(endTime: Date): Long = endTime.time - currentDate.time

    override fun calculateProgress(startTime: Date, endTime: Date): Float {
        return (currentDate.time - startTime.time).toFloat() / (endTime.time - startTime.time)
    }

    override fun setCurrentHMS(date: Date): Date = date

    private fun Date.startDay(): Date {
        return Calendar.getInstance().apply {
            time = this@startDay
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}

private const val GOAL_ID = 1L
private const val MILLIS_IN_HOUR = 60L * 60L * 1000L
