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
package ru.aleshin.features.overview.impl.presentation.ui.overview.views

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
internal class WeekTimelineLayoutTest {

    @Test
    fun calculatePositions_separatesConsecutiveShortTasks() {
        val date = date(day = 16)
        val timeTasks = listOf(
            timeTask(key = 1L, date = date, fromHour = 9, fromMinute = 0, toHour = 9, toMinute = 5),
            timeTask(key = 2L, date = date, fromHour = 9, fromMinute = 5, toHour = 9, toMinute = 15),
            timeTask(key = 3L, date = date, fromHour = 9, fromMinute = 15, toHour = 9, toMinute = 30),
        )

        val positions = WeekTimelineLayout.calculatePositions(
            timeTasks = timeTasks,
            date = date,
            timelineHeight = TIMELINE_HEIGHT,
            minimumTaskHeight = MINIMUM_TASK_HEIGHT,
            tasksSpace = TASKS_SPACE,
        )

        assertEquals(3, positions.size)
        assertTrue(positions.all { position -> position.height >= MINIMUM_TASK_HEIGHT })
        positions.zipWithNext().forEach { (current, next) ->
            assertTrue(next.top - current.top - current.height >= TASKS_SPACE - CALCULATION_TOLERANCE)
        }
    }

    @Test
    fun calculatePositions_keepsRegularTaskProportional() {
        val date = date(day = 16)
        val positions = WeekTimelineLayout.calculatePositions(
            timeTasks = listOf(
                timeTask(key = 1L, date = date, fromHour = 8, toHour = 10),
            ),
            date = date,
            timelineHeight = TIMELINE_HEIGHT,
            minimumTaskHeight = MINIMUM_TASK_HEIGHT,
            tasksSpace = TASKS_SPACE,
        )

        val expectedHeight = TIMELINE_HEIGHT / 12f - TASKS_SPACE

        assertEquals(expectedHeight, positions.single().height, CALCULATION_TOLERANCE)
    }

    @Test
    fun calculatePositions_clipsTasksByScheduleDay() {
        val date = date(day = 16)
        val previousDate = date(day = 15)
        val nextDate = date(day = 17)
        val positions = WeekTimelineLayout.calculatePositions(
            timeTasks = listOf(
                timeTask(
                    key = 1L,
                    date = previousDate,
                    fromHour = 23,
                    fromMinute = 55,
                    toDate = date,
                    toHour = 0,
                    toMinute = 10,
                ),
                timeTask(key = 2L, date = nextDate, fromHour = 9, toHour = 10),
            ),
            date = date,
            timelineHeight = TIMELINE_HEIGHT,
            minimumTaskHeight = MINIMUM_TASK_HEIGHT,
            tasksSpace = TASKS_SPACE,
        )

        assertEquals(1, positions.size)
        assertEquals(1L, positions.single().task.key)
        assertTrue(positions.single().top >= 0f)
    }

    @Test
    fun calculateHeight_expandsDenseTimeline() {
        val date = date(day = 16)
        val timeTasks = buildList {
            repeat(48) { index ->
                val fromMinutes = 8 * 60 + index * 5
                val toMinutes = fromMinutes + 5
                add(
                    timeTask(
                        key = index.toLong(),
                        date = date,
                        fromHour = fromMinutes / 60,
                        fromMinute = fromMinutes % 60,
                        toHour = toMinutes / 60,
                        toMinute = toMinutes % 60,
                    )
                )
            }
        }

        val timelineHeight = WeekTimelineLayout.calculateHeight(
            timeTasks = timeTasks,
            date = date,
            minimumTimelineHeight = TIMELINE_HEIGHT,
            minimumTaskHeight = MINIMUM_TASK_HEIGHT,
            tasksSpace = TASKS_SPACE,
        )
        val positions = WeekTimelineLayout.calculatePositions(
            timeTasks = timeTasks,
            date = date,
            timelineHeight = timelineHeight,
            minimumTaskHeight = MINIMUM_TASK_HEIGHT,
            tasksSpace = TASKS_SPACE,
        )

        assertTrue(timelineHeight > TIMELINE_HEIGHT)
        assertTrue(positions.all { position -> position.height >= MINIMUM_TASK_HEIGHT })
        positions.zipWithNext().forEach { (current, next) ->
            assertTrue(next.top - current.top - current.height >= TASKS_SPACE - CALCULATION_TOLERANCE)
        }
        assertTrue(positions.last().top + positions.last().height <= timelineHeight + CALCULATION_TOLERANCE)
    }

    private fun timeTask(
        key: Long,
        date: Date,
        fromHour: Int,
        fromMinute: Int = 0,
        toDate: Date = date,
        toHour: Int,
        toMinute: Int = 0,
    ) = TimeTaskUi(
        key = key,
        date = date,
        timeRanges = TimeRange(
            from = date.at(fromHour, fromMinute),
            to = toDate.at(toHour, toMinute),
        ),
        category = MainCategoryUi(id = key),
    )

    private fun date(day: Int): Date {
        return Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, day)
        }.time
    }

    private fun Date.at(hour: Int, minute: Int): Date {
        return Calendar.getInstance().apply {
            time = this@at
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.time
    }
}

private const val TIMELINE_HEIGHT = 238f
private const val MINIMUM_TASK_HEIGHT = 5f
private const val TASKS_SPACE = 1f
private const val CALCULATION_TOLERANCE = 0.01f
