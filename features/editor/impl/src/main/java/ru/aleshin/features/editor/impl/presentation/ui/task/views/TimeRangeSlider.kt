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
package ru.aleshin.features.editor.impl.presentation.ui.task.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * @author Stanislav Aleshin on 18.07.2026.
 */
@Composable
internal fun TimeRangeSlider(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    scheduleDate: Date,
    timeRange: TimeRange,
    onTimeRangeChange: (TimeRange) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val actualTimeRange by rememberUpdatedState(timeRange)
    val actualOnTimeRangeChange by rememberUpdatedState(onTimeRangeChange)
    val timeFormat = remember { SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT) }
    var activeHandle by remember { mutableStateOf<TimeRangeSliderHandle?>(null) }
    var lastHandle by rememberSaveable { mutableStateOf(TimeRangeSliderHandle.END) }

    val startMinute = fetchTimeSliderMinute(scheduleDate, timeRange.from, false)
    val endMinute = fetchTimeSliderMinute(scheduleDate, timeRange.to, true)
    val startFraction = startMinute / SLIDER_MINUTES_IN_DAY.toFloat()
    val endFraction = endMinute / SLIDER_MINUTES_IN_DAY.toFloat()
    val dayStart = scheduleDate.startThisDay()
    val nextDayStart = dayStart.shiftDay(1)
    val duration = timeRange.to.time - timeRange.from.time
    val isFullDay = duration >= nextDayStart.time - dayStart.time
    val isOvernight = timeRange.to.startThisDay() > dayStart && endMinute < startMinute
    val startTimeTitle = timeFormat.format(timeRange.from)
    val endTimeTitle = timeFormat.format(timeRange.to)
    val contentAlpha = if (enabled) 1f else DISABLED_CONTENT_ALPHA
    val activeColor = when (isError) {
        true -> MaterialTheme.colorScheme.error
        false -> MaterialTheme.colorScheme.primary
    }
    val inactiveColor = when (isError) {
        true -> MaterialTheme.colorScheme.errorContainer
        false -> MaterialTheme.colorScheme.outlineVariant
    }
    val handleContentColor = when (isError) {
        true -> MaterialTheme.colorScheme.onError
        false -> MaterialTheme.colorScheme.onPrimary
    }

    val gestureModifier = if (enabled) {
        Modifier
            .pointerInput(scheduleDate) {
                val trackStart = TIME_SLIDER_HANDLE_TOUCH_SIZE.toPx() / 2f
                val trackEnd = size.width - trackStart
                val trackWidth = trackEnd - trackStart
                detectTapGestures { offset ->
                    val currentRange = actualTimeRange
                    val selectedHandle = fetchTimeSliderHandle(
                        position = offset.x,
                        trackStart = trackStart,
                        trackWidth = trackWidth,
                        scheduleDate = scheduleDate,
                        timeRange = currentRange,
                        lastHandle = lastHandle,
                    )
                    val minute = fetchTimeSliderMinute(
                        offset = offset.x,
                        trackStart = trackStart,
                        trackEnd = trackEnd,
                        maximum = when (selectedHandle) {
                            TimeRangeSliderHandle.START -> SLIDER_MAXIMUM_START_MINUTE
                            TimeRangeSliderHandle.END -> SLIDER_MINUTES_IN_DAY
                        },
                    )
                    val updatedRange = currentRange.updateTimeSliderRange(
                        scheduleDate = scheduleDate,
                        handle = selectedHandle,
                        minute = minute,
                    )
                    if (updatedRange != currentRange) {
                        actualOnTimeRangeChange(updatedRange)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    lastHandle = selectedHandle
                }
            }
            .pointerInput(scheduleDate) {
                val trackStart = TIME_SLIDER_HANDLE_TOUCH_SIZE.toPx() / 2f
                val trackEnd = size.width - trackStart
                val trackWidth = trackEnd - trackStart
                var selectedHandle: TimeRangeSliderHandle? = null
                var lastHapticSegment: Int? = null

                fun updateTimeRange(offset: Float) {
                    val handle = selectedHandle ?: return
                    val minute = fetchTimeSliderMinute(
                        offset = offset,
                        trackStart = trackStart,
                        trackEnd = trackEnd,
                        maximum = when (handle) {
                            TimeRangeSliderHandle.START -> SLIDER_MAXIMUM_START_MINUTE
                            TimeRangeSliderHandle.END -> SLIDER_MINUTES_IN_DAY
                        },
                    )
                    val range = actualTimeRange
                    val updatedRange = range.updateTimeSliderRange(
                        scheduleDate = scheduleDate,
                        handle = handle,
                        minute = minute,
                    )
                    if (updatedRange != range) {
                        actualOnTimeRangeChange(updatedRange)
                        val hapticSegment = minute / HAPTIC_MINUTE_STEP
                        if (lastHapticSegment != hapticSegment) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            lastHapticSegment = hapticSegment
                        }
                    }
                }

                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        val currentRange = actualTimeRange
                        selectedHandle = fetchTimeSliderHandle(
                            position = offset.x,
                            trackStart = trackStart,
                            trackWidth = trackWidth,
                            scheduleDate = scheduleDate,
                            timeRange = currentRange,
                            lastHandle = lastHandle,
                        )
                        val currentMinute = when (selectedHandle) {
                            TimeRangeSliderHandle.START -> fetchTimeSliderMinute(
                                scheduleDate = scheduleDate,
                                time = currentRange.from,
                                isEndTime = false,
                            )
                            TimeRangeSliderHandle.END -> fetchTimeSliderMinute(
                                scheduleDate = scheduleDate,
                                time = currentRange.to,
                                isEndTime = true,
                            )
                            null -> 0
                        }
                        activeHandle = selectedHandle
                        lastHandle = selectedHandle ?: lastHandle
                        lastHapticSegment = currentMinute / HAPTIC_MINUTE_STEP
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        updateTimeRange(offset.x)
                    },
                    onDragCancel = {
                        selectedHandle = null
                        activeHandle = null
                    },
                    onDragEnd = {
                        selectedHandle = null
                        activeHandle = null
                    },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        updateTimeRange(change.position.x)
                    },
                )
            }
    } else {
        Modifier
    }

    TimeRangeSliderLayout(
        modifier = modifier
            .then(gestureModifier)
            .alpha(contentAlpha)
            .drawWithCache {
                val trackStart = TIME_SLIDER_HANDLE_TOUCH_SIZE.toPx() / 2f
                val trackEnd = size.width - trackStart
                val trackWidth = trackEnd - trackStart
                val trackOffset = TIME_SLIDER_TRACK_OFFSET.toPx()
                val currentStartOffset = trackStart + trackWidth * startFraction
                val currentEndOffset = trackStart + trackWidth * endFraction
                val smallTickSize = TIME_SLIDER_SMALL_TICK_SIZE.toPx()
                val largeTickSize = TIME_SLIDER_LARGE_TICK_SIZE.toPx()

                onDrawBehind {
                    drawLine(
                        color = inactiveColor,
                        start = Offset(trackStart, trackOffset),
                        end = Offset(trackEnd, trackOffset),
                        strokeWidth = TIME_SLIDER_INACTIVE_TRACK_SIZE.toPx(),
                        cap = StrokeCap.Round,
                    )
                    repeat(SLIDER_HOURS_IN_DAY + 1) { hour ->
                        val tickOffset = trackStart + trackWidth * hour / SLIDER_HOURS_IN_DAY
                        val tickSize = when (hour % TIME_SLIDER_LARGE_TICK_HOUR_STEP) {
                            0 -> largeTickSize
                            else -> smallTickSize
                        }
                        drawLine(
                            color = inactiveColor,
                            start = Offset(tickOffset, trackOffset - tickSize),
                            end = Offset(tickOffset, trackOffset + tickSize),
                            strokeWidth = TIME_SLIDER_TICK_WIDTH.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                    when {
                        isFullDay -> drawLine(
                            color = activeColor,
                            start = Offset(trackStart, trackOffset),
                            end = Offset(trackEnd, trackOffset),
                            strokeWidth = TIME_SLIDER_ACTIVE_TRACK_SIZE.toPx(),
                            cap = StrokeCap.Round,
                        )
                        isOvernight -> {
                            drawLine(
                                color = activeColor,
                                start = Offset(trackStart, trackOffset),
                                end = Offset(currentEndOffset, trackOffset),
                                strokeWidth = TIME_SLIDER_ACTIVE_TRACK_SIZE.toPx(),
                                cap = StrokeCap.Round,
                            )
                            drawLine(
                                color = activeColor,
                                start = Offset(currentStartOffset, trackOffset),
                                end = Offset(trackEnd, trackOffset),
                                strokeWidth = TIME_SLIDER_ACTIVE_TRACK_SIZE.toPx(),
                                cap = StrokeCap.Round,
                            )
                        }
                        duration > 0L -> drawLine(
                            color = activeColor,
                            start = Offset(currentStartOffset, trackOffset),
                            end = Offset(currentEndOffset, trackOffset),
                            strokeWidth = TIME_SLIDER_ACTIVE_TRACK_SIZE.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                }
            },
        startFraction = startFraction,
        endFraction = endFraction,
        startTimeTitle = startTimeTitle,
        endTimeTitle = endTimeTitle,
        startHandleActive = activeHandle == TimeRangeSliderHandle.START,
        endHandleActive = activeHandle == TimeRangeSliderHandle.END,
        enabled = enabled,
        activeColor = activeColor,
        handleContentColor = handleContentColor,
        onStartProgressChange = { minute ->
            actualOnTimeRangeChange(
                actualTimeRange.updateTimeSliderRange(
                    scheduleDate = scheduleDate,
                    handle = TimeRangeSliderHandle.START,
                    minute = minute,
                ),
            )
        },
        onEndProgressChange = { minute ->
            actualOnTimeRangeChange(
                actualTimeRange.updateTimeSliderRange(
                    scheduleDate = scheduleDate,
                    handle = TimeRangeSliderHandle.END,
                    minute = minute,
                ),
            )
        },
    )
}

@Composable
private fun TimeRangeSliderLayout(
    modifier: Modifier,
    startFraction: Float,
    endFraction: Float,
    startTimeTitle: String,
    endTimeTitle: String,
    startHandleActive: Boolean,
    endHandleActive: Boolean,
    enabled: Boolean,
    activeColor: Color,
    handleContentColor: Color,
    onStartProgressChange: (Int) -> Unit,
    onEndProgressChange: (Int) -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            TimeRangeSliderLabel(text = startTimeTitle)
            TimeRangeSliderLabel(text = endTimeTitle)
            TimeRangeSliderThumb(
                description = EditorThemeRes.strings.timeFieldStartLabel,
                timeTitle = startTimeTitle,
                progress = startFraction * SLIDER_MINUTES_IN_DAY,
                maximum = SLIDER_MAXIMUM_START_MINUTE,
                active = startHandleActive,
                enabled = enabled,
                color = activeColor,
                contentColor = handleContentColor,
                onProgressChange = onStartProgressChange,
            )
            TimeRangeSliderThumb(
                description = EditorThemeRes.strings.timeFieldEndLabel,
                timeTitle = endTimeTitle,
                progress = endFraction * SLIDER_MINUTES_IN_DAY,
                maximum = SLIDER_MINUTES_IN_DAY,
                active = endHandleActive,
                enabled = enabled,
                color = activeColor,
                contentColor = handleContentColor,
                onProgressChange = onEndProgressChange,
            )
            TIME_SLIDER_LABEL_HOURS.forEach { hour ->
                Text(
                    text = hour.toString().padStart(2, '0'),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        },
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = TIME_SLIDER_HEIGHT.roundToPx().coerceIn(
            minimumValue = constraints.minHeight,
            maximumValue = constraints.maxHeight,
        )
        val trackStart = TIME_SLIDER_HANDLE_TOUCH_SIZE.roundToPx() / 2
        val trackWidth = width - trackStart * 2
        val startOffset = trackStart + (trackWidth * startFraction).roundToInt()
        val endOffset = trackStart + (trackWidth * endFraction).roundToInt()
        val labelConstraints = Constraints(maxWidth = width / 2)
        val startLabel = measurables[0].measure(labelConstraints)
        val endLabel = measurables[1].measure(labelConstraints)
        val startThumb = measurables[2].measure(Constraints.fixed(
            TIME_SLIDER_HANDLE_TOUCH_SIZE.roundToPx(),
            TIME_SLIDER_HANDLE_TOUCH_SIZE.roundToPx(),
        ))
        val endThumb = measurables[3].measure(Constraints.fixed(
            TIME_SLIDER_HANDLE_TOUCH_SIZE.roundToPx(),
            TIME_SLIDER_HANDLE_TOUCH_SIZE.roundToPx(),
        ))
        val hourLabels = TIME_SLIDER_LABEL_HOURS.indices.map { index ->
            measurables[index + TIME_SLIDER_FIXED_ITEMS].measure(Constraints())
        }
        val labelGap = TIME_SLIDER_LABEL_GAP.roundToPx()
        val labelOffsets = fetchTimeSliderLabelOffsets(
            width = width,
            startCenter = startOffset,
            endCenter = endOffset,
            startWidth = startLabel.width,
            endWidth = endLabel.width,
            gap = labelGap,
        )
        val thumbOffset = TIME_SLIDER_TRACK_OFFSET.roundToPx() - startThumb.height / 2
        val hourLabelOffset = TIME_SLIDER_HOUR_LABEL_OFFSET.roundToPx()

        layout(width, height) {
            startLabel.placeRelative(labelOffsets.first, 0)
            endLabel.placeRelative(labelOffsets.second, 0)
            startThumb.placeRelative(startOffset - startThumb.width / 2, thumbOffset)
            endThumb.placeRelative(endOffset - endThumb.width / 2, thumbOffset)
            hourLabels.forEachIndexed { index, label ->
                val fraction = TIME_SLIDER_LABEL_HOURS[index] / SLIDER_HOURS_IN_DAY.toFloat()
                val center = trackStart + (trackWidth * fraction).roundToInt()
                label.placeRelative(
                    x = (center - label.width / 2).coerceIn(0, width - label.width),
                    y = hourLabelOffset,
                )
            }
        }
    }
}

@Composable
private fun TimeRangeSliderLabel(
    text: String,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun TimeRangeSliderThumb(
    description: String,
    timeTitle: String,
    progress: Float,
    maximum: Int,
    active: Boolean,
    enabled: Boolean,
    color: Color,
    contentColor: Color,
    onProgressChange: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                contentDescription = description
                stateDescription = timeTitle
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = progress,
                    range = 0f..maximum.toFloat(),
                    steps = maximum / SLIDER_MINUTE_STEP - 1,
                )
                if (enabled) {
                    setProgress { value ->
                        val minute = snapTimeSliderMinute(
                            minute = value.roundToInt(),
                            maximum = maximum,
                        )
                        onProgressChange(minute)
                        true
                    }
                } else {
                    disabled()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(
                when (active) {
                    true -> TIME_SLIDER_ACTIVE_HANDLE_SIZE
                    false -> TIME_SLIDER_HANDLE_SIZE
                },
            ),
            shape = CircleShape,
            color = color,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(4.dp, 14.dp)) {
                    drawRoundRect(
                        color = contentColor,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width / 2f),
                    )
                }
            }
        }
    }
}

