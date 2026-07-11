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
import ru.aleshin.core.data.datasources.tasks.UndefinedTaskLocalDataSource
import ru.aleshin.core.data.mappers.schedules.mapToData
import ru.aleshin.core.data.mappers.schedules.mapToDomain
import ru.aleshin.core.domain.entities.tasks.UndefinedTask
import ru.aleshin.core.domain.repository.UndefinedTaskRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
class UndefinedTaskRepositoryImpl @Inject constructor(
    private val localDataSource: UndefinedTaskLocalDataSource,
) : UndefinedTaskRepository {

    override suspend fun addOrUpdateUndefinedTask(task: UndefinedTask): Long {
        return localDataSource.addOrUpdateUndefinedTask(task.mapToData())
    }

    override suspend fun addOrUpdateUndefinedTasks(tasks: List<UndefinedTask>) {
        localDataSource.addOrUpdateUndefinedTasks(tasks.map { it.mapToData() })
    }

    override suspend fun fetchUndefinedTaskByIdOnce(taskId: Long): UndefinedTask? {
        return localDataSource.fetchUndefinedTaskByIdOnce(taskId)?.mapToDomain()
    }

    override suspend fun fetchUndefinedTasks(): Flow<List<UndefinedTask>> {
        return localDataSource.fetchUndefinedTasks().map { undefinedTasks ->
            undefinedTasks.map { it.mapToDomain() }
        }
    }

    override suspend fun deleteUndefinedTask(key: Long) {
        localDataSource.deleteUndefinedTask(key)
    }

    override suspend fun deleteAllUndefinedTasks() {
        localDataSource.deleteAllUndefinedTasks()
    }
}
