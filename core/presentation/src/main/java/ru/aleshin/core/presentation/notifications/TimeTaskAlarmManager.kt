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
package ru.aleshin.core.presentation.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.core.domain.entities.tasks.TaskNotificationType
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.presentation.mappers.mapToIcon
import ru.aleshin.core.presentation.mappers.mapToString
import ru.aleshin.core.presentation.models.toTimeType
import ru.aleshin.core.utils.extensions.fetchCurrentLanguage
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreStrings
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.03.2023.
 */
interface TimeTaskAlarmManager {

    fun addOrUpdateNotifyAlarm(timeTask: TimeTask)
    fun deleteNotifyAlarm(timeTask: TimeTask)

    class Base @Inject constructor(
        private val context: Context,
        private val receiverProvider: AlarmReceiverProvider,
        private val dateManager: DateManager,
        private val alarmKeyFactory: AlarmKeyFactory,
        private val ongoingNotificationManager: OngoingTimeTaskNotificationManager,
    ) : TimeTaskAlarmManager {

        private val alarmManager: AlarmManager
            get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        private val currentTime: Long
            get() = dateManager.fetchCurrentDate().time

        override fun addOrUpdateNotifyAlarm(timeTask: TimeTask) {
            timeTask.taskNotifications.toTypes(timeTask.isEnableNotification).forEach { type ->
                addOrUpdateNotifyAlarm(timeTask, type)
            }
            if (timeTask.isEnableNotification) {
                addOrUpdateNotifyAlarm(timeTask, TaskNotificationType.END_ONGOING)
                ongoingNotificationManager.addOrUpdate(timeTask)
            }
        }

        private fun addOrUpdateNotifyAlarm(timeTask: TimeTask, type: TaskNotificationType) {
            val id = alarmKeyFactory.fetchTimeTaskAlarmId(timeTask.key, type)
            val alarmIntent = createAlarmIntent(timeTask.key, timeTask.category, timeTask.subCategory, id, type)
            cancelLegacyAlarm(alarmIntent, id)
            val pendingAlarmIntent = createPendingAlarmIntent(alarmIntent, id)
            val triggerTime = type.fetchNotifyTrigger(timeTask.timeRange).time
            if (triggerTime > currentTime) {
                scheduleAlarm(triggerTime, pendingAlarmIntent)
            } else {
                cancelAlarm(pendingAlarmIntent)
            }
        }

        override fun deleteNotifyAlarm(timeTask: TimeTask) {
            TaskNotificationType.entries.forEach { type ->
                val id = alarmKeyFactory.fetchTimeTaskAlarmId(timeTask.key, type)
                val alarmIntent = createAlarmIntent(timeTask.key, timeTask.category, timeTask.subCategory, id, type)
                val pendingAlarmIntent = createPendingAlarmIntent(alarmIntent, id)
                cancelAlarm(pendingAlarmIntent)
                cancelLegacyAlarm(alarmIntent, id)
            }
            ongoingNotificationManager.delete(timeTask)
        }

        private fun createAlarmIntent(
            timeTaskKey: Long,
            category: MainCategory,
            subCategory: SubCategory?,
            notificationId: Int,
            taskNotificationType: TaskNotificationType,
        ): Intent {
            val language = fetchCoreLanguage(context.fetchCurrentLanguage())
            val categoryName = category.let { it.default?.mapToString(fetchCoreStrings(language)) ?: it.customName }
            val subCategoryName = subCategory?.name ?: ""
            val categoryIcon = category.default?.mapToIcon(fetchCoreIcons())
            val appIcon = fetchCoreIcons().logo

            return receiverProvider.provideReceiverIntent(
                category = categoryName ?: Constants.App.NAME,
                subCategory = subCategoryName,
                icon = categoryIcon,
                appIcon = appIcon,
                notificationId = notificationId,
                timeTaskId = timeTaskKey,
                taskNotificationType = taskNotificationType,
                timeType = taskNotificationType.toTimeType(),
            )
        }

        private fun scheduleAlarm(triggerTime: Long, pendingAlarmIntent: PendingIntent) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent)
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent)
                }
            } catch (exception: SecurityException) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent)
            } catch (exception: RuntimeException) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent)
            }
        }

        private fun createPendingAlarmIntent(alarmIntent: Intent, requestId: Int): PendingIntent {
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            return PendingIntent.getBroadcast(context, requestId, alarmIntent, flags)
        }

        private fun cancelLegacyAlarm(alarmIntent: Intent, requestId: Int) {
            val legacyIntent = Intent(alarmIntent).apply { data = null }
            val flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            val pendingIntent = PendingIntent.getBroadcast(context, requestId, legacyIntent, flags) ?: return

            cancelAlarm(pendingIntent)
        }

        private fun cancelAlarm(pendingAlarmIntent: PendingIntent) {
            alarmManager.cancel(pendingAlarmIntent)
            pendingAlarmIntent.cancel()
        }
    }
}