internal fun TimeRange.updateTimeSliderRange(
    scheduleDate: Date,
    handle: TimeRangeSliderHandle,
    minute: Int,
): TimeRange {
    val dayStart = scheduleDate.startThisDay()
    val nextDayStart = dayStart.shiftDay(1)
    return when (handle) {
        TimeRangeSliderHandle.START -> {
            val startMinute = minute.coerceIn(0, SLIDER_MAXIMUM_START_MINUTE)
            val endMinute = fetchTimeSliderMinute(scheduleDate, to, false)
            val endDay = when (endMinute > startMinute) {
                true -> dayStart
                false -> nextDayStart
            }
            TimeRange(
                from = dayStart.fetchTimeSliderDate(startMinute),
                to = endDay.fetchTimeSliderDate(endMinute),
            )
        }
        TimeRangeSliderHandle.END -> {
            val endMinute = minute.coerceIn(0, SLIDER_MINUTES_IN_DAY)
            val startMinute = fetchTimeSliderMinute(scheduleDate, from, false)
            val endTime = when {
                endMinute == SLIDER_MINUTES_IN_DAY -> nextDayStart
                endMinute > startMinute -> dayStart.fetchTimeSliderDate(endMinute)
                else -> nextDayStart.fetchTimeSliderDate(endMinute)
            }
            TimeRange(
                from = dayStart.fetchTimeSliderDate(startMinute),
                to = endTime,
            )
        }
    }
}

