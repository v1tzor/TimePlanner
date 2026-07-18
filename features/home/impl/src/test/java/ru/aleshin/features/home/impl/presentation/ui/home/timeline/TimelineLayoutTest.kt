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
import ru.aleshin.features.home.impl.presentation.ui.home.views.timeline.TimelineLayout
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
internal class TimelineLayoutTest {

    @Test
    fun calculateSeparatesConsecutiveShortTasks() {
        val date = date()
        val timeTasks = listOf(
            timeTask(1L, date, 9, 0, 9, 5),
            timeTask(2L, date, 9, 5, 9, 15),
            timeTask(3L, date, 9, 15, 9, 30),
        )

        val result = TimelineLayout.calculate(
            dayTimeRange = TimeRange(date, date.shiftDay()),
            timeTasks = timeTasks,
            hourHeight = HOUR_HEIGHT,
            minimumTaskHeight = MINIMUM_TASK_HEIGHT,
            maximumTaskHeight = MAXIMUM_TASK_HEIGHT,
            longTaskThreshold = LONG_TASK_THRESHOLD,
            longTaskScale = LONG_TASK_SCALE,
            minimumFreeTimeHeight = MINIMUM_FREE_TIME_HEIGHT,
            freeTimeScale = FREE_TIME_SCALE,
            taskSpace = TASK_SPACE,
            verticalPadding = VERTICAL_PADDING,
        )

        assertEquals(3, result.taskPositions.size)
        assertTrue(result.taskPositions.all { position ->
            position.height >= MINIMUM_TASK_HEIGHT - TASK_SPACE
        })
        result.taskPositions.zipWithNext().forEach { (current, next) ->
            assertTrue(next.top - current.top - current.height >= TASK_SPACE - TOLERANCE)
        }
    }

    @Test
    fun calculateLimitsLongTaskHeight() {
        val date = date()
        val result = TimelineLayout.calculate(
            dayTimeRange = TimeRange(date, date.shiftDay()),
            timeTasks = listOf(timeTask(1L, date, 0, 0, 23, 59)),
            hourHeight = HOUR_HEIGHT,
            minimumTaskHeight = MINIMUM_TASK_HEIGHT,
            maximumTaskHeight = MAXIMUM_TASK_HEIGHT,
            longTaskThreshold = LONG_TASK_THRESHOLD,
            longTaskScale = LONG_TASK_SCALE,
            minimumFreeTimeHeight = MINIMUM_FREE_TIME_HEIGHT,
            freeTimeScale = FREE_TIME_SCALE,
            taskSpace = TASK_SPACE,
            verticalPadding = VERTICAL_PADDING,
        )

        assertEquals(
            MAXIMUM_TASK_HEIGHT - TASK_SPACE,
            result.taskPositions.single().height,
            TOLERANCE,
        )
    }

    @Test
    fun calculateCompressesOnlyLongPartOfTask() {
        val date = date()
        val result = TimelineLayout.calculate(
            dayTimeRange = TimeRange(date, date.shiftDay()),
            timeTasks = listOf(timeTask(1L, date, 0, 0, 8, 0)),
            hourHeight = HOUR_HEIGHT,
            minimumTaskHeight = MINIMUM_TASK_HEIGHT,
            maximumTaskHeight = MAXIMUM_TASK_HEIGHT,
            longTaskThreshold = LONG_TASK_THRESHOLD,
            longTaskScale = LONG_TASK_SCALE,
            minimumFreeTimeHeight = MINIMUM_FREE_TIME_HEIGHT,
            freeTimeScale = FREE_TIME_SCALE,
            taskSpace = TASK_SPACE,
            verticalPadding = VERTICAL_PADDING,
        )
        val expectedHeight = 2f * HOUR_HEIGHT + 6f * HOUR_HEIGHT * LONG_TASK_SCALE

        assertEquals(
            expectedHeight - TASK_SPACE,
            result.taskPositions.single().height,
            TOLERANCE,
        )
    }

