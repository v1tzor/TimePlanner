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
 * imitations under the License.
 */
package ru.aleshin.core.utils.functional

/**
 * @author Stanislav Aleshin on 22.04.2023.
 */
enum class TimePeriod {
    WEEK, MONTH, HALF_YEAR, YEAR;

    fun convertToDays() = when (this) {
        WEEK -> Constants.Date.DAYS_IN_WEEK
        MONTH -> Constants.Date.DAYS_IN_MONTH
        HALF_YEAR -> Constants.Date.DAYS_IN_HALF_YEAR
        YEAR -> Constants.Date.DAYS_IN_YEAR
    }
}
