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

import androidx.compose.runtime.Composable
import ru.aleshin.core.ui.theme.tokens.LanguageUiType
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes

/**
 * @author Stanislav Aleshin on 21.03.2023.
 */
@Composable
fun LanguageUiType.toLanguageName(): String = when (this) {
    LanguageUiType.DEFAULT -> SettingsThemeRes.strings.defaultLanguageTitle
    LanguageUiType.EN -> SettingsThemeRes.strings.engLanguageTitle
    LanguageUiType.RU -> SettingsThemeRes.strings.rusLanguageTitle
    LanguageUiType.DE -> SettingsThemeRes.strings.gerLanguageTitle
    LanguageUiType.ES -> SettingsThemeRes.strings.spaLanguageTitle
    LanguageUiType.FA -> SettingsThemeRes.strings.perLanguageTitle
    LanguageUiType.FR -> SettingsThemeRes.strings.freLanguageTitle
    LanguageUiType.PT -> SettingsThemeRes.strings.ptLanguageTitle
    LanguageUiType.TR -> SettingsThemeRes.strings.trLanguageTitle
}
