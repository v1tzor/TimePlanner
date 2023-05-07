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
package ru.aleshin.core.utils.charts

import androidx.compose.ui.graphics.Color

/**
 * @author Stanislav Aleshin on 29.04.2023.
 */
object BarChartColorsDefaults {

    private val barChartColors = listOf(
        Color(0xFFFF7723),
        Color(0xFF0263FF),
        Color(0xFF8E30FF),
        Color(0xFF51A8B7),
        Color(0xFF7E6BA1),
        Color(0xFFC4E7D4),
        Color(0xFFD12137),
        Color(0xFF274210),
        Color(0xFF6582CC),
        Color(0xFF3237D8),
    )

    fun fetchColors() = barChartColors
}
