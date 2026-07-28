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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.presentation.mappers.toTitle
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsCreationDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsDonutSliceUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsDonutCenterLabel
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsDonutChart
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsValueFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import kotlin.math.abs

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun AnalyticsCreationSection(
    modifier: Modifier = Modifier,
    creationAnalytics: AnalyticsCreationDistributionUi,
    selectedBucketKey: Long?,
    fillAvailableHeight: Boolean = false,
    onSelect: (Long?) -> Unit,
) {
    val formatter = rememberAnalyticsValueFormatter()
    val colors = analyticsCreationColors()
    val strings = AnalyticsThemeRes.strings
    val slices = remember(creationAnalytics, colors, strings) {
        creationAnalytics.buckets.mapIndexed { index, bucket ->
            AnalyticsDonutSliceUi(
                key = bucket.type.ordinal.toLong(),
                label = bucket.type.toTitle(strings = strings),
                value = bucket.durationMillis.toFloat(),
                color = colors[index % colors.size],
                rawValue = bucket.durationMillis,
            )
        }
    }

    AnalyticsSection(
        title = strings.creationTitle,
        modifier = modifier,
        fillAvailableHeight = fillAvailableHeight,
        verticalSpacing = 12.dp,
    ) {
        if (creationAnalytics.qualifyingTaskCount == 0) {
            Text(
                text = strings.creationNoData,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            AnalyticsCreationDistribution(
                creationAnalytics = creationAnalytics,
                selectedBucketKey = selectedBucketKey,
                colors = colors,
                slices = slices,
                formatter = formatter,
                onSelect = onSelect,
            )
            AnalyticsPlanningLeadTime(
                leadTime = creationAnalytics.medianLeadTimeMillis ?: 0L,
                strings = strings,
                formatter = formatter,
            )
        }
    }
}

@Composable
private fun AnalyticsCreationDistribution(
    modifier: Modifier = Modifier,
    creationAnalytics: AnalyticsCreationDistributionUi,
    selectedBucketKey: Long?,
    colors: List<Color>,
    slices: List<AnalyticsDonutSliceUi>,
    formatter: AnalyticsValueFormatter,
    onSelect: (Long?) -> Unit,
) {
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }

    val donutContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier.size(136.dp),
            contentAlignment = Alignment.Center,
        ) {
            AnalyticsDonutChart(
                slices = slices,
                sliceDescription = { slice ->
                    "${slice.label}: ${
                        formatter.formatDuration(
                            durationMillis = checkNotNull(slice.rawValue),
                            hourSymbol = strings.hourShort,
                            minuteSymbol = strings.minuteShort,
                        )
                    }"
                },
                size = 136.dp,
                selectedKey = selectedBucketKey,
                onSelect = onSelect,
            )
            AnalyticsDonutCenterLabel(
                value = formatter.formatDuration(
                    durationMillis = creationAnalytics.totalDurationMillis,
                    hourSymbol = strings.hourShort,
                    minuteSymbol = strings.minuteShort,
                ),
                label = strings.included,
            )
        }
    }
    val legendContent: @Composable () -> Unit = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            creationAnalytics.buckets.forEachIndexed { index, bucket ->
                val bucketKey = bucket.type.ordinal.toLong()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            color = if (selectedBucketKey == bucketKey) {
                                MaterialTheme.colorScheme.surfaceContainer
                            } else {
                                Color.Transparent
                            },
                        )
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = colors[index % colors.size],
                                shape = CircleShape,
                            ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = bucket.type.toTitle(strings = strings),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatter.formatPercent(
                                value = bucket.share,
                                locale = locale,
                            ),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = formatter.formatDuration(
                                durationMillis = bucket.durationMillis,
                                hourSymbol = strings.hourShort,
                                minuteSymbol = strings.minuteShort,
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
    val fontScale = LocalDensity.current.fontScale

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val useVerticalLayout = maxWidth < 280.dp || maxWidth < 400.dp && fontScale >= 1.75f
        if (useVerticalLayout) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                donutContent()
                legendContent()
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                donutContent()
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    legendContent()
                }
            }
        }
    }
}

@Composable
private fun AnalyticsPlanningLeadTime(
    leadTime: Long,
    strings: AnalyticsStrings,
    formatter: AnalyticsValueFormatter,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = strings.medianPlanningAhead,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = when {
                        leadTime > 0L -> strings.beforeStartFormat.format(
                            formatter.formatDuration(
                                durationMillis = leadTime,
                                hourSymbol = strings.hourShort,
                                minuteSymbol = strings.minuteShort,
                            ),
                        )
                        leadTime < 0L -> strings.afterStartFormat.format(
                            formatter.formatDuration(
                                durationMillis = abs(leadTime),
                                hourSymbol = strings.hourShort,
                                minuteSymbol = strings.minuteShort,
                            ),
                        )
                        else -> strings.atStart
                    },
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun analyticsCreationColors(): List<Color> {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val error = MaterialTheme.colorScheme.error
    return remember(primary, secondary, tertiary, error) {
        listOf(primary, secondary, tertiary, error)
    }
}
