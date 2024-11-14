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

val blue_theme_light_primary = Color(0xFF275EA7)
val blue_theme_light_onPrimary = Color(0xFFFFFFFF)
val blue_theme_light_primaryContainer = Color(0xFFD6E3FF)
val blue_theme_light_onPrimaryContainer = Color(0xFF001B3D)
val blue_theme_light_secondary = Color(0xFF555F71)
val blue_theme_light_onSecondary = Color(0xFFFFFFFF)
val blue_theme_light_secondaryContainer = Color(0xFFD9E3F9)
val blue_theme_light_onSecondaryContainer = Color(0xFF121C2B)
val blue_theme_light_tertiary = Color(0xFF6F5675)
val blue_theme_light_onTertiary = Color(0xFFFFFFFF)
val blue_theme_light_tertiaryContainer = Color(0xFFF9D8FE)
val blue_theme_light_onTertiaryContainer = Color(0xFF28132F)
val blue_theme_light_error = Color(0xFFBA1A1A)
val blue_theme_light_errorContainer = Color(0xFFFFDAD6)
val blue_theme_light_onError = Color(0xFFFFFFFF)
val blue_theme_light_onErrorContainer = Color(0xFF410002)
val blue_theme_light_background = Color(0xFFFDFBFF)
val blue_theme_light_onBackground = Color(0xFF1A1B1E)
val blue_theme_light_outline = Color(0xFF74777F)
val blue_theme_light_inverseOnSurface = Color(0xFFF1F0F4)
val blue_theme_light_inverseSurface = Color(0xFF2F3033)
val blue_theme_light_inversePrimary = Color(0xFFA9C7FF)
val blue_theme_light_surfaceTint = Color(0xFF275EA7)
val blue_theme_light_outlineVariant = Color(0xFFC4C6CF)
val blue_theme_light_scrim = Color(0xFF000000)
val blue_theme_light_surface = Color(0xFFFAF9FD)
val blue_theme_light_onSurface = Color(0xFF1A1B1E)
val blue_theme_light_surfaceVariant = Color(0xFFE0E2EC)
val blue_theme_light_onSurfaceVariant = Color(0xFF43474E)

val blue_theme_dark_primary = Color(0xFFA9C7FF)
val blue_theme_dark_onPrimary = Color(0xFF003062)
val blue_theme_dark_primaryContainer = Color(0xFF00468B)
val blue_theme_dark_onPrimaryContainer = Color(0xFFD6E3FF)
val blue_theme_dark_secondary = Color(0xFFBDC7DC)
val blue_theme_dark_onSecondary = Color(0xFF273141)
val blue_theme_dark_secondaryContainer = Color(0xFF3E4758)
val blue_theme_dark_onSecondaryContainer = Color(0xFFD9E3F9)
val blue_theme_dark_tertiary = Color(0xFFDCBCE1)
val blue_theme_dark_onTertiary = Color(0xFF3E2845)
val blue_theme_dark_tertiaryContainer = Color(0xFF563E5C)
val blue_theme_dark_onTertiaryContainer = Color(0xFFF9D8FE)
val blue_theme_dark_error = Color(0xFFFFB4AB)
val blue_theme_dark_errorContainer = Color(0xFF93000A)
val blue_theme_dark_onError = Color(0xFF690005)
val blue_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val blue_theme_dark_background = Color(0xFF1A1B1E)
val blue_theme_dark_onBackground = Color(0xFFE3E2E6)
val blue_theme_dark_outline = Color(0xFF8E9099)
val blue_theme_dark_inverseOnSurface = Color(0xFF1A1B1E)
val blue_theme_dark_inverseSurface = Color(0xFFE3E2E6)
val blue_theme_dark_inversePrimary = Color(0xFF275EA7)
val blue_theme_dark_surfaceTint = Color(0xFFA9C7FF)
val blue_theme_dark_outlineVariant = Color(0xFF43474E)
val blue_theme_dark_scrim = Color(0xFF000000)
val blue_theme_dark_surface = Color(0xFF1A1B1E)
val blue_theme_dark_onSurface = Color(0xFFC7C6CA)
val blue_theme_dark_surfaceVariant = Color(0xFF43474E)
val blue_theme_dark_onSurfaceVariant = Color(0xFFC4C6CF)

