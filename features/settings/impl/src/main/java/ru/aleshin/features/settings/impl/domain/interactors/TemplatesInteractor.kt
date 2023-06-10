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
package ru.aleshin.features.settings.impl.domain.interactors

import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.home.api.domains.entities.template.Template
import ru.aleshin.features.home.api.domains.repository.TemplatesRepository
import ru.aleshin.features.settings.impl.domain.common.SettingsEitherWrapper
import ru.aleshin.features.settings.impl.domain.common.SettingsFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 09.06.2023.
 */
internal interface TemplatesInteractor {

    suspend fun removeAllTemplates(): UnitDomainResult<SettingsFailures>
    suspend fun fetchAllTemplates(): DomainResult<SettingsFailures, List<Template>>
    suspend fun addTemplates(templates: List<Template>): UnitDomainResult<SettingsFailures>

    class Base @Inject constructor(
        private val templatesRepository: TemplatesRepository,
        private val eitherWrapper: SettingsEitherWrapper,
    ) : TemplatesInteractor {

        override suspend fun removeAllTemplates() = eitherWrapper.wrap {
            templatesRepository.deleteAllTemplates()
        }

        override suspend fun fetchAllTemplates() = eitherWrapper.wrap {
            templatesRepository.fetchAllTemplates()
        }

        override suspend fun addTemplates(templates: List<Template>) = eitherWrapper.wrap {
            templatesRepository.addTemplates(templates)
        }
    }
}
