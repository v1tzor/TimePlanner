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
package ru.aleshin.core.data.mappers.schedules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.extensions.setHoursAndMinutes
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 30.08.2026.
 */
class TimeTaskDataMappersTest {

    private val scheduleDate = Calendar.getInstance().apply {
        set(2026, Calendar.AUGUST, 30)
        setStartDay()
    }.time

    @Test
    fun mapToData_keepsSingleDayTaskWithoutNextScheduleDate() {
        val timeTask = timeTask(start = scheduleDate.at(9, 0), end = scheduleDate.at(10, 0))

        val entity = timeTask.mapToData()

        assertNull(entity.nextScheduleDate)
    }

    @Test
    fun mapToData_keepsTaskEndingAtMidnightWithoutNextScheduleDate() {
        val timeTask = timeTask(start = scheduleDate.at(23, 0), end = scheduleDate.shiftDay(1))

        val entity = timeTask.mapToData()

        assertNull(entity.nextScheduleDate)
    }

    @Test
    fun mapToData_marksOvernightTaskWithNextScheduleDate() {
        val timeTask = timeTask(
            start = scheduleDate.at(23, 0),
            end = scheduleDate.shiftDay(1).at(1, 30),
        )

        val entity = timeTask.mapToData()

        assertEquals(scheduleDate.shiftDay(1).time, entity.nextScheduleDate)
    }

    @Test
    fun mapToData_marksTaskEndingOneMillisecondAfterMidnight() {
        val timeTask = timeTask(
            start = scheduleDate.at(23, 0),
            end = Date(scheduleDate.shiftDay(1).time + 1),
        )

        val entity = timeTask.mapToData()

        assertEquals(scheduleDate.shiftDay(1).time, entity.nextScheduleDate)
    }

    @Test
    fun mapToData_keepsZeroDurationTaskWithoutNextScheduleDate() {
        val timeTask = timeTask(start = scheduleDate.at(10, 0), end = scheduleDate.at(10, 0))

        val entity = timeTask.mapToData()

        assertNull(entity.nextScheduleDate)
    }

    private fun timeTask(start: Date, end: Date) = TimeTask(
        key = 1L,
        date = scheduleDate,
        timeRange = TimeRange(start, end),
        category = MainCategory(id = 1L),
    )

    private fun Date.at(hour: Int, minute: Int): Date {
        return Calendar.getInstance().apply {
            time = this@at
            setHoursAndMinutes(hour, minute)
        }.time
    }
}
