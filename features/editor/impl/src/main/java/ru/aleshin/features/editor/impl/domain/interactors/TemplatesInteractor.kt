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
package ru.aleshin.features.editor.impl.domain.interactors

import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal interface TemplatesInteractor {

    suspend fun addOrUpdateTemplate(template: Template): DomainResult<EditorFailures, Long>
    suspend fun fetchAllTemplates(): FlowDomainResult<EditorFailures, List<Template>>
    suspend fun fetchTemplateById(templateId: Long): DomainResult<EditorFailures, Template?>
    suspend fun deleteTemplateById(id: Long): DomainResult<EditorFailures, Unit>

    class Base @Inject constructor(
        private val eitherWrapper: EditorEitherWrapper,
        private val templatesRepository: TemplatesRepository,
    ) : TemplatesInteractor {

        override suspend fun addOrUpdateTemplate(template: Template) = eitherWrapper.wrap {
            templatesRepository.addOrUpdateTemplate(template)
        }

        override suspend fun fetchAllTemplates() = eitherWrapper.wrapFlow {
            templatesRepository.fetchAllTemplates()
        }

        override suspend fun fetchTemplateById(templateId: Long) = eitherWrapper.wrap {
            templatesRepository.fetchTemplatesByIdOnce(templateId)
        }

        override suspend fun deleteTemplateById(id: Long) = eitherWrapper.wrap {
            templatesRepository.deleteTemplateById(id)
        }
    }
}
