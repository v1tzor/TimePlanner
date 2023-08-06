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
package ru.aleshin.core.ui.theme

import androidx.compose.runtime.Composable
import ru.aleshin.core.ui.theme.tokens.*

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
object TimePlannerRes {

    val elevations: TimePlannerElevations
        @Composable get() = LocalTimePlannerElevations.current

    val colorsType: TimePlannerColorsType
        @Composable get() = LocalTimePlannerColorsType.current

    val language: TimePlannerLanguage
        @Composable get() = LocalTimePlannerLanguage.current

    val strings: TimePlannerStrings
        @Composable get() = LocalTimePlannerStrings.current

    val icons: TimePlannerIcons
        @Composable get() = LocalTimePlannerIcons.current
}
