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
package ru.aleshin.features.home.impl.di

import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.managers.TimeOverlayManager
import ru.aleshin.core.utils.navigation.Router
import ru.aleshin.features.editor.api.navigations.EditorFeatureStarter
import ru.aleshin.features.editor.api.presentation.TemplatesAlarmManager
import ru.aleshin.features.editor.api.presentation.TimeTaskAlarmManager
import ru.aleshin.features.home.api.domain.common.ScheduleStatusChecker
import ru.aleshin.features.home.api.domain.common.TimeTaskStatusChecker
import ru.aleshin.features.home.api.domain.repository.CategoriesRepository
import ru.aleshin.features.home.api.domain.repository.ScheduleRepository
import ru.aleshin.features.home.api.domain.repository.SubCategoriesRepository
import ru.aleshin.features.home.api.domain.repository.TemplatesRepository
import ru.aleshin.features.home.api.domain.repository.TimeTaskRepository
import ru.aleshin.features.home.api.domain.repository.UndefinedTasksRepository
import ru.aleshin.features.settings.api.domain.repositories.TasksSettingsRepository
import ru.aleshin.module_injector.BaseFeatureDependencies

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
interface HomeFeatureDependencies : BaseFeatureDependencies {
    val globalRouter: Router
    val editorFeatureStarter: EditorFeatureStarter
    val schedulesRepository: ScheduleRepository
    val timeTaskRepository: TimeTaskRepository
    val undefinedTasksRepository: UndefinedTasksRepository
    val templatesRepository: TemplatesRepository
    val categoriesRepository: CategoriesRepository
    val subCategoriesRepository: SubCategoriesRepository
    val tasksSettingsRepository: TasksSettingsRepository
    val coroutineManager: CoroutineManager
    val scheduleStatusChecker: ScheduleStatusChecker
    val timeOverlayManager: TimeOverlayManager
    val timeTaskAlarmManager: TimeTaskAlarmManager
    val templatesAlarmManager: TemplatesAlarmManager
    val taskStatusManager: TimeTaskStatusChecker
    val dateManger: DateManager
}
