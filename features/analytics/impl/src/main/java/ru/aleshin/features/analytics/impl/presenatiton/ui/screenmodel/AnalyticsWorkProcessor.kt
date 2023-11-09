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

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.functional.rightOrError
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.EffectResult
import ru.aleshin.core.utils.platform.screenmodel.work.FlowWorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.features.analytics.impl.domain.interactors.AnalyticsInteractor
import ru.aleshin.features.analytics.impl.domain.interactors.SettingsInteractor
import ru.aleshin.features.analytics.impl.presenatiton.mappers.mapToUi
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsAction
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsEffect
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 22.04.2023.
 */
internal interface AnalyticsWorkProcessor : FlowWorkProcessor<AnalyticsWorkCommand, AnalyticsAction, AnalyticsEffect> {

    class Base @Inject constructor(
        private val analyticsInteractor: AnalyticsInteractor,
        private val settingsInteractor: SettingsInteractor,
    ) : AnalyticsWorkProcessor {

        override suspend fun work(command: AnalyticsWorkCommand) = when (command) {
            is AnalyticsWorkCommand.LoadSettings -> loadSettingWork()
            is AnalyticsWorkCommand.UpdateTimePeriod -> updateTimePeriodWork(command.period)
            is AnalyticsWorkCommand.LoadAnalytics -> loadAnalyticsWork(command.period)
        }

        private fun loadSettingWork() = flow {
            settingsInteractor.fetchTasksSettings().handle(
                onLeftAction = { emit(EffectResult(AnalyticsEffect.ShowFailure(it))) },
                onRightAction = { settings ->
                    emit(ActionResult(AnalyticsAction.UpdateTimePeriod(settings.taskAnalyticsRange)))
                },
            )
        }

        private fun updateTimePeriodWork(period: TimePeriod) = flow {
            val oldSettings = settingsInteractor.fetchTasksSettings().rightOrError("Error get tasks settings")
            val newSettings = oldSettings.copy(taskAnalyticsRange = period)
            settingsInteractor.updateTasksSettings(newSettings).handle(
                onLeftAction = { emit(EffectResult(AnalyticsEffect.ShowFailure(it))) },
                onRightAction = { emit(ActionResult(AnalyticsAction.UpdateTimePeriod(period))) },
            )
        }

        private fun loadAnalyticsWork(period: TimePeriod) = flow {
            emit(ActionResult(AnalyticsAction.UpdateLoading(true)))
            delay(Constants.Delay.LOAD_ANIMATION)
            analyticsInteractor.fetchAnalytics(period).handle(
                onLeftAction = { emit(EffectResult(AnalyticsEffect.ShowFailure(it))) },
                onRightAction = { analytics ->
                    emit(ActionResult(AnalyticsAction.UpdateAnalytics(analytics.mapToUi())))
                },
            )
        }
    }
}

internal sealed class AnalyticsWorkCommand : WorkCommand {
    object LoadSettings : AnalyticsWorkCommand()
    data class UpdateTimePeriod(val period: TimePeriod) : AnalyticsWorkCommand()
    data class LoadAnalytics(val period: TimePeriod) : AnalyticsWorkCommand()
}
