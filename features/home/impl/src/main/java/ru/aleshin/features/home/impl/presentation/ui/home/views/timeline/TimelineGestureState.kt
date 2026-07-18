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

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.impl.presentation.models.TimelineTimeTaskUi
import java.util.Date
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
@Stable
internal class TimelineGestureState {

    var selectedTimeTaskId by mutableStateOf<Long?>(null)
        private set

    var taskEdit by mutableStateOf<TimelineTaskEdit?>(null)
        private set

    var lastDragMode by mutableStateOf<TimelineTaskDragMode?>(null)
        private set

    private var selectedTimeRange by mutableStateOf<TimeRange?>(null)
    private var hasPendingUpdate by mutableStateOf(false)

    fun startEditMode(timeTask: TimelineTimeTaskUi) {
        if (selectedTimeTaskId != timeTask.timeTask.key) {
            selectedTimeTaskId = timeTask.timeTask.key
            selectedTimeRange = timeTask.timeTask.timeRanges
            hasPendingUpdate = false
            lastDragMode = null
        }
    }

    fun synchronize(timeTask: TimelineTimeTaskUi) {
        if (selectedTimeTaskId != timeTask.timeTask.key || taskEdit != null) return

        val actualTimeRange = timeTask.timeTask.timeRanges
        if (!hasPendingUpdate || selectedTimeRange == actualTimeRange) {
            selectedTimeRange = actualTimeRange
            hasPendingUpdate = false
        }
    }

    fun startTaskEdit(
        timeTask: TimelineTimeTaskUi,
        mode: TimelineTaskDragMode,
    ): Boolean {
        val canStart = when (mode) {
            TimelineTaskDragMode.MOVE -> timeTask.canMove
            TimelineTaskDragMode.RESIZE_START -> timeTask.canResizeStart
            TimelineTaskDragMode.RESIZE_END -> timeTask.canResizeEnd
        }
        if (!canStart || selectedTimeTaskId != timeTask.timeTask.key) return false

        val timeRange = fetchTimeRange(timeTask)
        taskEdit = TimelineTaskEdit(
            timeTaskId = timeTask.timeTask.key,
            mode = mode,
            previousDragMode = lastDragMode,
            originalTimeRange = timeRange,
            currentTimeRange = timeRange,
            dragOffset = 0f,
        )
        lastDragMode = mode
        return true
    }

    fun dragTask(
        dragAmount: Float,
        timeTask: TimelineTimeTaskUi,
        scale: TimelineScale,
        freeTimeRanges: List<TimeRange>,
        timeStep: Long,
        minimumTaskDuration: Long,
    ): Boolean {
        val edit = taskEdit?.takeIf { taskEdit ->
            taskEdit.timeTaskId == timeTask.timeTask.key
        } ?: return false
        val updatedOffset = edit.dragOffset + dragAmount
        val updatedRange = when (edit.mode) {
            TimelineTaskDragMode.MOVE -> moveTimeTask(
                edit = edit,
                dragOffset = updatedOffset,
                timeTask = timeTask,
                scale = scale,
                freeTimeRanges = freeTimeRanges,
                timeStep = timeStep,
            )
            TimelineTaskDragMode.RESIZE_START -> resizeTimeTaskStart(
                edit = edit,
                dragOffset = updatedOffset,
                timeTask = timeTask,
                scale = scale,
                timeStep = timeStep,
                minimumTaskDuration = minimumTaskDuration,
            )
            TimelineTaskDragMode.RESIZE_END -> resizeTimeTaskEnd(
                edit = edit,
                dragOffset = updatedOffset,
                timeTask = timeTask,
                scale = scale,
                timeStep = timeStep,
                minimumTaskDuration = minimumTaskDuration,
            )
        }
        val isTimeChanged = updatedRange != edit.currentTimeRange
        taskEdit = edit.copy(
            currentTimeRange = updatedRange,
            dragOffset = updatedOffset,
        )
        return isTimeChanged
    }

    fun finishTaskEdit(): TimeRange? {
        val edit = taskEdit ?: return null
        taskEdit = null
        selectedTimeRange = edit.currentTimeRange
        return edit.currentTimeRange.takeIf { timeRange ->
            timeRange != edit.originalTimeRange
        }?.also { hasPendingUpdate = true }
    }

