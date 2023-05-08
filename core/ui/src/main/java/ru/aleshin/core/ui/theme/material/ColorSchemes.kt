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
package ru.aleshin.core.ui.theme.material

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ru.aleshin.core.ui.theme.TimePlannerRes

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */

private val baseLightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    outline = md_theme_light_outline,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    inverseSurface = md_theme_light_inverseSurface,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

private val baseDarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    outline = md_theme_dark_outline,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    inverseSurface = md_theme_dark_inverseSurface,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

enum class ThemeColorsUiType {
    DEFAULT, LIGHT, DARK
}

@Composable
fun ThemeColorsUiType.toColorScheme(
    dynamicColor: Boolean,
) = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val context = LocalContext.current
    when (this) {
        ThemeColorsUiType.DEFAULT -> if (isSystemInDarkTheme()) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
        ThemeColorsUiType.LIGHT -> dynamicLightColorScheme(context)
        ThemeColorsUiType.DARK -> dynamicDarkColorScheme(context)
    }
} else {
    when (this) {
        ThemeColorsUiType.DEFAULT -> if (isSystemInDarkTheme()) baseDarkColorScheme else baseLightColorScheme
        ThemeColorsUiType.LIGHT -> baseLightColorScheme
        ThemeColorsUiType.DARK -> baseDarkColorScheme
    }
}

@Composable
fun ThemeColorsUiType.isDarkTheme() = when (this) {
    ThemeColorsUiType.DEFAULT -> isSystemInDarkTheme()
    ThemeColorsUiType.LIGHT -> false
    ThemeColorsUiType.DARK -> true
}

@Composable
fun ColorScheme.surfaceOne() = surfaceColorAtElevation(TimePlannerRes.elevations.levelOne)

@Composable
fun ColorScheme.surfaceTwo() = surfaceColorAtElevation(TimePlannerRes.elevations.levelTwo)

@Composable
fun ColorScheme.surfaceThree() = surfaceColorAtElevation(TimePlannerRes.elevations.levelThree)

@Composable
fun ColorScheme.surfaceFour() = surfaceColorAtElevation(TimePlannerRes.elevations.levelFour)
@Composable
fun ColorScheme.surfaceFive() = surfaceColorAtElevation(TimePlannerRes.elevations.levelFive)
