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
package ru.aleshin.features.templates.impl.presentation.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import ru.aleshin.core.utils.architecture.component.FeatureComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.features.templates.api.TemplatesConfig
import ru.aleshin.features.templates.api.TemplatesOutput
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesComponent
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesComposeStore

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal abstract class InternalTemplatesFeatureComponent(
    componentContext: ComponentContext,
) : FeatureComponent<TemplatesConfig, TemplatesOutput>(
    componentContext = componentContext
) {

    abstract val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class TemplatesChild(val component: TemplatesComponent) : Child()
    }

    class Default(
        startConfig: TemplatesConfig,
        componentContext: ComponentContext,
        private val outputConsumer: OutputConsumer<TemplatesOutput>,
        private val templatesStoreFactory: TemplatesComposeStore.Factory,
    ) : InternalTemplatesFeatureComponent(
        componentContext = componentContext
    ) {

        private val stackNavigation = StackNavigation<TemplatesConfig>()

        override val stack: Value<ChildStack<*, Child>> = childStack(
            source = stackNavigation,
            serializer = TemplatesConfig.serializer(),
            initialConfiguration = startConfig,
            key = STACK_KEY,
            handleBackButton = true,
            childFactory = ::createChild,
        )

        private companion object Companion {
            const val STACK_KEY = "HOME_ROOT_STACK"
        }

        override fun navigateToBack() {
            stackNavigation.pop { isPop ->
                if (!isPop) outputConsumer.consume(TemplatesOutput.NavigateToBack)
            }
        }

        private fun createChild(
            config: TemplatesConfig,
            componentContext: ComponentContext
        ): Child {
            return when (config) {
                is TemplatesConfig.Templates -> Child.TemplatesChild(
                    component = TemplatesComponent.Default(
                        storeFactory = templatesStoreFactory,
                        componentContext = componentContext,
                    )
                )
            }
        }
    }
}
