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
package ru.aleshin.features.home.impl.presentation.ui.home.timeline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.impl.presentation.models.TimelineTaskUpdateRequestUi
import ru.aleshin.features.home.impl.presentation.models.TimelineTimeTaskUi
import ru.aleshin.features.home.impl.presentation.ui.home.views.timeline.TimelineGestureState
import ru.aleshin.features.home.impl.presentation.ui.home.views.timeline.TimelineScale
import ru.aleshin.features.home.impl.presentation.ui.home.views.timeline.TimelineScaleSegment
import ru.aleshin.features.home.impl.presentation.ui.home.views.timeline.TimelineTaskDragMode
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
internal class TimelineGestureStateTest {

    @Test
    fun moveJumpsOverOccupiedTimeAndPreservesDuration() {
        val date = date()
        val timeTask = timeTask(date)
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(timeTask)
            startTaskEdit(timeTask, TimelineTaskDragMode.MOVE)
        }

        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(10, 30)) - scale.fetchOffset(date.at(9, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = listOf(TimeRange(date.at(11, 0), date.at(13, 0))),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        val updatedRange = state.finishTaskEdit()?.timeRange
        assertEquals(date.at(11, 0), updatedRange?.from)
        assertEquals(Constants.Date.MILLIS_IN_HOUR, updatedRange?.let { it.to.time - it.from.time })
    }

    @Test
    fun moveReachesDayBoundariesAndPreservesDuration() {
        val date = date()
        val timeTask = timeTask(
            date = date,
            minimumStartTime = date,
            maximumEndTime = date.shiftDay(),
        )
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(timeTask)
            startTaskEdit(timeTask, TimelineTaskDragMode.MOVE)
        }

        state.dragTask(
            dragAmount = scale.fetchOffset(date) - scale.fetchOffset(date.at(9, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        assertEquals(TimeRange(date, date.at(1, 0)), state.finishTaskEdit()?.timeRange)

        state.synchronize(
            timeTask(
                date = date,
                timeRange = TimeRange(date, date.at(1, 0)),
                minimumStartTime = date,
                maximumEndTime = date.shiftDay(),
            ),
        )
        state.startTaskEdit(timeTask, TimelineTaskDragMode.MOVE)
        state.dragTask(
            dragAmount = scale.fetchOffset(date.shiftDay()) - scale.fetchOffset(date),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        assertEquals(
            TimeRange(date.at(23, 0), date.shiftDay()),
            state.finishTaskEdit()?.timeRange,
        )
    }

    @Test
    fun resizeStopsAtNextTaskBoundary() {
        val date = date()
        val timeTask = timeTask(date)
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(timeTask)
            startTaskEdit(timeTask, TimelineTaskDragMode.RESIZE_END)
        }

        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(14, 0)) - scale.fetchOffset(date.at(10, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        assertEquals(date.at(11, 0), state.finishTaskEdit()?.timeRange?.to)
    }

    @Test
    fun repeatedResizeStartsFromPendingTimeRange() {
        val date = date()
        val timeTask = timeTask(date)
        val scale = linearScale(date)
        val state = TimelineGestureState().apply { startEditMode(timeTask) }

        state.startTaskEdit(timeTask, TimelineTaskDragMode.RESIZE_END)
        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(10, 30)) - scale.fetchOffset(date.at(10, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )
        val firstUpdate = checkNotNull(state.finishTaskEdit())
        state.synchronize(
            timeTask(
                date = date,
                timeRange = firstUpdate.timeRange,
            ),
        )

        state.startTaskEdit(timeTask, TimelineTaskDragMode.RESIZE_END)
        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(10, 45)) - scale.fetchOffset(date.at(10, 30)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        assertEquals(date.at(10, 45), state.finishTaskEdit()?.timeRange?.to)
    }

    @Test
    fun cancelDragRestoresPendingTimeRangeAndDragMode() {
        val date = date()
        val timeTask = timeTask(date)
        val scale = linearScale(date)
        val state = TimelineGestureState().apply { startEditMode(timeTask) }

        state.startTaskEdit(timeTask, TimelineTaskDragMode.RESIZE_END)
        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(10, 30)) - scale.fetchOffset(date.at(10, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )
        val update = checkNotNull(state.finishTaskEdit())
        state.synchronize(timeTask(date = date, timeRange = update.timeRange))

        state.startTaskEdit(timeTask, TimelineTaskDragMode.MOVE)
        state.cancelTaskDrag()

        assertEquals(date.at(10, 30), state.fetchTimeRange(timeTask).to)
        assertEquals(TimelineTaskDragMode.RESIZE_END, state.lastDragMode)
    }

    @Test
    fun moveAfterAcknowledgedResizeWithoutEditExitUsesLatestDuration() {
        val date = date()
        val originalTask = timeTask(
            date = date,
            timeRange = TimeRange(date.at(8, 30), date.at(15, 45)),
            minimumStartTime = date,
            maximumEndTime = date.shiftDay(),
        )
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(originalTask)
            startTaskEdit(originalTask, TimelineTaskDragMode.RESIZE_END)
        }

        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(8, 35)) -
                scale.fetchOffset(date.at(15, 45)),
            timeTask = originalTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )
        val resizeUpdate = checkNotNull(state.finishTaskEdit())
        val resizedTask = timeTask(
            date = date,
            timeRange = resizeUpdate.timeRange,
            minimumStartTime = date,
            maximumEndTime = date.shiftDay(),
        )

        state.synchronize(resizedTask)
        assertTrue(state.startTaskEdit(resizedTask, TimelineTaskDragMode.MOVE))
        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(9, 0)) -
                scale.fetchOffset(date.at(8, 30)),
            timeTask = resizedTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        val moveRange = state.finishTaskEdit()?.timeRange
        assertEquals(TimeRange(date.at(9, 0), date.at(9, 5)), moveRange)
    }

    @Test
    fun moveWaitsForFiveMinuteResizeAcknowledgementAfterReentry() {
        val date = date()
        val originalTask = timeTask(
            date = date,
            timeRange = TimeRange(date.at(8, 30), date.at(15, 45)),
            minimumStartTime = date,
            maximumEndTime = date.shiftDay(),
        )
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(originalTask)
            startTaskEdit(originalTask, TimelineTaskDragMode.RESIZE_END)
        }

        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(8, 35)) -
                scale.fetchOffset(date.at(15, 45)),
            timeTask = originalTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )
        val resizeUpdate = checkNotNull(state.finishTaskEdit())

        state.exitEditMode()
        assertEquals(resizeUpdate.timeRange, state.fetchTimeRange(originalTask))
        state.startEditMode(originalTask)

        assertEquals(resizeUpdate.timeRange, state.fetchTimeRange(originalTask))
        assertTrue(!state.startTaskEdit(originalTask, TimelineTaskDragMode.MOVE))

        val resizedTask = timeTask(
            date = date,
            timeRange = resizeUpdate.timeRange,
            minimumStartTime = date,
            maximumEndTime = date.shiftDay(),
        )
        state.synchronize(resizedTask)

        assertTrue(state.startTaskEdit(resizedTask, TimelineTaskDragMode.MOVE))
    }

    @Test
    fun restoredStorePendingUpdateKeepsPreviewAndBlocksStaleGesture() {
        val date = date()
        val originalTask = timeTask(
            date = date,
            timeRange = TimeRange(date.at(8, 30), date.at(15, 45)),
        )
        val pendingUpdate = TimelineTaskUpdateRequestUi(
            operationId = 10L,
            timeTaskId = originalTask.timeTask.key,
            timeRange = TimeRange(date.at(8, 30), date.at(8, 35)),
        )
        val restoredState = TimelineGestureState()

        restoredState.startEditMode(originalTask, pendingUpdate)

        assertEquals(
            pendingUpdate.timeRange,
            restoredState.fetchTimeRange(originalTask, pendingUpdate),
        )
        assertTrue(
            !restoredState.startTaskEdit(
                timeTask = originalTask,
                mode = TimelineTaskDragMode.MOVE,
                externalPendingUpdate = pendingUpdate,
            ),
        )
    }

    @Test
    fun sourceOvernightResizeKeepsFiveMinutesVisibleBeforeDayEnd() {
        val date = date()
        val dayEnd = date.shiftDay()
        val timeTask = timeTask(
            date = date,
            timeRange = TimeRange(date.at(23, 0), dayEnd.at(1, 0)),
            visibleTimeRange = TimeRange(date.at(23, 0), dayEnd),
            minimumStartTime = date.at(22, 0),
            maximumEndTime = dayEnd.at(1, 0),
            canMove = false,
            canResizeStart = true,
            canResizeEnd = false,
        )
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(timeTask)
            startTaskEdit(timeTask, TimelineTaskDragMode.RESIZE_START)
        }

        state.dragTask(
            dragAmount = scale.fetchOffset(dayEnd) - scale.fetchOffset(date.at(23, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        assertEquals(dayEnd.time - TIME_STEP, state.fetchTimeRange(timeTask).from.time)
    }

    @Test
    fun overlayOvernightResizeKeepsFiveMinutesVisibleAfterDayStart() {
        val date = date()
        val previousDay = Date(
            date.time - Constants.Date.HOURS_IN_DAY * Constants.Date.MILLIS_IN_HOUR,
        )
        val timeTask = timeTask(
            date = previousDay,
            timeRange = TimeRange(previousDay.at(23, 0), date.at(1, 0)),
            visibleTimeRange = TimeRange(date, date.at(1, 0)),
            minimumStartTime = previousDay.at(23, 0),
            maximumEndTime = date.at(2, 0),
            canMove = false,
            canResizeStart = false,
            canResizeEnd = true,
        )
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(timeTask)
            startTaskEdit(timeTask, TimelineTaskDragMode.RESIZE_END)
        }

        state.dragTask(
            dragAmount = scale.fetchOffset(date) - scale.fetchOffset(date.at(1, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        assertEquals(date.time + TIME_STEP, state.fetchTimeRange(timeTask).to.time)
    }

    @Test
    fun failedResizeRestoresRepositoryRangeAndUnlocksGestures() {
        val date = date()
        val timeTask = timeTask(date)
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(timeTask)
            startTaskEdit(timeTask, TimelineTaskDragMode.RESIZE_END)
        }

        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(10, 30)) -
                scale.fetchOffset(date.at(10, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )
        val failedUpdate = checkNotNull(state.finishTaskEdit())

        state.rejectTimeTaskUpdate(failedUpdate, timeTask)

        assertEquals(timeTask.timeTask.timeRanges, state.fetchTimeRange(timeTask))
        assertTrue(state.startTaskEdit(timeTask, TimelineTaskDragMode.MOVE))
    }

    @Test
    fun noOpDragRestoresPreviousMode() {
        val date = date()
        val timeTask = timeTask(date)
        val state = TimelineGestureState().apply {
            startEditMode(timeTask)
            startTaskEdit(timeTask, TimelineTaskDragMode.MOVE)
        }

        assertEquals(null, state.finishTaskEdit())
        assertEquals(null, state.lastDragMode)
    }

    @Test
    fun resizeEndMovesImmediatelyAfterMaximumOvershootIsReversed() {
        val date = date()
        val timeTask = timeTask(date)
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(timeTask)
            startTaskEdit(timeTask, TimelineTaskDragMode.RESIZE_END)
        }

        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(14, 0)) -
                scale.fetchOffset(date.at(10, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )
        assertEquals(date.at(11, 0), state.fetchTimeRange(timeTask).to)

        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(10, 55)) -
                scale.fetchOffset(date.at(11, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        assertEquals(date.at(10, 55), state.fetchTimeRange(timeTask).to)
    }

    @Test
    fun moveMovesImmediatelyAfterDayStartOvershootIsReversed() {
        val date = date()
        val timeTask = timeTask(date)
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(timeTask)
            startTaskEdit(timeTask, TimelineTaskDragMode.MOVE)
        }

        state.dragTask(
            dragAmount = -DAY_HEIGHT,
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )
        assertEquals(date.at(8, 0), state.fetchTimeRange(timeTask).from)

        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(8, 5)) -
                scale.fetchOffset(date.at(8, 0)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        assertEquals(date.at(8, 5), state.fetchTimeRange(timeTask).from)
    }

    @Test
    fun subStepDragAmountsAccumulateUntilFiveMinuteStep() {
        val date = date()
        val timeTask = timeTask(date)
        val scale = linearScale(date)
        val state = TimelineGestureState().apply {
            startEditMode(timeTask)
            startTaskEdit(timeTask, TimelineTaskDragMode.RESIZE_END)
        }

        repeat(5) {
            state.dragTask(
                dragAmount = 2f,
                timeTask = timeTask,
                scale = scale,
                freeTimeRanges = emptyList(),
                timeStep = TIME_STEP,
                minimumTaskDuration = TIME_STEP,
            )
        }

        assertEquals(date.at(10, 5), state.fetchTimeRange(timeTask).to)
    }

    @Test
    fun selectedTaskIsRequiredForDrag() {
        val date = date()
        val state = TimelineGestureState()

        assertTrue(!state.startTaskEdit(timeTask(date), TimelineTaskDragMode.MOVE))
    }

    private fun timeTask(
        date: Date,
        timeRange: TimeRange = TimeRange(date.at(9, 0), date.at(10, 0)),
        visibleTimeRange: TimeRange = timeRange,
        minimumStartTime: Date = date.at(8, 0),
        maximumEndTime: Date = date.at(11, 0),
        canMove: Boolean = true,
        canResizeStart: Boolean = true,
        canResizeEnd: Boolean = true,
    ): TimelineTimeTaskUi {
        val task = TimeTaskUi(
            key = 1L,
            date = date,
            timeRanges = timeRange,
            category = MainCategoryUi(id = 1L),
        )
        return TimelineTimeTaskUi(
            timeTask = task,
            executionStatus = TimeTaskStatus.PLANNED,
            visibleTimeRange = visibleTimeRange,
            minimumStartTime = minimumStartTime,
            maximumEndTime = maximumEndTime,
            canMove = canMove,
            canResizeStart = canResizeStart,
            canResizeEnd = canResizeEnd,
        )
    }

    private fun linearScale(date: Date): TimelineScale {
        val dayEnd = Date(
            date.time + Constants.Date.HOURS_IN_DAY * Constants.Date.MILLIS_IN_HOUR,
        )
        return TimelineScale(
            dayTimeRange = TimeRange(date, dayEnd),
            height = DAY_HEIGHT,
            segments = listOf(
                TimelineScaleSegment(
                    timeRange = TimeRange(date, dayEnd),
                    top = 0f,
                    bottom = DAY_HEIGHT,
                ),
            ),
        )
    }

    private fun date(): Date {
        return Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, 17)
        }.time
    }

    private fun Date.at(hour: Int, minute: Int): Date {
        return Calendar.getInstance().apply {
            time = this@at
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.time
    }

    private fun Date.shiftDay(): Date {
        return Date(time + Constants.Date.HOURS_IN_DAY * Constants.Date.MILLIS_IN_HOUR)
    }
}

private const val DAY_HEIGHT = 2400f
private const val TIME_STEP = 5L * Constants.Date.MILLIS_IN_MINUTE
