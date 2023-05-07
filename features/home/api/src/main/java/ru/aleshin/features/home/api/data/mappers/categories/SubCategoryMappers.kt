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
package ru.aleshin.features.home.api.data.mappers.categories

import ru.aleshin.features.home.api.data.models.categories.SubCategoryEntity
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
fun SubCategoryEntity.mapToDomain(mainCategory: MainCategory) = SubCategory(
    id = id,
    name = subCategoryName,
    mainCategory = mainCategory,
    description = description,
)

fun SubCategory.mapToData() = SubCategoryEntity(
    id = id,
    subCategoryName = name,
    mainCategoryId = mainCategory.id,
    description = description,
)
