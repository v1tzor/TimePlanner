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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRegularityUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsComparisonLabel
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsRegularityCalendar
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsValueFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import java.util.Locale
import kotlin.math.abs

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun AnalyticsRegularitySection(
    modifier: Modifier = Modifier,
    range: AnalyticsRangeUi,
    regularity: AnalyticsRegularityUi,
    fillAvailableHeight: Boolean = false,
) {
    val formatter = rememberAnalyticsValueFormatter()
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }

    AnalyticsSection(
        title = AnalyticsThemeRes.strings.regularityTitle,
        modifier = modifier,
        fillAvailableHeight = fillAvailableHeight,
    ) {
        val fontScale = LocalDensity.current.fontScale
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val useVerticalLayout = maxWidth < 400.dp && fontScale >= 1.75f
            if (useVerticalLayout) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AnalyticsRegularityCalendar(
                        range = range,
                        activeDates = regularity.activeDates,
                        locale = locale,
                        strings = strings,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                    AnalyticsRegularityMetrics(
                        regularity = regularity,
                        locale = locale,
                        strings = strings,
                        formatter = formatter,
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnalyticsRegularityCalendar(
                        range = range,
                        activeDates = regularity.activeDates,
                        locale = locale,
                        strings = strings,
                        modifier = Modifier.weight(1.65f),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    AnalyticsRegularityMetrics(
                        regularity = regularity,
                        locale = locale,
                        strings = strings,
                        formatter = formatter,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsRegularityMetrics(
    modifier: Modifier = Modifier,
    regularity: AnalyticsRegularityUi,
    locale: Locale,
    strings: AnalyticsStrings,
    formatter: AnalyticsValueFormatter,
) {
    val activeDayCount = regularity.activeDayCount
    val totalDayCount = regularity.totalDayCount
    val deltaDayCount = abs(regularity.activeDayDelta)
    val activeDayUnit = remember(activeDayCount, locale, strings.dayUnit) {
        formatter.formatDayUnit(
            count = activeDayCount,
            locale = locale,
            unitForms = strings.dayUnit,
        )
    }
    val periodDayUnit = remember(totalDayCount, locale, strings.dayUnit) {
        formatter.formatDayUnit(
            count = totalDayCount,
            locale = locale,
            unitForms = strings.dayUnit,
        )
    }
    val deltaDayUnit = remember(deltaDayCount, locale, strings.dayUnit) {
        formatter.formatDayUnit(
            count = deltaDayCount,
            locale = locale,
            unitForms = strings.dayUnit,
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = activeDayCount.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = activeDayUnit,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = strings.periodDaysFormat.format(
                totalDayCount,
                periodDayUnit,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        AnalyticsComparisonLabel(
            value = when {
                regularity.activeDayDelta > 0 -> {
                    strings.activeDayIncreaseFormat.format(deltaDayCount, deltaDayUnit)
                }
                regularity.activeDayDelta < 0 -> {
                    strings.activeDayDecreaseFormat.format(deltaDayCount, deltaDayUnit)
                }
                else -> strings.activeDayUnchanged
            },
            isPositive = regularity.activeDayDelta
                .takeIf { it != 0 }
                ?.let { it > 0 },
        )
    }
}
