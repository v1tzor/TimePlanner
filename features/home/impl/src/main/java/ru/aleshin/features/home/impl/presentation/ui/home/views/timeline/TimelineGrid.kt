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
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.impl.presentation.models.TimelineScheduleUi
import ru.aleshin.features.home.impl.presentation.models.TimelineTaskUpdateRequestUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.theme.tokens.fetchHomeCategoryColors
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
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
    pendingTimeTaskUpdate: TimelineTaskUpdateRequestUi?,
    failedTimeTaskUpdate: TimelineTaskUpdateRequestUi?,
    taskMaxWidth: Dp?,
    scrollState: ScrollState,
    viewportHeight: Int,
    gestureState: TimelineGestureState,
    onTimeTaskEdit: (Long) -> Unit,
    onTaskDoneChange: (TimeTaskUi) -> Unit,
    onTimeTaskAdd: (Date, Date) -> Unit,
    onTimeTaskUpdate: (TimelineTaskUpdateRequestUi) -> Unit,
    onInitialTimePositioned: (Float) -> Unit,
) {
    val density = LocalDensity.current
    val axisWidth = when (rememberTimelineUses24HourFormat()) {
        true -> TIMELINE_24_HOUR_AXIS_WIDTH
        false -> TIMELINE_12_HOUR_AXIS_WIDTH
    }
    val hapticFeedback = LocalHapticFeedback.current
    val surfaceColor = MaterialTheme.colorScheme.surface
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val railColor = MaterialTheme.colorScheme.outline
    val nowColor = MaterialTheme.colorScheme.primary
    val taskMaxWidthPx = with(density) {
        taskMaxWidth?.roundToPx()
    }
    val layoutTaskKey = schedule.timeTasks.map { timeTask ->
        timeTask.timeTask.key to timeTask.visibleTimeRange
    }
    val baseLayoutResult = remember(schedule.dayTimeRange, layoutTaskKey, density) {
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
    var editLayoutTaskKey by remember(schedule.date) {
        mutableStateOf<List<Pair<Long, TimeRange>>?>(null)
    }
    val isEditLayoutCompatible = isTimelineEditLayoutCompatible(
        frozenTaskKey = editLayoutTaskKey,
        currentTaskKey = layoutTaskKey,
        selectedTimeTaskId = gestureState.selectedTimeTaskId,
        isDragging = gestureState.taskEdit != null,
    )
    val layoutResult = editLayoutResult?.takeIf { isEditLayoutCompatible } ?: baseLayoutResult
    val taskColors = remember(schedule.timeTasks, surfaceColor) {
        schedule.timeTasks.associate { timeTask ->
            timeTask.timeTask.key to fetchHomeCategoryColors(
                categoryId = timeTask.timeTask.category.id,
                surface = surfaceColor,
            )
        }
    }
    val hourTimes = remember(schedule.dayTimeRange) {
        fetchTimelineHourTimes(schedule.dayTimeRange)
    }
    val baseTaskPositionById = remember(layoutResult) {
        layoutResult.taskPositions.associateBy { position -> position.timeTaskId }
    }
    val minimumTaskHeight = with(density) { TIMELINE_TASK_MIN_HEIGHT.toPx() }
    val taskSpace = with(density) { TIMELINE_TASK_SPACE.toPx() }
    var fixedMoveTaskHeight by remember(schedule.date) {
        mutableFloatStateOf(Float.NaN)
    }
    val visibleTaskRanges = schedule.timeTasks.map { timeTask ->
        gestureState.fetchTimeRange(timeTask, pendingTimeTaskUpdate)
            .intersect(schedule.dayTimeRange)
            ?: timeTask.visibleTimeRange
    }
    val taskPositions = schedule.timeTasks.mapIndexed { index, timeTask ->
        val basePosition = checkNotNull(baseTaskPositionById[timeTask.timeTask.key])
        val isSelected = gestureState.selectedTimeTaskId == timeTask.timeTask.key
        val dragMode = gestureState.lastDragMode
        val hasPreviewRange = visibleTaskRanges[index] != timeTask.visibleTimeRange
        if ((isSelected && dragMode != null) || hasPreviewRange) {
            TimelineLayout.calculateEditedTaskPosition(
                basePosition = basePosition,
                timeRange = visibleTaskRanges[index],
                scale = layoutResult.scale,
                minimumTaskHeight = minimumTaskHeight,
                dragMode = if (isSelected) dragMode else null,
                taskSpace = taskSpace,
                fixedMoveHeight = fixedMoveTaskHeight.takeIf {
                    isSelected &&
                        dragMode == TimelineTaskDragMode.MOVE &&
                        !fixedMoveTaskHeight.isNaN()
                },
            )
        } else {
            basePosition
        }
    }
    val verticalPadding = with(density) { TIMELINE_VERTICAL_PADDING.toPx() }
    val timelineHeight = maxOf(
        layoutResult.scale.height,
        taskPositions.maxOfOrNull { position ->
            position.top + position.height + verticalPadding
        } ?: layoutResult.scale.height,
    )
    val taskPositionById = taskPositions.associateBy { position -> position.timeTaskId }
    val taskStartOffsets = visibleTaskRanges.map { timeRange ->
        layoutResult.scale.fetchOffset(timeRange.from)
    }
    val taskEndOffsets = visibleTaskRanges.map { timeRange ->
        layoutResult.scale.fetchOffset(timeRange.to)
    }
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
    var dragContentAnchorY by remember { mutableFloatStateOf(Float.NaN) }
    var displayedScale by remember(schedule.date) {
        mutableStateOf<TimelineScale?>(null)
    }
    val selectedTimeTask by rememberUpdatedState(
        schedule.timeTasks.find { timeTask ->
            timeTask.timeTask.key == gestureState.taskEdit?.timeTaskId
        },
    )
    val currentLayoutResult by rememberUpdatedState(layoutResult)
    val currentSchedule by rememberUpdatedState(schedule)
    val currentViewportHeight by rememberUpdatedState(viewportHeight)
    val currentOnTimeTaskAdd by rememberUpdatedState(onTimeTaskAdd)
    val isTimeTaskUpdatePending = gestureState.isTimeTaskUpdatePending(
        pendingTimeTaskUpdate,
    )
    val currentIsTimeTaskUpdatePending by rememberUpdatedState(isTimeTaskUpdatePending)

    LaunchedEffect(isEditLayoutCompatible, layoutTaskKey) {
        if (!isEditLayoutCompatible) {
            gestureState.exitEditMode()
            editLayoutResult = null
            editLayoutTaskKey = null
            fixedMoveTaskHeight = Float.NaN
        } else if (editLayoutTaskKey != null && editLayoutTaskKey != layoutTaskKey) {
            editLayoutTaskKey = layoutTaskKey
        }
    }

    LaunchedEffect(
        schedule.timeTasks,
        gestureState.selectedTimeTaskId,
        gestureState.taskEdit != null,
        pendingTimeTaskUpdate,
        failedTimeTaskUpdate,
    ) {
        schedule.timeTasks.forEach { timeTask ->
            gestureState.synchronize(timeTask, pendingTimeTaskUpdate)
        }
        failedTimeTaskUpdate?.let { request ->
            val timeTask = schedule.timeTasks.find { timeTask ->
                timeTask.timeTask.key == request.timeTaskId
            }
            gestureState.rejectTimeTaskUpdate(request, timeTask)
        }
        val selectedTaskExists = schedule.timeTasks.any { timeTask ->
            timeTask.timeTask.key == gestureState.selectedTimeTaskId
        }
        if (!selectedTaskExists && gestureState.selectedTimeTaskId != null) {
            gestureState.exitEditMode()
            editLayoutResult = null
            editLayoutTaskKey = null
            fixedMoveTaskHeight = Float.NaN
        }
    }

    LaunchedEffect(layoutResult, schedule.initialTime) {
        onInitialTimePositioned(layoutResult.scale.fetchOffset(schedule.initialTime))
    }

    LaunchedEffect(layoutResult.scale, viewportHeight, scrollState.maxValue) {
        val previousScale = displayedScale
        val currentScale = layoutResult.scale
        if (previousScale != null && previousScale !== currentScale && viewportHeight > 0) {
            val anchorOffset = scrollState.value + viewportHeight / 2f
            val anchorTime = previousScale.fetchTime(anchorOffset)
            withFrameNanos { }
            val targetOffset = (
                currentScale.fetchOffset(anchorTime) - viewportHeight / 2f
                ).roundToInt().coerceIn(0, scrollState.maxValue)
            scrollState.scrollTo(targetOffset)
        }
        displayedScale = currentScale
    }

    LaunchedEffect(gestureState.taskEdit?.sessionId, scrollState) {
        val sessionId = gestureState.taskEdit?.sessionId ?: return@LaunchedEffect
        val edgeSize = with(density) { AUTO_SCROLL_EDGE_SIZE.toPx() }
        val maximumSpeed = with(density) { AUTO_SCROLL_MAX_SPEED.toPx() }

        scrollState.scroll(MutatePriority.UserInput) {
            var previousFrameTime: Long? = null
            while (gestureState.taskEdit?.sessionId == sessionId) {
                val frameTime = withFrameNanos { time -> time }
                val elapsedSeconds = previousFrameTime?.let { previousTime ->
                    ((frameTime - previousTime) / NANOS_IN_SECOND)
                        .coerceIn(0f, AUTO_SCROLL_MAX_FRAME_SECONDS)
                } ?: 0f
                previousFrameTime = frameTime
                if (elapsedSeconds == 0f) continue

                val viewportHeight = currentViewportHeight
                val pointerPosition = dragContentAnchorY
                val timeTask = selectedTimeTask ?: continue
                if (pointerPosition.isNaN() || viewportHeight <= 0) continue

                val viewportPosition = pointerPosition - scrollState.value
                val scrollStep = calculateTimelineAutoScrollStep(
                    viewportPosition = viewportPosition,
                    viewportHeight = viewportHeight.toFloat(),
                    edgeSize = edgeSize,
                    maximumSpeed = maximumSpeed,
                    elapsedSeconds = elapsedSeconds,
                )
                if (scrollStep != 0f) {
                    val consumedScroll = scrollBy(scrollStep)
                    if (consumedScroll == 0f) continue

                    dragContentAnchorY += consumedScroll
                    val activeLayoutResult = currentLayoutResult
                    val activeSchedule = currentSchedule
                    gestureState.dragTask(
                        dragAmount = consumedScroll,
                        timeTask = timeTask,
                        scale = activeLayoutResult.scale,
                        freeTimeRanges = activeSchedule.freeTimeRanges,
                        timeStep = activeSchedule.timeStep,
                        minimumTaskDuration = activeSchedule.minimumTaskDuration,
                    )
                }
            }
        }
    }

    val drawModifier = Modifier.drawWithCache {
        val axisOffset = axisWidth.toPx()
        val lineStart = axisOffset + TIMELINE_LINE_START_GAP.toPx()
        val dottedEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 5.dp.toPx()))
        val halfHourDistance = HALF_HOUR_MIN_DISTANCE.toPx()
        val hourOffsets = hourTimes.map { hourTime -> layoutResult.scale.fetchOffset(hourTime) }

        onDrawBehind {
            drawLine(
                color = railColor.copy(alpha = 0.55f),
                start = Offset(axisOffset, TIMELINE_VERTICAL_PADDING.toPx()),
                end = Offset(
                    axisOffset,
                    layoutResult.scale.fetchOffset(schedule.dayTimeRange.to),
                ),
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
            .height(with(density) { timelineHeight.toDp() })
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
                                    editLayoutTaskKey = null
                                    fixedMoveTaskHeight = Float.NaN
                                    return@detectTapGestures
                                }
                                if (currentIsTimeTaskUpdatePending) {
                                    return@detectTapGestures
                                }
                                if (offset.x < axisWidth.toPx()) {
                                    return@detectTapGestures
                                }

                                val timeRange = fetchCreateTimeRange(
                                    offset = offset.y,
                                    scale = layoutResult.scale,
                                    freeTimeRanges = schedule.freeTimeRanges,
                                    timeStep = schedule.timeStep,
                                    minimumDuration = schedule.minimumTaskDuration,
                                ) ?: return@detectTapGestures
                                currentOnTimeTaskAdd(timeRange.from, timeRange.to)
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
                    timeRange = gestureState.fetchTimeRange(timeTask, pendingTimeTaskUpdate),
                    colors = checkNotNull(taskColors[timeTask.timeTask.key]),
                    isSelected = gestureState.selectedTimeTaskId == timeTask.timeTask.key,
                    isDragging = taskEdit != null,
                    onClick = {
                        if (!isTimeTaskUpdatePending) {
                            gestureState.exitEditMode()
                            editLayoutResult = null
                            editLayoutTaskKey = null
                            fixedMoveTaskHeight = Float.NaN
                            onTimeTaskEdit(timeTask.timeTask.key)
                        }
                    },
                    onMoveClick = {
                        if (gestureState.selectedTimeTaskId != timeTask.timeTask.key) {
                            editLayoutResult = baseLayoutResult
                            editLayoutTaskKey = layoutTaskKey
                        } else if (editLayoutResult == null) {
                            editLayoutResult = baseLayoutResult
                            editLayoutTaskKey = layoutTaskKey
                        }
                        gestureState.startEditMode(timeTask, pendingTimeTaskUpdate)
                    },
                    onEditModeCancel = {
                        gestureState.exitEditMode()
                        editLayoutResult = null
                        editLayoutTaskKey = null
                        fixedMoveTaskHeight = Float.NaN
                    },
                    onDoneChange = {
                        if (!isTimeTaskUpdatePending) {
                            onTaskDoneChange(timeTask.timeTask)
                        }
                    },
                    onDragStart = { mode ->
                        if (gestureState.selectedTimeTaskId != timeTask.timeTask.key) {
                            editLayoutResult = baseLayoutResult
                            editLayoutTaskKey = layoutTaskKey
                        } else if (editLayoutResult == null) {
                            editLayoutResult = baseLayoutResult
                            editLayoutTaskKey = layoutTaskKey
                        }
                        gestureState.startEditMode(timeTask, pendingTimeTaskUpdate)
                        gestureState.startTaskEdit(
                            timeTask = timeTask,
                            mode = mode,
                            externalPendingUpdate = pendingTimeTaskUpdate,
                        ).also { isStarted ->
                            if (isStarted) {
                                val taskPosition = checkNotNull(
                                    taskPositionById[timeTask.timeTask.key],
                                )
                                if (mode == TimelineTaskDragMode.MOVE) {
                                    fixedMoveTaskHeight = taskPosition.height
                                }
                                dragContentAnchorY = fetchDragContentAnchor(
                                    position = taskPosition,
                                    mode = mode,
                                )
                            }
                        }
                    },
                    onDrag = { dragAmount ->
                        val dragMode = gestureState.taskEdit?.mode
                        if (dragMode != null && !dragContentAnchorY.isNaN()) {
                            dragContentAnchorY += dragAmount
                        }
                        val isChanged = gestureState.dragTask(
                            dragAmount = dragAmount,
                            timeTask = timeTask,
                            scale = layoutResult.scale,
                            freeTimeRanges = schedule.freeTimeRanges,
                            timeStep = schedule.timeStep,
                            minimumTaskDuration = schedule.minimumTaskDuration,
                        )
                        val updatedRange = gestureState.fetchTimeRange(
                            timeTask,
                            pendingTimeTaskUpdate,
                        )
                        val changedTime = when (dragMode) {
                            TimelineTaskDragMode.RESIZE_END -> updatedRange.to
                            else -> updatedRange.from
                        }
                        if (isChanged && changedTime.isQuarterHour()) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    },
                    onDragEnd = {
                        dragContentAnchorY = Float.NaN
                        gestureState.finishTaskEdit()?.let(onTimeTaskUpdate)
                    },
                    onDragCancel = {
                        dragContentAnchorY = Float.NaN
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
                    axisWidth = axisWidth,
                )
                TimelineNowLabel(
                    modifier = Modifier.zIndex(2f),
                    time = time,
                )
            }
        },
    ) { measurables, constraints ->
        val axisWidthPx = axisWidth.roundToPx()
        val backgroundIndex = 0
        val taskStart = backgroundIndex + 1
        val hourStart = taskStart + schedule.timeTasks.size
        val startLabelStart = hourStart + visibleHourTimes.size
        val endLabelStart = startLabelStart + schedule.timeTasks.size
        val nowLineIndex = endLabelStart + groupEndTaskIndexes.size
        val nowLabelIndex = nowLineIndex + if (visibleCurrentTime != null) 1 else 0
        val taskStartPadding = TIMELINE_TASK_START_PADDING.roundToPx()
        val availableTaskWidth = (
            constraints.maxWidth - axisWidthPx - taskStartPadding -
                TIMELINE_TASK_END_PADDING.roundToPx()
            ).coerceAtLeast(0)
        val taskWidth = taskMaxWidthPx?.let { maximumWidth ->
            minOf(availableTaskWidth, maximumWidth)
        } ?: availableTaskWidth
        val backgroundPlaceable = measurables[backgroundIndex].measure(
            Constraints.fixed(
                width = constraints.maxWidth,
                height = timelineHeight.roundToInt(),
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
        val labelConstraints = Constraints(
            maxWidth = axisWidthPx - TIMELINE_LABEL_END_PADDING.roundToPx(),
        )
        val hourPlaceables = visibleHourTimes.indices.map { index ->
            measurables[hourStart + index].measure(labelConstraints)
        }
        val startLabelPlaceables = schedule.timeTasks.indices.map { index ->
            measurables[startLabelStart + index].measure(labelConstraints)
        }
        val endLabelPlaceables = groupEndTaskIndexes.indices.map { index ->
            measurables[endLabelStart + index].measure(labelConstraints)
        }
        val labelAnchorOffsets = visibleHourTimes.map { hourTime ->
            layoutResult.scale.fetchOffset(hourTime)
        } + taskStartOffsets + groupEndTaskIndexes.map { index ->
            taskEndOffsets[index]
        }
        val labelHeights = hourPlaceables.map { placeable ->
            placeable.height.toFloat()
        } + startLabelPlaceables.map { placeable ->
            placeable.height.toFloat()
        } + endLabelPlaceables.map { placeable ->
            placeable.height.toFloat()
        }
        val labelTopPositions = TimelineLayout.calculateLabelTopPositions(
            anchorOffsets = labelAnchorOffsets,
            labelHeights = labelHeights,
            minimumGap = LABEL_MIN_GAP.toPx(),
            minimumTop = 0f,
            maximumBottom = timelineHeight,
        )
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
                Constraints(maxWidth = constraints.maxWidth - axisWidthPx),
            )
        }

        layout(constraints.maxWidth, timelineHeight.roundToInt()) {
            backgroundPlaceable.placeRelative(0, 0)
            schedule.timeTasks.forEachIndexed { index, timeTask ->
                val position = checkNotNull(taskPositionById[timeTask.timeTask.key])
                taskPlaceables[index].placeRelative(
                    axisWidthPx + taskStartPadding,
                    position.top.roundToInt(),
                )
            }
            visibleHourTimes.forEachIndexed { index, hourTime ->
                val placeable = hourPlaceables[index]
                placeable.placeRelative(0, labelTopPositions[index].roundToInt())
            }
            schedule.timeTasks.forEachIndexed { index, timeTask ->
                val placeable = startLabelPlaceables[index]
                val labelIndex = visibleHourTimes.size + index
                placeable.placeRelative(0, labelTopPositions[labelIndex].roundToInt())
            }
            groupEndTaskIndexes.forEachIndexed { placeableIndex, taskIndex ->
                val placeable = endLabelPlaceables[placeableIndex]
                val labelIndex = visibleHourTimes.size +
                    schedule.timeTasks.size +
                    placeableIndex
                placeable.placeRelative(0, labelTopPositions[labelIndex].roundToInt())
            }
            if (nowLinePlaceable != null && nowPlaceable != null) {
                val nowOffset = layoutResult.scale.fetchOffset(checkNotNull(visibleCurrentTime))
                nowLinePlaceable.placeRelative(
                    0,
                    (nowOffset - nowLinePlaceable.height / 2f).roundToInt(),
                )
                nowPlaceable.placeRelative(axisWidthPx, (nowOffset - nowPlaceable.height / 2f).roundToInt())
            }
        }
    }
}

