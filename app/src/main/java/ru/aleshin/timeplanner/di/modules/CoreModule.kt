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
package ru.aleshin.timeplanner.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.common.TimeTaskStatusChecker
import ru.aleshin.core.ui.notifications.AlarmKeyFactory
import ru.aleshin.core.ui.notifications.AlarmReceiverProvider
import ru.aleshin.core.ui.notifications.OngoingTimeTaskNotificationManager
import ru.aleshin.core.ui.notifications.TemplatesAlarmManager
import ru.aleshin.core.ui.notifications.TimeTaskAlarmManager
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.managers.TimeOverlayManager
import ru.aleshin.core.utils.notifications.NotificationCreator
import ru.aleshin.timeplanner.presentation.notifications.NotificationAlarmHandler
import ru.aleshin.timeplanner.presentation.notifications.NotificationContentMapper
import ru.aleshin.timeplanner.presentation.notifications.OngoingTimeTaskNotificationManagerImpl
import ru.aleshin.timeplanner.presentation.receiver.AlarmReceiverProviderImpl
import javax.inject.Singleton

/**
 * @author Stanislav Aleshin on 29.03.2023.
 */
@Module
interface CoreModule {

    @Binds
    fun bindAlarmReceiverProvider(provider: AlarmReceiverProviderImpl): AlarmReceiverProvider

    @Binds
    fun bindAlarmKeyFactory(factory: AlarmKeyFactory.Base): AlarmKeyFactory

    @Binds
    fun bindNotificationCreator(creator: NotificationCreator.Base): NotificationCreator

    @Binds
    fun bindNotificationContentMapper(mapper: NotificationContentMapper.Base): NotificationContentMapper

    @Binds
    fun bindOngoingTimeTaskNotificationManager(
        manager: OngoingTimeTaskNotificationManagerImpl,
    ): OngoingTimeTaskNotificationManager

    @Binds
    @Singleton
    fun bindCoroutineManager(manager: CoroutineManager.Base): CoroutineManager

    @Binds
    fun bindTimeTaskAlarmManager(manager: TimeTaskAlarmManager.Base): TimeTaskAlarmManager

    @Binds
    fun bindTemplatesAlarmManager(manager: TemplatesAlarmManager.Base): TemplatesAlarmManager

    @Binds
    @Singleton
    fun bindNotificationAlarmHandler(handler: NotificationAlarmHandler.Base): NotificationAlarmHandler

    @Binds
    @Singleton
    fun bindTimeOverlayManager(manager: TimeOverlayManager.Base): TimeOverlayManager

    @Binds
    fun bindTimeTaskStatusChecker(checker: TimeTaskStatusChecker.Base): TimeTaskStatusChecker

    @Binds
    fun bindScheduleStatusChecker(checker: ScheduleStatusChecker.Base): ScheduleStatusChecker

    @Binds
    fun bindDateManager(interactor: DateManager.Base): DateManager
}
