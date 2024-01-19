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

import ru.aleshin.core.domain.entities.settings.ColorsType
import ru.aleshin.core.domain.entities.settings.LanguageType
import ru.aleshin.core.domain.entities.settings.ThemeSettings
import ru.aleshin.core.domain.entities.settings.ThemeType
import ru.aleshin.core.ui.theme.material.ColorsUiType
import ru.aleshin.core.ui.theme.material.ThemeUiType
import ru.aleshin.core.ui.theme.tokens.LanguageUiType
import ru.aleshin.features.settings.impl.presentation.models.ThemeSettingsUi

/**
 * @author Stanislav Aleshin on 30.07.2023.
 */
internal fun ThemeSettings.mapToUi() = ThemeSettingsUi(
    language = language.mapToUi(),
    themeColors = themeColors.mapToUi(),
    isDynamicColorEnable = isDynamicColorEnable,
    colorsType = colorsType.mapToUi(),
)

internal fun ThemeSettingsUi.mapToDomain() = ThemeSettings(
    language = language.mapToDomain(),
    themeColors = themeColors.mapToDomain(),
    isDynamicColorEnable = isDynamicColorEnable,
    colorsType = colorsType.mapToDomain(),
)

fun LanguageType.mapToUi() = LanguageUiType.values().find { it.code == this.code }!!

fun ThemeType.mapToUi() = when (this) {
    ThemeType.DEFAULT -> ThemeUiType.DEFAULT
    ThemeType.LIGHT -> ThemeUiType.LIGHT
    ThemeType.DARK -> ThemeUiType.DARK
}

fun ColorsType.mapToUi() = when (this) {
    ColorsType.PINK -> ColorsUiType.PINK
    ColorsType.PURPLE -> ColorsUiType.PURPLE
    ColorsType.RED -> ColorsUiType.RED
    ColorsType.BLUE -> ColorsUiType.BLUE
}

fun LanguageUiType.mapToDomain() = LanguageType.values().find { it.code == this.code }!!

fun ThemeUiType.mapToDomain() = when (this) {
    ThemeUiType.DEFAULT -> ThemeType.DEFAULT
    ThemeUiType.LIGHT -> ThemeType.LIGHT
    ThemeUiType.DARK -> ThemeType.DARK
}

fun ColorsUiType.mapToDomain() = when (this) {
    ColorsUiType.PINK -> ColorsType.PINK
    ColorsUiType.PURPLE -> ColorsType.PURPLE
    ColorsUiType.RED -> ColorsType.RED
    ColorsUiType.BLUE -> ColorsType.BLUE
}
