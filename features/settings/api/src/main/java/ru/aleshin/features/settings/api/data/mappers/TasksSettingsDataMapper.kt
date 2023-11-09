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
package ru.aleshin.features.settings.api.data.mappers

import ru.aleshin.core.ui.views.CalendarButtonBehavior
import ru.aleshin.core.ui.views.ViewToggleStatus
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.settings.api.data.models.TasksSettingsEntity
import ru.aleshin.features.settings.api.domain.entities.TasksSettings

/**
 * @author Stanislav Aleshin on 15.09.2023.
 */
fun TasksSettings.mapToData() = TasksSettingsEntity(
    id = 0,
    taskViewStatus = taskViewStatus.toString(),
    taskAnalyticsRange = taskAnalyticsRange.toString(),
    calendarButtonBehavior = calendarButtonBehavior.toString(),
    secureMode = secureMode,
)

fun TasksSettingsEntity.mapToDomain() = TasksSettings(
    taskViewStatus = ViewToggleStatus.valueOf(taskViewStatus),
    taskAnalyticsRange = TimePeriod.valueOf(taskAnalyticsRange),
    calendarButtonBehavior = CalendarButtonBehavior.valueOf(calendarButtonBehavior),
    secureMode = secureMode,
)
