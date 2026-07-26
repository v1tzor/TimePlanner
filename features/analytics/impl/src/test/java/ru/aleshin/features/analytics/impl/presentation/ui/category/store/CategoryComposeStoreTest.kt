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
package ru.aleshin.features.analytics.impl.presentation.ui.category.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryAnalyticsUi
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryAction
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEvent
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryInput
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryOutput
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryState
import java.util.Date

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class CategoryComposeStoreTest {

    @Test
    fun `missing category produces typed unavailable state`() = runTest {
        val range = range(1L)
        val unavailable = unavailableAnalytics()
        val store = createStore {
            flowOf(
                ActionResult(CategoryAction.SetupRange(range)),
                ActionResult(
                    CategoryAction.UpdateAnalytics(
                        category = null,
                        analytics = unavailable,
                        isUnavailable = true,
                    )
                ),
            )
        }

        store.initialize(CategoryInput(42L), false)
        advanceUntilIdle()

        assertEquals(42L, store.state.mainCategoryId)
        assertTrue(store.state.isUnavailable)
        assertNull(store.state.category)
    }

    @Test
    fun `range mutation waits for repository observation`() = runTest {
        val range = range(1L)
        val store = createStore { command ->
            if (command is CategoryWorkCommand.ObserveAnalytics) {
                flowOf(ActionResult(CategoryAction.SetupRange(range)))
            } else {
                emptyFlow()
            }
        }

        store.initialize(CategoryInput(42L), false)
        advanceUntilIdle()
        store.dispatchEvent(CategoryEvent.SelectPeriod(TimePeriod.MONTH))
        advanceUntilIdle()

        assertEquals(range, store.state.range)
    }

    @Test
    fun `back emits one output`() = runTest {
        val store = createStore { emptyFlow() }
        val outputs = mutableListOf<CategoryOutput>()
        store.setOutputConsumer(OutputConsumer(outputs::add))

        store.dispatchEvent(CategoryEvent.NavigateBack)
        advanceUntilIdle()

        assertEquals(listOf(CategoryOutput.NavigateToBack), outputs)
    }

    @Test
    fun `chart selections are mutually exclusive local state`() = runTest {
        val store = createStore { emptyFlow() }

        store.dispatchEvent(CategoryEvent.SelectSubCategoryBucket(7L))
        advanceUntilIdle()
        assertEquals(7L, store.state.selectedSubCategoryBucketKey)
        assertNull(store.state.selectedLoadBucketIndex)

        store.dispatchEvent(CategoryEvent.SelectLoadBucket(3))
        advanceUntilIdle()
        assertNull(store.state.selectedSubCategoryBucketKey)
        assertEquals(3, store.state.selectedLoadBucketIndex)
    }

    @Test
    fun `deactivate and activate keep one category observer`() = runTest {
        var activeCollectors = 0
        var maximumCollectors = 0
        val store = createStore { command ->
            if (command !is CategoryWorkCommand.ObserveAnalytics) return@createStore emptyFlow()
            flow {
                activeCollectors++
                maximumCollectors = maxOf(maximumCollectors, activeCollectors)
                try {
                    awaitCancellation()
                } finally {
                    activeCollectors--
                }
            }
        }

        store.initialize(CategoryInput(42L), false)
        advanceUntilIdle()
        assertEquals(1, activeCollectors)

        store.dispatchEvent(CategoryEvent.Deactivate)
        advanceUntilIdle()
        assertEquals(0, activeCollectors)

        store.dispatchEvent(CategoryEvent.Activate)
        store.dispatchEvent(CategoryEvent.Activate)
        advanceUntilIdle()

        assertEquals(1, activeCollectors)
        assertEquals(1, maximumCollectors)
    }

    @Test
    fun `category serialization excludes aggregate and keeps local state`() {
        val state = CategoryState(
            mainCategoryId = 42L,
            range = range(7L),
            analytics = unavailableAnalytics(),
            isTasksExpanded = true,
        )

        val encoded = Json.encodeToString(CategoryState.serializer(), state)
        val restored = Json.decodeFromString(CategoryState.serializer(), encoded)

        assertEquals(42L, restored.mainCategoryId)
        assertEquals(state.range, restored.range)
        assertTrue(restored.isTasksExpanded)
        assertNull(restored.analytics)
    }

    private fun createStore(
        initialState: CategoryState = CategoryState(),
        work: suspend (CategoryWorkCommand) -> Flow<CategoryWorkResult>,
    ): CategoryComposeStore {
        val dispatcher = Dispatchers.Unconfined
        return CategoryComposeStore(
            workProcessor = object : CategoryWorkProcessor {
                override suspend fun work(command: CategoryWorkCommand) = work(command)
            },
            stateCommunicator = StateCommunicator.Default(initialState),
            effectCommunicator = EffectCommunicator.Default(),
            coroutineManager = object : CoroutineManager.Abstract(dispatcher, dispatcher, dispatcher) {},
        )
    }

    private fun range(day: Long) = AnalyticsRangeUi(
        period = TimePeriod.WEEK,
        anchorDate = Date(day),
        from = Date(day),
        to = Date(day),
        comparisonFrom = Date(day),
        comparisonTo = Date(day),
    )

    private fun unavailableAnalytics() = CategoryAnalyticsUi(
        category = null,
        summary = null,
        keyMetrics = null,
        subCategories = null,
        load = null,
        dayParts = emptyList(),
        dayPartSummaries = emptyList(),
        taskRows = emptyList(),
        observation = null,
    )
}
