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
import ru.aleshin.core.data.models.tasks.TimeTaskDetailsEntity
import ru.aleshin.core.data.models.tasks.TimeTaskEntity
import ru.aleshin.core.data.models.tasks.UndefinedTaskDetailsEntity
import ru.aleshin.core.data.models.tasks.UndefinedTaskEntity
import java.util.Date

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Dao
interface TimeTaskDao {

    @Upsert
    suspend fun addOrUpdateTimeTask(timeTask: TimeTaskEntity): Long

    @Upsert
    suspend fun addOrUpdateTimeTasks(timeTasks: List<TimeTaskEntity>)

    @Transaction
    @Query("SELECT * FROM timeTasks WHERE daily_schedule_date = :date OR next_schedule_date = :date ORDER BY start_time")
    fun fetchAllTimeTasksDetailsByDate(date: Long): Flow<List<TimeTaskDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM timeTasks WHERE key = :id")
    suspend fun fetchTimeTaskDetailsById(id: Long): TimeTaskDetailsEntity?

    @Transaction
    @Query("SELECT * FROM timeTasks WHERE linked_template_id = :templateId AND daily_schedule_date = :date")
    suspend fun fetchTimeTaskByTemplate(templateId: Long, date: Long): TimeTaskDetailsEntity?

    @Query("DELETE FROM timeTasks WHERE key IN (:ids)")
    suspend fun deleteTimeTasksByIds(ids: List<Long>)
}
