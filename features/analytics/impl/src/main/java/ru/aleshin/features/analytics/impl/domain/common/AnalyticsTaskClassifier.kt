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
package ru.aleshin.features.analytics.impl.domain.common

import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTask
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTaskStatus
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface AnalyticsTaskClassifier {

    fun prepare(
        tasks: List<TimeTask>,
        sourceRange: TimeRange,
        now: Date,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): List<AnalyticsTask>

    class Base @Inject constructor() : AnalyticsTaskClassifier {

        override fun prepare(
            tasks: List<TimeTask>,
            sourceRange: TimeRange,
            now: Date,
            timeZone: TimeZone,
        ): List<AnalyticsTask> {
            val sourceFrom = startOfDay(sourceRange.from, timeZone)
            val sourceTo = startOfDay(sourceRange.to, timeZone)
            val keys = mutableSetOf<Long>()
            return tasks.mapNotNull { task ->
                val sourceDate = startOfDay(task.date, timeZone)
                if (!task.isConsiderInStatistics || sourceDate !in sourceFrom..sourceTo || !keys.add(task.key)) {
                    return@mapNotNull null
                }
                val status = when {
                    task.timeRange.to.after(now) -> AnalyticsTaskStatus.UNFINISHED
                    task.isCompleted -> AnalyticsTaskStatus.COMPLETED
                    else -> AnalyticsTaskStatus.SKIPPED
                }
                AnalyticsTask(
                    timeTask = task,
                    status = status,
                    safeDurationMillis = (task.timeRange.to.time - task.timeRange.from.time).coerceAtLeast(0L),
                )
            }
        }

        private fun startOfDay(date: Date, timeZone: TimeZone): Long {
            return Calendar.getInstance(timeZone).apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
}
