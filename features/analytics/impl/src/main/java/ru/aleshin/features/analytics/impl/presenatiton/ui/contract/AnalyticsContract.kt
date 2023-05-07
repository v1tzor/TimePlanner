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
package ru.aleshin.features.analytics.impl.presenatiton.ui.contract

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseAction
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseEvent
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseUiEffect
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseViewState
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsFailure
import ru.aleshin.features.analytics.impl.domain.entities.ScheduleAnalytics

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
@Parcelize
internal data class AnalyticsViewState(
    val timePeriod: TimePeriod? = null,
    val scheduleAnalytics: ScheduleAnalytics? = null,
) : BaseViewState

internal sealed class AnalyticsEvent : BaseEvent {
    data class ChangeTimePeriod(val period: TimePeriod) : AnalyticsEvent()
    object PressRefreshAnalytics : AnalyticsEvent()
}

internal sealed class AnalyticsEffect : BaseUiEffect {
    data class ShowFailure(val failure: AnalyticsFailure) : AnalyticsEffect()
}

internal sealed class AnalyticsAction : BaseAction {
    data class LoadScheduleAnalytics(val analytics: ScheduleAnalytics) : AnalyticsAction()
    data class UpdateTimePeriod(val period: TimePeriod) : AnalyticsAction()
    object RefreshAnalytics : AnalyticsAction()
}
