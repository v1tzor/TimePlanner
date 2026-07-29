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
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.goals.GoalDetailsEntity
import ru.aleshin.core.data.models.goals.GoalEntity

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Dao
interface GoalDao {

    @Upsert
    suspend fun addOrUpdateGoal(goal: GoalEntity): Long

    @Upsert
    suspend fun addOrUpdateGoals(goals: List<GoalEntity>)

    @Transaction
    @Query("SELECT * FROM goals ORDER BY created_at ASC, id ASC")
    fun fetchAllGoals(): Flow<List<GoalDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun fetchGoalById(goalId: Long): Flow<GoalDetailsEntity?>

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun fetchGoalByIdOnce(goalId: Long): GoalDetailsEntity?

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Long)

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()
}
