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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.features.overview.impl.presentation.models.overview.DaySummaryUi
import ru.aleshin.features.overview.impl.presentation.models.overview.WeekScheduleUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.theme.tokens.fetchOverviewCategoryColors
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox
import ru.aleshin.timeplanner.core.ui.views.toMinutesOrHoursTitle
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
@Composable
internal fun WeekTimelineSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    selectedDate: Date?,
    schedules: List<WeekScheduleUi>,
    weekTasksCount: Int,
    onSelectSchedule: (Date) -> Unit,
) {
    val daySummary = remember(selectedDate, schedules) {
        schedules.find { schedule -> schedule.date == selectedDate }?.summary
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        WeekTimelineHeader(
            isLoading = isLoading,
            schedules = schedules,
            tasksCount = weekTasksCount,
        )
        DaySummaryCards(
            isLoading = isLoading,
            daySummary = daySummary,
        )
        AnimatedContent(
            modifier = modifier,
            targetState = isLoading,
            label = "WeekTimeline",
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
                        .height(294.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                )
            } else {
                WeekTimeline(
                    selectedDate = selectedDate,
                    schedules = schedules,
                    onSelectSchedule = onSelectSchedule,
                )
            }
        }
    }
}

@Composable
private fun WeekTimelineHeader(
    isLoading: Boolean,
    schedules: List<WeekScheduleUi>,
    tasksCount: Int,
) {
    val strings = OverviewThemeRes.strings

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = strings.weekTimelineTitle,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = remember(schedules) { schedules.fetchWeekRangeTitle() },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (!isLoading) {
            Text(
                text = strings.tasksCountFormat.format(tasksCount),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun DaySummaryCards(
    isLoading: Boolean,
    daySummary: DaySummaryUi?,
) {
    val strings = OverviewThemeRes.strings
    val icons = OverviewThemeRes.icons
    val showPlaceholder = isLoading || daySummary == null

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DaySummaryCard(
            modifier = Modifier.weight(1f),
            isLoading = showPlaceholder,
            icon = icons.duration,
            iconColor = MaterialTheme.colorScheme.tertiary,
            title = strings.freeTimeTitle,
            value = daySummary?.freeTime?.toDurationTitle().orEmpty(),
        )
        DaySummaryCard(
            modifier = Modifier.weight(1f),
            isLoading = showPlaceholder,
            icon = icons.schedule,
            iconColor = MaterialTheme.colorScheme.primary,
            title = strings.workloadTitle,
            value = daySummary?.workload?.toDurationTitle().orEmpty(),
        )
        DaySummaryCard(
            modifier = Modifier.weight(1f),
            isLoading = showPlaceholder,
            icon = icons.completedTask,
            iconColor = MaterialTheme.colorScheme.secondary,
            title = strings.progressTitle,
            value = remember(daySummary?.progress) {
                daySummary?.progress?.let { progress ->
                    NumberFormat.getPercentInstance().format(progress.coerceIn(0f, 1f))
                }.orEmpty()
            },
        )
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
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
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
private fun WeekTimeline(
    selectedDate: Date?,
    schedules: List<WeekScheduleUi>,
    onSelectSchedule: (Date) -> Unit,
) {
    val timelineTasksHeight = remember(schedules) {
        schedules.maxOfOrNull { schedule ->
            WeekTimelineLayout.calculateHeight(
                timeTasks = schedule.timeTasks,
                date = schedule.date,
                minimumTimelineHeight = TIMELINE_TASKS_HEIGHT.value,
                minimumTaskHeight = TIMELINE_TASK_MIN_HEIGHT.value,
                tasksSpace = TIMELINE_TASK_SPACE.value,
            )
        }?.dp ?: TIMELINE_TASKS_HEIGHT
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TIMELINE_HEADER_HEIGHT + timelineTasksHeight),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        TimelineHours()
        schedules.forEach { schedule ->
            TimelineDay(
                modifier = Modifier.weight(1f),
                schedule = schedule,
                isSelected = schedule.date == selectedDate,
                onClick = { onSelectSchedule(schedule.date) },
            )
        }
    }
}

@Composable
private fun TimelineHours() {
    Column(
        modifier = Modifier
            .width(42.dp)
            .fillMaxHeight(),
    ) {
        Spacer(modifier = Modifier.height(TIMELINE_HEADER_HEIGHT))
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            listOf("00:00", "06:00", "12:00", "18:00", "24:00").forEachIndexed { index, title ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = maxHeight * (index / 4f).coerceAtMost(0.98f))
                        .graphicsLayer { translationY = -size.height / 2f },
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun TimelineDay(
    modifier: Modifier = Modifier,
    schedule: WeekScheduleUi,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("EEE", Locale.getDefault()) }

    Surface(
        modifier = modifier.fillMaxHeight(),
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = when (isSelected) {
            true -> MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            false -> MaterialTheme.colorScheme.surfaceContainerLowest
        },
        border = if (isSelected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        },
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TIMELINE_HEADER_HEIGHT)
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = remember(schedule.date) { dateFormat.format(schedule.date) },
                    color = when (isSelected) {
                        true -> MaterialTheme.colorScheme.primary
                        false -> MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = remember(schedule.date) {
                        SimpleDateFormat("d", Locale.getDefault()).format(schedule.date)
                    },
                    color = when (isSelected) {
                        true -> MaterialTheme.colorScheme.primary
                        false -> MaterialTheme.colorScheme.onSurface
                    },
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            TimelineTasks(
                modifier = Modifier.fillMaxSize(),
                schedule = schedule,
            )
        }
    }
}

@Composable
private fun TimelineTasks(
    modifier: Modifier = Modifier,
    schedule: WeekScheduleUi,
) {
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val taskColors = remember(schedule.timeTasks, surfaceColor) {
        schedule.timeTasks.associate { task ->
            task.key to fetchOverviewCategoryColors(
                categoryId = task.category.id,
                surface = surfaceColor,
            )
        }
    }

    Spacer(
        modifier = modifier.drawWithCache {
            val horizontalPadding = TIMELINE_TASK_HORIZONTAL_PADDING.toPx()
            val taskWidth = (size.width - horizontalPadding * 2f).coerceAtLeast(0f)
            val taskCornerRadius = CornerRadius(TIMELINE_TASK_CORNER_RADIUS.toPx())
            val gridPathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
            val taskPositions = WeekTimelineLayout.calculatePositions(
                timeTasks = schedule.timeTasks,
                date = schedule.date,
                timelineHeight = size.height,
                minimumTaskHeight = TIMELINE_TASK_MIN_HEIGHT.toPx(),
                tasksSpace = TIMELINE_TASK_SPACE.toPx(),
            )

            onDrawBehind {
                repeat(5) { index ->
                    val y = size.height * index / 4f
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        pathEffect = gridPathEffect,
                    )
                }
                taskPositions.forEach { taskPosition ->
                    val categoryColors = taskColors[taskPosition.task.key] ?: return@forEach
                    val taskSize = Size(
                        width = taskWidth,
                        height = taskPosition.height,
                    )
                    val taskTopLeft = Offset(
                        x = horizontalPadding,
                        y = taskPosition.top,
                    )

                    drawRoundRect(
                        color = categoryColors.container,
                        topLeft = taskTopLeft,
                        size = taskSize,
                        cornerRadius = taskCornerRadius,
                    )
                    drawRoundRect(
                        color = categoryColors.accent.copy(alpha = 0.18f),
                        topLeft = taskTopLeft,
                        size = taskSize,
                        cornerRadius = taskCornerRadius,
                    )
                }
            }
        },
    )
}

private fun List<WeekScheduleUi>.fetchWeekRangeTitle(): String {
    val firstDate = firstOrNull()?.date ?: return ""
    val lastDate = lastOrNull()?.date ?: return ""
    val dayFormat = SimpleDateFormat("d", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
    val fullFormat = SimpleDateFormat("d MMM", Locale.getDefault())

    return if (monthFormat.format(firstDate) == monthFormat.format(lastDate)) {
        "${dayFormat.format(firstDate)}–${fullFormat.format(lastDate)}"
    } else {
        "${fullFormat.format(firstDate)}–${fullFormat.format(lastDate)}"
    }
}

@Composable
private fun Long.toDurationTitle(): String {
    return when {
        this <= 0L -> "0${TimePlannerRes.strings.hoursSymbol}"
        else -> toMinutesOrHoursTitle()
    }
}

private val TIMELINE_HEADER_HEIGHT = 54.dp
private val TIMELINE_TASKS_HEIGHT = 238.dp
private val TIMELINE_TASK_MIN_HEIGHT = 5.dp
private val TIMELINE_TASK_SPACE = 1.dp
private val TIMELINE_TASK_HORIZONTAL_PADDING = 4.dp
private val TIMELINE_TASK_CORNER_RADIUS = 4.dp
