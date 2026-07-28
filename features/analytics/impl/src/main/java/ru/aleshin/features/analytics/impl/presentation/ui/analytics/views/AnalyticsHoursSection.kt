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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsWeekdayHourLoadUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsWeekdayHourHeatmap

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AnalyticsHoursSection(
    modifier: Modifier = Modifier,
    weekdayHourLoad: AnalyticsWeekdayHourLoadUi,
    fillAvailableHeight: Boolean = false,
) {
    AnalyticsSection(
        modifier = modifier,
        title = AnalyticsThemeRes.strings.busiestHoursTitle,
        fillAvailableHeight = fillAvailableHeight,
    ) {
        AnalyticsWeekdayHourHeatmap(
            modifier = Modifier.fillMaxWidth(),
            weekdayHourLoad = weekdayHourLoad,
        )
        Text(
            text = AnalyticsThemeRes.strings.averagePlannedMinutes,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
