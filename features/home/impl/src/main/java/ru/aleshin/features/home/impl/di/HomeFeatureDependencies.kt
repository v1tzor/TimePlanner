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
package ru.aleshin.features.home.impl.di

import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.common.TimeTaskProgressManager
import ru.aleshin.core.domain.common.TimeTaskStatusChecker
import ru.aleshin.core.domain.repository.MainCategoryRepository
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.SubCategoryRepository
import ru.aleshin.core.domain.repository.TasksSettingsRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.domain.repository.UndefinedTaskRepository
import ru.aleshin.core.presentation.notifications.TemplatesAlarmManager
import ru.aleshin.core.presentation.notifications.TimeTaskAlarmManager
import ru.aleshin.core.utils.inject.BaseFeatureDependencies
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
public interface HomeFeatureDependencies : BaseFeatureDependencies {
    public val schedulesRepository: ScheduleRepository
    public val timeTaskRepository: TimeTaskRepository
    public val undefinedTaskRepository: UndefinedTaskRepository
    public val templatesRepository: TemplatesRepository
    public val mainCategoryRepository: MainCategoryRepository
    public val subCategoryRepository: SubCategoryRepository
    public val tasksSettingsRepository: TasksSettingsRepository
    public val coroutineManager: CoroutineManager
    public val scheduleStatusChecker: ScheduleStatusChecker
    public val timeTaskProgressManager: TimeTaskProgressManager
    public val timeOverlayManager: TimeOverlayManager
    public val timeTaskAlarmManager: TimeTaskAlarmManager
    public val templatesAlarmManager: TemplatesAlarmManager
    public val taskStatusManager: TimeTaskStatusChecker
    public val dateManger: DateManager
}
