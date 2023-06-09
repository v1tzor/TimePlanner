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

import ru.aleshin.features.home.api.data.datasources.subcategories.SubCategoriesLocalDataSource
import ru.aleshin.features.home.api.data.mappers.categories.mapToData
import ru.aleshin.features.home.api.data.mappers.categories.mapToDomain
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.repository.SubCategoriesRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
class SubCategoriesRepositoryImpl @Inject constructor(
    private val localDataSource: SubCategoriesLocalDataSource,
) : SubCategoriesRepository {

    override suspend fun addSubCategory(category: SubCategory) {
        localDataSource.addSubCategory(category.mapToData())
    }

    override suspend fun fetchSubCategories(type: MainCategory): List<SubCategory> {
        return localDataSource.fetchSubCategoriesByType(type).map { subCategory ->
            subCategory.mapToDomain(type)
        }
    }

    override suspend fun updateSubCategory(category: SubCategory) {
        localDataSource.updateSubCategory(category.mapToData())
    }

    override suspend fun deleteSubCategory(category: SubCategory) {
        localDataSource.removeSubCategory(category.id)
    }

    override suspend fun deleteAllSubCategories() {
        localDataSource.removeAllSubCategories()
    }
}