internal fun fetchTimeSliderMinute(
    scheduleDate: Date,
    time: Date,
    isEndTime: Boolean,
): Int {
    val calendar = Calendar.getInstance().apply { this.time = time }
    val minute = calendar.get(Calendar.HOUR_OF_DAY) * Constants.Date.MINUTES_IN_HOUR.toInt() +
        calendar.get(Calendar.MINUTE)
    val isNextDayStart = isEndTime && time.startThisDay() > scheduleDate.startThisDay() && minute == 0
    return when (isNextDayStart) {
        true -> SLIDER_MINUTES_IN_DAY
        false -> minute
    }
}

private fun fetchTimeSliderHandle(
    position: Float,
    trackStart: Float,
    trackWidth: Float,
    scheduleDate: Date,
    timeRange: TimeRange,
    lastHandle: TimeRangeSliderHandle,
): TimeRangeSliderHandle {
    val startMinute = fetchTimeSliderMinute(scheduleDate, timeRange.from, false)
    val endMinute = fetchTimeSliderMinute(scheduleDate, timeRange.to, true)
    val startOffset = trackStart + trackWidth * startMinute / SLIDER_MINUTES_IN_DAY
    val endOffset = trackStart + trackWidth * endMinute / SLIDER_MINUTES_IN_DAY
    val startDistance = abs(position - startOffset)
    val endDistance = abs(position - endOffset)
    return when {
        startDistance < endDistance -> TimeRangeSliderHandle.START
        endDistance < startDistance -> TimeRangeSliderHandle.END
        lastHandle == TimeRangeSliderHandle.START -> TimeRangeSliderHandle.END
        else -> TimeRangeSliderHandle.START
    }
}

