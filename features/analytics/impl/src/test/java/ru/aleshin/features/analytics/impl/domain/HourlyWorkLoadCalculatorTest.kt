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
package ru.aleshin.features.analytics.impl.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.analytics.impl.domain.common.HourlyWorkLoadCalculator
import ru.aleshin.features.analytics.impl.domain.entities.HourlyWorkLoadAnalytic
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 03.07.2026.
 */
internal class HourlyWorkLoadCalculatorTest {

    private lateinit var calculator: HourlyWorkLoadCalculator

    @Before
    fun setUp() {
        calculator = HourlyWorkLoadCalculator.Base()
    }

    @Test
    fun test_calculate_returns_empty_bucket_durations_for_empty_list() {
        val actual = calculator.calculate(
            timeTasks = emptyList(),
            globalTimeRange = TimeRange(date(2026, Calendar.JULY, 1, 0, 0), date(2026, Calendar.JULY, 2, 0, 0)),
        )

        assertEquals(Constants.Date.HOURS_IN_DAY.toInt() / 2, actual.size)
        assertEquals(0, actual.first().fromHour)
        assertEquals(2, actual.first().toHour)
        assertEquals(22, actual.last().fromHour)
        assertEquals(24, actual.last().toHour)
        assertTrue(actual.all { it.duration == 0L })
    }

    @Test
    fun test_calculate_counts_task_inside_single_hour() {
        val actual = calculator.calculate(
            timeTasks = listOf(timeTask(date(2026, Calendar.JULY, 1, 9, 15), date(2026, Calendar.JULY, 1, 9, 45))),
            globalTimeRange = TimeRange(date(2026, Calendar.JULY, 1, 0, 0), date(2026, Calendar.JULY, 2, 0, 0)),
        )

        assertEquals(30 * Constants.Date.MILLIS_IN_MINUTE, actual.fetchBucket(8).duration)
        assertEquals(30 * Constants.Date.MILLIS_IN_MINUTE, actual.sumOf { it.duration })
    }

    @Test
    fun test_calculate_splits_task_between_several_hours() {
        val actual = calculator.calculate(
            timeTasks = listOf(timeTask(date(2026, Calendar.JULY, 1, 9, 30), date(2026, Calendar.JULY, 1, 11, 15))),
            globalTimeRange = TimeRange(date(2026, Calendar.JULY, 1, 0, 0), date(2026, Calendar.JULY, 2, 0, 0)),
        )

        assertEquals(30 * Constants.Date.MILLIS_IN_MINUTE, actual.fetchBucket(8).duration)
        assertEquals(75 * Constants.Date.MILLIS_IN_MINUTE, actual.fetchBucket(10).duration)
    }

    @Test
    fun test_calculate_splits_overnight_task_by_clock_hours() {
        val actual = calculator.calculate(
            timeTasks = listOf(timeTask(date(2026, Calendar.JULY, 1, 23, 0), date(2026, Calendar.JULY, 2, 9, 0))),
            globalTimeRange = TimeRange(date(2026, Calendar.JULY, 1, 0, 0), date(2026, Calendar.JULY, 2, 12, 0)),
        )

        assertEquals(2 * Constants.Date.MILLIS_IN_HOUR, actual.fetchBucket(0).duration)
        assertEquals(2 * Constants.Date.MILLIS_IN_HOUR, actual.fetchBucket(2).duration)
        assertEquals(2 * Constants.Date.MILLIS_IN_HOUR, actual.fetchBucket(4).duration)
        assertEquals(2 * Constants.Date.MILLIS_IN_HOUR, actual.fetchBucket(6).duration)
        assertEquals(Constants.Date.MILLIS_IN_HOUR, actual.fetchBucket(8).duration)
        assertEquals(Constants.Date.MILLIS_IN_HOUR, actual.fetchBucket(22).duration)
        assertEquals(10 * Constants.Date.MILLIS_IN_HOUR, actual.sumOf { it.duration })
    }

    @Test
    fun test_calculate_clips_task_by_global_time_range() {
        val actual = calculator.calculate(
            timeTasks = listOf(timeTask(date(2026, Calendar.JULY, 1, 8, 0), date(2026, Calendar.JULY, 1, 12, 0))),
            globalTimeRange = TimeRange(date(2026, Calendar.JULY, 1, 9, 30), date(2026, Calendar.JULY, 1, 10, 30)),
        )

        assertEquals(30 * Constants.Date.MILLIS_IN_MINUTE, actual.fetchBucket(8).duration)
        assertEquals(30 * Constants.Date.MILLIS_IN_MINUTE, actual.fetchBucket(10).duration)
        assertEquals(Constants.Date.MILLIS_IN_HOUR, actual.sumOf { it.duration })
    }

    @Test
    fun test_calculate_sums_several_tasks_in_same_hour() {
        val actual = calculator.calculate(
            timeTasks = listOf(
                timeTask(date(2026, Calendar.JULY, 1, 15, 0), date(2026, Calendar.JULY, 1, 15, 20)),
                timeTask(date(2026, Calendar.JULY, 2, 15, 10), date(2026, Calendar.JULY, 2, 15, 40)),
            ),
            globalTimeRange = TimeRange(date(2026, Calendar.JULY, 1, 0, 0), date(2026, Calendar.JULY, 3, 0, 0)),
        )

        assertEquals(50 * Constants.Date.MILLIS_IN_MINUTE, actual.fetchBucket(14).duration)
    }

    private fun timeTask(from: Date, to: Date) = TimeTask(
        date = from,
        timeRange = TimeRange(from = from, to = to),
    )

    private fun List<HourlyWorkLoadAnalytic>.fetchBucket(fromHour: Int) = checkNotNull(find { it.fromHour == fromHour })

    private fun date(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}
