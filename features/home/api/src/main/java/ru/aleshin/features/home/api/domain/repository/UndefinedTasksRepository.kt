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
package ru.aleshin.features.home.api.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.aleshin.features.home.api.domain.entities.schedules.UndefinedTask

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
interface UndefinedTasksRepository {
    suspend fun addOrUpdateUndefinedTasks(tasks: List<UndefinedTask>)
    fun fetchUndefinedTasks(): Flow<List<UndefinedTask>>
    suspend fun removeUndefinedTask(key: Long)
    suspend fun removeAllUndefinedTasks()
}
