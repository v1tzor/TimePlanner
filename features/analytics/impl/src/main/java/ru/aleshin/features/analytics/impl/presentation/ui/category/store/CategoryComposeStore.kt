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

import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryAction
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEffect
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEvent
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryInput
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryOutput
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class CategoryComposeStore @Inject constructor(
    private val workProcessor: CategoryWorkProcessor,
    stateCommunicator: StateCommunicator<CategoryState>,
    effectCommunicator: EffectCommunicator<CategoryEffect>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<CategoryState, CategoryEvent, CategoryAction, CategoryEffect, CategoryInput, CategoryOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: CategoryInput, isRestore: Boolean) {
        dispatchEvent(CategoryEvent.Init(input))
    }

    override suspend fun WorkScope<CategoryState, CategoryAction, CategoryEffect, CategoryOutput>.handleEvent(
        event: CategoryEvent,
    ) {
        when (event) {
            is CategoryEvent.Init -> {
                sendAction(CategoryAction.SetupCategoryId(event.input.mainCategoryId))
                launchBackgroundWork(BackgroundKey.OBSERVE_ANALYTICS) {
                    val command = CategoryWorkCommand.ObserveAnalytics(
                        mainCategoryId = event.input.mainCategoryId,
                        currentRange = state.range,
                        hasAnalytics = state.analytics != null,
                    )
                    workProcessor.work(command).collectAndHandleWork()
                }
            }
            CategoryEvent.Activate,
            CategoryEvent.Retry -> state().mainCategoryId?.let { mainCategoryId ->
                launchBackgroundWork(BackgroundKey.OBSERVE_ANALYTICS) {
                    val command = CategoryWorkCommand.ObserveAnalytics(
                        mainCategoryId = mainCategoryId,
                        currentRange = state.range,
                        hasAnalytics = state.analytics != null,
                    )
                    workProcessor.work(command).collectAndHandleWork()
                }
            }
            CategoryEvent.Deactivate -> launchBackgroundWork(BackgroundKey.OBSERVE_ANALYTICS) {}
            CategoryEvent.NavigateBack -> consumeOutput(CategoryOutput.NavigateToBack)
            is CategoryEvent.SelectPeriod -> {
                launchBackgroundWork(BackgroundKey.UPDATE_RANGE) {
                    val command = CategoryWorkCommand.SelectPeriod(period = event.period)
                    workProcessor.work(command).collectAndHandleWork()
                }
            }
            CategoryEvent.PreviousPeriod -> {
                launchBackgroundWork(BackgroundKey.UPDATE_RANGE) {
                    val command = CategoryWorkCommand.ShiftRange(direction = PREVIOUS_DIRECTION)
                    workProcessor.work(command).collectAndHandleWork()
                }
            }
            CategoryEvent.NextPeriod -> {
                launchBackgroundWork(BackgroundKey.UPDATE_RANGE) {
                    val command = CategoryWorkCommand.ShiftRange(direction = NEXT_DIRECTION)
                    workProcessor.work(command).collectAndHandleWork()
                }
            }
            CategoryEvent.MoveToCurrent -> {
                launchBackgroundWork(BackgroundKey.UPDATE_RANGE) {
                    val command = CategoryWorkCommand.MoveToCurrent
                    workProcessor.work(command).collectAndHandleWork()
                }
            }
            is CategoryEvent.ConfirmCalendar -> {
                launchBackgroundWork(BackgroundKey.UPDATE_RANGE) {
                    val command = CategoryWorkCommand.ConfirmCustomRange(
                        fromPickerToken = event.fromPickerToken,
                        toPickerToken = event.toPickerToken,
                    )
                    workProcessor.work(command).collectAndHandleWork()
                }
            }
            CategoryEvent.ToggleTasksExpanded -> sendAction(
                CategoryAction.UpdateTasksExpanded(
                    isExpanded = !state.isTasksExpanded,
                )
            )
            is CategoryEvent.SelectSubCategoryBucket -> sendAction(
                CategoryAction.UpdateChartSelection(
                    selectedSubCategoryBucketKey = event.key,
                    selectedLoadBucketIndex = null,
                )
            )
            is CategoryEvent.SelectLoadBucket -> sendAction(
                CategoryAction.UpdateChartSelection(
                    selectedSubCategoryBucketKey = null,
                    selectedLoadBucketIndex = event.index,
                )
            )
        }
    }

    override suspend fun reduce(action: CategoryAction, currentState: CategoryState) = when (action) {
        is CategoryAction.SetupCategoryId -> currentState.copy(
            mainCategoryId = action.mainCategoryId,
        )
        is CategoryAction.SetupRange -> currentState.copy(
            isLoading = true,
            range = action.range,
            analytics = null,
            isUnavailable = false,
            isError = false,
            selectedSubCategoryBucketKey = null,
            selectedLoadBucketIndex = null,
        )
        is CategoryAction.UpdateAnalytics -> currentState.copy(
            isLoading = false,
            category = action.category,
            analytics = action.analytics,
            isUnavailable = action.isUnavailable,
            isError = false,
        )
        is CategoryAction.UpdateLoading -> currentState.copy(
            isLoading = action.isLoading,
            isError = action.isError,
        )
        is CategoryAction.UpdateTasksExpanded -> currentState.copy(
            isTasksExpanded = action.isExpanded,
        )
        is CategoryAction.UpdateChartSelection -> currentState.copy(
            selectedSubCategoryBucketKey = action.selectedSubCategoryBucketKey,
            selectedLoadBucketIndex = action.selectedLoadBucketIndex,
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        OBSERVE_ANALYTICS,
        UPDATE_RANGE,
    }

    class Factory @Inject constructor(
        private val workProcessor: CategoryWorkProcessor,
        private val coroutineManager: CoroutineManager,
    ) : BaseComposeStore.Factory<CategoryComposeStore, CategoryState> {

        override fun create(savedState: CategoryState): CategoryComposeStore {
            return CategoryComposeStore(
                workProcessor = workProcessor,
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
