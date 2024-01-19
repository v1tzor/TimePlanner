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
package ru.aleshin.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.aleshin.core.data.datasources.schedules.SchedulesLocalDataSource
import ru.aleshin.core.data.mappers.schedules.ScheduleDataToDomainMapper
import ru.aleshin.core.data.mappers.schedules.mapToData
import ru.aleshin.core.data.models.tasks.TimeTaskEntity
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.utils.functional.TimeRange
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
class ScheduleRepositoryImpl @Inject constructor(
    private val localDataSource: SchedulesLocalDataSource,
    private val mapperToDomain: ScheduleDataToDomainMapper,
) : ScheduleRepository {

    override suspend fun fetchSchedulesByRange(timeRange: TimeRange?): Flow<List<Schedule>> {
        return localDataSource.fetchScheduleByRange(timeRange).map { schedules ->
            schedules.map { mapperToDomain.map(it) }
        }
    }

    override suspend fun fetchScheduleByDate(date: Long): Flow<Schedule?> {
        return localDataSource.fetchScheduleByDate(date).map { it?.map(mapperToDomain) }
    }

    override suspend fun createSchedules(schedules: List<Schedule>) {
        val dailySchedules = schedules.map { it.mapToData() }
        val timeTasks = mutableListOf<TimeTaskEntity>().apply {
            schedules.forEach { schedule ->
                addAll(schedule.timeTasks.map { it.mapToData() })
            }
        }
        localDataSource.addSchedules(dailySchedules, timeTasks)
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        localDataSource.updateTimeTasks(schedule.timeTasks.map { it.mapToData() })
    }

    override suspend fun deleteAllSchedules(): List<Schedule> {
        return localDataSource.removeAllSchedules().map { mapperToDomain.map(it) }
    }
}
