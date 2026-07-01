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
package ru.aleshin.core.domain.entities.template

import junit.framework.TestCase.assertEquals
import org.junit.Test
import ru.aleshin.core.utils.functional.WeekDay
import java.util.Calendar

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
class RepeatTimeTest {

    @Test
    fun test_next_date_or_current_returns_current_day_when_time_is_future() {
        val repeatTime = RepeatTime.WeekDays(WeekDay.TUESDAY)
        val current = date(year = 2026, month = Calendar.JUNE, day = 30, hour = 9, minute = 0)
        val startTime = date(year = 2026, month = Calendar.JANUARY, day = 1, hour = 10, minute = 0)

        val actual = repeatTime.nextDateOrCurrent(startTime, current)
        val expected = date(year = 2026, month = Calendar.JUNE, day = 30, hour = 10, minute = 0)

        assertEquals(expected, actual)
    }

    @Test
    fun test_next_date_or_current_returns_next_repeat_day_when_time_is_past() {
        val repeatTime = RepeatTime.WeekDays(WeekDay.TUESDAY)
        val current = date(year = 2026, month = Calendar.JUNE, day = 30, hour = 11, minute = 0)
        val startTime = date(year = 2026, month = Calendar.JANUARY, day = 1, hour = 10, minute = 0)

        val actual = repeatTime.nextDateOrCurrent(startTime, current)
        val expected = date(year = 2026, month = Calendar.JULY, day = 7, hour = 10, minute = 0)

        assertEquals(expected, actual)
    }

    private fun date(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ) = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}
