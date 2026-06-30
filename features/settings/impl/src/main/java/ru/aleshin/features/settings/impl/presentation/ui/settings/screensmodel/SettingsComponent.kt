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
package ru.aleshin.features.settings.impl.presentation.ui.settings.screensmodel

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.ChildComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.component.saveableStore
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsOutput
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsState

/**
 * @author Stanislav Aleshin on 13.09.2025
 */
internal abstract class SettingsComponent(
    componentContext: ComponentContext
) : ChildComponent(componentContext) {

    abstract val store: SettingsComposeStore

    class Default(
        storeFactory: SettingsComposeStore.Factory,
        componentContext: ComponentContext,
        outputConsumer: OutputConsumer<SettingsOutput>,
    ) : SettingsComponent(componentContext) {

        private companion object Companion {
            const val COMPONENT_KEY = "SETTINGS_STORE_KEY"
        }

        override val store by saveableStore(
            storeFactory = storeFactory,
            defaultState = SettingsState(),
            stateSerializer = SettingsState.serializer(),
            outputConsumer = outputConsumer,
            storeKey = COMPONENT_KEY,
        )
    }
}