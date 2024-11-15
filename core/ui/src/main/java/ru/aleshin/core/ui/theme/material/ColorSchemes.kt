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

import android.os.Build
import android.os.Parcelable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.parcelize.Parcelize

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Parcelize
enum class ThemeUiType : Parcelable {
    DEFAULT, LIGHT, DARK;

    @Composable
    fun isDarkTheme() = when (this) {
        DEFAULT -> isSystemInDarkTheme()
        LIGHT -> false
        DARK -> true
    }
}

@Parcelize
enum class ColorsUiType : Parcelable {
    RED, PINK, PURPLE, BLUE;

    fun seed() = when (this) {
        RED -> redSeed
        PINK -> pinkSeed
        PURPLE -> purpleSeed
        BLUE -> blueSeed
    }

    fun onSeed() = when (this) {
        RED -> red_theme_light_primary
        PINK -> pink_theme_light_primary
        PURPLE -> purple_theme_light_primary
        BLUE -> blue_theme_light_primary
    }
    
    @Composable
    fun fetchLightColorScheme() = when (this) {
        RED -> redLightColorScheme
        PINK -> pinkLightColorScheme
        PURPLE -> purpleLightColorScheme
        BLUE -> blueLightColorScheme
    }

    @Composable
    fun fetchDarkColorScheme() = when (this) {
        RED -> redDarkColorScheme
        PINK -> pinkDarkColorScheme
        PURPLE -> purpleDarkColorScheme
        BLUE -> blueDarkColorScheme
    }

    @Composable
    fun fetchColorScheme(themeType: ThemeUiType) = when (themeType) {
        ThemeUiType.DEFAULT -> if (isSystemInDarkTheme()) fetchDarkColorScheme() else fetchLightColorScheme()
        ThemeUiType.LIGHT -> fetchLightColorScheme()
        ThemeUiType.DARK -> fetchDarkColorScheme()
    }
}

@Composable
fun ThemeUiType.toColorScheme(
    dynamicColor: Boolean,
    colors: ColorsUiType,
): ColorScheme {
    val context = LocalContext.current
    return if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        when (this) {
            ThemeUiType.DEFAULT -> if (isSystemInDarkTheme()) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
            ThemeUiType.LIGHT -> dynamicLightColorScheme(context)
            ThemeUiType.DARK -> dynamicDarkColorScheme(context)
        }
    } else {
        colors.fetchColorScheme(themeType = this)
    }
}