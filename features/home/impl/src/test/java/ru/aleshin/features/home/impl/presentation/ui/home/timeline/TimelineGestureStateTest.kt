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

        val updatedRange = state.finishTaskEdit()
        assertEquals(date.at(11, 0), updatedRange?.from)
        assertEquals(Constants.Date.MILLIS_IN_HOUR, updatedRange?.let { it.to.time - it.from.time })
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

        assertEquals(date.at(11, 0), state.finishTaskEdit()?.to)
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
        state.finishTaskEdit()

        state.startTaskEdit(timeTask, TimelineTaskDragMode.RESIZE_END)
        state.dragTask(
            dragAmount = scale.fetchOffset(date.at(10, 45)) - scale.fetchOffset(date.at(10, 30)),
            timeTask = timeTask,
            scale = scale,
            freeTimeRanges = emptyList(),
            timeStep = TIME_STEP,
            minimumTaskDuration = TIME_STEP,
        )

        assertEquals(date.at(10, 45), state.finishTaskEdit()?.to)
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
        state.finishTaskEdit()

        state.startTaskEdit(timeTask, TimelineTaskDragMode.MOVE)
        state.cancelTaskDrag()

        assertEquals(date.at(10, 30), state.fetchTimeRange(timeTask).to)
        assertEquals(TimelineTaskDragMode.RESIZE_END, state.lastDragMode)
    }

    @Test
    fun selectedTaskIsRequiredForDrag() {
        val date = date()
        val state = TimelineGestureState()

        assertTrue(!state.startTaskEdit(timeTask(date), TimelineTaskDragMode.MOVE))
    }

    private fun timeTask(date: Date): TimelineTimeTaskUi {
        val task = TimeTaskUi(
            key = 1L,
            date = date,
            timeRanges = TimeRange(date.at(9, 0), date.at(10, 0)),
            category = MainCategoryUi(id = 1L),
        )
        return TimelineTimeTaskUi(
            timeTask = task,
            executionStatus = TimeTaskStatus.PLANNED,
            visibleTimeRange = task.timeRanges,
            minimumStartTime = date.at(8, 0),
            maximumEndTime = date.at(11, 0),
            canMove = true,
            canResizeStart = true,
            canResizeEnd = true,
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
}

private const val DAY_HEIGHT = 2400f
private const val TIME_STEP = 5L * Constants.Date.MILLIS_IN_MINUTE
