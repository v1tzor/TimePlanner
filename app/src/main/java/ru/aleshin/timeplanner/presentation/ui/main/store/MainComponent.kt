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
package ru.aleshin.timeplanner.presentation.ui.main.store

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.component.saveableStore
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.editor.api.EditorDecomposeFeatureFactory
import ru.aleshin.features.editor.api.EditorOutput
import ru.aleshin.features.overview.api.OverviewConfig
import ru.aleshin.features.settings.api.SettingsConfig
import ru.aleshin.features.settings.api.SettingsDecomposeFeatureFactory
import ru.aleshin.features.settings.api.SettingsOutput
import ru.aleshin.features.templates.api.TemplatesConfig
import ru.aleshin.features.templates.api.TemplatesDecomposeFeatureFactory
import ru.aleshin.features.templates.api.TemplatesOutput
import ru.aleshin.timeplanner.presentation.ui.main.contract.DeepLinkTarget
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEvent
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainInput
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainOutput
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainState
import ru.aleshin.timeplanner.presentation.ui.main.contract.ShareTarget
import ru.aleshin.timeplanner.presentation.ui.main.store.MainComponent.Child.EditorChild
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponent
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponent.TabNavigationConfig
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponent.TabNavigationOutput
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponentFactory

/**
 * @author Stanislav Aleshin on 12.09.2025.
 */
abstract class MainComponent(
    componentContext: ComponentContext,
) : BaseComponent(componentContext = componentContext) {

    abstract val store: MainComposeStore

    abstract val childStack: Value<ChildStack<*, Child>>

    abstract fun navigateToBack()

    abstract fun onDeepLink(target: DeepLinkTarget)

    abstract fun onShare(target: ShareTarget)

    @Serializable
    sealed interface Config {

        @Serializable
        data object Splash : Config

        @Serializable
        data class TabNavigation(val startConfig: TabNavigationConfig) : Config

        @Serializable
        data class Editor(val startConfig: EditorConfig) : Config

        @Serializable
        data class Settings(val startConfig: SettingsConfig) : Config

        @Serializable
        data class Templates(val startConfig: TemplatesConfig) : Config
    }

    sealed interface Child {
        data object SplashChild : Child
        data class TabNavigationChild(val component: TabNavigationComponent) : Child
        data class EditorChild(val contentProvider: FeatureContentProvider) : Child
        data class SettingsChild(val contentProvider: FeatureContentProvider) : Child
        data class TemplatesChild(val contentProvider: FeatureContentProvider) : Child
    }

    class Base(
        componentContext: ComponentContext,
        initialDeepLinkTarget: DeepLinkTarget?,
        initialShareTarget: ShareTarget?,
        mainStoreFactory: MainComposeStore.Factory,
        private val navigationComponentFactory: TabNavigationComponentFactory,
        private val editorFeatureFactory: EditorDecomposeFeatureFactory,
        private val settingsFeatureFactory: SettingsDecomposeFeatureFactory,
        private val templatesFeatureFactory: TemplatesDecomposeFeatureFactory,
    ) : MainComponent(componentContext) {

        companion object {
            const val STORE_KEY = "MAIN_STORE_KEY"
            const val STACK_KEY = "MAIN_STACK_KEY"
        }

        override val store by saveableStore(
            storeFactory = mainStoreFactory,
            defaultState = MainState(),
            stateSerializer = MainState.serializer(),
            input = MainInput(initialDeepLinkTarget, initialShareTarget),
            outputConsumer = mainOutputConsumer(),
            storeKey = STORE_KEY,
        )

        private val stackNavigation = StackNavigation<Config>()

        override val childStack = childStack(
            source = stackNavigation,
            initialConfiguration = Config.Splash,
            serializer = Config.serializer(),
            key = STACK_KEY,
            handleBackButton = true,
            childFactory = ::childFactory,
        )

        override fun navigateToBack() {
            stackNavigation.pop()
        }

        override fun onDeepLink(target: DeepLinkTarget) {
            store.dispatchEvent(MainEvent.ProcessDeepLink(target))
        }

        override fun onShare(target: ShareTarget) {
            store.dispatchEvent(MainEvent.ProcessShare(target))
        }

        private fun childFactory(config: Config, componentContext: ComponentContext): Child {
            return when (config) {
                is Config.Splash -> Child.SplashChild
                is Config.TabNavigation -> {
                    val component = navigationComponentFactory.createComponent(
                        componentContext = componentContext,
                        startConfig = config.startConfig,
                        outputConsumer = tabNavigationOutputConsumer()
                    )
                    Child.TabNavigationChild(component = component)
                }
                is Config.Editor -> {
                    val api = editorFeatureFactory.createOrGetFeature(componentContext).contentProviderFactory()
                    val provider = api.createProvider(
                        componentContext = componentContext,
                        startConfig = config.startConfig,
                        outputConsumer = editorOutputConsumer()
                    )
                    EditorChild(contentProvider = provider)
                }
                is Config.Settings -> {
                    val api = settingsFeatureFactory.createOrGetFeature(componentContext)
                    val provider = api.contentProviderFactory().createProvider(
                        componentContext = componentContext,
                        startConfig = config.startConfig,
                        outputConsumer = settingsOutputConsumer(),
                    )
                    Child.SettingsChild(provider)
                }
                is Config.Templates -> {
                    val api = templatesFeatureFactory.createOrGetFeature(componentContext)
                    val provider = api.contentProviderFactory().createProvider(
                        componentContext = componentContext,
                        startConfig = config.startConfig,
                        outputConsumer = templatesOutputConsumer(),
                    )
                    Child.TemplatesChild(provider)
                }
            }
        }

        private fun mainOutputConsumer() = OutputConsumer<MainOutput> { output ->
            when (output) {
                is MainOutput.NavigateToEditor -> {
                    stackNavigation.pushToFront(Config.Editor(output.config))
                }
                is MainOutput.NavigateToOverview -> {
                    val config = TabNavigationConfig.Overview(OverviewConfig.Overview(output.sharedText, output.sharedKey))
                    stackNavigation.replaceAll(Config.TabNavigation(config))
                }
                is MainOutput.NavigateToTabNavigation -> {
                    val config = TabNavigationConfig.Home()
                    stackNavigation.replaceAll(Config.TabNavigation(config))
                }
            }
        }

        private fun tabNavigationOutputConsumer() = OutputConsumer<TabNavigationOutput> { output ->
            when (output) {
                is TabNavigationOutput.NavigateToEditor -> {
                    stackNavigation.pushToFront(Config.Editor(output.config))
                }
                is TabNavigationOutput.NavigateToSettings -> {
                    stackNavigation.pushToFront(Config.Settings(SettingsConfig.Settings))
                }
                is TabNavigationOutput.NavigateToBack -> {
                    navigateToBack()
                }
            }
        }

        private fun editorOutputConsumer() = OutputConsumer<EditorOutput> { output ->
            when (output) {
                is EditorOutput.NavigateToTemplates -> {
                    stackNavigation.pushToFront(Config.Templates(TemplatesConfig.Templates))
                }
                is EditorOutput.NavigateToBack -> {
                    navigateToBack()
                }
            }
        }

        private fun settingsOutputConsumer() = OutputConsumer<SettingsOutput> { output ->
            when (output) {
                is SettingsOutput.NavigateToBack -> navigateToBack()
            }
        }

        private fun templatesOutputConsumer() = OutputConsumer<TemplatesOutput> { output ->
            when (output) {
                is TemplatesOutput.NavigateToBack -> navigateToBack()
            }
        }
    }
}
