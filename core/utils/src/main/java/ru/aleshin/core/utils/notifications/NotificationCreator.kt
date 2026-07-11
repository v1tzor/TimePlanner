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
package ru.aleshin.core.utils.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.aleshin.core.utils.functional.Constants.App.LOGGER_TAG
import ru.aleshin.core.utils.notifications.parameters.LockScreenVisibility
import ru.aleshin.core.utils.notifications.parameters.NotificationChronometer
import ru.aleshin.core.utils.notifications.parameters.NotificationDefaults
import ru.aleshin.core.utils.notifications.parameters.NotificationImportance
import ru.aleshin.core.utils.notifications.parameters.NotificationPriority
import ru.aleshin.core.utils.notifications.parameters.NotificationProgress
import ru.aleshin.core.utils.notifications.parameters.NotificationStyles
import ru.aleshin.core.utils.notifications.parameters.NotificationVisibility
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.03.2023.
 */
interface NotificationCreator {

    fun createNotify(
        channelId: String,
        title: String,
        text: String,
        timeStamp: Long? = null,
        smallIcon: Int,
        largeIcon: Bitmap? = null,
        visibility: NotificationVisibility = NotificationVisibility.PUBLIC,
        priority: NotificationPriority = NotificationPriority.DEFAULT,
        actions: List<NotificationCompat.Action> = emptyList(),
        contentIntent: PendingIntent? = null,
        notificationDefaults: NotificationDefaults = NotificationDefaults(),
        autoCancel: Boolean = true,
        silent: Boolean = false,
        ongoing: Boolean = false,
        chronometer: NotificationChronometer? = null,
        timeoutAfterMillis: Long? = null,
        style: NotificationStyles? = null,
        color: Int? = null,
        progress: NotificationProgress? = null,
    ): Notification

    fun showNotify(notification: Notification, notifyId: Int)

    fun showNotify(notification: Notification, notifyTag: String?, notifyId: Int)

    fun cancelNotify(notifyId: Int)

    fun cancelNotify(notifyTag: String?, notifyId: Int)

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotifyChannel(
        channelId: String,
        channelName: String,
        importance: NotificationImportance,
        lockscreenVisibility: LockScreenVisibility?,
        defaults: NotificationDefaults,
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteNotifyChannel(channelId: String)

    class Base @Inject constructor(
        private val context: Context,
    ) : NotificationCreator {

        private val notificationManager: NotificationManager
            get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        private val notificationManagerCompat: NotificationManagerCompat
            get() = NotificationManagerCompat.from(context)

        override fun createNotify(
            channelId: String,
            title: String,
            text: String,
            timeStamp: Long?,
            smallIcon: Int,
            largeIcon: Bitmap?,
            visibility: NotificationVisibility,
            priority: NotificationPriority,
            actions: List<NotificationCompat.Action>,
            contentIntent: PendingIntent?,
            notificationDefaults: NotificationDefaults,
            autoCancel: Boolean,
            silent: Boolean,
            ongoing: Boolean,
            chronometer: NotificationChronometer?,
            timeoutAfterMillis: Long?,
            style: NotificationStyles?,
            color: Int?,
            progress: NotificationProgress?,
        ): Notification {
            return NotificationCompat.Builder(context, channelId).apply {
                setSmallIcon(smallIcon)
                setContentTitle(title)
                setContentText(text)
                setVisibility(visibility.visibility)
                setPriority(priority.importance)

                if (timeStamp == null) {
                    setShowWhen(false)
                } else {
                    setWhen(timeStamp)
                    setShowWhen(true)
                }

                color?.let(::setColor)
                largeIcon?.let(::setLargeIcon)
                contentIntent?.let(::setContentIntent)

                setOngoing(ongoing)
                setAutoCancel(autoCancel && !ongoing)

                if (silent) {
                    setSilent(true)
                    setOnlyAlertOnce(true)
                }

                if (!silent) {
                    val defaults = notificationDefaults.toCompatDefaults()
                    if (defaults != 0) {
                        setDefaults(defaults)
                    }
                }

                chronometer?.let {
                    applyChronometer(it)
                }

                timeoutAfterMillis?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        setTimeoutAfter(it.coerceAtLeast(0L))
                    }
                }

                progress?.let {
                    setProgress(it.max, it.value, it.isIndeterminate)
                }

                style?.let {
                    setStyle(it.style)
                }

                actions.forEach(::addAction)
            }.build()
        }

        @SuppressLint("MissingPermission")
        override fun showNotify(notification: Notification, notifyId: Int) {
            showNotify(notification, null, notifyId)
        }

        @SuppressLint("MissingPermission")
        override fun showNotify(notification: Notification, notifyTag: String?, notifyId: Int) {
            if (!notificationManagerCompat.areNotificationsEnabled()) {
                Log.e(LOGGER_TAG, "Notifications are disabled")
                return
            }

            try {
                notificationManagerCompat.notify(notifyTag, notifyId, notification)
            } catch (exception: SecurityException) {
                exception.printStackTrace()
            }
        }

        override fun cancelNotify(notifyId: Int) {
            cancelNotify(null, notifyId)
        }

        override fun cancelNotify(notifyTag: String?, notifyId: Int) {
            notificationManagerCompat.cancel(notifyTag, notifyId)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun createNotifyChannel(
            channelId: String,
            channelName: String,
            importance: NotificationImportance,
            lockscreenVisibility: LockScreenVisibility?,
            defaults: NotificationDefaults,
        ) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                importance.importance,
            ).apply {
                enableLights(defaults.isLights)
                enableVibration(defaults.isVibrate)

                if (!defaults.isSound) {
                    setSound(null, null)
                }
                if (lockscreenVisibility != null) {
                    this.lockscreenVisibility = lockscreenVisibility.visibility
                }
            }

            notificationManager.createNotificationChannel(channel)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun deleteNotifyChannel(channelId: String) {
            notificationManager.deleteNotificationChannel(channelId)
        }

        private fun NotificationCompat.Builder.applyChronometer(
            chronometer: NotificationChronometer,
        ) {
            setWhen(chronometer.whenMillis)
            setShowWhen(true)

            if (chronometer.countDown) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setUsesChronometer(true)
                    setChronometerCountDown(true)
                } else {
                    setUsesChronometer(false)
                }
            } else {
                setUsesChronometer(true)
            }
        }
    }
}
