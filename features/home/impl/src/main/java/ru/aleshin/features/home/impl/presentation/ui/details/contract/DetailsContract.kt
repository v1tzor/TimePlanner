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
package ru.aleshin.features.home.impl.presentation.ui.details.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.presentation.models.schedules.OverviewScheduleUi
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.features.home.api.HomeConfig
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import java.util.Date

/**
 * @author Stanislav Aleshin on 06.11.2023
 */
@Serializable
internal data class DetailsState(
    val isLoading: Boolean = true,
    @Serializable(DateSerializer::class)
    val currentDate: Date? = null,
    val schedules: List<OverviewScheduleUi> = emptyList(),
) : StoreState

internal sealed class DetailsEvent : StoreEvent {
    object Init : DetailsEvent()
    object PressBackButton : DetailsEvent()
    data class OpenSchedule(val schedule: OverviewScheduleUi) : DetailsEvent()
}

internal sealed class DetailsEffect : StoreEffect {
    data class ShowError(val failures: HomeFailures) : DetailsEffect()
}

internal sealed class DetailsAction : StoreAction {
    data class UpdateLoading(val isLoading: Boolean) : DetailsAction()
    data class UpdateSchedules(val date: Date, val schedules: List<OverviewScheduleUi>) : DetailsAction()
}

internal sealed class DetailsOutput : BaseOutput {
    data object NavigateToBack : DetailsOutput()
    data class NavigateToHome(val config: HomeConfig.Home) : DetailsOutput()
}