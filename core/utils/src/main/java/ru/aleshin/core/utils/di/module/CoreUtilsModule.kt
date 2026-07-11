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
package ru.aleshin.core.utils.di.module

import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.managers.WorkDispatchersProvider
import ru.aleshin.core.utils.notifications.NotificationCreator
import javax.inject.Singleton

/**
 * @author Stanislav Aleshin on 06.07.2026.
 */
@Module
interface CoreUtilsModule {

    @Binds
    fun bindDateManager(interactor: DateManager.Base): DateManager

    @Binds
    @Singleton
    fun bindCoroutineManager(manager: CoroutineManager.Base): CoroutineManager

    @Binds
    @Singleton
    fun bindWorkDispatchersProvider(manager: CoroutineManager.Base): WorkDispatchersProvider

    @Binds
    fun bindNotificationCreator(creator: NotificationCreator.Base): NotificationCreator
}
