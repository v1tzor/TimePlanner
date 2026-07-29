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
package ru.aleshin.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.aleshin.core.data.datasources.goals.GoalLocalDataSource
import ru.aleshin.core.data.mappers.goals.mapToData
import ru.aleshin.core.data.mappers.goals.mapToDomain
import ru.aleshin.core.domain.entities.goals.Goal
import ru.aleshin.core.domain.repository.GoalRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
class GoalRepositoryImpl @Inject constructor(
    private val localDataSource: GoalLocalDataSource,
) : GoalRepository {

    override suspend fun addOrUpdateGoal(goal: Goal): Long {
        return localDataSource.addOrUpdateGoal(goal.mapToData())
    }

    override suspend fun addOrUpdateGoals(goals: List<Goal>) {
        localDataSource.addOrUpdateGoals(goals.map { it.mapToData() })
    }

    override suspend fun fetchAllGoals(): Flow<List<Goal>> {
        return localDataSource.fetchAllGoals().map { goals ->
            goals.map { it.mapToDomain() }
        }
    }

    override suspend fun fetchGoalById(goalId: Long): Flow<Goal?> {
        return localDataSource.fetchGoalById(goalId).map { it?.mapToDomain() }
    }

    override suspend fun fetchGoalByIdOnce(goalId: Long): Goal? {
        return localDataSource.fetchGoalByIdOnce(goalId)?.mapToDomain()
    }

    override suspend fun deleteGoalById(goalId: Long) {
        localDataSource.deleteGoalById(goalId)
    }

    override suspend fun deleteAllGoals(): List<Goal> {
        return localDataSource.deleteAllGoals().map { it.mapToDomain() }
    }
}
