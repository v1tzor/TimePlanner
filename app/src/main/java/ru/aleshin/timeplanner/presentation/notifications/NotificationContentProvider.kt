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
package ru.aleshin.timeplanner.presentation.notifications

import ru.aleshin.core.domain.entities.tasks.TaskNotificationType
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.presentation.mappers.mapToString
import ru.aleshin.core.presentation.models.NotificationTimeTypeUi
import ru.aleshin.core.utils.extensions.changeDay
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerStrings
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.07.2026.
 */
interface NotificationContentProvider {

    fun fetchContent(
        timeTask: TimeTask,
        notificationType: TaskNotificationType,
        strings: TimePlannerStrings,
    ): NotificationContent

    fun fetchContent(
        template: Template,
        repeatTime: RepeatTime,
        timeType: NotificationTimeTypeUi,
        currentDate: Date,
        strings: TimePlannerStrings,
    ): NotificationContent

    class Base @Inject constructor() : NotificationContentProvider {

        override fun fetchContent(
            timeTask: TimeTask,
            notificationType: TaskNotificationType,
            strings: TimePlannerStrings,
        ): NotificationContent {
            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
            val title = timeTask.category.default?.mapToString(strings) ?: timeTask.category.customName.orEmpty()
            val categoryTitle = title.ifBlank { Constants.App.NAME }.withSubCategory(timeTask.subCategory?.name, strings)
            val timeRange = timeTask.timeRange.format(strings, timeFormat)
            if (notificationType == TaskNotificationType.END_ONGOING) {
                return NotificationContent(
                    title = strings.splitFormat.format(strings.ongoingTaskNotifyText, categoryTitle),
                    text = buildOngoingText(timeRange, timeTask.note),
                )
            }
            if (notificationType == TaskNotificationType.AFTER_START_BEFORE_END) {
                return NotificationContent(
                    title = strings.notificationEndsSoonText,
                    text = strings.notificationDetailsFormat.format(categoryTitle, timeRange),
                )
            }
            val details = when (notificationType) {
                TaskNotificationType.START, TaskNotificationType.END_ONGOING -> timeRange
                TaskNotificationType.AFTER_START_BEFORE_END -> strings.notificationEndsSoonText
                TaskNotificationType.FIFTEEN_MINUTES_BEFORE -> strings.notificationBeforeFifteenMinutesText
                TaskNotificationType.ONE_HOUR_BEFORE -> strings.notificationBeforeOneHourText
                TaskNotificationType.THREE_HOUR_BEFORE -> strings.notificationBeforeThreeHoursText
                TaskNotificationType.ONE_DAY_BEFORE -> strings.notificationBeforeOneDayText
                TaskNotificationType.ONE_WEEK_BEFORE -> strings.notificationBeforeOneWeekText
            }
            val text = when (notificationType) {
                TaskNotificationType.START, TaskNotificationType.END_ONGOING -> timeTask.note.orEmpty()
                else -> ""
            }
            return NotificationContent(
                title = strings.notificationDetailsFormat.format(categoryTitle, details),
                text = text,
            )
        }

        override fun fetchContent(
            template: Template,
            repeatTime: RepeatTime,
            timeType: NotificationTimeTypeUi,
            currentDate: Date,
            strings: TimePlannerStrings,
        ): NotificationContent {
            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
            val title = template.category.default?.mapToString(strings) ?: template.category.customName.orEmpty()
            val categoryTitle = title.ifBlank { Constants.App.NAME }.withSubCategory(template.subCategory?.name, strings)
            val startTime = repeatTime.fetchCurrentOccurrenceStartTime(template.startTime, currentDate)
            val endTime = when (template.endTime.isCurrentDay(template.startTime)) {
                true -> template.endTime.changeDay(startTime)
                false -> template.endTime.changeDay(startTime.shiftDay(1))
            }
            val details = when (timeType) {
                NotificationTimeTypeUi.START_TASK -> strings.notificationTimeRangeFormat.format(
                    timeFormat.format(startTime),
                    timeFormat.format(endTime),
                )
                NotificationTimeTypeUi.BEFORE_TASK -> strings.beforeTaskNotifyText
                NotificationTimeTypeUi.AFTER_TASK -> strings.notificationEndsSoonText
            }
            return NotificationContent(
                title = strings.notificationDetailsFormat.format(categoryTitle, details),
                text = "",
            )
        }

        private fun String.withSubCategory(subCategory: String?, strings: TimePlannerStrings): String {
            return when (subCategory.isNullOrBlank()) {
                true -> this
                false -> strings.splitFormat.format(this, subCategory)
            }
        }

        private fun buildOngoingText(timeRange: String, note: String?): String {
            return listOf(timeRange, note.orEmpty()).filter { it.isNotBlank() }.joinToString(separator = "\n")
        }

        private fun RepeatTime.fetchCurrentOccurrenceStartTime(startTime: Date, currentDate: Date): Date {
            val currentStartTime = startTime.changeDay(currentDate)
            return if (checkDateIsRepeat(currentStartTime)) {
                currentStartTime
            } else {
                nextDateOrCurrent(startTime, currentDate)
            }
        }

        private fun ru.aleshin.core.utils.functional.TimeRange.format(
            strings: TimePlannerStrings,
            timeFormat: DateFormat,
        ): String {
            return strings.notificationTimeRangeFormat.format(timeFormat.format(from), timeFormat.format(to))
        }
    }
}

/**
 * @author Stanislav Aleshin on 02.07.2026.
 */
data class NotificationContent(
    val title: String,
    val text: String,
)
