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
package ru.aleshin.core.data.models.tasks

import androidx.room.Embedded
import androidx.room.Relation
import ru.aleshin.core.data.models.categories.MainCategoryEntity
import ru.aleshin.core.data.models.categories.SubCategoryEntity

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
data class UndefinedTaskDetails(
    @Embedded
    val task: UndefinedTaskEntity,
    @Relation(
        parentColumn = "main_category_id",
        entityColumn = "id",
        entity = MainCategoryEntity::class,
    )
    val mainCategory: MainCategoryEntity,
    @Relation(
        parentColumn = "sub_category_id",
        entityColumn = "id",
        entity = SubCategoryEntity::class,
    )
    val subCategory: SubCategoryEntity?,
)
