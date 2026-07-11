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
package ru.aleshin.core.data.datasources.templates

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.template.RepeatTimeEntity
import ru.aleshin.core.data.models.template.TemplateCompoundEntity
import ru.aleshin.core.data.models.template.TemplateDetailsEntity
import ru.aleshin.core.data.models.template.TemplateEntity

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Dao
interface TemplateDao {

    @Transaction
    suspend fun addOrUpdateTemplateCompound(template: TemplateCompoundEntity): Long {
        val savedTemplateId = addOrUpdateTemplate(template.template)
        val templateId = if (template.template.id == 0L) savedTemplateId else template.template.id

        deleteRepeatTimesByTemplates(listOf(templateId))
        addOrUpdateRepeatTimes(template.repeatTimes.map { it.copy(templateId = templateId) })

        return templateId
    }

    @Transaction
    suspend fun addOrUpdateTemplatesCompound(templates: List<TemplateCompoundEntity>) {
        templates.forEach { template ->
            addOrUpdateTemplateCompound(template)
        }
    }

    @Upsert
    suspend fun addOrUpdateTemplate(template: TemplateEntity): Long

    @Upsert
    suspend fun addOrUpdateTemplates(templates: List<TemplateEntity>)

    @Upsert
    suspend fun addOrUpdateRepeatTimes(repeatTimes: List<RepeatTimeEntity>)

    @Transaction
    @Query("SELECT * FROM timeTaskTemplates")
    fun fetchAllTemplatesDetails(): Flow<List<TemplateDetailsEntity>>
    
    @Transaction
    @Query("SELECT * FROM timeTaskTemplates WHERE id = :templateId")
    suspend fun fetchTemplateDetailsById(templateId: Long): TemplateDetailsEntity?

    @Query("DELETE FROM timeTaskTemplates WHERE id = :id")
    suspend fun deleteTemplateById(id: Long)

    @Query("DELETE FROM timeTaskTemplates")
    suspend fun deleteAllTemplates()

    @Query("DELETE FROM repeatTimes WHERE template_id IN (:templatesId)")
    suspend fun deleteRepeatTimesByTemplates(templatesId: List<Long>)
}
