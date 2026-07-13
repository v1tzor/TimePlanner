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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.store

import ru.aleshin.core.utils.architecture.component.EmptyInput
import ru.aleshin.core.utils.architecture.component.EmptyOutput
import ru.aleshin.core.utils.architecture.store.BaseOnlyOutComposeStore
import ru.aleshin.core.utils.architecture.store.BaseSimpleComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsAction
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEffect
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
internal class AnalyticsComposeStore @Inject constructor(
    private val analyticsWorkProcessor: AnalyticsWorkProcessor,
    stateCommunicator: StateCommunicator<AnalyticsState>,
    effectCommunicator: EffectCommunicator<AnalyticsEffect>,
    coroutineManager: CoroutineManager,
) : BaseSimpleComposeStore<AnalyticsState, AnalyticsEvent, AnalyticsAction, AnalyticsEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: EmptyInput, isRestore: Boolean) {
        dispatchEvent(AnalyticsEvent.Init)
    }

    override suspend fun WorkScope<AnalyticsState, AnalyticsAction, AnalyticsEffect, EmptyOutput>.handleEvent(
        event: AnalyticsEvent,
    ) {
        when (event) {
            is AnalyticsEvent.Init -> {
                launchBackgroundWork(BackgroundKey.LOAD_ANALYTICS) {
                    val settingsCommand = AnalyticsWorkCommand.LoadSettings
                    analyticsWorkProcessor.work(settingsCommand).collectAndHandleWork()
                    val analyticsCommand = AnalyticsWorkCommand.LoadAnalytics(checkNotNull(state().timePeriod))
                    analyticsWorkProcessor.work(analyticsCommand).collectAndHandleWork()
                }
            }
            is AnalyticsEvent.ChangeTimePeriod -> {
                launchBackgroundWork(BackgroundKey.UPDATE_TIME_PERIOD) {
                    val periodCommand = AnalyticsWorkCommand.UpdateTimePeriod(event.period)
                    analyticsWorkProcessor.work(periodCommand).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_ANALYTICS) {
                    val analyticsCommand = AnalyticsWorkCommand.LoadAnalytics(event.period)
                    analyticsWorkProcessor.work(analyticsCommand).collectAndHandleWork()
                }
            }
        }
    }

    override suspend fun reduce(
        action: AnalyticsAction,
        currentState: AnalyticsState,
    ) = when (action) {
        is AnalyticsAction.UpdateLoading -> currentState.copy(
            isLoading = action.isLoading,
        )
        is AnalyticsAction.UpdateAnalytics -> currentState.copy(
            scheduleAnalytics = action.analytics,
            isLoading = false,
        )
        is AnalyticsAction.UpdateTimePeriod -> currentState.copy(
            timePeriod = action.period,
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_SETTING, LOAD_ANALYTICS, UPDATE_TIME_PERIOD
    }

     class Factory @Inject constructor(
         private val workProcessor: AnalyticsWorkProcessor,
         private val coroutineManager: CoroutineManager,
     ) : BaseOnlyOutComposeStore.Factory<AnalyticsComposeStore, AnalyticsState> {

         override fun create(savedState: AnalyticsState): AnalyticsComposeStore {
             return AnalyticsComposeStore(
                 analyticsWorkProcessor = workProcessor,
                 stateCommunicator = StateCommunicator.Default(savedState),
                 effectCommunicator = EffectCommunicator.Default(),
                 coroutineManager = coroutineManager,
             )
         }
     }
}