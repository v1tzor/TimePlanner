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
package ru.aleshin.core.data.datasources.tasks

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.tasks.UndefinedTaskDetailsEntity
import ru.aleshin.core.data.models.tasks.UndefinedTaskEntity

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Dao
interface UndefinedTaskDao {

    @Upsert
    suspend fun addOrUpdateUndefinedTask(task: UndefinedTaskEntity): Long

    @Upsert
    suspend fun addOrUpdateUndefinedTasks(tasks: List<UndefinedTaskEntity>)

    @Transaction
    @Query("SELECT * FROM undefinedTasks")
    fun fetchAllUndefinedTasks(): Flow<List<UndefinedTaskDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM undefinedTasks WHERE key = :taskId")
    fun fetchUndefinedTaskByIdOnce(taskId: Long): UndefinedTaskDetailsEntity?

    @Query("DELETE FROM undefinedTasks WHERE key = :key")
    suspend fun deleteUndefinedTask(key: Long)

    @Query("DELETE FROM undefinedTasks")
    suspend fun deleteAllUndefinedTasks()
}
