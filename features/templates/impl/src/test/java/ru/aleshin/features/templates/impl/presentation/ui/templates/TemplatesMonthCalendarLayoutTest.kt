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
package ru.aleshin.features.templates.impl.presentation.ui.templates

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import ru.aleshin.core.utils.functional.WeekDay
import ru.aleshin.features.templates.impl.presentation.models.TemplatePatternDayUi
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.sections.buildTemplatesMonthCalendarDays
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class TemplatesMonthCalendarLayoutTest {

    @Test
    fun `aligns month days with locale week start`() {
        val days = listOf(
            createDay(dayNumber = 1, weekDay = WeekDay.WEDNESDAY),
            createDay(dayNumber = 2, weekDay = WeekDay.THURSDAY),
            createDay(dayNumber = 3, weekDay = WeekDay.FRIDAY),
        )

        val calendarDays = buildTemplatesMonthCalendarDays(
            days = days,
            firstDayOfWeek = Calendar.MONDAY,
        )

        assertEquals(7, calendarDays.size)
        assertNull(calendarDays[0])
        assertNull(calendarDays[1])
        assertEquals(1, calendarDays[2]?.dayNumber)
        assertEquals(3, calendarDays[4]?.dayNumber)
        assertNull(calendarDays[5])
        assertNull(calendarDays[6])
    }

    @Test
    fun `returns empty calendar for missing pattern`() {
        assertEquals(
            emptyList<TemplatePatternDayUi?>(),
            buildTemplatesMonthCalendarDays(
                days = emptyList(),
                firstDayOfWeek = Calendar.MONDAY,
            ),
        )
    }

    private fun createDay(
        dayNumber: Int,
        weekDay: WeekDay,
    ): TemplatePatternDayUi {
        return TemplatePatternDayUi(
            date = Date(dayNumber.toLong()),
            weekDay = weekDay,
            dayNumber = dayNumber,
            isCurrentDay = false,
            templatesCount = 0,
            templates = emptyList(),
        )
    }
}
