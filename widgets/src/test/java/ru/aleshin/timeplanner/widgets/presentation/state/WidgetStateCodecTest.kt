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
package ru.aleshin.timeplanner.widgets.presentation.state

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalProgressStatus
import ru.aleshin.core.domain.entities.goals.GoalScopeType
import ru.aleshin.core.domain.entities.tasks.TaskPriority
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.timeplanner.widgets.domain.entities.deadlines.WidgetDeadlineType
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetGoalUi
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetTaskUi
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetUndefinedTaskUi
import ru.aleshin.timeplanner.widgets.presentation.ui.deadlines.state.DeadlinesWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.ui.goals.state.GoalsWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.ui.today.state.TodayWidgetStateUi

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class WidgetStateCodecTest {

    @Test
    fun `returns default for corrupted state`() {
        val state = WidgetStateCodec.decodeOrDefault<TodayWidgetStateUi>("{broken") {
            TodayWidgetStateUi(version = 7)
        }

        assertEquals(7, state.version)
    }

    @Test
    fun `returns default for incompatible state version`() {
        val state = WidgetStateCodec.decodeCurrentOrDefault(
            value = """{"version":2,"updatedAt":42,"tasks":[],"futureField":true}""",
            version = TodayWidgetStateUi::version,
            defaultValue = ::TodayWidgetStateUi,
        )

        assertEquals(1, state.version)
        assertEquals(0L, state.updatedAt)
    }

    @Test
    fun `round trips typed time task values`() {
        val expected = TodayWidgetStateUi(
            tasks = listOf(
                WidgetTaskUi(
                    id = 1L,
                    date = 2L,
                    startTime = 3L,
                    endTime = 4L,
                    title = "Task",
                    subtitle = null,
                    categoryType = DefaultCategoryType.WORK,
                    priority = TaskPriority.MAX,
                    status = TimeTaskStatus.RUNNING,
                    isCompleted = false,
                ),
            ),
        )

        val actual = WidgetStateCodec.decodeOrDefault(
            value = WidgetStateCodec.encode(expected),
            defaultValue = ::TodayWidgetStateUi,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `round trips typed undefined task values`() {
        val expected = DeadlinesWidgetStateUi(
            tasks = listOf(
                WidgetUndefinedTaskUi(
                    id = 1L,
                    title = "Task",
                    subtitle = null,
                    categoryType = DefaultCategoryType.SHOPPING,
                    priority = TaskPriority.MEDIUM,
                    deadline = 2L,
                    deadlineTitle = "Tomorrow",
                    deadlineType = WidgetDeadlineType.UPCOMING,
                ),
            ),
        )

        val actual = WidgetStateCodec.decodeOrDefault(
            value = WidgetStateCodec.encode(expected),
            defaultValue = ::DeadlinesWidgetStateUi,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `round trips typed goal values`() {
        val expected = GoalsWidgetStateUi(
            goals = listOf(
                WidgetGoalUi(
                    id = 1L,
                    title = "Deep work",
                    categoryType = DefaultCategoryType.WORK,
                    scopeType = GoalScopeType.MAIN_CATEGORY,
                    metric = GoalMetric.DURATION,
                    direction = GoalDirection.AT_LEAST,
                    actualValue = 7_200_000L,
                    plannedValue = 10_800_000L,
                    targetValue = 14_400_000L,
                    remainingValue = 7_200_000L,
                    progressFraction = 0.5f,
                    progressTitle = "50%",
                    valueTitle = "2h / 4h",
                    deadline = 1_800_000_000_000L,
                    deadlineTitle = "3 Aug",
                    status = GoalProgressStatus.IN_PROGRESS,
                ),
            ),
        )

        val actual = WidgetStateCodec.decodeOrDefault(
            value = WidgetStateCodec.encode(expected),
            defaultValue = ::GoalsWidgetStateUi,
        )

        assertEquals(expected, actual)
    }
}
