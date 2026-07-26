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
package ru.aleshin.features.analytics.impl.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface AnalyticsRangeFormatter {

    fun format(range: AnalyticsRangeUi, locale: Locale, currentDate: Date = Date()): String

    class Base @Inject constructor() : AnalyticsRangeFormatter {

        override fun format(range: AnalyticsRangeUi, locale: Locale, currentDate: Date): String {
            return when (range.period) {
                TimePeriod.MONTH -> formatMonth(range.from, locale)
                TimePeriod.HALF_YEAR -> formatHalfYear(range.from, range.to, locale)
                TimePeriod.YEAR -> formatDate(range.from, YEAR_PATTERN, locale)
                TimePeriod.LAST_7_DAYS,
                TimePeriod.WEEK,
                TimePeriod.CUSTOM -> formatRange(range.from, range.to, currentDate, locale)
            }
        }

        private fun formatMonth(date: Date, locale: Locale): String {
            return formatDate(date, MONTH_YEAR_PATTERN, locale).replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(locale) else char.toString()
            }
        }

        private fun formatHalfYear(from: Date, to: Date, locale: Locale): String {
            val fromMonth = formatShortMonth(from, locale)
            val toMonth = formatShortMonth(to, locale)
            val year = formatDate(from, YEAR_PATTERN, locale)
            return "$fromMonth–$toMonth $year".replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(locale) else char.toString()
            }
        }

        private fun formatRange(from: Date, to: Date, currentDate: Date, locale: Locale): String {
            val fromCalendar = from.toUtcCalendar(locale)
            val toCalendar = to.toUtcCalendar(locale)
            val currentCalendar = currentDate.toCalendar(locale)
            val sameYear = fromCalendar[Calendar.YEAR] == toCalendar[Calendar.YEAR]
            val sameMonth = sameYear && fromCalendar[Calendar.MONTH] == toCalendar[Calendar.MONTH]
            val isCurrentYear = sameYear && fromCalendar[Calendar.YEAR] == currentCalendar[Calendar.YEAR]

            return when {
                sameMonth && isCurrentYear -> {
                    "${formatDate(from, DAY_PATTERN, locale)}–${formatDate(to, DAY_MONTH_PATTERN, locale)}"
                }
                sameMonth -> {
                    "${formatDate(from, DAY_PATTERN, locale)}–${formatDate(to, DAY_MONTH_YEAR_PATTERN, locale)}"
                }
                sameYear && isCurrentYear -> {
                    "${formatDate(from, DAY_MONTH_PATTERN, locale)} – ${formatDate(to, DAY_MONTH_PATTERN, locale)}"
                }
                sameYear -> {
                    "${formatDate(from, DAY_MONTH_PATTERN, locale)} – ${formatDate(to, DAY_MONTH_YEAR_PATTERN, locale)}"
                }
                else -> {
                    "${formatDate(from, DAY_MONTH_YEAR_PATTERN, locale)} – ${formatDate(to, DAY_MONTH_YEAR_PATTERN, locale)}"
                }
            }
        }

        private fun formatDate(date: Date, pattern: String, locale: Locale): String {
            return SimpleDateFormat(pattern, locale).apply {
                timeZone = CIVIL_TIME_ZONE
            }.format(date).replace(MONTH_DOT, EMPTY_STRING)
        }

        private fun formatShortMonth(date: Date, locale: Locale): String {
            return formatDate(date, STANDALONE_MONTH_PATTERN, locale).take(SHORT_MONTH_LENGTH)
                .replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase(locale) else char.toString()
                }
        }

        private fun Date.toUtcCalendar(locale: Locale): Calendar {
            return Calendar.getInstance(CIVIL_TIME_ZONE, locale).apply { time = this@toUtcCalendar }
        }

        private fun Date.toCalendar(locale: Locale): Calendar {
            return Calendar.getInstance(locale).apply { time = this@toCalendar }
        }

        private companion object Companion {
            val CIVIL_TIME_ZONE: TimeZone = TimeZone.getTimeZone("UTC")
            const val DAY_PATTERN = "d"
            const val YEAR_PATTERN = "yyyy"
            const val STANDALONE_MONTH_PATTERN = "MMM"
            const val DAY_MONTH_PATTERN = "d MMM"
            const val DAY_MONTH_YEAR_PATTERN = "d MMM yyyy"
            const val MONTH_YEAR_PATTERN = "LLLL yyyy"
            const val MONTH_DOT = "."
            const val EMPTY_STRING = ""
            const val SHORT_MONTH_LENGTH = 3
        }
    }
}

@Composable
internal fun rememberAnalyticsRangeFormatter(): AnalyticsRangeFormatter {
    return remember { AnalyticsRangeFormatter.Base() }
}

internal fun Date.formatAnalyticsCivilDate(pattern: String, locale: Locale): String {
    return SimpleDateFormat(pattern, locale).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(this).replace(".", "")
}

internal fun TimePlannerLanguage.fetchAnalyticsLocale(): Locale = when (this) {
    TimePlannerLanguage.PT_BR -> Locale.forLanguageTag("pt-BR")
    TimePlannerLanguage.VN -> Locale.forLanguageTag("vi")
    else -> Locale.forLanguageTag(code)
}

internal fun fetchAnalyticsBucketDatePattern(dates: List<Date>): String {
    val yearCount = dates.map { date ->
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { time = date }.get(Calendar.YEAR)
    }.distinct().size
    return if (yearCount > 1) "d MMM yyyy" else "d MMM"
}
