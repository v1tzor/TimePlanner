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
package ru.aleshin.core.ui.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * @author Stanislav Aleshin on 24.02.2023.
 */
data class TimePlannerStrings(
    val appName: String,
    val startTaskNotifyText: String,
    val timeTaskChannelName: String,
    val categoryWorkTitle: String,
    val categoryRestTitle: String,
    val categorySportTitle: String,
    val categorySleepTitle: String,
    val categoryCultureTitle: String,
    val categoryAffairsTitle: String,
    val categoryTransportTitle: String,
    val categoryStudyTitle: String,
    val categoryEatTitle: String,
    val categoryEntertainmentsTitle: String,
    val categoryHugieneTitle: String,
    val categoryOtherTitle: String,
    val minutesSymbol: String,
    val hoursSymbol: String,
    val separator: String,
    val alertDialogDismissTitle: String,
    val alertDialogSelectConfirmTitle: String,
    val alertDialogOkConfirmTitle: String,
    val categoryEmptyTitle: String,
    val expandedViewToggleTitle: String,
    val compactViewToggleTitle: String,
    val warningDialogTitle: String,
    val warningDeleteConfirmTitle: String,
    val hoursTitle: String,
    val minutesTitle: String,
    val homeTabTitle: String,
    val analyticsTabTitle: String,
    val settingsTabTitle: String,
    val mainDrawerTitle: String,
    val drawerMainSection: String,
    val templateDrawerTitle: String,
    val categoriesDrawerTitle: String,
    val splitFormat: String,
    val amFormatTitle: String,
    val pmFormatTitle: String,
)

internal val russianTimePlannerString = TimePlannerStrings(
    appName = "Time Planner",
    startTaskNotifyText = "Начало события",
    timeTaskChannelName = "События",
    categoryWorkTitle = "Работа",
    categoryRestTitle = "Отдых",
    categorySleepTitle = "Сон",
    categoryCultureTitle = "Культура",
    categorySportTitle = "Спорт",
    categoryAffairsTitle = "Дела",
    categoryTransportTitle = "Транспорт",
    categoryStudyTitle = "Учёба",
    categoryEatTitle = "Приём пищи",
    categoryEntertainmentsTitle = "Развлечение",
    categoryOtherTitle = "Прочее",
    categoryEmptyTitle = "Отсутствует",
    categoryHugieneTitle = "Гигиена",
    minutesSymbol = "м",
    hoursSymbol = "ч",
    separator = ":",
    alertDialogDismissTitle = "Отменить",
    alertDialogSelectConfirmTitle = "Выбрать",
    alertDialogOkConfirmTitle = "ОК",
    expandedViewToggleTitle = "Расширенный вид",
    compactViewToggleTitle = "Компактный вид",
    warningDialogTitle = "Предупреждение!",
    warningDeleteConfirmTitle = "Удалить",
    hoursTitle = "Часы",
    minutesTitle = "Минуты",
    homeTabTitle = "Главная",
    analyticsTabTitle = "Аналитика",
    settingsTabTitle = "Настройки",
    mainDrawerTitle = "Главная",
    templateDrawerTitle = "Шаблоны",
    categoriesDrawerTitle = "Категории",
    drawerMainSection = "Планы",
    splitFormat = "%s | %s",
    amFormatTitle = "AM",
    pmFormatTitle = "PM",
)

internal val englishTimePlannerString = TimePlannerStrings(
    appName = "Time Planner",
    startTaskNotifyText = "The beginning of the event",
    timeTaskChannelName = "Events",
    categoryWorkTitle = "Work",
    categoryRestTitle = "Rest",
    categorySleepTitle = "Sleep",
    categoryCultureTitle = "Culture",
    categoryAffairsTitle = "Affairs",
    categorySportTitle = "Sport",
    categoryTransportTitle = "Transport",
    categoryStudyTitle = "Study",
    categoryEatTitle = "Eating",
    categoryEntertainmentsTitle = "Entertainments",
    categoryOtherTitle = "Other",
    categoryEmptyTitle = "Absent",
    categoryHugieneTitle = "Hygiene",
    minutesSymbol = "m",
    hoursSymbol = "h",
    separator = ":",
    alertDialogDismissTitle = "Cancel",
    alertDialogSelectConfirmTitle = "Select",
    alertDialogOkConfirmTitle = "OK",
    expandedViewToggleTitle = "Expanded view",
    compactViewToggleTitle = "Compact view",
    warningDialogTitle = "Warning!",
    warningDeleteConfirmTitle = "Delete",
    hoursTitle = "Hours",
    minutesTitle = "Minutes",
    homeTabTitle = "Home",
    analyticsTabTitle = "Analytics",
    settingsTabTitle = "Settings",
    mainDrawerTitle = "Main",
    templateDrawerTitle = "Templates",
    categoriesDrawerTitle = "Categories",
    drawerMainSection = "Plans",
    splitFormat = "%s | %s",
    amFormatTitle = "AM",
    pmFormatTitle = "PM",
)

val LocalTimePlannerStrings = staticCompositionLocalOf<TimePlannerStrings> {
    error("Core Strings is not provided")
}

fun fetchCoreStrings(language: TimePlannerLanguage) = when (language) {
    TimePlannerLanguage.EN -> englishTimePlannerString
    TimePlannerLanguage.RU -> russianTimePlannerString
}
