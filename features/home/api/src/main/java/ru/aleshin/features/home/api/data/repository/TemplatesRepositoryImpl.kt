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
package ru.aleshin.features.home.api.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.aleshin.features.home.api.data.datasources.templates.TemplatesLocalDataSource
import ru.aleshin.features.home.api.data.mappers.template.mapToData
import ru.aleshin.features.home.api.data.mappers.template.mapToDomain
import ru.aleshin.features.home.api.domain.entities.template.Template
import ru.aleshin.features.home.api.domain.repository.TemplatesRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
class TemplatesRepositoryImpl @Inject constructor(
    private val localDataSource: TemplatesLocalDataSource,
) : TemplatesRepository {

    override suspend fun addTemplate(template: Template): Int {
        return localDataSource.createTemplates(listOf(template.mapToData()))[0].toInt()
    }

    override suspend fun addTemplates(templates: List<Template>) {
        localDataSource.createTemplates(templates.map { it.mapToData() })
    }

    override suspend fun fetchTemplatesById(templateId: Int): Template? {
        return localDataSource.fetchTemplatesById(templateId)?.mapToDomain()
    }

    override fun fetchAllTemplates(): Flow<List<Template>> {
        return localDataSource.fetchAllTemplates().map { templates ->
            templates.map { template -> template.mapToDomain() }
        }
    }

    override suspend fun updateTemplate(template: Template) {
        localDataSource.updateTemplate(template.mapToData())
    }

    override suspend fun deleteTemplateById(id: Int) {
        localDataSource.deleteTemplateById(id)
    }

    override suspend fun deleteAllTemplates(): List<Template> {
        return localDataSource.deleteAllTemplates().map { it.mapToDomain() }
    }
}
