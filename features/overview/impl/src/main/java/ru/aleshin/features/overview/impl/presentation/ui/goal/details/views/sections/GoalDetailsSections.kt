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
package ru.aleshin.features.overview.impl.presentation.ui.goal.details.views.sections

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalProgressStatus
import ru.aleshin.features.overview.impl.presentation.models.GoalDetailsUi
import ru.aleshin.features.overview.impl.presentation.models.GoalProgressUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.common.toGoalDurationTitle
import java.text.DateFormat

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun GoalSummarySection(
    modifier: Modifier = Modifier,
    details: GoalDetailsUi,
) {
    val progress = details.progress
    val dateFormat = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    val statusTitle = when (progress.status) {
        GoalProgressStatus.IN_PROGRESS -> OverviewThemeRes.strings.inProgressTitle
        GoalProgressStatus.ACHIEVED -> OverviewThemeRes.strings.achievedTitle
        GoalProgressStatus.EXCEEDED -> OverviewThemeRes.strings.exceededTitle
        GoalProgressStatus.EXPIRED -> OverviewThemeRes.strings.expiredTitle
        GoalProgressStatus.UNAVAILABLE -> OverviewThemeRes.strings.scopeUnavailableTitle
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = progress.goal.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "${dateFormat.format(progress.goal.createdAt)} — " +
                    dateFormat.format(progress.goal.deadline),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            LinearProgressIndicator(
                progress = { progress.progressFraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                drawStopIndicator = {},
            )
            Text(
                text = statusTitle,
                color = if (
                    progress.status == GoalProgressStatus.EXCEEDED ||
                    progress.status == GoalProgressStatus.EXPIRED
                ) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
internal fun GoalMetricsSection(
    modifier: Modifier = Modifier,
    details: GoalDetailsUi,
) {
    val progress = details.progress

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GoalMetricCard(
                modifier = Modifier.weight(1f),
                icon = OverviewThemeRes.icons.completedTask,
                title = OverviewThemeRes.strings.actualTitle,
                value = progress.formatValue(progress.actualValue),
            )
            GoalMetricCard(
                modifier = Modifier.weight(1f),
                icon = OverviewThemeRes.icons.schedule,
                title = OverviewThemeRes.strings.plannedTitle,
                value = progress.formatValue(progress.plannedValue),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GoalMetricCard(
                modifier = Modifier.weight(1f),
                icon = OverviewThemeRes.icons.goalTarget,
                title = OverviewThemeRes.strings.targetTitle,
                value = progress.formatValue(progress.goal.targetValue),
            )
            GoalMetricCard(
                modifier = Modifier.weight(1f),
                icon = OverviewThemeRes.icons.unexecutedTask,
                title = OverviewThemeRes.strings.remainingTitle,
                value = progress.formatValue(progress.remainingValue),
            )
        }
    }
}

@Composable
private fun GoalMetricCard(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    title: String,
    value: String,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
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
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
internal fun GoalTasksEmptyCard(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(OverviewThemeRes.icons.schedule),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
            Text(
                modifier = Modifier.weight(1f),
                text = OverviewThemeRes.strings.emptyGoalTasksTitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun GoalProgressUi.formatValue(value: Long): String {
    return when (goal.metric) {
        GoalMetric.DURATION -> value.toGoalDurationTitle()
        GoalMetric.TASK_COUNT -> "$value ${OverviewThemeRes.strings.tasksUnit}"
    }
}
