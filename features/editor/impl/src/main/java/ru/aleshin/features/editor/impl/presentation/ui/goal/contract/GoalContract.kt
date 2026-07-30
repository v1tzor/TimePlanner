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
package ru.aleshin.features.editor.impl.presentation.ui.goal.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalScopeType
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalEditUi
import ru.aleshin.features.editor.impl.presentation.ui.goal.validators.GoalValidationError
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Serializable
internal data class GoalState(
    val isLoading: Boolean = true,
    val editModel: GoalEditUi? = null,
    val categories: List<MainCategoryDetailsUi> = emptyList(),
    val validationErrors: Set<GoalValidationError> = emptySet(),
) : StoreState

internal sealed interface GoalEvent : StoreEvent {
    data class Init(val input: GoalInput, val isRestore: Boolean) : GoalEvent
    data class ChangeTitle(val title: String) : GoalEvent
    data class ChangeScope(val scopeType: GoalScopeType) : GoalEvent
    data class ChangeMainCategory(val category: MainCategoryUi) : GoalEvent
    data class ChangeSubCategory(val subCategory: SubCategoryUi?) : GoalEvent
    data class ChangeMetric(val metric: GoalMetric) : GoalEvent
    data class ChangeDirection(val direction: GoalDirection) : GoalEvent
    data class ChangeTargetValue(val targetValue: String) : GoalEvent
    data class ChangeDeadline(val deadline: Date) : GoalEvent
    data object PressSave : GoalEvent
    data object PressBack : GoalEvent
}

internal sealed interface GoalAction : StoreAction {
    data class SetupEditor(
        val editModel: GoalEditUi?,
        val categories: List<MainCategoryDetailsUi>,
        val isLoading: Boolean,
    ) : GoalAction
    data class UpdateEditModel(val editModel: GoalEditUi) : GoalAction
    data class UpdateValidation(val errors: Set<GoalValidationError>) : GoalAction
}

internal sealed interface GoalEffect : StoreEffect {
    data class ShowError(val failure: EditorFailures) : GoalEffect
}

internal sealed interface GoalOutput : BaseOutput {
    data object NavigateBack : GoalOutput
}

internal data class GoalInput(
    val goalId: Long?,
) : BaseInput
