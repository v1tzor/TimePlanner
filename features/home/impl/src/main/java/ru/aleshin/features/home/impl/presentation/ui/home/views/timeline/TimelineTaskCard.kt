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
package ru.aleshin.features.home.impl.presentation.ui.home.views.timeline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.presentation.mappers.mapToIconPainter
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.impl.presentation.models.TimelineTimeTaskUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.theme.tokens.HomeCategoryColors
import ru.aleshin.timeplanner.core.ui.mappers.mapToMonogram
import ru.aleshin.timeplanner.core.ui.views.CategoryIconMonogram
import ru.aleshin.timeplanner.core.ui.views.CategoryTextMonogram
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
@Composable
internal fun TimelineTaskCard(
    modifier: Modifier = Modifier,
    model: TimelineTimeTaskUi,
    timeRange: TimeRange,
    colors: HomeCategoryColors,
    isSelected: Boolean,
    isDragging: Boolean,
    onClick: () -> Unit,
    onMoveClick: () -> Unit,
    onEditModeCancel: () -> Unit,
    onDoneChange: () -> Unit,
    onDragStart: (TimelineTaskDragMode) -> Boolean,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    val timeTask = model.timeTask
    val isRunning = model.executionStatus == TimeTaskStatus.RUNNING
    val isCompleted = model.executionStatus == TimeTaskStatus.COMPLETED

    Box(
        modifier = modifier
            .pointerInput(timeTask.key) {
                detectTapGestures(onTap = { onClick() })
            },
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = TASK_HANDLE_VISIBLE_HEIGHT)
                .shadow(
                    elevation = if (isDragging || isRunning) 3.dp else 0.dp,
                    shape = MaterialTheme.shapes.medium,
                    clip = false,
            ),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            border = when {
                isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                isRunning -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                else -> BorderStroke(0.5.dp, MaterialTheme.colorScheme.surfaceContainerHighest)
            },
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(colors.accent),
                )
                TimelineTaskCategory(
                    modifier = Modifier.padding(start = 10.dp),
                    model = model,
                    colors = colors,
                )
                TimelineTaskContent(
                    modifier = Modifier.weight(1f),
                    model = model,
                    timeRange = timeRange,
                    isDragging = isDragging,
                    colors = colors,
                )
                if (isCompleted && !isSelected) {
                    IconButton(onClick = onDoneChange) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(
                                id = when (timeTask.isCompleted) {
                                    true -> HomeThemeRes.icons.check
                                    false -> HomeThemeRes.icons.cancel
                                },
                            ),
                            contentDescription = null,
                            tint = when (timeTask.isCompleted) {
                                true -> MaterialTheme.colorScheme.onSurfaceVariant
                                false -> MaterialTheme.colorScheme.error
                            },
                        )
                    }
                }
                if (isSelected) {
                    IconButton(onClick = onEditModeCancel) {
                        Icon(
                            painter = painterResource(HomeThemeRes.icons.close),
                            contentDescription = HomeThemeRes.strings.timelineTaskEditCancelIconDesc,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
                TimelineTaskMoveButton(
                    enabled = model.canMove,
                    onClick = onMoveClick,
                    onDragStart = { onDragStart(TimelineTaskDragMode.MOVE) },
                    onDrag = onDrag,
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel,
                )
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f),
            visible = isSelected && model.canResizeStart,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            TimelineTaskEdgeHandle(
                mode = TimelineTaskDragMode.RESIZE_START,
                onDragStart = onDragStart,
                onDrag = onDrag,
                onDragEnd = onDragEnd,
                onDragCancel = onDragCancel,
            )
        }
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f),
            visible = isSelected && model.canResizeEnd,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            TimelineTaskEdgeHandle(
                mode = TimelineTaskDragMode.RESIZE_END,
                onDragStart = onDragStart,
                onDrag = onDrag,
                onDragEnd = onDragEnd,
                onDragCancel = onDragCancel,
            )
        }
    }
}

