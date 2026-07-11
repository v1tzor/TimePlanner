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
import ru.aleshin.core.domain.entities.tasks.TaskNotificationType
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.RepeatTimeType
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.presentation.mappers.mapToIcon
import ru.aleshin.core.presentation.notifications.AlarmKeyFactory
import ru.aleshin.core.presentation.notifications.OngoingTimeTaskNotificationManager
import ru.aleshin.core.presentation.notifications.TemplatesAlarmManager
import ru.aleshin.core.presentation.notifications.TimeTaskAlarmManager
import ru.aleshin.core.utils.extensions.fetchCurrentLanguage
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.Constants.Date.DAYS_IN_WEEK
import ru.aleshin.core.utils.functional.Month
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.WeekDay
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.notifications.NotificationCreator
import ru.aleshin.core.utils.notifications.parameters.NotificationDefaults
import ru.aleshin.core.utils.notifications.parameters.NotificationPriority
import ru.aleshin.core.utils.notifications.parameters.NotificationStyles
import ru.aleshin.timeplanner.R
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.timeplanner.presentation.ui.main.MainActivity
import java.util.Date
import javax.inject.Inject

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
        private val ongoingNotificationManager: OngoingTimeTaskNotificationManager,
        private val notificationCreator: NotificationCreator,
        private val notificationContentProvider: NotificationContentProvider,
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
            repeatTemplates.forEach(templatesAlarmManager::addOrUpdateNotifyAlarm)
            val repeatTimesMap = repeatTemplates.associate { Pair(it.templateId, it.repeatTimes) }
            val currentDay = dateManager.fetchBeginningCurrentDay()
            val timeRange = TimeRange(
                from = currentDay.shiftDay(-DAYS_IN_WEEK),
                to = Date(Long.MAX_VALUE),
            )
            scheduleRepository.fetchSchedulesByRange(timeRange).first()
                .flatMap { schedule -> schedule.overlayTimeTasks + schedule.timeTasks }
                .distinctBy { timeTask -> timeTask.key }
                .filter { timeTask -> timeTask.isEnableNotification }
                .forEach { timeTask ->
                    val isRunning = timeTask.isRunning(dateManager.fetchCurrentDate())
                    val isRepeat = repeatTimesMap[timeTask.linkedTemplateId]?.any { it.checkDateIsRepeat(timeTask.date) } == true
                    if (!isRepeat || isRunning) {
                        timeTaskAlarmManager.addOrUpdateNotifyAlarm(timeTask)
                    }
                }
        }

        private suspend fun handleTimeTaskAlarm(intent: Intent) {
            val timeTaskId = intent.getLongExtra(Constants.Alarm.TIME_TASK_ID, 0L)
            val notificationType = intent.fetchTaskNotificationType() ?: return
            val timeTask = timeTaskRepository.fetchTimeTaskById(timeTaskId) ?: return
            if (!timeTask.isEnableNotification) return
            if (notificationType == TaskNotificationType.END_ONGOING) {
                ongoingNotificationManager.delete(timeTask)
                return
            }
            if (!timeTask.taskNotifications.toTypes(true).contains(notificationType)) return

            val notificationId = intent.fetchNotificationId(
                defaultId = alarmKeyFactory.fetchTimeTaskAlarmId(timeTask.key, notificationType)
            )
            showNotification(
                content = notificationContentProvider.fetchContent(timeTask, notificationType, coreStrings),
                icon = timeTask.category.default?.mapToIcon(coreIcons),
                appIcon = coreIcons.logo,
                notificationTag = alarmKeyFactory.fetchTimeTaskAlarmTag(timeTask.key, notificationType),
                notificationId = notificationId,
            )
            if (notificationType == TaskNotificationType.START && timeTask.isRunning(dateManager.fetchCurrentDate())) {
                ongoingNotificationManager.addOrUpdate(timeTask)
            }
        }

        private suspend fun handleTemplateAlarm(intent: Intent) {
            val repeatTime = intent.fetchRepeatTime() ?: return
            val templateId = intent.getLongExtra(Constants.Alarm.TEMPLATE_ID, 0)
            val occurrenceDate = intent.getLongExtra(Constants.Alarm.TEMPLATE_NOTIFICATION_TRIGGER_TIME, 0L)
                .takeIf { time -> time > 0L }
                ?.let(::Date)
                ?: dateManager.fetchBeginningCurrentDay()
            val template = templatesRepository.fetchTemplatesByIdOnce(templateId) ?: return
            if (!template.repeatEnabled || !template.isEnableNotification || !template.repeatTimes.contains(repeatTime)) return
            val timeTask = timeTaskRepository.fetchTimeTaskByTemplate(templateId, occurrenceDate.startThisDay())

            if (timeTask != null && timeTask.isEnableNotification) {
                showNotification(
                    content = notificationContentProvider.fetchContent(
                        timeTask = timeTask,
                        notificationType = TaskNotificationType.START,
                        strings = coreStrings,
                    ),
                    icon = timeTask.category.default?.mapToIcon(coreIcons),
                    appIcon = coreIcons.logo,
                    notificationTag = alarmKeyFactory.fetchTemplateAlarmTag(template.templateId),
                    notificationId = intent.fetchNotificationId(
                        alarmKeyFactory.fetchTemplateAlarmId(template.templateId),
                    ),
                )
                timeTaskAlarmManager.addOrUpdateNotifyAlarm(timeTask)
            }
            templatesAlarmManager.addOrUpdateNotifyAlarm(template)
        }

        private fun showNotification(
            content: NotificationContent,
            icon: Int?,
            appIcon: Int,
            notificationTag: String,
            notificationId: Int,
        ) {
            val activityIntent = Intent(context, MainActivity::class.java)
            val contentIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE)
            val notification = notificationCreator.createNotify(
                channelId = Constants.Notification.CHANNEL_ID_NEW,
                title = content.title,
                text = content.text,
                smallIcon = appIcon,
                largeIcon = icon?.let { largeIcon ->
                    val drawable = ContextCompat.getDrawable(context, largeIcon)
                    drawable?.colorFilter = LightingColorFilter(Color.DKGRAY, Color.DKGRAY)
                    return@let drawable?.toBitmap()
                },
                autoCancel = true,
                ongoing = false,
                priority = NotificationPriority.MAX,
                contentIntent = contentIntent,
                notificationDefaults = NotificationDefaults(true, true, true),
                style = content.text.takeIf { it.isNotBlank() }?.let { NotificationStyles.BigTextStyle(it) },
                color = ContextCompat.getColor(context, R.color.notification_icon),
            )
            notificationCreator.showNotify(notification, notificationTag, notificationId)
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
