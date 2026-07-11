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
package ru.aleshin.core.domain.entities.template

import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.core.domain.entities.tasks.TaskPriority
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.extensions.changeDay
import ru.aleshin.core.utils.extensions.compareByHoursAndMinutes
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.Mapper
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Collections.emptyList
import java.util.Date

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Serializable
data class Template(
    val templateId: Long = 0,
    @Serializable(DateSerializer::class)
    val startTime: Date,
    @Serializable(DateSerializer::class)
    val endTime: Date,
    val category: MainCategory,
    val subCategory: SubCategory? = null,
    val priority: TaskPriority = TaskPriority.STANDARD,
    val isEnableNotification: Boolean = true,
    val isConsiderInStatistics: Boolean = true,
    val repeatEnabled: Boolean = false,
    val repeatTimes: List<RepeatTime> = emptyList(),
) {
    fun <T> map(mapper: Mapper<Template, T>) = mapper.map(this)

    fun checkDateIsRepeat(date: Date): Boolean {
        return repeatEnabled && repeatTimes.find { it.checkDateIsRepeat(date) } != null
    }

    fun equalsIsTemplate(timeTask: TimeTask) =
        startTime.compareByHoursAndMinutes(timeTask.timeRange.from) &&
            endTime.compareByHoursAndMinutes(timeTask.timeRange.to) &&
            category.id == timeTask.category.id &&
            subCategory == timeTask.subCategory &&
            priority == timeTask.priority &&
            isEnableNotification == timeTask.isEnableNotification &&
            isConsiderInStatistics == timeTask.isConsiderInStatistics
}

fun Template.convertToTimeTask(
    date: Date,
    key: Long = generateUniqueKey(),
    createdAt: Date? = Date(),
) = TimeTask(
    key = key,
    date = date,
    timeRange = TimeRange(
        from = startTime.changeDay(date),
        to = if (endTime.isCurrentDay(startTime)) endTime.changeDay(date) else endTime.changeDay(date.shiftDay(1)),
    ),
    createdAt = createdAt,
    category = category,
    linkedTemplateId = templateId,
    subCategory = subCategory,
    priority = priority,
    isEnableNotification = isEnableNotification,
    isConsiderInStatistics = isConsiderInStatistics,
)
