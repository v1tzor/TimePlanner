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
package ru.aleshin.features.analytics.impl.presentation.theme.tokens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

/**
 * @author Stanislav Aleshin on 27.07.2026.
 */
internal object AnalyticsLayoutDefaults {

    val CompactContentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp)
    val AdaptiveContentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp)
    val ContentMaxWidth = 680.dp
    val SectionSpacing = 24.dp
    val RangeSpacing = 16.dp
    val SummaryRowHeight = 320.dp
    val CreationRegularityRowHeight = 320.dp
    val HoursDurationRowHeight = 320.dp
    val CategorySummaryRowHeight = 304.dp
    val CategoryDistributionRowHeight = 280.dp
}
