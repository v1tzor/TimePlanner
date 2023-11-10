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
package ru.aleshin.features.editor.api.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ru.aleshin.core.ui.theme.tokens.TimePlannerIcons
import ru.aleshin.core.ui.theme.tokens.TimePlannerStrings
import ru.aleshin.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.core.utils.extensions.fetchCurrentLanguage
import ru.aleshin.features.home.api.domain.entities.template.RepeatTime
import ru.aleshin.features.home.api.domain.entities.template.Template
import ru.aleshin.features.home.api.presentation.mappers.mapToIcon
import ru.aleshin.features.home.api.presentation.mappers.mapToString
import ru.aleshin.features.home.api.presentation.models.NotificationTimeType
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 15.09.2023.
 */
interface TemplatesAlarmManager {

    fun addOrUpdateNotifyAlarm(template: Template, repeatTime: RepeatTime)

    fun addRawNotifyAlarm(
        templateId: Int,
        timeType: NotificationTimeType,
        repeatTime: RepeatTime,
        time: Date,
        category: String,
        subCategory: String?,
        icon: Int?,
    )
    fun deleteNotifyAlarm(template: Template, repeatTime: RepeatTime)

    class Base @Inject constructor(
        private val context: Context,
        private val receiverProvider: AlarmReceiverProvider,
    ) : TemplatesAlarmManager {

        private val alarmManager: AlarmManager
            get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        private val coreIcons: TimePlannerIcons
            get() = fetchCoreIcons()

        private val coreString: TimePlannerStrings
            get() = fetchCoreStrings(fetchCoreLanguage(context.fetchCurrentLanguage()))

        override fun addOrUpdateNotifyAlarm(template: Template, repeatTime: RepeatTime) = addRawNotifyAlarm(
            templateId = template.templateId,
            timeType = NotificationTimeType.START_TASK,
            repeatTime = repeatTime,
            time = template.startTime,
            category = template.category.let { it.default?.mapToString(coreString) ?: it.customName } ?: "",
            subCategory = template.subCategory?.name,
            icon = template.category.default?.mapToIcon(coreIcons),
        )

        override fun addRawNotifyAlarm(
            templateId: Int,
            timeType: NotificationTimeType,
            repeatTime: RepeatTime,
            time: Date,
            category: String,
            subCategory: String?,
            icon: Int?,
        ) {
            val id = templateId + repeatTime.key
            val alarmIntent = receiverProvider.provideReceiverIntent(
                category = category,
                subCategory = subCategory.orEmpty(),
                icon = icon,
                appIcon = fetchCoreIcons().logo,
                time = time,
                templateId = templateId,
                repeatTime = repeatTime,
                timeType = timeType,
            )
            val pendingAlarmIntent = createPendingAlarmIntent(alarmIntent, id)
            val triggerTime = repeatTime.nextDate(time)

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime.time, pendingAlarmIntent)
        }

        override fun deleteNotifyAlarm(template: Template, repeatTime: RepeatTime) {
            val id = template.templateId + repeatTime.key
            val alarmIntent = receiverProvider.provideReceiverIntent(
                category = template.category.let { it.default?.mapToString(coreString) ?: it.customName } ?: "",
                subCategory = template.subCategory?.name.orEmpty(),
                icon = template.category.default?.mapToIcon(coreIcons),
                appIcon = fetchCoreIcons().logo,
                time = template.startTime,
                templateId = template.templateId,
                repeatTime = repeatTime,
                timeType = NotificationTimeType.START_TASK,
            )
            val pendingAlarmIntent = createPendingAlarmIntent(alarmIntent, id)

            alarmManager.cancel(pendingAlarmIntent)
            pendingAlarmIntent.cancel()
        }

        private fun createPendingAlarmIntent(
            alarmIntent: Intent,
            requestId: Int,
        ) = PendingIntent.getBroadcast(
            context,
            requestId,
            alarmIntent,
            PendingIntent.FLAG_MUTABLE,
        )
    }
}
