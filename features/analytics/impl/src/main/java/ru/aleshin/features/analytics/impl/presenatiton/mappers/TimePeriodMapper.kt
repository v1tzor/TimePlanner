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
package ru.aleshin.features.analytics.impl.presenatiton.mappers

import androidx.compose.runtime.Composable
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes

/**
 * @author Stanislav Aleshin on 22.04.2023.
 */
@Composable
internal fun TimePeriod.mapToString() = when (this) {
    TimePeriod.WEEK -> AnalyticsThemeRes.strings.weekTimePeriod
    TimePeriod.MONTH -> AnalyticsThemeRes.strings.monthTimePeriod
    TimePeriod.HALF_YEAR -> AnalyticsThemeRes.strings.halfYearTimePeriod
    TimePeriod.YEAR -> AnalyticsThemeRes.strings.yearTimePeriod
}
