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
package ru.aleshin.features.analytics.impl.presentation.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import ru.aleshin.core.utils.architecture.component.FeatureComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.features.analytics.api.AnalyticsConfig
import ru.aleshin.features.analytics.api.AnalyticsOutput
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.store.AnalyticsComponent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.store.AnalyticsComposeStore

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal abstract class InternalAnalyticsFeatureComponent(
    componentContext: ComponentContext,
) : FeatureComponent<AnalyticsConfig, AnalyticsOutput>(
    componentContext = componentContext
) {

    abstract val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class AnalyticsChild(val component: AnalyticsComponent) : Child()
    }

    class Default(
        startConfig: AnalyticsConfig,
        componentContext: ComponentContext,
        private val outputConsumer: OutputConsumer<AnalyticsOutput>,
        private val analyticsStoreFactory: AnalyticsComposeStore.Factory,
    ) : InternalAnalyticsFeatureComponent(
        componentContext = componentContext
    ) {

        private val stackNavigation = StackNavigation<AnalyticsConfig>()

        override val stack: Value<ChildStack<*, Child>> = childStack(
            source = stackNavigation,
            serializer = AnalyticsConfig.serializer(),
            initialConfiguration = startConfig,
            key = STACK_KEY,
            handleBackButton = true,
            childFactory = ::createChild,
        )

        private companion object Companion {
            const val STACK_KEY = "ANALYTICS_ROOT_STACK"
        }

        override fun navigateToBack() {
            stackNavigation.pop { isPop ->
                if (!isPop) outputConsumer.consume(AnalyticsOutput.NavigateToBack)
            }
        }

        private fun createChild(
            config: AnalyticsConfig,
            componentContext: ComponentContext
        ): Child {
            return when (config) {
                is AnalyticsConfig.Analytics -> Child.AnalyticsChild(
                    component = AnalyticsComponent.Default(
                        storeFactory = analyticsStoreFactory,
                        componentContext = componentContext,
                    )
                )
            }
        }
    }
}
