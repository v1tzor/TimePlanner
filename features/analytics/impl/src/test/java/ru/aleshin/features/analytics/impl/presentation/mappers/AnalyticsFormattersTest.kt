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
package ru.aleshin.features.analytics.impl.presentation.mappers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsBucketGranularity
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsBucketFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsRangeFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsValueFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsBucketDatePattern
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class AnalyticsFormattersTest {

    private val rangeFormatter = AnalyticsRangeFormatter.Base()
    private val bucketFormatter = AnalyticsBucketFormatter.Base()
    private val valueFormatter = AnalyticsValueFormatter.Base()
    private val russianLocale = Locale.forLanguageTag("ru")

    @Test
    fun `format exact russian range examples`() {
        val current = date(2026, Calendar.JULY, 21)

        assertEquals("14–20 июл", rangeFormatter.format(range(TimePeriod.WEEK, date(2026, 6, 14), date(2026, 6, 20)), russianLocale, current))
        assertEquals("28 июл – 3 авг", rangeFormatter.format(range(TimePeriod.CUSTOM, date(2026, 6, 28), date(2026, 7, 3)), russianLocale, current))
        assertEquals("28 дек 2026 – 3 янв 2027", rangeFormatter.format(range(TimePeriod.CUSTOM, date(2026, 11, 28), date(2027, 0, 3)), russianLocale, current))
        assertEquals("Июль 2026", rangeFormatter.format(range(TimePeriod.MONTH, date(2026, 6, 1), date(2026, 6, 31)), russianLocale, current))
        assertEquals("Янв–Июн 2026", rangeFormatter.format(range(TimePeriod.HALF_YEAR, date(2026, 0, 1), date(2026, 5, 30)), russianLocale, current))
        assertEquals("2026", rangeFormatter.format(range(TimePeriod.YEAR, date(2026, 0, 1), date(2026, 11, 31)), russianLocale, current))
    }

    @Test
    fun `format duration and percent without invalid values`() {
        assertEquals("4 ч 31 мин", valueFormatter.formatDuration(16_260_000L, "ч", "мин"))
        assertEquals("+12%", valueFormatter.formatSignedPercent(0.12, russianLocale))
        assertEquals("0%", valueFormatter.formatPercent(Double.NaN, russianLocale))
    }

    @Test
    fun `format localized day unit forms`() {
        assertEquals("день", valueFormatter.formatDayUnit(1, russianLocale, "день|дня|дней"))
        assertEquals("дня", valueFormatter.formatDayUnit(2, russianLocale, "день|дня|дней"))
        assertEquals("дней", valueFormatter.formatDayUnit(5, russianLocale, "день|дня|дней"))
        assertEquals("дней", valueFormatter.formatDayUnit(11, russianLocale, "день|дня|дней"))
        assertEquals("дня", valueFormatter.formatDayUnit(22, russianLocale, "день|дня|дней"))
        assertEquals("day", valueFormatter.formatDayUnit(1, Locale.ENGLISH, "day|days|days"))
        assertEquals("days", valueFormatter.formatDayUnit(2, Locale.ENGLISH, "day|days|days"))
        assertEquals("dzień", valueFormatter.formatDayUnit(1, Locale.forLanguageTag("pl"), "dzień|dni|dni"))
        assertEquals("dni", valueFormatter.formatDayUnit(22, Locale.forLanguageTag("pl"), "dzień|dni|dni"))
    }

    @Test
    fun `format english and rtl ranges`() {
        val current = date(2026, Calendar.JULY, 21)
        val english = rangeFormatter.format(range(TimePeriod.WEEK, date(2026, 6, 14), date(2026, 6, 20)), Locale.ENGLISH, current)
        val rtl = rangeFormatter.format(range(TimePeriod.CUSTOM, date(2026, 6, 28), date(2026, 7, 3)), Locale.forLanguageTag("fa"), current)

        assertEquals("14–20 Jul", english)
        assertFalse(rtl.isBlank())
    }

    @Test
    fun `analytics locale uses valid product language tags`() {
        assertEquals("vi", TimePlannerLanguage.VN.fetchAnalyticsLocale().toLanguageTag())
        assertEquals("pt-BR", TimePlannerLanguage.PT_BR.fetchAnalyticsLocale().toLanguageTag())
        assertEquals("fa", TimePlannerLanguage.FA.fetchAnalyticsLocale().toLanguageTag())
    }

    @Test
    fun `bucket labels include year only when needed for uniqueness`() {
        assertEquals(
            "d MMM",
            fetchAnalyticsBucketDatePattern(
                listOf(
                    date(2026, Calendar.JANUARY, 1),
                    date(2026, Calendar.JULY, 1)
                )
            ),
        )
        assertEquals(
            "d MMM yyyy",
            fetchAnalyticsBucketDatePattern(
                listOf(
                    date(2026, Calendar.JANUARY, 1),
                    date(2027, Calendar.JANUARY, 1)
                )
            ),
        )
    }

    @Test
    fun `weekly bucket labels show their full civil range`() {
        assertEquals(
            "1–5 Jul",
            bucketFormatter.format(
                from = date(2026, Calendar.JULY, 1),
                to = date(2026, Calendar.JULY, 5),
                granularity = AnalyticsBucketGranularity.WEEK,
                locale = Locale.ENGLISH,
                includeYear = false,
            ),
        )
        assertEquals(
            "29 Jun–5 Jul",
            bucketFormatter.format(
                from = date(2026, Calendar.JUNE, 29),
                to = date(2026, Calendar.JULY, 5),
                granularity = AnalyticsBucketGranularity.WEEK,
                locale = Locale.ENGLISH,
                includeYear = false,
            ),
        )
        assertEquals(
            "28 Dec 2026–3 Jan 2027",
            bucketFormatter.format(
                from = date(2026, Calendar.DECEMBER, 28),
                to = date(2027, Calendar.JANUARY, 3),
                granularity = AnalyticsBucketGranularity.WEEK,
                locale = Locale.ENGLISH,
                includeYear = true,
            ),
        )
    }

    private fun range(period: TimePeriod, from: Date, to: Date) = AnalyticsRangeUi(
        period = period,
        anchorDate = from,
        from = from,
        to = to,
        comparisonFrom = from,
        comparisonTo = to,
    )

    private fun date(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(year, month, day)
        }.time
    }
}
