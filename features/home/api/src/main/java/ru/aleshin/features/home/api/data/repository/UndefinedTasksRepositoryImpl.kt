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
package ru.aleshin.features.home.api.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.aleshin.features.home.api.data.datasources.undefinedtasks.UndefinedTasksLocalDataSource
import ru.aleshin.features.home.api.data.mappers.schedules.mapToData
import ru.aleshin.features.home.api.data.mappers.schedules.mapToDomain
import ru.aleshin.features.home.api.domain.entities.schedules.UndefinedTask
import ru.aleshin.features.home.api.domain.repository.UndefinedTasksRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
class UndefinedTasksRepositoryImpl @Inject constructor(
    private val localDataSource: UndefinedTasksLocalDataSource,
) : UndefinedTasksRepository {

    override suspend fun addOrUpdateUndefinedTasks(tasks: List<UndefinedTask>) {
        localDataSource.addOrUpdateUndefinedTasks(tasks.map { it.mapToData() })
    }

    override fun fetchUndefinedTasks(): Flow<List<UndefinedTask>> {
        return localDataSource.fetchUndefinedTasks().map { undefinedTasks ->
            undefinedTasks.map { it.mapToDomain() }
        }
    }

    override suspend fun removeUndefinedTask(key: Long) {
        localDataSource.removeUndefinedTask(key)
    }

    override suspend fun removeAllUndefinedTasks() {
        localDataSource.removeAllUndefinedTasks()
    }
}