val blueSeed = Color(0xFF5D7EB4)

val blueLightColorScheme = lightColorScheme(
    primary = blue_theme_light_primary,
    onPrimary = blue_theme_light_onPrimary,
    primaryContainer = blue_theme_light_primaryContainer,
    onPrimaryContainer = blue_theme_light_onPrimaryContainer,
    secondary = blue_theme_light_secondary,
    onSecondary = blue_theme_light_onSecondary,
    secondaryContainer = blue_theme_light_secondaryContainer,
    onSecondaryContainer = blue_theme_light_onSecondaryContainer,
    tertiary = blue_theme_light_tertiary,
    onTertiary = blue_theme_light_onTertiary,
    tertiaryContainer = blue_theme_light_tertiaryContainer,
    onTertiaryContainer = blue_theme_light_onTertiaryContainer,
    error = blue_theme_light_error,
    onError = blue_theme_light_onError,
    errorContainer = blue_theme_light_errorContainer,
    onErrorContainer = blue_theme_light_onErrorContainer,
    outline = blue_theme_light_outline,
    background = blue_theme_light_background,
    onBackground = blue_theme_light_onBackground,
    surface = blue_theme_light_surface,
    onSurface = blue_theme_light_onSurface,
    surfaceVariant = blue_theme_light_surfaceVariant,
    onSurfaceVariant = blue_theme_light_onSurfaceVariant,
    inverseSurface = blue_theme_light_inverseSurface,
    inverseOnSurface = blue_theme_light_inverseOnSurface,
    inversePrimary = blue_theme_light_inversePrimary,
    surfaceTint = blue_theme_light_surfaceTint,
    outlineVariant = blue_theme_light_outlineVariant,
    scrim = blue_theme_light_scrim,
)

val blueDarkColorScheme = darkColorScheme(
    primary = blue_theme_dark_primary,
    onPrimary = blue_theme_dark_onPrimary,
    primaryContainer = blue_theme_dark_primaryContainer,
    onPrimaryContainer = blue_theme_dark_onPrimaryContainer,
    secondary = blue_theme_dark_secondary,
    onSecondary = blue_theme_dark_onSecondary,
    secondaryContainer = blue_theme_dark_secondaryContainer,
    onSecondaryContainer = blue_theme_dark_onSecondaryContainer,
    tertiary = blue_theme_dark_tertiary,
    onTertiary = blue_theme_dark_onTertiary,
    tertiaryContainer = blue_theme_dark_tertiaryContainer,
    onTertiaryContainer = blue_theme_dark_onTertiaryContainer,
    error = blue_theme_dark_error,
    onError = blue_theme_dark_onError,
    errorContainer = blue_theme_dark_errorContainer,
    onErrorContainer = blue_theme_dark_onErrorContainer,
    outline = blue_theme_dark_outline,
    background = blue_theme_dark_background,
    onBackground = blue_theme_dark_onBackground,
    surface = blue_theme_dark_surface,
    onSurface = blue_theme_dark_onSurface,
    surfaceVariant = blue_theme_dark_surfaceVariant,
    onSurfaceVariant = blue_theme_dark_onSurfaceVariant,
    inverseSurface = blue_theme_dark_inverseSurface,
    inverseOnSurface = blue_theme_dark_inverseOnSurface,
    inversePrimary = blue_theme_dark_inversePrimary,
    surfaceTint = blue_theme_dark_surfaceTint,
    outlineVariant = blue_theme_dark_outlineVariant,
    scrim = blue_theme_dark_scrim,
)
