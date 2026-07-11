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
package ru.aleshin.timeplanner.application

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.aleshin.core.utils.extensions.fetchLocale
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.notifications.NotificationCreator
import ru.aleshin.core.utils.notifications.parameters.LockScreenVisibility
import ru.aleshin.core.utils.notifications.parameters.NotificationDefaults
import ru.aleshin.core.utils.notifications.parameters.NotificationImportance
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerStrings
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreLanguage
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreStrings
import ru.aleshin.timeplanner.di.component.AppComponent
import ru.aleshin.timeplanner.presentation.notifications.NotificationAlarmHandler
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
class TimePlannerApp : BaseFlavorApplication(), CoroutineScope {

    val appComponent by lazy {
        AppComponent.create(applicationContext)
    }

    @Inject
    lateinit var notificationCreator: NotificationCreator

    @Inject
    lateinit var notificationAlarmHandler: NotificationAlarmHandler

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO

    private val coreStrings: TimePlannerStrings
        get() = fetchCoreStrings(fetchCoreLanguage(fetchLocale().language))

    override fun initDI() {
        appComponent.inject(this)
    }

    override fun initSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deleteOldChannel()
            createTimeTaskNotifyChannel()
            createOngoingTimeTaskNotifyChannel()
        }
        rescheduleNotifications()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTimeTaskNotifyChannel() = notificationCreator.createNotifyChannel(
        channelId = Constants.Notification.CHANNEL_ID_NEW,
        channelName = coreStrings.timeTaskChannelName,
        importance = NotificationImportance.MAX,
        lockscreenVisibility = LockScreenVisibility.PUBLIC,
        defaults = NotificationDefaults(isSound = true, isVibrate = true, isLights = true),
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOngoingTimeTaskNotifyChannel() = notificationCreator.createNotifyChannel(
        channelId = Constants.Notification.CHANNEL_ID_ONGOING,
        channelName = coreStrings.ongoingTaskNotifyText,
        importance = NotificationImportance.LOW,
        lockscreenVisibility = LockScreenVisibility.PUBLIC,
        defaults = NotificationDefaults(),
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun deleteOldChannel() = notificationCreator.deleteNotifyChannel(
        channelId = Constants.Notification.CHANNEL_ID,
    )

    private fun rescheduleNotifications() = launch {
        try {
            notificationAlarmHandler.rescheduleAll()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}

fun Context.fetchApp(): TimePlannerApp {
    return applicationContext as TimePlannerApp
}

@Composable
fun fetchAppComponent(): AppComponent {
    return LocalContext.current.fetchApp().appComponent
}
