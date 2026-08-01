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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.aleshin.core.domain.entities.tasks.TaskPriority
import ru.aleshin.core.presentation.mappers.mapToIconPainter
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.features.overview.impl.presentation.mapppers.mapToString
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.theme.tokens.fetchOverviewCategoryColors
import ru.aleshin.timeplanner.core.ui.views.SwipeToDismissBackground
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun UndefinedTaskItem(
    modifier: Modifier = Modifier,
    model: UndefinedTaskUi,
    onClick: () -> Unit,
    onExecuteButtonClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.background,
    onDelete: () -> Unit,
) {
    val currentOnDelete by rememberUpdatedState(onDelete)
    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { distance -> distance * DISMISS_THRESHOLD },
    )

    SwipeToDismissBox(
        modifier = modifier.fillMaxWidth(),
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            SwipeToDismissBackground(
                dismissState = dismissState,
                endToStartContent = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                    )
                    Text(text = OverviewThemeRes.strings.deleteUndefinedTaskTitle)
                },
                endToStartColor = MaterialTheme.colorScheme.errorContainer,
            )
        },
        onDismiss = { direction ->
            if (direction == SwipeToDismissBoxValue.EndToStart) {
                currentOnDelete()
            }
            coroutineScope.launch { dismissState.reset() }
        },
    ) {
        UndefinedTaskItemContent(
            model = model,
            onClick = onClick,
            containerColor = containerColor,
            onExecuteButtonClick = onExecuteButtonClick,
        )
    }
}

@Composable
private fun UndefinedTaskItemContent(
    model: UndefinedTaskUi,
    onClick: () -> Unit,
    containerColor: Color,
    onExecuteButtonClick: () -> Unit,
) {
    val categoryTitle = model.mainCategory.fetchName()
    val categoryContentDescription = categoryTitle ?: OverviewThemeRes.strings.noneTitle
    val subCategoryTitle = model.subCategory?.name?.takeIf { name -> name.isNotBlank() }
    val taskTitle = subCategoryTitle ?: categoryTitle ?: OverviewThemeRes.strings.noneTitle
    val taskSubtitle = model.note?.takeIf { note -> note.isNotBlank() } ?: categoryTitle?.takeIf { subCategoryTitle != null }
    val deadlineTitle = model.deadline?.let { deadline ->
        remember(deadline) {
            SimpleDateFormat("d MMM", Locale.getDefault()).format(deadline)
        }
    } ?: OverviewThemeRes.strings.noDeadlineTitle
    val categoryColors = fetchOverviewCategoryColors(model.mainCategory.id)
    val defaultCategoryType = model.mainCategory.defaultType

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 88.dp)
            .clip(MaterialTheme.shapes.large)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(categoryColors.container),
            contentAlignment = Alignment.Center,
        ) {
            if (defaultCategoryType != null) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = defaultCategoryType.mapToIconPainter(),
                    contentDescription = categoryContentDescription,
                    tint = categoryColors.accent,
                )
            } else {
                Text(
                    text = remember(categoryContentDescription) {
                        categoryContentDescription.fetchMonogram()
                    },
                    color = categoryColors.accent,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = taskTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = deadlineTitle,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (taskSubtitle != null) {
                Text(
                    text = taskSubtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(
                        id = when (model.priority) {
                            TaskPriority.STANDARD -> OverviewThemeRes.icons.priorityStandard
                            TaskPriority.MEDIUM -> OverviewThemeRes.icons.priorityMedium
                            TaskPriority.MAX -> OverviewThemeRes.icons.priorityMax
                        },
                    ),
                    contentDescription = null,
                    tint = model.priority.fetchPriorityColor(),
                )
                Text(
                    text = model.priority.mapToString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Surface(
            modifier = Modifier.size(40.dp),
            onClick = onExecuteButtonClick,
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(id = OverviewThemeRes.icons.schedule),
                    contentDescription = OverviewThemeRes.strings.executeUndefinedTasksTitle,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun TaskPriority.fetchPriorityColor() = when (this) {
    TaskPriority.STANDARD -> MaterialTheme.colorScheme.outline
    TaskPriority.MEDIUM -> MaterialTheme.colorScheme.tertiary
    TaskPriority.MAX -> MaterialTheme.colorScheme.error
}

private fun String.fetchMonogram(): String {
    return filter { char -> char.isLetterOrDigit() }.take(2).ifEmpty { "*" }
}

private const val DISMISS_THRESHOLD = 0.6f
