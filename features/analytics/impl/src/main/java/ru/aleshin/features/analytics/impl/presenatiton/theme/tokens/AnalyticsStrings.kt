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
package ru.aleshin.features.analytics.impl.presenatiton.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
internal data class AnalyticsStrings(
    val topAppBarTitle: String,
    val menuIconDesc: String,
    val timeTabTitle: String,
    val workLoadTabTitle: String,
    val intelligenceTabTitle: String,
    val weekTimePeriod: String,
    val monthTimePeriod: String,
    val yearTimePeriod: String,
    val timeSelectorTitle: String,
    val refreshAnalyticIconDesc: String,
    val otherAnalyticsName: String,
    val allTimeTitle: String,
    val totalCountTaskTitle: String,
    val totalTimeTaskTitle: String,
    val averageCountTaskTitle: String,
    val averageTimeTaskTitle: String,
    val planningStatisticsTitle: String,
    val otherError: String,
)

internal val russianAnalyticsStrings = AnalyticsStrings(
    topAppBarTitle = "Аналитика",
    menuIconDesc = "Меню",
    timeTabTitle = "Время",
    workLoadTabTitle = "Загруженность",
    intelligenceTabTitle = "Сведения",
    weekTimePeriod = "Неделя",
    monthTimePeriod = "Месяц",
    yearTimePeriod = "Год",
    timeSelectorTitle = "Временной промежуток:",
    refreshAnalyticIconDesc = "Обновить аналитику",
    otherAnalyticsName = "Прочее",
    allTimeTitle = "Всего:",
    totalCountTaskTitle = "Общее кол-во задач",
    averageCountTaskTitle = "Среднее число задач в день",
    totalTimeTaskTitle = "Общее время задач",
    averageTimeTaskTitle = "Среднее время задачи",
    planningStatisticsTitle = "Статистика планирования",
    otherError = "Ошибка! Обратитесь к разработчику.",
)

internal val englishAnalyticsStrings = AnalyticsStrings(
    topAppBarTitle = "Analytics",
    menuIconDesc = "Menu",
    timeTabTitle = "Time",
    workLoadTabTitle = "Workload",
    intelligenceTabTitle = "Information",
    weekTimePeriod = "Week",
    monthTimePeriod = "Month",
    yearTimePeriod = "Year",
    timeSelectorTitle = "Time period:",
    refreshAnalyticIconDesc = "Refresh analytics",
    otherAnalyticsName = "Else",
    allTimeTitle = "Total:",
    totalCountTaskTitle = "Total number of tasks",
    averageCountTaskTitle = "Average number of tasks",
    totalTimeTaskTitle = "Total time of tasks",
    averageTimeTaskTitle = "Average time of tasks",
    planningStatisticsTitle = "Planning statistics",
    otherError = "Error! Contact the developer.",
)

internal val LocalAnalyticsStrings = staticCompositionLocalOf<AnalyticsStrings> {
    error("Analytics Strings is not provided")
}

internal fun fetchAnalyticsStrings(language: TimePlannerLanguage) = when (language) {
    TimePlannerLanguage.EN -> englishAnalyticsStrings
    TimePlannerLanguage.RU -> russianAnalyticsStrings
}
