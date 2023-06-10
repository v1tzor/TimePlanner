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
package ru.aleshin.features.home.api.data.repository

import ru.aleshin.features.home.api.data.datasources.categories.CategoriesLocalDataSource
import ru.aleshin.features.home.api.data.mappers.categories.mapToData
import ru.aleshin.features.home.api.data.mappers.categories.mapToDomain
import ru.aleshin.features.home.api.domains.entities.categories.Categories
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.repository.CategoriesRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
class CategoriesRepositoryImpl @Inject constructor(
    private val localDataSource: CategoriesLocalDataSource,
) : CategoriesRepository {

    override suspend fun addMainCategories(categories: List<MainCategory>) {
        localDataSource.addMainCategories(categories.map { it.mapToData() })
    }

    override suspend fun fetchCategories(): List<Categories> {
        return localDataSource.fetchMainCategories().map { mainCategory ->
            mainCategory.mapToDomain()
        }
    }

    override suspend fun fetchCategoriesByType(mainCategory: MainCategory): Categories? {
        return localDataSource.fetchCategoriesByType(mainCategory)?.mapToDomain()
    }

    override suspend fun updateMainCategory(category: MainCategory) {
        localDataSource.updateMainCategory(category.mapToData())
    }

    override suspend fun deleteMainCategory(category: MainCategory) {
        localDataSource.removeMainCategory(category.id)
    }

    override suspend fun deleteAllCategories() {
        localDataSource.removeAllCategories()
    }
}
