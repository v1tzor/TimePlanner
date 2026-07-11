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
package ru.aleshin.core.presentation.mappers

import ru.aleshin.core.domain.entities.tasks.TaskNotifications
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.tasks.TimeTaskDetails
import ru.aleshin.core.presentation.models.tasks.TaskNotificationsUi
import ru.aleshin.core.presentation.models.tasks.TimeTaskDetailsUi
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.functional.TimeRange

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
fun TimeTask.mapToUi() = TimeTaskUi(
    key = key,
    date = date,
    timeRanges = timeRange,
    category = category.mapToUi(),
    subCategory = subCategory?.mapToUi(),
    linkedTemplateId = linkedTemplateId,
    isCompleted = isCompleted,
    priority = priority,
    isEnableNotification = isEnableNotification,
    taskNotifications = taskNotifications.mapToUi(),
    isConsiderInStatistics = isConsiderInStatistics,
    note = note,
)

fun TimeTaskDetails.mapToUi() = TimeTaskDetailsUi(
    key = key,
    executionStatus = executionStatus,
    date = date,
    startTime = startTime,
    endTime = endTime,
    createdAt = createdAt,
    duration = duration,
    leftTime = leftTime,
    progress = progress,
    mainCategory = mainCategory.mapToUi(),
    subCategory = subCategory?.mapToUi(),
    linkedTemplateId = linkedTemplateId,
    isCompleted = isCompleted,
    priority = priority,
    isEnableNotification = isEnableNotification,
    taskNotifications = taskNotifications.mapToUi(),
    isConsiderInStatistics = isConsiderInStatistics,
    note = note,
)

fun TaskNotifications.mapToUi() = TaskNotificationsUi(
    fifteenMinutesBefore = fifteenMinutesBefore,
    oneHourBefore = oneHourBefore,
    threeHourBefore = threeHourBefore,
    oneDayBefore = oneDayBefore,
    oneWeekBefore = oneWeekBefore,
    beforeEnd = beforeEnd,
)

fun TimeTaskDetailsUi.mapToDomain() = TimeTask(
    key = key,
    date = date,
    timeRange = TimeRange(startTime, endTime),
    createdAt = createdAt,
    category = mainCategory.mapToDomain(),
    subCategory = subCategory?.mapToDomain(),
    linkedTemplateId = linkedTemplateId,
    priority = priority,
    isCompleted = isCompleted,
    isEnableNotification = isEnableNotification,
    taskNotifications = taskNotifications.mapToDomain(),
    isConsiderInStatistics = isConsiderInStatistics,
    note = note,
)

fun TimeTaskUi.mapToDomain() = TimeTask(
    key = key,
    date = date,
    timeRange = timeRanges,
    createdAt = createdAt,
    category = category.mapToDomain(),
    subCategory = subCategory?.mapToDomain(),
    linkedTemplateId = linkedTemplateId,
    priority = priority,
    isCompleted = isCompleted,
    isEnableNotification = isEnableNotification,
    taskNotifications = taskNotifications.mapToDomain(),
    isConsiderInStatistics = isConsiderInStatistics,
    note = note,
)

fun TaskNotificationsUi.mapToDomain() = TaskNotifications(
    fifteenMinutesBefore = fifteenMinutesBefore,
    oneHourBefore = oneHourBefore,
    threeHourBefore = threeHourBefore,
    oneDayBefore = oneDayBefore,
    oneWeekBefore = oneWeekBefore,
    beforeEnd = beforeEnd,
)
