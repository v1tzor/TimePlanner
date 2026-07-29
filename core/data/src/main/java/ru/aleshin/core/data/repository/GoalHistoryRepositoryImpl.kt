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
import ru.aleshin.core.data.datasources.goals.GoalHistoryLocalDataSource
import ru.aleshin.core.data.mappers.goals.mapToData
import ru.aleshin.core.data.mappers.goals.mapToDomain
import ru.aleshin.core.domain.entities.goals.GoalHistory
import ru.aleshin.core.domain.repository.GoalHistoryRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
class GoalHistoryRepositoryImpl @Inject constructor(
    private val localDataSource: GoalHistoryLocalDataSource,
) : GoalHistoryRepository {

    override suspend fun addGoalHistory(history: GoalHistory): Long {
        return localDataSource.addGoalHistory(history.mapToData())
    }

    override suspend fun addGoalsHistory(history: List<GoalHistory>) {
        localDataSource.addGoalsHistory(history.map { it.mapToData() })
    }

    override suspend fun fetchGoalHistoryPage(
        beforePeriodEnd: Long?,
        beforeId: Long?,
        pageSize: Int,
    ): List<GoalHistory> {
        return localDataSource.fetchGoalHistoryPage(beforePeriodEnd, beforeId, pageSize).map { goalHistory ->
            goalHistory.mapToDomain()
        }
    }

    override suspend fun fetchGoalHistoryByGoalId(goalId: Long): Flow<List<GoalHistory>> {
        return localDataSource.fetchGoalHistoryByGoalId(goalId).map { goalHistory ->
            goalHistory.map { it.mapToDomain() }
        }
    }

    override suspend fun fetchLatestGoalHistory(goalId: Long): GoalHistory? {
        return localDataSource.fetchLatestGoalHistory(goalId)?.mapToDomain()
    }

    override suspend fun fetchLatestGoalsHistory(): List<GoalHistory> {
        return localDataSource.fetchLatestGoalsHistory().map { it.mapToDomain() }
    }

    override suspend fun fetchAllGoalsHistory(): List<GoalHistory> {
        return localDataSource.fetchAllGoalsHistory().map { it.mapToDomain() }
    }

    override suspend fun deleteAllGoalsHistory(): List<GoalHistory> {
        return localDataSource.deleteAllGoalsHistory().map { it.mapToDomain() }
    }
}
