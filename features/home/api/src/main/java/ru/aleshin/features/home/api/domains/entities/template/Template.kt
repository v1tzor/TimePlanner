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
package ru.aleshin.features.home.api.domains.entities.template

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.extensions.compareByHoursAndMinutes
import ru.aleshin.core.utils.functional.Mapper
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
import java.util.*

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Parcelize
data class Template(
    val templateId: Int,
    val startTime: Date,
    val endTime: Date,
    val category: MainCategory,
    val subCategory: SubCategory? = null,
    val isImportant: Boolean = false,
    val isEnableNotification: Boolean = true,
    val isConsiderInStatistics: Boolean = true,
) : Parcelable {
    fun <T> map(mapper: Mapper<Template, T>) = mapper.map(this)
}

fun Template.equalsIsTemplate(
    timeTask: TimeTask,
) = startTime.compareByHoursAndMinutes(timeTask.timeRanges.from) &&
    endTime.compareByHoursAndMinutes(timeTask.timeRanges.to) &&
    category == timeTask.category &&
    subCategory == timeTask.subCategory &&
    isImportant == timeTask.isImportant &&
    isEnableNotification == timeTask.isEnableNotification &&
    isConsiderInStatistics == timeTask.isConsiderInStatistics
