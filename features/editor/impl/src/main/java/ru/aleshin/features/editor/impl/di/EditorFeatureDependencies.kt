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
package ru.aleshin.features.editor.impl.di

import ru.aleshin.core.data.datasources.schedules.SchedulesLocalDataSource
import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.repository.CategoriesRepository
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.SubCategoriesRepository
import ru.aleshin.core.domain.repository.TasksSettingsRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.domain.repository.UndefinedTasksRepository
import ru.aleshin.core.ui.notifications.TimeTaskAlarmManager
import ru.aleshin.core.utils.inject.BaseFeatureDependencies
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.managers.TimeOverlayManager

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
public interface EditorFeatureDependencies : BaseFeatureDependencies {
    public val categoriesRepository: CategoriesRepository
    public val timeTaskRepository: TimeTaskRepository
    public val scheduleRepository: ScheduleRepository
    public val templatesRepository: TemplatesRepository
    public val undefinedTasksRepository: UndefinedTasksRepository
    public val subCategoriesRepository: SubCategoriesRepository
    public val tasksSettingsRepository: TasksSettingsRepository
    public val schedulesLocalDataSource: SchedulesLocalDataSource
    public val scheduleStatusChecker: ScheduleStatusChecker
    public val coroutineManager: CoroutineManager
    public val timeOverlayManager: TimeOverlayManager
    public val timeTaskAlarmManager: TimeTaskAlarmManager
    public val dateManger: DateManager
}
