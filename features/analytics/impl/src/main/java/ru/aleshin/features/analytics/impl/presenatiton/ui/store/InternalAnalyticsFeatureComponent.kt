/*
 * Copyright 2025 Stanislav Aleshin
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
package ru.aleshin.features.analytics.impl.presenatiton.ui.store

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import ru.aleshin.core.utils.architecture.component.FeatureComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.component.saveableStore
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.features.analytics.api.AnalyticsConfig
import ru.aleshin.features.analytics.api.AnalyticsOutput
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsState

/**
 * @author Stanislav Aleshin on 13.09.2025
 */
internal abstract class InternalAnalyticsFeatureComponent(
    startConfig: StartFeatureConfig<AnalyticsConfig>,
    componentContext: ComponentContext,
    outputConsumer: OutputConsumer<AnalyticsOutput>,
) : FeatureComponent<AnalyticsConfig, AnalyticsOutput>(
    startConfig = startConfig,
    componentContext = componentContext,
    outputConsumer = outputConsumer,
) {

    abstract val store: AnalyticsComposeStore

    class Default(
        startConfig: StartFeatureConfig<AnalyticsConfig>,
        componentContext: ComponentContext,
        outputConsumer: OutputConsumer<AnalyticsOutput>,
        private val analyticsStoreFactory: AnalyticsComposeStore.Factory,
    ) : InternalAnalyticsFeatureComponent(
        startConfig = startConfig,
        componentContext = componentContext,
        outputConsumer = outputConsumer,
    ) {

        override val store by saveableStore(
            storeFactory = analyticsStoreFactory,
            defaultState = AnalyticsState(),
            stateSerializer = AnalyticsState.serializer(),
            outputConsumer = outputConsumer,
            storeKey = COMPONENT_KEY,
        )

        private companion object Companion {
            const val COMPONENT_KEY = "ANALYTICS_STORE_KEY"
        }

        private val backCallback = BackCallback {
            navigateToBack()
        }

        init {
            backHandler.register(backCallback)
        }

        override fun navigateToBack() {
            outputConsumer.consume(AnalyticsOutput.NavigateToBack)
        }
    }
}
