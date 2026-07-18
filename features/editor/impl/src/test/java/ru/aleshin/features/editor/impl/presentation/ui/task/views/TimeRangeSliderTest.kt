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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 18.07.2026.
 */
internal class TimeRangeSliderTest {

    private lateinit var scheduleDate: Date

    @Before
    fun setUp() {
        scheduleDate = date(dayOffset = 0, hour = 0, minute = 0)
    }

    @Test
    fun snapTimeRoundsUpToFiveMinuteStep() {
        assertEquals(770, snapTimeSliderMinute(minute = 769, maximum = 1440))
        assertEquals(770, snapTimeSliderMinute(minute = 770, maximum = 1440))
        assertEquals(775, snapTimeSliderMinute(minute = 771, maximum = 1440))
    }

    @Test
    fun snapTimeRespectsHandleBounds() {
        assertEquals(0, snapTimeSliderMinute(minute = -1, maximum = 1435))
        assertEquals(1435, snapTimeSliderMinute(minute = 1439, maximum = 1435))
        assertEquals(1440, snapTimeSliderMinute(minute = 1440, maximum = 1440))
    }

    @Test
    fun updateEndBeforeStartMovesEndToNextDay() {
        val timeRange = TimeRange(
            from = date(dayOffset = 0, hour = 22, minute = 0),
            to = date(dayOffset = 0, hour = 23, minute = 0),
        )

        val updatedRange = timeRange.updateTimeSliderRange(
            scheduleDate = scheduleDate,
            handle = TimeRangeSliderHandle.END,
            minute = 120,
        )

        assertEquals(date(dayOffset = 1, hour = 2, minute = 0), updatedRange.to)
    }

    @Test
    fun updateStartAfterEndKeepsPositiveOvernightRange() {
        val timeRange = TimeRange(
            from = date(dayOffset = 0, hour = 9, minute = 0),
            to = date(dayOffset = 0, hour = 10, minute = 0),
        )

        val updatedRange = timeRange.updateTimeSliderRange(
            scheduleDate = scheduleDate,
            handle = TimeRangeSliderHandle.START,
            minute = 660,
        )

        assertEquals(date(dayOffset = 0, hour = 11, minute = 0), updatedRange.from)
        assertEquals(date(dayOffset = 1, hour = 10, minute = 0), updatedRange.to)
        assertTrue(updatedRange.to.after(updatedRange.from))
    }

    @Test
    fun nextDayMidnightUsesEndOfSlider() {
        val nextDayMidnight = date(dayOffset = 1, hour = 0, minute = 0)

        val minute = fetchTimeSliderMinute(
            scheduleDate = scheduleDate,
            time = nextDayMidnight,
            isEndTime = true,
        )

        assertEquals(1440, minute)
    }

    @Test
    fun closeLabelsStayGroupedNearHandles() {
        val offsets = fetchTimeSliderLabelOffsets(
            width = 1000,
            startCenter = 430,
            endCenter = 490,
            startWidth = 90,
            endWidth = 90,
            gap = 11,
        )

        assertEquals(11, offsets.second - offsets.first - 90)
        assertTrue(offsets.first > 0)
        assertTrue(offsets.second + 90 < 1000)
    }

    @Test
    fun closeLabelsStayInsideLeftBound() {
        val offsets = fetchTimeSliderLabelOffsets(
            width = 300,
            startCenter = 0,
            endCenter = 20,
            startWidth = 80,
            endWidth = 90,
            gap = 6,
        )

        assertEquals(0, offsets.first)
        assertEquals(6, offsets.second - offsets.first - 80)
    }

    @Test
    fun closeLabelsStayInsideRightBound() {
        val offsets = fetchTimeSliderLabelOffsets(
            width = 300,
            startCenter = 280,
            endCenter = 300,
            startWidth = 80,
            endWidth = 90,
            gap = 6,
        )

        assertEquals(300, offsets.second + 90)
        assertEquals(6, offsets.second - offsets.first - 80)
    }

    @Test
    fun closeLabelsKeepSpatialHandleOrder() {
        val offsets = fetchTimeSliderLabelOffsets(
            width = 300,
            startCenter = 170,
            endCenter = 130,
            startWidth = 80,
            endWidth = 90,
            gap = 6,
        )

        assertTrue(offsets.second < offsets.first)
        assertEquals(6, offsets.first - offsets.second - 90)
    }

    private fun date(
        dayOffset: Int,
        hour: Int,
        minute: Int,
    ): Date {
        return Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, 18 + dayOffset, hour, minute)
        }.time
    }
}
