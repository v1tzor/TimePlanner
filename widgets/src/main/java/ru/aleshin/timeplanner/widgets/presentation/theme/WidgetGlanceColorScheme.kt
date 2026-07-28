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
package ru.aleshin.timeplanner.widgets.presentation.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProviders
import androidx.glance.material3.ColorProviders
import ru.aleshin.timeplanner.core.ui.theme.material.ThemeUiType

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
object WidgetGlanceColorScheme {

    fun fetch(context: Context, theme: ThemeUiType): ColorProviders {
        val systemDark = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val dark = when (theme) {
            ThemeUiType.DEFAULT -> systemDark
            ThemeUiType.LIGHT -> false
            ThemeUiType.DARK -> true
        }
        val colorScheme = if (dark) DARK_COLOR_SCHEME else LIGHT_COLOR_SCHEME
        return ColorProviders(light = colorScheme, dark = colorScheme)
    }
}

private val LIGHT_COLOR_SCHEME = lightColorScheme(
    primary = Color(0xFFC2185B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9E4),
    onPrimaryContainer = Color(0xFF3F001D),
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF386A20),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB7F397),
    onTertiaryContainer = Color(0xFF082100),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1B1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFF0EEF2),
    onSurfaceVariant = Color(0xFF47464F),
    outline = Color(0xFF777680),
    outlineVariant = Color(0xFFC8C5CA),
)

private val DARK_COLOR_SCHEME = darkColorScheme(
    primary = Color(0xFFFFB0C8),
    onPrimary = Color(0xFF650033),
    primaryContainer = Color(0xFF8E004A),
    onPrimaryContainer = Color(0xFFFFD9E4),
    secondary = Color(0xFFCBC2DB),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFF9CD67D),
    onTertiary = Color(0xFF143800),
    tertiaryContainer = Color(0xFF25520B),
    onTertiaryContainer = Color(0xFFB7F397),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF17171A),
    onBackground = Color(0xFFE5E1E6),
    surface = Color(0xFF17171A),
    onSurface = Color(0xFFE5E1E6),
    surfaceVariant = Color(0xFF302F34),
    onSurfaceVariant = Color(0xFFC9C5CA),
    outline = Color(0xFF918F99),
    outlineVariant = Color(0xFF47464F),
)
