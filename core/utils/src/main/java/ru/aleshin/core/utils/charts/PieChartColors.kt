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
 * @author Stanislav Aleshin on 22.04.2023.
 */
data class PieChartColors(
    val topOne: Color = Color(0xFFC71CCB),
    val topTwo: Color = Color(0xFFF8B32D),
    val topThree: Color = Color(0xFFFFE600),
    val topFour: Color = Color(0xFF9ABD10),
    val topFive: Color = Color(0xFF1DD79D),
    val other: Color = Color(0xFF00A3FF),
) {
    fun fetchColorByTopIndex(topIndex: Int) = when (topIndex) {
        0 -> topOne
        1 -> topTwo
        2 -> topThree
        3 -> topFour
        4 -> topFive
        else -> other
    }

    companion object {
        fun default() = PieChartColors()
    }
}

fun fetchPieColorByTop(index: Int): Color {
    val pieColors = PieChartColors.default()
    return pieColors.fetchColorByTopIndex(index)
}
