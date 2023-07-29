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
    val gerLanguageTitle: String,
    val defaultLanguageTitle: String,
    val backIconDesc: String,
    val moreIconDesc: String,
    val resetToDefaultTitle: String,
    val menuIconDesc: String,
    val mainSettingsClearDataTitle: String,
    val clearDataTitle: String,
    val clearDataButtonTitle: String,
    val clearDataWarning: String,
    val backupDataTitle: String,
    val backupDataButtonTitle: String,
    val restoreDataButtonTitle: String,
    val errorBackupMessage: String,
    val errorBackupFileMessage: String,
    val otherError: String,
    val mainSettingsDynamicColorTitle: String,
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
    gerLanguageTitle = "Немецкий (b)",
    defaultLanguageTitle = "По умолчанию",
    mainSettingsLanguageTitle = "Язык приложения",
    backIconDesc = "Назад",
    moreIconDesc = "Дополнительно",
    resetToDefaultTitle = "По умолчанию",
    menuIconDesc = "Меню",
    clearDataTitle = "Удалить все данные",
    mainSettingsClearDataTitle = "Данные",
    clearDataButtonTitle = "Очистить",
    clearDataWarning = "Данное действие приведёт к полной очистке данных приложения!",
    backupDataTitle = "Резервная копия",
    backupDataButtonTitle = "Сохранить",
    restoreDataButtonTitle = "Восстановить",
    errorBackupMessage = "Ошибка резервного копирования",
    errorBackupFileMessage = "Ошибка при работе с файлом",
    otherError = "Ошибка! Обратитесь к разработчику.",
    mainSettingsDynamicColorTitle = "Динамические цвета",
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
    gerLanguageTitle = "German (beta)",
    defaultLanguageTitle = "Default",
    mainSettingsLanguageTitle = "App language",
    backIconDesc = "Navigate up",
    moreIconDesc = "More",
    resetToDefaultTitle = "Reset to default",
    menuIconDesc = "Menu",
    clearDataTitle = "Delete all data",
    mainSettingsClearDataTitle = "Data",
    clearDataButtonTitle = "Clear",
    clearDataWarning = "This action will lead to a complete cleaning of the application data!",
    backupDataTitle = "Backup",
    backupDataButtonTitle = "Save",
    restoreDataButtonTitle = "Restore",
    errorBackupMessage = "Backup Error",
    errorBackupFileMessage = "Error when working with the file",
    otherError = "Error! Contact the developer.",
    mainSettingsDynamicColorTitle = "Dynamic colors",
)

internal val germanSettingsString = SettingsStrings(
    settingsTitle = "Einstellungen",
    mainSettingsTitle = "Allgemeine Einstellungen",
    mainSettingsThemeTitle = "Thema:",
    darkThemeTitle = "Dunkel",
    lightThemeTitle = "Hell",
    systemThemeTitle = "System",
    rusLanguageTitle = "Russisch",
    engLanguageTitle = "Englisch",
    gerLanguageTitle = "Deutsch (b)",
    defaultLanguageTitle = "Standard",
    mainSettingsLanguageTitle = "Sprache der Anwendung",
    backIconDesc = "Nach oben navigieren",
    moreIconDesc = "Mehr",
    resetToDefaultTitle = "Auf Standard zurücksetzen",
    menuIconDesc = "Menü",
    clearDataTitle = "Alle Daten löschen",
    mainSettingsClearDataTitle = "Daten",
    clearDataButtonTitle = "Löschen",
    clearDataWarning = "Diese Aktion führt zu einer vollständigen Bereinigung der Anwendungsdaten!",
    backupDataTitle = "Sicherung",
    backupDataButtonTitle = "Sichern",
    restoreDataButtonTitle = "Wiederherstellen",
    errorBackupMessage = "Sicherungsfehler",
    errorBackupFileMessage = "Fehler beim Arbeiten mit der Datei",
    otherError = "Fehler! Kontaktieren Sie den Entwickler.",
    mainSettingsDynamicColorTitle = "Dynamische Farben",
)

internal val LocalSettingsStrings = staticCompositionLocalOf<SettingsStrings> {
    error("Settings Strings is not provided")
}

internal fun fetchSettingsStrings(language: TimePlannerLanguage) = when (language) {
    TimePlannerLanguage.EN -> englishSettingsString
    TimePlannerLanguage.RU -> russianSettingsString
    TimePlannerLanguage.DE -> germanSettingsString
}
