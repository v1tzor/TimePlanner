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
package ru.aleshin.features.home.impl.domain.interactors

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.domain.entities.TemplatesSortedType
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal interface TemplatesInteractor {

    suspend fun addOrUpdateTemplate(template: Template): DomainResult<HomeFailures, Long>
    suspend fun fetchTemplates(sortedType: TemplatesSortedType): FlowDomainResult<HomeFailures, List<Template>>
    suspend fun checkIsTemplate(timeTask: TimeTask): DomainResult<HomeFailures, Template?>
    suspend fun deleteTemplate(id: Long): DomainResult<HomeFailures, Unit>

    class Base @Inject constructor(
        private val templatesRepository: TemplatesRepository,
        private val eitherWrapper: HomeEitherWrapper,
    ) : TemplatesInteractor {

        override suspend fun addOrUpdateTemplate(template: Template) = eitherWrapper.wrap {
            templatesRepository.addOrUpdateTemplate(template)
        }

        override suspend fun fetchTemplates(sortedType: TemplatesSortedType) = eitherWrapper.wrapFlow {
            templatesRepository.fetchAllTemplates().map { templates ->
                when (sortedType) {
                    TemplatesSortedType.DATE -> templates.sortedBy { it.startTime }
                    TemplatesSortedType.CATEGORIES -> templates.sortedBy { it.category.id }
                    TemplatesSortedType.DURATION -> templates.sortedBy { duration(it.startTime, it.endTime) }
                }
            }
        }

        override suspend fun checkIsTemplate(timeTask: TimeTask) = eitherWrapper.wrap {
            val templates = templatesRepository.fetchAllTemplates().first()
            templates.find { template -> template.templateId == timeTask.linkedTemplateId }
        }

        override suspend fun deleteTemplate(id: Long) = eitherWrapper.wrap {
            templatesRepository.deleteTemplateById(id)
        }
    }
}
