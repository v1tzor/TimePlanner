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
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.impl.presentation.models.TimelineTaskUpdateRequestUi
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
    private var pendingUpdate by mutableStateOf<TimelineTaskUpdateRequestUi?>(null)
    private var dragOffset = 0f
    private var nextEditSessionId = 0L

    fun startEditMode(
        timeTask: TimelineTimeTaskUi,
        externalPendingUpdate: TimelineTaskUpdateRequestUi? = null,
    ) {
        if (selectedTimeTaskId != timeTask.timeTask.key) {
            selectedTimeTaskId = timeTask.timeTask.key
            selectedTimeRange = (externalPendingUpdate ?: pendingUpdate)
                ?.takeIf { request -> request.timeTaskId == timeTask.timeTask.key }
                ?.timeRange
                ?: timeTask.timeTask.timeRanges
            lastDragMode = null
        }
    }

    fun synchronize(
        timeTask: TimelineTimeTaskUi,
        externalPendingUpdate: TimelineTaskUpdateRequestUi? = null,
    ) {
        val actualTimeRange = timeTask.timeTask.timeRanges
        val currentPendingUpdate = pendingUpdate
        if (
            currentPendingUpdate?.timeTaskId == timeTask.timeTask.key &&
            currentPendingUpdate.timeRange == actualTimeRange
        ) {
            pendingUpdate = null
        }
        if (selectedTimeTaskId != timeTask.timeTask.key || taskEdit != null) return

        selectedTimeRange = (externalPendingUpdate ?: pendingUpdate)
            ?.takeIf { request -> request.timeTaskId == timeTask.timeTask.key }
            ?.timeRange
            ?: actualTimeRange
    }

    fun startTaskEdit(
        timeTask: TimelineTimeTaskUi,
        mode: TimelineTaskDragMode,
        externalPendingUpdate: TimelineTaskUpdateRequestUi? = null,
    ): Boolean {
        val canStart = when (mode) {
            TimelineTaskDragMode.MOVE -> timeTask.canMove
            TimelineTaskDragMode.RESIZE_START -> timeTask.canResizeStart
            TimelineTaskDragMode.RESIZE_END -> timeTask.canResizeEnd
        }
        if (
            !canStart ||
            externalPendingUpdate != null ||
            pendingUpdate != null ||
            selectedTimeTaskId != timeTask.timeTask.key
        ) {
            return false
        }

        val timeRange = fetchTimeRange(timeTask)
        nextEditSessionId += 1L
        dragOffset = 0f
        taskEdit = TimelineTaskEdit(
            sessionId = nextEditSessionId,
            timeTaskId = timeTask.timeTask.key,
            mode = mode,
            previousDragMode = lastDragMode,
            originalTimeRange = timeRange,
            currentTimeRange = timeRange,
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
        val rawOffset = dragOffset + dragAmount
        var updatedOffset = rawOffset
        val updatedRange = when (edit.mode) {
            TimelineTaskDragMode.MOVE -> {
                val moveStartRanges = fetchMoveStartRanges(
                    edit = edit,
                    timeTask = timeTask,
                    freeTimeRanges = freeTimeRanges,
                )
                val minimumStartTime = moveStartRanges.minOfOrNull { timeRange ->
                    timeRange.from
                } ?: edit.originalTimeRange.from
                val maximumStartTime = moveStartRanges.maxOfOrNull { timeRange ->
                    timeRange.to
                } ?: edit.originalTimeRange.from
                updatedOffset = rawOffset.coerceToTimeRange(
                    originalTime = edit.originalTimeRange.from,
                    minimumTime = minimumStartTime,
                    maximumTime = maximumStartTime,
                    scale = scale,
                )
                moveTimeTask(
                    edit = edit,
                    dragOffset = updatedOffset,
                    scale = scale,
                    moveStartRanges = moveStartRanges,
                    timeStep = timeStep,
                )
            }
            TimelineTaskDragMode.RESIZE_START -> {
                val maximumStartTime = Date(
                    minOf(
                        edit.originalTimeRange.to.time,
                        timeTask.visibleTimeRange.to.time,
                    ) - minimumTaskDuration,
                )
                updatedOffset = rawOffset.coerceToTimeRange(
                    originalTime = edit.originalTimeRange.from,
                    minimumTime = timeTask.minimumStartTime,
                    maximumTime = maximumStartTime,
                    scale = scale,
                )
                resizeTimeTaskStart(
                    edit = edit,
                    dragOffset = updatedOffset,
                    timeTask = timeTask,
                    scale = scale,
                    timeStep = timeStep,
                    minimumTaskDuration = minimumTaskDuration,
                )
            }
            TimelineTaskDragMode.RESIZE_END -> {
                val minimumEndTime = Date(
                    maxOf(
                        edit.originalTimeRange.from.time,
                        timeTask.visibleTimeRange.from.time,
                    ) + minimumTaskDuration,
                )
                updatedOffset = rawOffset.coerceToTimeRange(
                    originalTime = edit.originalTimeRange.to,
                    minimumTime = minimumEndTime,
                    maximumTime = timeTask.maximumEndTime,
                    scale = scale,
                )
                resizeTimeTaskEnd(
                    edit = edit,
                    dragOffset = updatedOffset,
                    timeTask = timeTask,
                    scale = scale,
                    timeStep = timeStep,
                    minimumTaskDuration = minimumTaskDuration,
                )
            }
        }
        val isTimeChanged = updatedRange != edit.currentTimeRange
        dragOffset = updatedOffset
        if (isTimeChanged) {
            taskEdit = edit.copy(currentTimeRange = updatedRange)
        }
        return isTimeChanged
    }

    fun finishTaskEdit(): TimelineTaskUpdateRequestUi? {
        val edit = taskEdit ?: return null
        taskEdit = null
        dragOffset = 0f
        if (edit.currentTimeRange == edit.originalTimeRange) {
            selectedTimeRange = edit.originalTimeRange
            lastDragMode = edit.previousDragMode
            return null
        }
        selectedTimeRange = edit.currentTimeRange
        return TimelineTaskUpdateRequestUi(
            operationId = generateUniqueKey(),
            timeTaskId = edit.timeTaskId,
            timeRange = edit.currentTimeRange,
        ).also { request ->
            pendingUpdate = request
        }
    }

    fun cancelTaskDrag() {
        taskEdit?.let { edit ->
            selectedTimeRange = edit.originalTimeRange
            lastDragMode = edit.previousDragMode
        }
        taskEdit = null
        dragOffset = 0f
    }

    fun exitEditMode() {
        selectedTimeTaskId = null
        selectedTimeRange = null
        lastDragMode = null
        taskEdit = null
        dragOffset = 0f
    }

    fun rejectTimeTaskUpdate(
        request: TimelineTaskUpdateRequestUi,
        timeTask: TimelineTimeTaskUi?,
    ) {
        val currentPendingUpdate = pendingUpdate
        if (
            currentPendingUpdate != null &&
            currentPendingUpdate.operationId != request.operationId
        ) {
            return
        }
        if (
            currentPendingUpdate == null &&
            selectedTimeTaskId == request.timeTaskId &&
            selectedTimeRange != request.timeRange
        ) {
            return
        }

        pendingUpdate = null
        if (selectedTimeTaskId == request.timeTaskId && taskEdit == null) {
            selectedTimeRange = timeTask?.timeTask?.timeRanges
            lastDragMode = null
        }
    }

    fun isTimeTaskUpdatePending(
        externalPendingUpdate: TimelineTaskUpdateRequestUi? = null,
    ): Boolean {
        return externalPendingUpdate != null || pendingUpdate != null
    }

    fun fetchTimeRange(
        timeTask: TimelineTimeTaskUi,
        externalPendingUpdate: TimelineTaskUpdateRequestUi? = null,
    ): TimeRange {
        return taskEdit
            ?.takeIf { edit -> edit.timeTaskId == timeTask.timeTask.key }
            ?.currentTimeRange
            ?: (externalPendingUpdate ?: pendingUpdate)
                ?.takeIf { request -> request.timeTaskId == timeTask.timeTask.key }
                ?.timeRange
            ?: selectedTimeRange?.takeIf {
                selectedTimeTaskId == timeTask.timeTask.key
            }
            ?: timeTask.timeTask.timeRanges
    }

    private fun moveTimeTask(
        edit: TimelineTaskEdit,
        dragOffset: Float,
        scale: TimelineScale,
        moveStartRanges: List<TimeRange>,
        timeStep: Long,
    ): TimeRange {
        val startOffset = scale.fetchOffset(edit.originalTimeRange.from) + dragOffset
        val desiredStart = scale.fetchTime(startOffset).snap(scale.dayTimeRange.from, timeStep)
        val duration = edit.originalTimeRange.to.time - edit.originalTimeRange.from.time
        val startTime = moveStartRanges.fetchNearestTime(desiredStart)
            ?: edit.originalTimeRange.from

        return TimeRange(
            from = startTime,
            to = Date(startTime.time + duration),
        )
    }

    private fun fetchMoveStartRanges(
        edit: TimelineTaskEdit,
        timeTask: TimelineTimeTaskUi,
        freeTimeRanges: List<TimeRange>,
    ): List<TimeRange> {
        val duration = edit.originalTimeRange.to.time - edit.originalTimeRange.from.time
        val currentMoveRange = TimeRange(
            from = timeTask.minimumStartTime,
            to = Date(timeTask.maximumEndTime.time - duration),
        )
        return freeTimeRanges.mapNotNull { freeTimeRange ->
            val latestStartTime = freeTimeRange.to.time - duration
            if (latestStartTime < freeTimeRange.from.time) return@mapNotNull null
            TimeRange(freeTimeRange.from, Date(latestStartTime))
        } + currentMoveRange
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

    private fun Float.coerceToTimeRange(
        originalTime: Date,
        minimumTime: Date,
        maximumTime: Date,
        scale: TimelineScale,
    ): Float {
        val originalOffset = scale.fetchOffset(originalTime)
        val minimumOffset = scale.fetchOffset(minimumTime) - originalOffset
        val maximumOffset = scale.fetchOffset(maximumTime) - originalOffset
        return coerceIn(minimumOffset, maximumOffset)
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
