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
package ru.aleshin.timeplanner.presentation.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LightingColorFilter
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import ru.aleshin.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.core.utils.extensions.fetchLocale
import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.Month
import ru.aleshin.core.utils.functional.WeekDay
import ru.aleshin.core.utils.notifications.NotificationCreator
import ru.aleshin.core.utils.notifications.parameters.NotificationDefaults
import ru.aleshin.core.utils.notifications.parameters.NotificationPriority
import ru.aleshin.features.editor.api.presentation.TemplatesAlarmManager
import ru.aleshin.features.home.api.domain.entities.template.RepeatTime
import ru.aleshin.features.home.api.domain.entities.template.RepeatTimeType
import ru.aleshin.features.home.api.presentation.models.NotificationTimeType
import ru.aleshin.timeplanner.R
import ru.aleshin.timeplanner.presentation.mappers.mapToString
import ru.aleshin.timeplanner.presentation.ui.main.MainActivity

/**
 * @author Stanislav Aleshin on 29.03.2023.
 */
class TimeTaskAlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return

        if (intent.action == Constants.Alarm.ALARM_NOTIFICATION_ACTION) {
            showNotificationByIntent(context, intent)
        }
    }
    
    private fun showNotificationByIntent(context: Context, intent: Intent) {
        val notificationCreator = NotificationCreator.Base(context)
        val coreStrings = fetchCoreStrings(fetchCoreLanguage(context.fetchLocale().language))

        val timeType = intent.getStringExtra(Constants.Alarm.NOTIFICATION_TIME_TYPE)?.let { type ->
            NotificationTimeType.valueOf(type)
        } ?: NotificationTimeType.START_TASK
        val repeatTypeName = intent.getStringExtra(Constants.Alarm.REPEAT_TYPE)
        val repeatTime = repeatTypeName?.let { RepeatTimeType.valueOf(it) }
        val category = checkNotNull(intent.getStringExtra(Constants.Alarm.NOTIFICATION_CATEGORY))
        val subCategory = checkNotNull(intent.getStringExtra(Constants.Alarm.NOTIFICATION_SUBCATEGORY))
        val iconData = intent.getIntExtra(Constants.Alarm.NOTIFICATION_ICON, 0)
        val icon = if (iconData == 0) null else iconData
        val appIcon = checkNotNull(intent.getIntExtra(Constants.Alarm.APP_ICON, 0))
        
        val activityIntent = Intent(context, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE)
        
        val notification = notificationCreator.createNotify(
            channelId = Constants.Notification.CHANNEL_ID_NEW,
            title = if (subCategory.isNotEmpty()) "$category, $subCategory" else category,
            text = timeType.mapToString(coreStrings),
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
        notificationCreator.showNotify(notification, 0)

        if (repeatTime != null) {
            scheduleNextNotify(context, intent, timeType, repeatTime, category, subCategory, icon)
        }
    }
    
    private fun scheduleNextNotify(
        context: Context,
        intent: Intent,
        timeType: NotificationTimeType,
        repeatType: RepeatTimeType,
        category: String,
        subCategory: String?,
        icon: Int?,
    ) {
        val receiverProvider = AlarmReceiverProviderImpl(context)
        val templatesAlarmManager = TemplatesAlarmManager.Base(context, receiverProvider)
        
        val id = intent.getIntExtra(Constants.Alarm.TEMPLATE_ID, 0)
        val time = intent.getLongExtra(Constants.Alarm.REPEAT_TIME, 0)
        val dayNumber = intent.getIntExtra(Constants.Alarm.DAY_OF_MONTH, 0)
        val weekDayName = intent.getStringExtra(Constants.Alarm.WEEK_DAY)
        val weekNumber = intent.getIntExtra(Constants.Alarm.WEEK_NUMBER, 0)
        val month = intent.getStringExtra(Constants.Alarm.MONTH)

        val repeatTime = when (repeatType) {
            RepeatTimeType.WEEK_DAY -> RepeatTime.WeekDays(
                day = checkNotNull(weekDayName?.let { WeekDay.valueOf(it) }),
            )
            RepeatTimeType.WEEK_DAY_IN_MONTH -> RepeatTime.WeekDayInMonth(
                day = checkNotNull(weekDayName?.let { WeekDay.valueOf(it) }),
                weekNumber = weekNumber,
            )
            RepeatTimeType.MONTH_DAY -> RepeatTime.MonthDay(
                dayNumber = dayNumber,
            )
            RepeatTimeType.YEAR_DAY -> RepeatTime.YearDay(
                month = checkNotNull(month?.let { Month.valueOf(it) }),
                dayNumber = dayNumber,
            )
        }
        val targetDay = repeatTime.nextDate(time.mapToDate())
        templatesAlarmManager.addRawNotifyAlarm(id, timeType, repeatTime, targetDay, category, subCategory, icon)
    }
}