internal fun fetchTimeSliderMinute(
    offset: Float,
    trackStart: Float,
    trackEnd: Float,
    maximum: Int,
): Int {
    val fraction = ((offset - trackStart) / (trackEnd - trackStart)).coerceIn(0f, 1f)
    return snapTimeSliderMinute(
        minute = (fraction * SLIDER_MINUTES_IN_DAY).roundToInt(),
        maximum = maximum,
    )
}

internal fun snapTimeSliderMinute(
    minute: Int,
    maximum: Int,
): Int {
    val steps = (minute + SLIDER_MINUTE_STEP - 1) / SLIDER_MINUTE_STEP
    return (steps * SLIDER_MINUTE_STEP).coerceIn(0, maximum)
}

internal fun fetchTimeSliderLabelOffsets(
    width: Int,
    startCenter: Int,
    endCenter: Int,
    startWidth: Int,
    endWidth: Int,
    gap: Int,
): Pair<Int, Int> {
    if (width <= 0) return 0 to 0

    val startOffset = (startCenter - startWidth / 2).coerceIn(
        minimumValue = 0,
        maximumValue = (width - startWidth).coerceAtLeast(0),
    )
    val endOffset = (endCenter - endWidth / 2).coerceIn(
        minimumValue = 0,
        maximumValue = (width - endWidth).coerceAtLeast(0),
    )
    val labelsOverlap = when (startOffset <= endOffset) {
        true -> startOffset + startWidth + gap > endOffset
        false -> endOffset + endWidth + gap > startOffset
    }
    if (!labelsOverlap) return startOffset to endOffset

    val resolvedGap = gap.coerceIn(
        minimumValue = 0,
        maximumValue = (width - startWidth - endWidth).coerceAtLeast(0),
    )
    val groupWidth = startWidth + resolvedGap + endWidth
    val groupCenter = (startCenter + endCenter) / 2
    val groupOffset = (groupCenter - groupWidth / 2).coerceIn(
        minimumValue = 0,
        maximumValue = (width - groupWidth).coerceAtLeast(0),
    )
    return when (startCenter <= endCenter) {
        true -> groupOffset to groupOffset + startWidth + resolvedGap
        false -> groupOffset + endWidth + resolvedGap to groupOffset
    }
}

