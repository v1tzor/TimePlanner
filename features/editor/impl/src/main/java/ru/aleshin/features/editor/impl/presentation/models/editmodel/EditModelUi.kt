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
package ru.aleshin.features.editor.impl.presentation.models.editmodel

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.categories.SubCategoryUi
import java.util.Date

/**
 * @author Stanislav Aleshin on 16.05.2023.
 */
@Immutable
@Serializable
internal data class EditModelUi(
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
    val parameters: EditParametersUi = EditParametersUi(),
    val repeatEnabled: Boolean = false,
    val templateId: Int? = null,
    val undefinedTaskId: Long? = null,
    val repeatTimes: List<RepeatTime> = emptyList(),
    val note: String? = null,
) {
    fun checkDateIsRepeat(): Boolean {
        return repeatEnabled && repeatTimes.find { it.checkDateIsRepeat(date) } != null
    }
}
