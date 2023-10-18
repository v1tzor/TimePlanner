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
package ru.aleshin.features.home.api.data.datasources.templates

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.aleshin.features.home.api.data.models.template.TemplateCompound
import ru.aleshin.features.home.api.data.models.template.TemplateDetails
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
interface TemplatesLocalDataSource {

    suspend fun createTemplates(templates: List<TemplateCompound>): List<Long>
    suspend fun fetchTemplatesById(templateId: Int): TemplateDetails?
    fun fetchAllTemplates(): Flow<List<TemplateDetails>>
    suspend fun updateTemplate(template: TemplateCompound)
    suspend fun deleteTemplateById(id: Int)
    suspend fun deleteAllTemplates(): List<TemplateDetails>

    class Base @Inject constructor(
        private val templatesDao: TemplatesDao,
    ) : TemplatesLocalDataSource {

        override suspend fun createTemplates(templates: List<TemplateCompound>): List<Long> {
            return templatesDao.addOrUpdateCompoundTemplates(templates)
        }

        override fun fetchAllTemplates(): Flow<List<TemplateDetails>> {
            return templatesDao.fetchAllTemplates()
        }

        override suspend fun fetchTemplatesById(templateId: Int): TemplateDetails? {
            return templatesDao.fetchTemplateById(templateId)
        }

        override suspend fun updateTemplate(template: TemplateCompound) {
            templatesDao.addOrUpdateCompoundTemplates(listOf(template))
        }

        override suspend fun deleteTemplateById(id: Int) {
            templatesDao.deleteTemplate(id)
            templatesDao.deleteRepeatTimesByTemplates(listOf(id))
        }

        override suspend fun deleteAllTemplates(): List<TemplateDetails> {
            val deletableTemplates = fetchAllTemplates().first()
            templatesDao.deleteAllTemplates()
            templatesDao.deleteAllRepeatTimes()

            return deletableTemplates
        }
    }
}
