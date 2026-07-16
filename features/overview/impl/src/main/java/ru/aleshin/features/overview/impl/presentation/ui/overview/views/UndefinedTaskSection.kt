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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.tasks.TaskPriority
import ru.aleshin.core.presentation.mappers.mapToIconPainter
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.features.overview.impl.presentation.mapppers.mapToString
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.theme.tokens.fetchOverviewCategoryColors
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
@Composable
internal fun UndefinedTaskSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    categories: List<MainCategoryDetailsUi>,
    tasks: List<UndefinedTaskUi>,
    onAddOrUpdateTask: (UndefinedTaskUi) -> Unit,
    onExecuteTask: (Date, UndefinedTaskUi) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var openTaskDateChooserDialog by remember { mutableStateOf(false) }
    var openTaskEditorDialog by remember { mutableStateOf(false) }
    var editableTask by remember { mutableStateOf<UndefinedTaskUi?>(null) }
    val visibleTasks = remember(tasks, isExpanded) {
        if (isExpanded) tasks else tasks.take(VISIBLE_TASKS_COUNT)
    }

    AnimatedContent(
        modifier = modifier,
        targetState = isLoading,
        label = "UndefinedTaskSection",
        transitionSpec = {
            fadeIn(animationSpec = tween(600, delayMillis = 90)).togetherWith(
                fadeOut(animationSpec = tween(300)),
            )
        },
    ) { loading ->
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UndefinedTasksHeader(
                tasksCount = tasks.size,
                onAddTask = {
                    editableTask = null
                    openTaskEditorDialog = true
                },
            )
            if (loading) {
                repeat(3) {
                    PlaceholderBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp),
                        shape = MaterialTheme.shapes.large,
                    )
                }
            } else if (tasks.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                        text = OverviewThemeRes.strings.noneTitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                visibleTasks.forEachIndexed { index, task ->
                    UndefinedTaskItem(
                        model = task,
                        onClick = {
                            editableTask = task
                            openTaskEditorDialog = true
                        },
                        onExecuteButtonClick = {
                            editableTask = task
                            openTaskDateChooserDialog = true
                        },
                    )
                    if (index != visibleTasks.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
                if (tasks.size > VISIBLE_TASKS_COUNT) {
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = { isExpanded = !isExpanded },
                    ) {
                        Text(
                            text = when (isExpanded) {
                                true -> OverviewThemeRes.strings.showLessTasksTitle
                                false -> OverviewThemeRes.strings.showAllTasksTitle
                            },
                        )
                    }
                }
            }
        }
    }

    if (openTaskEditorDialog) {
        UndefinedTaskEditorDialog(
            categories = categories,
            model = editableTask,
            onDismiss = { openTaskEditorDialog = false },
            onConfirm = {
                onAddOrUpdateTask(it)
                openTaskEditorDialog = false
            },
        )
    }

    if (openTaskDateChooserDialog) {
        TaskDateChooserDialog(
            onDismiss = { openTaskDateChooserDialog = false },
            onConfirm = { date ->
                editableTask?.let { task -> onExecuteTask(date, task) }
                openTaskDateChooserDialog = false
            },
        )
    }
}

@Composable
private fun UndefinedTasksHeader(
    tasksCount: Int,
    onAddTask: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = OverviewThemeRes.strings.undefinedTasksHeader,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
        Surface(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = tasksCount.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Surface(
            modifier = Modifier
                .width(56.dp)
                .height(40.dp),
            onClick = onAddTask,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = OverviewThemeRes.strings.addTaskTitle,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun UndefinedTaskItem(
    modifier: Modifier = Modifier,
    model: UndefinedTaskUi,
    onClick: () -> Unit,
    onExecuteButtonClick: () -> Unit,
) {
    val strings = OverviewThemeRes.strings
    val icons = OverviewThemeRes.icons
    val categoryTitle = model.mainCategory.fetchName() ?: strings.noneTitle
    val taskTitle = model.note?.takeIf { note -> note.isNotBlank() }
        ?: model.subCategory?.name
        ?: categoryTitle
    val deadlineTitle = model.deadline?.let { deadline ->
        remember(deadline) {
            SimpleDateFormat("d MMM", Locale.getDefault()).format(deadline)
        }
    } ?: strings.noDeadlineTitle
    val categoryColors = fetchOverviewCategoryColors(model.mainCategory.id)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(MaterialTheme.shapes.large)
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
            if (model.mainCategory.defaultType != null) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = model.mainCategory.defaultType!!.mapToIconPainter(),
                    contentDescription = categoryTitle,
                    tint = categoryColors.accent,
                )
            } else {
                Text(
                    text = remember(categoryTitle) { categoryTitle.fetchMonogram() },
                    color = categoryColors.accent,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = taskTitle,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = categoryTitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = strings.metadataDivider,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = deadlineTitle,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
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
                            TaskPriority.STANDARD -> icons.priorityStandard
                            TaskPriority.MEDIUM -> icons.priorityMedium
                            TaskPriority.MAX -> icons.priorityMax
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
            color = MaterialTheme.colorScheme.surfaceContainerLow,
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

private const val VISIBLE_TASKS_COUNT = 5
