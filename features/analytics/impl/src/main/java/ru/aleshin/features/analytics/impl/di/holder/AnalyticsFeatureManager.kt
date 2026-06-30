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
package ru.aleshin.features.analytics.impl.di.holder

import ru.aleshin.core.utils.inject.BaseFeatureManager
import ru.aleshin.features.analytics.api.AnalyticsFeatureApi
import ru.aleshin.features.analytics.impl.di.AnalyticsFeatureDependencies
import ru.aleshin.features.analytics.impl.di.component.AnalyticsComponent

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
public object AnalyticsFeatureManager : BaseFeatureManager<AnalyticsFeatureApi, AnalyticsFeatureDependencies> {

    private var component: AnalyticsComponent? = null

    override fun createOrGetFeature(dependencies: AnalyticsFeatureDependencies): AnalyticsFeatureApi {
        return component ?: AnalyticsComponent.create(dependencies).apply {
            component = this
        }
    }

    override fun finish() {
        component = null
    }

    internal fun fetchComponent() = checkNotNull(component) {
        "Analytics Component is not initialized"
    }
}
