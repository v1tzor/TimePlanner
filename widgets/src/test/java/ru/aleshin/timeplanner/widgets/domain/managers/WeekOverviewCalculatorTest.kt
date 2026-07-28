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
package ru.aleshin.timeplanner.widgets.domain.managers

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.DailyScheduleStatus
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class WeekOverviewCalculatorTest {

    private val calculator = WeekOverviewCalculator.Base(FixedScheduleStatusChecker())

    @Test
    fun `merges overlaps clips overnight tasks and deduplicates weekly count`() {
        val day = Date(1_800_000_000_000L).startThisDay()
        val nextDay = day.shiftDay(1)
        val first = task(1L, day, 8, 10)
        val second = task(2L, day, 9, 11)
        val overnight = TimeTask(
            key = 3L,
            date = day,
            timeRange = TimeRange(
                from = Date(day.time + 23 * MILLIS_IN_HOUR),
                to = Date(nextDay.time + 2 * MILLIS_IN_HOUR),
            ),
            category = MainCategory(id = 3L),
        )
        val schedules = listOf(
            Schedule(day, timeTasks = listOf(first, second, overnight)),
            Schedule(nextDay, overlayTimeTasks = listOf(overnight)),
        )

        val result = calculator.calculate(listOf(day, nextDay), schedules)

        assertEquals(3, result.tasksCount)
        assertEquals(4 * MILLIS_IN_HOUR, result.days[0].workload)
        assertEquals(2 * MILLIS_IN_HOUR, result.days[1].workload)
        assertEquals(6 * MILLIS_IN_HOUR, result.totalWorkload)
    }

    private fun task(
        id: Long,
        date: Date,
        fromHour: Int,
        toHour: Int,
    ) = TimeTask(
        key = id,
        date = date,
        timeRange = TimeRange(
            from = Date(date.time + fromHour * MILLIS_IN_HOUR),
            to = Date(date.time + toHour * MILLIS_IN_HOUR),
        ),
        category = MainCategory(id = id),
    )
}

private class FixedScheduleStatusChecker : ScheduleStatusChecker {
    override fun fetchStatus(scheduleDate: Date) = DailyScheduleStatus.ACCOMPLISHMENT
    override fun fetchProgress(timeTasks: List<TimeTask>) = 0f
}

private const val MILLIS_IN_HOUR = 3_600_000L
