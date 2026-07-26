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

import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsRangeSelection
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsRanges
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface AnalyticsRangeCalculator {

    fun calculate(
        period: TimePeriod,
        anchorDate: Date,
        customRange: AnalyticsCivilDateRange?,
        locale: Locale,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): AnalyticsRangeSelection

    fun shift(
        period: TimePeriod,
        anchorDate: Date,
        customRange: AnalyticsCivilDateRange?,
        direction: Int,
    ): Pair<Date, AnalyticsCivilDateRange?>

    fun moveToCurrent(
        period: TimePeriod,
        currentDate: Date,
        customRange: AnalyticsCivilDateRange?,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): Pair<Date, AnalyticsCivilDateRange?>

    fun localDateToCivilToken(date: Date, timeZone: TimeZone = TimeZone.getDefault()): Date
    fun pickerTokenToCivilToken(token: Long): Date
    fun civilTokenToPickerToken(date: Date): Long
    fun normalizeCivilToken(date: Date): Date

    class Base @Inject constructor() : AnalyticsRangeCalculator {

        override fun calculate(
            period: TimePeriod,
            anchorDate: Date,
            customRange: AnalyticsCivilDateRange?,
            locale: Locale,
            timeZone: TimeZone,
        ): AnalyticsRangeSelection {
            val normalizedAnchor = normalizeCivilToken(anchorDate)
            val normalizedCustom = customRange?.let {
                AnalyticsCivilDateRange(normalizeCivilToken(it.from), normalizeCivilToken(it.to))
            }
            val currentCivilRange = fetchCurrentCivilRange(period, normalizedAnchor, normalizedCustom, locale)
            val comparisonCivilRange = fetchComparisonCivilRange(period, currentCivilRange, locale)

            return AnalyticsRangeSelection(
                period = period,
                anchorDate = normalizedAnchor,
                customRange = normalizedCustom,
                civilRange = currentCivilRange,
                comparisonCivilRange = comparisonCivilRange,
                ranges = AnalyticsRanges(
                    current = currentCivilRange.toLocalTimeRange(timeZone),
                    comparison = comparisonCivilRange.toLocalTimeRange(timeZone),
                ),
                locale = locale,
                timeZone = timeZone.clone() as TimeZone,
            )
        }

        override fun shift(
            period: TimePeriod,
            anchorDate: Date,
            customRange: AnalyticsCivilDateRange?,
            direction: Int,
        ): Pair<Date, AnalyticsCivilDateRange?> {
            require(direction == -1 || direction == 1)
            if (period == TimePeriod.CUSTOM) {
                val range = requireNotNull(customRange)
                require(!range.from.after(range.to))
                val shiftDays = inclusiveDays(range) * direction
                val shiftedRange = AnalyticsCivilDateRange(
                    from = addCivil(range.from, Calendar.DAY_OF_YEAR, shiftDays),
                    to = addCivil(range.to, Calendar.DAY_OF_YEAR, shiftDays),
                )
                return shiftedRange.from to shiftedRange
            }
            val field = when (period) {
                TimePeriod.LAST_7_DAYS -> Calendar.DAY_OF_YEAR
                TimePeriod.WEEK -> Calendar.WEEK_OF_YEAR
                TimePeriod.MONTH -> Calendar.MONTH
                TimePeriod.HALF_YEAR -> Calendar.MONTH
                TimePeriod.YEAR -> Calendar.YEAR
                TimePeriod.CUSTOM -> error("Unsupported period")
            }
            val amount = when (period) {
                TimePeriod.LAST_7_DAYS -> 7 * direction
                TimePeriod.HALF_YEAR -> 6 * direction
                else -> direction
            }
            return addCivil(anchorDate, field, amount) to customRange
        }

        override fun moveToCurrent(
            period: TimePeriod,
            currentDate: Date,
            customRange: AnalyticsCivilDateRange?,
            timeZone: TimeZone,
        ): Pair<Date, AnalyticsCivilDateRange?> {
            val today = localDateToCivilToken(currentDate, timeZone)
            if (period != TimePeriod.CUSTOM) return today to customRange

            val range = requireNotNull(customRange)
            require(!range.from.after(range.to))
            val movedRange = AnalyticsCivilDateRange(
                from = today,
                to = addCivil(today, Calendar.DAY_OF_YEAR, inclusiveDays(range) - 1),
            )
            return today to movedRange
        }

        override fun localDateToCivilToken(date: Date, timeZone: TimeZone): Date {
            val localCalendar = Calendar.getInstance(timeZone).apply { time = date }
            return createCivilToken(
                year = localCalendar.get(Calendar.YEAR),
                month = localCalendar.get(Calendar.MONTH),
                day = localCalendar.get(Calendar.DAY_OF_MONTH),
            )
        }

        override fun pickerTokenToCivilToken(token: Long): Date {
            return normalizeCivilToken(Date(token))
        }

        override fun civilTokenToPickerToken(date: Date): Long {
            return normalizeCivilToken(date).time
        }

        override fun normalizeCivilToken(date: Date): Date {
            val calendar = civilCalendar(date)
            return createCivilToken(
                year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH),
                day = calendar.get(Calendar.DAY_OF_MONTH),
            )
        }

        private fun fetchCurrentCivilRange(
            period: TimePeriod,
            anchorDate: Date,
            customRange: AnalyticsCivilDateRange?,
            locale: Locale,
        ): AnalyticsCivilDateRange {
            if (period == TimePeriod.CUSTOM) {
                val range = requireNotNull(customRange)
                require(!range.from.after(range.to))
                return range
            }
            val anchor = civilCalendar(anchorDate, locale)
            return when (period) {
                TimePeriod.LAST_7_DAYS -> AnalyticsCivilDateRange(
                    from = addCivil(anchorDate, Calendar.DAY_OF_YEAR, -6),
                    to = anchorDate,
                )
                TimePeriod.WEEK -> {
                    val firstDay = if (locale.language == RUSSIAN_LANGUAGE) {
                        Calendar.MONDAY
                    } else {
                        anchor.firstDayOfWeek
                    }
                    val dayOffset = Math.floorMod(anchor.get(Calendar.DAY_OF_WEEK) - firstDay, DAYS_IN_WEEK)
                    val from = addCivil(anchorDate, Calendar.DAY_OF_YEAR, -dayOffset)
                    AnalyticsCivilDateRange(from, addCivil(from, Calendar.DAY_OF_YEAR, 6))
                }
                TimePeriod.MONTH -> {
                    anchor.set(Calendar.DAY_OF_MONTH, 1)
                    val from = normalizeCivilToken(anchor.time)
                    AnalyticsCivilDateRange(from, addCivil(from, Calendar.DAY_OF_MONTH, anchor.getActualMaximum(Calendar.DAY_OF_MONTH) - 1))
                }
                TimePeriod.HALF_YEAR -> {
                    val firstMonth = if (anchor.get(Calendar.MONTH) < Calendar.JULY) Calendar.JANUARY else Calendar.JULY
                    val from = createCivilToken(anchor.get(Calendar.YEAR), firstMonth, 1)
                    AnalyticsCivilDateRange(from, addCivil(addCivil(from, Calendar.MONTH, 6), Calendar.DAY_OF_YEAR, -1))
                }
                TimePeriod.YEAR -> {
                    val from = createCivilToken(anchor.get(Calendar.YEAR), Calendar.JANUARY, 1)
                    AnalyticsCivilDateRange(from, addCivil(addCivil(from, Calendar.YEAR, 1), Calendar.DAY_OF_YEAR, -1))
                }
                TimePeriod.CUSTOM -> error("Unsupported period")
            }
        }

        private fun fetchComparisonCivilRange(
            period: TimePeriod,
            currentRange: AnalyticsCivilDateRange,
            locale: Locale,
        ): AnalyticsCivilDateRange {
            return when (period) {
                TimePeriod.LAST_7_DAYS,
                TimePeriod.CUSTOM,
                -> {
                    val comparisonTo = addCivil(currentRange.from, Calendar.DAY_OF_YEAR, -1)
                    AnalyticsCivilDateRange(
                        from = addCivil(comparisonTo, Calendar.DAY_OF_YEAR, -(inclusiveDays(currentRange) - 1)),
                        to = comparisonTo,
                    )
                }
                TimePeriod.WEEK -> fetchCurrentCivilRange(
                    period,
                    addCivil(currentRange.from, Calendar.WEEK_OF_YEAR, -1),
                    null,
                    locale,
                )
                TimePeriod.MONTH -> fetchCurrentCivilRange(
                    period,
                    addCivil(currentRange.from, Calendar.MONTH, -1),
                    null,
                    locale,
                )
                TimePeriod.HALF_YEAR -> fetchCurrentCivilRange(
                    period,
                    addCivil(currentRange.from, Calendar.MONTH, -6),
                    null,
                    locale,
                )
                TimePeriod.YEAR -> fetchCurrentCivilRange(
                    period,
                    addCivil(currentRange.from, Calendar.YEAR, -1),
                    null,
                    locale,
                )
            }
        }

        private fun AnalyticsCivilDateRange.toLocalTimeRange(timeZone: TimeZone): TimeRange {
            val fromCalendar = localCalendar(from, timeZone)
            val toCalendar = localCalendar(to, timeZone).apply { add(Calendar.DAY_OF_YEAR, 1) }
            return TimeRange(fromCalendar.time, Date(toCalendar.timeInMillis - 1L))
        }

        private fun localCalendar(date: Date, timeZone: TimeZone): Calendar {
            val civil = civilCalendar(date)
            return Calendar.getInstance(timeZone).apply {
                clear()
                set(
                    civil.get(Calendar.YEAR),
                    civil.get(Calendar.MONTH),
                    civil.get(Calendar.DAY_OF_MONTH),
                    0,
                    0,
                    0,
                )
                set(Calendar.MILLISECOND, 0)
            }
        }

        private fun inclusiveDays(range: AnalyticsCivilDateRange): Int {
            return ((range.to.time - range.from.time) / MILLIS_IN_DAY).toInt() + 1
        }

        private fun addCivil(date: Date, field: Int, amount: Int): Date {
            return civilCalendar(date).apply { add(field, amount) }.time
        }

        private fun civilCalendar(date: Date, locale: Locale = Locale.ROOT): Calendar {
            return Calendar.getInstance(UTC, locale).apply { time = date }
        }

        private fun createCivilToken(year: Int, month: Int, day: Int): Date {
            return Calendar.getInstance(UTC).apply {
                clear()
                set(year, month, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

        companion object {
            private val UTC = TimeZone.getTimeZone("UTC")
            private const val DAYS_IN_WEEK = 7
            private const val MILLIS_IN_DAY = 86_400_000L
            private const val RUSSIAN_LANGUAGE = "ru"
        }
    }
}
