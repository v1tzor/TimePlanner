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
package ru.aleshin.features.analytics.impl.presenatiton.mappers

import ru.aleshin.features.analytics.impl.presenatiton.models.timetask.TimeTaskUi
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask

internal fun TimeTask.mapToUi() = TimeTaskUi(
    key = key,
    date = date,
    timeRanges = timeRange,
    category = category.mapToUi(),
    subCategory = subCategory?.mapToUi(),
    isCompleted = isCompleted,
    isImportant = isImportant,
    isEnableNotification = isEnableNotification,
    isConsiderInStatistics = isConsiderInStatistics,
    note = note,
)
