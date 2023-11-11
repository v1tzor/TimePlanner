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
package ru.aleshin.core.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import ru.aleshin.core.ui.theme.material.*
import ru.aleshin.core.ui.theme.tokens.*

/**
 * @author Stanislav Aleshin on 27.02.2023.
 */
@Composable
fun TimePlannerTheme(
    languageType: LanguageUiType = LanguageUiType.DEFAULT,
    themeType: ThemeUiType = ThemeUiType.DEFAULT,
    colors: ColorsUiType = ColorsUiType.PINK,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val appLanguage = fetchAppLanguage(languageType)
    val coreStrings = fetchCoreStrings(appLanguage)
    val colorsType = fetchAppColorsType(themeType, colors)
    val appElevations = fetchAppElevations()
    val coreIcons = fetchCoreIcons()

    MaterialTheme(
        colorScheme = themeType.toColorScheme(dynamicColor, colors),
        shapes = baseShapes,
        typography = baseTypography,
    ) {
        CompositionLocalProvider(
            LocalTimePlannerColorsType provides colorsType,
            LocalTimePlannerLanguage provides appLanguage,
            LocalTimePlannerElevations provides appElevations,
            LocalTimePlannerStrings provides coreStrings,
            LocalTimePlannerIcons provides coreIcons,
            content = content,
        )
    }
}
