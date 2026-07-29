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
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.overview.api.OverviewConfig
import ru.aleshin.features.overview.api.OverviewOutput
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsAction
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsEffect
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsOutput
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsComposeStore
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsWorkCommand
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsWorkProcessor
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryAction
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryEffect
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryOutput
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryComposeStore
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryWorkCommand
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryWorkProcessor
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewEvent
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewComposeStore
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewWorkCommand
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewWorkProcessor
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewWorkResult

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class InternalOverviewFeatureComponentTest {

    @Test
    fun goalDetailsAndHistoryRoutesReturnToSameOverview() = runTest {
        val outputs = mutableListOf<OverviewOutput>()
        val root = createRoot(outputs)
        val overview = (
            root.stack.value.active.instance as InternalOverviewFeatureComponent.Child.OverviewChild
        ).component

        overview.store.dispatchEvent(OverviewEvent.OpenGoal(GOAL_ID))
        advanceUntilIdle()
        assertEquals(
            GOAL_ID,
            (
                root.stack.value.active.configuration as OverviewConfig.GoalDetails
            ).goalId,
        )

        root.navigateToBack()
        advanceUntilIdle()
        val restoredOverview = (
            root.stack.value.active.instance as InternalOverviewFeatureComponent.Child.OverviewChild
        ).component
        assertEquals(overview, restoredOverview)

        overview.store.dispatchEvent(OverviewEvent.OpenGoalsHistory)
        advanceUntilIdle()
        assertTrue(root.stack.value.active.configuration is OverviewConfig.GoalsHistory)

        root.navigateToBack()
        advanceUntilIdle()
        assertEquals(
            overview,
            (
                root.stack.value.active.instance as InternalOverviewFeatureComponent.Child.OverviewChild
            ).component,
        )
        assertTrue(outputs.isEmpty())
    }

    @Test
    fun createGoalUsesPublicEditorOutput() = runTest {
        val outputs = mutableListOf<OverviewOutput>()
        val root = createRoot(outputs)
        val overview = (
            root.stack.value.active.instance as InternalOverviewFeatureComponent.Child.OverviewChild
        ).component

        overview.store.dispatchEvent(OverviewEvent.CreateGoal)
        advanceUntilIdle()

        assertEquals(listOf(OverviewOutput.NavigateToGoalEditor(null)), outputs)
    }

    private fun createRoot(
        outputs: MutableList<OverviewOutput>,
    ): InternalOverviewFeatureComponent.Default {
        val dispatcher = Dispatchers.Unconfined
        val coroutineManager = object : CoroutineManager.Abstract(
            dispatcher,
            dispatcher,
            dispatcher,
        ) {}
        val overviewProcessor = object : OverviewWorkProcessor {
            override suspend fun work(command: OverviewWorkCommand) = emptyFlow<OverviewWorkResult>()
        }
        val detailsProcessor = object : GoalDetailsWorkProcessor {
            override suspend fun work(command: GoalDetailsWorkCommand) = emptyFlow<
                WorkResult<GoalDetailsAction, GoalDetailsEffect, GoalDetailsOutput>
                >()
        }
        val historyProcessor = object : GoalsHistoryWorkProcessor {
            override suspend fun work(command: GoalsHistoryWorkCommand) = emptyFlow<
                WorkResult<GoalsHistoryAction, GoalsHistoryEffect, GoalsHistoryOutput>
                >()
        }
        return InternalOverviewFeatureComponent.Default(
            startConfig = OverviewConfig.Overview(),
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            outputConsumer = OutputConsumer(outputs::add),
            overviewStoreFactory = OverviewComposeStore.Factory(
                overviewProcessor,
                coroutineManager,
            ),
            goalDetailsStoreFactory = GoalDetailsComposeStore.Factory(
                detailsProcessor,
                coroutineManager,
            ),
            goalsHistoryStoreFactory = GoalsHistoryComposeStore.Factory(
                historyProcessor,
                coroutineManager,
            ),
        )
    }

    private companion object {
        const val GOAL_ID = 42L
    }
}
