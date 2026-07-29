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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalProgressStatus
import ru.aleshin.core.presentation.mappers.mapToIconPainter
import ru.aleshin.features.overview.impl.presentation.models.GoalProgressUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.theme.tokens.fetchOverviewCategoryColors
import ru.aleshin.features.overview.impl.presentation.ui.common.toGoalDurationTitle
import ru.aleshin.timeplanner.core.ui.views.CategoryIconMonogram
import ru.aleshin.timeplanner.core.ui.views.CategoryTextMonogram
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun GoalsSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    goals: List<GoalProgressUi>,
    horizontalPadding: Dp = 16.dp,
    onGoalClick: (Long) -> Unit,
    onHistoryClick: () -> Unit,
    onAddClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GoalsHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = horizontalPadding, end = horizontalPadding - 4.dp),
            onHistoryClick = onHistoryClick,
            onAddClick = onAddClick,
        )
        when {
            isLoading -> {
                GoalsPlaceholderRow(horizontalPadding = horizontalPadding)
            }
            goals.isEmpty() -> {
                GoalsEmptyState(modifier = Modifier.padding(horizontal = horizontalPadding))
            }
            else -> {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items = goals, key = { progress -> progress.goal.id }) { progress ->
                        GoalCard(
                            progress = progress,
                            onClick = { onGoalClick(progress.goal.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalsHeader(
    modifier: Modifier = Modifier,
    onHistoryClick: () -> Unit,
    onAddClick: () -> Unit,
) {
    val strings = OverviewThemeRes.goalStrings
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = strings.goalsTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
        )
        FilledTonalIconButton(
            onClick = onHistoryClick,
        ) {
            Icon(
                modifier = Modifier.size(22.dp),
                painter = painterResource(OverviewThemeRes.icons.goalHistory),
                contentDescription = strings.historyIconDesc,
            )
        }
        FilledIconButton(onClick = onAddClick) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(OverviewThemeRes.icons.goalAdd),
                contentDescription = strings.addGoalIconDesc,
            )
        }
    }
}

@Composable
private fun GoalCard(
    modifier: Modifier = Modifier,
    progress: GoalProgressUi,
    onClick: () -> Unit,
) {
    val category = progress.goal.mainCategory
    val categoryId = category?.id ?: 0L
    val categoryColors = fetchOverviewCategoryColors(categoryId)
    val strings = OverviewThemeRes.goalStrings
    val deadlineFormat = remember {
        SimpleDateFormat("d MMM", Locale.getDefault(Locale.Category.FORMAT))
    }
    val valueTitle = when (progress.goal.metric) {
        GoalMetric.DURATION -> {
            "${progress.actualValue.toGoalDurationTitle()} / " + progress.goal.targetValue.toGoalDurationTitle()
        }
        GoalMetric.TASK_COUNT -> {
            "${progress.actualValue} / ${progress.goal.targetValue} ${strings.tasksUnit}"
        }
    }

    Surface(
        modifier = modifier.widthIn(min = 180.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.padding(12.dp).width(IntrinsicSize.Min),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val categoryTitle = category?.fetchName()
                val categoryIcon = category?.defaultType?.mapToIconPainter()
                when {
                    categoryIcon != null -> CategoryIconMonogram(
                        modifier = Modifier.size(36.dp),
                        icon = categoryIcon,
                        iconDescription = categoryTitle,
                        iconColor = categoryColors.accent,
                        backgroundColor = categoryColors.container,
                    )
                    categoryTitle != null -> CategoryTextMonogram(
                        modifier = Modifier.size(36.dp),
                        text = remember(categoryTitle) { categoryTitle.fetchMonogram() },
                        textColor = categoryColors.accent,
                        backgroundColor = categoryColors.container,
                    )
                    else -> CategoryIconMonogram(
                        modifier = Modifier.size(36.dp),
                        icon = painterResource(OverviewThemeRes.icons.goalTarget),
                        iconDescription = strings.goalsTitle,
                        iconColor = categoryColors.accent,
                        backgroundColor = categoryColors.container,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = progress.goal.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.padding(end = 10.dp),
                            text = valueTitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Icon(
                            modifier = Modifier.size(12.dp),
                            painter = painterResource(OverviewThemeRes.icons.schedule),
                            contentDescription = strings.deadlineIconDesc,
                            tint = if (progress.status == GoalProgressStatus.EXPIRED) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                        )
                        Text(
                            text = deadlineFormat.format(progress.goal.deadline),
                            color = if (progress.status == GoalProgressStatus.EXPIRED) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(4.dp)) {
                LinearProgressIndicator(
                    progress = { progress.progressFraction.coerceIn(0f, 1f) },
                    modifier = Modifier.matchParentSize(),
                    color = categoryColors.accent,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
            }
        }
    }
}

@Composable
private fun GoalsPlaceholderRow(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(2) {
            PlaceholderBox(
                modifier = Modifier
                    .width(184.dp)
                    .height(88.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            )
        }
    }
}

@Composable
private fun GoalsEmptyState(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = OverviewThemeRes.goalStrings.emptyGoalsTitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

private fun String.fetchMonogram(): String {
    return trim().firstOrNull()?.uppercaseChar()?.toString().orEmpty()
}