@Composable
private fun TimelineTaskCategory(
    modifier: Modifier,
    model: TimelineTimeTaskUi,
    colors: HomeCategoryColors,
) {
    val timeTask = model.timeTask
    val mainCategory = timeTask.category.fetchName() ?: HomeThemeRes.strings.noneTitle
    val title = timeTask.subCategory?.name ?: mainCategory
    val categoryIcon = timeTask.category.defaultType?.mapToIconPainter()

    if (categoryIcon != null) {
        CategoryIconMonogram(
            modifier = modifier.size(36.dp),
            iconSize = 18.dp,
            icon = categoryIcon,
            iconDescription = title,
            iconColor = colors.accent,
            priority = timeTask.priority.mapToMonogram(),
            backgroundColor = colors.container,
        )
    } else {
        CategoryTextMonogram(
            modifier = modifier.size(36.dp),
            text = title.first().toString(),
            textColor = colors.accent,
            priority = timeTask.priority.mapToMonogram(),
            backgroundColor = colors.container,
        )
    }
}

@Composable
private fun TimelineTaskContent(
    modifier: Modifier,
    model: TimelineTimeTaskUi,
    timeRange: TimeRange,
    isDragging: Boolean,
    colors: HomeCategoryColors,
) {
    val timeTask = model.timeTask
    val mainCategory = timeTask.category.fetchName() ?: HomeThemeRes.strings.noneTitle
    val title = timeTask.subCategory?.name ?: mainCategory
    val categorySubtitle = mainCategory.takeIf { timeTask.subCategory != null }
    val note = timeTask.note?.takeIf { value -> value.isNotBlank() }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val taskMaxHeight = maxHeight
        val showAdditionalNote = note != null && taskMaxHeight >= 82.dp
        val subtitle = when {
            showAdditionalNote -> categorySubtitle
            note != null -> note
            else -> categorySubtitle
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = colors.accent,
                    maxLines = if (subtitle == note) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            if (showAdditionalNote) {
                Text(
                    text = checkNotNull(note),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (taskMaxHeight >= 102.dp) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (isDragging && taskMaxHeight >= 76.dp) {
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = "${timeFormat.format(timeRange.from)}–${timeFormat.format(timeRange.to)}",
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun TimelineTaskMoveButton(
    enabled: Boolean,
    onClick: () -> Unit,
    onDragStart: () -> Boolean,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    IconButton(
        modifier = Modifier.pointerInput(enabled) {
            if (!enabled) return@pointerInput

            var isDragStarted = false
            detectVerticalDragGestures(
                onDragStart = { isDragStarted = onDragStart() },
                onDragEnd = { if (isDragStarted) onDragEnd() },
                onDragCancel = { if (isDragStarted) onDragCancel() },
                onVerticalDrag = { change, dragAmount ->
                    if (isDragStarted) {
                        change.consume()
                        onDrag(dragAmount)
                    }
                },
            )
        },
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(HomeThemeRes.icons.dragIndicator),
            contentDescription = HomeThemeRes.strings.timelineTaskMoveIconDesc,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TimelineTaskEdgeHandle(
    mode: TimelineTaskDragMode,
    onDragStart: (TimelineTaskDragMode) -> Boolean,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = TASK_HANDLE_TOUCH_HEIGHT)
            .pointerInput(mode) {
                var isDragStarted = false
                val touchPadding = TASK_HANDLE_TOUCH_PADDING.toPx()
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        isDragStarted = offset.y in -touchPadding..size.height + touchPadding &&
                            onDragStart(mode)
                    },
                    onDragEnd = { if (isDragStarted) onDragEnd() },
                    onDragCancel = { if (isDragStarted) onDragCancel() },
                    onVerticalDrag = { change, dragAmount ->
                        if (isDragStarted) {
                            change.consume()
                            onDrag(dragAmount)
                        }
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(width = 22.dp, height = 4.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primary,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.surface),
            content = {},
        )
    }
}

private val TASK_HANDLE_VISIBLE_HEIGHT = 2.dp
private val TASK_HANDLE_TOUCH_HEIGHT = 12.dp
private val TASK_HANDLE_TOUCH_PADDING = 10.dp
