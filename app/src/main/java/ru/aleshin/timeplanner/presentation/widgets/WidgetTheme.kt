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
package ru.aleshin.timeplanner.presentation.widgets

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.glance.text.FontWeight
import androidx.glance.text.TextDefaults
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import ru.aleshin.core.ui.theme.material.ColorsUiType
import ru.aleshin.core.ui.theme.tokens.LocalTimePlannerElevations
import ru.aleshin.core.ui.theme.tokens.LocalTimePlannerIcons
import ru.aleshin.core.ui.theme.tokens.LocalTimePlannerLanguage
import ru.aleshin.core.ui.theme.tokens.LocalTimePlannerStrings
import ru.aleshin.core.ui.theme.tokens.fetchAppElevations
import ru.aleshin.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.core.utils.extensions.fetchLocale
import ru.aleshin.timeplanner.R
import ru.aleshin.timeplanner.presentation.widgets.WidgetGlanceColorScheme.fetchColorScheme
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetReceiver.Companion.COLORS_TYPE_KEY
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetReceiver.Companion.DYNAMIC_COLOR
import kotlin.math.ln

/**
 * @author Stanislav Aleshin on 28.04.2024.
 */
@Composable
fun WidgetTheme(
    context: Context,
    content: @Composable () -> Unit,
) {
    val typography = GlanceTypography()
    val appLanguage = fetchCoreLanguage(context.fetchLocale().language)
    val coreStrings = fetchCoreStrings(appLanguage)
    val appElevations = fetchAppElevations()
    val colorsType = currentState(COLORS_TYPE_KEY)?.let { ColorsUiType.valueOf(it) } ?: ColorsUiType.PINK
    val dynamicColors = currentState(DYNAMIC_COLOR) ?: false
    val coreIcons = fetchCoreIcons()

    GlanceTheme(colors = fetchColorScheme(colorsType, dynamicColors)) {
        CompositionLocalProvider(
            LocalGlanceTypography provides typography,
            LocalTimePlannerLanguage provides appLanguage,
            LocalTimePlannerElevations provides appElevations,
            LocalTimePlannerStrings provides coreStrings,
            LocalTimePlannerIcons provides coreIcons,
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

data class GlanceTypography(
    val titleLarge: TextStyle = TextDefaults.defaultTextStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
    ),
    val titleMedium: TextStyle = TextDefaults.defaultTextStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    val titleSmall: TextStyle = TextDefaults.defaultTextStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
)

fun GlanceModifier.compatCornerBackground(
    color: ColorProvider,
    cornerRadius: Int,
): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.cornerRadius(cornerRadius.dp).background(color)
    } else {
        this.background(
            imageProvider = when (cornerRadius) {
                in 0..8 -> ImageProvider(R.drawable.rouned_background_8_dp)
                in 9..16 -> ImageProvider(R.drawable.rouned_background_16_dp)
                in 17..24 -> ImageProvider(R.drawable.rouned_background_24_dp)
                else -> ImageProvider(R.drawable.circular_background)
            },
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
fun GlanceTheme.typography() = LocalGlanceTypography.current

val LocalGlanceTypography = compositionLocalOf<GlanceTypography> {
    error("Glance typography is not provided")
}