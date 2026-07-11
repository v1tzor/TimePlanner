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

package ru.aleshin.core.data.datasources.schedules

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.schedules.DailyScheduleEntity
import ru.aleshin.core.data.models.schedules.ScheduleDetailsEntity

/**
 * @author Stanislav Aleshin on 21.02.2023.
 */
@Dao
interface ScheduleDao {

    @Upsert
    suspend fun addOrUpdateSchedule(schedule: DailyScheduleEntity): Long

    @Upsert
    suspend fun addOrUpdateSchedules(schedules: List<DailyScheduleEntity>)

    @Transaction
    @Query("SELECT * FROM dailySchedules ORDER BY date")
    fun fetchAllSchedulesDetails(): Flow<List<ScheduleDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM dailySchedules WHERE date >= :start AND date <= :end ORDER BY date")
    fun fetchDailySchedulesByRange(start: Long, end: Long): Flow<List<ScheduleDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM dailySchedules WHERE date = :date LIMIT 1")
    fun fetchScheduleDetailsByDate(date: Long): Flow<ScheduleDetailsEntity?>

    @Query("DELETE FROM dailySchedules")
    suspend fun deleteAllSchedules()
}
