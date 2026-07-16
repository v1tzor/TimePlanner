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
package ru.aleshin.features.overview.impl.presentation.ui.overview.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.home.api.HomeConfig
import ru.aleshin.features.overview.impl.domain.entities.OverviewFailures
import ru.aleshin.features.overview.impl.presentation.models.overview.WeekOverviewUi
import java.util.Date

/**
 * @author Stanislav Aleshin on 02.11.2023
 */
@Serializable
internal data class OverviewState(
    val isLoading: Boolean = true,
    @Serializable(DateSerializer::class)
    val selectedDate: Date? = null,
    val weekOverview: WeekOverviewUi = WeekOverviewUi(),
    val categories: List<MainCategoryDetailsUi> = emptyList(),
    val undefinedTasks: List<UndefinedTaskUi> = emptyList(),
    val sharedTextTasks: List<UndefinedTaskUi>? = null,
) : StoreState

internal sealed class OverviewEvent : StoreEvent {
    data class Init(val input: OverviewInput, val isRestore: Boolean) : OverviewEvent()
    object Refresh : OverviewEvent()
    data class OpenSchedule(val scheduleDate: Date?) : OverviewEvent()
    data class SelectSchedule(val scheduleDate: Date) : OverviewEvent()
    data class OpenTimeTask(val timeTask: TimeTaskUi) : OverviewEvent()
    data class CreateOrUpdateUndefinedTask(val task: UndefinedTaskUi) : OverviewEvent()
    data class ConfirmBatchUndefinedTasks(val tasks: List<UndefinedTaskUi>) : OverviewEvent()
    data object DismissBatchUndefinedTasks : OverviewEvent()
    data class ExecuteUndefinedTask(val scheduleDate: Date, val task: UndefinedTaskUi) : OverviewEvent()
}

internal sealed class OverviewEffect : StoreEffect {
    data class ShowError(val failures: OverviewFailures) : OverviewEffect()
}

internal sealed class OverviewAction : StoreAction {
    data class UpdateLoading(val isLoading: Boolean) : OverviewAction()
    data class UpdateWeekOverview(val weekOverview: WeekOverviewUi) : OverviewAction()
    data class UpdateSelectedDate(val date: Date) : OverviewAction()
    data class UpdateUndefinedTasks(val tasks: List<UndefinedTaskUi>) : OverviewAction()
    data class UpdateCategories(val categories: List<MainCategoryDetailsUi>) : OverviewAction()
    data class UpdateSharedTextTasks(val tasks: List<UndefinedTaskUi>) : OverviewAction()
    data object ClearSharedTextTasks : OverviewAction()
}


internal sealed class OverviewOutput : BaseOutput {
    data class NavigateToHome(val config: HomeConfig.Home) : OverviewOutput()
    data class NavigateToEditor(val config: EditorConfig.Task) : OverviewOutput()
}


internal data class OverviewInput(
    val sharedText: String?,
) : BaseInput
