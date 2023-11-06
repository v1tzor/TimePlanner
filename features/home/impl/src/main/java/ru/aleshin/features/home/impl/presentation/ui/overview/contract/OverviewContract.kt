/*
 * Copyright 2023 Stanislav Aleshin
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
package ru.aleshin.features.home.impl.presentation.ui.overview.contract

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseAction
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseEvent
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseUiEffect
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseViewState
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.home.impl.presentation.models.schedules.ScheduleUi
import ru.aleshin.features.home.impl.presentation.models.schedules.UndefinedTaskUi
import java.util.Date

/**
 * @author Stanislav Aleshin on 02.11.2023
 */
@Parcelize
internal data class OverviewViewState(
    val isLoading: Boolean = true,
    val currentDate: Date? = null,
    val currentSchedule: ScheduleUi? = null,
    val schedules: List<ScheduleUi> = emptyList(),
    val categories: List<CategoriesUi> = emptyList(),
    val undefinedTasks: List<UndefinedTaskUi> = emptyList(),
) : BaseViewState

internal sealed class OverviewEvent : BaseEvent {
    object Init : OverviewEvent()
    object Refresh : OverviewEvent()
    object PressScheduleButton : OverviewEvent()
    object OpenAllSchedules : OverviewEvent()
    data class OpenSchedule(val scheduleDate: Date?) : OverviewEvent()
    data class CreateOrUpdateUndefinedTask(val task: UndefinedTaskUi) : OverviewEvent()
    data class ExecuteUndefinedTask(val scheduleDate: Date, val task: UndefinedTaskUi) : OverviewEvent()
    data class DeleteUndefinedTask(val task: UndefinedTaskUi) : OverviewEvent()
}

internal sealed class OverviewEffect : BaseUiEffect {
    data class ShowError(val failures: HomeFailures) : OverviewEffect()
}

internal sealed class OverviewAction : BaseAction {
    object Navigate : OverviewAction()
    data class UpdateLoading(val isLoading: Boolean) : OverviewAction()
    data class UpdateSchedules(val date: Date, val schedules: List<ScheduleUi>) : OverviewAction()
    data class UpdateUndefinedTasks(val tasks: List<UndefinedTaskUi>) : OverviewAction()
    data class UpdateCategories(val categories: List<CategoriesUi>) : OverviewAction()
}