    fun cancelTaskDrag() {
        taskEdit?.let { edit ->
            selectedTimeRange = edit.originalTimeRange
            lastDragMode = edit.previousDragMode
        }
        taskEdit = null
    }

    fun exitEditMode() {
        selectedTimeTaskId = null
        selectedTimeRange = null
        hasPendingUpdate = false
        lastDragMode = null
        taskEdit = null
    }

    fun fetchTimeRange(timeTask: TimelineTimeTaskUi): TimeRange {
        return taskEdit
            ?.takeIf { edit -> edit.timeTaskId == timeTask.timeTask.key }
            ?.currentTimeRange
            ?: selectedTimeRange?.takeIf {
                selectedTimeTaskId == timeTask.timeTask.key
            }
            ?: timeTask.timeTask.timeRanges
    }

    private fun moveTimeTask(
        edit: TimelineTaskEdit,
        dragOffset: Float,
        timeTask: TimelineTimeTaskUi,
        scale: TimelineScale,
        freeTimeRanges: List<TimeRange>,
        timeStep: Long,
    ): TimeRange {
        val startOffset = scale.fetchOffset(edit.originalTimeRange.from) + dragOffset
        val desiredStart = scale.fetchTime(startOffset).snap(scale.dayTimeRange.from, timeStep)
        val duration = edit.originalTimeRange.to.time - edit.originalTimeRange.from.time
        val currentMoveRange = TimeRange(
            from = timeTask.minimumStartTime,
            to = Date(timeTask.maximumEndTime.time - duration),
        )
        val moveStartRanges = freeTimeRanges.mapNotNull { freeTimeRange ->
            val latestStartTime = freeTimeRange.to.time - duration
            if (latestStartTime < freeTimeRange.from.time) return@mapNotNull null
            TimeRange(freeTimeRange.from, Date(latestStartTime))
        } + currentMoveRange
        val startTime = moveStartRanges.fetchNearestTime(desiredStart)
            ?: edit.originalTimeRange.from

        return TimeRange(
            from = startTime,
            to = Date(startTime.time + duration),
        )
    }

    private fun resizeTimeTaskStart(
        edit: TimelineTaskEdit,
        dragOffset: Float,
        timeTask: TimelineTimeTaskUi,
        scale: TimelineScale,
        timeStep: Long,
        minimumTaskDuration: Long,
    ): TimeRange {
        val startOffset = scale.fetchOffset(edit.originalTimeRange.from) + dragOffset
        val maximumStartTime = edit.originalTimeRange.to.time - minimumTaskDuration
        val startTime = scale.fetchTime(startOffset)
            .snap(scale.dayTimeRange.from, timeStep)
            .time
            .coerceIn(timeTask.minimumStartTime.time, maximumStartTime)

        return edit.originalTimeRange.copy(from = Date(startTime))
    }

    private fun resizeTimeTaskEnd(
        edit: TimelineTaskEdit,
        dragOffset: Float,
        timeTask: TimelineTimeTaskUi,
        scale: TimelineScale,
        timeStep: Long,
        minimumTaskDuration: Long,
    ): TimeRange {
        val endOffset = scale.fetchOffset(edit.originalTimeRange.to) + dragOffset
        val minimumEndTime = edit.originalTimeRange.from.time + minimumTaskDuration
        val endTime = scale.fetchTime(endOffset)
            .snap(scale.dayTimeRange.from, timeStep)
            .time
            .coerceIn(minimumEndTime, timeTask.maximumEndTime.time)

        return edit.originalTimeRange.copy(to = Date(endTime))
    }

    private fun Date.snap(
        startTime: Date,
        timeStep: Long,
    ): Date {
        val steps = ((time - startTime.time) / timeStep.toDouble()).roundToLong()
        return Date(startTime.time + steps * timeStep)
    }

    private fun List<TimeRange>.fetchNearestTime(time: Date): Date? {
        return asSequence()
            .flatMap { timeRange ->
                sequenceOf(
                    Date(time.time.coerceIn(timeRange.from.time, timeRange.to.time)),
                    timeRange.from,
                    timeRange.to,
                )
            }
            .minByOrNull { possibleTime -> abs(possibleTime.time - time.time) }
    }
}
