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
package ru.aleshin.features.home.impl.domain.common

import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.utils.extensions.changeDay
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 06.05.2023.
 */
internal fun Template.convertToTimeTask(
    date: Date,
    key: Long = generateUniqueKey(),
    createdAt: Date? = Date(),
) = TimeTask(
    key = key,
    date = date,
    timeRange = TimeRange(
        from = startTime.changeDay(date), 
        to = if (endTime.isCurrentDay(startTime)) endTime.changeDay(date) else endTime.changeDay(date.shiftDay(1)),
    ),
    createdAt = createdAt,
    category = category,
    subCategory = subCategory,
    priority = priority,
    isEnableNotification = isEnableNotification,
    isConsiderInStatistics = isConsiderInStatistics,
)
