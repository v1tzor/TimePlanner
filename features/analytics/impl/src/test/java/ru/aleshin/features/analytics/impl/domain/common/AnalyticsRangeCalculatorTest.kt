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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class AnalyticsRangeCalculatorTest {

    private val calculator = AnalyticsRangeCalculator.Base()

    @Test
    fun lastSevenDaysIncludesAnchorAndAdjacentComparison() {
        val anchor = token(2026, Calendar.JULY, 21)
        val selection = calculator.calculate(
            period = TimePeriod.LAST_7_DAYS,
            anchorDate = anchor,
            customRange = null,
            locale = Locale.US,
            timeZone = UTC,
        )
        val previousAnchor = calculator.shift(TimePeriod.LAST_7_DAYS, anchor, null, -1).first
        val nextAnchor = calculator.shift(TimePeriod.LAST_7_DAYS, anchor, null, 1).first

        assertEquals(token(2026, Calendar.JULY, 15), selection.civilRange.from)
        assertEquals(token(2026, Calendar.JULY, 21), selection.civilRange.to)
        assertEquals(token(2026, Calendar.JULY, 8), calculator.localDateToCivilToken(selection.ranges.comparison.from, UTC))
        assertEquals(token(2026, Calendar.JULY, 14), calculator.localDateToCivilToken(selection.ranges.comparison.to, UTC))
        assertTrue(selection.ranges.comparison.to.before(selection.ranges.current.from))
        assertEquals(token(2026, Calendar.JULY, 14), previousAnchor)
        assertEquals(token(2026, Calendar.JULY, 28), nextAnchor)
    }

    @Test
    fun weekUsesExplicitRussianAndUsLocale() {
        val anchor = token(2026, Calendar.JULY, 15)
        val russian = calculator.calculate(TimePeriod.WEEK, anchor, null, Locale.forLanguageTag("ru"), UTC)
        val us = calculator.calculate(TimePeriod.WEEK, anchor, null, Locale.US, UTC)

        assertEquals(token(2026, Calendar.JULY, 13), russian.civilRange.from)
        assertEquals(token(2026, Calendar.JULY, 19), russian.civilRange.to)
        assertEquals(token(2026, Calendar.JULY, 12), us.civilRange.from)
        assertEquals(token(2026, Calendar.JULY, 18), us.civilRange.to)
    }

    @Test
    fun monthHalfYearAndYearUseCalendarBoundaries() {
        val leapMonth = calculator.calculate(
            TimePeriod.MONTH,
            token(2024, Calendar.FEBRUARY, 29),
            null,
            Locale.US,
            UTC,
        )
        val commonFebruary = calculator.calculate(
            TimePeriod.MONTH,
            token(2026, Calendar.FEBRUARY, 28),
            null,
            Locale.US,
            UTC,
        )
        val april = calculator.calculate(
            TimePeriod.MONTH,
            token(2026, Calendar.APRIL, 30),
            null,
            Locale.US,
            UTC,
        )
        val july = calculator.calculate(
            TimePeriod.MONTH,
            token(2026, Calendar.JULY, 31),
            null,
            Locale.US,
            UTC,
        )
        val firstHalfJanuary = calculator.calculate(
            TimePeriod.HALF_YEAR,
            token(2026, Calendar.JANUARY, 1),
            null,
            Locale.US,
            UTC,
        )
        val firstHalf = calculator.calculate(
            TimePeriod.HALF_YEAR,
            token(2026, Calendar.JUNE, 30),
            null,
            Locale.US,
            UTC,
        )
        val secondHalf = calculator.calculate(
            TimePeriod.HALF_YEAR,
            token(2026, Calendar.JULY, 1),
            null,
            Locale.US,
            UTC,
        )
        val secondHalfDecember = calculator.calculate(
            TimePeriod.HALF_YEAR,
            token(2026, Calendar.DECEMBER, 31),
            null,
            Locale.US,
            UTC,
        )
        val year = calculator.calculate(
            TimePeriod.YEAR,
            token(2026, Calendar.DECEMBER, 31),
            null,
            Locale.US,
            UTC,
        )

        assertEquals(token(2024, Calendar.FEBRUARY, 1), leapMonth.civilRange.from)
        assertEquals(token(2024, Calendar.FEBRUARY, 29), leapMonth.civilRange.to)
        assertEquals(token(2026, Calendar.FEBRUARY, 28), commonFebruary.civilRange.to)
        assertEquals(token(2026, Calendar.APRIL, 30), april.civilRange.to)
        assertEquals(token(2026, Calendar.JULY, 1), july.civilRange.from)
        assertEquals(token(2026, Calendar.JULY, 31), july.civilRange.to)
        assertEquals(token(2026, Calendar.JANUARY, 1), firstHalfJanuary.civilRange.from)
        assertEquals(token(2026, Calendar.JUNE, 30), firstHalfJanuary.civilRange.to)
        assertEquals(token(2026, Calendar.JANUARY, 1), firstHalf.civilRange.from)
        assertEquals(token(2026, Calendar.JUNE, 30), firstHalf.civilRange.to)
        assertEquals(token(2026, Calendar.JULY, 1), secondHalf.civilRange.from)
        assertEquals(token(2026, Calendar.DECEMBER, 31), secondHalf.civilRange.to)
        assertEquals(token(2026, Calendar.JULY, 1), secondHalfDecember.civilRange.from)
        assertEquals(token(2026, Calendar.DECEMBER, 31), secondHalfDecember.civilRange.to)
        assertEquals(token(2026, Calendar.JANUARY, 1), year.civilRange.from)
        assertEquals(token(2026, Calendar.DECEMBER, 31), year.civilRange.to)
    }

    @Test
    fun customOneDayShiftAndMoveToCurrentPreserveInclusiveDuration() {
        val oneDay = AnalyticsCivilDateRange(
            token(2026, Calendar.JULY, 10),
            token(2026, Calendar.JULY, 10),
        )
        val shiftedOneDay = calculator.shift(TimePeriod.CUSTOM, oneDay.from, oneDay, 1).second
        val range = AnalyticsCivilDateRange(
            token(2026, Calendar.JULY, 10),
            token(2026, Calendar.JULY, 12),
        )
        val shifted = calculator.shift(TimePeriod.CUSTOM, range.from, range, -1).second
        val moved = calculator.moveToCurrent(
            TimePeriod.CUSTOM,
            localDate(2026, Calendar.AUGUST, 3, UTC),
            range,
            UTC,
        ).second

        assertEquals(
            AnalyticsCivilDateRange(token(2026, Calendar.JULY, 11), token(2026, Calendar.JULY, 11)),
            shiftedOneDay,
        )
        assertEquals(
            AnalyticsCivilDateRange(token(2026, Calendar.JULY, 7), token(2026, Calendar.JULY, 9)),
            shifted,
        )
        assertEquals(
            AnalyticsCivilDateRange(token(2026, Calendar.AUGUST, 3), token(2026, Calendar.AUGUST, 5)),
            moved,
        )
    }

    @Test
    fun localBoundariesSurviveDstAndPickerTokensAcrossOffsets() {
        val newYork = TimeZone.getTimeZone("America/New_York")
        val range = AnalyticsCivilDateRange(
            token(2026, Calendar.MARCH, 8),
            token(2026, Calendar.MARCH, 8),
        )
        val selection = calculator.calculate(TimePeriod.CUSTOM, range.from, range, Locale.US, newYork)
        val start = Calendar.getInstance(newYork).apply { time = selection.ranges.current.from }
        val end = Calendar.getInstance(newYork).apply { time = selection.ranges.current.to }
        val repeatedHourRange = AnalyticsCivilDateRange(
            token(2026, Calendar.NOVEMBER, 1),
            token(2026, Calendar.NOVEMBER, 1),
        )
        val repeatedHourSelection = calculator.calculate(
            TimePeriod.CUSTOM,
            repeatedHourRange.from,
            repeatedHourRange,
            Locale.US,
            newYork,
        )
        val pickerToken = token(2026, Calendar.JULY, 21).time

        assertEquals(newYork.id, selection.timeZone.id)
        assertEquals(8, start.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, start.get(Calendar.HOUR_OF_DAY))
        assertEquals(8, end.get(Calendar.DAY_OF_MONTH))
        assertEquals(23, end.get(Calendar.HOUR_OF_DAY))
        assertEquals(23L * 60L * 60L * 1_000L - 1L, selection.ranges.current.to.time - selection.ranges.current.from.time)
        assertEquals(
            25L * 60L * 60L * 1_000L - 1L,
            repeatedHourSelection.ranges.current.to.time - repeatedHourSelection.ranges.current.from.time,
        )
        assertEquals(token(2026, Calendar.JULY, 21), calculator.pickerTokenToCivilToken(pickerToken))
        assertEquals(pickerToken, calculator.civilTokenToPickerToken(Date(pickerToken)))

        val plusFourteen = TimeZone.getTimeZone("Pacific/Kiritimati")
        val minusSeven = TimeZone.getTimeZone("America/Los_Angeles")
        assertEquals(
            token(2026, Calendar.JULY, 22),
            calculator.localDateToCivilToken(localDate(2026, Calendar.JULY, 22, plusFourteen), plusFourteen),
        )
        assertEquals(
            token(2026, Calendar.JULY, 21),
            calculator.localDateToCivilToken(localDate(2026, Calendar.JULY, 21, minusSeven), minusSeven),
        )
    }

    @Test
    fun everyComparisonIsStrictlyBeforeCurrentRange() {
        TimePeriod.entries.forEach { period ->
            val custom = if (period == TimePeriod.CUSTOM) {
                AnalyticsCivilDateRange(token(2026, Calendar.JULY, 10), token(2026, Calendar.JULY, 23))
            } else {
                null
            }
            val selection = calculator.calculate(
                period = period,
                anchorDate = token(2026, Calendar.JULY, 15),
                customRange = custom,
                locale = Locale.forLanguageTag("ru"),
                timeZone = UTC,
            )

            assertTrue(selection.comparisonCivilRange.to.before(selection.civilRange.from))
            assertTrue(selection.ranges.comparison.to.before(selection.ranges.current.from))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun reversedCustomRangeIsRejected() {
        val range = AnalyticsCivilDateRange(
            token(2026, Calendar.JULY, 12),
            token(2026, Calendar.JULY, 10),
        )

        calculator.calculate(TimePeriod.CUSTOM, range.from, range, Locale.US, UTC)
    }

    private fun token(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance(UTC).apply {
            clear()
            set(year, month, day, 0, 0, 0)
        }.time
    }

    private fun localDate(year: Int, month: Int, day: Int, timeZone: TimeZone): Date {
        return Calendar.getInstance(timeZone).apply {
            clear()
            set(year, month, day, 12, 0, 0)
        }.time
    }

    companion object {
        private val UTC = TimeZone.getTimeZone("UTC")
    }
}
