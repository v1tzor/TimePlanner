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
package ru.aleshin.features.home.impl.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.features.home.impl.presentation.theme.tokens.*
import ru.aleshin.features.home.impl.presentation.theme.tokens.LocalHomeIcons
import ru.aleshin.features.home.impl.presentation.theme.tokens.LocalHomeStrings

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Composable
internal fun HomeTheme(content: @Composable () -> Unit) {
    val strings = fetchHomeStrings(TimePlannerRes.language)
    val icons = fetchHomeIcons()

    CompositionLocalProvider(
        LocalHomeStrings provides strings,
        LocalHomeIcons provides icons,
        content = content,
    )
}
