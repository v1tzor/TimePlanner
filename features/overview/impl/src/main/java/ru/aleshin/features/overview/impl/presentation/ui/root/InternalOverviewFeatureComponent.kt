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
package ru.aleshin.features.overview.impl.presentation.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import ru.aleshin.core.utils.architecture.component.FeatureComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.features.overview.api.OverviewConfig
import ru.aleshin.features.overview.api.OverviewOutput
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewInput
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewComponent
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewComposeStore
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewOutput as OverviewScreenOutput

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal abstract class InternalOverviewFeatureComponent(
    componentContext: ComponentContext,
) : FeatureComponent<OverviewConfig, OverviewOutput>(
    componentContext = componentContext
) {

    abstract val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class OverviewChild(val component: OverviewComponent) : Child()
    }

    class Default(
        startConfig: OverviewConfig,
        componentContext: ComponentContext,
        private val outputConsumer: OutputConsumer<OverviewOutput>,
        private val overviewStoreFactory: OverviewComposeStore.Factory,
    ) : InternalOverviewFeatureComponent(
        componentContext = componentContext
    ) {

        private val stackNavigation = StackNavigation<OverviewConfig>()

        override val stack: Value<ChildStack<*, Child>> = childStack(
            source = stackNavigation,
            serializer = OverviewConfig.serializer(),
            initialConfiguration = startConfig,
            key = STACK_KEY,
            handleBackButton = true,
            childFactory = ::createChild,
        )

        private companion object Companion {
            const val STACK_KEY = "OVERVIEW_ROOT_STACK"
        }

        override fun navigateToBack() {
            stackNavigation.pop { isPop ->
                if (!isPop) outputConsumer.consume(OverviewOutput.NavigateToBack)
            }
        }

        private fun createChild(
            config: OverviewConfig,
            componentContext: ComponentContext
        ): Child {
            return when (config) {
                is OverviewConfig.Overview -> Child.OverviewChild(
                    component = OverviewComponent.Default(
                        storeFactory = overviewStoreFactory,
                        componentContext = componentContext,
                        inputData = OverviewInput(
                            sharedText = config.sharedText
                        ),
                        outputConsumer = overviewOutputConsumer(),
                    )
                )
            }
        }

        private fun overviewOutputConsumer() = OutputConsumer<OverviewScreenOutput> { output ->
            when (output) {
                is OverviewScreenOutput.NavigateToHome -> {
                    outputConsumer.consume(OverviewOutput.NavigateToHome(output.config.scheduleDate))
                }
                is OverviewScreenOutput.NavigateToEditor -> {
                    val data = OverviewOutput.NavigateToTaskEditor(
                        timeTaskId = output.config.timeTaskId,
                        timeRange = output.config.timeRange,
                        date = output.config.date,
                        undefinedTaskId = output.config.undefinedTaskId,
                    )
                    outputConsumer.consume(data)
                }
            }
        }
    }
}
