/*
 * Copyright 2023 Stanislav Aleshin
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

package ru.aleshin.features.home.api.data.datasources.schedules

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import kotlinx.coroutines.flow.Flow
import ru.aleshin.features.home.api.data.models.schedules.DailyScheduleEntity
import ru.aleshin.features.home.api.data.models.schedules.ScheduleDetails
import ru.aleshin.features.home.api.data.models.timetasks.TimeTaskEntity

/**
 * @author Stanislav Aleshin on 21.02.2023.
 */
@Dao
interface SchedulesDao {

    @Transaction
    @Query("SELECT * FROM dailySchedules WHERE date > :start AND date < :end")
    suspend fun fetchDailySchedulesByRange(start: Long, end: Long): List<ScheduleDetails>

    @Transaction
    @Query("SELECT * FROM dailySchedules")
    suspend fun fetchAllSchedules(): List<ScheduleDetails>

    @Transaction
    @Query("SELECT * FROM dailySchedules WHERE date = :date")
    fun fetchDailyScheduleByDate(date: Long): Flow<ScheduleDetails?>

    @Update
    suspend fun updateDailySchedules(schedules: List<DailyScheduleEntity>)

    @Update
    suspend fun updateTimeTasks(schedules: List<TimeTaskEntity>)

    @Insert(entity = DailyScheduleEntity::class)
    suspend fun addDailySchedules(schedules: List<DailyScheduleEntity>)

    @Insert(entity = TimeTaskEntity::class)
    suspend fun addTimeTasks(tasks: List<TimeTaskEntity>): List<Long?>

    @Delete
    suspend fun removeDailySchedule(schedule: DailyScheduleEntity)

    @Query("DELETE FROM timeTasks WHERE key IN (:keys)")
    suspend fun removeTimeTasksByKey(keys: List<Long>)

    @Query("DELETE FROM dailySchedules")
    suspend fun removeAllSchedules()
}
