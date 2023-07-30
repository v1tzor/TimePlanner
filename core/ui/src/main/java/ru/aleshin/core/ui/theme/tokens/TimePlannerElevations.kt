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
package ru.aleshin.core.ui.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @author Stanislav Aleshin on 23.02.2023.
 */
data class TimePlannerElevations(
    val levelZero: Dp,
    val levelOne: Dp,
    val levelTwo: Dp,
    val levelThree: Dp,
    val levelFour: Dp,
    val levelFive: Dp,
)

val baseTimePlannerElevations = TimePlannerElevations(
    levelZero = 0.dp,
    levelOne = 1.dp,
    levelTwo = 3.dp,
    levelThree = 6.dp,
    levelFour = 8.dp,
    levelFive = 12.dp,
)

val LocalTimePlannerElevations = staticCompositionLocalOf<TimePlannerElevations> {
    error("Elevations is not provided")
}
