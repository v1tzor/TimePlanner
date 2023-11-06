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

import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.managers.TimeOverlayManager
import ru.aleshin.core.utils.navigation.Router
import ru.aleshin.core.utils.navigation.TabRouter
import ru.aleshin.features.editor.api.presentation.TimeTaskAlarmManager
import ru.aleshin.features.home.api.data.datasources.schedules.SchedulesLocalDataSource
import ru.aleshin.features.home.api.domain.common.ScheduleStatusChecker
import ru.aleshin.features.home.api.domain.repository.CategoriesRepository
import ru.aleshin.features.home.api.domain.repository.ScheduleRepository
import ru.aleshin.features.home.api.domain.repository.SubCategoriesRepository
import ru.aleshin.features.home.api.domain.repository.TemplatesRepository
import ru.aleshin.features.home.api.domain.repository.TimeTaskRepository
import ru.aleshin.features.home.api.domain.repository.UndefinedTasksRepository
import ru.aleshin.features.home.api.navigation.HomeFeatureStarter
import ru.aleshin.module_injector.BaseFeatureDependencies

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
interface EditorFeatureDependencies : BaseFeatureDependencies {
    val globalRouter: Router
    val tabRouter: TabRouter
    val homeFeatureStarter: HomeFeatureStarter
    val categoriesRepository: CategoriesRepository
    val timeTaskRepository: TimeTaskRepository
    val scheduleRepository: ScheduleRepository
    val templatesRepository: TemplatesRepository
    val undefinedTasksRepository: UndefinedTasksRepository
    val subCategoriesRepository: SubCategoriesRepository
    val schedulesLocalDataSource: SchedulesLocalDataSource
    val scheduleStatusChecker: ScheduleStatusChecker
    val coroutineManager: CoroutineManager
    val timeOverlayManager: TimeOverlayManager
    val timeTaskAlarmManager: TimeTaskAlarmManager
    val dateManger: DateManager
}
