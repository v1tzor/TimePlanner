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
package ru.aleshin.features.home.impl.presentation.ui.root

import androidx.compose.material3.DrawerValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import ru.aleshin.core.utils.architecture.component.FeatureComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DrawerManager
import ru.aleshin.features.home.api.HomeConfig
import ru.aleshin.features.home.api.HomeOutput
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesOutput
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.CategoriesComponent
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.CategoriesComposeStore
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsOutput
import ru.aleshin.features.home.impl.presentation.ui.details.store.DetailsComponent
import ru.aleshin.features.home.impl.presentation.ui.details.store.DetailsComposeStore
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeInput
import ru.aleshin.features.home.impl.presentation.ui.home.store.HomeComponent
import ru.aleshin.features.home.impl.presentation.ui.home.store.HomeComposeStore
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewInput
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewOutput
import ru.aleshin.features.home.impl.presentation.ui.overview.store.OverviewComponent
import ru.aleshin.features.home.impl.presentation.ui.overview.store.OverviewComposeStore
import ru.aleshin.features.home.impl.presentation.ui.templates.store.TemplatesComponent
import ru.aleshin.features.home.impl.presentation.ui.templates.store.TemplatesComposeStore
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeOutput as HomeScreenOutput

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal abstract class InternalHomeFeatureComponent(
    componentContext: ComponentContext,
    startConfig: StartFeatureConfig<HomeConfig>,
    outputConsumer: OutputConsumer<HomeOutput>,
) : FeatureComponent<HomeConfig, HomeOutput>(
    componentContext = componentContext,
    startConfig = startConfig,
    outputConsumer = outputConsumer,
) {

    abstract val stack: Value<ChildStack<*, Child>>

    abstract fun setDrawerManager(drawerManager: DrawerManager?)

    sealed class Child {
        data class OverviewChild(val component: OverviewComponent) : Child()
        data class HomeChild(val component: HomeComponent) : Child()
        data class DetailsChild(val component: DetailsComponent) : Child()
        data class TemplatesChild(val component: TemplatesComponent) : Child()
        data class CategoriesChild(val component: CategoriesComponent) : Child()
    }

    class Default(
        startConfig: StartFeatureConfig<HomeConfig>,
        componentContext: ComponentContext,
        outputConsumer: OutputConsumer<HomeOutput>,
        private val homeStoreFactory: HomeComposeStore.Factory,
        private val overviewStoreFactory: OverviewComposeStore.Factory,
        private val categoriesStoreFactory: CategoriesComposeStore.Factory,
        private val templatesStoreFactory: TemplatesComposeStore.Factory,
        private val detailsStoreFactory: DetailsComposeStore.Factory,
        private val coroutineManager: CoroutineManager,
    ) : InternalHomeFeatureComponent(
        componentContext = componentContext,
        startConfig = startConfig,
        outputConsumer = outputConsumer,
    ) {

        private var drawerManager: DrawerManager? = null

        private var drawerJob: Job? = null

        private val coroutineScope = CoroutineScope(coroutineManager.defaultDispatcher)

        private val backCallback = BackCallback {
            if (drawerManager?.drawerValue?.value == DrawerValue.Open) {
                coroutineManager.runOnDefaultBackground(coroutineScope) {
                    drawerManager?.closeDrawer()
                }
            } else {
                navigateToBack()
            }
        }

        private val stackNavigation = StackNavigation<HomeConfig>()

        override val stack: Value<ChildStack<*, Child>> = childStack(
            source = stackNavigation,
            serializer = HomeConfig.serializer(),
            initialStack = { startConfig.backstack ?: listOf(HomeConfig.Home()) },
            key = STACK_KEY,
            handleBackButton = false,
            childFactory = ::createChild,
        )

        private companion object {
            const val STACK_KEY = "Home_ROOT_STACK"
        }

        init {
            backHandler.register(backCallback)
        }

        override fun setDrawerManager(drawerManager: DrawerManager?) {
            this.drawerManager = null
            this.drawerManager = drawerManager

            drawerJob?.cancel()
            drawerJob = null

            drawerJob = coroutineManager.runOnDefaultBackground(coroutineScope) {
                drawerManager?.events?.collect { index ->
                    when (index) {
                        0 -> stackNavigation.bringToFront(HomeConfig.Home())
                        1 -> stackNavigation.bringToFront(HomeConfig.Overview())
                        2 -> stackNavigation.bringToFront(HomeConfig.Templates)
                        3 -> stackNavigation.bringToFront(HomeConfig.Categories())
                        else -> Unit
                    }
                }
            }
        }

        override fun navigateToBack() {
            stackNavigation.pop { isPop ->
                if (!isPop) outputConsumer.consume(HomeOutput.NavigateToBack)
            }
        }

        override fun onDestroyInstance() {
            coroutineScope.cancel()
            drawerManager = null
        }

        private fun createChild(config: HomeConfig, componentContext: ComponentContext): Child {
            return when (config) {
                is HomeConfig.Home -> Child.HomeChild(
                    component = HomeComponent.Default(
                        storeFactory = homeStoreFactory,
                        componentContext = componentContext,
                        inputData = HomeInput(config.scheduleDate),
                        outputConsumer = homeOutputConsumer(),
                    )
                )
                is HomeConfig.Categories -> Child.CategoriesChild(
                    component = CategoriesComponent.Default(
                        storeFactory = categoriesStoreFactory,
                        componentContext = componentContext,
                        outputConsumer = categoriesOutputConsumer(),
                    )
                )
                is HomeConfig.Details -> Child.DetailsChild(
                    component = DetailsComponent.Default(
                        storeFactory = detailsStoreFactory,
                        componentContext = componentContext,
                        outputConsumer = detailsOutputConsumer(),
                    )
                )
                is HomeConfig.Overview -> Child.OverviewChild(
                    component = OverviewComponent.Default(
                        storeFactory = overviewStoreFactory,
                        componentContext = componentContext,
                        inputData = OverviewInput(config.sharedText),
                        outputConsumer = overviewOutputConsumer(),
                    )
                )
                is HomeConfig.Templates -> Child.TemplatesChild(
                    component = TemplatesComponent.Default(
                        storeFactory = templatesStoreFactory,
                        componentContext = componentContext,
                    )
                )
            }
        }

        private fun homeOutputConsumer() = OutputConsumer<HomeScreenOutput> { output ->
            when (output) {
                is HomeScreenOutput.NavigateToEditor -> {
                    val config = HomeOutput.NavigateToEditor(
                        timeTask = output.config.timeTask,
                        template = output.config.template,
                        undefinedTaskId = output.config.undefinedTaskId,
                    )
                    outputConsumer.consume(config)
                }
                is HomeScreenOutput.NavigateToOverview -> {
                    stackNavigation.pushToFront(HomeConfig.Overview())
                }
            }
        }

        private fun categoriesOutputConsumer() = OutputConsumer<CategoriesOutput> { output ->
            when (output) {
                is CategoriesOutput.NavigateToBack -> navigateToBack()
            }
        }

        private fun detailsOutputConsumer() = OutputConsumer<DetailsOutput> { output ->
            when (output) {
                is DetailsOutput.NavigateToHome -> {
                    stackNavigation.pushToFront(output.config)
                }
                is DetailsOutput.NavigateToBack -> navigateToBack()
            }
        }

        private fun overviewOutputConsumer() = OutputConsumer<OverviewOutput> { output ->
            when (output) {
                is OverviewOutput.NavigateToEditor -> {
                    val config = HomeOutput.NavigateToEditor(
                        timeTask = output.config.timeTask,
                        template = output.config.template,
                        undefinedTaskId = output.config.undefinedTaskId,
                    )
                    outputConsumer.consume(config)
                }
                is OverviewOutput.NavigateToDetails -> {
                    stackNavigation.pushToFront(HomeConfig.Details)
                }
                is OverviewOutput.NavigateToHome -> {
                    stackNavigation.pushToFront(output.config)
                }
            }
        }
    }
}
