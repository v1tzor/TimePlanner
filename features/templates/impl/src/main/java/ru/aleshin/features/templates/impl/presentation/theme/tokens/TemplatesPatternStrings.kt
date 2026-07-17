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
package ru.aleshin.features.templates.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
internal data class TemplatesPatternStrings(
    val weeklyPatternTitle: String,
    val monthlyPatternTitle: String,
    val activeTitle: String,
    val allTitle: String,
    val repeatsThisWeekTitle: String,
    val repeatsThisMonthTitle: String,
    val patternSummaryFormat: String,
    val activeTemplatesTitle: String,
    val inactiveTemplatesTitle: String,
    val pausedTitle: String,
    val addRepeatTitle: String,
    val repeatSettingsTitle: String,
    val stopRepeatTitle: String,
    val restartRepeatTitle: String,
    val deleteTitle: String,
)

internal val russianTemplatesPatternStrings = TemplatesPatternStrings(
    weeklyPatternTitle = "Недельный паттерн",
    monthlyPatternTitle = "Месячный паттерн",
    activeTitle = "Активные",
    allTitle = "Все",
    repeatsThisWeekTitle = "Повторы на этой неделе",
    repeatsThisMonthTitle = "Повторы в этом месяце",
    patternSummaryFormat = "%1\$d шаблонов • %2\$d повторов",
    activeTemplatesTitle = "Активные",
    inactiveTemplatesTitle = "Неактивные",
    pausedTitle = "Пауза",
    addRepeatTitle = "Добавить повтор",
    repeatSettingsTitle = "Повторения",
    stopRepeatTitle = "Остановить",
    restartRepeatTitle = "Возобновить",
    deleteTitle = "Удалить",
)

internal val englishTemplatesPatternStrings = TemplatesPatternStrings(
    weeklyPatternTitle = "Weekly pattern",
    monthlyPatternTitle = "Monthly pattern",
    activeTitle = "Active",
    allTitle = "All",
    repeatsThisWeekTitle = "Repeats this week",
    repeatsThisMonthTitle = "Repeats this month",
    patternSummaryFormat = "%1\$d templates • %2\$d repeats",
    activeTemplatesTitle = "Active",
    inactiveTemplatesTitle = "Inactive",
    pausedTitle = "Paused",
    addRepeatTitle = "Add repeat",
    repeatSettingsTitle = "Repeats",
    stopRepeatTitle = "Stop repeats",
    restartRepeatTitle = "Resume repeats",
    deleteTitle = "Delete",
)

internal val LocalTemplatesPatternStrings = staticCompositionLocalOf<TemplatesPatternStrings> {
    error("Templates pattern strings are not provided")
}

internal fun fetchTemplatesPatternStrings(language: TimePlannerLanguage) = when (language) {
    TimePlannerLanguage.RU -> russianTemplatesPatternStrings
    else -> englishTemplatesPatternStrings
}
