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

import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.timeplanner.widgets.domain.entities.analytics.WidgetDailySummary
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
interface DailySummaryCalculator {

    fun calculate(tasks: List<TimeTask>, currentDate: Date): WidgetDailySummary

    class Base @Inject constructor() : DailySummaryCalculator {

        override fun calculate(tasks: List<TimeTask>, currentDate: Date): WidgetDailySummary {
            val currentDay = currentDate.startThisDay()
            val statisticTasks = tasks
                .distinctBy { it.key }
                .filter { it.isConsiderInStatistics && it.date.startThisDay() == currentDay }
            val completed = statisticTasks.count {
                it.timeRange.to.time <= currentDate.time && it.isCompleted
            }
            val skipped = statisticTasks.count {
                it.timeRange.to.time <= currentDate.time && !it.isCompleted
            }
            val remaining = statisticTasks.size - completed - skipped

            return WidgetDailySummary(
                completedCount = completed,
                skippedCount = skipped,
                remainingCount = remaining,
                allCount = statisticTasks.size,
                plannedDuration = statisticTasks.sumOf {
                    (it.timeRange.to.time - it.timeRange.from.time).coerceAtLeast(0L)
                },
                completion = if (statisticTasks.isEmpty()) {
                    0f
                } else {
                    completed / statisticTasks.size.toFloat()
                },
            )
        }
    }
}
