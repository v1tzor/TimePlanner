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
package ru.aleshin.timeplanner.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.tokens.TimePlannerLanguage
import ru.aleshin.timeplanner.presentation.theme.tokens.*

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
@Composable
fun MainTheme(content: @Composable () -> Unit) {
    val strings = when (TimePlannerRes.language) {
        TimePlannerLanguage.EN -> englishMainStrings
        TimePlannerLanguage.RU -> russianMainStrings
    }
    val icons = baseMainIcons

    CompositionLocalProvider(
        LocalMainStrings provides strings,
        LocalMainIcons provides icons,
        content = content,
    )
}
