/*
 * Copyright 2023 Stanislav Aleshin
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
package ru.aleshin.features.home.api.domain.entities.template

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.extensions.fetchDayNumberByMax
import ru.aleshin.core.utils.extensions.fetchMonth
import ru.aleshin.core.utils.extensions.fetchWeekDay
import ru.aleshin.core.utils.extensions.fetchWeekNumber
import ru.aleshin.core.utils.functional.Month
import ru.aleshin.core.utils.functional.WeekDay
import java.util.Date

/**
 * @author Stanislav Aleshin on 03.08.2023.
 */
@Parcelize
sealed class RepeatTime : Parcelable {

    abstract val type: RepeatTimeType

    data class WeekDays(val day: WeekDay) : RepeatTime() {
        @IgnoredOnParcel override val type = RepeatTimeType.WEEK_DAY
    }
    data class WeekDayInMonth(val day: WeekDay, val weekNumber: Int) : RepeatTime() {
        @IgnoredOnParcel override val type = RepeatTimeType.WEEK_DAY_IN_MONTH
    }
    data class MonthDay(val dayNumber: Int) : RepeatTime() {
        @IgnoredOnParcel override val type = RepeatTimeType.MONTH_DAY
    }
    data class YearDay(val month: Month, val dayNumber: Int) : RepeatTime() {
        @IgnoredOnParcel override val type = RepeatTimeType.YEAR_DAY
    }

    fun checkDateIsRepeat(date: Date) = when (this) {
        is WeekDays -> date.fetchWeekDay() == day
        is WeekDayInMonth -> date.fetchWeekDay() == day && date.fetchWeekNumber() == weekNumber
        is MonthDay -> date.fetchDayNumberByMax(dayNumber) == dayNumber
        is YearDay -> date.fetchDayNumberByMax(dayNumber) == dayNumber && date.fetchMonth() == month
    }
}
