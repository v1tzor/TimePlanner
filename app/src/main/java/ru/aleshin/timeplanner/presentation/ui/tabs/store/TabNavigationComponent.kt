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
package ru.aleshin.timeplanner.presentation.ui.tabs.store

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.component.ChildComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.features.analytics.api.AnalyticsConfig
import ru.aleshin.features.analytics.api.AnalyticsDecomposeFeatureFactory
import ru.aleshin.features.analytics.api.AnalyticsOutput
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.home.api.HomeConfig
import ru.aleshin.features.home.api.HomeDecomposeFeatureFactory
import ru.aleshin.features.home.api.HomeOutput
import ru.aleshin.features.overview.api.OverviewConfig
import ru.aleshin.features.overview.api.OverviewDecomposeFeatureFactory
import ru.aleshin.features.overview.api.OverviewOutput
import ru.aleshin.features.templates.api.TemplatesConfig
import ru.aleshin.features.templates.api.TemplatesDecomposeFeatureFactory
import ru.aleshin.features.templates.api.TemplatesOutput

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
    abstract fun clickOverviewTab()
    abstract fun clickTemplatesTab()
    abstract fun clickAnalyticsTab()

    sealed class TabNavigationChild {
        data class HomeChild(val contentProvider: FeatureContentProvider) : TabNavigationChild()
        data class OverviewChild(val contentProvider: FeatureContentProvider) : TabNavigationChild()
        data class TemplatesChild(val contentProvider: FeatureContentProvider) : TabNavigationChild()
        data class AnalyticsChild(val contentProvider: FeatureContentProvider) : TabNavigationChild()
    }

    @Serializable
    sealed class TabNavigationConfig {

        @Serializable
        data class Home(val startConfig: HomeConfig = HomeConfig.Home()) : TabNavigationConfig()

        @Serializable
        data class Overview(val startConfig: OverviewConfig = OverviewConfig.Overview()) : TabNavigationConfig()

        @Serializable
        data class Templates(val startConfig: TemplatesConfig = TemplatesConfig.Templates) : TabNavigationConfig()

        @Serializable
        data class Analytics(val startConfig: AnalyticsConfig = AnalyticsConfig.Analytics) : TabNavigationConfig()
    }

    sealed class TabNavigationOutput : BaseOutput {
        data class NavigateToEditor(val config: EditorConfig) : TabNavigationOutput()
        data object NavigateToSettings : TabNavigationOutput()
        data object NavigateToBack : TabNavigationOutput()
    }

    class Default(
        componentContext: ComponentContext,
        startConfig: TabNavigationConfig,
        private val outputConsumer: OutputConsumer<TabNavigationOutput>,
        private val homeFeatureFactory: HomeDecomposeFeatureFactory,
        private val overviewFeatureFactory: OverviewDecomposeFeatureFactory,
        private val templatesFeatureFactory: TemplatesDecomposeFeatureFactory,
        private val analyticsFeatureFactory: AnalyticsDecomposeFeatureFactory,
    ) : TabNavigationComponent(
        componentContext = componentContext,
    ) {

        private val stackNavigation = StackNavigation<TabNavigationConfig>()

        override val stack = childStack(
            source = stackNavigation,
            serializer = TabNavigationConfig.serializer(),
            initialConfiguration = startConfig,
            key = STACK_KEY,
            handleBackButton = true,
            childFactory = ::childFactory
        )

        private companion object {
            const val STACK_KEY = "TABS_STACK"
        }

        override fun clickHomeTab() {
            stackNavigation.bringToFront(TabNavigationConfig.Home())
        }

        override fun clickOverviewTab() {
            stackNavigation.bringToFront(TabNavigationConfig.Overview())
        }

        override fun clickTemplatesTab() {
            stackNavigation.bringToFront(TabNavigationConfig.Templates())
        }

        override fun clickAnalyticsTab() {
            stackNavigation.bringToFront(TabNavigationConfig.Analytics())
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
                        startConfig = HomeConfig.Home(),
                        outputConsumer = homeOutputConsumer()
                    )
                    TabNavigationChild.HomeChild(contentProvider = provider)
                }
                is TabNavigationConfig.Analytics -> {
                    val api = analyticsFeatureFactory.createOrGetFeature(componentContext)
                    val provider = api.contentProviderFactory().createProvider(
                        componentContext = componentContext,
                        startConfig = AnalyticsConfig.Analytics,
                        outputConsumer = analyticsOutputConsumer()
                    )
                    TabNavigationChild.AnalyticsChild(contentProvider = provider)
                }
                is TabNavigationConfig.Overview -> {
                    val api = overviewFeatureFactory.createOrGetFeature(componentContext)
                    val provider = api.contentProviderFactory().createProvider(
                        componentContext = componentContext,
                        startConfig = OverviewConfig.Overview(),
                        outputConsumer = overviewOutputConsumer()
                    )
                    TabNavigationChild.OverviewChild(contentProvider = provider)
                }
                is TabNavigationConfig.Templates -> {
                    val api = templatesFeatureFactory.createOrGetFeature(componentContext)
                    val provider = api.contentProviderFactory().createProvider(
                        componentContext = componentContext,
                        startConfig = TemplatesConfig.Templates,
                        outputConsumer = templatesOutputConsumer()
                    )
                    TabNavigationChild.TemplatesChild(contentProvider = provider)
                }
            }
        }

        private fun homeOutputConsumer() = OutputConsumer<HomeOutput> { output ->
            when (output) {
                is HomeOutput.NavigateToTaskEditor -> {
                    val config = EditorConfig.Task(
                        timeTaskId = output.timeTaskId,
                        timeRange = output.timeRange,
                        date = output.date,
                        undefinedTaskId = output.undefinedTaskId,
                    )
                    outputConsumer.consume(TabNavigationOutput.NavigateToEditor(config))
                }
                is HomeOutput.NavigateToBack -> {
                    stackNavigation.pop()
                }
                is HomeOutput.NavigateToSettings -> outputConsumer.consume(TabNavigationOutput.NavigateToSettings)
            }
        }

        private fun analyticsOutputConsumer() = OutputConsumer<AnalyticsOutput> { output ->
            when (output) {
                is AnalyticsOutput.NavigateToBack -> {
                    stackNavigation.pop()
                }
            }
        }

        private fun overviewOutputConsumer() = OutputConsumer<OverviewOutput> { output ->
            when (output) {
                is OverviewOutput.NavigateToBack -> {
                    stackNavigation.pop()
                }
                is OverviewOutput.NavigateToHome -> {
                    val config = HomeConfig.Home(output.scheduleDate)
                    stackNavigation.bringToFront(TabNavigationConfig.Home(config))
                }
                is OverviewOutput.NavigateToTaskEditor -> {
                    val config = EditorConfig.Task(
                        timeTaskId = output.timeTaskId,
                        timeRange = output.timeRange,
                        date = output.date,
                        undefinedTaskId = output.undefinedTaskId,
                    )
                    outputConsumer.consume(TabNavigationOutput.NavigateToEditor(config))
                }
            }
        }

        private fun templatesOutputConsumer() = OutputConsumer<TemplatesOutput> { output ->
            when (output) {
                is TemplatesOutput.NavigateToBack -> {
                    stackNavigation.pop()
                }
            }
        }
    }
}
