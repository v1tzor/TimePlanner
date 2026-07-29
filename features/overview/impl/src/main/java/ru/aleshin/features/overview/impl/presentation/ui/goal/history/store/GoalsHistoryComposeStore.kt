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
package ru.aleshin.features.overview.impl.presentation.ui.goal.history.store

import ru.aleshin.core.utils.architecture.component.EmptyInput
import ru.aleshin.core.utils.architecture.store.BaseOnlyOutComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryAction
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryEffect
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryEvent
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryOutput
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal class GoalsHistoryComposeStore @Inject constructor(
    private val workProcessor: GoalsHistoryWorkProcessor,
    stateCommunicator: StateCommunicator<GoalsHistoryState>,
    effectCommunicator: EffectCommunicator<GoalsHistoryEffect>,
    coroutineManager: CoroutineManager,
) : BaseOnlyOutComposeStore<GoalsHistoryState, GoalsHistoryEvent, GoalsHistoryAction, GoalsHistoryEffect, GoalsHistoryOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: EmptyInput, isRestore: Boolean) {
        dispatchEvent(GoalsHistoryEvent.Init)
    }

    override suspend fun WorkScope<GoalsHistoryState, GoalsHistoryAction, GoalsHistoryEffect, GoalsHistoryOutput>.handleEvent(event: GoalsHistoryEvent) {
        when (event) {
            is GoalsHistoryEvent.Init -> {
                launchBackgroundWork(BackgroundKey.LOAD) {
                    val command = GoalsHistoryWorkCommand.LoadFirstPage
                    workProcessor.work(command).collectAndHandleWork()
                }
            }
            is GoalsHistoryEvent.LoadMore -> with(state) {
                val lastItem = history.lastOrNull()
                if (canLoadMore && !isLoadingMore && lastItem != null) {
                    launchBackgroundWork(BackgroundKey.LOAD_MORE) {
                        val command = GoalsHistoryWorkCommand.LoadNextPage(
                            currentHistory = history,
                            beforePeriodEnd = lastItem.periodEnd.time,
                            beforeId = lastItem.id,
                        )
                        workProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is GoalsHistoryEvent.PressBack -> {
                consumeOutput(GoalsHistoryOutput.NavigateBack)
            }
        }
    }

    override suspend fun reduce(
        action: GoalsHistoryAction,
        currentState: GoalsHistoryState,
    ) = when (action) {
        is GoalsHistoryAction.UpdateHistory -> currentState.copy(
            history = action.history,
            isLoading = action.isLoading,
            isLoadingMore = action.isLoadingMore,
            canLoadMore = action.canLoadMore,
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD,
        LOAD_MORE,
    }

    class Factory @Inject constructor(
        private val workProcessor: GoalsHistoryWorkProcessor,
        private val coroutineManager: CoroutineManager,
    ) : BaseOnlyOutComposeStore.Factory<GoalsHistoryComposeStore, GoalsHistoryState> {

        override fun create(savedState: GoalsHistoryState): GoalsHistoryComposeStore {
            return GoalsHistoryComposeStore(
                workProcessor = workProcessor,
                stateCommunicator = StateCommunicator.Default(savedState),
                effectCommunicator = EffectCommunicator.Default(),
                coroutineManager = coroutineManager,
            )
        }
    }
}
