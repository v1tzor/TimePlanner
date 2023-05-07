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
package ru.aleshin.features.editor.api.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ru.aleshin.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.core.utils.di.ApplicationContext
import ru.aleshin.core.utils.extensions.fetchCurrentLanguage
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
import ru.aleshin.features.home.api.presentation.mappers.toIconRes
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.03.2023.
 */
interface TimeTaskAlarmManager {

    fun addNotifyAlarm(timeTask: TimeTask)
    fun updateNotifyAlarm(timeTask: TimeTask)
    fun deleteNotifyAlarm(timeTask: TimeTask)

    class Base @Inject constructor(
        @ApplicationContext private val context: Context,
        private val receiverProvider: AlarmReceiverProvider,
    ) : TimeTaskAlarmManager {

        private val alarmManager: AlarmManager
            get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        override fun addNotifyAlarm(timeTask: TimeTask) {
            val alarmIntent = createAlarmIntent(timeTask.category, timeTask.subCategory)
            val pendingAlarmIntent = createPendingAlarmIntent(alarmIntent, timeTask.key.toInt())
            val triggerTime = timeTask.timeRanges.from.time

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingAlarmIntent,
            )
        }

        override fun updateNotifyAlarm(timeTask: TimeTask) {
            val alarmIntent = createAlarmIntent(timeTask.category, timeTask.subCategory)
            val pendingAlarmIntent = createPendingAlarmIntent(alarmIntent, timeTask.key.toInt())
            val triggerTime = timeTask.timeRanges.from.time

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent)
        }

        override fun deleteNotifyAlarm(timeTask: TimeTask) {
            val alarmIntent = createAlarmIntent(timeTask.category, timeTask.subCategory)
            val pendingAlarmIntent = createPendingAlarmIntent(alarmIntent, timeTask.key.toInt())

            alarmManager.cancel(pendingAlarmIntent)
        }

        private fun createAlarmIntent(category: MainCategory, subCategory: SubCategory?): Intent {
            val categoryName = when (context.fetchCurrentLanguage() == "en") {
                true -> category.englishName ?: category.name
                false -> category.name
            }
            val subCategoryName = subCategory?.name ?: ""
            val categoryIcon = category.icon?.toIconRes(fetchCoreIcons())
            val appIcon = fetchCoreIcons().calendar

            return receiverProvider.provideReceiverIntent(
                categoryName,
                subCategoryName,
                categoryIcon,
                appIcon,
            )
        }

        private fun createPendingAlarmIntent(
            alarmIntent: Intent,
            requestId: Int,
        ) = PendingIntent.getBroadcast(
            context,
            requestId,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
