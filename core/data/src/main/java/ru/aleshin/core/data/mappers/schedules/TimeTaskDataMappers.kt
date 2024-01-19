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
package ru.aleshin.core.data.mappers.schedules

import ru.aleshin.core.data.mappers.categories.mapToDomain
import ru.aleshin.core.data.models.tasks.TimeTaskDetails
import ru.aleshin.core.data.models.tasks.TimeTaskEntity
import ru.aleshin.core.domain.entities.schedules.TaskNotifications
import ru.aleshin.core.domain.entities.schedules.TaskPriority
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.TimeRange

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
fun TimeTaskDetails.mapToDomain() = TimeTask(
    key = timeTask.key,
    date = timeTask.dailyScheduleDate.mapToDate(),
    timeRange = TimeRange(timeTask.startTime.mapToDate(), timeTask.endTime.mapToDate()),
    createdAt = timeTask.createdAt?.mapToDate(),
    category = mainCategory.mainCategory.mapToDomain(),
    subCategory = subCategory?.mapToDomain(mainCategory.mainCategory.mapToDomain()),
    isCompleted = timeTask.isCompleted,
    priority = when {
        timeTask.isImportantMax -> TaskPriority.MAX
        timeTask.isImportantMedium -> TaskPriority.MEDIUM
        else -> TaskPriority.STANDARD
    },
    isEnableNotification = timeTask.isEnableNotification,
    taskNotifications = TaskNotifications(
        fifteenMinutesBefore = timeTask.fifteenMinutesBeforeNotify,
        oneHourBefore = timeTask.oneHourBeforeNotify,
        threeHourBefore = timeTask.threeHourBeforeNotify,
        oneDayBefore = timeTask.oneDayBeforeNotify,
        oneWeekBefore = timeTask.oneWeekBeforeNotify,
        beforeEnd = timeTask.beforeEndNotify,
    ),
    isConsiderInStatistics = timeTask.isConsiderInStatistics,
    note = timeTask.note,
)

fun TimeTask.mapToData() = TimeTaskEntity(
    key = key,
    dailyScheduleDate = date.time,
    nextScheduleDate = if (timeRange.to.isCurrentDay(date)) null else date.shiftDay(1).time,
    startTime = timeRange.from.time,
    endTime = timeRange.to.time,
    createdAt = createdAt?.time,
    mainCategoryId = category.id,
    subCategoryId = subCategory?.id,
    isCompleted = isCompleted,
    isImportantMedium = priority == TaskPriority.MEDIUM,
    isImportantMax = priority == TaskPriority.MAX,
    isEnableNotification = isEnableNotification,
    fifteenMinutesBeforeNotify = taskNotifications.fifteenMinutesBefore,
    oneHourBeforeNotify = taskNotifications.oneHourBefore,
    threeHourBeforeNotify = taskNotifications.threeHourBefore,
    oneWeekBeforeNotify = taskNotifications.oneWeekBefore,
    oneDayBeforeNotify = taskNotifications.oneDayBefore,
    beforeEndNotify = taskNotifications.beforeEnd,
    isConsiderInStatistics = isConsiderInStatistics,
    note = note,
)
