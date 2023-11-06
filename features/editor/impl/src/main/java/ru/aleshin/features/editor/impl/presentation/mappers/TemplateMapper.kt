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
package ru.aleshin.features.editor.impl.presentation.mappers

import ru.aleshin.features.editor.impl.presentation.models.template.TemplateUi
import ru.aleshin.features.home.api.domain.entities.template.Template

/**
 * @author Stanislav Aleshin on 30.07.2023.
 */
internal fun TemplateUi.mapToDomain() = Template(
    templateId = templateId,
    startTime = startTime,
    endTime = endTime,
    category = category.mapToDomain(),
    subCategory = subCategory?.mapToDomain(),
    isImportant = isImportant,
    isEnableNotification = isEnableNotification,
    isConsiderInStatistics = isConsiderInStatistics,
    repeatEnabled = repeatEnabled,
    repeatTimes = repeatTimes,
)

internal fun Template.mapToUi() = TemplateUi(
    templateId = templateId,
    startTime = startTime,
    endTime = endTime,
    category = category.mapToUi(),
    subCategory = subCategory?.mapToUi(),
    isImportant = isImportant,
    isEnableNotification = isEnableNotification,
    isConsiderInStatistics = isConsiderInStatistics,
    repeatEnabled = repeatEnabled,
    repeatTimes = repeatTimes,
)
