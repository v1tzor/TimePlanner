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
package ru.aleshin.core.domain.di

import dagger.Binds
import dagger.Module
import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.common.TimeTaskProgressManager
import ru.aleshin.core.domain.common.TimeTaskStatusChecker
import javax.inject.Singleton

/**
 * @author Stanislav Aleshin on 06.07.2026.
 */
@Module
interface CoreDomainModule {

    @Binds
    @Singleton
    fun bindTimeOverlayManager(manager: TimeOverlayManager.Base): TimeOverlayManager

    @Binds
    fun bindTimeTaskStatusChecker(checker: TimeTaskStatusChecker.Base): TimeTaskStatusChecker

    @Binds
    fun bindTimeTaskProgressManager(manager: TimeTaskProgressManager.Base): TimeTaskProgressManager

    @Binds
    fun bindScheduleStatusChecker(checker: ScheduleStatusChecker.Base): ScheduleStatusChecker
}