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
package ru.aleshin.features.home.api.domain.entities.schedules

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.features.home.api.domain.entities.categories.MainCategory
import ru.aleshin.features.home.api.domain.entities.categories.SubCategory
import java.util.Date

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Serializable
data class UndefinedTask(
    val id: Long = 0L,
    @Serializable(DateSerializer::class) val createdAt: Date? = null,
    @Serializable(DateSerializer::class) val deadline: Date? = null,
    val mainCategory: MainCategory,
    val subCategory: SubCategory? = null,
    val isImportant: Boolean = false,
    val note: String? = null,
)
