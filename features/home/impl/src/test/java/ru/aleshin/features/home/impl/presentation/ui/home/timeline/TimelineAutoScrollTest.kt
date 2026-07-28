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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.impl.presentation.ui.home.views.timeline.calculateTimelineAutoScrollStep
import ru.aleshin.features.home.impl.presentation.ui.home.views.timeline.fetchTimelineHourTimes
import ru.aleshin.features.home.impl.presentation.ui.home.views.timeline.isTimelineEditLayoutCompatible
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
internal class TimelineAutoScrollTest {

    @Test
    fun autoScrollUsesExpectedDirectionOnlyInsideEdges() {
        val topStep = calculateTimelineAutoScrollStep(
            viewportPosition = 0f,
            viewportHeight = VIEWPORT_HEIGHT,
            edgeSize = EDGE_SIZE,
            maximumSpeed = MAXIMUM_SPEED,
            elapsedSeconds = FRAME_SECONDS,
        )
        val centerStep = calculateTimelineAutoScrollStep(
            viewportPosition = VIEWPORT_HEIGHT / 2f,
            viewportHeight = VIEWPORT_HEIGHT,
            edgeSize = EDGE_SIZE,
            maximumSpeed = MAXIMUM_SPEED,
            elapsedSeconds = FRAME_SECONDS,
        )
        val bottomStep = calculateTimelineAutoScrollStep(
            viewportPosition = VIEWPORT_HEIGHT,
            viewportHeight = VIEWPORT_HEIGHT,
            edgeSize = EDGE_SIZE,
            maximumSpeed = MAXIMUM_SPEED,
            elapsedSeconds = FRAME_SECONDS,
        )

        assertTrue(topStep < 0f)
        assertEquals(0f, centerStep)
        assertTrue(bottomStep > 0f)
    }

    @Test
    fun autoScrollDistanceIsIndependentFromRefreshRate() {
        val distanceAt60Hz = List(60) {
            calculateTimelineAutoScrollStep(
                viewportPosition = VIEWPORT_HEIGHT,
                viewportHeight = VIEWPORT_HEIGHT,
                edgeSize = EDGE_SIZE,
                maximumSpeed = MAXIMUM_SPEED,
                elapsedSeconds = 1f / 60f,
            )
        }.sum()
        val distanceAt120Hz = List(120) {
            calculateTimelineAutoScrollStep(
                viewportPosition = VIEWPORT_HEIGHT,
                viewportHeight = VIEWPORT_HEIGHT,
                edgeSize = EDGE_SIZE,
                maximumSpeed = MAXIMUM_SPEED,
                elapsedSeconds = 1f / 120f,
            )
        }.sum()

        assertEquals(distanceAt60Hz, distanceAt120Hz, TOLERANCE)
        assertEquals(MAXIMUM_SPEED, distanceAt120Hz, TOLERANCE)
    }

    @Test
    fun autoScrollEdgesDoNotOverlapInsideSmallViewport() {
        val smallViewportHeight = EDGE_SIZE
        val upperStep = calculateTimelineAutoScrollStep(
            viewportPosition = smallViewportHeight / 4f,
            viewportHeight = smallViewportHeight,
            edgeSize = EDGE_SIZE,
            maximumSpeed = MAXIMUM_SPEED,
            elapsedSeconds = FRAME_SECONDS,
        )
        val lowerStep = calculateTimelineAutoScrollStep(
            viewportPosition = smallViewportHeight * 3f / 4f,
            viewportHeight = smallViewportHeight,
            edgeSize = EDGE_SIZE,
            maximumSpeed = MAXIMUM_SPEED,
            elapsedSeconds = FRAME_SECONDS,
        )

        assertTrue(upperStep < 0f)
        assertTrue(lowerStep > 0f)
    }

