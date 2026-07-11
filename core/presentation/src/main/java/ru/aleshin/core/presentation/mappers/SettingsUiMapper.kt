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
package ru.aleshin.core.presentation.mappers

import ru.aleshin.core.domain.entities.settings.ColorsType
import ru.aleshin.core.domain.entities.settings.LanguageType
import ru.aleshin.core.domain.entities.settings.ThemeType
import ru.aleshin.timeplanner.core.ui.theme.material.ColorsUiType
import ru.aleshin.timeplanner.core.ui.theme.material.ThemeUiType
import ru.aleshin.timeplanner.core.ui.theme.tokens.LanguageUiType

/**
 * @author Stanislav Aleshin on 07.07.2026.
 */
fun LanguageType.mapToUi() = LanguageUiType.entries.find { it.code == this.code }!!

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

fun LanguageUiType.mapToDomain() = LanguageType.entries.find { it.code == this.code }!!

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
