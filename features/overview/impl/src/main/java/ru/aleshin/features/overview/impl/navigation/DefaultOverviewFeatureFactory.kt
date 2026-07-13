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
package ru.aleshin.features.overview.impl.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.aleshin.features.overview.api.OverviewDecomposeFeatureFactory
import ru.aleshin.features.overview.api.OverviewFeatureApi
import ru.aleshin.features.overview.impl.di.OverviewFeatureDependencies
import ru.aleshin.features.overview.impl.di.component.OverviewComponent
import ru.aleshin.features.overview.impl.di.holder.OverviewFeatureController

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
public class DefaultOverviewFeatureFactory(
    private val dependenciesFactory: () -> OverviewFeatureDependencies,
) : OverviewDecomposeFeatureFactory {

    override fun createOrGetFeature(context: ComponentContext): OverviewFeatureApi {
        return context.instanceKeeper.getOrCreate(key = OVERVIEW_FEATURE_CONTROLLER_KEY) {
            OverviewFeatureController(component = OverviewComponent.create(dependenciesFactory()))
        }.fetchApi()
    }

    private companion object Companion {
        const val OVERVIEW_FEATURE_CONTROLLER_KEY = "OVERIVEW_FEATURE_CONTROLLER_KEY"
    }
}
