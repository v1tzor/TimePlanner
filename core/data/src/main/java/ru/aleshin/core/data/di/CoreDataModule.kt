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
package ru.aleshin.core.data.di

import dagger.Binds
import dagger.Module
import ru.aleshin.core.data.repository.MainCategoryRepositoryImpl
import ru.aleshin.core.data.repository.ScheduleRepositoryImpl
import ru.aleshin.core.data.repository.SubCategoriesRepositoryImpl
import ru.aleshin.core.data.repository.TasksSettingsRepositoryImpl
import ru.aleshin.core.data.repository.TemplatesRepositoryImpl
import ru.aleshin.core.data.repository.ThemeSettingsRepositoryImpl
import ru.aleshin.core.data.repository.TimeTaskRepositoryImpl
import ru.aleshin.core.data.repository.UndefinedTaskRepositoryImpl
import ru.aleshin.core.domain.repository.MainCategoryRepository
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.SubCategoryRepository
import ru.aleshin.core.domain.repository.TasksSettingsRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.ThemeSettingsRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.domain.repository.UndefinedTaskRepository
import javax.inject.Singleton

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
@Module
interface CoreDataModule {

    @Singleton
    @Binds
    fun bindTemplatesRepository(repository: TemplatesRepositoryImpl): TemplatesRepository

    @Singleton
    @Binds
    fun bindUndefinedTasksRepository(repository: UndefinedTaskRepositoryImpl): UndefinedTaskRepository

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
    fun bindSubCategoriesRepository(repository: SubCategoriesRepositoryImpl): SubCategoryRepository

    @Binds
    @Singleton
    fun bindCategoriesRepository(repository: MainCategoryRepositoryImpl): MainCategoryRepository
}
