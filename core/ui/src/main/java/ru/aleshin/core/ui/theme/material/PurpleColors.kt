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

val purple_theme_light_primary = Color(0xFF5D53A7)
val purple_theme_light_onPrimary = Color(0xFFFFFFFF)
val purple_theme_light_primaryContainer = Color(0xFFE4DFFF)
val purple_theme_light_onPrimaryContainer = Color(0xFF180362)
val purple_theme_light_secondary = Color(0xFF5F5C71)
val purple_theme_light_onSecondary = Color(0xFFFFFFFF)
val purple_theme_light_secondaryContainer = Color(0xFFE5DFF9)
val purple_theme_light_onSecondaryContainer = Color(0xFF1B192C)
val purple_theme_light_tertiary = Color(0xFF7B5265)
val purple_theme_light_onTertiary = Color(0xFFFFFFFF)
val purple_theme_light_tertiaryContainer = Color(0xFFFFD8E7)
val purple_theme_light_onTertiaryContainer = Color(0xFF301121)
val purple_theme_light_error = Color(0xFFBA1A1A)
val purple_theme_light_errorContainer = Color(0xFFFFDAD6)
val purple_theme_light_onError = Color(0xFFFFFFFF)
val purple_theme_light_onErrorContainer = Color(0xFF410002)
val purple_theme_light_background = Color(0xFFFFFBFF)
val purple_theme_light_onBackground = Color(0xFF1C1B1F)
val purple_theme_light_outline = Color(0xFF78767F)
val purple_theme_light_inverseOnSurface = Color(0xFFF4EFF4)
val purple_theme_light_inverseSurface = Color(0xFF313034)
val purple_theme_light_inversePrimary = Color(0xFFC7BFFF)
val purple_theme_light_surfaceTint = Color(0xFF5D53A7)
val purple_theme_light_outlineVariant = Color(0xFFC9C5D0)
val purple_theme_light_scrim = Color(0xFF000000)
val purple_theme_light_surface = Color(0xFFFCF8FD)
val purple_theme_light_onSurface = Color(0xFF1C1B1F)
val purple_theme_light_surfaceVariant = Color(0xFFE5E1EC)
val purple_theme_light_onSurfaceVariant = Color(0xFF47464F)

val purple_theme_dark_primary = Color(0xFFC7BFFF)
val purple_theme_dark_onPrimary = Color(0xFF2E2176)
val purple_theme_dark_primaryContainer = Color(0xFF453A8E)
val purple_theme_dark_onPrimaryContainer = Color(0xFFE4DFFF)
val purple_theme_dark_secondary = Color(0xFFC8C3DC)
val purple_theme_dark_onSecondary = Color(0xFF302E41)
val purple_theme_dark_secondaryContainer = Color(0xFF474459)
val purple_theme_dark_onSecondaryContainer = Color(0xFFE5DFF9)
val purple_theme_dark_tertiary = Color(0xFFECB8CE)
val purple_theme_dark_onTertiary = Color(0xFF482537)
val purple_theme_dark_tertiaryContainer = Color(0xFF613B4D)
val purple_theme_dark_onTertiaryContainer = Color(0xFFFFD8E7)
val purple_theme_dark_error = Color(0xFFFFB4AB)
val purple_theme_dark_errorContainer = Color(0xFF93000A)
val purple_theme_dark_onError = Color(0xFF690005)
val purple_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val purple_theme_dark_background = Color(0xFF1C1B1F)
val purple_theme_dark_onBackground = Color(0xFFE5E1E6)
val purple_theme_dark_outline = Color(0xFF928F99)
val purple_theme_dark_inverseOnSurface = Color(0xFF1C1B1F)
val purple_theme_dark_inverseSurface = Color(0xFFE5E1E6)
val purple_theme_dark_inversePrimary = Color(0xFF5D53A7)
val purple_theme_dark_surfaceTint = Color(0xFFC7BFFF)
val purple_theme_dark_outlineVariant = Color(0xFF47464F)
val purple_theme_dark_scrim = Color(0xFF000000)
val purple_theme_dark_surface = Color(0xFF1C1B1F)
val purple_theme_dark_onSurface = Color(0xFFC9C5CA)
val purple_theme_dark_surfaceVariant = Color(0xFF47464F)
val purple_theme_dark_onSurfaceVariant = Color(0xFFC9C5D0)

