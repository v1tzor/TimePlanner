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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.shiftHours
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.impl.presentation.models.TimelineScheduleUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.theme.tokens.fetchHomeCategoryColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
@Composable
internal fun TimelineGrid(
    modifier: Modifier = Modifier,
    schedule: TimelineScheduleUi,
    currentTime: Date?,
    scrollState: ScrollState,
    viewportHeight: Int,
    gestureState: TimelineGestureState,
    onTimeTaskEdit: (Long) -> Unit,
    onTaskDoneChange: (TimeTaskUi) -> Unit,
    onTimeTaskAdd: (Date, Date) -> Unit,
    onTimeTaskUpdate: (Long, TimeRange) -> Unit,
    onInitialTimePositioned: (Float) -> Unit,
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    val surfaceColor = MaterialTheme.colorScheme.surface
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val railColor = MaterialTheme.colorScheme.outline
    val nowColor = MaterialTheme.colorScheme.primary
    val baseLayoutResult = remember(schedule, density) {
        with(density) {
            TimelineLayout.calculate(
                dayTimeRange = schedule.dayTimeRange,
                timeTasks = schedule.timeTasks,
                hourHeight = TIMELINE_HOUR_HEIGHT.toPx(),
                minimumTaskHeight = TIMELINE_TASK_MIN_HEIGHT.toPx(),
                maximumTaskHeight = TIMELINE_TASK_MAX_HEIGHT.toPx(),
                longTaskThreshold = TIMELINE_LONG_TASK_THRESHOLD,
                longTaskScale = TIMELINE_LONG_TASK_SCALE,
                minimumFreeTimeHeight = TIMELINE_FREE_TIME_MIN_HEIGHT.toPx(),
                freeTimeScale = TIMELINE_FREE_TIME_SCALE,
                taskSpace = TIMELINE_TASK_SPACE.toPx(),
                verticalPadding = TIMELINE_VERTICAL_PADDING.toPx(),
            )
        }
    }
    var editLayoutResult by remember(schedule.date) {
        mutableStateOf<TimelineLayoutResult?>(null)
    }
    val layoutResult = editLayoutResult ?: baseLayoutResult
    val taskColors = remember(schedule.timeTasks, surfaceColor) {
        schedule.timeTasks.associate { timeTask ->
            timeTask.timeTask.key to fetchHomeCategoryColors(
                categoryId = timeTask.timeTask.category.id,
                surface = surfaceColor,
            )
        }
    }
    val hourTimes = remember(schedule.dayTimeRange) {
        List(HOURS_IN_DAY + 1) { hour -> schedule.dayTimeRange.from.shiftHours(hour) }
    }
    val baseTaskPositionById = remember(layoutResult) {
        layoutResult.taskPositions.associateBy { position -> position.timeTaskId }
    }
    val visibleTaskRanges = schedule.timeTasks.map { timeTask ->
        if (gestureState.selectedTimeTaskId == timeTask.timeTask.key) {
            gestureState.fetchTimeRange(timeTask)
        } else {
            timeTask.visibleTimeRange
        }
    }
    val taskPositions = schedule.timeTasks.mapIndexed { index, timeTask ->
        val basePosition = checkNotNull(baseTaskPositionById[timeTask.timeTask.key])
        val isSelected = gestureState.selectedTimeTaskId == timeTask.timeTask.key
        when {
            !isSelected || gestureState.lastDragMode == null -> basePosition
            gestureState.lastDragMode == TimelineTaskDragMode.MOVE -> basePosition.copy(
                top = layoutResult.scale.fetchOffset(visibleTaskRanges[index].from),
            )
            else -> {
                val startOffset = layoutResult.scale.fetchOffset(visibleTaskRanges[index].from)
                val endOffset = layoutResult.scale.fetchOffset(visibleTaskRanges[index].to)
                basePosition.copy(
                    top = startOffset,
                    height = (endOffset - startOffset).coerceAtLeast(
                        with(density) { TIMELINE_TASK_MIN_HEIGHT.toPx() },
                    ),
                )
            }
        }
    }
    val taskPositionById = taskPositions.associateBy { position -> position.timeTaskId }
    val taskStartOffsets = taskPositions.map { position -> position.top }
    val taskEndOffsets = taskPositions.map { position -> position.top + position.height }
    val labelMinimumDistance = with(density) { LABEL_MIN_DISTANCE.toPx() }
    val groupEndTaskIndexes = visibleTaskRanges.indices.filter { index ->
        val timeRange = visibleTaskRanges[index]
        visibleTaskRanges.withIndex().none { (nextIndex, nextTimeRange) ->
            nextIndex != index && nextTimeRange.from == timeRange.to
        }
    }.filter { index ->
        taskStartOffsets.withIndex().none { (otherIndex, startOffset) ->
            otherIndex != index && abs(
                taskEndOffsets[index] - startOffset,
            ) < labelMinimumDistance
        }
    }
    val taskBoundaryOffsets = taskStartOffsets +
        groupEndTaskIndexes.map { index -> taskEndOffsets[index] }
    val visibleCurrentTime = currentTime?.takeIf { time -> schedule.date.isCurrentDay(time) }
    val visibleHourTimes = TimelineLayout.fetchVisibleHourTimes(
        hourTimes = hourTimes,
        boundaryOffsets = taskBoundaryOffsets,
        scale = layoutResult.scale,
        minimumDistance = labelMinimumDistance,
    )
    var dragPointerPosition by remember { mutableFloatStateOf(Float.NaN) }
    val selectedTimeTask by rememberUpdatedState(
        schedule.timeTasks.find { timeTask ->
            timeTask.timeTask.key == gestureState.taskEdit?.timeTaskId
        },
    )

    LaunchedEffect(schedule.timeTasks, gestureState.selectedTimeTaskId) {
        val timeTask = schedule.timeTasks.find { timeTask ->
            timeTask.timeTask.key == gestureState.selectedTimeTaskId
        }
        if (timeTask != null) {
            gestureState.synchronize(timeTask)
        } else if (gestureState.selectedTimeTaskId != null) {
            gestureState.exitEditMode()
            editLayoutResult = null
        }
    }

    LaunchedEffect(layoutResult, schedule.initialTime) {
        onInitialTimePositioned(layoutResult.scale.fetchOffset(schedule.initialTime))
    }

    LaunchedEffect(gestureState.taskEdit?.timeTaskId, viewportHeight, scrollState.maxValue) {
        while (gestureState.taskEdit != null && viewportHeight > 0) {
            withFrameNanos { }
            val pointerPosition = dragPointerPosition
            val timeTask = selectedTimeTask ?: continue
            if (pointerPosition.isNaN()) continue

            val viewportPosition = pointerPosition - scrollState.value
            val edgeSize = with(density) { AUTO_SCROLL_EDGE_SIZE.toPx() }
            val maximumStep = with(density) { AUTO_SCROLL_MAX_STEP.toPx() }
            val scrollStep = when {
                viewportPosition < edgeSize -> {
                    -maximumStep * (1f - viewportPosition.coerceAtLeast(0f) / edgeSize)
                }
                viewportPosition > viewportHeight - edgeSize -> {
                    val edgeProgress = (viewportPosition - viewportHeight + edgeSize) / edgeSize
                    maximumStep * edgeProgress.coerceIn(0f, 1f)
                }
                else -> 0f
            }
            if (scrollStep != 0f) {
                val consumedScroll = scrollState.scrollBy(scrollStep)
                gestureState.dragTask(
                    dragAmount = consumedScroll,
                    timeTask = timeTask,
                    scale = layoutResult.scale,
                    freeTimeRanges = schedule.freeTimeRanges,
                    timeStep = schedule.timeStep,
                    minimumTaskDuration = schedule.minimumTaskDuration,
                )
            }
        }
    }

    val drawModifier = Modifier.drawWithCache {
        val axisOffset = TIMELINE_AXIS_WIDTH.toPx()
        val lineStart = axisOffset + TIMELINE_LINE_START_GAP.toPx()
        val dottedEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 5.dp.toPx()))
        val halfHourDistance = HALF_HOUR_MIN_DISTANCE.toPx()
        val hourOffsets = hourTimes.map { hourTime -> layoutResult.scale.fetchOffset(hourTime) }

        onDrawBehind {
            drawLine(
                color = railColor.copy(alpha = 0.55f),
                start = Offset(axisOffset, TIMELINE_VERTICAL_PADDING.toPx()),
                end = Offset(axisOffset, size.height - TIMELINE_VERTICAL_PADDING.toPx()),
                strokeWidth = 1.dp.toPx(),
            )
            hourOffsets.forEachIndexed { index, hourOffset ->
                drawLine(
                    color = gridColor.copy(alpha = 0.46f),
                    start = Offset(lineStart, hourOffset),
                    end = Offset(size.width, hourOffset),
                    strokeWidth = 1.dp.toPx(),
                )
                drawCircle(
                    color = railColor.copy(alpha = 0.55f),
                    radius = 3.dp.toPx(),
                    center = Offset(axisOffset, hourOffset),
                )
                if (index < hourOffsets.lastIndex) {
                    val halfHour = hourTimes[index].time + Constants.Date.MILLIS_IN_HOUR / 2L
                    val halfHourOffset = layoutResult.scale.fetchOffset(Date(halfHour))
                    if (hourOffsets[index + 1] - hourOffset >= halfHourDistance) {
                        drawLine(
                            color = gridColor.copy(alpha = 0.32f),
                            start = Offset(lineStart, halfHourOffset),
                            end = Offset(size.width, halfHourOffset),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = dottedEffect,
                        )
                        drawCircle(
                            color = railColor.copy(alpha = 0.45f),
                            radius = 2.dp.toPx(),
                            center = Offset(axisOffset, halfHourOffset),
                        )
                    }
                }
            }
        }
    }
    Layout(
        modifier = modifier
            .fillMaxWidth()
            .height(with(density) { layoutResult.scale.height.toDp() })
            .then(drawModifier),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(schedule, layoutResult) {
                        detectTapGestures(
                            onTap = { offset ->
                                if (
                                    gestureState.selectedTimeTaskId != null ||
                                    gestureState.taskEdit != null
                                ) {
                                    gestureState.exitEditMode()
                                    editLayoutResult = null
                                    return@detectTapGestures
                                }
                                if (offset.x < TIMELINE_AXIS_WIDTH.toPx()) {
                                    return@detectTapGestures
                                }

                                val timeRange = fetchCreateTimeRange(
                                    offset = offset.y,
                                    scale = layoutResult.scale,
                                    freeTimeRanges = schedule.freeTimeRanges,
                                    timeStep = schedule.timeStep,
                                    minimumDuration = schedule.minimumTaskDuration,
                                ) ?: return@detectTapGestures
                                onTimeTaskAdd(timeRange.from, timeRange.to)
                            },
                        )
                    },
            )
            schedule.timeTasks.forEach { timeTask ->
                val taskEdit = gestureState.taskEdit?.takeIf { edit ->
                    edit.timeTaskId == timeTask.timeTask.key
                }
                TimelineTaskCard(
                    modifier = Modifier.zIndex(1f),
                    model = timeTask,
                    timeRange = gestureState.fetchTimeRange(timeTask),
                    colors = checkNotNull(taskColors[timeTask.timeTask.key]),
                    isSelected = gestureState.selectedTimeTaskId == timeTask.timeTask.key,
                    isDragging = taskEdit != null,
                    onClick = {
                        gestureState.exitEditMode()
                        editLayoutResult = null
                        onTimeTaskEdit(timeTask.timeTask.key)
                    },
                    onMoveClick = {
                        if (gestureState.selectedTimeTaskId != timeTask.timeTask.key) {
                            editLayoutResult = baseLayoutResult
                        } else if (editLayoutResult == null) {
                            editLayoutResult = baseLayoutResult
                        }
                        gestureState.startEditMode(timeTask)
                    },
                    onEditModeCancel = {
                        gestureState.exitEditMode()
                        editLayoutResult = null
                    },
                    onDoneChange = { onTaskDoneChange(timeTask.timeTask) },
                    onDragStart = { mode ->
                        if (gestureState.selectedTimeTaskId != timeTask.timeTask.key) {
                            editLayoutResult = baseLayoutResult
                        } else if (editLayoutResult == null) {
                            editLayoutResult = baseLayoutResult
                        }
                        gestureState.startEditMode(timeTask)
                        gestureState.startTaskEdit(timeTask, mode)
                    },
                    onDrag = { dragAmount ->
                        val position = taskPositionById[timeTask.timeTask.key]
                        val currentRange = gestureState.fetchTimeRange(timeTask)
                        val dragMode = gestureState.taskEdit?.mode
                        dragPointerPosition = when (dragMode) {
                            TimelineTaskDragMode.MOVE -> {
                                layoutResult.scale.fetchOffset(currentRange.from) +
                                    (position?.height ?: 0f) / 2f
                            }
                            TimelineTaskDragMode.RESIZE_START -> {
                                layoutResult.scale.fetchOffset(currentRange.from)
                            }
                            TimelineTaskDragMode.RESIZE_END -> layoutResult.scale.fetchOffset(currentRange.to)
                            null -> position?.top ?: 0f
                        }
                        val isChanged = gestureState.dragTask(
                            dragAmount = dragAmount,
                            timeTask = timeTask,
                            scale = layoutResult.scale,
                            freeTimeRanges = schedule.freeTimeRanges,
                            timeStep = schedule.timeStep,
                            minimumTaskDuration = schedule.minimumTaskDuration,
                        )
                        val updatedRange = gestureState.fetchTimeRange(timeTask)
                        val changedTime = when (dragMode) {
                            TimelineTaskDragMode.RESIZE_END -> updatedRange.to
                            else -> updatedRange.from
                        }
                        if (isChanged && changedTime.isQuarterHour()) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    },
                    onDragEnd = {
                        dragPointerPosition = Float.NaN
                        gestureState.finishTaskEdit()?.let { timeRange ->
                            onTimeTaskUpdate(timeTask.timeTask.key, timeRange)
                        }
                    },
                    onDragCancel = {
                        dragPointerPosition = Float.NaN
                        gestureState.cancelTaskDrag()
                    },
                )
            }
            visibleHourTimes.forEach { hourTime ->
                TimelineTimeLabel(
                    text = rememberTimeTitle(hourTime),
                    isEmphasized = false,
                )
            }
            visibleTaskRanges.forEach { timeRange ->
                TimelineTimeLabel(
                    text = rememberTimeTitle(timeRange.from),
                    isEmphasized = true,
                )
            }
            groupEndTaskIndexes.forEach { index ->
                TimelineTimeLabel(
                    text = rememberTimeTitle(visibleTaskRanges[index].to),
                    isEmphasized = false,
                )
            }
            visibleCurrentTime?.let { time ->
                TimelineNowLine(
                    modifier = Modifier.zIndex(1.5f),
                    color = nowColor,
                )
                TimelineNowLabel(
                    modifier = Modifier.zIndex(2f),
                    time = time,
                )
            }
        },
    ) { measurables, constraints ->
        val axisWidth = TIMELINE_AXIS_WIDTH.roundToPx()
        val backgroundIndex = 0
        val taskStart = backgroundIndex + 1
        val hourStart = taskStart + schedule.timeTasks.size
        val startLabelStart = hourStart + visibleHourTimes.size
        val endLabelStart = startLabelStart + schedule.timeTasks.size
        val nowLineIndex = endLabelStart + groupEndTaskIndexes.size
        val nowLabelIndex = nowLineIndex + if (visibleCurrentTime != null) 1 else 0
        val taskStartPadding = TIMELINE_TASK_START_PADDING.roundToPx()
        val taskWidth = (
            constraints.maxWidth - axisWidth - taskStartPadding -
                TIMELINE_TASK_END_PADDING.roundToPx()
            ).coerceAtLeast(0)
        val backgroundPlaceable = measurables[backgroundIndex].measure(
            Constraints.fixed(
                width = constraints.maxWidth,
                height = layoutResult.scale.height.roundToInt(),
            ),
        )
        val taskPlaceables = schedule.timeTasks.mapIndexed { index, timeTask ->
            val position = checkNotNull(taskPositionById[timeTask.timeTask.key])
            measurables[taskStart + index].measure(
                Constraints.fixed(
                    width = taskWidth,
                    height = position.height.roundToInt().coerceAtLeast(1),
                ),
            )
        }
        val labelConstraints = Constraints(maxWidth = axisWidth - TIMELINE_LABEL_END_PADDING.roundToPx())
        val hourPlaceables = visibleHourTimes.indices.map { index ->
            measurables[hourStart + index].measure(labelConstraints)
        }
        val startLabelPlaceables = schedule.timeTasks.indices.map { index ->
            measurables[startLabelStart + index].measure(labelConstraints)
        }
        val endLabelPlaceables = groupEndTaskIndexes.indices.map { index ->
            measurables[endLabelStart + index].measure(labelConstraints)
        }
        val nowLinePlaceable = visibleCurrentTime?.let {
            measurables[nowLineIndex].measure(
                Constraints.fixed(
                    width = constraints.maxWidth,
                    height = NOW_LINE_CONTAINER_HEIGHT.roundToPx(),
                ),
            )
        }
        val nowPlaceable = visibleCurrentTime?.let {
            measurables[nowLabelIndex].measure(
                Constraints(maxWidth = constraints.maxWidth - axisWidth),
            )
        }

        layout(constraints.maxWidth, layoutResult.scale.height.roundToInt()) {
            backgroundPlaceable.placeRelative(0, 0)
            schedule.timeTasks.forEachIndexed { index, timeTask ->
                val position = checkNotNull(taskPositionById[timeTask.timeTask.key])
                taskPlaceables[index].placeRelative(
                    axisWidth + taskStartPadding,
                    position.top.roundToInt(),
                )
            }
            visibleHourTimes.forEachIndexed { index, hourTime ->
                val placeable = hourPlaceables[index]
                val offset = layoutResult.scale.fetchOffset(hourTime) - placeable.height / 2f
                placeable.placeRelative(0, offset.roundToInt())
            }
            schedule.timeTasks.forEachIndexed { index, timeTask ->
                val placeable = startLabelPlaceables[index]
                val offset = taskStartOffsets[index]
                placeable.placeRelative(0, (offset + 4.dp.toPx()).roundToInt())
            }
            groupEndTaskIndexes.forEachIndexed { placeableIndex, taskIndex ->
                val placeable = endLabelPlaceables[placeableIndex]
                val offset = taskEndOffsets[taskIndex]
                placeable.placeRelative(0, (offset - placeable.height / 2f).roundToInt())
            }
            if (nowLinePlaceable != null && nowPlaceable != null) {
                val nowOffset = layoutResult.scale.fetchOffset(checkNotNull(visibleCurrentTime))
                nowLinePlaceable.placeRelative(
                    0,
                    (nowOffset - nowLinePlaceable.height / 2f).roundToInt(),
                )
                nowPlaceable.placeRelative(axisWidth, (nowOffset - nowPlaceable.height / 2f).roundToInt())
            }
        }
    }
}

