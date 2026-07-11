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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.aleshin.core.data.models.schedules.DailyScheduleEntity
import ru.aleshin.core.data.models.schedules.ScheduleDetailsEntity
import ru.aleshin.core.utils.functional.TimeRange
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.02.2023.
 */
interface ScheduleLocalDataSource {

    suspend fun addOrUpdateSchedule(schedule: DailyScheduleEntity): Long
    suspend fun addOrUpdateSchedules(schedules: List<DailyScheduleEntity>)
    suspend fun fetchSchedulesDetailsByRange(timeRange: TimeRange?): Flow<List<ScheduleDetailsEntity>>
    suspend fun fetchScheduleDetailsByDate(date: Long): Flow<ScheduleDetailsEntity?>
    suspend fun deleteAllSchedules(): List<ScheduleDetailsEntity>

    class Base @Inject constructor(
        private val scheduleDao: ScheduleDao,
    ) : ScheduleLocalDataSource {

        override suspend fun addOrUpdateSchedule(schedule: DailyScheduleEntity): Long {
            return scheduleDao.addOrUpdateSchedule(schedule)
        }

        override suspend fun addOrUpdateSchedules(schedules: List<DailyScheduleEntity>) {
            scheduleDao.addOrUpdateSchedules(schedules)
        }

        override suspend fun fetchScheduleDetailsByDate(date: Long): Flow<ScheduleDetailsEntity?> {
            return scheduleDao.fetchScheduleDetailsByDate(date)
        }

        override suspend fun fetchSchedulesDetailsByRange(timeRange: TimeRange?): Flow<List<ScheduleDetailsEntity>> {
            return if (timeRange != null) {
                scheduleDao.fetchDailySchedulesByRange(timeRange.from.time, timeRange.to.time)
            } else {
                scheduleDao.fetchAllSchedulesDetails()
            }
        }

        override suspend fun deleteAllSchedules(): List<ScheduleDetailsEntity> {
            val schedules = scheduleDao.fetchAllSchedulesDetails().first()
            scheduleDao.deleteAllSchedules()

            return schedules
        }
    }
}
