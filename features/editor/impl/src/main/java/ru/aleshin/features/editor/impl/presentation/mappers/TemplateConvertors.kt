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
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.presentation.models.EditModelUi
import ru.aleshin.features.editor.impl.presentation.models.EditParameters
import ru.aleshin.features.home.api.domains.entities.template.Template
import java.util.Date

/**
 * @author Stanislav Aleshin on 06.05.2023.
 */
internal fun Template.convertToEditModel(date: Date) = EditModelUi(
    date = date,
    timeRanges = TimeRange(
        from = startTime.changeDay(date),
        to = endTime.changeDay(date),
    ),
    mainCategory = category,
    subCategory = subCategory,
    templateId = templateId,
    parameters = EditParameters(
        isImportant = isImportant,
        isEnableNotification = isEnableNotification,
        isConsiderInStatistics = isConsiderInStatistics,
    ),
)

internal fun EditModelUi.convertToTemplate(id: Int = 0) = Template(
    templateId = id,
    startTime = timeRanges.from,
    endTime = timeRanges.to,
    category = mainCategory,
    subCategory = subCategory,
    isImportant = parameters.isImportant,
    isEnableNotification = parameters.isEnableNotification,
    isConsiderInStatistics = parameters.isConsiderInStatistics,
)
