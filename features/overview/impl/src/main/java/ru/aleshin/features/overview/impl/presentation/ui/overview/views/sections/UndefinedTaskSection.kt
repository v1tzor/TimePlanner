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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.TaskDateChooserDialog
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.UndefinedTaskEditorDialog
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.UndefinedTaskItem
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox
import java.util.Date

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
@Composable
internal fun UndefinedTaskSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    categories: List<MainCategoryDetailsUi>,
    tasks: List<UndefinedTaskUi>,
    horizontalPadding: Dp = 16.dp,
    isPaneSection: Boolean,
    onAddOrUpdateTask: (UndefinedTaskUi) -> Unit,
    onExecuteTask: (Date, UndefinedTaskUi) -> Unit,
    onDeleteTask: (Long) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var openTaskDateChooserDialog by remember { mutableStateOf(false) }
    var openTaskEditorDialog by remember { mutableStateOf(false) }
    var editableTask by remember { mutableStateOf<UndefinedTaskUi?>(null) }
    val visibleTasks = remember(tasks, isExpanded) {
        if (isExpanded) tasks else tasks.take(VISIBLE_TASKS_COUNT)
    }
    val contentState = when {
        isLoading -> UndefinedTaskSectionContentState.LOADING
        tasks.isNotEmpty() -> UndefinedTaskSectionContentState.DATA
        else -> UndefinedTaskSectionContentState.EMPTY
    }

    Column(
        modifier = modifier
            .padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        UndefinedTasksHeader(
            tasksCount = tasks.size,
            onAddTask = {
                editableTask = null
                openTaskEditorDialog = true
            },
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
            label = "UndefinedTaskSectionContent",
        ) { state ->
            when (state) {
                UndefinedTaskSectionContentState.LOADING -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(3) {
                            PlaceholderBox(
                                modifier = Modifier.fillMaxWidth().height(88.dp),
                                shape = MaterialTheme.shapes.large,
                            )
                        }
                    }
                }
                UndefinedTaskSectionContentState.EMPTY -> {
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
                }
                UndefinedTaskSectionContentState.DATA -> {
                    Column(
                        modifier = Modifier.animateContentSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        visibleTasks.forEachIndexed { index, task ->
                            key(task.id) {
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
                                    onDelete = { onDeleteTask(task.id) },
                                    containerColor = if (isPaneSection) {
                                        MaterialTheme.colorScheme.surfaceContainerLow
                                    } else {
                                        MaterialTheme.colorScheme.background
                                    }
                                )
                                if (index != visibleTasks.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                    )
                                }
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
        FilledIconButton(onClick = onAddTask) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Add,
                contentDescription = OverviewThemeRes.strings.addTaskTitle,
            )
        }
    }
}

private enum class UndefinedTaskSectionContentState {
    LOADING,
    EMPTY,
    DATA,
}

private const val VISIBLE_TASKS_COUNT = 5
