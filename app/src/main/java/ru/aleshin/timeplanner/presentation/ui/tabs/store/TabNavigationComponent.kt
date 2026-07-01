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
package ru.aleshin.timeplanner.presentation.ui.tabs.store

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.component.ChildComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.features.analytics.api.AnalyticsConfig
import ru.aleshin.features.analytics.api.AnalyticsDecomposeFeatureFactory
import ru.aleshin.features.analytics.api.AnalyticsOutput
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.home.api.HomeConfig
import ru.aleshin.features.home.api.HomeDecomposeFeatureFactory
import ru.aleshin.features.home.api.HomeOutput
import ru.aleshin.features.settings.api.SettingsConfig
import ru.aleshin.features.settings.api.SettingsDecomposeFeatureFactory
import ru.aleshin.features.settings.api.SettingsOutput

/**
 * @author Stanislav Aleshin on 12.09.2025.
 */
abstract class TabNavigationComponent(
    componentContext: ComponentContext
) : ChildComponent(
    componentContext = componentContext
) {

    abstract val stack: Value<ChildStack<*, TabNavigationChild>>

    abstract fun clickHomeTab()
    abstract fun clickAnalyticsTab()
    abstract fun clickSettingsTab()

    sealed class TabNavigationChild {
        data class HomeChild(val contentProvider: FeatureContentProvider) : TabNavigationChild()
        data class AnalyticsChild(val contentProvider: FeatureContentProvider) : TabNavigationChild()
        data class SettingsChild(val contentProvider: FeatureContentProvider) : TabNavigationChild()
    }

    @Serializable
    sealed class TabNavigationConfig {

        @Serializable
        data object Home : TabNavigationConfig()

        @Serializable
        data object Analytics : TabNavigationConfig()

        @Serializable
        data object Settings : TabNavigationConfig()
    }

    sealed class TabNavigationOutput : BaseOutput {
        data class NavigateToEditor(val config: EditorConfig) : TabNavigationOutput()
        data object NavigateToBack : TabNavigationOutput()
    }

    class Default(
        componentContext: ComponentContext,
        startConfig: StartFeatureConfig<TabNavigationConfig>,
        private val outputConsumer: OutputConsumer<TabNavigationOutput>,
        private val homeFeatureFactory: HomeDecomposeFeatureFactory,
        private val analyticsFeatureFactory: AnalyticsDecomposeFeatureFactory,
        private val settingsFeatureFactory: SettingsDecomposeFeatureFactory,
    ) : TabNavigationComponent(
        componentContext = componentContext,
    ) {

        private val stackNavigation = StackNavigation<TabNavigationConfig>()

        override val stack = childStack(
            source = stackNavigation,
            serializer = TabNavigationConfig.serializer(),
            initialStack = { startConfig.backstack ?: listOf(TabNavigationConfig.Home) },
            key = STACK_KEY,
            handleBackButton = false,
            childFactory = ::childFactory
        )

        private val backCallback = BackCallback {
            stackNavigation.pop { isPop ->
                if (!isPop) outputConsumer.consume(TabNavigationOutput.NavigateToBack)
            }
        }

        private companion object {
            const val STACK_KEY = "TABS_STACK"
        }

        init {
            backHandler.register(backCallback)
        }

        override fun clickHomeTab() {
            stackNavigation.bringToFront(TabNavigationConfig.Home)
        }

        override fun clickAnalyticsTab() {
            stackNavigation.bringToFront(TabNavigationConfig.Analytics)
        }

        override fun clickSettingsTab() {
            stackNavigation.bringToFront(TabNavigationConfig.Settings)
        }

        private fun childFactory(
            config: TabNavigationConfig,
            componentContext: ComponentContext
        ): TabNavigationChild {
            return when (config) {
                is TabNavigationConfig.Home -> {
                    val api = homeFeatureFactory.createOrGetFeature(componentContext)
                    val provider = api.contentProviderFactory().createProvider(
                        componentContext = componentContext,
                        startConfig = StartFeatureConfig<HomeConfig>(null),
                        outputConsumer = homeOutputConsumer()
                    )
                    TabNavigationChild.HomeChild(contentProvider = provider)
                }
                is TabNavigationConfig.Analytics -> {
                    val api = analyticsFeatureFactory.createOrGetFeature(componentContext)
                    val provider = api.contentProviderFactory().createProvider(
                        componentContext = componentContext,
                        startConfig = StartFeatureConfig<AnalyticsConfig>(null),
                        outputConsumer = analyticsOutputConsumer()
                    )
                    TabNavigationChild.AnalyticsChild(contentProvider = provider)
                }
                is TabNavigationConfig.Settings -> {
                    val api = settingsFeatureFactory.createOrGetFeature(componentContext)
                    val provider = api.contentProviderFactory().createProvider(
                        componentContext = componentContext,
                        startConfig = StartFeatureConfig<SettingsConfig>(null),
                        outputConsumer = settingsOutputConsumer()
                    )
                    TabNavigationChild.SettingsChild(contentProvider = provider)
                }
            }
        }

        private fun homeOutputConsumer() = OutputConsumer<HomeOutput> { output ->
            when (output) {
                is HomeOutput.NavigateToEditor -> {
                    val config = EditorConfig.Editor(
                        timeTask = output.timeTask,
                        template = output.template,
                        undefinedTaskId = output.undefinedTaskId,
                    )
                    outputConsumer.consume(TabNavigationOutput.NavigateToEditor(config))
                }
                is HomeOutput.NavigateToBack -> {
                    stackNavigation.pop()
                }
            }
        }

        private fun analyticsOutputConsumer() = OutputConsumer<AnalyticsOutput> { output ->
            when (output) {
                is AnalyticsOutput.NavigateToBack -> {
                    stackNavigation.pop()
                }
            }
        }

        private fun settingsOutputConsumer() = OutputConsumer<SettingsOutput> { output ->
            when (output) {
                is SettingsOutput.NavigateToBack -> {
                    stackNavigation.pop()
                }
            }
        }
    }
}
