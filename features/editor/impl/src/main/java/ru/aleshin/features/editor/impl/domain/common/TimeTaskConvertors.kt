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
package ru.aleshin.features.editor.impl.domain.common

import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.domain.entites.EditModel
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask

/**
 * @author Stanislav Aleshin on 17.05.2023.
 */
internal fun TimeTask.convertToEditModel(templateId: Int?) = EditModel(
    key = key,
    date = date,
    startTime = timeRanges.from,
    endTime = timeRanges.to,
    mainCategory = category,
    subCategory = subCategory,
    isImportant = isImportant,
    isCompleted = isCompleted,
    isEnableNotification = isEnableNotification,
    isConsiderInStatistics = isConsiderInStatistics,
    templateId = templateId,
)

internal fun EditModel.convertToTimeTask() = TimeTask(
    key = key,
    date = date,
    timeRanges = TimeRange(startTime, endTime),
    category = mainCategory,
    subCategory = subCategory,
    isCompleted = isCompleted,
    isImportant = isImportant,
    isEnableNotification = isEnableNotification,
    isConsiderInStatistics = isConsiderInStatistics,
)
