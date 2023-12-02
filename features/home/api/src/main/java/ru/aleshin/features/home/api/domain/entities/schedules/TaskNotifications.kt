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
package ru.aleshin.features.home.api.domain.entities.schedules

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.shiftHours
import ru.aleshin.core.utils.extensions.shiftMillis
import ru.aleshin.core.utils.extensions.shiftMinutes
import ru.aleshin.core.utils.functional.TimeRange

/**
 * @author Stanislav Aleshin on 10.11.2023.
 */
@Serializable
data class TaskNotifications(
    val fifteenMinutesBefore: Boolean = false,
    val oneHourBefore: Boolean = false,
    val threeHourBefore: Boolean = false,
    val oneDayBefore: Boolean = false,
    val oneWeekBefore: Boolean = false,
    val beforeEnd: Boolean = false,
) {
    fun toTypes(enabledNotifications: Boolean) = mutableListOf<TaskNotificationType>().apply {
        if (enabledNotifications) {
            add(TaskNotificationType.START)
            if (fifteenMinutesBefore) add(TaskNotificationType.FIFTEEN_MINUTES_BEFORE)
            if (oneHourBefore) add(TaskNotificationType.ONE_HOUR_BEFORE)
            if (threeHourBefore) add(TaskNotificationType.THREE_HOUR_BEFORE)
            if (oneDayBefore) add(TaskNotificationType.ONE_DAY_BEFORE)
            if (oneWeekBefore) add(TaskNotificationType.ONE_WEEK_BEFORE)
            if (beforeEnd) add(TaskNotificationType.AFTER_START_BEFORE_END)
        }
    }.toList()
}

enum class TaskNotificationType(val idAmount: Long) {
    START(0),
    FIFTEEN_MINUTES_BEFORE(60L),
    ONE_HOUR_BEFORE(10L),
    THREE_HOUR_BEFORE(20L),
    ONE_DAY_BEFORE(30L),
    ONE_WEEK_BEFORE(50L),
    AFTER_START_BEFORE_END(40L);

    fun fetchNotifyTrigger(timeRange: TimeRange) = when (this) {
        START -> timeRange.from
        FIFTEEN_MINUTES_BEFORE -> timeRange.from.shiftMinutes(-15)
        ONE_HOUR_BEFORE -> timeRange.from.shiftHours(-1)
        THREE_HOUR_BEFORE -> timeRange.from.shiftHours(-3)
        ONE_DAY_BEFORE -> timeRange.from.shiftDay(-1)
        ONE_WEEK_BEFORE -> timeRange.from.shiftDay(-7)
        AFTER_START_BEFORE_END -> timeRange.to.shiftMillis(-10000)
    }
}
