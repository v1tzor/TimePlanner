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
package ru.aleshin.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.domain.entities.goals.Goal

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
interface GoalRepository {

    suspend fun addOrUpdateGoal(goal: Goal): Long
    suspend fun addOrUpdateGoals(goals: List<Goal>)
    suspend fun fetchAllGoals(): Flow<List<Goal>>
    suspend fun fetchGoalById(goalId: Long): Flow<Goal?>
    suspend fun fetchGoalByIdOnce(goalId: Long): Goal?
    suspend fun deleteGoalById(goalId: Long)
    suspend fun deleteAllGoals(): List<Goal>
}
