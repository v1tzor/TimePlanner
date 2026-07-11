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
package ru.aleshin.core.data.mappers.settings

import ru.aleshin.core.data.models.settings.TasksSettingsEntity
import ru.aleshin.core.domain.entities.settings.CalendarButtonBehavior
import ru.aleshin.core.domain.entities.settings.TasksSettings
import ru.aleshin.core.domain.entities.settings.ViewToggleStatus
import ru.aleshin.core.utils.extensions.minutesToMillis
import ru.aleshin.core.utils.extensions.toMinutes
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimePeriod

/**
 * @author Stanislav Aleshin on 15.09.2023.
 */
fun TasksSettings.mapToData() = TasksSettingsEntity(
    id = 0,
    taskViewStatus = taskViewStatus.toString(),
    taskAnalyticsRange = taskAnalyticsRange.toString(),
    calendarButtonBehavior = calendarButtonBehavior.toString(),
    secureMode = secureMode,
    durationPresets = durationPresets.mapToData(),
)

fun TasksSettingsEntity.mapToDomain() = TasksSettings(
    taskViewStatus = ViewToggleStatus.valueOf(taskViewStatus),
    taskAnalyticsRange = TimePeriod.valueOf(taskAnalyticsRange),
    calendarButtonBehavior = CalendarButtonBehavior.valueOf(calendarButtonBehavior),
    secureMode = secureMode,
    durationPresets = durationPresets.mapToDomain(),
)

private fun List<Long>.mapToData(): String {
    return map { it.toMinutes() }.distinct().sorted().joinToString(separator = ",")
}

private fun String.mapToDomain(): List<Long> {
    return split(",")
        .mapNotNull { value -> value.trim().toIntOrNull() }
        .filter { value -> value in MIN_PRESET_MINUTES..MAX_PRESET_MINUTES }
        .distinct()
        .sorted()
        .takeIf { it.isNotEmpty() }
        ?.map { minutes -> minutes.minutesToMillis() }
        ?: defaultPresets()
}

private fun defaultPresets(): List<Long> {
    return Constants.Date.DEFAULT_DURATION_PRESETS.split(",").map { it.toInt().minutesToMillis() }
}

private const val MIN_PRESET_MINUTES = 1
private const val MAX_PRESET_MINUTES = 1440
