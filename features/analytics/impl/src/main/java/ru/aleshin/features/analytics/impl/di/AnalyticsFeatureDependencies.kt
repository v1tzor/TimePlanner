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
package ru.aleshin.features.analytics.impl.di

import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.api.domain.repository.CategoriesRepository
import ru.aleshin.features.home.api.domain.repository.ScheduleRepository
import ru.aleshin.features.settings.api.domain.repositories.TasksSettingsRepository
import ru.aleshin.module_injector.BaseFeatureDependencies

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
interface AnalyticsFeatureDependencies : BaseFeatureDependencies {
    val scheduleRepository: ScheduleRepository
    val tasksSettingsRepository: TasksSettingsRepository
    val categoriesRepository: CategoriesRepository
    val coroutineManager: CoroutineManager
    val dateManager: DateManager
}
