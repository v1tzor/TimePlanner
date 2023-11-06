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
package ru.aleshin.features.home.impl.presentation.ui.overview.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.features.home.api.navigation.HomeScreens
import ru.aleshin.features.home.impl.di.holder.HomeComponentHolder
import ru.aleshin.features.home.impl.navigation.NavigationManager
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewAction
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewEffect
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewEvent
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewViewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023
 */
internal class OverviewScreenModel @Inject constructor(
    private val workProcessor: OverviewWorkProcessor,
    private val navigationManager: NavigationManager,
    stateCommunicator: OverviewStateCommunicator,
    effectCommunicator: OverviewEffectCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<OverviewViewState, OverviewEvent, OverviewAction, OverviewEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun init() {
        if (!isInitialize.get()) {
            super.init()
            dispatchEvent(OverviewEvent.Init)
        }
    }

    override suspend fun WorkScope<OverviewViewState, OverviewAction, OverviewEffect>.handleEvent(
        event: OverviewEvent,
    ) {
        when (event) {
            is OverviewEvent.Init, OverviewEvent.Refresh -> {
                sendAction(OverviewAction.UpdateLoading(true))
                launchBackgroundWork(OverviewWorkCommand.LoadSchedules) {
                    val schedulesCommand = OverviewWorkCommand.LoadSchedules
                    workProcessor.work(schedulesCommand).collectAndHandleWork()
                }
                launchBackgroundWork(OverviewWorkCommand.LoadUndefinedTasks) {
                    val tasksCommand = OverviewWorkCommand.LoadUndefinedTasks
                    workProcessor.work(tasksCommand).collectAndHandleWork()
                }
                launchBackgroundWork(OverviewWorkCommand.LoadCategories) {
                    val categoriesCommand = OverviewWorkCommand.LoadCategories
                    workProcessor.work(categoriesCommand).collectAndHandleWork()
                }
            }
            is OverviewEvent.CreateOrUpdateUndefinedTask -> {
                val command = OverviewWorkCommand.CreateOrUpdateUndefinedTask(event.task)
                workProcessor.work(command).collectAndHandleWork()
            }
            is OverviewEvent.ExecuteUndefinedTask -> {
                val command = OverviewWorkCommand.ExecuteUndefinedTask(event.scheduleDate, event.task)
                workProcessor.work(command).collectAndHandleWork()
            }
            is OverviewEvent.DeleteUndefinedTask -> {
                val command = OverviewWorkCommand.DeleteUndefinedTask(event.task)
                workProcessor.work(command).collectAndHandleWork()
            }
            is OverviewEvent.OpenSchedule -> {
                val screen = HomeScreens.Home(event.scheduleDate)
                navigationManager.navigateToLocal(screen)
            }
            is OverviewEvent.OpenAllSchedules -> {
                val screen = HomeScreens.Details
                navigationManager.navigateToLocal(screen, false)
            }
            is OverviewEvent.PressScheduleButton -> {
                val screen = HomeScreens.Home()
                navigationManager.navigateToLocal(screen, false)
            }
        }
    }

    override suspend fun reduce(
        action: OverviewAction,
        currentState: OverviewViewState,
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
    }
}

@Composable
internal fun Screen.rememberOverviewScreenModel(): OverviewScreenModel {
    val component = HomeComponentHolder.fetchComponent()
    return rememberScreenModel { component.fetchOverviewScreenModel() }
}
