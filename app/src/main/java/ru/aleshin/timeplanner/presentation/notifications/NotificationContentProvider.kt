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
import ru.aleshin.core.presentation.mappers.mapToString
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerStrings
import java.text.DateFormat
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.07.2026.
 */
interface NotificationContentProvider {

    fun fetchAlertContent(
        timeTask: TimeTask,
        notificationType: TaskNotificationType,
        strings: TimePlannerStrings,
    ): NotificationContent

    fun fetchOngoingContent(
        timeTask: TimeTask,
        strings: TimePlannerStrings,
    ): NotificationContent

    class Base @Inject constructor() : NotificationContentProvider {

        override fun fetchAlertContent(
            timeTask: TimeTask,
            notificationType: TaskNotificationType,
            strings: TimePlannerStrings,
        ): NotificationContent {
            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
            val timeRange = timeTask.timeRange.format(strings, timeFormat)
            val displayName = timeTask.fetchDisplayName(strings)
            val categoryContext = timeTask.fetchCategoryName(strings).takeIf {
                it.isNotBlank() &&
                    timeTask.subCategory?.name?.isNotBlank() == true &&
                    !it.equals(displayName, ignoreCase = true)
            }
            val state = when (notificationType) {
                TaskNotificationType.START -> strings.startTaskNotifyText
                TaskNotificationType.FIFTEEN_MINUTES_BEFORE -> strings.notificationBeforeFifteenMinutesText
                TaskNotificationType.ONE_HOUR_BEFORE -> strings.notificationBeforeOneHourText
                TaskNotificationType.THREE_HOUR_BEFORE -> strings.notificationBeforeThreeHoursText
                TaskNotificationType.ONE_DAY_BEFORE -> strings.notificationBeforeOneDayText
                TaskNotificationType.ONE_WEEK_BEFORE -> strings.notificationBeforeOneWeekText
                TaskNotificationType.AFTER_START_BEFORE_END -> strings.notificationEndsSoonText
                TaskNotificationType.END_ONGOING -> throw IllegalArgumentException()
            }
            val text = timeRange.withContext(categoryContext, strings)

            return NotificationContent(
                title = strings.notificationTitleFormat.format(displayName, state),
                text = text,
                expandedText = text.withNote(timeTask.note),
            )
        }

        override fun fetchOngoingContent(
            timeTask: TimeTask,
            strings: TimePlannerStrings,
        ): NotificationContent {
            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
            val timeRange = timeTask.timeRange.format(strings, timeFormat)
            val displayName = timeTask.fetchDisplayName(strings)
            val categoryContext = timeTask.fetchCategoryName(strings).takeIf {
                it.isNotBlank() &&
                    timeTask.subCategory?.name?.isNotBlank() == true &&
                    !it.equals(displayName, ignoreCase = true)
            }
            val text = timeRange.withContext(categoryContext, strings)

            return NotificationContent(
                title = strings.notificationTitleFormat.format(displayName, strings.ongoingTaskNotifyText),
                text = text,
                expandedText = text.withNote(timeTask.note),
            )
        }

        private fun TimeTask.fetchDisplayName(strings: TimePlannerStrings): String {
            return subCategory?.name?.trim().takeUnless { it.isNullOrBlank() }
                ?: fetchCategoryName(strings).takeIf { it.isNotBlank() }
                ?: strings.appName
        }

        private fun TimeTask.fetchCategoryName(strings: TimePlannerStrings): String {
            return (category.customName ?: category.default?.mapToString(strings).orEmpty()).trim()
        }

        private fun String.withContext(context: String?, strings: TimePlannerStrings): String {
            return context?.let { strings.notificationContextFormat.format(this, it) } ?: this
        }

        private fun String.withNote(note: String?): String? {
            return note?.trim()?.takeIf { it.isNotBlank() }?.let { "$this\n$it" }
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
    val expandedText: String? = null,
)
