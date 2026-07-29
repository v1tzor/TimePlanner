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
import kotlinx.coroutines.flow.first
import ru.aleshin.core.data.models.goals.GoalDetailsEntity
import ru.aleshin.core.data.models.goals.GoalEntity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
interface GoalLocalDataSource {

    suspend fun addOrUpdateGoal(goal: GoalEntity): Long
    suspend fun addOrUpdateGoals(goals: List<GoalEntity>)
    suspend fun fetchAllGoals(): Flow<List<GoalDetailsEntity>>
    suspend fun fetchGoalById(goalId: Long): Flow<GoalDetailsEntity?>
    suspend fun fetchGoalByIdOnce(goalId: Long): GoalDetailsEntity?
    suspend fun deleteGoalById(goalId: Long)
    suspend fun deleteAllGoals(): List<GoalDetailsEntity>

    class Base @Inject constructor(
        private val goalDao: GoalDao,
    ) : GoalLocalDataSource {

        override suspend fun addOrUpdateGoal(goal: GoalEntity): Long {
            return goalDao.addOrUpdateGoal(goal)
        }

        override suspend fun addOrUpdateGoals(goals: List<GoalEntity>) {
            goalDao.addOrUpdateGoals(goals)
        }

        override suspend fun fetchAllGoals(): Flow<List<GoalDetailsEntity>> {
            return goalDao.fetchAllGoals()
        }

        override suspend fun fetchGoalById(goalId: Long): Flow<GoalDetailsEntity?> {
            return goalDao.fetchGoalById(goalId)
        }

        override suspend fun fetchGoalByIdOnce(goalId: Long): GoalDetailsEntity? {
            return goalDao.fetchGoalByIdOnce(goalId)
        }

        override suspend fun deleteGoalById(goalId: Long) {
            goalDao.deleteGoalById(goalId)
        }

        override suspend fun deleteAllGoals(): List<GoalDetailsEntity> {
            val goals = goalDao.fetchAllGoals().first()
            goalDao.deleteAllGoals()

            return goals
        }
    }
}
