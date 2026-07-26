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
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsBucketGranularity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.07.2026.
 */
internal interface AnalyticsBucketFormatter {

    fun format(
        from: Date,
        to: Date,
        granularity: AnalyticsBucketGranularity,
        locale: Locale,
        includeYear: Boolean,
    ): String

    class Base @Inject constructor() : AnalyticsBucketFormatter {

        override fun format(
            from: Date,
            to: Date,
            granularity: AnalyticsBucketGranularity,
            locale: Locale,
            includeYear: Boolean,
        ): String {
            require(!from.after(to))
            return when {
                granularity == AnalyticsBucketGranularity.DAY -> {
                    formatDate(
                        date = from,
                        pattern = if (includeYear) DAY_MONTH_YEAR_PATTERN else DAY_MONTH_PATTERN,
                        locale = locale,
                    )
                }
                granularity == AnalyticsBucketGranularity.MONTH && isWholeMonth(from, to, locale) -> {
                    formatDate(
                        date = from,
                        pattern = if (includeYear) MONTH_YEAR_PATTERN else MONTH_PATTERN,
                        locale = locale,
                    ).replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase(locale) else char.toString()
                    }
                }
                else -> formatRange(
                    from = from,
                    to = to,
                    locale = locale,
                    includeYear = includeYear,
                )
            }
        }

        private fun formatRange(
            from: Date,
            to: Date,
            locale: Locale,
            includeYear: Boolean,
        ): String {
            val fromCalendar = calendar(from, locale)
            val toCalendar = calendar(to, locale)
            val sameYear = fromCalendar[Calendar.YEAR] == toCalendar[Calendar.YEAR]
            val sameMonth = sameYear && fromCalendar[Calendar.MONTH] == toCalendar[Calendar.MONTH]

            return when {
                sameMonth && includeYear -> {
                    "${formatDate(from, DAY_PATTERN, locale)}–${
                        formatDate(to, DAY_MONTH_YEAR_PATTERN, locale)
                    }"
                }
                sameMonth -> {
                    "${formatDate(from, DAY_PATTERN, locale)}–${formatDate(to, DAY_MONTH_PATTERN, locale)}"
                }
                sameYear && includeYear -> {
                    "${formatDate(from, DAY_MONTH_PATTERN, locale)}–${
                        formatDate(to, DAY_MONTH_YEAR_PATTERN, locale)
                    }"
                }
                sameYear -> {
                    "${formatDate(from, DAY_MONTH_PATTERN, locale)}–${formatDate(to, DAY_MONTH_PATTERN, locale)}"
                }
                else -> {
                    "${formatDate(from, DAY_MONTH_YEAR_PATTERN, locale)}–${
                        formatDate(to, DAY_MONTH_YEAR_PATTERN, locale)
                    }"
                }
            }
        }

        private fun isWholeMonth(
            from: Date,
            to: Date,
            locale: Locale,
        ): Boolean {
            val fromCalendar = calendar(from, locale)
            val toCalendar = calendar(to, locale)
            return fromCalendar[Calendar.YEAR] == toCalendar[Calendar.YEAR] &&
                fromCalendar[Calendar.MONTH] == toCalendar[Calendar.MONTH] &&
                fromCalendar[Calendar.DAY_OF_MONTH] == 1 &&
                toCalendar[Calendar.DAY_OF_MONTH] == toCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        private fun formatDate(
            date: Date,
            pattern: String,
            locale: Locale,
        ): String {
            return SimpleDateFormat(pattern, locale).apply {
                timeZone = UTC
            }.format(date).replace(MONTH_DOT, EMPTY_STRING)
        }

        private fun calendar(
            date: Date,
            locale: Locale,
        ): Calendar {
            return Calendar.getInstance(UTC, locale).apply { time = date }
        }

        private companion object Companion {
            val UTC: TimeZone = TimeZone.getTimeZone("UTC")
            const val DAY_PATTERN = "d"
            const val DAY_MONTH_PATTERN = "d MMM"
            const val DAY_MONTH_YEAR_PATTERN = "d MMM yyyy"
            const val MONTH_PATTERN = "LLL"
            const val MONTH_YEAR_PATTERN = "LLL yyyy"
            const val MONTH_DOT = "."
            const val EMPTY_STRING = ""
        }
    }
}

@Composable
internal fun rememberAnalyticsBucketFormatter(): AnalyticsBucketFormatter {
    return remember { AnalyticsBucketFormatter.Base() }
}
