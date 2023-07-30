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
package ru.aleshin.features.home.api.data.mappers.schedules

import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.api.data.mappers.categories.mapToDomain
import ru.aleshin.features.home.api.data.models.timetasks.TimeTaskDetails
import ru.aleshin.features.home.api.data.models.timetasks.TimeTaskEntity
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
fun TimeTaskDetails.mapToDomain() = TimeTask(
    key = timeTask.key,
    date = timeTask.dailyScheduleDate.mapToDate(),
    timeRanges = TimeRange(timeTask.startTime.mapToDate(), timeTask.endTime.mapToDate()),
    category = mainCategory.mainCategory.mapToDomain(),
    subCategory = subCategory?.mapToDomain(mainCategory.mainCategory.mapToDomain()),
    isCompleted = timeTask.isCompleted,
    isImportant = timeTask.isImportant,
    isEnableNotification = timeTask.isEnableNotification,
    isConsiderInStatistics = timeTask.isConsiderInStatistics,
)

fun TimeTask.mapToData(dailyScheduleDate: Long) = TimeTaskEntity(
    key = key,
    dailyScheduleDate = dailyScheduleDate,
    startTime = timeRanges.from.time,
    endTime = timeRanges.to.time,
    mainCategoryId = category.id,
    subCategoryId = subCategory?.id,
    isCompleted = isCompleted,
    isImportant = isImportant,
    isEnableNotification = isEnableNotification,
    isConsiderInStatistics = isConsiderInStatistics,
)
