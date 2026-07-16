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
package ru.aleshin.core.utils.charts

import androidx.compose.ui.graphics.Color

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
object CategoryColorsDefaults {

    private val categoryColors = listOf(
        Color(0xFFC71CCB),
        Color(0xFFF8B32D),
        Color(0xFFE0C900),
        Color(0xFF83A900),
        Color(0xFF00A979),
        Color(0xFF008BCB),
        Color(0xFF526DDA),
        Color(0xFF8E5BC7),
        Color(0xFFD14D72),
        Color(0xFFB5632C),
        Color(0xFF006E75),
        Color(0xFF6D5EBA),
        Color(0xFFD55E00),
        Color(0xFF467D35),
        Color(0xFFB13C8B),
        Color(0xFF3D7196),
        Color(0xFF9A6B00),
        Color(0xFF008F70),
        Color(0xFFA14F3D),
        Color(0xFF5D6FBC),
        Color(0xFF6E7E1D),
        Color(0xFFB8435C),
        Color(0xFF007D9C),
        Color(0xFF87507E),
    )

    fun fetchColor(categoryId: Long): Color {
        val colorIndex = Math.floorMod(categoryId - 1L, categoryColors.size.toLong()).toInt()
        return categoryColors[colorIndex]
    }
}
