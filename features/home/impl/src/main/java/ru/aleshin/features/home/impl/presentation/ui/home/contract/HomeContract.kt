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
package ru.aleshin.features.home.impl.presentation.ui.home.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.settings.CalendarButtonBehavior
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.core.domain.entities.settings.TasksSettings
import ru.aleshin.core.domain.entities.settings.ViewToggleStatus
import ru.aleshin.core.presentation.models.schedules.ScheduleDetailsUi
import ru.aleshin.core.presentation.models.tasks.TimeTaskDetailsUi
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.presentation.models.TimelineScheduleUi
import java.util.Date

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Serializable
internal data class HomeState(
    @Serializable(DateSerializer::class)
    val selectedDate: Date? = null,
    @Serializable(DateSerializer::class)
    val currentTime: Date? = null,
    val schedule: ScheduleDetailsUi? = null,
    val timelineSchedule: TimelineScheduleUi? = null,
    val homeViewMode: HomeViewMode = HomeViewMode.AGENDA,
    val taskViewStatus: ViewToggleStatus = ViewToggleStatus.COMPACT,
    val calendarButtonBehavior: CalendarButtonBehavior = CalendarButtonBehavior.SET_CURRENT_DATE,
) : StoreState

internal sealed class HomeEvent : StoreEvent {
    data class Init(val input: HomeInput, val isRestore: Boolean) : HomeEvent()
    data object CreateSchedule : HomeEvent()
    data object PressSettingsButton : HomeEvent()
    data object SelectedCurrentDate : HomeEvent()
    data class LoadSchedule(val date: Date?) : HomeEvent()
    data class PressAddTimeTaskButton(val startTime: Date, val endTime: Date) : HomeEvent()
    data object PressAddTimeTaskFab : HomeEvent()
    data class PressEditTimeTaskButton(val timeTask: TimeTaskDetailsUi) : HomeEvent()
    data class PressEditTimelineTimeTaskButton(val timeTaskId: Long) : HomeEvent()
    data class ChangeTaskDoneStateButton(val timeTask: TimeTaskDetailsUi) : HomeEvent()
    data class ChangeTimelineTaskDoneStateButton(val timeTask: TimeTaskUi) : HomeEvent()
    data class TimeTaskShiftUp(val timeTask: TimeTaskDetailsUi) : HomeEvent()
    data class TimeTaskShiftDown(val timeTask: TimeTaskDetailsUi) : HomeEvent()
    data class PressViewToggleButton(val status: ViewToggleStatus) : HomeEvent()
    data class ChangeHomeViewMode(val mode: HomeViewMode) : HomeEvent()
    data class UpdateTimelineTimeTask(val timeTaskId: Long, val timeRange: TimeRange) : HomeEvent()
}

internal sealed class HomeEffect : StoreEffect {
    data class ShowError(val failures: HomeFailures) : HomeEffect()
}

internal sealed class HomeAction : StoreAction {
    data class UpdateSettings(val settings: TasksSettings) : HomeAction()
    data class UpdateCurrentTime(val currentTime: Date) : HomeAction()
    data class UpdateSchedule(
        val date: Date,
        val schedule: ScheduleDetailsUi?,
        val timelineSchedule: TimelineScheduleUi,
    ) : HomeAction()
}

internal sealed class HomeOutput : BaseOutput {
    data object NavigateToSettings : HomeOutput()
    data class NavigateToEditor(val config: EditorConfig.Task) : HomeOutput()
}

internal data class HomeInput(val scheduleDate: Date? = null) : BaseInput
