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

import ru.aleshin.core.utils.inject.BaseFeatureManager
import ru.aleshin.features.settings.api.SettingsFeatureApi
import ru.aleshin.features.settings.impl.di.SettingsFeatureDependencies
import ru.aleshin.features.settings.impl.di.component.SettingsComponent

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
public object SettingsFeatureManager : BaseFeatureManager<SettingsFeatureApi, SettingsFeatureDependencies> {

    private var component: SettingsComponent? = null

    override fun createOrGetFeature(dependencies: SettingsFeatureDependencies): SettingsFeatureApi {
        return component ?: SettingsComponent.create(dependencies).apply {
            component = this
        }
    }

    override fun finish() {
        component = null
    }

    internal fun fetchComponent() = checkNotNull(component) {
        "Settings Component is not initialized"
    }
}
