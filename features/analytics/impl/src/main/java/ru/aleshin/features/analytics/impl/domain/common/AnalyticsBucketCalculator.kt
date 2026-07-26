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

import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsBucketGranularity
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsSourceBucket
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface AnalyticsBucketCalculator {

    fun calculate(
        range: AnalyticsCivilDateRange,
        locale: Locale,
    ): Pair<AnalyticsBucketGranularity, List<AnalyticsSourceBucket>>

    class Base @Inject constructor() : AnalyticsBucketCalculator {

        override fun calculate(
            range: AnalyticsCivilDateRange,
            locale: Locale,
        ): Pair<AnalyticsBucketGranularity, List<AnalyticsSourceBucket>> {
            require(!range.from.after(range.to))
            val inclusiveDays = ((range.to.time - range.from.time) / MILLIS_IN_DAY).toInt() + 1
            val granularity = when {
                inclusiveDays <= MAX_DAY_BUCKET_RANGE -> AnalyticsBucketGranularity.DAY
                inclusiveDays <= MAX_WEEK_BUCKET_RANGE -> AnalyticsBucketGranularity.WEEK
                else -> AnalyticsBucketGranularity.MONTH
            }
            val buckets = when (granularity) {
                AnalyticsBucketGranularity.DAY -> calculateDayBuckets(range)
                AnalyticsBucketGranularity.WEEK -> calculateWeekBuckets(range, locale)
                AnalyticsBucketGranularity.MONTH -> calculateMonthBuckets(range)
            }
            return granularity to buckets
        }

        private fun calculateDayBuckets(range: AnalyticsCivilDateRange): List<AnalyticsSourceBucket> {
            val buckets = mutableListOf<AnalyticsSourceBucket>()
            var date = range.from
            while (!date.after(range.to)) {
                buckets.add(
                    AnalyticsSourceBucket(
                        range = AnalyticsCivilDateRange(date, date),
                    )
                )
                date = add(date, Calendar.DAY_OF_YEAR, 1)
            }
            return buckets
        }

        private fun calculateWeekBuckets(
            range: AnalyticsCivilDateRange,
            locale: Locale,
        ): List<AnalyticsSourceBucket> {
            val firstDay = if (locale.language == RUSSIAN_LANGUAGE) {
                Calendar.MONDAY
            } else {
                calendar(range.from, locale).firstDayOfWeek
            }
            val fromCalendar = calendar(range.from, locale)
            val offset = Math.floorMod(fromCalendar.get(Calendar.DAY_OF_WEEK) - firstDay, DAYS_IN_WEEK)
            var bucketStart = add(range.from, Calendar.DAY_OF_YEAR, -offset)
            val buckets = mutableListOf<AnalyticsSourceBucket>()
            while (!bucketStart.after(range.to)) {
                val calendarTo = add(bucketStart, Calendar.DAY_OF_YEAR, 6)
                val clippedFrom = if (bucketStart.before(range.from)) range.from else bucketStart
                val clippedTo = if (calendarTo.after(range.to)) range.to else calendarTo
                buckets.add(
                    AnalyticsSourceBucket(
                        range = AnalyticsCivilDateRange(clippedFrom, clippedTo),
                    ),
                )
                bucketStart = add(bucketStart, Calendar.DAY_OF_YEAR, DAYS_IN_WEEK)
            }
            return buckets
        }

        private fun calculateMonthBuckets(range: AnalyticsCivilDateRange): List<AnalyticsSourceBucket> {
            val firstCalendar = calendar(range.from).apply { set(Calendar.DAY_OF_MONTH, 1) }
            var bucketStart = firstCalendar.time
            val buckets = mutableListOf<AnalyticsSourceBucket>()
            while (!bucketStart.after(range.to)) {
                val nextMonth = add(bucketStart, Calendar.MONTH, 1)
                val calendarTo = add(nextMonth, Calendar.DAY_OF_YEAR, -1)
                val clippedFrom = if (bucketStart.before(range.from)) range.from else bucketStart
                val clippedTo = if (calendarTo.after(range.to)) range.to else calendarTo
                buckets.add(
                    AnalyticsSourceBucket(
                        range = AnalyticsCivilDateRange(clippedFrom, clippedTo),
                    ),
                )
                bucketStart = nextMonth
            }
            return buckets
        }

        private fun add(date: Date, field: Int, amount: Int): Date {
            return calendar(date).apply { add(field, amount) }.time
        }

        private fun calendar(date: Date, locale: Locale = Locale.ROOT): Calendar {
            return Calendar.getInstance(UTC, locale).apply { time = date }
        }

        companion object {
            private val UTC = TimeZone.getTimeZone("UTC")
            private const val MAX_DAY_BUCKET_RANGE = 14
            private const val MAX_WEEK_BUCKET_RANGE = 186
            private const val DAYS_IN_WEEK = 7
            private const val MILLIS_IN_DAY = 86_400_000L
            private const val RUSSIAN_LANGUAGE = "ru"
        }
    }
}
