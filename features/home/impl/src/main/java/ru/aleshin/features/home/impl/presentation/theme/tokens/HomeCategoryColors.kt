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
package ru.aleshin.features.home.impl.presentation.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import ru.aleshin.core.utils.charts.CategoryColorsDefaults

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
@Immutable
internal data class HomeCategoryColors(
    val accent: Color,
    val container: Color,
)

internal fun fetchHomeCategoryColors(
    categoryId: Long,
    surface: Color,
): HomeCategoryColors {
    val isDarkTheme = surface.luminance() < 0.5f
    val accent = CategoryColorsDefaults.fetchColor(categoryId)
    val container = accent
        .copy(alpha = if (isDarkTheme) 0.32f else 0.24f)
        .compositeOver(surface)

    return HomeCategoryColors(
        accent = accent,
        container = container,
    )
}
