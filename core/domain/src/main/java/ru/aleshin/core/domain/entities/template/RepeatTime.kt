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
package ru.aleshin.core.domain.entities.template

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.extensions.fetchDayNumberByMax
import ru.aleshin.core.utils.extensions.fetchWeekDay
import ru.aleshin.core.utils.extensions.fetchWeekNumber
import ru.aleshin.core.utils.extensions.isYearDayOccurrence
import ru.aleshin.core.utils.extensions.setTimeWithoutDate
import ru.aleshin.core.utils.functional.Month
import ru.aleshin.core.utils.functional.WeekDay
import java.util.Calendar
import java.util.Date

private const val MAX_NEXT_DATE_LOOK_AHEAD_DAYS = 366 * 5

/**
 * @author Stanislav Aleshin on 03.08.2023.
 */
@Serializable
@Parcelize
sealed class RepeatTime : Parcelable {

    abstract val repeatType: RepeatTimeType
    abstract val key: Int

    @Serializable
    data class WeekDays(val day: WeekDay) : RepeatTime() {

        @IgnoredOnParcel
        override val repeatType = RepeatTimeType.WEEK_DAY

        @IgnoredOnParcel
        override val key = day.number
    }

    @Serializable
    data class WeekDayInMonth(val day: WeekDay, val weekNumber: Int) : RepeatTime() {

        @IgnoredOnParcel
        override val repeatType = RepeatTimeType.WEEK_DAY_IN_MONTH

        @IgnoredOnParcel
        override val key = day.number + weekNumber
    }

    @Serializable
    data class MonthDay(val dayNumber: Int) : RepeatTime() {

        @IgnoredOnParcel
        override val repeatType = RepeatTimeType.MONTH_DAY

        @IgnoredOnParcel
        override val key = dayNumber
    }

    @Serializable
    data class YearDay(val month: Month, val dayNumber: Int) : RepeatTime() {

        @IgnoredOnParcel
        override val repeatType = RepeatTimeType.YEAR_DAY

        @IgnoredOnParcel
        override val key = month.number + dayNumber
    }

    fun checkDateIsRepeat(date: Date) = when (this) {
        is WeekDays -> date.fetchWeekDay() == day
        is WeekDayInMonth -> date.fetchWeekDay() == day && date.fetchWeekNumber() == weekNumber
        is MonthDay -> date.fetchDayNumberByMax(dayNumber) == dayNumber
        is YearDay -> date.isYearDayOccurrence(month, dayNumber)
    }

    fun nextDateOrCurrent(startTime: Date, current: Date = Date()): Date {
        return nextDate(startTime, current)
    }

    fun nextDate(startTime: Date, current: Date = Date()): Date {
        val calendar = Calendar.getInstance().apply { time = current }

        repeat(MAX_NEXT_DATE_LOOK_AHEAD_DAYS) {
            val candidate = Calendar.getInstance().apply {
                time = calendar.time
                setTimeWithoutDate(startTime)
            }.time
            if (checkDateIsRepeat(candidate) && candidate > current) return candidate

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        error("Unable to find next repeat date for $this")
    }

    fun toAlarmKey() = when (this) {
        is RepeatTime.WeekDays -> "${repeatType.name}:${day.name}"
        is RepeatTime.WeekDayInMonth -> "${repeatType.name}:${day.name}:$weekNumber"
        is RepeatTime.MonthDay -> "${repeatType.name}:$dayNumber"
        is RepeatTime.YearDay -> "${repeatType.name}:${month.name}:$dayNumber"
    }

}
