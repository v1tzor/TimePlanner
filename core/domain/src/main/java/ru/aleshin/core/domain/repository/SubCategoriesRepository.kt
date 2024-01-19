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
package ru.aleshin.core.domain.repository

import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.SubCategory

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
interface SubCategoriesRepository {
    suspend fun addSubCategories(categories: List<SubCategory>)
    suspend fun fetchSubCategories(type: MainCategory): List<SubCategory>
    suspend fun updateSubCategory(category: SubCategory)
    suspend fun deleteSubCategory(category: SubCategory)
    suspend fun deleteAllSubCategories()
}
