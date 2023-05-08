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
package ru.aleshin.features.analytics.impl.presenatiton.ui.screenmodel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.EffectResult
import ru.aleshin.core.utils.platform.screenmodel.work.FlowWorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.features.analytics.impl.domain.interactors.AnalyticsInteractor
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsAction
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsEffect
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 22.04.2023.
 */
internal interface AnalyticsWorkProcessor : FlowWorkProcessor<AnalyticsWorkCommand, AnalyticsAction, AnalyticsEffect> {

    class Base @Inject constructor(
        private val analyticsInteractor: AnalyticsInteractor,
    ) : AnalyticsWorkProcessor {

        override suspend fun work(command: AnalyticsWorkCommand) = when (command) {
            is AnalyticsWorkCommand.LoadAnalytics -> loadAnalyticsWork(command.period)
        }

        private fun loadAnalyticsWork(period: TimePeriod) = flow {
            emit(ActionResult(AnalyticsAction.RefreshAnalytics))
            delay(Constants.Delay.LOAD_ANIMATION)
            val result = when (val analytics = analyticsInteractor.fetchAnalytics(period)) {
                is Either.Right -> ActionResult(AnalyticsAction.LoadScheduleAnalytics(analytics.data))
                is Either.Left -> EffectResult(AnalyticsEffect.ShowFailure(analytics.data))
            }
            emit(result)
        }
    }
}

internal sealed class AnalyticsWorkCommand : WorkCommand {
    data class LoadAnalytics(val period: TimePeriod) : AnalyticsWorkCommand()
}
