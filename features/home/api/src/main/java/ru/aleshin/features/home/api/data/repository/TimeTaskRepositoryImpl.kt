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

import ru.aleshin.features.home.api.data.datasources.schedules.SchedulesLocalDataSource
import ru.aleshin.features.home.api.data.mappers.schedules.mapToData
import ru.aleshin.features.home.api.data.mappers.schedules.mapToDomain
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domains.repository.TimeTaskRepository
import java.util.*
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
class TimeTaskRepositoryImpl @Inject constructor(
    private val localDataSource: SchedulesLocalDataSource,
) : TimeTaskRepository {

    override suspend fun addTimeTasks(timeTasks: List<TimeTask>) {
        localDataSource.addTimeTasks(timeTasks.map { it.mapToData(it.date.time) })
    }

    override suspend fun fetchAllTimeTaskByDate(date: Date): List<TimeTask> {
        return localDataSource.fetchScheduleByDate(date.time)?.timeTasks?.map { timeTaskDetails ->
            timeTaskDetails.mapToDomain()
        } ?: emptyList()
    }

    override suspend fun updateTimeTask(timeTask: TimeTask) {
        localDataSource.updateTimeTasks(listOf(timeTask.mapToData(timeTask.date.time)))
    }

    override suspend fun deleteTimeTask(key: Long) {
        localDataSource.removeTimeTaskByKey(key)
    }
}
