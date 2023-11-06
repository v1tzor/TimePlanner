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
package ru.aleshin.features.editor.impl.presentation.models.tasks

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditModelUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditParameters
import java.util.Date

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Parcelize
internal data class UndefinedTaskUi(
    val id: Long = 0L,
    val createdAt: Date? = null,
    val deadline: Date? = null,
    val mainCategory: MainCategoryUi,
    val subCategory: SubCategoryUi? = null,
    val isImportant: Boolean = false,
    val note: String? = null,
) : Parcelable

internal fun UndefinedTaskUi.convertToEditModel(
    scheduleDate: Date,
    timeRange: TimeRange,
) = EditModelUi(
    date = scheduleDate,
    timeRange = timeRange,
    createdAt = createdAt,
    mainCategory = mainCategory,
    subCategory = subCategory,
    parameters = EditParameters(
        isImportant = isImportant,
    ),
    undefinedTaskId = id,
    note = note,
)
