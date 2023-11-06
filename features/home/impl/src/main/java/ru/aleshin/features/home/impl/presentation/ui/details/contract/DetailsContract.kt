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
package ru.aleshin.features.home.impl.presentation.ui.details.contract

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseAction
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseEvent
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseUiEffect
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseViewState
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.presentation.models.schedules.ScheduleUi
import java.util.Date

/**
 * @author Stanislav Aleshin on 06.11.2023
 */
@Parcelize
internal data class DetailsViewState(
    val isLoading: Boolean = true,
    val currentSchedule: ScheduleUi? = null,
    val schedules: List<ScheduleUi> = emptyList(),
) : BaseViewState

internal sealed class DetailsEvent : BaseEvent {
    object Init : DetailsEvent()
    object PressBackButton : DetailsEvent()
    data class OpenSchedule(val schedule: ScheduleUi) : DetailsEvent()
}

internal sealed class DetailsEffect : BaseUiEffect {
    data class ShowError(val failures: HomeFailures) : DetailsEffect()
}

internal sealed class DetailsAction : BaseAction {
    data class UpdateSchedules(val date: Date, val schedules: List<ScheduleUi>) : DetailsAction()
    data class UpdateLoading(val isLoading: Boolean) : DetailsAction()
}
