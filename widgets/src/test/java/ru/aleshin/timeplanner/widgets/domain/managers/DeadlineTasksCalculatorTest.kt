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
import ru.aleshin.core.domain.entities.tasks.TaskPriority
import ru.aleshin.core.domain.entities.tasks.UndefinedTask
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.timeplanner.widgets.domain.entities.deadlines.WidgetDeadlineType
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class DeadlineTasksCalculatorTest {

    private val calculator = DeadlineTasksCalculator.Base()

    @Test
    fun `classifies boundary today upcoming and inbox tasks`() {
        val currentTime = Date(1_800_000_000_000L)
        val currentDay = currentTime.startThisDay()
        val tasks = listOf(
            task(id = 1L, deadline = currentTime),
            task(id = 2L, deadline = Date(currentTime.time + 1L)),
            task(id = 3L, deadline = currentDay.shiftDay(1)),
            task(id = 4L, deadline = null),
        )

        val result = calculator.calculate(tasks, currentTime)

        assertEquals(
            listOf(
                WidgetDeadlineType.OVERDUE,
                WidgetDeadlineType.TODAY,
                WidgetDeadlineType.UPCOMING,
                WidgetDeadlineType.INBOX,
            ),
            result.tasks.map { it.type },
        )
        assertEquals(1, result.overdueCount)
        assertEquals(1, result.todayCount)
        assertEquals(2, result.upcomingCount)
    }

    @Test
    fun `sorts by deadline priority creation time and id`() {
        val currentTime = Date(1_800_000_000_000L)
        val deadline = Date(currentTime.time + 60_000L)
        val tasks = listOf(
            task(4L, deadline, TaskPriority.STANDARD, Date(4L)),
            task(3L, deadline, TaskPriority.MAX, Date(3L)),
            task(2L, deadline, TaskPriority.MAX, Date(2L)),
            task(1L, deadline, TaskPriority.MAX, Date(2L)),
        )

        val result = calculator.calculate(tasks, currentTime)

        assertEquals(listOf(1L, 2L, 3L, 4L), result.tasks.map { it.task.id })
    }

    @Test
    fun `returns empty summary for empty inbox`() {
        val result = calculator.calculate(emptyList(), Date(1_800_000_000_000L))

        assertEquals(emptyList<UndefinedTask>(), result.tasks.map { it.task })
        assertEquals(null, result.nearestDeadline)
        assertEquals(0, result.upcomingCount)
    }

    private fun task(
        id: Long,
        deadline: Date?,
        priority: TaskPriority = TaskPriority.STANDARD,
        createdAt: Date = Date(id),
    ) = UndefinedTask(
        id = id,
        createdAt = createdAt,
        deadline = deadline,
        mainCategory = MainCategory(id = id),
        priority = priority,
    )
}
