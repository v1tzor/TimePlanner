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
package ru.aleshin.features.home.impl.presentation.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import ru.aleshin.core.utils.architecture.component.FeatureComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.features.home.api.HomeConfig
import ru.aleshin.features.home.api.HomeOutput
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeInput
import ru.aleshin.features.home.impl.presentation.ui.home.store.HomeComponent
import ru.aleshin.features.home.impl.presentation.ui.home.store.HomeComposeStore
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeOutput as HomeScreenOutput

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal abstract class InternalHomeFeatureComponent(
    componentContext: ComponentContext,
) : FeatureComponent<HomeConfig, HomeOutput>(
    componentContext = componentContext
) {

    abstract val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class HomeChild(val component: HomeComponent) : Child()
    }

    class Default(
        startConfig: HomeConfig,
        componentContext: ComponentContext,
        private val outputConsumer: OutputConsumer<HomeOutput>,
        private val homeStoreFactory: HomeComposeStore.Factory,
    ) : InternalHomeFeatureComponent(
        componentContext = componentContext
    ) {

        private val stackNavigation = StackNavigation<HomeConfig>()

        override val stack: Value<ChildStack<*, Child>> = childStack(
            source = stackNavigation,
            serializer = HomeConfig.serializer(),
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
                if (!isPop) outputConsumer.consume(HomeOutput.NavigateToBack)
            }
        }

        private fun createChild(
            config: HomeConfig,
            componentContext: ComponentContext
        ): Child {
            return when (config) {
                is HomeConfig.Home -> Child.HomeChild(
                    component = HomeComponent.Default(
                        storeFactory = homeStoreFactory,
                        componentContext = componentContext,
                        inputData = HomeInput(scheduleDate = config.scheduleDate),
                        outputConsumer = homeOutputConsumer(),
                    )
                )
            }
        }

        private fun homeOutputConsumer() = OutputConsumer<HomeScreenOutput> { output ->
            when (output) {
                is HomeScreenOutput.NavigateToEditor -> {
                    val output = HomeOutput.NavigateToTaskEditor(
                        timeTaskId = output.config.timeTaskId,
                        timeRange = output.config.timeRange,
                        date = output.config.date,
                        undefinedTaskId = output.config.undefinedTaskId,
                    )
                    outputConsumer.consume(output)
                }
                is HomeScreenOutput.NavigateToSettings -> {
                    outputConsumer.consume(HomeOutput.NavigateToSettings)
                }
            }
        }
    }
}
