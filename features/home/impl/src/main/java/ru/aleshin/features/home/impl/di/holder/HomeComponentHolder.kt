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
package ru.aleshin.features.home.impl.di.holder

import ru.aleshin.features.home.api.di.HomeFeatureApi
import ru.aleshin.features.home.impl.di.HomeFeatureDependencies
import ru.aleshin.features.home.impl.di.component.HomeComponent
import ru.aleshin.module_injector.BaseComponentHolder

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
object HomeComponentHolder : BaseComponentHolder<HomeFeatureApi, HomeFeatureDependencies> {

    private var component: HomeComponent? = null

    override fun init(dependencies: HomeFeatureDependencies) {
        if (component == null) component = HomeComponent.create(dependencies)
    }

    override fun fetchApi(): HomeFeatureApi = fetchComponent()

    override fun clear() {
        component = null
    }

    internal fun fetchComponent() = checkNotNull(component) {
        "Home Component is not initialized"
    }
}
