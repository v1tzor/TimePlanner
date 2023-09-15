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
import ru.aleshin.features.home.api.data.mappers.schedules.*
import ru.aleshin.features.home.api.data.repository.CategoriesRepositoryImpl
import ru.aleshin.features.home.api.data.repository.ScheduleRepositoryImpl
import ru.aleshin.features.home.api.data.repository.SubCategoriesRepositoryImpl
import ru.aleshin.features.home.api.data.repository.TemplatesRepositoryImpl
import ru.aleshin.features.home.api.data.repository.TimeTaskRepositoryImpl
import ru.aleshin.features.home.api.domain.repository.CategoriesRepository
import ru.aleshin.features.home.api.domain.repository.ScheduleRepository
import ru.aleshin.features.home.api.domain.repository.SubCategoriesRepository
import ru.aleshin.features.home.api.domain.repository.TemplatesRepository
import ru.aleshin.features.home.api.domain.repository.TimeTaskRepository
import ru.aleshin.features.settings.api.data.repositories.TasksSettingsRepositoryImpl
import ru.aleshin.features.settings.api.data.repositories.ThemeSettingsRepositoryImpl
import ru.aleshin.features.settings.api.domain.repositories.TasksSettingsRepository
import ru.aleshin.features.settings.api.domain.repositories.ThemeSettingsRepository
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
