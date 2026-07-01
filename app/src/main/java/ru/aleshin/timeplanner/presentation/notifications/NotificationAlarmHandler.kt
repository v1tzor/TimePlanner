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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LightingColorFilter
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.flow.first
import ru.aleshin.core.domain.entities.schedules.TaskNotificationType
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.RepeatTimeType
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.ui.mappers.mapToIcon
import ru.aleshin.core.ui.mappers.mapToString
import ru.aleshin.core.ui.models.NotificationTimeType
import ru.aleshin.core.ui.models.toTimeType
import ru.aleshin.core.ui.notifications.AlarmKeyFactory
import ru.aleshin.core.ui.notifications.TemplatesAlarmManager
import ru.aleshin.core.ui.notifications.TimeTaskAlarmManager
import ru.aleshin.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.core.utils.extensions.fetchCurrentLanguage
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.Constants.Date.DAYS_IN_WEEK
import ru.aleshin.core.utils.functional.Month
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.WeekDay
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.notifications.NotificationCreator
import ru.aleshin.core.utils.notifications.parameters.NotificationDefaults
import ru.aleshin.core.utils.notifications.parameters.NotificationPriority
import ru.aleshin.timeplanner.R
import ru.aleshin.timeplanner.presentation.ui.main.MainActivity
import java.util.Date
import javax.inject.Inject
import ru.aleshin.timeplanner.presentation.mappers.mapToString as mapNotificationTimeToString

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
interface NotificationAlarmHandler {

    suspend fun handleAlarm(intent: Intent)

    suspend fun rescheduleAll()

    class Base @Inject constructor(
        private val context: Context,
        private val timeTaskRepository: TimeTaskRepository,
        private val scheduleRepository: ScheduleRepository,
        private val templatesRepository: TemplatesRepository,
        private val timeTaskAlarmManager: TimeTaskAlarmManager,
        private val templatesAlarmManager: TemplatesAlarmManager,
        private val notificationCreator: NotificationCreator,
        private val dateManager: DateManager,
        private val alarmKeyFactory: AlarmKeyFactory,
    ) : NotificationAlarmHandler {

        private val coreIcons
            get() = fetchCoreIcons()

        private val coreStrings
            get() = fetchCoreStrings(fetchCoreLanguage(context.fetchCurrentLanguage()))

        override suspend fun handleAlarm(intent: Intent) {
            if (!intent.hasExtra(Constants.Alarm.NOTIFICATION_ID)) return

            when {
                intent.hasExtra(Constants.Alarm.TIME_TASK_ID) -> handleTimeTaskAlarm(intent)
                intent.hasExtra(Constants.Alarm.TEMPLATE_ID) -> handleTemplateAlarm(intent)
            }
        }

        override suspend fun rescheduleAll() {
            val templates = templatesRepository.fetchAllTemplates().first()
            val repeatTemplates = templates.filter { template -> template.repeatEnabled && template.isEnableNotification }
            repeatTemplates.forEach { template ->
                template.repeatTimes.forEach { repeatTime ->
                    templatesAlarmManager.addOrUpdateNotifyAlarm(template, repeatTime)
                }
            }
            val currentDay = dateManager.fetchBeginningCurrentDay()
            val timeRange = TimeRange(
                from = currentDay.shiftDay(-DAYS_IN_WEEK),
                to = Date(Long.MAX_VALUE),
            )
            scheduleRepository.fetchSchedulesByRange(timeRange).first()
                .flatMap { schedule -> schedule.overlayTimeTasks + schedule.timeTasks }
                .distinctBy { timeTask -> timeTask.key }
                .filter { timeTask -> timeTask.isEnableNotification && !timeTask.isRepeatTemplateTask(repeatTemplates) }
                .forEach { timeTask -> timeTaskAlarmManager.addOrUpdateNotifyAlarm(timeTask) }
        }

        private suspend fun handleTimeTaskAlarm(intent: Intent) {
            val timeTaskId = intent.getLongExtra(Constants.Alarm.TIME_TASK_ID, 0L)
            val notificationType = intent.fetchTaskNotificationType() ?: return
            val timeTask = timeTaskRepository.fetchTimeTaskByKey(timeTaskId) ?: return
            if (!timeTask.isEnableNotification) return
            if (!timeTask.taskNotifications.toTypes(true).contains(notificationType)) return

            showNotification(
                category = timeTask.category.default?.mapToString(coreStrings) ?: timeTask.category.customName.orEmpty(),
                subCategory = timeTask.subCategory?.name.orEmpty(),
                icon = timeTask.category.default?.mapToIcon(coreIcons),
                appIcon = coreIcons.logo,
                timeType = notificationType.toTimeType(),
                notificationId = intent.fetchNotificationId(
                    alarmKeyFactory.fetchTimeTaskAlarmId(timeTask.key, notificationType),
                ),
            )
        }

        private suspend fun handleTemplateAlarm(intent: Intent) {
            val repeatTime = intent.fetchRepeatTime() ?: return
            val templateId = intent.getIntExtra(Constants.Alarm.TEMPLATE_ID, 0)
            val timeType = intent.fetchNotificationTimeType()
            val template = templatesRepository.fetchTemplatesById(templateId) ?: return
            if (!template.repeatEnabled || !template.isEnableNotification || !template.repeatTimes.contains(repeatTime)) return

            showNotification(
                category = template.category.default?.mapToString(coreStrings) ?: template.category.customName.orEmpty(),
                subCategory = template.subCategory?.name.orEmpty(),
                icon = template.category.default?.mapToIcon(coreIcons),
                appIcon = coreIcons.logo,
                timeType = timeType,
                notificationId = intent.fetchNotificationId(
                    alarmKeyFactory.fetchTemplateAlarmId(template.templateId, repeatTime, timeType),
                ),
            )
            templatesAlarmManager.addOrUpdateNotifyAlarm(template, repeatTime)
        }

        private fun TimeTask.isRepeatTemplateTask(templates: List<Template>): Boolean {
            return templates.any { template -> template.checkDateIsRepeat(date) && template.equalsIsTemplate(this) }
        }

        private fun showNotification(
            category: String,
            subCategory: String,
            icon: Int?,
            appIcon: Int,
            timeType: NotificationTimeType,
            notificationId: Int,
        ) {
            val titleCategory = category.ifBlank { Constants.App.NAME }
            val activityIntent = Intent(context, MainActivity::class.java)
            val contentIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE)
            val notification = notificationCreator.createNotify(
                channelId = Constants.Notification.CHANNEL_ID_NEW,
                title = if (subCategory.isNotEmpty()) "$titleCategory, $subCategory" else titleCategory,
                text = timeType.mapNotificationTimeToString(coreStrings),
                smallIcon = appIcon,
                largeIcon = icon?.let { largeIcon ->
                    val drawable = ContextCompat.getDrawable(context, largeIcon)
                    drawable?.colorFilter = LightingColorFilter(Color.DKGRAY, Color.DKGRAY)
                    return@let drawable?.toBitmap()
                },
                autoCancel = true,
                priority = NotificationPriority.MAX,
                contentIntent = contentIntent,
                notificationDefaults = NotificationDefaults(true, true, true),
                color = ContextCompat.getColor(context, R.color.notification_icon),
            )
            notificationCreator.showNotify(notification, notificationId)
        }

