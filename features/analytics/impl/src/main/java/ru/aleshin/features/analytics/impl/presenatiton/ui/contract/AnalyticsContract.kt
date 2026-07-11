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
package ru.aleshin.features.analytics.impl.presenatiton.ui.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsFailure
import ru.aleshin.features.analytics.impl.presenatiton.models.analytics.ScheduleAnalyticsUi

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
@Serializable
internal data class AnalyticsState(
    val isLoading: Boolean = true,
    val timePeriod: TimePeriod? = null,
    val scheduleAnalytics: ScheduleAnalyticsUi? = null,
) : StoreState

internal sealed class AnalyticsEvent : StoreEvent {
    data object Init : AnalyticsEvent()
    data class ChangeTimePeriod(val period: TimePeriod) : AnalyticsEvent()
}

internal sealed class AnalyticsEffect : StoreEffect {
    data class ShowFailure(val failure: AnalyticsFailure) : AnalyticsEffect()
}

internal sealed class AnalyticsAction : StoreAction {
    data class UpdateAnalytics(val analytics: ScheduleAnalyticsUi) : AnalyticsAction()
    data class UpdateTimePeriod(val period: TimePeriod) : AnalyticsAction()
    data class UpdateLoading(val isLoading: Boolean) : AnalyticsAction()
}
