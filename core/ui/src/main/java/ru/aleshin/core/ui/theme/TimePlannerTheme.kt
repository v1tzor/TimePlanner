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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.aleshin.core.ui.theme.material.*
import ru.aleshin.core.ui.theme.tokens.*

@Composable
fun TimePlannerTheme(
    dynamicColor: Boolean = false,
    themeColorsType: ThemeColorsUiType = ThemeColorsUiType.DEFAULT,
    language: LanguageUiType = LanguageUiType.DEFAULT,
    content: @Composable () -> Unit,
) {
    val appLanguage = when (language) {
        LanguageUiType.DEFAULT -> fetchAppLanguage(Locale.current.language)
        LanguageUiType.EN -> TimePlannerLanguage.EN
        LanguageUiType.RU -> TimePlannerLanguage.RU
    }
    val appStrings = fetchCoreStrings(appLanguage)
    val appElevations = baseTimePlannerElevations
    val appIcons = baseTimePlannerIcons

    MaterialTheme(
        colorScheme = themeColorsType.toColorScheme(dynamicColor),
        shapes = baseShapes,
        typography = baseTypography,
    ) {
        CompositionLocalProvider(
            LocalTimePlannerLanguage provides appLanguage,
            LocalTimePlannerElevations provides appElevations,
            LocalTimePlannerStrings provides appStrings,
            LocalTimePlannerIcons provides appIcons,
            content = content,
        )
        TimePlannerSystemUi(
            navigationBarColor = colorScheme.background,
            statusBarColor = colorScheme.background,
            isDarkIcons = themeColorsType.isDarkTheme(),
        )
    }
}

@Composable
fun TimePlannerSystemUi(navigationBarColor: Color, statusBarColor: Color, isDarkIcons: Boolean) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = !isDarkIcons,
        )
        systemUiController.setStatusBarColor(color = statusBarColor, darkIcons = !isDarkIcons)
    }
}
