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
package ru.aleshin.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.aleshin.core.data.datasources.templates.TemplateLocalDataSource
import ru.aleshin.core.data.mappers.template.mapToData
import ru.aleshin.core.data.mappers.template.mapToDomain
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.repository.TemplatesRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
class TemplatesRepositoryImpl @Inject constructor(
    private val localDataSource: TemplateLocalDataSource,
) : TemplatesRepository {

    override suspend fun addOrUpdateTemplate(template: Template): Long {
        return localDataSource.addOrUpdateTemplate(template.mapToData())
    }

    override suspend fun addOrUpdateTemplates(templates: List<Template>) {
        localDataSource.addOrUpdateTemplates(templates.map { it.mapToData() })
    }

    override suspend fun fetchTemplatesByIdOnce(templateId: Long): Template? {
        return localDataSource.fetchTemplatesById(templateId)?.mapToDomain()
    }

    override suspend fun fetchAllTemplates(): Flow<List<Template>> {
        return localDataSource.fetchAllTemplates().map { templates ->
            templates.map { it.mapToDomain() }
        }
    }

    override suspend fun deleteTemplateById(id: Long) {
        localDataSource.deleteTemplateById(id)
    }

    override suspend fun deleteAllTemplates(): List<Template> {
        return localDataSource.deleteAllTemplates().map { it.mapToDomain() }
    }
}
