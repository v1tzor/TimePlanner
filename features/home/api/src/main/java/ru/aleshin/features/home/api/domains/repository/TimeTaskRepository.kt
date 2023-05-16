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
package ru.aleshin.features.home.api.domains.repository

import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
import java.util.*

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
interface TimeTaskRepository {
    suspend fun addTimeTasks(timeTasks: List<TimeTask>)
    suspend fun fetchAllTimeTaskByDate(date: Date): List<TimeTask>
    suspend fun updateTimeTask(timeTask: TimeTask)
    suspend fun deleteTimeTask(key: Long)
}
