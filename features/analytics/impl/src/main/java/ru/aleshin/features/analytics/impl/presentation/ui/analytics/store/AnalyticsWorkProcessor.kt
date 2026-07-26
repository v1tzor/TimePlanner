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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.functional.handleAndGet
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsRangeCalculator
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCategorySort
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.interactors.AnalyticsOverviewInteractor
import ru.aleshin.features.analytics.impl.domain.interactors.AnalyticsRangeInteractor
import ru.aleshin.features.analytics.impl.presentation.mappers.mapToUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsAction
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEffect
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface AnalyticsWorkProcessor :
    FlowWorkProcessor<AnalyticsWorkCommand, AnalyticsAction, AnalyticsEffect, AnalyticsOutput> {

    class Base @Inject constructor(
        private val rangeInteractor: AnalyticsRangeInteractor,
        private val overviewInteractor: AnalyticsOverviewInteractor,
        private val rangeCalculator: AnalyticsRangeCalculator,
    ) : AnalyticsWorkProcessor {

        override suspend fun work(command: AnalyticsWorkCommand) = when (command) {
            is AnalyticsWorkCommand.ObserveAnalytics -> observeAnalyticsWork(
                categorySort = command.categorySort,
                currentRange = command.currentRange,
                hasOverview = command.hasOverview,
            )
            is AnalyticsWorkCommand.SelectPeriod -> selectPeriodWork(command.period)
            is AnalyticsWorkCommand.ShiftRange -> shiftRangeWork(command.direction)
            is AnalyticsWorkCommand.MoveToCurrent -> moveToCurrentWork()
            is AnalyticsWorkCommand.ConfirmCustomRange -> confirmCustomRangeWork(
                fromPickerToken = command.fromPickerToken,
                toPickerToken = command.toPickerToken,
            )
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        private suspend fun observeAnalyticsWork(
            categorySort: AnalyticsCategorySort,
            currentRange: AnalyticsRangeUi?,
            hasOverview: Boolean,
        ): Flow<AnalyticsWorkResult> {
            var displayedRange = currentRange
            var isOverviewAvailable = hasOverview
            return rangeInteractor.fetchRangeSelection().flatMapLatest { result ->
                result.handleAndGet(
                    onLeftAction = { failure ->
                        flow {
                            emit(ActionResult(AnalyticsAction.UpdateLoading(isLoading = false, isError = !isOverviewAvailable)))
                            emit(EffectResult(AnalyticsEffect.ShowFailure(failure)))
                        }
                    },
                    onRightAction = { selection ->
                        val range = selection.mapToUi()
                        val shouldSetupRange = displayedRange != range || !isOverviewAvailable
                        displayedRange = range
                        flow {
                            if (shouldSetupRange) {
                                isOverviewAvailable = false
                                emit(ActionResult(AnalyticsAction.SetupRange(range = range)))
                            }
                            overviewInteractor.fetchOverview(selection = selection, categorySort = categorySort).collectAndHandle(
                                onLeftAction = { failure ->
                                    emit(ActionResult(AnalyticsAction.UpdateLoading(isLoading = false, isError = !isOverviewAvailable)))
                                    emit(EffectResult(AnalyticsEffect.ShowFailure(failure)))
                                },
                                onRightAction = { overview ->
                                    isOverviewAvailable = true
                                    val overview = overview.mapToUi()
                                    emit(ActionResult(AnalyticsAction.UpdateAnalytics(categorySort = categorySort, overview = overview)))
                                },
                            )
                        }
                    },
                )
            }
        }

        private fun selectPeriodWork(period: TimePeriod) = flow {
            rangeInteractor.selectPeriod(period).handle(
                onLeftAction = { emit(EffectResult(AnalyticsEffect.ShowFailure(it))) },
            )
        }

        private fun shiftRangeWork(direction: Int) = flow {
            rangeInteractor.shiftRange(direction).handle(
                onLeftAction = { emit(EffectResult(AnalyticsEffect.ShowFailure(it))) }
            )
        }

        private fun moveToCurrentWork() = flow {
            rangeInteractor.moveToCurrent().handle(
                onLeftAction = { emit(EffectResult(AnalyticsEffect.ShowFailure(it))) }
            )
        }

        private fun confirmCustomRangeWork(fromPickerToken: Long, toPickerToken: Long) = flow {
            val range = AnalyticsCivilDateRange(
                from = rangeCalculator.pickerTokenToCivilToken(fromPickerToken),
                to = rangeCalculator.pickerTokenToCivilToken(toPickerToken),
            )

            rangeInteractor.confirmCustomRange(range).handle(
                onLeftAction = { emit(EffectResult(AnalyticsEffect.ShowFailure(it))) }
            )
        }
    }
}

internal sealed class AnalyticsWorkCommand : WorkCommand {
    data class ObserveAnalytics(
        val categorySort: AnalyticsCategorySort,
        val currentRange: AnalyticsRangeUi?,
        val hasOverview: Boolean,
    ) : AnalyticsWorkCommand()
    data class SelectPeriod(val period: TimePeriod) : AnalyticsWorkCommand()
    data class ShiftRange(val direction: Int) : AnalyticsWorkCommand()
    data object MoveToCurrent : AnalyticsWorkCommand()
    data class ConfirmCustomRange(val fromPickerToken: Long, val toPickerToken: Long) : AnalyticsWorkCommand()
}

internal typealias AnalyticsWorkResult = WorkResult<AnalyticsAction, AnalyticsEffect, AnalyticsOutput>
