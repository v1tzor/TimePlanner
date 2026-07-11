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

package ru.aleshin.core.data.datasources.tasks

import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.tasks.UndefinedTaskDetailsEntity
import ru.aleshin.core.data.models.tasks.UndefinedTaskEntity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
interface UndefinedTaskLocalDataSource {

    suspend fun addOrUpdateUndefinedTask(task: UndefinedTaskEntity): Long
    suspend fun addOrUpdateUndefinedTasks(tasks: List<UndefinedTaskEntity>)
    suspend fun fetchUndefinedTaskByIdOnce(taskId: Long): UndefinedTaskDetailsEntity?
    suspend fun fetchUndefinedTasks(): Flow<List<UndefinedTaskDetailsEntity>>
    suspend fun deleteUndefinedTask(key: Long)
    suspend fun deleteAllUndefinedTasks()

    class Base @Inject constructor(
        private val undefinedTaskDao: UndefinedTaskDao,
    ) : UndefinedTaskLocalDataSource {

        override suspend fun addOrUpdateUndefinedTask(task: UndefinedTaskEntity): Long {
            return undefinedTaskDao.addOrUpdateUndefinedTask(task)
        }

        override suspend fun addOrUpdateUndefinedTasks(tasks: List<UndefinedTaskEntity>) {
            undefinedTaskDao.addOrUpdateUndefinedTasks(tasks)
        }

        override suspend fun fetchUndefinedTaskByIdOnce(taskId: Long): UndefinedTaskDetailsEntity? {
            return undefinedTaskDao.fetchUndefinedTaskByIdOnce(taskId)
        }

        override suspend fun fetchUndefinedTasks(): Flow<List<UndefinedTaskDetailsEntity>> {
            return undefinedTaskDao.fetchAllUndefinedTasks()
        }

        override suspend fun deleteUndefinedTask(key: Long) {
            undefinedTaskDao.deleteUndefinedTask(key)
        }

        override suspend fun deleteAllUndefinedTasks() {
            undefinedTaskDao.deleteAllUndefinedTasks()
        }
    }
}
