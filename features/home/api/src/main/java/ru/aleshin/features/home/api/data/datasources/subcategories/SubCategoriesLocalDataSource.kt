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
package ru.aleshin.features.home.api.data.datasources.subcategories

import ru.aleshin.features.home.api.data.models.categories.SubCategoryEntity
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
interface SubCategoriesLocalDataSource {

    suspend fun addSubCategory(subCategory: SubCategoryEntity)
    suspend fun fetchSubCategoriesByType(type: MainCategory): List<SubCategoryEntity>
    suspend fun updateSubCategory(subCategory: SubCategoryEntity)
    suspend fun removeSubCategory(id: Int)
    suspend fun removeAllSubCategories()

    class Base @Inject constructor(
        private val subCategoriesDao: SubCategoriesDao,
    ) : SubCategoriesLocalDataSource {

        override suspend fun addSubCategory(subCategory: SubCategoryEntity) {
            subCategoriesDao.addSubCategory(subCategory)
        }

        override suspend fun fetchSubCategoriesByType(type: MainCategory): List<SubCategoryEntity> {
            return subCategoriesDao.fetchSubCategoriesByTypeId(type.id)
        }

        override suspend fun updateSubCategory(subCategory: SubCategoryEntity) {
            subCategoriesDao.updateSubCategory(subCategory)
        }

        override suspend fun removeSubCategory(id: Int) {
            subCategoriesDao.removeSubCategory(id)
        }

        override suspend fun removeAllSubCategories() {
            subCategoriesDao.removeAllSubCategories()
        }
    }
}
