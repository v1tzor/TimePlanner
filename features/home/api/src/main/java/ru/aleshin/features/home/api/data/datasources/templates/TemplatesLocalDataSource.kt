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

import ru.aleshin.features.home.api.data.models.template.TemplateDetails
import ru.aleshin.features.home.api.data.models.template.TemplateEntity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
interface TemplatesLocalDataSource {

    suspend fun createTemplate(template: TemplateEntity): Long
    suspend fun fetchAllTemplates(): List<TemplateDetails>
    suspend fun updateTemplate(template: TemplateEntity)
    suspend fun deleteTemplateById(id: Int)

    class Base @Inject constructor(
        private val templatesDao: TemplatesDao,
    ) : TemplatesLocalDataSource {

        override suspend fun createTemplate(template: TemplateEntity): Long {
            return templatesDao.addTemplate(template)
        }

        override suspend fun fetchAllTemplates(): List<TemplateDetails> {
            return templatesDao.fetchAllTemplates()
        }

        override suspend fun updateTemplate(template: TemplateEntity) {
            templatesDao.updateTemplate(template)
        }

        override suspend fun deleteTemplateById(id: Int) {
            templatesDao.deleteTemplate(id)
        }
    }
}
