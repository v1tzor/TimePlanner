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
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface AnalyticsValueFormatter {

    fun formatDuration(durationMillis: Long, hourSymbol: String, minuteSymbol: String): String
    fun formatSignedDuration(durationMillis: Long, hourSymbol: String, minuteSymbol: String): String
    fun formatDayUnit(count: Int, locale: Locale, unitForms: String): String
    fun formatPercent(value: Double?, locale: Locale): String
    fun formatSignedPercent(value: Double, locale: Locale): String

    class Base @Inject constructor() : AnalyticsValueFormatter {

        override fun formatDuration(
            durationMillis: Long,
            hourSymbol: String,
            minuteSymbol: String,
        ): String {
            val totalMinutes = (durationMillis.coerceAtLeast(0L) / MILLIS_IN_MINUTE)
            val hours = totalMinutes / MINUTES_IN_HOUR
            val minutes = totalMinutes % MINUTES_IN_HOUR
            return when {
                hours > 0L && minutes > 0L -> "$hours $hourSymbol $minutes $minuteSymbol"
                hours > 0L -> "$hours $hourSymbol"
                else -> "$minutes $minuteSymbol"
            }
        }

        override fun formatPercent(value: Double?, locale: Locale): String {
            val percent = value?.takeIf(Double::isFinite)?.times(PERCENT_MULTIPLIER)?.roundToInt() ?: 0
            return "${NumberFormat.getIntegerInstance(locale).format(percent)}%"
        }

        override fun formatDayUnit(count: Int, locale: Locale, unitForms: String): String {
            val forms = unitForms.split(UNIT_FORM_SEPARATOR, limit = UNIT_FORM_COUNT)
            val formIndex = when (locale.language) {
                RUSSIAN_LANGUAGE -> when {
                    count % 10 == 1 && count % 100 != 11 -> ONE_FORM_INDEX
                    count % 10 in 2..4 && count % 100 !in 12..14 -> FEW_FORM_INDEX
                    else -> MANY_FORM_INDEX
                }
                POLISH_LANGUAGE -> when {
                    count == 1 -> ONE_FORM_INDEX
                    count % 10 in 2..4 && count % 100 !in 12..14 -> FEW_FORM_INDEX
                    else -> MANY_FORM_INDEX
                }
                else -> if (count == 1) ONE_FORM_INDEX else MANY_FORM_INDEX
            }
            return forms.getOrElse(formIndex) { forms.lastOrNull().orEmpty() }
        }

        override fun formatSignedDuration(
            durationMillis: Long,
            hourSymbol: String,
            minuteSymbol: String,
        ): String {
            val sign = when {
                durationMillis > 0L -> PLUS_SIGN
                durationMillis < 0L -> MINUS_SIGN
                else -> EMPTY_STRING
            }
            return "$sign${formatDuration(abs(durationMillis), hourSymbol, minuteSymbol)}"
        }

        override fun formatSignedPercent(value: Double, locale: Locale): String {
            val percent = value.takeIf(Double::isFinite)?.times(PERCENT_MULTIPLIER)?.roundToInt() ?: 0
            val sign = when {
                percent > 0 -> PLUS_SIGN
                percent < 0 -> MINUS_SIGN
                else -> EMPTY_STRING
            }
            return "$sign${NumberFormat.getIntegerInstance(locale).format(abs(percent))}%"
        }

        private companion object Companion {
            const val MILLIS_IN_MINUTE = 60_000L
            const val MINUTES_IN_HOUR = 60L
            const val PERCENT_MULTIPLIER = 100.0
            const val PLUS_SIGN = "+"
            const val MINUS_SIGN = "−"
            const val EMPTY_STRING = ""
            const val UNIT_FORM_SEPARATOR = "|"
            const val UNIT_FORM_COUNT = 3
            const val ONE_FORM_INDEX = 0
            const val FEW_FORM_INDEX = 1
            const val MANY_FORM_INDEX = 2
            const val RUSSIAN_LANGUAGE = "ru"
            const val POLISH_LANGUAGE = "pl"
        }
    }
}

@Composable
internal fun rememberAnalyticsValueFormatter(): AnalyticsValueFormatter {
    return remember { AnalyticsValueFormatter.Base() }
}