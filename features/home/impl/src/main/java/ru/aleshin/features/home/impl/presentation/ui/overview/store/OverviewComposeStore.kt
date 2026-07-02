/*
 * Copyright 2025 Stanislav Aleshin
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
package ru.aleshin.features.home.impl.presentation.ui.overview.store

import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.home.api.HomeConfig
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewAction
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewEffect
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewEvent
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewInput
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewOutput
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023
 */
internal class OverviewComposeStore @Inject constructor(
    private val workProcessor: OverviewWorkProcessor,
    stateCommunicator: StateCommunicator<OverviewState>,
    effectCommunicator: EffectCommunicator<OverviewEffect>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<OverviewState, OverviewEvent, OverviewAction, OverviewEffect, OverviewInput, OverviewOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: OverviewInput, isRestore: Boolean) {
        dispatchEvent(OverviewEvent.Init(input, isRestore))
    }

    override suspend fun WorkScope<OverviewState, OverviewAction, OverviewEffect, OverviewOutput>.handleEvent(
        event: OverviewEvent,
    ) {
        when (event) {
            is OverviewEvent.Init -> {
                sendAction(OverviewAction.UpdateLoading(true))
                launchBackgroundWork(BackgroundKey.LOAD_SCHEDULES) {
                    val schedulesCommand = OverviewWorkCommand.LoadSchedules
                    workProcessor.work(schedulesCommand).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_UNDEFINED_TASKS) {
                    val tasksCommand = OverviewWorkCommand.LoadUndefinedTasks
                    workProcessor.work(tasksCommand).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_CATEGORIES) {
                    val categoriesCommand = OverviewWorkCommand.LoadCategories
                    workProcessor.work(categoriesCommand).collectAndHandleWork()
                }
                if (!event.isRestore && event.input.sharedText != null) {
                    launchBackgroundWork(BackgroundKey.SHARE_IMPORT) {
                        val command = OverviewWorkCommand.PrepareSharedTextImport(event.input.sharedText)
                        workProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is OverviewEvent.Refresh -> {
                sendAction(OverviewAction.UpdateLoading(true))
                launchBackgroundWork(BackgroundKey.LOAD_SCHEDULES) {
                    val schedulesCommand = OverviewWorkCommand.LoadSchedules
                    workProcessor.work(schedulesCommand).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_UNDEFINED_TASKS) {
                    val tasksCommand = OverviewWorkCommand.LoadUndefinedTasks
                    workProcessor.work(tasksCommand).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_CATEGORIES) {
                    val categoriesCommand = OverviewWorkCommand.LoadCategories
                    workProcessor.work(categoriesCommand).collectAndHandleWork()
                }
            }
            is OverviewEvent.CreateOrUpdateUndefinedTask -> launchBackgroundWork(BackgroundKey.TASK_ACTION) {
                val command = OverviewWorkCommand.CreateOrUpdateUndefinedTasks(listOf(event.task))
                workProcessor.work(command).collectAndHandleWork()
            }
            is OverviewEvent.ConfirmBatchUndefinedTasks -> launchBackgroundWork(BackgroundKey.TASK_ACTION) {
                sendAction(OverviewAction.ClearSharedTextTasks)
                val command = OverviewWorkCommand.CreateOrUpdateUndefinedTasks(event.tasks)
                workProcessor.work(command).collectAndHandleWork()
            }
            is OverviewEvent.DismissBatchUndefinedTasks -> {
                sendAction(OverviewAction.ClearSharedTextTasks)
            }
            is OverviewEvent.ExecuteUndefinedTask -> launchBackgroundWork(BackgroundKey.TASK_ACTION) {
                val command = OverviewWorkCommand.ExecuteUndefinedTask(event.scheduleDate, event.task)
                workProcessor.work(command).collectAndHandleWork()
            }
            is OverviewEvent.DeleteUndefinedTask -> launchBackgroundWork(BackgroundKey.TASK_ACTION) {
                val command = OverviewWorkCommand.DeleteUndefinedTask(event.task)
                workProcessor.work(command).collectAndHandleWork()
            }
            is OverviewEvent.OpenSchedule -> {
                val config = HomeConfig.Home(event.scheduleDate)
                consumeOutput(OverviewOutput.NavigateToHome(config))
            }
            is OverviewEvent.OpenAllSchedules -> {
                consumeOutput(OverviewOutput.NavigateToDetails)
            }
            is OverviewEvent.PressScheduleButton -> {
                val config = HomeConfig.Home()
                consumeOutput(OverviewOutput.NavigateToHome(config))
            }
        }
    }

    override suspend fun reduce(
        action: OverviewAction,
        currentState: OverviewState,
    ) = when (action) {
        is OverviewAction.Navigate -> currentState
        is OverviewAction.UpdateLoading -> currentState.copy(
            isLoading = action.isLoading,
        )
        is OverviewAction.UpdateSchedules -> currentState.copy(
            isLoading = false,
            currentDate = action.date,
            currentSchedule = action.schedules.find { it.date == action.date },
            schedules = action.schedules,
        )
        is OverviewAction.UpdateUndefinedTasks -> currentState.copy(
            undefinedTasks = action.tasks,
        )
        is OverviewAction.UpdateCategories -> currentState.copy(
            categories = action.categories,
        )
        is OverviewAction.UpdateSharedTextTasks -> currentState.copy(
            sharedTextTasks = action.tasks,
            sharedTextCategories = action.categories,
        )
        is OverviewAction.ClearSharedTextTasks -> currentState.copy(
            sharedTextTasks = null,
            sharedTextCategories = emptyList(),
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_SCHEDULES, LOAD_UNDEFINED_TASKS, LOAD_CATEGORIES, TASK_ACTION, SHARE_IMPORT
    }

    class Factory @Inject constructor(
        private val workProcessor: OverviewWorkProcessor,
        private val coroutineManager: CoroutineManager,
    ) : BaseComposeStore.Factory<OverviewComposeStore, OverviewState> {

        override fun create(savedState: OverviewState): OverviewComposeStore {
            return OverviewComposeStore(
                workProcessor = workProcessor,
                stateCommunicator = StateCommunicator.Default(savedState),
                effectCommunicator = EffectCommunicator.Default(),
                coroutineManager = coroutineManager,
            )
        }
    }
}
