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

package ru.aleshin.core.data.datasources.categories

import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.categories.MainCategoryDetailsEntity
import ru.aleshin.core.data.models.categories.MainCategoryEntity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
interface MainCategoryLocalDataSource {

    suspend fun addOrUpdateCategory(category: MainCategoryEntity): Long
    suspend fun addOrUpdateCategories(categories: List<MainCategoryEntity>)
    suspend fun fetchAllCategoriesDetails(): Flow<List<MainCategoryDetailsEntity>>
    suspend fun fetchCategoryDetailsById(categoryId: Long): Flow<MainCategoryDetailsEntity?>
    suspend fun deleteCategoryById(categoryId: Long)
    suspend fun deleteAllCategories()

    class Base @Inject constructor(
        private val mainCategoryDao: MainCategoryDao,
    ) : MainCategoryLocalDataSource {

        override suspend fun addOrUpdateCategory(category: MainCategoryEntity): Long {
            return mainCategoryDao.addOrUpdateCategory(category)
        }

        override suspend fun addOrUpdateCategories(categories: List<MainCategoryEntity>) {
            mainCategoryDao.addOrUpdateCategories(categories)
        }

        override suspend fun fetchAllCategoriesDetails(): Flow<List<MainCategoryDetailsEntity>> {
            return mainCategoryDao.fetchAllCategoriesDetails()
        }

        override suspend fun fetchCategoryDetailsById(categoryId: Long): Flow<MainCategoryDetailsEntity?> {
            return mainCategoryDao.fetchCategoryDetailsById(categoryId)
        }

        override suspend fun deleteCategoryById(categoryId: Long) {
            mainCategoryDao.deleteCategoryById(categoryId)
        }

        override suspend fun deleteAllCategories() {
            mainCategoryDao.deleteAllCategories()
        }
    }
}
