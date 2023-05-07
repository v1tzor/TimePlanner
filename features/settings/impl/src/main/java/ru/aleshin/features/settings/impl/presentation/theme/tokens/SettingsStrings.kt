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
package ru.aleshin.features.settings.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
internal data class SettingsStrings(
    val settingsTitle: String,
    val mainSettingsTitle: String,
    val mainSettingsThemeTitle: String,
    val darkThemeTitle: String,
    val lightThemeTitle: String,
    val systemThemeTitle: String,
    val mainSettingsLanguageTitle: String,
    val rusLanguageTitle: String,
    val engLanguageTitle: String,
    val defaultLanguageTitle: String,
    val backIconDesc: String,
    val moreIconDesc: String,
    val resetToDefaultTitle: String,
    val menuIconDesc: String,
)

internal val russianSettingsString = SettingsStrings(
    settingsTitle = "Настройки",
    mainSettingsTitle = "Основные настройки",
    mainSettingsThemeTitle = "Тема:",
    darkThemeTitle = "Тёмная",
    lightThemeTitle = "Светлая",
    systemThemeTitle = "Системная",
    rusLanguageTitle = "Русский",
    engLanguageTitle = "Английский",
    defaultLanguageTitle = "По умолчанию",
    mainSettingsLanguageTitle = "Язык приложения",
    backIconDesc = "Назад",
    moreIconDesc = "Дополнительно",
    resetToDefaultTitle = "По умолчанию",
    menuIconDesc = "Меню",
)

internal val englishSettingsString = SettingsStrings(
    settingsTitle = "Settings",
    mainSettingsTitle = "General settings",
    mainSettingsThemeTitle = "Theme:",
    darkThemeTitle = "Dark",
    lightThemeTitle = "Light",
    systemThemeTitle = "System",
    rusLanguageTitle = "Russian",
    engLanguageTitle = "English",
    defaultLanguageTitle = "Defualt",
    mainSettingsLanguageTitle = "App language",
    backIconDesc = "Navigate up",
    moreIconDesc = "More",
    resetToDefaultTitle = "Reset to defualt",
    menuIconDesc = "Menu",
)

internal val LocalSettingsStrings = staticCompositionLocalOf<SettingsStrings> {
    error("Settings Strings is not provided")
}

internal fun fetchSettingsStrings(language: TimePlannerLanguage) = when (language) {
    TimePlannerLanguage.EN -> englishSettingsString
    TimePlannerLanguage.RU -> russianSettingsString
}
