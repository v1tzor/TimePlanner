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
package ru.aleshin.core.data.datasources.subcategories

import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.categories.SubCategoryEntity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
interface SubCategoryLocalDataSource {

    suspend fun addOrUpdateSubCategory(category: SubCategoryEntity): Long
    suspend fun addOrUpdateSubCategories(categories: List<SubCategoryEntity>)
    suspend fun fetchSubCategoriesByMain(mainCategoryId: Long): Flow<List<SubCategoryEntity>>
    suspend fun deleteSubCategoryById(categoryId: Long)
    suspend fun deleteAllSubCategories()

    class Base @Inject constructor(
        private val subCategoryDao: SubCategoryDao,
    ) : SubCategoryLocalDataSource {

        override suspend fun addOrUpdateSubCategory(category: SubCategoryEntity): Long {
            return subCategoryDao.addOrUpdateSubCategory(category)
        }

        override suspend fun addOrUpdateSubCategories(categories: List<SubCategoryEntity>) {
            subCategoryDao.addOrUpdateSubCategories(categories)
        }

        override suspend fun fetchSubCategoriesByMain(mainCategoryId: Long): Flow<List<SubCategoryEntity>> {
            return subCategoryDao.fetchSubCategoriesByMain(mainCategoryId)
        }

        override suspend fun deleteSubCategoryById(categoryId: Long) {
            subCategoryDao.deleteSubCategory(categoryId)
        }

        override suspend fun deleteAllSubCategories() {
            subCategoryDao.deleteAllSubCategories()
        }
    }
}
