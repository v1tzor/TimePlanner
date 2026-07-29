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
package ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.features.overview.impl.domain.entities.OverviewFailures
import ru.aleshin.features.overview.impl.presentation.models.GoalHistoryUi

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Serializable
internal data class GoalsHistoryState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = true,
    val history: List<GoalHistoryUi> = emptyList(),
) : StoreState

internal sealed interface GoalsHistoryEvent : StoreEvent {
    data object Init : GoalsHistoryEvent
    data object PressBack : GoalsHistoryEvent
    data object LoadMore : GoalsHistoryEvent
}

internal sealed interface GoalsHistoryAction : StoreAction {
    data class UpdateHistory(
        val history: List<GoalHistoryUi>,
        val isLoading: Boolean,
        val isLoadingMore: Boolean,
        val canLoadMore: Boolean,
    ) : GoalsHistoryAction
}

internal sealed interface GoalsHistoryEffect : StoreEffect {
    data class ShowError(val failure: OverviewFailures) : GoalsHistoryEffect
}

internal sealed interface GoalsHistoryOutput : BaseOutput {
    data object NavigateBack : GoalsHistoryOutput
}
