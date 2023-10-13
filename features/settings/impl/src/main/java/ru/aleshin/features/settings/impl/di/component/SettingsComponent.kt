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
package ru.aleshin.features.settings.impl.di.component

import dagger.Component
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.settings.api.di.SettingsFeatureApi
import ru.aleshin.features.settings.impl.di.SettingsFeatureDependencies
import ru.aleshin.features.settings.impl.di.modules.DomainModule
import ru.aleshin.features.settings.impl.di.modules.PresentationModule
import ru.aleshin.features.settings.impl.presentation.ui.donate.screenmodel.DonateScreenModel
import ru.aleshin.features.settings.impl.presentation.ui.settings.screensmodel.SettingsScreenModel

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
@FeatureScope
@Component(
    modules = [DomainModule::class, PresentationModule::class],
    dependencies = [SettingsFeatureDependencies::class],
)
internal interface SettingsComponent : SettingsFeatureApi {

    fun fetchSettingsScreenModel(): SettingsScreenModel
    fun fetchDonateScreenModel(): DonateScreenModel

    @Component.Builder
    interface Builder {
        fun dependencies(deps: SettingsFeatureDependencies): Builder
        fun build(): SettingsComponent
    }

    companion object {
        fun create(dependencies: SettingsFeatureDependencies): SettingsComponent {
            return DaggerSettingsComponent.builder()
                .dependencies(dependencies)
                .build()
        }
    }
}
