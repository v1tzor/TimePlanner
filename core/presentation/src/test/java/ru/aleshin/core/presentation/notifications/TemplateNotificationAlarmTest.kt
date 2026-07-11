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
package ru.aleshin.core.presentation.notifications

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.utils.functional.Month
import ru.aleshin.core.utils.functional.WeekDay
import java.util.Calendar

/**
 * @author Stanislav Aleshin on 10.07.2026.
 */
class TemplateNotificationAlarmTest {

    @Test
    fun test_fetch_next_notification_alarm_selects_earliest_repeat_time() {
        val template = template(
            repeatTimes = listOf(
                RepeatTime.WeekDays(WeekDay.WEDNESDAY),
                RepeatTime.WeekDays(WeekDay.MONDAY),
            ),
        )

        val actual = template.fetchNextNotificationAlarm(
            currentDate = date(2026, Calendar.MARCH, 1, 9),
        )

        assertEquals(RepeatTime.WeekDays(WeekDay.MONDAY), actual?.repeatTime)
        assertEquals(date(2026, Calendar.MARCH, 2, 10), actual?.triggerTime)
    }

    @Test
    fun test_fetch_next_notification_alarm_uses_stable_rule_for_equal_trigger_times() {
        val template = template(
            repeatTimes = listOf(
                RepeatTime.YearDay(Month.MARCH, 1),
                RepeatTime.MonthDay(1),
            ),
        )

        val actual = template.fetchNextNotificationAlarm(
            currentDate = date(2026, Calendar.FEBRUARY, 28, 11),
        )

        assertEquals(RepeatTime.MonthDay(1), actual?.repeatTime)
        assertEquals(date(2026, Calendar.MARCH, 1, 10), actual?.triggerTime)
    }

    private fun template(repeatTimes: List<RepeatTime>) = Template(
        templateId = 42,
        startTime = date(2026, Calendar.JANUARY, 1, 10),
        endTime = date(2026, Calendar.JANUARY, 1, 11),
        category = MainCategory(),
        repeatEnabled = true,
        repeatTimes = repeatTimes,
    )

    private fun date(year: Int, month: Int, day: Int, hour: Int) = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}
