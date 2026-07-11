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
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import ru.aleshin.core.domain.entities.tasks.TaskNotificationType
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.presentation.mappers.mapToIcon
import ru.aleshin.core.presentation.notifications.AlarmKeyFactory
import ru.aleshin.core.presentation.notifications.OngoingTimeTaskNotificationManager
import ru.aleshin.core.utils.extensions.fetchCurrentLanguage
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.notifications.NotificationCreator
import ru.aleshin.core.utils.notifications.parameters.NotificationChronometer
import ru.aleshin.core.utils.notifications.parameters.NotificationDefaults
import ru.aleshin.core.utils.notifications.parameters.NotificationPriority
import ru.aleshin.core.utils.notifications.parameters.NotificationStyles
import ru.aleshin.timeplanner.R
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.timeplanner.presentation.ui.main.MainActivity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.07.2026.
 */
class OngoingTimeTaskNotificationManagerImpl @Inject constructor(
    private val context: Context,
    private val notificationCreator: NotificationCreator,
    private val notificationContentProvider: NotificationContentProvider,
    private val dateManager: DateManager,
    private val alarmKeyFactory: AlarmKeyFactory,
) : OngoingTimeTaskNotificationManager {

    private val coreIcons
        get() = fetchCoreIcons()

    private val coreStrings
        get() = fetchCoreStrings(fetchCoreLanguage(context.fetchCurrentLanguage()))

    override fun addOrUpdate(timeTask: TimeTask) {
        val currentDate = dateManager.fetchCurrentDate()
        val notificationId = alarmKeyFactory.fetchTimeTaskAlarmId(timeTask.key, TaskNotificationType.END_ONGOING)
        val notificationTag = alarmKeyFactory.fetchTimeTaskAlarmTag(timeTask.key, TaskNotificationType.END_ONGOING)

        if (!timeTask.isRunning(currentDate)) {
            return
        }

        val content = notificationContentProvider.fetchContent(
            timeTask = timeTask,
            notificationType = TaskNotificationType.END_ONGOING,
            strings = coreStrings,
        )

        val largeIcon = timeTask.category.default
            ?.mapToIcon(coreIcons)
            ?.let(::createLargeIcon)

        val notification = notificationCreator.createNotify(
            channelId = Constants.Notification.CHANNEL_ID_ONGOING,
            title = content.title,
            text = content.text,
            smallIcon = coreIcons.logo,
            largeIcon = largeIcon,
            autoCancel = false,
            silent = true,
            ongoing = true,
            priority = NotificationPriority.LOW,
            actions = emptyList(),
            contentIntent = createContentPendingIntent(),
            notificationDefaults = NotificationDefaults(),
            chronometer = NotificationChronometer(
                whenMillis = timeTask.timeRange.to.time,
                countDown = true,
            ),
            timeoutAfterMillis = timeTask.timeRange.to.time
                .minus(currentDate.time)
                .coerceAtLeast(0L),
            style = content.text
                .takeIf(String::isNotBlank)
                ?.let(NotificationStyles::BigTextStyle),
            color = ContextCompat.getColor(context, R.color.notification_icon),
        )

        notificationCreator.showNotify(notification, notificationTag, notificationId)
    }

    override fun delete(timeTask: TimeTask) {
        val notificationId = alarmKeyFactory.fetchTimeTaskAlarmId(timeTask.key, TaskNotificationType.END_ONGOING)
        val notificationTag = alarmKeyFactory.fetchTimeTaskAlarmTag(timeTask.key, TaskNotificationType.END_ONGOING)
        notificationCreator.cancelNotify(notificationTag, notificationId)
    }

    private fun createLargeIcon(iconId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, iconId) ?: return null
        val wrappedDrawable = DrawableCompat.wrap(drawable.mutate())

        DrawableCompat.setTint(wrappedDrawable, Color.DKGRAY)

        val size = context.resources.getDimensionPixelSize(R.dimen.notification_large_icon_size)
            .coerceAtLeast(1)

        return wrappedDrawable.toBitmap(
            width = size,
            height = size,
            config = Bitmap.Config.ARGB_8888,
        )
    }

    private fun createContentPendingIntent(): PendingIntent {
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        return PendingIntent.getActivity(
            context,
            CONTENT_REQUEST_CODE,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val CONTENT_REQUEST_CODE = 1001
    }
}
