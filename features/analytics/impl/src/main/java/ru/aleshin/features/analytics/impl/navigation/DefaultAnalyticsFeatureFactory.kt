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
package ru.aleshin.features.analytics.impl.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.aleshin.features.analytics.api.AnalyticsDecomposeFeatureFactory
import ru.aleshin.features.analytics.api.AnalyticsFeatureApi
import ru.aleshin.features.analytics.impl.di.AnalyticsFeatureDependencies
import ru.aleshin.features.analytics.impl.di.component.AnalyticsComponent
import ru.aleshin.features.analytics.impl.di.holder.AnalyticsFeatureController

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
public class DefaultAnalyticsFeatureFactory(
    private val dependenciesFactory: () -> AnalyticsFeatureDependencies,
) : AnalyticsDecomposeFeatureFactory {

    override fun createOrGetFeature(context: ComponentContext): AnalyticsFeatureApi {
        return context.instanceKeeper.getOrCreate(key = ANALYTICS_FEATURE_CONTROLLER_KEY) {
            AnalyticsFeatureController(
                component = AnalyticsComponent.create(dependenciesFactory())
            )
        }.fetchApi()
    }

    private companion object {
        const val ANALYTICS_FEATURE_CONTROLLER_KEY = "ANALYTICS_FEATURE_CONTROLLER_KEY"
    }
}
