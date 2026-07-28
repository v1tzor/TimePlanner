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
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.color.ColorProviders
import androidx.glance.currentState
import androidx.glance.layout.ContentScale
import androidx.glance.unit.ColorProvider
import ru.aleshin.core.utils.extensions.fetchLocale
import ru.aleshin.timeplanner.core.ui.theme.material.ThemeUiType
import ru.aleshin.timeplanner.core.ui.theme.tokens.LocalTimePlannerElevations
import ru.aleshin.timeplanner.core.ui.theme.tokens.LocalTimePlannerIcons
import ru.aleshin.timeplanner.core.ui.theme.tokens.LocalTimePlannerLanguage
import ru.aleshin.timeplanner.core.ui.theme.tokens.LocalTimePlannerStrings
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchAppElevations
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.timeplanner.widgets.R
import ru.aleshin.timeplanner.widgets.presentation.state.WidgetStateKeys
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetTypography
import kotlin.math.ln

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun WidgetTheme(
    context: Context,
    content: @Composable () -> Unit,
) {
    val languageCode = currentState(WidgetStateKeys.language) ?: context.fetchLocale().language
    val language = fetchWidgetLanguage(languageCode)
    val theme = currentState(WidgetStateKeys.theme)
        ?.let { value -> ThemeUiType.entries.firstOrNull { it.name == value } }
        ?: ThemeUiType.DEFAULT

    GlanceTheme(colors = WidgetGlanceColorScheme.fetch(context, theme)) {
        CompositionLocalProvider(
            LocalWidgetTypography provides WidgetTypography(),
            LocalTimePlannerLanguage provides language,
            LocalTimePlannerElevations provides fetchAppElevations(),
            LocalTimePlannerStrings provides fetchCoreStrings(language),
            LocalTimePlannerIcons provides fetchCoreIcons(),
            content = content,
        )
    }
}

@Composable
fun ColorProviders.surfaceColorAtElevation(
    elevation: Dp,
): ColorProvider {
    val context = LocalContext.current
    if (elevation == 0.dp) return surface
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    val surfaceColor = surface.getColor(context)
    val color = primary.getColor(context).copy(alpha = alpha).compositeOver(surfaceColor)
    return ColorProvider(color)
}

fun GlanceModifier.compatCornerBackground(
    color: ColorProvider,
    cornerRadius: Int,
): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        cornerRadius(cornerRadius.dp).background(color)
    } else {
        background(
            imageProvider = when (cornerRadius) {
                in 0..8 -> ImageProvider(R.drawable.rouned_background_8_dp)
                in 9..16 -> ImageProvider(R.drawable.rouned_background_16_dp)
                in 17..24 -> ImageProvider(R.drawable.rouned_background_24_dp)
                else -> ImageProvider(R.drawable.circular_background)
            },
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(color),
        )
    }
}

@Composable
fun GlanceTheme.widgetTypography(): WidgetTypography = LocalWidgetTypography.current

private val LocalWidgetTypography = compositionLocalOf<WidgetTypography> {
    error("Widget typography is not provided")
}
