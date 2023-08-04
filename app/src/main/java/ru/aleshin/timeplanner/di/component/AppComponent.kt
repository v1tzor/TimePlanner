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
import ru.aleshin.core.utils.navigation.navigator.NavigatorManager
import ru.aleshin.core.utils.notifications.NotificationCreator
import ru.aleshin.features.analytics.impl.di.AnalyticsFeatureDependencies
import ru.aleshin.features.editor.impl.di.EditorFeatureDependencies
import ru.aleshin.features.home.impl.di.HomeFeatureDependencies
import ru.aleshin.features.settings.impl.di.SettingsFeatureDependencies
import ru.aleshin.timeplanner.di.annotation.TabNavigation
import ru.aleshin.timeplanner.di.modules.*
import ru.aleshin.timeplanner.presentation.ui.main.MainActivity
import ru.aleshin.timeplanner.presentation.ui.tabs.screenmodel.TabScreenModel
import javax.inject.Singleton

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Singleton
@Component(
    modules = [
        DataBaseModule::class,
        DataModule::class,
        NavigationModule::class,
        CoreModule::class,
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

    @TabNavigation
    fun fetchTabNavigatorManager(): NavigatorManager
    fun fetchTabScreenModel(): TabScreenModel
    fun fetchNotificationCreator(): NotificationCreator
    fun inject(activity: MainActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(context: Context): Builder
        fun navigationModule(module: NavigationModule): Builder
        fun featureModule(module: FeatureModule): Builder
        fun dataBaseModule(module: DataBaseModule): Builder
        fun build(): AppComponent
    }

    companion object {
        fun create(context: Context): AppComponent {
            return DaggerAppComponent.builder()
                .applicationContext(context)
                .navigationModule(NavigationModule())
                .featureModule(FeatureModule())
                .dataBaseModule(DataBaseModule())
                .build()
        }
    }
}
