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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.utils.architecture.component.EmptyInput
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsBucketGranularity
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCategorySort
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparison
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparisonState
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsCategoryDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsCreationDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsDurationDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsKeyMetricsUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsLoadDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsOverviewUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsPlanSourceDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRegularityUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsSummaryUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsWeekdayHourLoadUi
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsAction
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsOutput
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsState
import java.util.Date

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class AnalyticsComposeStoreTest {

    @Test
    fun `initialization requests analytics with restored sort`() = runTest {
        val commands = mutableListOf<AnalyticsWorkCommand>()
        val store = createStore(
            initialState = AnalyticsState(categorySort = AnalyticsCategorySort.BY_TASKS),
        ) { command ->
            commands.add(command)
            emptyFlow()
        }

        store.initialize(EmptyInput, false)
        advanceUntilIdle()

        assertEquals(
            listOf(
                AnalyticsWorkCommand.ObserveAnalytics(
                    categorySort = AnalyticsCategorySort.BY_TASKS,
                    currentRange = null,
                    hasOverview = false,
                ),
            ),
            commands,
        )
    }

    @Test
    fun `restored render snapshot does not restart placeholder`() = runTest {
        val commands = mutableListOf<AnalyticsWorkCommand>()
        val store = createStore(
            initialState = AnalyticsState(
                isLoading = false,
                range = range(1L),
                overview = overview(),
                categorySort = AnalyticsCategorySort.BY_TASKS,
            ),
        ) { command ->
            commands.add(command)
            emptyFlow()
        }

        store.initialize(EmptyInput, true)
        advanceUntilIdle()

        assertEquals(
            listOf(
                AnalyticsWorkCommand.ObserveAnalytics(
                    categorySort = AnalyticsCategorySort.BY_TASKS,
                    currentRange = range(1L),
                    hasOverview = true,
                ),
            ),
            commands,
        )
        assertFalse(store.state.isLoading)
    }

    @Test
    fun `range and analytics results update their state`() = runTest {
        val range = range(2L)
        val overview = overview()
        val store = createStore {
            flowOf(
                ActionResult(AnalyticsAction.SetupRange(range = range)),
                ActionResult(
                    AnalyticsAction.UpdateAnalytics(
                        categorySort = AnalyticsCategorySort.BY_TIME,
                        overview = overview,
                    )
                ),
            )
        }

        store.initialize(EmptyInput, false)
        advanceUntilIdle()

        assertEquals(range, store.state.range)
        assertEquals(overview, store.state.overview)
        assertFalse(store.state.isLoading)
    }

    @Test
    fun `initial failure stops placeholder and exposes retry state`() = runTest {
        val store = createStore {
            flowOf(
                ActionResult(
                    AnalyticsAction.UpdateLoading(
                        isLoading = false,
                        isError = true,
                    )
                )
            )
        }

        store.initialize(EmptyInput, false)
        advanceUntilIdle()

        assertFalse(store.state.isLoading)
        assertTrue(store.state.isError)
    }

    @Test
    fun `retry requests current category sort`() = runTest {
        val commands = mutableListOf<AnalyticsWorkCommand>()
        val store = createStore(
            initialState = AnalyticsState(
                isLoading = false,
                isError = true,
                categorySort = AnalyticsCategorySort.BY_TASKS,
            ),
        ) { command ->
            commands.add(command)
            emptyFlow()
        }

        store.dispatchEvent(AnalyticsEvent.Retry)
        advanceUntilIdle()

        assertEquals(
            listOf(
                AnalyticsWorkCommand.ObserveAnalytics(
                    categorySort = AnalyticsCategorySort.BY_TASKS,
                    currentRange = null,
                    hasOverview = false,
                ),
            ),
            commands,
        )
    }

    @Test
    fun `category sort updates state and reloads domain data without placeholder`() = runTest {
        val commands = mutableListOf<AnalyticsWorkCommand>()
        val range = range(1L)
        val overview = overview()
        val store = createStore(
            initialState = AnalyticsState(
                isLoading = false,
                range = range,
                overview = overview,
            ),
        ) { command ->
            commands.add(command)
            flowOf(
                ActionResult(
                    AnalyticsAction.UpdateAnalytics(
                        categorySort = AnalyticsCategorySort.BY_TASKS,
                        overview = overview,
                    )
                ),
            )
        }

        store.dispatchEvent(AnalyticsEvent.ChangeCategorySort(AnalyticsCategorySort.BY_TASKS))
        advanceUntilIdle()

        assertEquals(AnalyticsCategorySort.BY_TASKS, store.state.categorySort)
        assertFalse(store.state.isLoading)
        assertEquals(
            AnalyticsWorkCommand.ObserveAnalytics(
                categorySort = AnalyticsCategorySort.BY_TASKS,
                currentRange = range,
                hasOverview = true,
            ),
            commands.single(),
        )
    }

    @Test
    fun `range setup clears analytics from previous range`() = runTest {
        val previousRange = range(1L)
        val currentRange = range(2L)
        val previousOverview = overview()
        val store = createStore(
            initialState = AnalyticsState(
                isLoading = false,
                range = previousRange,
                overview = previousOverview,
            ),
        ) {
            flowOf(
                ActionResult(AnalyticsAction.SetupRange(currentRange)),
            )
        }

        store.initialize(EmptyInput, false)
        advanceUntilIdle()

        assertEquals(currentRange, store.state.range)
        assertNull(store.state.overview)
        assertTrue(store.state.isLoading)
        assertEquals(AnalyticsCategorySort.BY_TIME, store.state.categorySort)
    }

    @Test
    fun `category expansion is local render state`() = runTest {
        val store = createStore()

        store.dispatchEvent(AnalyticsEvent.ToggleCategories)
        advanceUntilIdle()

        assertTrue(store.state.isCategoriesExpanded)

        store.dispatchEvent(AnalyticsEvent.ToggleCategories)
        advanceUntilIdle()

        assertFalse(store.state.isCategoriesExpanded)
    }

    @Test
    fun `chart selections are reduced as local state`() = runTest {
        val store = createStore()

        store.dispatchEvent(AnalyticsEvent.SelectChartItem(2L))
        store.dispatchEvent(AnalyticsEvent.SelectCreationBucket(3L))
        advanceUntilIdle()

        assertEquals(2L, store.state.selectedChartKey)
        assertEquals(3L, store.state.selectedCreationBucketKey)
    }

    @Test
    fun `navigation emits exactly one output`() = runTest {
        val store = createStore()
        val outputs = mutableListOf<AnalyticsOutput>()
        store.setOutputConsumer(OutputConsumer(outputs::add))

        store.dispatchEvent(AnalyticsEvent.ClickCategoryItem(42L))
        advanceUntilIdle()

        assertEquals(listOf(AnalyticsOutput.NavigateToCategory(42L)), outputs)
    }

    @Test
    fun `serialization preserves render snapshot`() {
        val state = AnalyticsState(
            isLoading = false,
            range = range(7L),
            overview = overview(),
            categorySort = AnalyticsCategorySort.BY_TASKS,
            isCategoriesExpanded = true,
            selectedChartKey = 2L,
            selectedCreationBucketKey = 3L,
        )

        val encoded = Json.encodeToString(AnalyticsState.serializer(), state)
        val restored = Json.decodeFromString(AnalyticsState.serializer(), encoded)

        assertEquals(state, restored)
        assertNull(restored.overview?.categories?.otherBucket)
    }

    private fun createStore(
        initialState: AnalyticsState = AnalyticsState(),
        work: suspend (AnalyticsWorkCommand) -> Flow<AnalyticsWorkResult> = { emptyFlow() },
    ): AnalyticsComposeStore {
        val dispatcher = Dispatchers.Unconfined
        return AnalyticsComposeStore(
            analyticsWorkProcessor = object : AnalyticsWorkProcessor {
                override suspend fun work(command: AnalyticsWorkCommand) = work(command)
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

    private fun overview() = AnalyticsOverviewUi(
        summary = AnalyticsSummaryUi(0L, 0L, 0L, 0L, 0, 0, null, comparison()),
        categories = AnalyticsCategoryDistributionUi(
            buckets = emptyList(),
            collapsedBucketCount = 0,
            otherBucket = null,
        ),
        load = AnalyticsLoadDistributionUi(AnalyticsBucketGranularity.DAY, emptyList()),
        creation = AnalyticsCreationDistributionUi(
            buckets = emptyList(),
            totalDurationMillis = 0L,
            medianLeadTimeMillis = null,
            qualifyingTaskCount = 0,
        ),
        durations = AnalyticsDurationDistributionUi(emptyList(), null, null, comparison(), comparison()),
        planSource = AnalyticsPlanSourceDistributionUi(emptyList()),
        keyMetrics = AnalyticsKeyMetricsUi(0L, 0.0, 0L, 0.0, null, null, 0L),
        regularity = AnalyticsRegularityUi(emptyList(), 0, 0, 0),
        weekdayHourLoad = AnalyticsWeekdayHourLoadUi(rows = emptyList()),
    )

    private fun comparison() = AnalyticsComparison(null, AnalyticsComparisonState.UNAVAILABLE)
}
