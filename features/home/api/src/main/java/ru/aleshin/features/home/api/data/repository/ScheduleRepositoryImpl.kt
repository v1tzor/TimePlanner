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
 * imitations under the License.
 */
package ru.aleshin.features.home.api.data.repository

import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.api.data.datasources.schedules.SchedulesLocalDataSource
import ru.aleshin.features.home.api.data.mappers.schedules.ScheduleDataToDomainMapper
import ru.aleshin.features.home.api.data.mappers.schedules.mapToData
import ru.aleshin.features.home.api.domains.entities.schedules.Schedule
import ru.aleshin.features.home.api.domains.repository.ScheduleRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
class ScheduleRepositoryImpl @Inject constructor(
    private val localDataSource: SchedulesLocalDataSource,
    private val mapperToDomain: ScheduleDataToDomainMapper,
) : ScheduleRepository {

    override suspend fun fetchSchedulesByRange(timeRange: TimeRange): List<Schedule> {
        return localDataSource.fetchScheduleByRange(timeRange).map { mapperToDomain.map(it) }
    }

    override suspend fun fetchScheduleByDate(date: Long): Schedule? {
        return localDataSource.fetchScheduleByDate(date)?.map(mapperToDomain)
    }

    override suspend fun createSchedule(schedule: Schedule) {
        val dailySchedule = schedule.mapToData()
        val timeTasks = schedule.timeTasks.map { it.mapToData(schedule.date) }
        localDataSource.addSchedule(dailySchedule, timeTasks)
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        localDataSource.updateTimeTasks(schedule.timeTasks.map { it.mapToData(schedule.date) })
    }
}
