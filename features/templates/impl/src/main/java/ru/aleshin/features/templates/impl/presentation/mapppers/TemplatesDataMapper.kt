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
package ru.aleshin.features.templates.impl.presentation.mapppers

import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatePatternDay
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesData
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesPattern
import ru.aleshin.features.templates.impl.presentation.models.TemplatePatternDayUi
import ru.aleshin.features.templates.impl.presentation.models.TemplatesDataUi
import ru.aleshin.features.templates.impl.presentation.models.TemplatesPatternUi

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
internal fun TemplatesData.mapToUi() = TemplatesDataUi(
    activeTemplatesCount = activeTemplatesCount,
    inactiveTemplatesCount = inactiveTemplatesCount,
    activeTemplates = activeTemplates.map { template -> template.mapToUi() },
    inactiveTemplates = inactiveTemplates.map { template -> template.mapToUi() },
    weekPattern = weekPattern.mapToUi(),
    monthPattern = monthPattern.mapToUi(),
)

internal fun TemplatesPattern.mapToUi() = TemplatesPatternUi(
    templatesCount = templatesCount,
    repeatsCount = repeatsCount,
    days = days.map { day -> day.mapToUi() },
)

internal fun TemplatePatternDay.mapToUi() = TemplatePatternDayUi(
    date = date,
    weekDay = weekDay,
    dayNumber = dayNumber,
    isCurrentDay = isCurrentDay,
    templatesCount = templatesCount,
    templates = templates.map { template -> template.mapToUi() },
)
