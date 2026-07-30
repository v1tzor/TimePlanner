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
package ru.aleshin.features.overview.impl.presentation.ui.overview.views.sections

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.features.overview.impl.presentation.models.GoalProgressUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.GoalCard
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox

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
    val contentState = when {
        isLoading -> GoalsSectionContentState.LOADING
        goals.isNotEmpty() -> GoalsSectionContentState.DATA
        else -> GoalsSectionContentState.EMPTY
    }

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
        AnimatedContent(
            modifier = Modifier.fillMaxWidth(),
            targetState = contentState,
            transitionSpec = {
                fadeIn(animationSpec = tween(600, delayMillis = 90)).togetherWith(
                    fadeOut(animationSpec = tween(300)),
                )
            },
            contentKey = { state -> state },
            label = "GoalsSectionContent",
        ) { state ->
            when (state) {
                GoalsSectionContentState.LOADING -> {
                    GoalsPlaceholderRow(horizontalPadding = horizontalPadding)
                }
                GoalsSectionContentState.EMPTY -> {
                    GoalsEmptyState(modifier = Modifier.padding(horizontal = horizontalPadding))
                }
                GoalsSectionContentState.DATA -> {
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
}

@Composable
private fun GoalsHeader(
    modifier: Modifier = Modifier,
    onHistoryClick: () -> Unit,
    onAddClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = OverviewThemeRes.strings.goalsTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
        )
        FilledTonalIconButton(
            onClick = onHistoryClick,
        ) {
            Icon(
                modifier = Modifier.size(22.dp),
                painter = painterResource(OverviewThemeRes.icons.goalHistory),
                contentDescription = OverviewThemeRes.strings.historyIconDesc,
            )
        }
        FilledIconButton(onClick = onAddClick) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(OverviewThemeRes.icons.goalAdd),
                contentDescription = OverviewThemeRes.strings.addGoalIconDesc,
            )
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
                text = OverviewThemeRes.strings.emptyGoalsTitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

private enum class GoalsSectionContentState {
    LOADING,
    EMPTY,
    DATA,
}
