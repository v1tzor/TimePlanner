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

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.analytics.api.AnalyticsConfig
import ru.aleshin.features.analytics.api.AnalyticsOutput
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.store.AnalyticsComposeStore
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.store.AnalyticsWorkCommand
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.store.AnalyticsWorkProcessor
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEvent
import ru.aleshin.features.analytics.impl.presentation.ui.category.store.CategoryComposeStore
import ru.aleshin.features.analytics.impl.presentation.ui.category.store.CategoryWorkCommand
import ru.aleshin.features.analytics.impl.presentation.ui.category.store.CategoryWorkProcessor

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class InternalAnalyticsFeatureComponentTest {

    @Test
    fun `category route is unique and back restores main state`() = runTest {
        val outputs = mutableListOf<AnalyticsOutput>()
        val root = createRoot(outputs)
        val main = (root.stack.value.active.instance as InternalAnalyticsFeatureComponent.Child.AnalyticsChild).component

        main.store.dispatchEvent(AnalyticsEvent.ToggleCategories)
        main.store.dispatchEvent(AnalyticsEvent.ClickCategoryItem(42L))
        advanceUntilIdle()
        val firstCategory = root.stack.value.active.instance as InternalAnalyticsFeatureComponent.Child.CategoryChild

        main.store.dispatchEvent(AnalyticsEvent.ClickCategoryItem(42L))
        advanceUntilIdle()

        assertEquals(1, root.stack.value.backStack.size)
        assertEquals(firstCategory.component, (root.stack.value.active.instance as InternalAnalyticsFeatureComponent.Child.CategoryChild).component)

        firstCategory.component.store.dispatchEvent(CategoryEvent.NavigateBack)
        advanceUntilIdle()

        val restoredMain = (root.stack.value.active.instance as InternalAnalyticsFeatureComponent.Child.AnalyticsChild).component
        assertEquals(main, restoredMain)
        assertTrue(restoredMain.store.state.isCategoriesExpanded)
        assertTrue(outputs.isEmpty())
    }

    @Test
    fun `back from root uses feature fallback once`() = runTest {
        val outputs = mutableListOf<AnalyticsOutput>()
        val root = createRoot(outputs)

        root.navigateToBack()
        advanceUntilIdle()

        assertEquals(listOf(AnalyticsOutput.NavigateToBack), outputs)
    }

    private fun createRoot(outputs: MutableList<AnalyticsOutput>): InternalAnalyticsFeatureComponent.Default {
        val dispatcher = Dispatchers.Unconfined
        val coroutineManager = object : CoroutineManager.Abstract(dispatcher, dispatcher, dispatcher) {}
        val analyticsProcessor = object : AnalyticsWorkProcessor {
            override suspend fun work(command: AnalyticsWorkCommand) = emptyFlow<ru.aleshin.features.analytics.impl.presentation.ui.analytics.store.AnalyticsWorkResult>()
        }
        val categoryProcessor = object : CategoryWorkProcessor {
            override suspend fun work(command: CategoryWorkCommand) = emptyFlow<ru.aleshin.features.analytics.impl.presentation.ui.category.store.CategoryWorkResult>()
        }
        return InternalAnalyticsFeatureComponent.Default(
            startConfig = AnalyticsConfig.Analytics,
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            outputConsumer = OutputConsumer(outputs::add),
            analyticsStoreFactory = AnalyticsComposeStore.Factory(analyticsProcessor, coroutineManager),
            categoryStoreFactory = CategoryComposeStore.Factory(categoryProcessor, coroutineManager),
        )
    }
}
