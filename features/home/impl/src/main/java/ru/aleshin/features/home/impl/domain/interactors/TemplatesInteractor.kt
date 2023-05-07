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
package ru.aleshin.features.home.impl.domain.interactors

import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domains.entities.template.Template
import ru.aleshin.features.home.api.domains.entities.template.equalsTimeTask
import ru.aleshin.features.home.api.domains.repository.TemplatesRepository
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal interface TemplatesInteractor {

    suspend fun checkIsTemplate(timeTask: TimeTask): DomainResult<HomeFailures, Int?>
    suspend fun addTemplateTimeTask(template: Template): DomainResult<HomeFailures, Int>
    suspend fun deleteTemplateTimeTask(id: Int): DomainResult<HomeFailures, Unit>

    class Base @Inject constructor(
        private val templatesRepository: TemplatesRepository,
        private val eitherWrapper: HomeEitherWrapper,
    ) : TemplatesInteractor {

        override suspend fun checkIsTemplate(timeTask: TimeTask) = eitherWrapper.wrap {
            val allTemplates = templatesRepository.fetchAllTemplates()
            val foundTimeTask = allTemplates.find { it.equalsTimeTask(timeTask) }

            return@wrap foundTimeTask?.templateId
        }

        override suspend fun addTemplateTimeTask(template: Template) = eitherWrapper.wrap {
            templatesRepository.addTemplate(template)
        }

        override suspend fun deleteTemplateTimeTask(id: Int) = eitherWrapper.wrap {
            templatesRepository.deleteTemplateById(id)
        }
    }
}
