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
import ru.aleshin.core.data.datasources.tasks.TimeTaskLocalDataSource
import ru.aleshin.core.data.mappers.schedules.mapToData
import ru.aleshin.core.data.mappers.schedules.mapToDomain
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.repository.TimeTaskRepository
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
class TimeTaskRepositoryImpl @Inject constructor(
    private val localDataSource: TimeTaskLocalDataSource,
) : TimeTaskRepository {

    override suspend fun addOrUpdateTimeTask(timeTask: TimeTask): Long {
        return localDataSource.addOrUpdateTimeTask(timeTask.mapToData())
    }

    override suspend fun addOrUpdateTimeTasks(timeTasks: List<TimeTask>) {
        localDataSource.addOrUpdateTimeTasks(timeTasks.map { it.mapToData() })
    }

    override suspend fun fetchAllTimeTasksByDate(date: Date): Flow<List<TimeTask>> {
        return localDataSource.fetchAllTimeTasksDetailsByDate(date).map { timeTasks ->
            timeTasks.map { it.mapToDomain() }
        }
    }

    override suspend fun fetchTimeTaskById(id: Long): TimeTask? {
        return localDataSource.fetchTimeTaskDetailsById(id)?.mapToDomain()
    }

    override suspend fun fetchTimeTaskByTemplate(templateId: Long, date: Date): TimeTask? {
        return localDataSource.fetchTimeTaskByTemplate(templateId, date)?.mapToDomain()
    }

    override suspend fun deleteTimeTasksByIds(ids: List<Long>) {
        localDataSource.deleteTimeTasksByIds(ids)
    }
}
