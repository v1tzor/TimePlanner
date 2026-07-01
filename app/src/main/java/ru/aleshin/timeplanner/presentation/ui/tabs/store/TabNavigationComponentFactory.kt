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

package ru.aleshin.timeplanner.presentation.ui.tabs.store

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.features.analytics.api.AnalyticsDecomposeFeatureFactory
import ru.aleshin.features.home.api.HomeDecomposeFeatureFactory
import ru.aleshin.features.settings.api.SettingsDecomposeFeatureFactory
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponent.TabNavigationConfig
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponent.TabNavigationOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 26.08.2025.
 */
interface TabNavigationComponentFactory {

    fun createComponent(
        componentContext: ComponentContext,
        startConfig: StartFeatureConfig<TabNavigationConfig>,
        outputConsumer: OutputConsumer<TabNavigationOutput>
    ): TabNavigationComponent

    class Default @Inject constructor(
        private val homeFeatureFactory: HomeDecomposeFeatureFactory,
        private val analyticsFeatureFactory: AnalyticsDecomposeFeatureFactory,
        private val settingsFeatureFactory: SettingsDecomposeFeatureFactory,
    ) : TabNavigationComponentFactory {

        override fun createComponent(
            componentContext: ComponentContext,
            startConfig: StartFeatureConfig<TabNavigationConfig>,
            outputConsumer: OutputConsumer<TabNavigationOutput>
        ): TabNavigationComponent {
            return TabNavigationComponent.Default(
                componentContext = componentContext,
                startConfig = startConfig,
                outputConsumer = outputConsumer,
                homeFeatureFactory = homeFeatureFactory,
                analyticsFeatureFactory = analyticsFeatureFactory,
                settingsFeatureFactory = settingsFeatureFactory,
            )
        }
    }
}
