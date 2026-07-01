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
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.editor.api.EditorDecomposeFeatureFactory
import ru.aleshin.features.editor.api.EditorOutput
import ru.aleshin.features.home.api.HomeConfig
import ru.aleshin.features.home.api.HomeDecomposeFeatureFactory
import ru.aleshin.features.home.api.HomeOutput
import ru.aleshin.timeplanner.presentation.ui.main.contract.DeepLinkTarget
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEvent
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainInput
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainOutput
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainState
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

    @Serializable
    sealed interface Config {

        @Serializable
        data object Splash : Config

        @Serializable
        data class TabNavigation(val startConfig: StartFeatureConfig<TabNavigationConfig>) : Config

        @Serializable
        data class Home(val config: StartFeatureConfig<HomeConfig>?) : Config

        @Serializable
        data class Editor(val startConfig: StartFeatureConfig<EditorConfig>) : Config
    }

    sealed interface Child {
        data object SplashChild : Child
        data class HomeChild(val contentProvider: FeatureContentProvider) : Child
        data class TabNavigationChild(val component: TabNavigationComponent) : Child
        data class EditorChild(val contentProvider: FeatureContentProvider) : Child
    }

    class Base(
        componentContext: ComponentContext,
        private val initialDeepLinkTarget: DeepLinkTarget?,
        private val mainStoreFactory: MainComposeStore.Factory,
        private val homeFeatureFactory: HomeDecomposeFeatureFactory,
        private val navigationComponentFactory: TabNavigationComponentFactory,
        private val editorFeatureFactory: EditorDecomposeFeatureFactory,
    ) : MainComponent(componentContext) {

        companion object {
            const val STORE_KEY = "MAIN_STORE_KEY"
            const val STACK_KEY = "MAIN_STACK_KEY"
        }

        override val store by saveableStore(
            storeFactory = mainStoreFactory,
            defaultState = MainState(),
            stateSerializer = MainState.serializer(),
            input = MainInput(initialDeepLinkTarget),
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
                is Config.Home -> {
                    val api = homeFeatureFactory.createOrGetFeature(componentContext)
                    val provider = api.contentProviderFactory().createProvider(
                        componentContext = componentContext,
                        startConfig = config.config ?: StartFeatureConfig(null),
                        outputConsumer = homeOutputConsumer()
                    )
                    Child.HomeChild(contentProvider = provider)
                }
            }
        }

        private fun mainOutputConsumer() = OutputConsumer<MainOutput> { output ->
            when (output) {
                is MainOutput.NavigateToEditor -> {
                    val config = StartFeatureConfig(listOf(output.config))
                    stackNavigation.pushToFront(Config.Editor(config))
                }
                is MainOutput.NavigateToTabNavigation -> {
                    val config = StartFeatureConfig<TabNavigationConfig>(null)
                    stackNavigation.replaceAll(Config.TabNavigation(config))
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
                    val startConfig = StartFeatureConfig<EditorConfig>(listOf(config))
                    stackNavigation.pushToFront(Config.Editor(startConfig))
                }
                is HomeOutput.NavigateToBack -> {
                    stackNavigation.pop()
                }
            }
        }

        private fun editorOutputConsumer() = OutputConsumer<EditorOutput> { output ->
            when (output) {
                is EditorOutput.NavigateToCategories -> {
                    val homeConfig = StartFeatureConfig<HomeConfig>(listOf(HomeConfig.Categories(output.categoryId)))
                    stackNavigation.pushToFront(Config.Home(homeConfig))
                }
                is EditorOutput.NavigateToTemplates -> {
                    val homeConfig = StartFeatureConfig<HomeConfig>(listOf(HomeConfig.Templates))
                    stackNavigation.pushToFront(Config.Home(homeConfig))
                }
                is EditorOutput.NavigateToBack -> {
                    navigateToBack()
                }
            }
        }

        private fun tabNavigationOutputConsumer() = OutputConsumer<TabNavigationOutput> { output ->
            when (output) {
                is TabNavigationOutput.NavigateToEditor -> {
                    val config = StartFeatureConfig(listOf(output.config))
                    stackNavigation.pushToFront(Config.Editor(config))
                }
                is TabNavigationOutput.NavigateToBack -> {
                    navigateToBack()
                }
            }
        }
    }
}