@Composable
private fun TimelineNowLine(
    modifier: Modifier,
    color: Color,
    axisWidth: Dp,
) {
    Canvas(modifier = modifier) {
        val axisOffset = axisWidth.toPx()
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
    val timeFormat: DateFormat = rememberTimelineTimeFormatter()
    return remember(time, timeFormat) { timeFormat.format(time) }
}

private fun fetchDragContentAnchor(
    position: TimelineTaskPosition,
    mode: TimelineTaskDragMode,
): Float {
    return when (mode) {
        TimelineTaskDragMode.MOVE -> position.top + position.height / 2f
        TimelineTaskDragMode.RESIZE_START -> position.top
        TimelineTaskDragMode.RESIZE_END -> position.top + position.height
    }
}

internal fun calculateTimelineAutoScrollStep(
    viewportPosition: Float,
    viewportHeight: Float,
    edgeSize: Float,
    maximumSpeed: Float,
    elapsedSeconds: Float,
): Float {
    if (viewportHeight <= 0f || edgeSize <= 0f) return 0f

    val effectiveEdgeSize = minOf(edgeSize, viewportHeight / 2f)
    val maximumStep = maximumSpeed * elapsedSeconds
    return when {
        viewportPosition < effectiveEdgeSize -> {
            val edgeProgress = 1f -
                viewportPosition.coerceAtLeast(0f) / effectiveEdgeSize
            -maximumStep * edgeProgress
        }
        viewportPosition > viewportHeight - effectiveEdgeSize -> {
            val edgeProgress = (viewportPosition - viewportHeight + effectiveEdgeSize) /
                effectiveEdgeSize
            maximumStep * edgeProgress.coerceIn(0f, 1f)
        }
        else -> 0f
    }
}

internal fun isTimelineEditLayoutCompatible(
    frozenTaskKey: List<Pair<Long, TimeRange>>?,
    currentTaskKey: List<Pair<Long, TimeRange>>,
    selectedTimeTaskId: Long?,
    isDragging: Boolean,
): Boolean {
    if (frozenTaskKey == null || frozenTaskKey == currentTaskKey) return true
    if (isDragging || selectedTimeTaskId == null || frozenTaskKey.size != currentTaskKey.size) {
        return false
    }

    val frozenTaskRanges = frozenTaskKey.toMap()
    val currentTaskRanges = currentTaskKey.toMap()
    if (frozenTaskRanges.size != frozenTaskKey.size ||
        currentTaskRanges.size != currentTaskKey.size ||
        selectedTimeTaskId !in frozenTaskRanges ||
        selectedTimeTaskId !in currentTaskRanges
    ) {
        return false
    }
    return currentTaskRanges.all { (timeTaskId, timeRange) ->
        timeTaskId == selectedTimeTaskId || frozenTaskRanges[timeTaskId] == timeRange
    }
}

internal fun fetchTimelineHourTimes(
    dayTimeRange: TimeRange,
    timeZone: TimeZone = TimeZone.getDefault(),
): List<Date> {
    val hourTimes = mutableListOf(dayTimeRange.from)
    var currentTime = dayTimeRange.from

    while (currentTime < dayTimeRange.to) {
        val nextTime = Calendar.getInstance(timeZone).apply {
            time = currentTime
            add(Calendar.HOUR_OF_DAY, 1)
        }.time
        if (nextTime >= dayTimeRange.to) break

        hourTimes.add(nextTime)
        currentTime = nextTime
    }
    hourTimes.add(dayTimeRange.to)
    return hourTimes
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

private fun TimeRange.intersect(other: TimeRange): TimeRange? {
    val startTime = maxOf(from.time, other.from.time)
    val endTime = minOf(to.time, other.to.time)
    return if (startTime < endTime) {
        TimeRange(Date(startTime), Date(endTime))
    } else {
        null
    }
}

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
private val TIMELINE_VERTICAL_PADDING = TIMELINE_TASK_MIN_HEIGHT
private val TIMELINE_24_HOUR_AXIS_WIDTH = 68.dp
private val TIMELINE_12_HOUR_AXIS_WIDTH = 84.dp
private val TIMELINE_LINE_START_GAP = 7.dp
private val TIMELINE_TASK_START_PADDING = 8.dp
private val TIMELINE_TASK_END_PADDING = 12.dp
private val TIMELINE_LABEL_END_PADDING = 6.dp
private val LABEL_MIN_DISTANCE = 32.dp
private val LABEL_MIN_GAP = 2.dp
private val HALF_HOUR_MIN_DISTANCE = 64.dp
private val AUTO_SCROLL_EDGE_SIZE = 72.dp
private val AUTO_SCROLL_MAX_SPEED = 660.dp
private val NOW_LINE_CONTAINER_HEIGHT = 8.dp
private const val AUTO_SCROLL_MAX_FRAME_SECONDS = 0.05f
private const val NANOS_IN_SECOND = 1_000_000_000f
