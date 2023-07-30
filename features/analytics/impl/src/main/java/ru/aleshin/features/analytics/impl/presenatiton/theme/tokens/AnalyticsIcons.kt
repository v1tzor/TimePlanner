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
package ru.aleshin.features.analytics.impl.presenatiton.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.features.analytics.impl.R

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
internal data class AnalyticsIcons(
    val barChart: Int,
    val pieChart: Int,
    val numberedList: Int,
    val numericOneCircle: Int,
    val timeCheck: Int,
    val timeComplete: Int,
)

internal val baseAnalyticsIcons = AnalyticsIcons(
    barChart = R.drawable.ic_bar_chart,
    pieChart = R.drawable.ic_pie_chart,
    numberedList = R.drawable.ic_list_numbered,
    numericOneCircle = R.drawable.ic_numeric_1_circle,
    timeCheck = R.drawable.ic_timer_check,
    timeComplete = R.drawable.ic_time_complete,
)

internal val LocalAnalyticsIcons = staticCompositionLocalOf<AnalyticsIcons> {
    error("Analytics Icons is not provided")
}

internal fun fetchAnalyticsIcons() = baseAnalyticsIcons
