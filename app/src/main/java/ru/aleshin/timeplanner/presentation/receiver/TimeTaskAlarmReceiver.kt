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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import ru.aleshin.core.ui.theme.tokens.fetchAppLanguage
import ru.aleshin.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.core.utils.extensions.fetchLocale
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.notifications.NotificationCreator
import ru.aleshin.core.utils.notifications.parameters.NotificationPriority
import ru.aleshin.timeplanner.R
import ru.aleshin.timeplanner.presentation.ui.main.MainActivity

/**
 * @author Stanislav Aleshin on 29.03.2023.
 */
class TimeTaskAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return
        val notificationCreator = NotificationCreator.Base(context)
        val coreStrings = fetchCoreStrings(fetchAppLanguage(context.fetchLocale().language))

        if (intent.action == Constants.Alarm.ALARM_NOTIFICATION_ACTION) {
            val category = checkNotNull(intent.getStringExtra(Constants.Alarm.NOTIFICATION_CATEGORY))
            val subCategory = checkNotNull(intent.getStringExtra(Constants.Alarm.NOTIFICATION_SUBCATEGORY))
            val icon = intent.getIntExtra(Constants.Alarm.NOTIFICATION_ICON, 0)
            val appIcon = checkNotNull(intent.getIntExtra(Constants.Alarm.APP_ICON, 0))

            val activityIntent = Intent(context, MainActivity::class.java)
            val contentIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE)
            val notification = notificationCreator.createNotify(
                channelId = Constants.Notification.CHANNEL_ID,
                title = if (subCategory.isNotEmpty()) "$category, $subCategory" else category,
                text = coreStrings.startTaskNotifyText,
                smallIcon = appIcon,
                largeIcon = if (icon != 0) ContextCompat.getDrawable(context, icon)?.toBitmap() else null,
                autoCancel = true,
                priority = NotificationPriority.MAX,
                contentIntent = contentIntent,
                color = ContextCompat.getColor(context, R.color.notification_icon),
            )
            notificationCreator.showNotify(notification, 0)
        }
    }
}
