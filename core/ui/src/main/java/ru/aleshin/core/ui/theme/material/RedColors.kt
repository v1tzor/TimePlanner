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
package ru.aleshin.core.ui.theme.material

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * @author Stanislav Aleshin on 06.10.2023.
 */

val red_theme_light_primary = Color(0xFF99405D)
val red_theme_light_onPrimary = Color(0xFFFFFFFF)
val red_theme_light_primaryContainer = Color(0xFFFFD9E1)
val red_theme_light_onPrimaryContainer = Color(0xFF3F001B)
val red_theme_light_secondary = Color(0xFF74565D)
val red_theme_light_onSecondary = Color(0xFFFFFFFF)
val red_theme_light_secondaryContainer = Color(0xFFFFD9E1)
val red_theme_light_onSecondaryContainer = Color(0xFF2B151B)
val red_theme_light_tertiary = Color(0xFF7B5734)
val red_theme_light_onTertiary = Color(0xFFFFFFFF)
val red_theme_light_tertiaryContainer = Color(0xFFFFDCBF)
val red_theme_light_onTertiaryContainer = Color(0xFF2D1600)
val red_theme_light_error = Color(0xFFBA1A1A)
val red_theme_light_errorContainer = Color(0xFFFFDAD6)
val red_theme_light_onError = Color(0xFFFFFFFF)
val red_theme_light_onErrorContainer = Color(0xFF410002)
val red_theme_light_background = Color(0xFFFFFBFF)
val red_theme_light_onBackground = Color(0xFF201A1B)
val red_theme_light_outline = Color(0xFF847376)
val red_theme_light_inverseOnSurface = Color(0xFFFAEEEF)
val red_theme_light_inverseSurface = Color(0xFF352F30)
val red_theme_light_inversePrimary = Color(0xFFFFB1C5)
val red_theme_light_surfaceTint = Color(0xFF99405D)
val red_theme_light_outlineVariant = Color(0xFFD6C2C5)
val red_theme_light_scrim = Color(0xFF000000)
val red_theme_light_surface = Color(0xFFFFF8F8)
val red_theme_light_onSurface = Color(0xFF201A1B)
val red_theme_light_surfaceVariant = Color(0xFFF3DDE1)
val red_theme_light_onSurfaceVariant = Color(0xFF514346)

val red_theme_dark_primary = Color(0xFFFFB1C5)
val red_theme_dark_onPrimary = Color(0xFF5E112F)
val red_theme_dark_primaryContainer = Color(0xFF7B2945)
val red_theme_dark_onPrimaryContainer = Color(0xFFFFD9E1)
val red_theme_dark_secondary = Color(0xFFE3BDC5)
val red_theme_dark_onSecondary = Color(0xFF422930)
val red_theme_dark_secondaryContainer = Color(0xFF5B3F46)
val red_theme_dark_onSecondaryContainer = Color(0xFFFFD9E1)
val red_theme_dark_tertiary = Color(0xFFEEBD92)
val red_theme_dark_onTertiary = Color(0xFF472A0A)
val red_theme_dark_tertiaryContainer = Color(0xFF61401F)
val red_theme_dark_onTertiaryContainer = Color(0xFFFFDCBF)
val red_theme_dark_error = Color(0xFFFFB4AB)
val red_theme_dark_errorContainer = Color(0xFF93000A)
val red_theme_dark_onError = Color(0xFF690005)
val red_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val red_theme_dark_background = Color(0xFF201A1B)
val red_theme_dark_onBackground = Color(0xFFECE0E1)
val red_theme_dark_outline = Color(0xFF9E8C90)
val red_theme_dark_inverseOnSurface = Color(0xFF201A1B)
val red_theme_dark_inverseSurface = Color(0xFFECE0E1)
val red_theme_dark_inversePrimary = Color(0xFF99405D)
val red_theme_dark_surfaceTint = Color(0xFFFFB1C5)
val red_theme_dark_outlineVariant = Color(0xFF514346)
val red_theme_dark_scrim = Color(0xFF000000)
val red_theme_dark_surface = Color(0xFF201A1B)
val red_theme_dark_onSurface = Color(0xFFCFC4C5)
val red_theme_dark_surfaceVariant = Color(0xFF514346)
val red_theme_dark_onSurfaceVariant = Color(0xFFD6C2C5)

val redSeed = Color(0xFFB35C76)

internal val redLightColorScheme = lightColorScheme(
    primary = red_theme_light_primary,
    onPrimary = red_theme_light_onPrimary,
    primaryContainer = red_theme_light_primaryContainer,
    onPrimaryContainer = red_theme_light_onPrimaryContainer,
    secondary = red_theme_light_secondary,
    onSecondary = red_theme_light_onSecondary,
    secondaryContainer = red_theme_light_secondaryContainer,
    onSecondaryContainer = red_theme_light_onSecondaryContainer,
    tertiary = red_theme_light_tertiary,
    onTertiary = red_theme_light_onTertiary,
    tertiaryContainer = red_theme_light_tertiaryContainer,
    onTertiaryContainer = red_theme_light_onTertiaryContainer,
    error = red_theme_light_error,
    onError = red_theme_light_onError,
    errorContainer = red_theme_light_errorContainer,
    onErrorContainer = red_theme_light_onErrorContainer,
    outline = red_theme_light_outline,
    background = red_theme_light_background,
    onBackground = red_theme_light_onBackground,
    surface = red_theme_light_surface,
    onSurface = red_theme_light_onSurface,
    surfaceVariant = red_theme_light_surfaceVariant,
    onSurfaceVariant = red_theme_light_onSurfaceVariant,
    inverseSurface = red_theme_light_inverseSurface,
    inverseOnSurface = red_theme_light_inverseOnSurface,
    inversePrimary = red_theme_light_inversePrimary,
    surfaceTint = red_theme_light_surfaceTint,
    outlineVariant = red_theme_light_outlineVariant,
    scrim = red_theme_light_scrim,
)

internal val redDarkColorScheme = darkColorScheme(
    primary = red_theme_dark_primary,
    onPrimary = red_theme_dark_onPrimary,
    primaryContainer = red_theme_dark_primaryContainer,
    onPrimaryContainer = red_theme_dark_onPrimaryContainer,
    secondary = red_theme_dark_secondary,
    onSecondary = red_theme_dark_onSecondary,
    secondaryContainer = red_theme_dark_secondaryContainer,
    onSecondaryContainer = red_theme_dark_onSecondaryContainer,
    tertiary = red_theme_dark_tertiary,
    onTertiary = red_theme_dark_onTertiary,
    tertiaryContainer = red_theme_dark_tertiaryContainer,
    onTertiaryContainer = red_theme_dark_onTertiaryContainer,
    error = red_theme_dark_error,
    onError = red_theme_dark_onError,
    errorContainer = red_theme_dark_errorContainer,
    onErrorContainer = red_theme_dark_onErrorContainer,
    outline = red_theme_dark_outline,
    background = red_theme_dark_background,
    onBackground = red_theme_dark_onBackground,
    surface = red_theme_dark_surface,
    onSurface = red_theme_dark_onSurface,
    surfaceVariant = red_theme_dark_surfaceVariant,
    onSurfaceVariant = red_theme_dark_onSurfaceVariant,
    inverseSurface = red_theme_dark_inverseSurface,
    inverseOnSurface = red_theme_dark_inverseOnSurface,
    inversePrimary = red_theme_dark_inversePrimary,
    surfaceTint = red_theme_dark_surfaceTint,
    outlineVariant = red_theme_dark_outlineVariant,
    scrim = red_theme_dark_scrim,
)