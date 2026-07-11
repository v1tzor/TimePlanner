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
package ru.aleshin.features.editor.impl.presentation.models.tasks

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.core.utils.extensions.changeDay
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 16.05.2023.
 */
@Immutable
@Serializable
internal data class TimeTaskEditUi(
    val key: Long = 0L,
    @Serializable(DateSerializer::class)
    val date: Date,
    val timeRange: TimeRange,
    @Serializable(DateSerializer::class)
    val createdAt: Date? = null,
    val duration: Long = duration(timeRange.from, timeRange.to),
    val mainCategory: MainCategoryUi = MainCategoryUi(),
    val subCategory: SubCategoryUi? = null,
    val isCompleted: Boolean = true,
    val parameters: TimeTaskEditParametersUi = TimeTaskEditParametersUi(),
    val linkedTemplateId: Long? = null,
    val undefinedTaskId: Long? = null,
    val note: String? = null,
) {
    companion object {
        fun create(
            createdDate: Date,
            scheduleDate: Date,
            timeRange: TimeRange,
        ): TimeTaskEditUi {
            return TimeTaskEditUi(
                date = scheduleDate,
                timeRange = timeRange,
                createdAt = createdDate,
            )
        }
    }
}

internal fun TimeTaskUi.convertToEditModel() = TimeTaskEditUi(
    key = key,
    date = date,
    timeRange = timeRanges,
    createdAt = createdAt,
    mainCategory = category,
    subCategory = subCategory,
    isCompleted = isCompleted,
    parameters = TimeTaskEditParametersUi(
        priority = priority,
        isEnableNotification = isEnableNotification,
        taskNotifications = taskNotifications,
        isConsiderInStatistics = isConsiderInStatistics,
    ),
    linkedTemplateId = linkedTemplateId,
    undefinedTaskId = null,
    note = note,
)

internal fun UndefinedTaskUi.convertToEditModel(
    createdDate: Date,
    scheduleDate: Date,
    timeRange: TimeRange,
) = TimeTaskEditUi(
    date = scheduleDate,
    timeRange = timeRange,
    createdAt = createdDate,
    mainCategory = mainCategory,
    subCategory = subCategory,
    parameters = TimeTaskEditParametersUi(
        priority = priority,
    ),
    undefinedTaskId = id,
    note = note,
)

internal fun TemplateUi.convertToEditModel(
    scheduleDate: Date,
    createdDate: Date
) = TimeTaskEditUi(
    date = scheduleDate,
    createdAt = createdDate,
    timeRange = TimeRange(
        from = startTime.changeDay(scheduleDate),
        to = if (endTime.isCurrentDay(startTime)) {
            endTime.changeDay(scheduleDate)
        } else {
            endTime.changeDay(scheduleDate.shiftDay(1))
        },
    ),
    mainCategory = category,
    subCategory = subCategory,
    isCompleted = true,
    parameters = TimeTaskEditParametersUi(
        priority = priority,
        isEnableNotification = isEnableNotification,
        isConsiderInStatistics = isConsiderInStatistics,
    ),
    linkedTemplateId = templateId,
    undefinedTaskId = null,
)

internal fun TimeTaskEditUi.convertToTimeTask() = TimeTaskUi(
    key = key,
    date = date,
    createdAt = createdAt,
    timeRanges = timeRange,
    category = mainCategory,
    subCategory = subCategory,
    linkedTemplateId = linkedTemplateId,
    isCompleted = isCompleted,
    priority = parameters.priority,
    isEnableNotification = parameters.isEnableNotification,
    taskNotifications = parameters.taskNotifications,
    isConsiderInStatistics = parameters.isConsiderInStatistics,
    note = note,
)

internal fun TimeTaskEditUi.convertToTemplate(
    templateId: Long = 0L
) = TemplateUi(
    templateId = templateId,
    startTime = timeRange.from,
    endTime = timeRange.to,
    category = mainCategory,
    subCategory = subCategory,
    priority = parameters.priority,
    isEnableNotification = parameters.isEnableNotification,
    isConsiderInStatistics = parameters.isConsiderInStatistics,
    repeatEnabled = false,
    repeatTimes = emptyList(),
)