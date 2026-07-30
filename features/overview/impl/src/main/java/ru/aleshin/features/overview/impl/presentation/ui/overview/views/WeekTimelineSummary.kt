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
package ru.aleshin.features.overview.impl.presentation.ui.overview.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.features.overview.impl.presentation.models.overview.DaySummaryUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox
import ru.aleshin.timeplanner.core.ui.views.toMinutesOrHoursTitle
import java.text.NumberFormat

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun DaySummaryCards(
    isLoading: Boolean,
    daySummary: DaySummaryUi?,
    useCompactSummary: Boolean,
) {
    if (!useCompactSummary) {
        ExpandedDaySummary(
            isLoading = isLoading,
            daySummary = daySummary,
        )
        return
    }
    val showPlaceholder = isLoading || daySummary == null

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DaySummaryCard(
            modifier = Modifier.weight(1f),
            isLoading = showPlaceholder,
            icon = OverviewThemeRes.icons.duration,
            iconColor = MaterialTheme.colorScheme.tertiary,
            title = OverviewThemeRes.strings.freeTimeTitle,
            value = daySummary?.freeTime?.toDurationTitle().orEmpty(),
        )
        DaySummaryCard(
            modifier = Modifier.weight(1f),
            isLoading = showPlaceholder,
            icon = OverviewThemeRes.icons.schedule,
            iconColor = MaterialTheme.colorScheme.primary,
            title = OverviewThemeRes.strings.workloadTitle,
            value = daySummary?.workload?.toDurationTitle().orEmpty(),
        )
        DaySummaryCard(
            modifier = Modifier.weight(1f),
            isLoading = showPlaceholder,
            icon = OverviewThemeRes.icons.completedTask,
            iconColor = MaterialTheme.colorScheme.secondary,
            title = OverviewThemeRes.strings.progressTitle,
            value = remember(daySummary?.progress) {
                daySummary?.progress?.let { progress ->
                    NumberFormat.getPercentInstance().format(progress.coerceIn(0f, 1f))
                }.orEmpty()
            },
        )
    }
}

@Composable
private fun ExpandedDaySummary(
    isLoading: Boolean,
    daySummary: DaySummaryUi?,
) {
    val progressTitle = remember(daySummary?.progress) {
        daySummary?.progress?.let { progress ->
            NumberFormat.getPercentInstance().format(progress.coerceIn(0f, 1f))
        }.orEmpty()
    }

    AnimatedContent(
        targetState = isLoading || daySummary == null,
        label = "ExpandedDaySummary",
        transitionSpec = {
            fadeIn(animationSpec = tween(600, delayMillis = 90)).togetherWith(
                fadeOut(animationSpec = tween(300)),
            )
        },
    ) { loading ->
        if (loading) {
            PlaceholderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = MaterialTheme.shapes.large,
            )
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DaySummaryMetric(
                        modifier = Modifier.weight(1f),
                        icon = OverviewThemeRes.icons.duration,
                        iconColor = MaterialTheme.colorScheme.tertiary,
                        title = OverviewThemeRes.strings.freeTimeTitle,
                        value = daySummary?.freeTime?.toDurationTitle().orEmpty(),
                    )
                    VerticalDivider(modifier = Modifier.height(36.dp))
                    DaySummaryMetric(
                        modifier = Modifier.weight(1f),
                        icon = OverviewThemeRes.icons.schedule,
                        iconColor = MaterialTheme.colorScheme.primary,
                        title = OverviewThemeRes.strings.workloadTitle,
                        value = daySummary?.workload?.toDurationTitle().orEmpty(),
                    )
                    VerticalDivider(modifier = Modifier.height(36.dp))
                    DaySummaryMetric(
                        modifier = Modifier.weight(1f),
                        icon = OverviewThemeRes.icons.completedTask,
                        iconColor = MaterialTheme.colorScheme.secondary,
                        title = OverviewThemeRes.strings.progressTitle,
                        value = progressTitle,
                    )
                }
            }
        }
    }
}

@Composable
private fun DaySummaryMetric(
    modifier: Modifier = Modifier,
    icon: Int,
    iconColor: Color,
    title: String,
    value: String,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = iconColor,
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
private fun DaySummaryCard(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    icon: Int,
    iconColor: Color,
    title: String,
    value: String,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = isLoading,
        label = "DaySummaryCard",
        transitionSpec = {
            fadeIn(animationSpec = tween(600, delayMillis = 90)).togetherWith(
                fadeOut(animationSpec = tween(300)),
            )
        },
    ) { loading ->
        if (loading) {
            PlaceholderBox(
                modifier = modifier.height(64.dp),
                shape = MaterialTheme.shapes.large,
            )
        } else {
            Surface(
                modifier = modifier.height(64.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        tint = iconColor,
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = value,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Long.toDurationTitle(): String {
    return when {
        this <= 0L -> "0${TimePlannerRes.strings.hoursSymbol}"
        else -> toMinutesOrHoursTitle()
    }
}
