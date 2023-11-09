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
import ru.aleshin.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.core.utils.extensions.fetchCurrentLanguage
import ru.aleshin.core.utils.functional.Constants.App
import ru.aleshin.features.home.api.domain.entities.categories.MainCategory
import ru.aleshin.features.home.api.domain.entities.categories.SubCategory
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import ru.aleshin.features.home.api.presentation.mappers.mapToIcon
import ru.aleshin.features.home.api.presentation.mappers.mapToString
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
    ) : TimeTaskAlarmManager {

        private val alarmManager: AlarmManager
            get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        override fun addOrUpdateNotifyAlarm(timeTask: TimeTask) {
            val alarmIntent = createAlarmIntent(timeTask.category, timeTask.subCategory)
            val pendingAlarmIntent = createPendingAlarmIntent(alarmIntent, timeTask.key.toInt())
            val triggerTime = timeTask.timeRange.from.time

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingAlarmIntent)
        }

        override fun deleteNotifyAlarm(timeTask: TimeTask) {
            val alarmIntent = createAlarmIntent(timeTask.category, timeTask.subCategory)
            val pendingAlarmIntent = createPendingAlarmIntent(alarmIntent, timeTask.key.toInt())

            alarmManager.cancel(pendingAlarmIntent)
            pendingAlarmIntent.cancel()
        }

        private fun createAlarmIntent(category: MainCategory, subCategory: SubCategory?): Intent {
            val language = fetchCoreLanguage(context.fetchCurrentLanguage())
            val categoryName = category.let { it.default?.mapToString(fetchCoreStrings(language)) ?: it.customName }
            val subCategoryName = subCategory?.name ?: ""
            val categoryIcon = category.default?.mapToIcon(fetchCoreIcons())
            val appIcon = fetchCoreIcons().calendar

            return receiverProvider.provideReceiverIntent(
                category = categoryName ?: App.NAME,
                subCategory = subCategoryName,
                icon = categoryIcon,
                appIcon = appIcon,
            )
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
