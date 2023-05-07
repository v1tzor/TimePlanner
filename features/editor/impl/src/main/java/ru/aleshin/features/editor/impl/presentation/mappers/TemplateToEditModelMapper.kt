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
package ru.aleshin.features.editor.impl.presentation.mappers

import ru.aleshin.core.utils.extensions.changeDay
import ru.aleshin.core.utils.functional.ParameterizedMapper
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.api.domain.EditModel
import ru.aleshin.features.home.api.domains.entities.template.Template
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 06.05.2023.
 */
internal interface TemplateToEditModelMapper : ParameterizedMapper<Template, EditModel, Date> {
    class Base @Inject constructor() : TemplateToEditModelMapper {
        override fun map(input: Template, parameter: Date) = EditModel(
            date = parameter,
            timeRanges = TimeRange(
                from = input.startTime.changeDay(parameter),
                to = input.endTime.changeDay(parameter),
            ),
            mainCategory = input.category,
            subCategory = input.subCategory,
            isImportant = input.isImportant,
            isEnableNotification = input.isEnableNotification,
            isConsiderInStatistics = input.isConsiderInStatistics,
            templateId = input.templateId,
        )
    }
}
