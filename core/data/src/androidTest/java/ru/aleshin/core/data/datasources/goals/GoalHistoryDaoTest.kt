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
package ru.aleshin.core.data.datasources.goals

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.aleshin.core.data.datasources.schedules.SchedulesDataBase
import ru.aleshin.core.data.models.goals.GoalEntity
import ru.aleshin.core.data.models.goals.GoalHistoryEntity
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalScopeType

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@RunWith(AndroidJUnit4::class)
class GoalHistoryDaoTest {

    private lateinit var database: SchedulesDataBase
    private lateinit var dao: GoalHistoryDao

    @Before
    fun setUp() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            SchedulesDataBase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.fetchGoalHistoryDao()
        database.fetchGoalDao().addOrUpdateGoal(
            GoalEntity(
                id = GOAL_ID,
                title = "Goal",
                scopeType = GoalScopeType.ALL,
                metric = GoalMetric.DURATION,
                direction = GoalDirection.AT_LEAST,
                targetValue = 100L,
                createdAt = 0L,
                deadline = 1_000L,
            )
        )
        Unit
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun historyIsImmutableForSameGoalRange() = runBlocking {
        dao.addGoalHistory(history(id = 1L, periodStart = 1_000L, periodEnd = 2_000L, actual = 50L))
        val duplicateResult = dao.addGoalHistory(
            history(id = 0L, periodStart = 1_000L, periodEnd = 2_000L, actual = 90L)
        )

        val history = dao.fetchAllGoalsHistory()

        assertEquals(-1L, duplicateResult)
        assertEquals(1, history.size)
        assertEquals(50L, history.single().actualValue)
    }

    @Test
    fun historyPageUsesPeriodEndAndIdKeyset() = runBlocking {
        dao.addGoalsHistory(
            listOf(
                history(id = 1L, periodStart = 1_000L, periodEnd = 2_000L),
                history(id = 2L, periodStart = 2_000L, periodEnd = 3_000L),
                history(id = 3L, periodStart = 1_500L, periodEnd = 3_000L),
                history(id = 4L, periodStart = 3_000L, periodEnd = 4_000L),
            )
        )

        val firstPage = dao.fetchGoalHistoryPage(null, null, 2)
        val lastItem = firstPage.last()
        val secondPage = dao.fetchGoalHistoryPage(lastItem.periodEnd, lastItem.id, 2)

        assertEquals(listOf(4L, 3L), firstPage.map { item -> item.id })
        assertEquals(listOf(2L, 1L), secondPage.map { item -> item.id })
    }

    @Test
    fun historyRetainsIdentityAfterGoalDeletion() = runBlocking {
        dao.addGoalHistory(history(id = 1L, periodStart = 1_000L, periodEnd = 2_000L))
        database.fetchGoalDao().deleteGoalById(GOAL_ID)
        val duplicateResult = dao.addGoalHistory(
            history(id = 0L, periodStart = 1_000L, periodEnd = 2_000L)
        )

        val history = dao.fetchAllGoalsHistory().single()

        assertEquals(GOAL_ID, history.goalId)
        assertEquals(-1L, duplicateResult)
    }

    @Test
    fun historyPageRemainsBoundedForThreeYearFixture() = runBlocking {
        val fixture = buildList {
            repeat(FIXTURE_GOAL_COUNT) { goalIndex ->
                repeat(FIXTURE_PERIOD_COUNT) { periodIndex ->
                    val id = (goalIndex * FIXTURE_PERIOD_COUNT + periodIndex + 1).toLong()
                    val periodStart = periodIndex * FIXTURE_WEEK_MILLIS
                    add(
                        history(
                            id = id,
                            goalId = goalIndex.toLong() + 1L,
                            periodStart = periodStart,
                            periodEnd = periodStart + FIXTURE_WEEK_MILLIS,
                        )
                    )
                }
            }
        }

        dao.addGoalsHistory(fixture)

        val page = dao.fetchGoalHistoryPage(null, null, FIXTURE_PAGE_SIZE)

        assertEquals(FIXTURE_PAGE_SIZE, page.size)
    }

    private fun history(
        id: Long,
        goalId: Long = GOAL_ID,
        periodStart: Long,
        periodEnd: Long,
        actual: Long = 100L,
    ) = GoalHistoryEntity(
        id = id,
        goalId = goalId,
        goalTitle = "Goal",
        metric = GoalMetric.DURATION,
        direction = GoalDirection.AT_LEAST,
        targetValue = 100L,
        actualValue = actual,
        periodStart = periodStart,
        periodEnd = periodEnd,
        isAchieved = actual >= 100L,
        createdAt = periodEnd,
    )

    private companion object {
        const val GOAL_ID = 1L
        const val FIXTURE_GOAL_COUNT = 100
        const val FIXTURE_PERIOD_COUNT = 156
        const val FIXTURE_PAGE_SIZE = 30
        const val FIXTURE_WEEK_MILLIS = 7L * 24L * 60L * 60L * 1000L
    }
}
