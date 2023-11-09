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
package ru.aleshin.features.home.api.domain.entities.template

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.extensions.compareByHoursAndMinutes
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.Mapper
import ru.aleshin.features.home.api.domain.entities.categories.MainCategory
import ru.aleshin.features.home.api.domain.entities.categories.SubCategory
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import java.util.*

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Serializable
data class Template(
    val templateId: Int,
    @Serializable(DateSerializer::class)
    val startTime: Date,
    @Serializable(DateSerializer::class)
    val endTime: Date,
    val category: MainCategory,
    val subCategory: SubCategory? = null,
    val isImportant: Boolean = false,
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
        isImportant == timeTask.isImportant &&
        isEnableNotification == timeTask.isEnableNotification &&
        isConsiderInStatistics == timeTask.isConsiderInStatistics
}
