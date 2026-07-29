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

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.features.overview.impl.domain.interactors.GoalsHistoryInteractor
import ru.aleshin.features.overview.impl.domain.interactors.HISTORY_PAGE_SIZE
import ru.aleshin.features.overview.impl.presentation.mapppers.mapToUi
import ru.aleshin.features.overview.impl.presentation.models.GoalHistoryUi
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryAction
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryEffect
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal interface GoalsHistoryWorkProcessor :
    FlowWorkProcessor<GoalsHistoryWorkCommand, GoalsHistoryAction, GoalsHistoryEffect, GoalsHistoryOutput> {

    class Base @Inject constructor(
        private val historyInteractor: GoalsHistoryInteractor,
    ) : GoalsHistoryWorkProcessor {

        override suspend fun work(command: GoalsHistoryWorkCommand) = when (command) {
            is GoalsHistoryWorkCommand.LoadFirstPage -> loadFirstPageWork()
            is GoalsHistoryWorkCommand.LoadNextPage -> loadNextPageWork(
                currentHistory = command.currentHistory,
                beforePeriodEnd = command.beforePeriodEnd,
                beforeId = command.beforeId,
            )
        }

        private fun loadFirstPageWork() = flow {
            historyInteractor.syncCompletedGoals().handle(
                onLeftAction = { failure ->
                    emit(EffectResult(GoalsHistoryEffect.ShowError(failure)))
                },
                onRightAction = {
                    historyInteractor.fetchHistoryPage(null, null).handle(
                        onLeftAction = { failure ->
                            emit(EffectResult(GoalsHistoryEffect.ShowError(failure)))
                        },
                        onRightAction = { history ->
                            val action = GoalsHistoryAction.UpdateHistory(
                                history = history.map { item -> item.mapToUi() },
                                isLoading = false,
                                isLoadingMore = false,
                                canLoadMore = history.size == HISTORY_PAGE_SIZE,
                            )
                            emit(ActionResult(action))
                        },
                    )
                },
            )
        }.onStart {
            val action = GoalsHistoryAction.UpdateHistory(
                history = emptyList(),
                isLoading = true,
                isLoadingMore = false,
                canLoadMore = true,
            )
            emit(ActionResult(action))
        }

        private fun loadNextPageWork(
            currentHistory: List<GoalHistoryUi>,
            beforePeriodEnd: Long,
            beforeId: Long,
        ) = flow {
            historyInteractor.fetchHistoryPage(
                beforePeriodEnd = beforePeriodEnd,
                beforeId = beforeId,
            ).handle(
                onLeftAction = { failure ->
                    emit(EffectResult(GoalsHistoryEffect.ShowError(failure)))
                },
                onRightAction = { history ->
                    val action = GoalsHistoryAction.UpdateHistory(
                        history = currentHistory + history.map { item -> item.mapToUi() },
                        isLoading = false,
                        isLoadingMore = false,
                        canLoadMore = history.size == HISTORY_PAGE_SIZE,
                    )
                    emit(ActionResult(action))
                },
            )
        }.onStart {
            val action = GoalsHistoryAction.UpdateHistory(
                history = currentHistory,
                isLoading = false,
                isLoadingMore = true,
                canLoadMore = true,
            )
            emit(ActionResult(action))
        }
    }
}

internal sealed interface GoalsHistoryWorkCommand : WorkCommand {
    data object LoadFirstPage : GoalsHistoryWorkCommand
    data class LoadNextPage(
        val currentHistory: List<GoalHistoryUi>,
        val beforePeriodEnd: Long,
        val beforeId: Long,
    ) : GoalsHistoryWorkCommand
}
