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
 * imitations under the License.
 */
package ru.aleshin.features.home.impl.presentation.ui.home.contract

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.platform.screenmodel.contract.*
import ru.aleshin.features.home.api.domains.entities.schedules.status.DailyScheduleStatus
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.presentation.models.ScheduleUi
import ru.aleshin.features.home.impl.presentation.models.TimeTaskUi
import ru.aleshin.features.home.impl.presentation.ui.home.views.ViewToggleStatus
import java.util.*

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Parcelize
internal data class HomeViewState(
    val currentDate: Date? = null,
    val dateStatus: DailyScheduleStatus? = null,
    val timeTaskViewStatus: ViewToggleStatus = ViewToggleStatus.COMPACT,
    val timeTasks: List<TimeTaskUi> = emptyList(),
    val isLoadingContent: Boolean = true,
) : BaseViewState

internal sealed class HomeEvent : BaseEvent {
    object CreateSchedule : HomeEvent()
    data class TimeTaskShiftUp(val timeTask: TimeTaskUi) : HomeEvent()
    data class TimeTaskShiftDown(val timeTask: TimeTaskUi) : HomeEvent()
    data class LoadSchedule(val date: Date?) : HomeEvent()
    data class PressEditTimeTaskButton(val timeTask: TimeTaskUi) : HomeEvent()
    data class PressAddTimeTaskButton(val startTime: Date, val endTime: Date) : HomeEvent()
    data class PressViewToggleButton(val status: ViewToggleStatus) : HomeEvent()
}

internal sealed class HomeEffect : BaseUiEffect {
    data class ShowError(val failures: HomeFailures) : HomeEffect()
}

internal sealed class HomeAction : BaseAction {
    object Navigate : HomeAction()
    object ShowContentLoading : HomeAction()
    data class UpdateSchedule(val schedule: ScheduleUi) : HomeAction()
    data class UpdateViewStatus(val status: ViewToggleStatus) : HomeAction()
    data class UpdateDate(val date: Date, val status: DailyScheduleStatus?) : HomeAction()
}
