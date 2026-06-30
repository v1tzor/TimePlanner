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
package ru.aleshin.timeplanner.di.component

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.aleshin.features.analytics.impl.di.AnalyticsFeatureDependencies
import ru.aleshin.features.editor.impl.di.EditorFeatureDependencies
import ru.aleshin.features.home.impl.di.HomeFeatureDependencies
import ru.aleshin.features.settings.impl.di.SettingsFeatureDependencies
import ru.aleshin.timeplanner.application.TimePlannerApp
import ru.aleshin.timeplanner.di.PlatformServicesModule
import ru.aleshin.timeplanner.di.modules.CoreModule
import ru.aleshin.timeplanner.di.modules.DataBaseModule
import ru.aleshin.timeplanner.di.modules.DataModule
import ru.aleshin.timeplanner.di.modules.DependenciesModule
import ru.aleshin.timeplanner.di.modules.DomainModules
import ru.aleshin.timeplanner.di.modules.FeatureModule
import ru.aleshin.timeplanner.di.modules.PresentationModule
import ru.aleshin.timeplanner.domain.interactors.SettingsInteractor
import ru.aleshin.timeplanner.domain.interactors.TimeTaskInteractor
import ru.aleshin.timeplanner.presentation.ui.main.MainActivity
import javax.inject.Singleton

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Singleton
@Component(
    modules = [
        DataBaseModule::class,
        DataModule::class,
        CoreModule::class,
        PlatformServicesModule::class,
        PresentationModule::class,
        DomainModules::class,
        DependenciesModule::class,
        FeatureModule::class,
    ],
)
interface AppComponent :
    HomeFeatureDependencies,
    SettingsFeatureDependencies,
    EditorFeatureDependencies,
    AnalyticsFeatureDependencies {

    fun fetchTimeTaskInteractor(): TimeTaskInteractor
    fun fetchSettingsInteractor(): SettingsInteractor
    fun inject(activity: MainActivity)
    fun inject(application: TimePlannerApp)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: Context): Builder
        fun featureModule(module: FeatureModule): Builder
        fun platformServicesModule(module: PlatformServicesModule): Builder
        fun dataBaseModule(module: DataBaseModule): Builder
        fun build(): AppComponent
    }

    companion object {
        fun create(context: Context): AppComponent {
            return DaggerAppComponent.builder()
                .applicationContext(context)
                .platformServicesModule(PlatformServicesModule())
                .featureModule(FeatureModule())
                .dataBaseModule(DataBaseModule())
                .build()
        }
    }
}
