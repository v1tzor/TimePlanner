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
package ru.aleshin.features.settings.impl.di.holder

import ru.aleshin.features.settings.api.di.SettingsFeatureApi
import ru.aleshin.features.settings.impl.di.SettingsFeatureDependencies
import ru.aleshin.features.settings.impl.di.component.SettingsComponent
import ru.aleshin.module_injector.BaseComponentHolder

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
object SettingsComponentHolder : BaseComponentHolder<SettingsFeatureApi, SettingsFeatureDependencies> {

    private var component: SettingsComponent? = null

    override fun init(dependencies: SettingsFeatureDependencies) {
        if (component == null) component = SettingsComponent.create(dependencies)
    }

    override fun fetchApi(): SettingsFeatureApi = fetchComponent()

    override fun clear() {
        component = null
    }

    internal fun fetchComponent() = checkNotNull(component) {
        "Settings Component is not initialized"
    }
}
