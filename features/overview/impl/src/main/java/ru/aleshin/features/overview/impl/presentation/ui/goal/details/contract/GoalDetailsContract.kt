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
package ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.features.overview.impl.domain.entities.OverviewFailures
import ru.aleshin.features.overview.impl.presentation.models.GoalDetailsUi
import ru.aleshin.features.overview.impl.presentation.models.GoalUi

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Serializable
internal data class GoalDetailsState(
    val isLoading: Boolean = true,
    val details: GoalDetailsUi? = null,
) : StoreState

internal sealed interface GoalDetailsEvent : StoreEvent {
    data class Init(val input: GoalDetailsInput, val isRestore: Boolean) : GoalDetailsEvent
    data object PressBack : GoalDetailsEvent
    data object PressEdit : GoalDetailsEvent
    data class PressTask(val task: TimeTaskUi) : GoalDetailsEvent
    data object DeleteGoal : GoalDetailsEvent
    data class RestoreGoal(val goal: GoalUi) : GoalDetailsEvent
}

internal sealed interface GoalDetailsAction : StoreAction {
    data class UpdateDetails(val details: GoalDetailsUi?, val isLoading: Boolean) : GoalDetailsAction
}

internal sealed interface GoalDetailsEffect : StoreEffect {
    data class ShowError(val failure: OverviewFailures) : GoalDetailsEffect
    data class ShowGoalDeleted(val goal: GoalUi) : GoalDetailsEffect
}

internal sealed interface GoalDetailsOutput : BaseOutput {
    data object NavigateBack : GoalDetailsOutput
    data class NavigateToGoalEditor(val goalId: Long) : GoalDetailsOutput
    data class NavigateToTaskEditor(val taskId: Long) : GoalDetailsOutput
}

internal data class GoalDetailsInput(
    val goalId: Long,
) : BaseInput
