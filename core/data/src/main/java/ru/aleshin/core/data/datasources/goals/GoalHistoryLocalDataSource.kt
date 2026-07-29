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

import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.goals.GoalHistoryEntity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
interface GoalHistoryLocalDataSource {

    suspend fun addGoalHistory(history: GoalHistoryEntity): Long
    suspend fun addGoalsHistory(history: List<GoalHistoryEntity>)
    suspend fun fetchGoalHistoryPage(beforePeriodEnd: Long?, beforeId: Long?, pageSize: Int): List<GoalHistoryEntity>
    suspend fun fetchGoalHistoryByGoalId(goalId: Long): Flow<List<GoalHistoryEntity>>
    suspend fun fetchLatestGoalHistory(goalId: Long): GoalHistoryEntity?
    suspend fun fetchLatestGoalsHistory(): List<GoalHistoryEntity>
    suspend fun fetchAllGoalsHistory(): List<GoalHistoryEntity>
    suspend fun deleteAllGoalsHistory(): List<GoalHistoryEntity>

    class Base @Inject constructor(
        private val goalHistoryDao: GoalHistoryDao,
    ) : GoalHistoryLocalDataSource {

        override suspend fun addGoalHistory(history: GoalHistoryEntity): Long {
            return goalHistoryDao.addGoalHistory(history)
        }

        override suspend fun addGoalsHistory(history: List<GoalHistoryEntity>) {
            goalHistoryDao.addGoalsHistory(history)
        }

        override suspend fun fetchGoalHistoryPage(
            beforePeriodEnd: Long?,
            beforeId: Long?,
            pageSize: Int,
        ): List<GoalHistoryEntity> {
            return goalHistoryDao.fetchGoalHistoryPage(beforePeriodEnd, beforeId, pageSize)
        }

        override suspend fun fetchGoalHistoryByGoalId(
            goalId: Long,
        ): Flow<List<GoalHistoryEntity>> {
            return goalHistoryDao.fetchGoalHistoryByGoalId(goalId)
        }

        override suspend fun fetchLatestGoalHistory(goalId: Long): GoalHistoryEntity? {
            return goalHistoryDao.fetchLatestGoalHistory(goalId)
        }

        override suspend fun fetchLatestGoalsHistory(): List<GoalHistoryEntity> {
            return goalHistoryDao.fetchLatestGoalsHistory()
        }

        override suspend fun fetchAllGoalsHistory(): List<GoalHistoryEntity> {
            return goalHistoryDao.fetchAllGoalsHistory()
        }

        override suspend fun deleteAllGoalsHistory(): List<GoalHistoryEntity> {
            val history = goalHistoryDao.fetchAllGoalsHistory()
            goalHistoryDao.deleteAllGoalsHistory()

            return history
        }
    }
}
