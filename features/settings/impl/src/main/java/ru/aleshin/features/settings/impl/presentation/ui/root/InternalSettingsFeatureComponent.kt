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
package ru.aleshin.features.settings.impl.presentation.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import ru.aleshin.core.utils.architecture.component.FeatureComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.features.settings.api.SettingsConfig
import ru.aleshin.features.settings.api.SettingsOutput
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateOutput
import ru.aleshin.features.settings.impl.presentation.ui.donate.store.DonateComponent
import ru.aleshin.features.settings.impl.presentation.ui.donate.store.DonateComposeStore
import ru.aleshin.features.settings.impl.presentation.ui.settings.screensmodel.SettingsComponent
import ru.aleshin.features.settings.impl.presentation.ui.settings.screensmodel.SettingsComposeStore
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsOutput as SettingsScreenOutput

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal abstract class InternalSettingsFeatureComponent(
    componentContext: ComponentContext,
    startConfig: StartFeatureConfig<SettingsConfig>,
    outputConsumer: OutputConsumer<SettingsOutput>,
) : FeatureComponent<SettingsConfig, SettingsOutput>(
    componentContext = componentContext,
    startConfig = startConfig,
    outputConsumer = outputConsumer,
) {

    abstract val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class SettingsChild(val component: SettingsComponent) : Child()
        data class DonateChild(val component: DonateComponent) : Child()
    }

    class Default(
        startConfig: StartFeatureConfig<SettingsConfig>,
        componentContext: ComponentContext,
        outputConsumer: OutputConsumer<SettingsOutput>,
        private val settingsStoreFactory: SettingsComposeStore.Factory,
        private val donateStoreFactory: DonateComposeStore.Factory,
    ) : InternalSettingsFeatureComponent(
        componentContext = componentContext,
        startConfig = startConfig,
        outputConsumer = outputConsumer,
    ) {
        private val backCallback = BackCallback { navigateToBack() }

        private val stackNavigation = StackNavigation<SettingsConfig>()

        override val stack: Value<ChildStack<*, Child>> = childStack(
            source = stackNavigation,
            serializer = SettingsConfig.serializer(),
            initialStack = { startConfig.backstack ?: listOf(SettingsConfig.Settings) },
            key = STACK_KEY,
            handleBackButton = false,
            childFactory = ::createChild,
        )

        private companion object {
            const val STACK_KEY = "SETTINGS_ROOT_STACK"
        }

        init {
            backHandler.register(backCallback)
        }

        override fun navigateToBack() {
            stackNavigation.pop { isPop ->
                if (!isPop) outputConsumer.consume(SettingsOutput.NavigateToBack)
            }
        }

        private fun createChild(config: SettingsConfig, componentContext: ComponentContext): Child {
            return when (config) {
                is SettingsConfig.Settings -> Child.SettingsChild(
                    component = SettingsComponent.Default(
                        storeFactory = settingsStoreFactory,
                        componentContext = componentContext,
                        outputConsumer = settingsOutputConsumer()
                    )
                )
                is SettingsConfig.Donate -> Child.DonateChild(
                    component = DonateComponent.Default(
                        storeFactory = donateStoreFactory,
                        componentContext = componentContext,
                        outputConsumer = donateOutputConsumer()
                    )
                )
            }
        }

        private fun settingsOutputConsumer() = OutputConsumer<SettingsScreenOutput> { output ->
            when (output) {
                is SettingsScreenOutput.NavigateToDonate -> {
                    stackNavigation.pushToFront(SettingsConfig.Donate)
                }
                is SettingsScreenOutput.NavigateToBack -> navigateToBack()
            }
        }

        private fun donateOutputConsumer() = OutputConsumer<DonateOutput> { output ->
            when (output) {
                is DonateOutput.NavigateToBack -> navigateToBack()
            }
        }
    }
}
