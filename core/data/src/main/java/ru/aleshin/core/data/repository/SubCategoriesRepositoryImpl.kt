/*
 * Copyright 2026 Stanislav Aleshin
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
package ru.aleshin.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.aleshin.core.data.datasources.subcategories.SubCategoryLocalDataSource
import ru.aleshin.core.data.mappers.categories.mapToData
import ru.aleshin.core.data.mappers.categories.mapToDomain
import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.core.domain.repository.SubCategoryRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
class SubCategoriesRepositoryImpl @Inject constructor(
    private val localDataSource: SubCategoryLocalDataSource,
) : SubCategoryRepository {

    override suspend fun addOrUpdateSubCategory(category: SubCategory): Long {
        return localDataSource.addOrUpdateSubCategory(category.mapToData())
    }

    override suspend fun addOrUpdateSubCategories(categories: List<SubCategory>) {
        localDataSource.addOrUpdateSubCategories(categories.map { it.mapToData() })
    }

    override suspend fun fetchSubCategoriesByMain(mainCategoryId: Long): Flow<List<SubCategory>> {
        return localDataSource.fetchSubCategoriesByMain(mainCategoryId).map { categories ->
            categories.map { it.mapToDomain() }
        }
    }

    override suspend fun deleteSubCategoryById(categoryId: Long) {
        localDataSource.deleteSubCategoryById(categoryId)
    }

    override suspend fun deleteAllSubCategories() {
        localDataSource.deleteAllSubCategories()
    }
}
