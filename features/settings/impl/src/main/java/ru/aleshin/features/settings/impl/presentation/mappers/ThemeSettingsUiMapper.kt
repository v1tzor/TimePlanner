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
package ru.aleshin.features.settings.impl.presentation.mappers

import ru.aleshin.core.ui.theme.material.ThemeColorsUiType
import ru.aleshin.core.ui.theme.tokens.LanguageUiType
import ru.aleshin.features.settings.api.domain.entities.LanguageType
import ru.aleshin.features.settings.api.domain.entities.ThemeColorsType
import ru.aleshin.features.settings.api.domain.entities.ThemeSettings
import ru.aleshin.features.settings.impl.presentation.models.ThemeSettingsUi

/**
 * @author Stanislav Aleshin on 30.07.2023.
 */
internal fun ThemeSettings.mapToUi() = ThemeSettingsUi(
    language = language.mapToUi(),
    themeColors = themeColors.mapToUi(),
    isDynamicColorEnable = isDynamicColorEnable,
)

internal fun ThemeSettingsUi.mapToDomain() = ThemeSettings(
    language = language.mapToDomain(),
    themeColors = themeColors.mapToDomain(),
    isDynamicColorEnable = isDynamicColorEnable,
)

fun LanguageType.mapToUi() = LanguageUiType.values().find { it.code == this.code }!!

fun LanguageUiType.mapToDomain() = LanguageType.values().find { it.code == this.code }!!

fun ThemeColorsType.mapToUi() = when (this) {
    ThemeColorsType.DEFAULT -> ThemeColorsUiType.DEFAULT
    ThemeColorsType.LIGHT -> ThemeColorsUiType.LIGHT
    ThemeColorsType.DARK -> ThemeColorsUiType.DARK
}

fun ThemeColorsUiType.mapToDomain() = when (this) {
    ThemeColorsUiType.DEFAULT -> ThemeColorsType.DEFAULT
    ThemeColorsUiType.LIGHT -> ThemeColorsType.LIGHT
    ThemeColorsUiType.DARK -> ThemeColorsType.DARK
}
