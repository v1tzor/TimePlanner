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

package ru.aleshin.features.home.api.data.datasources.undefinedtasks

import kotlinx.coroutines.flow.Flow
import ru.aleshin.features.home.api.data.models.tasks.UndefinedTaskDetails
import ru.aleshin.features.home.api.data.models.tasks.UndefinedTaskEntity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
interface UndefinedTasksLocalDataSource {

    suspend fun addOrUpdateUndefinedTasks(tasks: List<UndefinedTaskEntity>)
    fun fetchUndefinedTasks(): Flow<List<UndefinedTaskDetails>>
    suspend fun removeUndefinedTask(key: Long)
    suspend fun removeAllUndefinedTasks()

    class Base @Inject constructor(
        private val undefinedTasksDao: UndefinedTasksDao,
    ) : UndefinedTasksLocalDataSource {

        override suspend fun addOrUpdateUndefinedTasks(tasks: List<UndefinedTaskEntity>) {
            undefinedTasksDao.addOrUpdateUndefinedTasks(tasks)
        }

        override fun fetchUndefinedTasks(): Flow<List<UndefinedTaskDetails>> {
            return undefinedTasksDao.fetchAllUndefinedTasks()
        }

        override suspend fun removeUndefinedTask(key: Long) {
            undefinedTasksDao.removeUndefinedTask(key)
        }

        override suspend fun removeAllUndefinedTasks() {
            undefinedTasksDao.removeAllUndefinedTasks()
        }
    }
}
