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
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.analytics.impl.domain.entities.HourlyWorkLoadAnalytic
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * @author Stanislav Aleshin on 03.07.2026.
 */
internal interface HourlyWorkLoadCalculator {

    fun calculate(timeTasks: List<TimeTask>, globalTimeRange: TimeRange): List<HourlyWorkLoadAnalytic>

    class Base @Inject constructor() : HourlyWorkLoadCalculator {

        override fun calculate(
            timeTasks: List<TimeTask>,
            globalTimeRange: TimeRange,
        ): List<HourlyWorkLoadAnalytic> {
            val hourlyDurations = LongArray(Constants.Date.HOURS_IN_DAY.toInt())

            timeTasks.forEach { timeTask ->
                var cursor = max(timeTask.timeRange.from.time, globalTimeRange.from.time)
                val end = min(timeTask.timeRange.to.time, globalTimeRange.to.time)

                while (cursor < end) {
                    val calendar = Calendar.getInstance().apply { timeInMillis = cursor }
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val nextHour = calendar.apply {
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        add(Calendar.HOUR_OF_DAY, 1)
                    }.timeInMillis
                    val bucketEnd = min(nextHour, end)

                    hourlyDurations[hour] += bucketEnd - cursor
                    cursor = bucketEnd
                }
            }

            return hourlyDurations.toList().chunked(HOURLY_WORK_LOAD_BUCKET_SIZE).mapIndexed { index, durations ->
                val fromHour = index * HOURLY_WORK_LOAD_BUCKET_SIZE
                HourlyWorkLoadAnalytic(
                    fromHour = fromHour,
                    toHour = fromHour + durations.size,
                    duration = durations.sum(),
                )
            }
        }
    }
}

private const val HOURLY_WORK_LOAD_BUCKET_SIZE = 3
