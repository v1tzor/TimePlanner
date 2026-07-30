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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsPlanSourceType
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsPlanSourceDistributionUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun AnalyticsPlanSourceSection(
    modifier: Modifier = Modifier,
    planSource: AnalyticsPlanSourceDistributionUi,
) {
    val formatter = rememberAnalyticsValueFormatter()
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }

    AnalyticsSection(
        title = AnalyticsThemeRes.strings.planSourceTitle,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        verticalSpacing = 0.dp,
    ) {
        planSource.buckets.forEachIndexed { index, bucket ->
            val isLinkedToTemplate = bucket.type == AnalyticsPlanSourceType.LINKED
            val iconContainerColor = if (isLinkedToTemplate) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            }
            val iconContentColor = if (isLinkedToTemplate) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onTertiaryContainer
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 88.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = iconContainerColor,
                            shape = MaterialTheme.shapes.medium,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(
                            if (isLinkedToTemplate) {
                                TimePlannerRes.icons.templateTab
                            } else {
                                TimePlannerRes.icons.add
                            },
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = iconContentColor,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isLinkedToTemplate) strings.withTemplate else strings.withoutTemplate,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = formatter.formatDuration(
                            durationMillis = bucket.durationMillis,
                            hourSymbol = strings.hourShort,
                            minuteSymbol = strings.minuteShort,
                        ),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatter.formatPercent(
                            value = bucket.share,
                            locale = locale,
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isLinkedToTemplate) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        },
                    )
                    Text(
                        text = strings.tasksFormat.format(bucket.taskCount),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (index != planSource.buckets.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}