private fun Date.fetchTimeSliderDate(minute: Int): Date {
    return Calendar.getInstance().apply {
        time = this@fetchTimeSliderDate.startThisDay()
        add(Calendar.MINUTE, minute)
    }.time
}

private const val SLIDER_HOURS_IN_DAY = 24
private const val SLIDER_MINUTES_IN_DAY = SLIDER_HOURS_IN_DAY * 60
private const val SLIDER_MINUTE_STEP = Constants.Date.SHIFT_MINUTE_VALUE
private const val SLIDER_MAXIMUM_START_MINUTE = SLIDER_MINUTES_IN_DAY - SLIDER_MINUTE_STEP
private const val HAPTIC_MINUTE_STEP = 15
private const val TIME_SLIDER_LARGE_TICK_HOUR_STEP = 6
private const val TIME_SLIDER_FIXED_ITEMS = 4
private const val DISABLED_CONTENT_ALPHA = 0.38f
private val TIME_SLIDER_LABEL_HOURS = listOf(0, 6, 12, 18, 24)
private val TIME_SLIDER_HEIGHT = 92.dp
private val TIME_SLIDER_TRACK_OFFSET = 48.dp
private val TIME_SLIDER_HOUR_LABEL_OFFSET = 70.dp
private val TIME_SLIDER_HANDLE_TOUCH_SIZE = 48.dp
private val TIME_SLIDER_HANDLE_SIZE = 28.dp
private val TIME_SLIDER_ACTIVE_HANDLE_SIZE = 32.dp
private val TIME_SLIDER_ACTIVE_TRACK_SIZE = 6.dp
private val TIME_SLIDER_INACTIVE_TRACK_SIZE = 4.dp
private val TIME_SLIDER_TICK_WIDTH = 1.dp
private val TIME_SLIDER_SMALL_TICK_SIZE = 3.dp
private val TIME_SLIDER_LARGE_TICK_SIZE = 6.dp
private val TIME_SLIDER_LABEL_GAP = 2.dp
