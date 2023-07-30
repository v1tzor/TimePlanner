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
package ru.aleshin.features.analytics.impl.presenatiton.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.features.analytics.impl.presenatiton.theme.tokens.LocalAnalyticsIcons
import ru.aleshin.features.analytics.impl.presenatiton.theme.tokens.LocalAnalyticsStrings
import ru.aleshin.features.analytics.impl.presenatiton.theme.tokens.fetchAnalyticsIcons
import ru.aleshin.features.analytics.impl.presenatiton.theme.tokens.fetchAnalyticsStrings

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
@Composable
internal fun AnalyticsTheme(content: @Composable () -> Unit) {
    val strings = fetchAnalyticsStrings(TimePlannerRes.language)
    val icons = fetchAnalyticsIcons()

    CompositionLocalProvider(
        LocalAnalyticsStrings provides strings,
        LocalAnalyticsIcons provides icons,
        content = content,
    )
}
