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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.goals.GoalHistoryEntity

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Dao
interface GoalHistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGoalHistory(history: GoalHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGoalsHistory(history: List<GoalHistoryEntity>)

    @Query(
        "SELECT * FROM goalHistory " +
            "WHERE :beforePeriodEnd IS NULL " +
            "OR period_end < :beforePeriodEnd " +
            "OR (period_end = :beforePeriodEnd AND id < :beforeId) " +
            "ORDER BY period_end DESC, id DESC LIMIT :pageSize"
    )
    suspend fun fetchGoalHistoryPage(beforePeriodEnd: Long?, beforeId: Long?, pageSize: Int): List<GoalHistoryEntity>

    @Query("SELECT * FROM goalHistory WHERE goal_id = :goalId ORDER BY period_end DESC, id DESC")
    fun fetchGoalHistoryByGoalId(goalId: Long): Flow<List<GoalHistoryEntity>>

    @Query("SELECT * FROM goalHistory WHERE goal_id = :goalId ORDER BY period_end DESC, id DESC LIMIT 1")
    suspend fun fetchLatestGoalHistory(goalId: Long): GoalHistoryEntity?

    @Query(
        "SELECT currentHistory.* FROM goalHistory AS currentHistory " +
            "WHERE NOT EXISTS (" +
            "SELECT 1 FROM goalHistory AS newer " +
            "WHERE newer.goal_id = currentHistory.goal_id AND (" +
            "newer.period_end > currentHistory.period_end OR " +
            "(newer.period_end = currentHistory.period_end AND newer.id > currentHistory.id))) " +
            "ORDER BY currentHistory.goal_id"
    )
    suspend fun fetchLatestGoalsHistory(): List<GoalHistoryEntity>

    @Query("SELECT * FROM goalHistory ORDER BY period_end DESC, id DESC")
    suspend fun fetchAllGoalsHistory(): List<GoalHistoryEntity>

    @Query("DELETE FROM goalHistory")
    suspend fun deleteAllGoalsHistory()
}
