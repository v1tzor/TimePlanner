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
package ru.aleshin.features.settings.impl.navigation

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.features.settings.api.SettingsFeatureComponent
import ru.aleshin.features.settings.api.SettingsFeatureComponent.SettingsOutput
import ru.aleshin.features.settings.api.SettingsFeatureComponentFactory
import ru.aleshin.features.settings.impl.presentation.ui.donate.store.DonateComposeStore
import ru.aleshin.features.settings.impl.presentation.ui.root.InternalSettingsFeatureComponent
import ru.aleshin.features.settings.impl.presentation.ui.settings.screensmodel.SettingsComposeStore
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal class DefaultSettingsComponentFactory @Inject constructor(
    private val settingsStoreFactory: SettingsComposeStore.Factory,
    private val donateStoreFactory: DonateComposeStore.Factory,
) : SettingsFeatureComponentFactory {

    override fun createComponent(
        componentContext: ComponentContext,
        startConfig: StartFeatureConfig<SettingsFeatureComponent.SettingsConfig>,
        outputConsumer: OutputConsumer<SettingsOutput>
    ): SettingsFeatureComponent {
        return InternalSettingsFeatureComponent.Default(
            componentContext = componentContext,
            startConfig = startConfig,
            outputConsumer = outputConsumer,
            settingsStoreFactory = settingsStoreFactory,
            donateStoreFactory = donateStoreFactory,
        )
    }
}