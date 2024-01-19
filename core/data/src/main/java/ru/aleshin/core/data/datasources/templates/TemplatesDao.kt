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
 * limitations under the License.
 */
package ru.aleshin.core.data.datasources.templates

import androidx.room.* // ktlint-disable import-ordering
import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.data.models.template.RepeatTimeEntity
import ru.aleshin.core.data.models.template.TemplateCompound
import ru.aleshin.core.data.models.template.TemplateDetails
import ru.aleshin.core.data.models.template.TemplateEntity
import ru.aleshin.core.data.models.template.allRepeatTimes
import ru.aleshin.core.data.models.template.allTemplatesId

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Dao
interface TemplatesDao {

    @Insert(entity = TemplateEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrUpdateTemplates(templates: List<TemplateEntity>): List<Long>

    @Insert(entity = RepeatTimeEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrUpdateRepeatTimes(repeatTimes: List<RepeatTimeEntity>): List<Long>

    @Transaction
    suspend fun addOrUpdateCompoundTemplates(templates: List<TemplateCompound>): List<Long> {
        deleteRepeatTimesByTemplates(templates.allTemplatesId())
        addOrUpdateRepeatTimes(templates.allRepeatTimes())
        return addOrUpdateTemplates(templates.map { it.template })
    }

    @Transaction
    @Query("SELECT * FROM timeTaskTemplates")
    fun fetchAllTemplates(): Flow<List<TemplateDetails>>
    
    @Transaction
    @Query("SELECT * FROM timeTaskTemplates WHERE id = :templateId")
    fun fetchTemplateById(templateId: Int): TemplateDetails?

    @Query("DELETE FROM timeTaskTemplates WHERE id = :id")
    suspend fun deleteTemplate(id: Int)

    @Query("DELETE FROM timeTaskTemplates")
    suspend fun deleteAllTemplates()

    @Query("DELETE FROM repeatTimes WHERE template_id IN (:templatesId)")
    suspend fun deleteRepeatTimesByTemplates(templatesId: List<Int>)

    @Query("DELETE FROM repeatTimes")
    suspend fun deleteAllRepeatTimes()
}
