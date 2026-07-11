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
package ru.aleshin.core.utils.functional

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.extensions.setStartDay
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 26.03.2023.
 */
@Parcelize
@Serializable
data class TimeRange(
    @Serializable(DateSerializer::class) val from: Date,
    @Serializable(DateSerializer::class) val to: Date,
) : Parcelable {

    fun periodDates(): List<Date> {
        if (from.after(to)) return emptyList()

        val startCalendar = from.startOfDayCalendar()
        val endCalendar = to.startOfDayCalendar()
        val dates = mutableListOf<Date>()

        while (!startCalendar.after(endCalendar)) {
            dates.add(startCalendar.time)
            startCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dates
    }

    private fun Date.startOfDayCalendar(): Calendar {
        return Calendar.getInstance().apply {
            time = this@startOfDayCalendar
            setStartDay()
        }
    }
}

infix fun Date.toRange(dateTo: Date): TimeRange = TimeRange(this, dateTo)