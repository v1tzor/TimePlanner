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
import ru.aleshin.core.data.models.tasks.TimeTaskDetailsEntity
import ru.aleshin.core.data.models.tasks.TimeTaskEntity
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
interface TimeTaskLocalDataSource {

    suspend fun addOrUpdateTimeTask(timeTask: TimeTaskEntity): Long
    suspend fun addOrUpdateTimeTasks(timeTasks: List<TimeTaskEntity>)
    suspend fun fetchAllTimeTasksDetailsByDate(date: Date): Flow<List<TimeTaskDetailsEntity>>
    suspend fun fetchTimeTaskDetailsById(id: Long): TimeTaskDetailsEntity?
    suspend fun fetchTimeTaskByTemplate(templateId: Long, date: Date): TimeTaskDetailsEntity?
    suspend fun deleteTimeTasksByIds(ids: List<Long>)

    class Base @Inject constructor(
        private val timeTaskDao: TimeTaskDao,
    ) : TimeTaskLocalDataSource {

        override suspend fun addOrUpdateTimeTask(timeTask: TimeTaskEntity): Long {
            return timeTaskDao.addOrUpdateTimeTask(timeTask)
        }

        override suspend fun addOrUpdateTimeTasks(timeTasks: List<TimeTaskEntity>) {
            timeTaskDao.addOrUpdateTimeTasks(timeTasks)
        }

        override suspend fun fetchAllTimeTasksDetailsByDate(date: Date): Flow<List<TimeTaskDetailsEntity>> {
            return timeTaskDao.fetchAllTimeTasksDetailsByDate(date.time)
        }

        override suspend fun fetchTimeTaskDetailsById(id: Long): TimeTaskDetailsEntity? {
            return timeTaskDao.fetchTimeTaskDetailsById(id)
        }

        override suspend fun fetchTimeTaskByTemplate(templateId: Long, date: Date): TimeTaskDetailsEntity? {
            return timeTaskDao.fetchTimeTaskByTemplate(templateId, date.time)
        }

        override suspend fun deleteTimeTasksByIds(ids: List<Long>) {
            timeTaskDao.deleteTimeTasksByIds(ids)
        }
    }
}
