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

import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.timeplanner.widgets.domain.entities.analytics.WidgetWeekDay
import ru.aleshin.timeplanner.widgets.domain.entities.analytics.WidgetWeekOverview
import java.util.Date
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
interface WeekOverviewCalculator {

    fun calculate(targetDates: List<Date>, schedules: List<Schedule>): WidgetWeekOverview

    class Base @Inject constructor(
        private val scheduleStatusChecker: ScheduleStatusChecker,
    ) : WeekOverviewCalculator {

        override fun calculate(
            targetDates: List<Date>,
            schedules: List<Schedule>,
        ): WidgetWeekOverview {
            val schedulesByDate = schedules.associateBy { it.date.startThisDay().time }
            val days = targetDates.map { targetDate ->
                val date = targetDate.startThisDay()
                val tasks = schedulesByDate[date.time]?.allTimeTasks
                    .orEmpty()
                    .distinctBy { it.key }
                    .sortedBy { it.timeRange.from.time }
                val dayEnd = date.shiftDay(1)
                val workload = tasks.fetchWorkload(date, dayEnd)

                WidgetWeekDay(
                    date = date,
                    tasks = tasks,
                    workload = workload,
                    freeTime = (dayEnd.time - date.time - workload).coerceAtLeast(0L),
                    progress = scheduleStatusChecker.fetchProgress(tasks),
                )
            }

            return WidgetWeekOverview(
                tasksCount = days.flatMap { it.tasks }.distinctBy { it.key }.size,
                totalWorkload = days.sumOf { it.workload },
                busiestDay = days.maxWithOrNull(
                    compareBy<WidgetWeekDay> { it.workload }.thenByDescending { it.date.time },
                )?.date,
                days = days,
            )
        }

        private fun List<TimeTask>.fetchWorkload(
            dayStart: Date,
            dayEnd: Date,
        ): Long {
            val intervals = mapNotNull { task ->
                val from = max(task.timeRange.from.time, dayStart.time)
                val to = min(task.timeRange.to.time, dayEnd.time)
                if (from < to) from to to else null
            }.sortedBy { it.first }
            var workload = 0L
            var currentEnd = dayStart.time

            intervals.forEach { interval ->
                workload += when {
                    interval.second <= currentEnd -> 0L
                    interval.first < currentEnd -> interval.second - currentEnd
                    else -> interval.second - interval.first
                }
                currentEnd = max(currentEnd, interval.second)
            }
            return workload
        }
    }
}