@Composable
private fun TimelineNowLine(
    modifier: Modifier,
    color: Color,
) {
    Canvas(modifier = modifier) {
        val axisOffset = TIMELINE_AXIS_WIDTH.toPx()
        val centerOffset = size.height / 2f

        drawLine(
            color = color,
            start = Offset(axisOffset, centerOffset),
            end = Offset(size.width, centerOffset),
            strokeWidth = 1.5.dp.toPx(),
        )
        drawCircle(
            color = color,
            radius = 4.dp.toPx(),
            center = Offset(axisOffset, centerOffset),
        )
    }
}

@Composable
private fun TimelineTimeLabel(
    text: String,
    isEmphasized: Boolean,
) {
    Text(
        modifier = Modifier.padding(start = 8.dp),
        text = text,
        color = when (isEmphasized) {
            true -> MaterialTheme.colorScheme.onSurface
            false -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        maxLines = 1,
        overflow = TextOverflow.Clip,
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
private fun TimelineNowLabel(
    modifier: Modifier,
    time: Date,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primary,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
            text = "${rememberTimeTitle(time)} · ${HomeThemeRes.strings.timelineNowTitle}",
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun rememberTimeTitle(time: Date): String {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    return remember(time) { timeFormat.format(time) }
}

private fun fetchCreateTimeRange(
    offset: Float,
    scale: TimelineScale,
    freeTimeRanges: List<TimeRange>,
    timeStep: Long,
    minimumDuration: Long,
): TimeRange? {
    val desiredTime = scale.fetchTime(offset).snap(scale.dayTimeRange.from, timeStep)
    val freeTimeRange = freeTimeRanges.find { timeRange ->
        desiredTime >= timeRange.from && desiredTime < timeRange.to
    } ?: return null
    val latestStartTime = freeTimeRange.to.time - minimumDuration
    if (latestStartTime < freeTimeRange.from.time) return null

    val startTime = Date(desiredTime.time.coerceIn(freeTimeRange.from.time, latestStartTime))
    val endTime = Date(
        (startTime.time + DEFAULT_TASK_DURATION).coerceAtMost(freeTimeRange.to.time),
    )
    return TimeRange(startTime, endTime)
}

private fun Date.snap(
    startTime: Date,
    timeStep: Long,
): Date {
    val steps = ((time - startTime.time) / timeStep.toDouble()).roundToLong()
    return Date(startTime.time + steps * timeStep)
}

private fun Date.isQuarterHour(): Boolean {
    return Calendar.getInstance().apply { time = this@isQuarterHour }
        .get(Calendar.MINUTE) % QUARTER_HOUR_MINUTES == 0
}

private const val HOURS_IN_DAY = 24
private const val QUARTER_HOUR_MINUTES = 15
private const val TIMELINE_FREE_TIME_SCALE = 0.64f
private const val TIMELINE_LONG_TASK_SCALE = 0.5f
private const val DEFAULT_TASK_DURATION = 30L * Constants.Date.MILLIS_IN_MINUTE
private const val TIMELINE_LONG_TASK_THRESHOLD = 2L * Constants.Date.MILLIS_IN_HOUR
private val TIMELINE_HOUR_HEIGHT = 80.dp
private val TIMELINE_TASK_MIN_HEIGHT = 58.dp
private val TIMELINE_TASK_MAX_HEIGHT = 720.dp
private val TIMELINE_FREE_TIME_MIN_HEIGHT = 10.dp
private val TIMELINE_TASK_SPACE = 2.dp
private val TIMELINE_VERTICAL_PADDING = 20.dp
private val TIMELINE_AXIS_WIDTH = 68.dp
private val TIMELINE_LINE_START_GAP = 7.dp
private val TIMELINE_TASK_START_PADDING = 8.dp
private val TIMELINE_TASK_END_PADDING = 12.dp
private val TIMELINE_LABEL_END_PADDING = 6.dp
private val LABEL_MIN_DISTANCE = 32.dp
private val HALF_HOUR_MIN_DISTANCE = 64.dp
private val AUTO_SCROLL_EDGE_SIZE = 72.dp
private val AUTO_SCROLL_MAX_STEP = 11.dp
private val NOW_LINE_CONTAINER_HEIGHT = 8.dp
