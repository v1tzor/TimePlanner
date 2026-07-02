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

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LightingColorFilter
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import ru.aleshin.core.domain.entities.schedules.TaskNotificationType
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.ui.mappers.mapToIcon
import ru.aleshin.core.ui.notifications.AlarmKeyFactory
import ru.aleshin.core.ui.notifications.OngoingTimeTaskNotificationManager
import ru.aleshin.core.ui.theme.tokens.fetchCoreIcons
import ru.aleshin.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.core.utils.extensions.fetchCurrentLanguage
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.notifications.NotificationCreator
import ru.aleshin.core.utils.notifications.parameters.NotificationDefaults
import ru.aleshin.core.utils.notifications.parameters.NotificationPriority
import ru.aleshin.core.utils.notifications.parameters.NotificationProgress
import ru.aleshin.core.utils.notifications.parameters.NotificationStyles
import ru.aleshin.timeplanner.R
import ru.aleshin.timeplanner.presentation.receiver.TimeTaskAlarmReceiver
import ru.aleshin.timeplanner.presentation.ui.main.MainActivity
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.07.2026.
 */
class OngoingTimeTaskNotificationManagerImpl @Inject constructor(
    private val context: Context,
    private val notificationCreator: NotificationCreator,
    private val notificationContentMapper: NotificationContentMapper,
    private val dateManager: DateManager,
    private val alarmKeyFactory: AlarmKeyFactory,
) : OngoingTimeTaskNotificationManager {

    private val coreIcons
        get() = fetchCoreIcons()

    private val coreStrings
        get() = fetchCoreStrings(fetchCoreLanguage(context.fetchCurrentLanguage()))

    override fun addOrUpdate(timeTask: TimeTask) {
        val currentDate = dateManager.fetchCurrentDate()
        if (!timeTask.isRunning(currentDate)) return

        val notificationId = alarmKeyFactory.fetchTimeTaskAlarmId(timeTask.key, TaskNotificationType.END_ONGOING)
        val content = notificationContentMapper.mapTimeTask(timeTask, TaskNotificationType.END_ONGOING, coreStrings)
        val icon = timeTask.category.default?.mapToIcon(coreIcons)
        val notification = notificationCreator.createNotify(
            channelId = Constants.Notification.CHANNEL_ID_ONGOING,
            title = content.title,
            text = content.text,
            smallIcon = coreIcons.logo,
            largeIcon = icon?.let { iconId -> createLargeIcon(iconId) },
            autoCancel = false,
            ongoing = true,
            priority = NotificationPriority.DEFAULT,
            actions = listOf(createMarkDoneAction(timeTask.key, notificationId)),
            contentIntent = createContentPendingIntent(),
            notificationDefaults = NotificationDefaults(),
            style = content.text.takeIf { it.isNotBlank() }?.let { NotificationStyles.BigTextStyle(it) },
            color = ContextCompat.getColor(context, R.color.notification_icon),
            progress = timeTask.fetchProgress(currentDate),
        ).apply {
            flags = flags or Notification.FLAG_NO_CLEAR or Notification.FLAG_ONLY_ALERT_ONCE
        }
        notificationCreator.showNotify(notification, notificationId)
    }

    override fun delete(timeTask: TimeTask) {
        val notificationId = alarmKeyFactory.fetchTimeTaskAlarmId(timeTask.key, TaskNotificationType.END_ONGOING)
        notificationCreator.cancelNotify(notificationId)
    }

    private fun createLargeIcon(iconId: Int) = ContextCompat.getDrawable(context, iconId)?.let { drawable ->
        drawable.colorFilter = LightingColorFilter(Color.DKGRAY, Color.DKGRAY)
        drawable.toBitmap()
    }

    private fun createContentPendingIntent(): PendingIntent {
        val activityIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createMarkDoneAction(timeTaskKey: Long, notificationId: Int): NotificationCompat.Action {
        val intent = Intent(context, TimeTaskAlarmReceiver::class.java).apply {
            action = Constants.Alarm.MARK_DONE_NOTIFICATION_ACTION
            putExtra(Constants.Alarm.TIME_TASK_ID, timeTaskKey)
            putExtra(Constants.Alarm.NOTIFICATION_ID, notificationId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Action.Builder(
            coreIcons.check,
            coreStrings.notificationMarkDoneTitle,
            pendingIntent,
        ).build()
    }

    private fun TimeTask.fetchProgress(currentDate: Date): NotificationProgress {
        val duration = (timeRange.to.time - timeRange.from.time).coerceAtLeast(1L)
        val elapsed = (currentDate.time - timeRange.from.time).coerceIn(0L, duration)
        return NotificationProgress(
            value = (elapsed.toDouble() / duration.toDouble() * PROGRESS_MAX).toInt(),
            max = PROGRESS_MAX,
            isIndeterminate = false,
        )
    }

    companion object {
        private const val PROGRESS_MAX = 100
    }
}
