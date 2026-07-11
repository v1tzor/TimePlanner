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
import ru.aleshin.core.data.datasources.categories.MainCategoryLocalDataSource
import ru.aleshin.core.data.mappers.categories.mapToData
import ru.aleshin.core.data.mappers.categories.mapToDomain
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.MainCategoryDetails
import ru.aleshin.core.domain.repository.MainCategoryRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
class MainCategoryRepositoryImpl @Inject constructor(
    private val localDataSource: MainCategoryLocalDataSource,
) : MainCategoryRepository {

    override suspend fun addOrUpdateCategory(category: MainCategory): Long {
        return localDataSource.addOrUpdateCategory(category.mapToData())
    }

    override suspend fun addOrUpdateCategories(categories: List<MainCategory>) {
        return localDataSource.addOrUpdateCategories(categories.map { it.mapToData() })
    }

    override suspend fun fetchAllCategoriesDetails(): Flow<List<MainCategoryDetails>> {
        return localDataSource.fetchAllCategoriesDetails().map { categories -> categories.map { it.mapToDomain() } }
    }

    override suspend fun fetchCategoryDetailsById(categoryId: Long): Flow<MainCategoryDetails?> {
        return localDataSource.fetchCategoryDetailsById(categoryId).map { category -> category?.mapToDomain() }
    }

    override suspend fun deleteCategoryById(categoryId: Long) {
        localDataSource.deleteCategoryById(categoryId)
    }

    override suspend fun deleteAllCategories() {
        localDataSource.deleteAllCategories()
    }
}
