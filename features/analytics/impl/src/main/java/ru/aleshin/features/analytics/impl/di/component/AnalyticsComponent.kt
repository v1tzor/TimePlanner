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
 * imitations under the License.
 */
package ru.aleshin.features.analytics.impl.di.component

import dagger.Component
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.analytics.api.di.AnalyticsFeatureApi
import ru.aleshin.features.analytics.impl.di.AnalyticsFeatureDependencies
import ru.aleshin.features.analytics.impl.di.modules.DomainModule
import ru.aleshin.features.analytics.impl.di.modules.PresentationModule
import ru.aleshin.features.analytics.impl.presenatiton.ui.screenmodel.AnalyticsScreenModel

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
@Component(
    modules = [DomainModule::class, PresentationModule::class],
    dependencies = [AnalyticsFeatureDependencies::class],
)
@FeatureScope
internal interface AnalyticsComponent : AnalyticsFeatureApi {

    fun fetchAnalyticsScreenModel(): AnalyticsScreenModel

    @Component.Builder
    interface Builder {
        fun dependencies(deps: AnalyticsFeatureDependencies): Builder
        fun build(): AnalyticsComponent
    }

    companion object {
        fun create(deps: AnalyticsFeatureDependencies): AnalyticsComponent {
            return DaggerAnalyticsComponent.builder()
                .dependencies(deps)
                .build()
        }
    }
}