    @Test
    fun scaleConvertsOffsetToSameTime() {
        val date = date()
        val targetTime = date.at(14, 35)
        val result = TimelineLayout.calculate(
            dayTimeRange = TimeRange(date, date.shiftDay()),
            timeTasks = listOf(
                timeTask(1L, date, 9, 0, 9, 5),
                timeTask(2L, date, 12, 0, 18, 0),
            ),
            hourHeight = HOUR_HEIGHT,
            minimumTaskHeight = MINIMUM_TASK_HEIGHT,
            maximumTaskHeight = MAXIMUM_TASK_HEIGHT,
            longTaskThreshold = LONG_TASK_THRESHOLD,
            longTaskScale = LONG_TASK_SCALE,
            minimumFreeTimeHeight = MINIMUM_FREE_TIME_HEIGHT,
            freeTimeScale = FREE_TIME_SCALE,
            taskSpace = TASK_SPACE,
            verticalPadding = VERTICAL_PADDING,
        )

        val restoredTime = result.scale.fetchTime(result.scale.fetchOffset(targetTime))

        assertTrue(kotlin.math.abs(restoredTime.time - targetTime.time) <= 1000L)
    }

    @Test
    fun visibleHourTimesDoNotOverlapInsideCompressedLongTask() {
        val date = date()
        val endTime = date.at(22, 59)
        val result = TimelineLayout.calculate(
            dayTimeRange = TimeRange(date, date.shiftDay()),
            timeTasks = listOf(timeTask(1L, date, 0, 0, 22, 59)),
            hourHeight = HOUR_HEIGHT,
            minimumTaskHeight = MINIMUM_TASK_HEIGHT,
            maximumTaskHeight = MAXIMUM_TASK_HEIGHT,
            longTaskThreshold = LONG_TASK_THRESHOLD,
            longTaskScale = LONG_TASK_SCALE,
            minimumFreeTimeHeight = MINIMUM_FREE_TIME_HEIGHT,
            freeTimeScale = FREE_TIME_SCALE,
            taskSpace = TASK_SPACE,
            verticalPadding = VERTICAL_PADDING,
        )
        val hourTimes = List(25) { hour -> Date(date.time + hour * MILLIS_IN_HOUR) }

        val visibleHourTimes = TimelineLayout.fetchVisibleHourTimes(
            hourTimes = hourTimes,
            boundaryOffsets = listOf(
                result.scale.fetchOffset(date),
                result.scale.fetchOffset(endTime),
            ),
            scale = result.scale,
            minimumDistance = LABEL_MINIMUM_DISTANCE,
        )

        assertTrue(visibleHourTimes.size < hourTimes.size)
        visibleHourTimes.zipWithNext().forEach { (current, next) ->
            val distance = result.scale.fetchOffset(next) - result.scale.fetchOffset(current)
            assertTrue(distance >= LABEL_MINIMUM_DISTANCE)
        }
    }

    private fun timeTask(
        key: Long,
        date: Date,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
    ): TimelineTimeTaskUi {
        val startTime = date.at(startHour, startMinute)
        val endTime = date.at(endHour, endMinute)
        val task = TimeTaskUi(
            key = key,
            date = date,
            timeRanges = TimeRange(startTime, endTime),
            category = MainCategoryUi(id = key),
        )
        return TimelineTimeTaskUi(
            timeTask = task,
            executionStatus = TimeTaskStatus.PLANNED,
            visibleTimeRange = task.timeRanges,
            minimumStartTime = date,
            maximumEndTime = date.shiftDay(),
            canMove = true,
            canResizeStart = true,
            canResizeEnd = true,
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

private const val HOUR_HEIGHT = 80f
private const val MINIMUM_TASK_HEIGHT = 58f
private const val MAXIMUM_TASK_HEIGHT = 720f
private const val LONG_TASK_SCALE = 0.5f
private const val LONG_TASK_THRESHOLD = 2L * Constants.Date.MILLIS_IN_HOUR
private const val MINIMUM_FREE_TIME_HEIGHT = 10f
private const val FREE_TIME_SCALE = 0.64f
private const val TASK_SPACE = 2f
private const val VERTICAL_PADDING = 20f
private const val TOLERANCE = 0.01f
private const val LABEL_MINIMUM_DISTANCE = 32f
private const val MILLIS_IN_HOUR = Constants.Date.MILLIS_IN_HOUR
