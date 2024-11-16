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
package ru.aleshin.core.utils.functional

/**
 * @author Stanislav Aleshin on 03.08.2023.
 */
enum class WeekDay(val number: Int, val priority: Int) {
    SUNDAY(1, 0),
    MONDAY(2, 1),
    TUESDAY(3, 2),
    WEDNESDAY(4, 3),
    THURSDAY(5, 4),
    FRIDAY(6, 5),
    SATURDAY(7, 6);

    companion object {
        fun fetchByWeekDayNumber(week: Int): WeekDay {
            val weekInstance = entries.find { it.number == week }
            return weekInstance ?: throw IllegalArgumentException("Wrong week number: $week")
        }
    }
}
