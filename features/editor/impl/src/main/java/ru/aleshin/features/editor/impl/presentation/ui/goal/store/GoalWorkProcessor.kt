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
package ru.aleshin.features.editor.impl.presentation.ui.goal.store

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.OutputResult
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.features.editor.impl.domain.interactors.GoalInteractor
import ru.aleshin.features.editor.impl.presentation.mappers.mapToDomain
import ru.aleshin.features.editor.impl.presentation.mappers.mapToEditUi
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalEditUi
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalAction
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalEffect
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal interface GoalWorkProcessor : FlowWorkProcessor<GoalWorkCommand, GoalAction, GoalEffect, GoalOutput> {

    class Base @Inject constructor(
        private val goalInteractor: GoalInteractor,
    ) : GoalWorkProcessor {

        override suspend fun work(command: GoalWorkCommand) = when (command) {
            is GoalWorkCommand.SetupEditModel -> setupEditModelWork(command.goalId)
            is GoalWorkCommand.SaveGoal -> saveGoalWork(command.goal)
        }

        private fun setupEditModelWork(goalId: Long?) = flow {
            goalInteractor.fetchGoalEditorData(goalId).collectAndHandle(
                onLeftAction = { failure ->
                    emit(EffectResult(GoalEffect.ShowError(failure)))
                },
                onRightAction = { data ->
                    val action = GoalAction.SetupEditor(
                        editModel = data.goal.mapToEditUi(),
                        categories = data.categories.map { details -> details.mapToUi() },
                        isLoading = false,
                    )
                    emit(ActionResult(action))
                },
            )
        }.onStart {
            emit(ActionResult(GoalAction.SetupEditor(editModel = null, categories = emptyList(), isLoading = true)))
        }

        private fun saveGoalWork(goal: GoalEditUi) = flow {
            goalInteractor.saveGoal(goal.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(GoalEffect.ShowError(it))) },
                onRightAction = { emit(OutputResult(GoalOutput.NavigateBack)) },
            )
        }
    }
}

internal sealed interface GoalWorkCommand : WorkCommand {
    data class SetupEditModel(val goalId: Long?) : GoalWorkCommand
    data class SaveGoal(val goal: GoalEditUi) : GoalWorkCommand
}