val purpleSeed = Color(0xFF6C65A2)

internal val purpleLightColorScheme = lightColorScheme(
    primary = purple_theme_light_primary,
    onPrimary = purple_theme_light_onPrimary,
    primaryContainer = purple_theme_light_primaryContainer,
    onPrimaryContainer = purple_theme_light_onPrimaryContainer,
    secondary = purple_theme_light_secondary,
    onSecondary = purple_theme_light_onSecondary,
    secondaryContainer = purple_theme_light_secondaryContainer,
    onSecondaryContainer = purple_theme_light_onSecondaryContainer,
    tertiary = purple_theme_light_tertiary,
    onTertiary = purple_theme_light_onTertiary,
    tertiaryContainer = purple_theme_light_tertiaryContainer,
    onTertiaryContainer = purple_theme_light_onTertiaryContainer,
    error = purple_theme_light_error,
    onError = purple_theme_light_onError,
    errorContainer = purple_theme_light_errorContainer,
    onErrorContainer = purple_theme_light_onErrorContainer,
    outline = purple_theme_light_outline,
    background = purple_theme_light_background,
    onBackground = purple_theme_light_onBackground,
    surface = purple_theme_light_surface,
    onSurface = purple_theme_light_onSurface,
    surfaceVariant = purple_theme_light_surfaceVariant,
    onSurfaceVariant = purple_theme_light_onSurfaceVariant,
    inverseSurface = purple_theme_light_inverseSurface,
    inverseOnSurface = purple_theme_light_inverseOnSurface,
    inversePrimary = purple_theme_light_inversePrimary,
    surfaceTint = purple_theme_light_surfaceTint,
    outlineVariant = purple_theme_light_outlineVariant,
    scrim = purple_theme_light_scrim,
)

internal val purpleDarkColorScheme = darkColorScheme(
    primary = purple_theme_dark_primary,
    onPrimary = purple_theme_dark_onPrimary,
    primaryContainer = purple_theme_dark_primaryContainer,
    onPrimaryContainer = purple_theme_dark_onPrimaryContainer,
    secondary = purple_theme_dark_secondary,
    onSecondary = purple_theme_dark_onSecondary,
    secondaryContainer = purple_theme_dark_secondaryContainer,
    onSecondaryContainer = purple_theme_dark_onSecondaryContainer,
    tertiary = purple_theme_dark_tertiary,
    onTertiary = purple_theme_dark_onTertiary,
    tertiaryContainer = purple_theme_dark_tertiaryContainer,
    onTertiaryContainer = purple_theme_dark_onTertiaryContainer,
    error = purple_theme_dark_error,
    onError = purple_theme_dark_onError,
    errorContainer = purple_theme_dark_errorContainer,
    onErrorContainer = purple_theme_dark_onErrorContainer,
    outline = purple_theme_dark_outline,
    background = purple_theme_dark_background,
    onBackground = purple_theme_dark_onBackground,
    surface = purple_theme_dark_surface,
    onSurface = purple_theme_dark_onSurface,
    surfaceVariant = purple_theme_dark_surfaceVariant,
    onSurfaceVariant = purple_theme_dark_onSurfaceVariant,
    inverseSurface = purple_theme_dark_inverseSurface,
    inverseOnSurface = purple_theme_dark_inverseOnSurface,
    inversePrimary = purple_theme_dark_inversePrimary,
    surfaceTint = purple_theme_dark_surfaceTint,
    outlineVariant = purple_theme_dark_outlineVariant,
    scrim = purple_theme_dark_scrim,
)
