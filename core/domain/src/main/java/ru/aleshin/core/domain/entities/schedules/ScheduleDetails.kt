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
package ru.aleshin.core.domain.entities.schedules

import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.tasks.TimeTaskDetails
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.utils.functional.DateSerializer
import java.util.Date

/**
 * @author Stanislav Aleshin on 05.07.2026.
 */
@Serializable
data class ScheduleDetails(
    @Serializable(DateSerializer::class) val date: Date,
    val dateStatus: DailyScheduleStatus,
    val timeTasks: List<TimeTaskDetails>,
    val progress: Float,
) {
    fun isCompleted(): Boolean {
        return timeTasks.all { timeTask -> timeTask.executionStatus == TimeTaskStatus.COMPLETED }
    }
}

fun Schedule.convertToDetails(
    dateStatus: DailyScheduleStatus,
    progress: Float,
    timeTaskMapper: (TimeTask) -> TimeTaskDetails
) = ScheduleDetails(
    date = date,
    dateStatus = dateStatus,
    timeTasks = (timeTasks + overlayTimeTasks).map(timeTaskMapper),
    progress = progress,
)