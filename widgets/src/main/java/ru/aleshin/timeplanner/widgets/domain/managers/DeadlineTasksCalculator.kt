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

import ru.aleshin.core.domain.entities.tasks.UndefinedTask
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.timeplanner.widgets.domain.entities.deadlines.WidgetDeadlineTask
import ru.aleshin.timeplanner.widgets.domain.entities.deadlines.WidgetDeadlineType
import ru.aleshin.timeplanner.widgets.domain.entities.deadlines.WidgetDeadlinesSummary
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
interface DeadlineTasksCalculator {

    fun calculate(tasks: List<UndefinedTask>, currentTime: Date): WidgetDeadlinesSummary

    class Base @Inject constructor() : DeadlineTasksCalculator {

        override fun calculate(
            tasks: List<UndefinedTask>,
            currentTime: Date,
        ): WidgetDeadlinesSummary {
            val nextDay = currentTime.startThisDay().shiftDay(1)
            val deadlineTasks = tasks.map { task ->
                val deadline = task.deadline
                WidgetDeadlineTask(
                    task = task,
                    type = when {
                        deadline == null -> WidgetDeadlineType.INBOX
                        deadline.time <= currentTime.time -> WidgetDeadlineType.OVERDUE
                        deadline.time < nextDay.time -> WidgetDeadlineType.TODAY
                        else -> WidgetDeadlineType.UPCOMING
                    },
                )
            }.sortedWith(
                compareBy<WidgetDeadlineTask> { it.type.ordinal }
                    .thenBy { it.task.deadline?.time ?: Long.MAX_VALUE }
                    .thenByDescending { it.task.priority.ordinal }
                    .thenBy { it.task.createdAt?.time ?: Long.MAX_VALUE }
                    .thenBy { it.task.id },
            )

            return WidgetDeadlinesSummary(
                tasks = deadlineTasks,
                overdueCount = deadlineTasks.count { it.type == WidgetDeadlineType.OVERDUE },
                todayCount = deadlineTasks.count { it.type == WidgetDeadlineType.TODAY },
                upcomingCount = deadlineTasks.count {
                    it.type == WidgetDeadlineType.UPCOMING || it.type == WidgetDeadlineType.INBOX
                },
                nearestDeadline = deadlineTasks
                    .asSequence()
                    .mapNotNull { it.task.deadline }
                    .filter { it.time > currentTime.time }
                    .minByOrNull { it.time },
            )
        }
    }
}