    @Test
    fun hourTimesUseExactDayEndAcrossDaylightSavingChanges() {
        val timeZone = TimeZone.getTimeZone("America/New_York")
        val springStart = date(timeZone, Calendar.MARCH, 8)
        val springEnd = springStart.shiftDay(timeZone)
        val fallStart = date(timeZone, Calendar.NOVEMBER, 1)
        val fallEnd = fallStart.shiftDay(timeZone)

        val springHours = fetchTimelineHourTimes(
            dayTimeRange = TimeRange(springStart, springEnd),
            timeZone = timeZone,
        )
        val fallHours = fetchTimelineHourTimes(
            dayTimeRange = TimeRange(fallStart, fallEnd),
            timeZone = timeZone,
        )

        assertEquals(24, springHours.size)
        assertEquals(26, fallHours.size)
        assertEquals(springEnd, springHours.last())
        assertEquals(fallEnd, fallHours.last())
        assertTrue(springHours.zipWithNext().all { (current, next) -> current < next })
        assertTrue(fallHours.zipWithNext().all { (current, next) -> current < next })
    }

    @Test
    fun selectedTaskRepositoryUpdateKeepsFrozenLayoutAfterDrag() {
        val date = date(TimeZone.getDefault(), Calendar.JULY, 17)
        val selectedRange = TimeRange(date.at(8), date.at(9))
        val otherRange = TimeRange(date.at(10), date.at(11))
        val updatedSelectedRange = TimeRange(date.at(12), date.at(13))

        val isCompatible = isTimelineEditLayoutCompatible(
            frozenTaskKey = listOf(1L to selectedRange, 2L to otherRange),
            currentTaskKey = listOf(2L to otherRange, 1L to updatedSelectedRange),
            selectedTimeTaskId = 1L,
            isDragging = false,
        )

        assertTrue(isCompatible)
    }

    @Test
    fun repositoryUpdateDoesNotReplaceFrozenLayoutDuringDrag() {
        val date = date(TimeZone.getDefault(), Calendar.JULY, 17)
        val originalRange = TimeRange(date.at(8), date.at(9))
        val updatedRange = TimeRange(date.at(12), date.at(13))

        val isCompatible = isTimelineEditLayoutCompatible(
            frozenTaskKey = listOf(1L to originalRange),
            currentTaskKey = listOf(1L to updatedRange),
            selectedTimeTaskId = 1L,
            isDragging = true,
        )

        assertFalse(isCompatible)
    }

    @Test
    fun unrelatedTaskUpdateInvalidatesFrozenLayout() {
        val date = date(TimeZone.getDefault(), Calendar.JULY, 17)
        val selectedRange = TimeRange(date.at(8), date.at(9))
        val otherRange = TimeRange(date.at(10), date.at(11))
        val updatedOtherRange = TimeRange(date.at(12), date.at(13))

        val isCompatible = isTimelineEditLayoutCompatible(
            frozenTaskKey = listOf(1L to selectedRange, 2L to otherRange),
            currentTaskKey = listOf(1L to selectedRange, 2L to updatedOtherRange),
            selectedTimeTaskId = 1L,
            isDragging = false,
        )

        assertFalse(isCompatible)
    }

    private fun date(
        timeZone: TimeZone,
        month: Int,
        day: Int,
    ): Date {
        return Calendar.getInstance(timeZone).apply {
            clear()
            set(2026, month, day)
        }.time
    }

    private fun Date.at(hour: Int): Date {
        return Calendar.getInstance().apply {
            time = this@at
            set(Calendar.HOUR_OF_DAY, hour)
        }.time
    }

    private fun Date.shiftDay(timeZone: TimeZone): Date {
        return Calendar.getInstance(timeZone).apply {
            time = this@shiftDay
            add(Calendar.DAY_OF_YEAR, 1)
        }.time
    }
}

private const val VIEWPORT_HEIGHT = 1_000f
private const val EDGE_SIZE = 100f
private const val MAXIMUM_SPEED = 600f
private const val FRAME_SECONDS = 1f / 60f
private const val TOLERANCE = 0.01f
