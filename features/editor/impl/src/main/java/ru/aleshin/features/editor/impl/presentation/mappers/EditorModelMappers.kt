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
package ru.aleshin.features.editor.impl.presentation.mappers

import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.domain.entites.EditModel
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditModelUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditParameters

/**
 * @author Stanislav Aleshin on 16.05.2023.
 */
internal fun EditModel.mapToUi() = EditModelUi(
    key = key,
    date = date,
    timeRange = TimeRange(startTime, endTime),
    createdAt = createdAt,
    duration = duration(startTime, endTime),
    mainCategory = mainCategory.mapToUi(),
    subCategory = subCategory?.mapToUi(),
    parameters = EditParameters(isImportant, isEnableNotification, isConsiderInStatistics),
    isCompleted = isCompleted,
    repeatEnabled = repeatEnabled,
    templateId = templateId,
    undefinedTaskId = undefinedTaskId,
    repeatTimes = repeatTimes,
    note = note,
)

internal fun EditModelUi.mapToDomain() = EditModel(
    key = key,
    date = date,
    startTime = timeRange.from,
    endTime = timeRange.to,
    createdAt = createdAt,
    mainCategory = mainCategory.mapToDomain(),
    subCategory = subCategory?.mapToDomain(),
    isCompleted = isCompleted,
    isImportant = parameters.isImportant,
    isEnableNotification = parameters.isEnableNotification,
    isConsiderInStatistics = parameters.isConsiderInStatistics,
    repeatEnabled = repeatEnabled,
    templateId = templateId,
    undefinedTaskId = undefinedTaskId,
    repeatTimes = repeatTimes,
    note = note,
)
