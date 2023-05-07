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
package ru.aleshin.features.home.api.data.mappers.template

import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.functional.Mapper
import ru.aleshin.features.home.api.data.mappers.categories.mapToDomain
import ru.aleshin.features.home.api.data.models.template.TemplateDetails
import ru.aleshin.features.home.api.data.models.template.TemplateEntity
import ru.aleshin.features.home.api.domains.entities.template.Template
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
interface TemplatesDataToDomainMapper : Mapper<TemplateDetails, Template> {
    class Base @Inject constructor() : TemplatesDataToDomainMapper {
        override fun map(input: TemplateDetails) = Template(
            startTime = input.template.startTime.mapToDate(),
            endTime = input.template.endTime.mapToDate(),
            category = input.mainCategory.mapToDomain(),
            subCategory = input.subCategory?.mapToDomain(input.mainCategory.mapToDomain()),
            isImportant = input.template.isImportant,
            isEnableNotification = input.template.isEnableNotification,
            isConsiderInStatistics = input.template.isConsiderInStatistics,
            templateId = input.template.id,
        )
    }
}

interface TemplatesDomainToDataMapper : Mapper<Template, TemplateEntity> {
    class Base @Inject constructor() : TemplatesDomainToDataMapper {
        override fun map(input: Template) = TemplateEntity(
            id = input.templateId,
            startTime = input.startTime.time,
            endTime = input.endTime.time,
            categoryId = input.category.id,
            subCategoryId = input.subCategory?.id,
            isImportant = input.isImportant,
            isEnableNotification = input.isEnableNotification,
            isConsiderInStatistics = input.isConsiderInStatistics,
        )
    }
}
