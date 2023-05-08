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
 * @author Stanislav Aleshin on 14.02.2023.
 */
object Constants {

    object Notification {
        const val CHANNEL_ID = "timeTaskAlarmChannel"
    }

    object Alarm {
        const val ALARM_NOTIFICATION_ACTION = "ru.aleshin.ALARM_NOTIFICATION_ACTION"
        const val NOTIFICATION_CATEGORY = "ALARM_DATA_CATEGORY"
        const val NOTIFICATION_SUBCATEGORY = "ALARM_DATA_SUBCATEGORY"
        const val NOTIFICATION_ICON = "ALARM_DATA_ICON"
        const val APP_ICON = "ALARM_DATA_APP_ICON"
    }

    object Placeholder {
        const val items = 6
    }

    object Delay {
        const val LOAD_ANIMATION = 400L
        const val SPLASH = 600L
        const val CHECK_STATUS = 5000L
    }

    object Date {
        const val DAY = 1
        const val DAYS_IN_WEEK = 7
        const val DAYS_IN_MONTH = 31
        const val DAYS_IN_YEAR = 365

        const val EMPTY_DURATION = 0L
        const val MILLIS_IN_SECONDS = 1000L
        const val MILLIS_IN_MINUTE = 60000L
        const val MILLIS_IN_HOUR = 3600000L
        const val SECONDS_IN_MINUTE = 60L
        const val MINUTES_IN_MILLIS = 60000L
        const val MINUTES_IN_HOUR = 60L
        const val HOURS_IN_DAY = 24L

        const val minutesFormat = "%s%s"
        const val hoursFormat = "%s%s"
        const val hoursAndMinutesFormat = "%s%s %s%s"
    }
}
