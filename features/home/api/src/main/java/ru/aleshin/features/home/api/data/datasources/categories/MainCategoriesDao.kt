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

package ru.aleshin.features.home.api.data.datasources.categories

import androidx.room.*
import ru.aleshin.features.home.api.data.models.categories.MainCategoryDetails
import ru.aleshin.features.home.api.data.models.categories.MainCategoryEntity

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
@Dao
interface MainCategoriesDao {

    @Transaction
    @Query("SELECT * FROM mainCategories")
    suspend fun fetchAllCategories(): List<MainCategoryDetails>

    @Transaction
    @Query("SELECT * FROM mainCategories WHERE id = :id")
    suspend fun fetchCategoriesById(id: Int): MainCategoryDetails?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategory(entity: MainCategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCategories(entities: List<MainCategoryEntity>)

    @Query("DELETE FROM mainCategories WHERE id = :id")
    suspend fun removeCategory(id: Int)

    @Query("DELETE FROM mainCategories WHERE id > 12")
    suspend fun removeAllCategories()

    @Update
    suspend fun updateCategory(entity: MainCategoryEntity)
}
