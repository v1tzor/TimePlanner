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
package ru.aleshin.features.home.impl.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.aleshin.features.home.api.HomeDecomposeFeatureFactory
import ru.aleshin.features.home.api.HomeFeatureApi
import ru.aleshin.features.home.impl.di.HomeFeatureDependencies
import ru.aleshin.features.home.impl.di.component.HomeComponent
import ru.aleshin.features.home.impl.di.holder.HomeFeatureController

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
public class DefaultHomeFeatureFactory(
    private val dependenciesFactory: () -> HomeFeatureDependencies,
) : HomeDecomposeFeatureFactory {

    override fun createOrGetFeature(context: ComponentContext): HomeFeatureApi {
        return context.instanceKeeper.getOrCreate(key = HOME_FEATURE_CONTROLLER_KEY) {
            HomeFeatureController(component = HomeComponent.create(dependenciesFactory()))
        }.fetchApi()
    }

    private companion object {
        const val HOME_FEATURE_CONTROLLER_KEY = "HOME_FEATURE_CONTROLLER_KEY"
    }
}
