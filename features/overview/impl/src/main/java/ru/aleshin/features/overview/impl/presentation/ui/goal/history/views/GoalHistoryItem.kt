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
package ru.aleshin.features.overview.impl.presentation.ui.goal.history.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.features.overview.impl.presentation.models.GoalHistoryUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.common.toGoalDurationTitle
import java.text.DateFormat

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun GoalHistoryItem(
    modifier: Modifier = Modifier,
    history: GoalHistoryUi,
) {
    val strings = OverviewThemeRes.goalStrings
    val dateFormat = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }
    val progress = if (history.targetValue == 0L) {
        0f
    } else {
        history.actualValue.toFloat() / history.targetValue.toFloat()
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = history.goalTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "${dateFormat.format(history.periodStart)} — " +
                            dateFormat.format(history.periodEnd),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    text = if (history.isAchieved) {
                        strings.achievedTitle
                    } else {
                        strings.notAchievedTitle
                    },
                    color = if (history.isAchieved) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                drawStopIndicator = {},
            )
            Text(
                text = "${history.formatValue(history.actualValue)} / " +
                    history.formatValue(history.targetValue),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun GoalHistoryUi.formatValue(value: Long): String {
    return when (metric) {
        GoalMetric.DURATION -> value.toGoalDurationTitle()
        GoalMetric.TASK_COUNT -> "$value ${OverviewThemeRes.goalStrings.tasksUnit}"
    }
}
