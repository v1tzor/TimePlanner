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
package ru.aleshin.core.domain.entities.tasks

import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 23.02.2023.
 */
@Serializable
data class TimeTaskDetails(
    val key: Long = 0L,
    val executionStatus: TimeTaskStatus,
    @Serializable(DateSerializer::class) val date: Date,
    @Serializable(DateSerializer::class) val startTime: Date,
    @Serializable(DateSerializer::class) val endTime: Date,
    @Serializable(DateSerializer::class) val createdAt: Date? = null,
    val duration: Long,
    val leftTime: Long,
    val progress: Float,
    val mainCategory: MainCategory,
    val subCategory: SubCategory? = null,
    val linkedTemplateId: Long? = null,
    val isCompleted: Boolean = true,
    val priority: TaskPriority = TaskPriority.STANDARD,
    val isEnableNotification: Boolean = true,
    val taskNotifications: TaskNotifications = TaskNotifications(),
    val isConsiderInStatistics: Boolean = true,
    val note: String? = null,
) {
    val timeRange: TimeRange get() = TimeRange(startTime, endTime)

    fun isRunning(currentDate: Date): Boolean {
        return currentDate.time >= startTime.time && currentDate.time < endTime.time
    }
}

fun TimeTask.mapToDetails(
    executionStatus: TimeTaskStatus,
    leftTime: Long,
    progress: Float,
) = TimeTaskDetails(
    key = key,
    executionStatus = executionStatus,
    date = date,
    startTime = timeRange.from,
    endTime = timeRange.to,
    createdAt = createdAt,
    duration = duration(timeRange),
    leftTime = leftTime,
    progress = progress,
    mainCategory = category,
    subCategory = subCategory,
    linkedTemplateId = linkedTemplateId,
    isCompleted = isCompleted,
    priority = priority,
    isEnableNotification = isEnableNotification,
    taskNotifications = taskNotifications,
    isConsiderInStatistics = isConsiderInStatistics,
    note = note,
)