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

import ru.aleshin.features.home.api.data.models.categories.MainCategoryDetails
import ru.aleshin.features.home.api.data.models.categories.MainCategoryEntity
import ru.aleshin.features.home.api.data.models.categories.SubCategoryEntity
import ru.aleshin.features.home.api.domain.entities.categories.Categories
import ru.aleshin.features.home.api.domain.entities.categories.MainCategory
import ru.aleshin.features.home.api.domain.entities.categories.SubCategory

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
fun MainCategoryDetails.mapToDomain() = Categories(
    category = mainCategory.mapToDomain(),
    subCategories = subCategories.map { subCategory ->
        subCategory.mapToDomain(mainCategory.mapToDomain())
    },
)

fun MainCategoryEntity.mapToDomain() = MainCategory(
    id = id,
    customName = customName,
    default = defaultType,
)

fun MainCategory.mapToData() = MainCategoryEntity(
    id = id,
    customName = customName,
    defaultType = default,
)

fun SubCategoryEntity.mapToDomain(mainCategory: MainCategory) = SubCategory(
    id = id,
    mainCategory = mainCategory,
    name = subCategoryName.ifEmpty { null },
    description = description,
)

fun SubCategory.mapToData() = SubCategoryEntity(
    id = id,
    mainCategoryId = mainCategory.id,
    subCategoryName = name ?: "",
    description = description,
)