        private fun Intent.fetchNotificationTimeType(): NotificationTimeType {
            return getStringExtra(Constants.Alarm.NOTIFICATION_TIME_TYPE)?.let { type ->
                runCatching { NotificationTimeType.valueOf(type) }.getOrNull()
            } ?: NotificationTimeType.START_TASK
        }

        private fun Intent.fetchTaskNotificationType(): TaskNotificationType? {
            return getStringExtra(Constants.Alarm.TIME_TASK_NOTIFICATION_TYPE)?.let { type ->
                runCatching { TaskNotificationType.valueOf(type) }.getOrNull()
            }
        }

        private fun Intent.fetchNotificationId(defaultId: Int): Int {
            return getIntExtra(Constants.Alarm.NOTIFICATION_ID, defaultId)
        }

        private fun Intent.fetchRepeatTime(): RepeatTime? {
            val repeatType = getStringExtra(Constants.Alarm.REPEAT_TYPE)?.let { type ->
                runCatching { RepeatTimeType.valueOf(type) }.getOrNull()
            } ?: return null
            val dayNumber = getIntExtra(Constants.Alarm.DAY_OF_MONTH, 0)
            val weekNumber = getIntExtra(Constants.Alarm.WEEK_NUMBER, 0)
            val weekDay = getStringExtra(Constants.Alarm.WEEK_DAY)?.let { day ->
                runCatching { WeekDay.valueOf(day) }.getOrNull()
            }
            val month = getStringExtra(Constants.Alarm.MONTH)?.let { month ->
                runCatching { Month.valueOf(month) }.getOrNull()
            }
            return when (repeatType) {
                RepeatTimeType.WEEK_DAY -> weekDay?.let { RepeatTime.WeekDays(it) }
                RepeatTimeType.WEEK_DAY_IN_MONTH -> weekDay?.let { RepeatTime.WeekDayInMonth(it, weekNumber) }
                RepeatTimeType.MONTH_DAY -> RepeatTime.MonthDay(dayNumber)
                RepeatTimeType.YEAR_DAY -> month?.let { RepeatTime.YearDay(it, dayNumber) }
            }
        }
    }
}
