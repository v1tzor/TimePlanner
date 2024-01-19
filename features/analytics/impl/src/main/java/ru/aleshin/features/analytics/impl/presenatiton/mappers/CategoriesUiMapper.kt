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
package ru.aleshin.features.analytics.impl.presenatiton.mappers

import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.features.analytics.impl.presenatiton.models.categories.MainCategoryUi
import ru.aleshin.features.analytics.impl.presenatiton.models.categories.SubCategoryUi

/**
 * @author Stanislav Aleshin on 30.07.2023.
 */
internal fun MainCategoryUi.mapToDomain() = MainCategory(
    id = id,
    customName = customName,
    default = defaultType,
)

internal fun MainCategory.mapToUi() = MainCategoryUi(
    id = id,
    customName = customName,
    defaultType = default,
)

internal fun SubCategoryUi.mapToDomain() = SubCategory(
    id = id,
    name = name,
    mainCategory = mainCategory.mapToDomain(),
    description = description,
)

internal fun SubCategory.mapToUi() = SubCategoryUi(
    id = id,
    name = name,
    mainCategory = mainCategory.mapToUi(),
    description = description,
)
