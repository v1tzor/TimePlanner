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
package ru.aleshin.features.analytics.impl.presentation.ui.category.store

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
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.interactors.AnalyticsRangeInteractor
import ru.aleshin.features.analytics.impl.domain.interactors.CategoryAnalyticsInteractor
import ru.aleshin.features.analytics.impl.presentation.mappers.mapToUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryAction
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEffect
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface CategoryWorkProcessor :
    FlowWorkProcessor<CategoryWorkCommand, CategoryAction, CategoryEffect, CategoryOutput> {

    class Base @Inject constructor(
        private val rangeInteractor: AnalyticsRangeInteractor,
        private val categoryInteractor: CategoryAnalyticsInteractor,
        private val rangeCalculator: AnalyticsRangeCalculator,
    ) : CategoryWorkProcessor {

        override suspend fun work(command: CategoryWorkCommand) = when (command) {
            is CategoryWorkCommand.ObserveAnalytics -> observeAnalyticsWork(
                mainCategoryId = command.mainCategoryId,
                currentRange = command.currentRange,
                hasAnalytics = command.hasAnalytics,
            )
            is CategoryWorkCommand.SelectPeriod -> selectPeriodWork(command.period)
            is CategoryWorkCommand.ShiftRange -> shiftRangeWork(command.direction)
            is CategoryWorkCommand.MoveToCurrent -> moveToCurrentWork()
            is CategoryWorkCommand.ConfirmCustomRange -> confirmCustomRangeWork(
                fromPickerToken = command.fromPickerToken,
                toPickerToken = command.toPickerToken,
            )
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        private suspend fun observeAnalyticsWork(
            mainCategoryId: Long,
            currentRange: AnalyticsRangeUi?,
            hasAnalytics: Boolean,
        ): Flow<CategoryWorkResult> {
            var displayedRange = currentRange
            var isAnalyticsAvailable = hasAnalytics
            return rangeInteractor.fetchRangeSelection().flatMapLatest { result ->
                result.handleAndGet(
                    onLeftAction = { failure ->
                        flow {
                            emit(ActionResult(CategoryAction.UpdateLoading(isLoading = false, isError = !isAnalyticsAvailable)))
                            emit(EffectResult(CategoryEffect.ShowFailure(failure)))
                        }
                    },
                    onRightAction = { selection ->
                        val range = selection.mapToUi()
                        val shouldSetupRange = displayedRange != range || !isAnalyticsAvailable
                        displayedRange = range
                        flow {
                            if (shouldSetupRange) {
                                isAnalyticsAvailable = false
                                emit(ActionResult(CategoryAction.SetupRange(range = range)))
                            }
                            categoryInteractor.fetchCategoryAnalytics(
                                mainCategoryId = mainCategoryId,
                                selection = selection,
                            ).collectAndHandle(
                                onLeftAction = { failure ->
                                    emit(ActionResult(CategoryAction.UpdateLoading(isLoading = false, isError = !isAnalyticsAvailable)))
                                    emit(EffectResult(CategoryEffect.ShowFailure(failure)))
                                },
                                onRightAction = { analytics ->
                                    isAnalyticsAvailable = true
                                    val analyticsUi = analytics.mapToUi()
                                    val action = CategoryAction.UpdateAnalytics(
                                        category = analyticsUi.category,
                                        analytics = analyticsUi,
                                        isUnavailable = analyticsUi.category == null,
                                    )
                                    emit(ActionResult(action))
                                },
                            )
                        }
                    },
                )
            }
        }

        private fun selectPeriodWork(period: TimePeriod) = flow {
            rangeInteractor.selectPeriod(period).handle(
                onLeftAction = { emit(EffectResult(CategoryEffect.ShowFailure(it))) },
            )
        }

        private fun shiftRangeWork(direction: Int) = flow {
            rangeInteractor.shiftRange(direction).handle(
                onLeftAction = { emit(EffectResult(CategoryEffect.ShowFailure(it))) },
            )
        }

        private fun moveToCurrentWork() = flow {
            rangeInteractor.moveToCurrent().handle(
                onLeftAction = { emit(EffectResult(CategoryEffect.ShowFailure(it))) },
            )
        }

        private fun confirmCustomRangeWork(
            fromPickerToken: Long,
            toPickerToken: Long,
        ) = flow {
            val range = AnalyticsCivilDateRange(
                from = rangeCalculator.pickerTokenToCivilToken(fromPickerToken),
                to = rangeCalculator.pickerTokenToCivilToken(toPickerToken),
            )

            rangeInteractor.confirmCustomRange(range).handle(
                onLeftAction = { emit(EffectResult(CategoryEffect.ShowFailure(it))) },
            )
        }
    }
}

internal sealed class CategoryWorkCommand : WorkCommand {
    data class ObserveAnalytics(
        val mainCategoryId: Long,
        val currentRange: AnalyticsRangeUi?,
        val hasAnalytics: Boolean,
    ) : CategoryWorkCommand()
    data class SelectPeriod(val period: TimePeriod) : CategoryWorkCommand()
    data class ShiftRange(val direction: Int) : CategoryWorkCommand()
    data object MoveToCurrent : CategoryWorkCommand()
    data class ConfirmCustomRange(val fromPickerToken: Long, val toPickerToken: Long) : CategoryWorkCommand()
}

internal typealias CategoryWorkResult = WorkResult<CategoryAction, CategoryEffect, CategoryOutput>
