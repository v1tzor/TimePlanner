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

import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsIntervalSlice
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface AnalyticsIntervalSplitter {

    fun splitByHour(
        timeRange: TimeRange,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): List<AnalyticsIntervalSlice>

    class Base @Inject constructor() : AnalyticsIntervalSplitter {

        override fun splitByHour(
            timeRange: TimeRange,
            timeZone: TimeZone,
        ): List<AnalyticsIntervalSlice> {
            if (!timeRange.from.before(timeRange.to)) return emptyList()
            val slices = mutableListOf<AnalyticsIntervalSlice>()
            var cursor = timeRange.from.time
            while (cursor < timeRange.to.time) {
                val cursorCalendar = Calendar.getInstance(timeZone).apply { timeInMillis = cursor }
                val millisAfterHour = cursorCalendar.get(Calendar.MINUTE) * MILLIS_IN_MINUTE +
                    cursorCalendar.get(Calendar.SECOND) * MILLIS_IN_SECOND +
                    cursorCalendar.get(Calendar.MILLISECOND)
                val boundary = cursor - millisAfterHour + MILLIS_IN_HOUR
                val sliceTo = minOf(boundary, timeRange.to.time)
                if (sliceTo <= cursor) break
                slices.add(
                    AnalyticsIntervalSlice(
                        civilDate = fetchCivilToken(cursorCalendar),
                        dayOfWeek = cursorCalendar.get(Calendar.DAY_OF_WEEK),
                        hourOfDay = cursorCalendar.get(Calendar.HOUR_OF_DAY),
                        durationMillis = sliceTo - cursor,
                    ),
                )
                cursor = sliceTo
            }
            return slices
        }

        private fun fetchCivilToken(localCalendar: Calendar): Date {
            return Calendar.getInstance(UTC).apply {
                clear()
                set(
                    localCalendar.get(Calendar.YEAR),
                    localCalendar.get(Calendar.MONTH),
                    localCalendar.get(Calendar.DAY_OF_MONTH),
                    0,
                    0,
                    0,
                )
            }.time
        }

        companion object {
            private val UTC = TimeZone.getTimeZone("UTC")
            private const val MILLIS_IN_SECOND = 1_000L
            private const val MILLIS_IN_MINUTE = 60L * MILLIS_IN_SECOND
            private const val MILLIS_IN_HOUR = 60L * MILLIS_IN_MINUTE
        }
    }
}
