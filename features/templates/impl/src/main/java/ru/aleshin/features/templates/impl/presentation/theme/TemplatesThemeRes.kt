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
package ru.aleshin.features.templates.impl.presentation.theme

import androidx.compose.runtime.Composable
import ru.aleshin.features.templates.impl.presentation.theme.tokens.LocalTemplatesIcons
import ru.aleshin.features.templates.impl.presentation.theme.tokens.LocalTemplatesStrings
import ru.aleshin.features.templates.impl.presentation.theme.tokens.TemplatesIcons
import ru.aleshin.features.templates.impl.presentation.theme.tokens.TemplatesPatternStrings
import ru.aleshin.features.templates.impl.presentation.theme.tokens.TemplatesStrings
import ru.aleshin.features.templates.impl.presentation.theme.tokens.LocalTemplatesPatternStrings

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
internal object TemplatesThemeRes {

    val strings: TemplatesStrings
        @Composable get() = LocalTemplatesStrings.current

    val patternStrings: TemplatesPatternStrings
        @Composable get() = LocalTemplatesPatternStrings.current

    val icons: TemplatesIcons
        @Composable get() = LocalTemplatesIcons.current
}
