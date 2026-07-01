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

import dagger.Module
import dagger.Provides
import ru.aleshin.features.analytics.api.AnalyticsDecomposeFeatureFactory
import ru.aleshin.features.analytics.impl.di.AnalyticsFeatureDependencies
import ru.aleshin.features.analytics.impl.navigation.DefaultAnalyticsFeatureFactory
import ru.aleshin.features.editor.api.EditorDecomposeFeatureFactory
import ru.aleshin.features.editor.impl.di.EditorFeatureDependencies
import ru.aleshin.features.editor.impl.navigation.DefaultEditorFeatureFactory
import ru.aleshin.features.home.api.HomeDecomposeFeatureFactory
import ru.aleshin.features.home.impl.di.HomeFeatureDependencies
import ru.aleshin.features.home.impl.navigation.DefaultHomeFeatureFactory
import ru.aleshin.features.settings.api.SettingsDecomposeFeatureFactory
import ru.aleshin.features.settings.impl.di.SettingsFeatureDependencies
import ru.aleshin.features.settings.impl.navigation.DefaultSettingsFeatureFactory
import javax.inject.Provider

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Module
class FeatureModule {

    @Provides
    fun provideHomeFeatureFactory(
        dependencies: Provider<HomeFeatureDependencies>,
    ): HomeDecomposeFeatureFactory {
        return DefaultHomeFeatureFactory(
            dependenciesFactory = { dependencies.get() }
        )
    }

    @Provides
    fun provideAnalyticsFeatureFactory(
        dependencies: Provider<AnalyticsFeatureDependencies>,
    ): AnalyticsDecomposeFeatureFactory {
        return DefaultAnalyticsFeatureFactory(
            dependenciesFactory = { dependencies.get() }
        )
    }

    @Provides
    fun provideEditorFeatureFactory(
        dependencies: Provider<EditorFeatureDependencies>,
    ): EditorDecomposeFeatureFactory {
        return DefaultEditorFeatureFactory(
            dependenciesFactory = { dependencies.get() }
        )
    }

    @Provides
    fun provideSettingsFeatureFactory(
        dependencies: Provider<SettingsFeatureDependencies>,
    ): SettingsDecomposeFeatureFactory {
        return DefaultSettingsFeatureFactory(
            dependenciesFactory = { dependencies.get() }
        )
    }
}
