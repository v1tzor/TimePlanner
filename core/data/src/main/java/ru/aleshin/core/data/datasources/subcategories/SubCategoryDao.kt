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

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.categories.SubCategoryEntity

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
@Dao
interface SubCategoryDao {

    @Upsert
    suspend fun addOrUpdateSubCategory(category: SubCategoryEntity): Long

    @Upsert
    suspend fun addOrUpdateSubCategories(categories: List<SubCategoryEntity>)

    @Query("SELECT * FROM subCategories")
    suspend fun fetchAllSubCategories(): List<SubCategoryEntity>

    @Query("SELECT * FROM subCategories WHERE main_category_id = :mainCategoryId")
    fun fetchSubCategoriesByMain(mainCategoryId: Long): Flow<List<SubCategoryEntity>>

    @Query("DELETE FROM subCategories WHERE id = :categoryId")
    suspend fun deleteSubCategory(categoryId: Long)

    @Query("DELETE FROM subCategories")
    suspend fun deleteAllSubCategories()
}
