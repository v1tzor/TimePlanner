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
package ru.aleshin.core.utils.managers

import ru.aleshin.core.utils.extensions.*
import java.util.*
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
interface DateManager {

    fun fetchCurrentDate(): Date
    fun fetchBeginningCurrentDay(): Date
    fun fetchEndCurrentDay(): Date
    fun calculateLeftTime(endTime: Date): Long
    fun calculateProgress(startTime: Date, endTime: Date): Float
    fun setCurrentHMS(date: Date): Date

    class Base @Inject constructor() : DateManager {

        override fun fetchCurrentDate() = checkNotNull(Calendar.getInstance().time)

        override fun fetchBeginningCurrentDay(): Date {
            val currentCalendar = Calendar.getInstance()
            return currentCalendar.setStartDay().time
        }

        override fun fetchEndCurrentDay(): Date {
            val currentCalendar = Calendar.getInstance()
            return currentCalendar.setEndDay().time
        }

        override fun calculateLeftTime(endTime: Date): Long {
            val currentDate = fetchCurrentDate()
            return endTime.time - currentDate.time
        }

        override fun calculateProgress(startTime: Date, endTime: Date): Float {
            val currentTime = fetchCurrentDate().time
            val pastTime = ((currentTime - startTime.time).toMinutes()).toFloat()
            val duration = ((endTime.time - startTime.time).toMinutes()).toFloat()
            val progress = pastTime / duration

            return if (progress < 0f) 0f else if (progress > 1f) 1f else progress
        }

        override fun setCurrentHMS(date: Date): Date {
            val currentCalendar = Calendar.getInstance()
            val targetCalendar = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
                set(Calendar.SECOND, currentCalendar.get(Calendar.SECOND))
                set(Calendar.MILLISECOND, currentCalendar.get(Calendar.MILLISECOND))
            }
            return targetCalendar.time
        }
    }
}
