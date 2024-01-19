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
import ru.aleshin.core.data.mappers.schedules.ScheduleDataToDomainMapper
import ru.aleshin.core.data.repository.CategoriesRepositoryImpl
import ru.aleshin.core.data.repository.ScheduleRepositoryImpl
import ru.aleshin.core.data.repository.SubCategoriesRepositoryImpl
import ru.aleshin.core.data.repository.TasksSettingsRepositoryImpl
import ru.aleshin.core.data.repository.TemplatesRepositoryImpl
import ru.aleshin.core.data.repository.ThemeSettingsRepositoryImpl
import ru.aleshin.core.data.repository.TimeTaskRepositoryImpl
import ru.aleshin.core.data.repository.UndefinedTasksRepositoryImpl
import ru.aleshin.core.domain.repository.CategoriesRepository
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.SubCategoriesRepository
import ru.aleshin.core.domain.repository.TasksSettingsRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.ThemeSettingsRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.domain.repository.UndefinedTasksRepository
import javax.inject.Singleton

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
@Module
interface DataModule {

    // Repositories

    @Singleton
    @Binds
    fun bindTemplatesRepository(repository: TemplatesRepositoryImpl): TemplatesRepository

    @Singleton
    @Binds
    fun bindUndefinedTasksRepository(repository: UndefinedTasksRepositoryImpl): UndefinedTasksRepository

    @Binds
    @Singleton
    fun bindTimeTaskRepository(repository: TimeTaskRepositoryImpl): TimeTaskRepository

    @Binds
    @Singleton
    fun bindThemeSettingsRepository(repository: ThemeSettingsRepositoryImpl): ThemeSettingsRepository

    @Binds
    @Singleton
    fun bindTasksSettingsRepository(repository: TasksSettingsRepositoryImpl): TasksSettingsRepository

    @Binds
    @Singleton
    fun bindScheduleRepository(repository: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    @Singleton
    fun bindSubCategoriesRepository(repository: SubCategoriesRepositoryImpl): SubCategoriesRepository

    @Binds
    @Singleton
    fun bindCategoriesRepository(repository: CategoriesRepositoryImpl): CategoriesRepository

    // Mappers

    @Binds
    fun bindScheduleDataToDomainMapper(mapper: ScheduleDataToDomainMapper.Base): ScheduleDataToDomainMapper
}
