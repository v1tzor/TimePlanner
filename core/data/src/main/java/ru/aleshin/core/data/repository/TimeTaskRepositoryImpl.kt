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
package ru.aleshin.core.data.repository

import kotlinx.coroutines.flow.first
import ru.aleshin.core.data.datasources.schedules.SchedulesLocalDataSource
import ru.aleshin.core.data.mappers.schedules.mapToData
import ru.aleshin.core.data.mappers.schedules.mapToDomain
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.domain.repository.TimeTaskRepository
import java.util.*
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
class TimeTaskRepositoryImpl @Inject constructor(
    private val localDataSource: SchedulesLocalDataSource,
) : TimeTaskRepository {

    override suspend fun addTimeTasks(timeTasks: List<TimeTask>) {
        localDataSource.addTimeTasks(timeTasks.map { it.mapToData() })
    }

    override suspend fun fetchAllTimeTaskByDate(date: Date): List<TimeTask> {
        val schedules = localDataSource.fetchScheduleByDate(date.time).first() ?: return emptyList()
        val timeTasks = schedules.overlayTimeTasks + schedules.timeTasks
        return timeTasks.map { timeTaskDetails -> timeTaskDetails.mapToDomain() }
    }

    override suspend fun updateTimeTaskList(timeTaskList: List<TimeTask>) {
        localDataSource.updateTimeTasks(timeTaskList.map { it.mapToData() })
    }

    override suspend fun updateTimeTask(timeTask: TimeTask) {
        localDataSource.updateTimeTasks(listOf(timeTask.mapToData()))
    }

    override suspend fun deleteTimeTasks(keys: List<Long>) {
        localDataSource.removeTimeTasksByKey(keys)
    }
}
