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
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsAction
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEffect
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsOutput
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class AnalyticsComposeStore @Inject constructor(
    private val analyticsWorkProcessor: AnalyticsWorkProcessor,
    stateCommunicator: StateCommunicator<AnalyticsState>,
    effectCommunicator: EffectCommunicator<AnalyticsEffect>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<AnalyticsState, AnalyticsEvent, AnalyticsAction, AnalyticsEffect, EmptyInput, AnalyticsOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: EmptyInput, isRestore: Boolean) {
        dispatchEvent(AnalyticsEvent.Init(isRestore = isRestore))
    }

    override suspend fun WorkScope<AnalyticsState, AnalyticsAction, AnalyticsEffect, AnalyticsOutput>.handleEvent(
        event: AnalyticsEvent,
    ) {
        when (event) {
            is AnalyticsEvent.Init -> {
                if (state.overview == null) {
                    sendAction(AnalyticsAction.UpdateLoading(isLoading = true, isError = false))
                }
                launchBackgroundWork(BackgroundKey.OBSERVE_ANALYTICS) {
                    val command = AnalyticsWorkCommand.ObserveAnalytics(
                        categorySort = state.categorySort,
                        currentRange = state.range,
                        hasOverview = state.overview != null,
                    )
                    analyticsWorkProcessor.work(command).collectAndHandleWork()
                }
            }
            AnalyticsEvent.Retry -> {
                if (state.overview == null) {
                    sendAction(AnalyticsAction.UpdateLoading(isLoading = true, isError = false))
                }
                launchBackgroundWork(BackgroundKey.OBSERVE_ANALYTICS) {
                    val command = AnalyticsWorkCommand.ObserveAnalytics(
                        categorySort = state.categorySort,
                        currentRange = state.range,
                        hasOverview = state.overview != null,
                    )
                    analyticsWorkProcessor.work(command).collectAndHandleWork()
                }
            }
            is AnalyticsEvent.SelectPeriod -> with(event) {
                sendAction(AnalyticsAction.UpdateLoading(isLoading = true, isError = false))

                launchBackgroundWork(BackgroundKey.UPDATE_RANGE) {
                    val command = AnalyticsWorkCommand.SelectPeriod(period)
                    analyticsWorkProcessor.work(command).collectAndHandleWork()
                }
            }
            AnalyticsEvent.PreviousPeriod -> {
                sendAction(AnalyticsAction.UpdateLoading(isLoading = true, isError = false))

                launchBackgroundWork(BackgroundKey.UPDATE_RANGE) {
                    val command = AnalyticsWorkCommand.ShiftRange(PREVIOUS_DIRECTION)
                    analyticsWorkProcessor.work(command).collectAndHandleWork()
                }
            }
            AnalyticsEvent.NextPeriod -> {
                sendAction(AnalyticsAction.UpdateLoading(isLoading = true, isError = false))

                launchBackgroundWork(BackgroundKey.UPDATE_RANGE) {
                    val command = AnalyticsWorkCommand.ShiftRange(NEXT_DIRECTION)
                    analyticsWorkProcessor.work(command).collectAndHandleWork()
                }
            }
            AnalyticsEvent.MoveToCurrent -> {
                sendAction(AnalyticsAction.UpdateLoading(isLoading = true, isError = false))

                launchBackgroundWork(BackgroundKey.UPDATE_RANGE) {
                    val command = AnalyticsWorkCommand.MoveToCurrent
                    analyticsWorkProcessor.work(command).collectAndHandleWork()
                }
            }
            is AnalyticsEvent.ConfirmCalendar -> with(event) {
                sendAction(AnalyticsAction.UpdateLoading(isLoading = true, isError = false))

                launchBackgroundWork(BackgroundKey.UPDATE_RANGE) {
                    val command = AnalyticsWorkCommand.ConfirmCustomRange(
                        fromPickerToken = fromPickerToken,
                        toPickerToken = toPickerToken,
                    )
                    analyticsWorkProcessor.work(command).collectAndHandleWork()
                }
            }
            is AnalyticsEvent.ChangeCategorySort -> with(event) {
                if (sort != state.categorySort) {
                    launchBackgroundWork(BackgroundKey.OBSERVE_ANALYTICS) {
                        val command = AnalyticsWorkCommand.ObserveAnalytics(
                            categorySort = sort,
                            currentRange = state.range,
                            hasOverview = state.overview != null,
                        )
                        analyticsWorkProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            AnalyticsEvent.ToggleCategories -> {
                sendAction(AnalyticsAction.UpdateCategoriesExpanded(isExpanded = !state.isCategoriesExpanded))
            }
            is AnalyticsEvent.SelectChartItem -> {
                val key = event.key.takeIf { state.selectedChartKey != it }
                sendAction(AnalyticsAction.UpdateChartItem(key))
            }
            is AnalyticsEvent.SelectCreationBucket -> {
                val key = event.key.takeIf { state.selectedCreationBucketKey != it }
                sendAction(AnalyticsAction.UpdateCreationBucket(key))
            }
            is AnalyticsEvent.ClickCategoryItem -> {
                consumeOutput(AnalyticsOutput.NavigateToCategory(event.mainCategoryId))
            }
        }
    }

    override suspend fun reduce(
        action: AnalyticsAction,
        currentState: AnalyticsState,
    ) = when (action) {
        is AnalyticsAction.UpdateLoading -> currentState.copy(
            isLoading = action.isLoading,
            isError = action.isError,
        )
        is AnalyticsAction.SetupRange -> currentState.copy(
            isLoading = true,
            isError = false,
            range = action.range,
            overview = null,
            selectedChartKey = null,
            selectedCreationBucketKey = null,
        )
        is AnalyticsAction.UpdateAnalytics -> currentState.copy(
            isLoading = false,
            isError = false,
            categorySort = action.categorySort,
            overview = action.overview,
        )
        is AnalyticsAction.UpdateCategoriesExpanded -> currentState.copy(
            isCategoriesExpanded = action.isExpanded,
        )
        is AnalyticsAction.UpdateChartItem -> currentState.copy(
            selectedChartKey = action.key
        )
        is AnalyticsAction.UpdateCreationBucket -> currentState.copy(
            selectedCreationBucketKey = action.key
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        OBSERVE_ANALYTICS,
        UPDATE_RANGE,
    }

    class Factory @Inject constructor(
        private val workProcessor: AnalyticsWorkProcessor,
        private val coroutineManager: CoroutineManager,
    ) : BaseComposeStore.Factory<AnalyticsComposeStore, AnalyticsState> {

        override fun create(savedState: AnalyticsState): AnalyticsComposeStore {
            return AnalyticsComposeStore(
                analyticsWorkProcessor = workProcessor,
                stateCommunicator = StateCommunicator.Default(savedState),
                effectCommunicator = EffectCommunicator.Default(),
                coroutineManager = coroutineManager,
            )
        }
    }

    private companion object Companion {
        const val PREVIOUS_DIRECTION = -1
        const val NEXT_DIRECTION = 1
    }
}
