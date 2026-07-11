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

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.aleshin.core.data.datasources.categories.MainCategoryDao
import ru.aleshin.core.data.datasources.categories.MainCategoryLocalDataSource
import ru.aleshin.core.data.datasources.schedules.ScheduleDao
import ru.aleshin.core.data.datasources.schedules.ScheduleLocalDataSource
import ru.aleshin.core.data.datasources.schedules.SchedulesDataBase
import ru.aleshin.core.data.datasources.settings.SettingsDataBase
import ru.aleshin.core.data.datasources.settings.TasksSettingsDao
import ru.aleshin.core.data.datasources.settings.TasksSettingsLocalDataSource
import ru.aleshin.core.data.datasources.settings.ThemeSettingsDao
import ru.aleshin.core.data.datasources.settings.ThemeSettingsLocalDataSource
import ru.aleshin.core.data.datasources.subcategories.SubCategoryDao
import ru.aleshin.core.data.datasources.subcategories.SubCategoryLocalDataSource
import ru.aleshin.core.data.datasources.tasks.TimeTaskDao
import ru.aleshin.core.data.datasources.tasks.TimeTaskLocalDataSource
import ru.aleshin.core.data.datasources.tasks.UndefinedTaskDao
import ru.aleshin.core.data.datasources.tasks.UndefinedTaskLocalDataSource
import ru.aleshin.core.data.datasources.templates.TemplateDao
import ru.aleshin.core.data.datasources.templates.TemplateLocalDataSource
import javax.inject.Singleton

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Module
class CoreDataBaseModule {

    // LocalDataSources

    @Provides
    @Singleton
    fun provideUndefinedTasksLocalDataSource(
        undefinedTasksDao: UndefinedTaskDao,
    ): UndefinedTaskLocalDataSource = UndefinedTaskLocalDataSource.Base(undefinedTasksDao)

    @Provides
    @Singleton
    fun provideTemplatesLocalDataSource(
        dao: TemplateDao,
    ): TemplateLocalDataSource = TemplateLocalDataSource.Base(dao)

    @Provides
    @Singleton
    fun provideThemeSettingsLocalDataSource(
        dao: ThemeSettingsDao,
    ): ThemeSettingsLocalDataSource = ThemeSettingsLocalDataSource.Base(dao)

    @Provides
    @Singleton
    fun provideTasksSettingsLocalDataSource(
        dao: TasksSettingsDao,
    ): TasksSettingsLocalDataSource = TasksSettingsLocalDataSource.Base(dao)

    @Provides
    @Singleton
    fun provideCategoriesLocalDataSource(
        dao: MainCategoryDao,
    ): MainCategoryLocalDataSource = MainCategoryLocalDataSource.Base(dao)

    @Provides
    @Singleton
    fun provideSubCategoriesLocalDataSource(
        dao: SubCategoryDao,
    ): SubCategoryLocalDataSource = SubCategoryLocalDataSource.Base(dao)

    @Provides
    @Singleton
    fun provideSchedulesLocalDataSource(
        schedulesDao: ScheduleDao,
    ): ScheduleLocalDataSource = ScheduleLocalDataSource.Base(schedulesDao)

    @Provides
    @Singleton
    fun provideTimeTaskLocalDataSource(
        timeTaskDao: TimeTaskDao,
    ): TimeTaskLocalDataSource = TimeTaskLocalDataSource.Base(timeTaskDao)

    // Dao

    @Provides
    @Singleton
    fun provideThemeSettingsDao(dataBase: SettingsDataBase): ThemeSettingsDao =
        dataBase.fetchThemeSettingsDao()

    @Provides
    @Singleton
    fun provideTasksSettingsDao(dataBase: SettingsDataBase): TasksSettingsDao =
        dataBase.fetchTasksSettingsDao()

    @Provides
    @Singleton
    fun provideTemplatesDao(dataBase: SchedulesDataBase): TemplateDao =
        dataBase.fetchTemplateDao()

    @Provides
    @Singleton
    fun provideMainCategoriesDao(dataBase: SchedulesDataBase): MainCategoryDao =
        dataBase.fetchMainCategoryDao()

    @Provides
    @Singleton
    fun provideSubCategoriesDao(dataBase: SchedulesDataBase): SubCategoryDao =
        dataBase.fetchSubCategoryDao()

    @Provides
    @Singleton
    fun provideScheduleDao(dataBase: SchedulesDataBase): ScheduleDao =
        dataBase.fetchScheduleDao()

    @Provides
    @Singleton
    fun provideTimeTaskDao(dataBase: SchedulesDataBase): TimeTaskDao =
        dataBase.fetchTimeTaskDao()

    @Provides
    @Singleton
    fun provideUndefinedTasksDao(dataBase: SchedulesDataBase): UndefinedTaskDao =
        dataBase.fetchUndefinedTaskDao()

    // DataBases

    @Provides
    @Singleton
    fun provideSettingsDataBase(context: Context): SettingsDataBase = SettingsDataBase.create(context)

    @Provides
    @Singleton
    fun provideSchedulesDataBase(context: Context): SchedulesDataBase = SchedulesDataBase.create(context)
}
