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

import androidx.compose.runtime.Composable
import androidx.glance.color.ColorProviders
import androidx.glance.color.DynamicThemeColorProviders
import androidx.glance.material3.ColorProviders
import ru.aleshin.core.ui.theme.material.ColorsUiType
import ru.aleshin.core.ui.theme.material.blueDarkColorScheme
import ru.aleshin.core.ui.theme.material.blueLightColorScheme
import ru.aleshin.core.ui.theme.material.pinkDarkColorScheme
import ru.aleshin.core.ui.theme.material.pinkLightColorScheme
import ru.aleshin.core.ui.theme.material.purpleDarkColorScheme
import ru.aleshin.core.ui.theme.material.purpleLightColorScheme
import ru.aleshin.core.ui.theme.material.redDarkColorScheme
import ru.aleshin.core.ui.theme.material.redLightColorScheme

/**
 * @author Stanislav Aleshin on 28.04.2024.
 */
object WidgetGlanceColorScheme {

    val pink = ColorProviders(
        light = pinkLightColorScheme,
        dark = pinkDarkColorScheme,
    )

    val red = ColorProviders(
        light = redLightColorScheme,
        dark = redDarkColorScheme,
    )

    val purple = ColorProviders(
        light = purpleLightColorScheme,
        dark = purpleDarkColorScheme,
    )

    val blue = ColorProviders(
        light = blueLightColorScheme,
        dark = blueDarkColorScheme,
    )

    @Composable
    fun fetchColorSchemeByColorsType(colors: ColorsUiType?) = when (colors) {
        ColorsUiType.RED -> red
        ColorsUiType.PINK -> pink
        ColorsUiType.PURPLE -> purple
        ColorsUiType.BLUE -> blue
        else -> pink
    }

    @Composable
    fun fetchColorScheme(colors: ColorsUiType?, isDynamic: Boolean): ColorProviders {
        return if (isDynamic) {
            DynamicThemeColorProviders
        } else {
            fetchColorSchemeByColorsType(colors = colors)
        }
    }
}