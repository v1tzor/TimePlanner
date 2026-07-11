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
package ru.aleshin.features.home.impl.di.component

import dagger.Component
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.home.api.HomeFeatureApi
import ru.aleshin.features.home.impl.di.HomeFeatureDependencies
import ru.aleshin.features.home.impl.di.modules.DomainModule
import ru.aleshin.features.home.impl.di.modules.PresentationModule

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@FeatureScope
@Component(
    modules = [
        DomainModule::class,
        PresentationModule::class
    ],
    dependencies = [HomeFeatureDependencies::class],
)
internal interface HomeComponent : HomeFeatureApi {

    @Component.Builder
    interface Builder {
        fun dependencies(deps: HomeFeatureDependencies): Builder
        fun build(): HomeComponent
    }

    companion object {
        fun create(dependencies: HomeFeatureDependencies): HomeComponent {
            return DaggerHomeComponent.builder()
                .dependencies(dependencies)
                .build()
        }
    }
}
