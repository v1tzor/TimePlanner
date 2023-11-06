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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.api.data.models.schedules.DailyScheduleEntity
import ru.aleshin.features.home.api.data.models.schedules.ScheduleDetails
import ru.aleshin.features.home.api.data.models.tasks.TimeTaskEntity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.02.2023.
 */
interface SchedulesLocalDataSource {

    suspend fun addSchedules(schedules: List<DailyScheduleEntity>, timeTasks: List<TimeTaskEntity>)
    suspend fun addTimeTasks(tasks: List<TimeTaskEntity>)
    fun fetchScheduleByDate(date: Long): Flow<ScheduleDetails?>
    suspend fun fetchScheduleByRange(timeRange: TimeRange?): Flow<List<ScheduleDetails>>
    suspend fun updateTimeTasks(timeTasks: List<TimeTaskEntity>)
    suspend fun removeDailySchedule(schedule: DailyScheduleEntity)
    suspend fun removeAllSchedules(): List<ScheduleDetails>
    suspend fun removeTimeTasksByKey(keys: List<Long>)

    class Base @Inject constructor(
        private val scheduleDao: SchedulesDao,
    ) : SchedulesLocalDataSource {

        override suspend fun addSchedules(
            schedules: List<DailyScheduleEntity>,
            timeTasks: List<TimeTaskEntity>,
        ) {
            scheduleDao.addDailySchedules(schedules)
            scheduleDao.addTimeTasks(timeTasks)
        }

        override suspend fun addTimeTasks(tasks: List<TimeTaskEntity>) {
            scheduleDao.addTimeTasks(tasks)
        }

        override fun fetchScheduleByDate(date: Long): Flow<ScheduleDetails?> {
            return scheduleDao.fetchDailyScheduleByDate(date)
        }

        override suspend fun fetchScheduleByRange(timeRange: TimeRange?): Flow<List<ScheduleDetails>> {
            return if (timeRange != null) {
                scheduleDao.fetchDailySchedulesByRange(timeRange.from.time, timeRange.to.time)
            } else {
                scheduleDao.fetchAllSchedules()
            }
        }

        override suspend fun updateTimeTasks(timeTasks: List<TimeTaskEntity>) {
            scheduleDao.updateTimeTasks(timeTasks)
        }

        override suspend fun removeTimeTasksByKey(keys: List<Long>) {
            scheduleDao.removeTimeTasksByKey(keys)
        }

        override suspend fun removeDailySchedule(schedule: DailyScheduleEntity) {
            scheduleDao.removeDailySchedule(schedule)
        }

        override suspend fun removeAllSchedules(): List<ScheduleDetails> {
            val deletableSchedules = scheduleDao.fetchAllSchedules().first()
            scheduleDao.removeAllSchedules()

            return deletableSchedules
        }
    }
}
