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
package ru.aleshin.features.analytics.impl.presenatiton.ui.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.features.analytics.impl.di.holder.AnalyticsComponentHolder
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsAction
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsEffect
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsViewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
internal class AnalyticsScreenModel @Inject constructor(
    private val analyticsWorkProcessor: AnalyticsWorkProcessor,
    stateCommunicator: AnalyticsStateCommunicator,
    effectCommunicator: AnalyticsEffectCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<AnalyticsViewState, AnalyticsEvent, AnalyticsAction, AnalyticsEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun init() {
        if (!isInitialize.get()) {
            dispatchEvent(AnalyticsEvent.ChangeTimePeriod(TimePeriod.WEEK))
            super.init()
        }
    }

    override suspend fun WorkScope<AnalyticsViewState, AnalyticsAction, AnalyticsEffect>.handleEvent(
        event: AnalyticsEvent,
    ) = when (event) {
        is AnalyticsEvent.ChangeTimePeriod -> {
            val workCommand = AnalyticsWorkCommand.LoadAnalytics(event.period)

            sendAction(AnalyticsAction.UpdateTimePeriod(event.period))
            analyticsWorkProcessor.work(workCommand).collectAndHandleWork()
        }

        is AnalyticsEvent.PressRefreshAnalytics -> {
            val timePeriod = checkNotNull(state().timePeriod)
            val workCommand = AnalyticsWorkCommand.LoadAnalytics(timePeriod)

            analyticsWorkProcessor.work(workCommand).collectAndHandleWork()
        }
    }

    override suspend fun reduce(
        action: AnalyticsAction,
        currentState: AnalyticsViewState,
    ) = when (action) {
        is AnalyticsAction.UpdateTimePeriod -> currentState.copy(
            timePeriod = action.period,
        )
        is AnalyticsAction.RefreshAnalytics -> currentState.copy(
            scheduleAnalytics = null,
        )
        is AnalyticsAction.LoadScheduleAnalytics -> currentState.copy(
            scheduleAnalytics = action.analytics,
        )
    }

    override fun onDispose() {
        super.onDispose()
        AnalyticsComponentHolder.clear()
    }
}

@Composable
internal fun Screen.rememberAnalyticsScreenModel(): AnalyticsScreenModel {
    val component = AnalyticsComponentHolder.fetchComponent()
    return rememberScreenModel { component.fetchAnalyticsScreenModel() }
}
