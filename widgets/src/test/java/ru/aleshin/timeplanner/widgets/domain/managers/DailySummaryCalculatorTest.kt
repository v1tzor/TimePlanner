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
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class DailySummaryCalculatorTest {

    private val calculator = DailySummaryCalculator.Base()

    @Test
    fun `calculates completed skipped and remaining statistics tasks`() {
        val currentTime = Date(1_800_000_000_000L)
        val day = currentTime.startThisDay()
        val tasks = listOf(
            task(1L, day, -3_600_000L, -1_800_000L, isCompleted = true),
            task(2L, day, -3_600_000L, -1_800_000L, isCompleted = false),
            task(3L, day, 1_800_000L, 3_600_000L, isCompleted = true),
            task(4L, day, -3_600_000L, -1_800_000L, isCompleted = true, statistics = false),
            task(5L, day.shiftDay(-1), -3_600_000L, -1_800_000L, isCompleted = true),
        )

        val result = calculator.calculate(tasks, currentTime)

        assertEquals(1, result.completedCount)
        assertEquals(1, result.skippedCount)
        assertEquals(1, result.remainingCount)
        assertEquals(3, result.allCount)
        assertEquals(5_400_000L, result.plannedDuration)
        assertEquals(1f / 3f, result.completion)
    }

    @Test
    fun `empty summary has zero completion`() {
        val result = calculator.calculate(emptyList(), Date(1_800_000_000_000L))

        assertEquals(0, result.allCount)
        assertEquals(0f, result.completion)
    }

    private fun task(
        id: Long,
        date: Date,
        fromOffset: Long,
        toOffset: Long,
        isCompleted: Boolean,
        statistics: Boolean = true,
    ) = TimeTask(
        key = id,
        date = date,
        timeRange = TimeRange(
            from = Date(1_800_000_000_000L + fromOffset),
            to = Date(1_800_000_000_000L + toOffset),
        ),
        category = MainCategory(id = id),
        isCompleted = isCompleted,
        isConsiderInStatistics = statistics,
    )
}
