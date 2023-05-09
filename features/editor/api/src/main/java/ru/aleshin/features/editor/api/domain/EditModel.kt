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
package ru.aleshin.features.editor.api.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.functional.Mapper
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.entities.template.Template
import java.util.*

/**
 * @author Stanislav Aleshin on 26.03.2023.
 */
@Parcelize
data class EditModel(
    val key: Long = 0L,
    val date: Date,
    val timeRanges: TimeRange,
    val duration: Long = duration(timeRanges.from, timeRanges.to),
    val mainCategory: MainCategory = MainCategory.absent(),
    val subCategory: SubCategory? = null,
    val isImportant: Boolean = false,
    val isEnableNotification: Boolean = true,
    val isConsiderInStatistics: Boolean = true,
    val templateId: Int? = null,
) : Parcelable {
    fun <T> map(mapper: Mapper<EditModel, T>) = mapper.map(this)
}

fun EditModel.convertToTemplate(id: Int = 0) = Template(
    templateId = id,
    startTime = timeRanges.from,
    endTime = timeRanges.to,
    category = mainCategory,
    subCategory = subCategory,
    isImportant = isImportant,
    isEnableNotification = isEnableNotification,
    isConsiderInStatistics = isConsiderInStatistics,
)
