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
import ru.aleshin.features.analytics.api.AnalyticsFeatureStarter
import ru.aleshin.features.analytics.impl.di.AnalyticsFeatureDependencies
import ru.aleshin.features.analytics.impl.navigation.DefaultAnalyticsFeatureStarter
import ru.aleshin.features.editor.api.EditorFeatureStarter
import ru.aleshin.features.editor.impl.di.EditorFeatureDependencies
import ru.aleshin.features.editor.impl.navigation.DefaultEditorFeatureStarter
import ru.aleshin.features.home.api.HomeFeatureStarter
import ru.aleshin.features.home.impl.di.HomeFeatureDependencies
import ru.aleshin.features.home.impl.navigation.DefaultHomeFeatureStarter
import ru.aleshin.features.settings.api.SettingsFeatureStarter
import ru.aleshin.features.settings.impl.di.SettingsFeatureDependencies
import ru.aleshin.features.settings.impl.navigation.DefaultSettingsFeatureStarter
import javax.inject.Provider

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Module
class FeatureModule {

    @Provides
    fun provideHomeFeatureStarter(
        dependencies: Provider<HomeFeatureDependencies>,
    ): HomeFeatureStarter {
        return DefaultHomeFeatureStarter(
            dependenciesFactory = { dependencies.get() }
        )
    }

    @Provides
    fun provideAnalyticsFeatureStarter(
        dependencies: Provider<AnalyticsFeatureDependencies>,
    ): AnalyticsFeatureStarter {
        return DefaultAnalyticsFeatureStarter(
            dependenciesFactory = { dependencies.get() }
        )
    }

    @Provides
    fun provideEditorFeatureStarter(
        dependencies: Provider<EditorFeatureDependencies>,
    ): EditorFeatureStarter {
        return DefaultEditorFeatureStarter(
            dependenciesFactory = { dependencies.get() }
        )
    }

    @Provides
    fun provideSettingsFeatureStarter(
        dependencies: Provider<SettingsFeatureDependencies>,
    ): SettingsFeatureStarter {
        return DefaultSettingsFeatureStarter(
            dependenciesFactory = { dependencies.get() }
        )
    }
}
