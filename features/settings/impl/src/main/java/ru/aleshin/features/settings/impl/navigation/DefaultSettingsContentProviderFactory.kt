/*
 * Copyright 2025 Stanislav Aleshin
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
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.features.settings.api.SettingsConfig
import ru.aleshin.features.settings.api.SettingsContentProviderFactory
import ru.aleshin.features.settings.api.SettingsOutput
import ru.aleshin.features.settings.impl.presentation.ui.donate.store.DonateComposeStore
import ru.aleshin.features.settings.impl.presentation.ui.root.InternalSettingsFeatureComponent
import ru.aleshin.features.settings.impl.presentation.ui.root.SettingsContentProvider
import ru.aleshin.features.settings.impl.presentation.ui.settings.screensmodel.SettingsComposeStore
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
internal class DefaultSettingsContentProviderFactory @Inject constructor(
    private val settingsStoreFactory: SettingsComposeStore.Factory,
    private val donateStoreFactory: DonateComposeStore.Factory,
) : SettingsContentProviderFactory {

    override fun createProvider(
        componentContext: ComponentContext,
        startConfig: StartFeatureConfig<SettingsConfig>,
        outputConsumer: OutputConsumer<SettingsOutput>
    ): FeatureContentProvider {
        val component = InternalSettingsFeatureComponent.Default(
            componentContext = componentContext,
            startConfig = startConfig,
            outputConsumer = outputConsumer,
            settingsStoreFactory = settingsStoreFactory,
            donateStoreFactory = donateStoreFactory,
        )

        return SettingsContentProvider(settingsComponent = component)
    }
}
