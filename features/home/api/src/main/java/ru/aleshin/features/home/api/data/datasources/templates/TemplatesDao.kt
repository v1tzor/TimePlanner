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
package ru.aleshin.features.home.api.data.datasources.templates

import androidx.room.*
import ru.aleshin.features.home.api.data.models.template.TemplateDetails
import ru.aleshin.features.home.api.data.models.template.TemplateEntity

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Dao
interface TemplatesDao {

    @Insert(entity = TemplateEntity::class)
    suspend fun addTemplate(template: TemplateEntity): Long

    @Transaction
    @Query("SELECT * FROM timeTaskTemplates")
    suspend fun fetchAllTemplates(): List<TemplateDetails>

    @Update
    suspend fun updateTemplate(template: TemplateEntity)

    @Query("DELETE FROM timeTaskTemplates WHERE id = :id")
    suspend fun deleteTemplate(id: Int)

    @Query("DELETE FROM timeTaskTemplates")
    fun deleteAllTemplates()
}
