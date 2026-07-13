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
package ru.aleshin.features.settings.impl.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.aleshin.features.settings.api.SettingsDecomposeFeatureFactory
import ru.aleshin.features.settings.api.SettingsFeatureApi
import ru.aleshin.features.settings.impl.di.SettingsFeatureDependencies
import ru.aleshin.features.settings.impl.di.component.SettingsComponent
import ru.aleshin.features.settings.impl.di.holder.SettingsFeatureController

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
public class DefaultSettingsFeatureFactory(
    private val dependenciesFactory: () -> SettingsFeatureDependencies,
) : SettingsDecomposeFeatureFactory {

    override fun createOrGetFeature(context: ComponentContext): SettingsFeatureApi {
        return context.instanceKeeper.getOrCreate(key = SETTINGS_FEATURE_CONTROLLER_KEY) {
            SettingsFeatureController(component = SettingsComponent.create(dependenciesFactory()))
        }.fetchApi()
    }

    private companion object {
        const val SETTINGS_FEATURE_CONTROLLER_KEY = "SETTINGS_FEATURE_CONTROLLER_KEY"
    }
}
