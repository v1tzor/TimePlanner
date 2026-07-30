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
package ru.aleshin.features.overview.impl.presentation.ui.goal.details.store

import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsAction
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsEffect
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsEvent
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsInput
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsOutput
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal class GoalDetailsComposeStore @Inject constructor(
    private val workProcessor: GoalDetailsWorkProcessor,
    stateCommunicator: StateCommunicator<GoalDetailsState>,
    effectCommunicator: EffectCommunicator<GoalDetailsEffect>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<GoalDetailsState, GoalDetailsEvent, GoalDetailsAction, GoalDetailsEffect, GoalDetailsInput, GoalDetailsOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: GoalDetailsInput, isRestore: Boolean) {
        dispatchEvent(GoalDetailsEvent.Init(input, isRestore))
    }

    override suspend fun WorkScope<GoalDetailsState, GoalDetailsAction, GoalDetailsEffect, GoalDetailsOutput>.handleEvent(event: GoalDetailsEvent) {
        when (event) {
            is GoalDetailsEvent.Init -> with(event) {
                launchBackgroundWork(BackgroundKey.LOAD) {
                    val command = GoalDetailsWorkCommand.LoadGoal(input.goalId)
                    workProcessor.work(command).collectAndHandleWork()
                }
            }
            is GoalDetailsEvent.DeleteGoal -> with(state) {
                val goal = details?.progress?.goal
                if (goal != null) {
                    launchBackgroundWork(BackgroundKey.MUTATION) {
                        val command = GoalDetailsWorkCommand.DeleteGoal(goal)
                        workProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is GoalDetailsEvent.RestoreGoal -> with(event) {
                launchBackgroundWork(BackgroundKey.MUTATION) {
                    val command = GoalDetailsWorkCommand.RestoreGoal(goal)
                    workProcessor.work(command).collectAndHandleWork()
                }
            }
            is GoalDetailsEvent.PressEdit -> with(state) {
                val goalId = details?.progress?.goal?.id
                if (goalId != null) {
                    val config = EditorConfig.Goal(goalId = goalId)
                    consumeOutput(GoalDetailsOutput.NavigateToGoalEditor(config))
                }
            }
            is GoalDetailsEvent.PressTask -> with(event) {
                val config = EditorConfig.Task(timeTaskId = task.key)
                consumeOutput(GoalDetailsOutput.NavigateToTaskEditor(config))
            }
            is GoalDetailsEvent.PressBack -> {
                consumeOutput(GoalDetailsOutput.NavigateBack)
            }
        }
    }

    override suspend fun reduce(
        action: GoalDetailsAction,
        currentState: GoalDetailsState,
    ) = when (action) {
        is GoalDetailsAction.UpdateDetails -> currentState.copy(
            details = action.details,
            isLoading = action.isLoading,
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD,
        MUTATION,
    }

    class Factory @Inject constructor(
        private val workProcessor: GoalDetailsWorkProcessor,
        private val coroutineManager: CoroutineManager,
    ) : BaseComposeStore.Factory<GoalDetailsComposeStore, GoalDetailsState> {

        override fun create(savedState: GoalDetailsState): GoalDetailsComposeStore {
            return GoalDetailsComposeStore(
                workProcessor = workProcessor,
                stateCommunicator = StateCommunicator.Default(savedState),
                effectCommunicator = EffectCommunicator.Default(),
                coroutineManager = coroutineManager,
            )
        }
    }
}
