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
package ru.aleshin.features.templates.impl.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.aleshin.features.templates.api.TemplatesDecomposeFeatureFactory
import ru.aleshin.features.templates.api.TemplatesFeatureApi
import ru.aleshin.features.templates.impl.di.TemplatesFeatureDependencies
import ru.aleshin.features.templates.impl.di.component.TemplatesComponent
import ru.aleshin.features.templates.impl.di.holder.TemplatesFeatureController

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
public class DefaultTemplatesFeatureFactory(
    private val dependenciesFactory: () -> TemplatesFeatureDependencies,
) : TemplatesDecomposeFeatureFactory {

    override fun createOrGetFeature(context: ComponentContext): TemplatesFeatureApi {
        return context.instanceKeeper.getOrCreate(key = TEMPLATES_FEATURE_CONTROLLER_KEY) {
            TemplatesFeatureController(component = TemplatesComponent.create(dependenciesFactory()))
        }.fetchApi()
    }

    private companion object Companion {
        const val TEMPLATES_FEATURE_CONTROLLER_KEY = "TEMPLATES_FEATURE_CONTROLLER_KEY"
    }
}
