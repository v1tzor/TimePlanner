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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.aleshin.core.data.models.template.TemplateCompoundEntity
import ru.aleshin.core.data.models.template.TemplateDetailsEntity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
interface TemplateLocalDataSource {

    suspend fun addOrUpdateTemplate(template: TemplateCompoundEntity): Long
    suspend fun addOrUpdateTemplates(templates: List<TemplateCompoundEntity>)
    suspend fun fetchTemplatesById(templateId: Long): TemplateDetailsEntity?
    suspend fun fetchAllTemplates(): Flow<List<TemplateDetailsEntity>>
    suspend fun deleteTemplateById(id: Long)
    suspend fun deleteAllTemplates(): List<TemplateDetailsEntity>

    class Base @Inject constructor(
        private val templatesDao: TemplateDao,
    ) : TemplateLocalDataSource {

        override suspend fun addOrUpdateTemplate(template: TemplateCompoundEntity): Long {
            return templatesDao.addOrUpdateTemplateCompound(template)
        }

        override suspend fun addOrUpdateTemplates(templates: List<TemplateCompoundEntity>) {
            templatesDao.addOrUpdateTemplatesCompound(templates)
        }

        override suspend fun fetchTemplatesById(templateId: Long): TemplateDetailsEntity? {
            return templatesDao.fetchTemplateDetailsById(templateId)
        }

        override suspend fun fetchAllTemplates(): Flow<List<TemplateDetailsEntity>> {
            return templatesDao.fetchAllTemplatesDetails()
        }

        override suspend fun deleteTemplateById(id: Long) {
            templatesDao.deleteTemplateById(id)
        }

        override suspend fun deleteAllTemplates(): List<TemplateDetailsEntity> {
            val deletableTemplates = fetchAllTemplates().first()
            templatesDao.deleteAllTemplates()

            return deletableTemplates
        }
    }
}
