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

import androidx.room.*
import ru.aleshin.features.home.api.data.models.categories.SubCategoryEntity

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
@Dao
interface SubCategoriesDao {

    @Query("SELECT * FROM subCategories")
    suspend fun fetchAllSubCategories(): List<SubCategoryEntity>

    @Query("SELECT * FROM subCategories WHERE main_category_id = :id")
    suspend fun fetchSubCategoriesByTypeId(id: Int): List<SubCategoryEntity>

    @Insert
    suspend fun addSubCategory(entity: SubCategoryEntity)

    @Query("DELETE FROM subCategories WHERE id = :id")
    suspend fun removeSubCategory(id: Int)

    @Query("DELETE FROM subCategories")
    suspend fun removeAllSubCategories()

    @Update
    suspend fun updateSubCategory(entity: SubCategoryEntity)
}
