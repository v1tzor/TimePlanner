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
 * imitations under the License.
 */
package ru.aleshin.features.analytics.impl.presenatiton.theme

import androidx.compose.runtime.Composable
import ru.aleshin.features.analytics.impl.presenatiton.theme.tokens.AnalyticsIcons
import ru.aleshin.features.analytics.impl.presenatiton.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presenatiton.theme.tokens.LocalAnalyticsIcons
import ru.aleshin.features.analytics.impl.presenatiton.theme.tokens.LocalAnalyticsStrings

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
internal object AnalyticsThemeRes {

    val strings: AnalyticsStrings
        @Composable get() = LocalAnalyticsStrings.current

    val icons: AnalyticsIcons
        @Composable get() = LocalAnalyticsIcons.current
}
