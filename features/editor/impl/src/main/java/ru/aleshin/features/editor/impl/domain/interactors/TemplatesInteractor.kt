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
package ru.aleshin.features.editor.impl.domain.interactors

import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.home.api.domains.entities.template.Template
import ru.aleshin.features.home.api.domains.repository.TemplatesRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal interface TemplatesInteractor {

    suspend fun fetchTemplates(): DomainResult<EditorFailures, List<Template>>
    suspend fun updateTemplate(template: Template): DomainResult<EditorFailures, Unit>
    suspend fun addTemplate(template: Template): DomainResult<EditorFailures, Int>
    suspend fun deleteTemplateById(id: Int): DomainResult<EditorFailures, Unit>

    class Base @Inject constructor(
        private val eitherWrapper: EditorEitherWrapper,
        private val templatesRepository: TemplatesRepository,
    ) : TemplatesInteractor {

        override suspend fun fetchTemplates() = eitherWrapper.wrap {
            templatesRepository.fetchAllTemplates()
        }

        override suspend fun updateTemplate(template: Template) = eitherWrapper.wrap {
            templatesRepository.updateTemplate(template)
        }

        override suspend fun addTemplate(template: Template) = eitherWrapper.wrap {
            templatesRepository.addTemplate(template)
        }

        override suspend fun deleteTemplateById(id: Int) = eitherWrapper.wrap {
            templatesRepository.deleteTemplateById(id)
        }
    }
}
