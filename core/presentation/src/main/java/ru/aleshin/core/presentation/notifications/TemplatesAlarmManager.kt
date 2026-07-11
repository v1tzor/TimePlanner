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
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.presentation.mappers.mapToIcon
import ru.aleshin.core.presentation.mappers.mapToString
import ru.aleshin.core.presentation.models.NotificationTimeTypeUi
import ru.aleshin.core.utils.extensions.fetchCurrentLanguage
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerIcons
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerStrings
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreStrings
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 15.09.2023.
 */
interface TemplatesAlarmManager {

    fun addOrUpdateNotifyAlarm(template: Template)

    fun deleteNotifyAlarm(template: Template)

    class Base @Inject constructor(
        private val context: Context,
        private val receiverProvider: AlarmReceiverProvider,
        private val dateManager: DateManager,
        private val alarmKeyFactory: AlarmKeyFactory,
    ) : TemplatesAlarmManager {

        private val alarmManager: AlarmManager
            get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        private val coreIcons: TimePlannerIcons
            get() = fetchCoreIcons()

        private val coreString: TimePlannerStrings
            get() = fetchCoreStrings(fetchCoreLanguage(context.fetchCurrentLanguage()))

        override fun addOrUpdateNotifyAlarm(template: Template) {
            deleteNotifyAlarm(template)
            if (!template.repeatEnabled || !template.isEnableNotification) return

            val notificationAlarm = template.fetchNextNotificationAlarm(dateManager.fetchCurrentDate()) ?: return
            val id = alarmKeyFactory.fetchTemplateAlarmId(template.templateId)
            val alarmIntent = receiverProvider.provideReceiverIntent(
                category = template.category.let { it.default?.mapToString(coreString) ?: it.customName } ?: "",
                subCategory = template.subCategory?.name.orEmpty(),
                icon = template.category.default?.mapToIcon(coreIcons),
                appIcon = coreIcons.logo,
                notificationId = id,
                time = template.startTime,
                templateNotificationTriggerTime = notificationAlarm.triggerTime,
                templateId = template.templateId,
                repeatTime = notificationAlarm.repeatTime,
                timeType = NotificationTimeTypeUi.START_TASK,
            )
            val pendingAlarmIntent = createPendingAlarmIntent(alarmIntent, id)

            scheduleAlarm(notificationAlarm.triggerTime.time, pendingAlarmIntent)
        }

        override fun deleteNotifyAlarm(template: Template) {
            val id = alarmKeyFactory.fetchTemplateAlarmId(template.templateId)
            val alarmIntent = receiverProvider.provideReceiverIntent(
                category = "",
                subCategory = "",
                icon = null,
                appIcon = 0,
                notificationId = id,
                templateId = template.templateId,
                timeType = NotificationTimeTypeUi.START_TASK,
            )
            val flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            val pendingAlarmIntent = PendingIntent.getBroadcast(context, id, alarmIntent, flags) ?: return

            cancelAlarm(pendingAlarmIntent)
        }

        private fun scheduleAlarm(
            triggerTime: Long,
            pendingAlarmIntent: PendingIntent,
        ) {
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

        private fun createPendingAlarmIntent(
            alarmIntent: Intent,
            requestId: Int,
        ) = PendingIntent.getBroadcast(
            context,
            requestId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        private fun cancelAlarm(pendingAlarmIntent: PendingIntent) {
            alarmManager.cancel(pendingAlarmIntent)
            pendingAlarmIntent.cancel()
        }
    }
}
