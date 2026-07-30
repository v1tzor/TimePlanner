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

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.features.overview.impl.domain.interactors.GoalsInteractor
import ru.aleshin.features.overview.impl.presentation.mapppers.mapToDomain
import ru.aleshin.features.overview.impl.presentation.mapppers.mapToUi
import ru.aleshin.features.overview.impl.presentation.models.GoalUi
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsAction
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsEffect
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal interface GoalDetailsWorkProcessor :
    FlowWorkProcessor<GoalDetailsWorkCommand, GoalDetailsAction, GoalDetailsEffect, GoalDetailsOutput> {

    class Base @Inject constructor(
        private val goalsInteractor: GoalsInteractor,
    ) : GoalDetailsWorkProcessor {

        override suspend fun work(command: GoalDetailsWorkCommand) = when (command) {
            is GoalDetailsWorkCommand.LoadGoal -> loadWork(command.goalId)
            is GoalDetailsWorkCommand.DeleteGoal -> deleteWork(command.goal)
            is GoalDetailsWorkCommand.RestoreGoal -> restoreWork(command.goal)
        }

        private fun loadWork(goalId: Long) = flow {
            goalsInteractor.fetchGoalDetails(goalId).collectAndHandle(
                onLeftAction = { failure ->
                    emit(EffectResult(GoalDetailsEffect.ShowError(failure)))
                },
                onRightAction = { details ->
                    emit(ActionResult(GoalDetailsAction.UpdateDetails(details = details?.mapToUi(), isLoading = false)))
                },
            )
        }.onStart {
            emit(ActionResult(GoalDetailsAction.UpdateDetails(null, true)))
        }

        private fun deleteWork(goal: GoalUi) = flow {
            goalsInteractor.deleteGoalById(goal.id).handle(
                onLeftAction = { failure ->
                    emit(EffectResult(GoalDetailsEffect.ShowError(failure)))
                },
                onRightAction = {
                    emit(EffectResult(GoalDetailsEffect.ShowGoalDeleted(goal)))
                },
            )
        }

        private fun restoreWork(goal: GoalUi) = flow {
            goalsInteractor.restoreGoal(goal.mapToDomain()).handle(
                onLeftAction = { failure ->
                    emit(EffectResult(GoalDetailsEffect.ShowError(failure)))
                },
            )
        }
    }
}

internal sealed interface GoalDetailsWorkCommand : WorkCommand {
    data class LoadGoal(val goalId: Long) : GoalDetailsWorkCommand
    data class DeleteGoal(val goal: GoalUi) : GoalDetailsWorkCommand
    data class RestoreGoal(val goal: GoalUi) : GoalDetailsWorkCommand
}
