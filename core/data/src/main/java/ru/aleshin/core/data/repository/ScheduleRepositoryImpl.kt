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
package ru.aleshin.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.aleshin.core.data.datasources.schedules.ScheduleLocalDataSource
import ru.aleshin.core.data.mappers.schedules.mapToData
import ru.aleshin.core.data.mappers.schedules.mapToDomain
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
class ScheduleRepositoryImpl @Inject constructor(
    private val localDataSource: ScheduleLocalDataSource,
) : ScheduleRepository {

    override suspend fun addOrUpdateSchedule(schedule: BaseDailySchedule): Long {
        return localDataSource.addOrUpdateSchedule(schedule.mapToData())
    }

    override suspend fun addOrUpdateSchedules(schedules: List<BaseDailySchedule>) {
        localDataSource.addOrUpdateSchedules(schedules.map { it.mapToData() })
    }

    override suspend fun fetchSchedulesByRange(timeRange: TimeRange?): Flow<List<Schedule>> {
        return localDataSource.fetchSchedulesDetailsByRange(timeRange).map { schedules ->
            schedules.map { it.mapToDomain() }
        }
    }

    override suspend fun fetchScheduleByDate(date: Date): Flow<Schedule?> {
        return localDataSource.fetchScheduleDetailsByDate(date.time).map { it?.mapToDomain() }
    }

    override suspend fun deleteAllSchedules(): List<Schedule> {
        return localDataSource.deleteAllSchedules().map { it.mapToDomain() }
    }
}
