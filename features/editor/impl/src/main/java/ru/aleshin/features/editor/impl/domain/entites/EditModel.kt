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
package ru.aleshin.features.editor.impl.domain.entites

import ru.aleshin.core.utils.functional.Mapper
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import java.util.*

/**
 * @author Stanislav Aleshin on 26.03.2023.
 */
data class EditModel(
    val key: Long = 0L,
    val date: Date,
    val startTime: Date,
    val endTime: Date,
    val mainCategory: MainCategory = MainCategory.absent(),
    val subCategory: SubCategory? = null,
    val isCompleted: Boolean = true,
    val isImportant: Boolean = false,
    val isEnableNotification: Boolean = true,
    val isConsiderInStatistics: Boolean = true,
    val templateId: Int? = null,
) {
    fun <T> map(mapper: Mapper<EditModel, T>) = mapper.map(this)
}
