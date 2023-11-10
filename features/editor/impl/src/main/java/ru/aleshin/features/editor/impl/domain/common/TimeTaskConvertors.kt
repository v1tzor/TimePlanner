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
package ru.aleshin.features.editor.impl.domain.common

import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.domain.entites.EditModel
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domain.entities.template.Template

/**
 * @author Stanislav Aleshin on 17.05.2023.
 */
internal fun TimeTask.convertToEditModel(template: Template?, undefinedTaskId: Long?) = EditModel(
    key = key,
    date = date,
    startTime = timeRange.from,
    endTime = timeRange.to,
    createdAt = createdAt,
    mainCategory = category,
    subCategory = subCategory,
    isImportant = isImportant,
    isCompleted = isCompleted,
    isEnableNotification = isEnableNotification,
    taskNotifications = taskNotifications,
    isConsiderInStatistics = isConsiderInStatistics,
    repeatEnabled = template?.repeatEnabled ?: false,
    templateId = template?.templateId,
    undefinedTaskId = undefinedTaskId,
    repeatTimes = template?.repeatTimes ?: emptyList(),
    note = note,
)

internal fun EditModel.convertToTimeTask() = TimeTask(
    key = key,
    date = date,
    timeRange = TimeRange(startTime, endTime),
    createdAt = createdAt,
    category = mainCategory,
    subCategory = subCategory,
    isCompleted = isCompleted,
    isImportant = isImportant,
    isEnableNotification = isEnableNotification,
    taskNotifications = taskNotifications,
    isConsiderInStatistics = isConsiderInStatistics,
    note = note,
)
