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
package ru.aleshin.timeplanner.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.core.presentation.notifications.AlarmReceiverProvider
import ru.aleshin.core.presentation.notifications.OngoingTimeTaskNotificationManager
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.timeplanner.presentation.notifications.NotificationAlarmHandler
import ru.aleshin.timeplanner.presentation.notifications.NotificationContentProvider
import ru.aleshin.timeplanner.presentation.notifications.OngoingTimeTaskNotificationManagerImpl
import ru.aleshin.timeplanner.presentation.receiver.AlarmReceiverProviderImpl
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainState
import ru.aleshin.timeplanner.presentation.ui.main.store.MainComponentFactory
import ru.aleshin.timeplanner.presentation.ui.main.store.MainComposeStore
import ru.aleshin.timeplanner.presentation.ui.main.store.NavigationWorkProcessor
import ru.aleshin.timeplanner.presentation.ui.main.store.SettingsWorkProcessor
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponentFactory
import javax.inject.Singleton

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Module
interface PresentationModule {

    @Binds
    fun bindMainComponentFactory(factory: MainComponentFactory.Default): MainComponentFactory

    @Binds
    fun bindMainStoreFactory(factory: MainComposeStore.Factory): BaseComposeStore.Factory<MainComposeStore, MainState>

    @Binds
    fun bindSettingsWorkProcessor(processor: SettingsWorkProcessor.Base): SettingsWorkProcessor

    @Binds
    fun bindNavigationWorkProcessor(processor: NavigationWorkProcessor.Base): NavigationWorkProcessor

    @Binds
    fun bindTabNavigationComponentFactory(factory: TabNavigationComponentFactory.Default): TabNavigationComponentFactory

    @Binds
    fun bindAlarmReceiverProvider(provider: AlarmReceiverProviderImpl): AlarmReceiverProvider

    @Binds
    fun bindNotificationContentProvider(mapper: NotificationContentProvider.Base): NotificationContentProvider

    @Binds
    fun bindOngoingTimeTaskNotificationManager(manager: OngoingTimeTaskNotificationManagerImpl): OngoingTimeTaskNotificationManager

    @Binds
    @Singleton
    fun bindNotificationAlarmHandler(handler: NotificationAlarmHandler.Base): NotificationAlarmHandler
}
