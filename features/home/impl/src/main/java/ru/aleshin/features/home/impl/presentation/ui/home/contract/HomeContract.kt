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
package ru.aleshin.features.home.impl.presentation.ui.home.contract

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.ui.views.CalendarButtonBehavior
import ru.aleshin.core.ui.views.ViewToggleStatus
import ru.aleshin.core.utils.platform.screenmodel.contract.*
import ru.aleshin.features.home.api.domain.entities.schedules.DailyScheduleStatus
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.presentation.models.schedules.ScheduleUi
import ru.aleshin.features.home.impl.presentation.models.schedules.TimeTaskUi
import ru.aleshin.features.settings.api.domain.entities.TasksSettings
import java.util.*

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Parcelize
internal data class HomeViewState(
    val isLoading: Boolean = true,
    val currentDate: Date? = null,
    val dateStatus: DailyScheduleStatus? = null,
    val taskViewStatus: ViewToggleStatus = ViewToggleStatus.COMPACT,
    val calendarButtonBehavior: CalendarButtonBehavior = CalendarButtonBehavior.SET_CURRENT_DATE,
    val timeTasks: List<TimeTaskUi> = emptyList(),
) : BaseViewState

internal sealed class HomeEvent : BaseEvent {
    object Init : HomeEvent()
    object CreateSchedule : HomeEvent()
    object PressOverviewButton : HomeEvent()
    data class LoadSchedule(val date: Date?) : HomeEvent()
    data class PressAddTimeTaskButton(val startTime: Date, val endTime: Date) : HomeEvent()
    data class PressEditTimeTaskButton(val timeTask: TimeTaskUi) : HomeEvent()
    data class ChangeTaskDoneStateButton(val timeTask: TimeTaskUi) : HomeEvent()
    data class TimeTaskShiftUp(val timeTask: TimeTaskUi) : HomeEvent()
    data class TimeTaskShiftDown(val timeTask: TimeTaskUi) : HomeEvent()
    data class PressViewToggleButton(val status: ViewToggleStatus) : HomeEvent()
}

internal sealed class HomeEffect : BaseUiEffect {
    data class ShowError(val failures: HomeFailures) : HomeEffect()
}

internal sealed class HomeAction : BaseAction {
    object Navigate : HomeAction()
    data class SetupSettings(val settings: TasksSettings) : HomeAction()
    data class UpdateDate(val date: Date) : HomeAction()
    data class UpdateSchedule(val schedule: ScheduleUi) : HomeAction()
    data class SetEmptySchedule(val date: Date, val status: DailyScheduleStatus?) : HomeAction()
}
