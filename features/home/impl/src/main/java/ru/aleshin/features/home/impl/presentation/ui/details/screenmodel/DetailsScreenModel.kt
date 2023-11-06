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
package ru.aleshin.features.home.impl.presentation.ui.details.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.features.home.api.navigation.HomeScreens
import ru.aleshin.features.home.impl.di.holder.HomeComponentHolder
import ru.aleshin.features.home.impl.navigation.NavigationManager
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsAction
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsEffect
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsEvent
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsViewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 06.11.2023
 */
internal class DetailsScreenModel @Inject constructor(
    private val workProcessor: DetailsWorkProcessor,
    private val navigationManager: NavigationManager,
    stateCommunicator: DetailsStateCommunicator,
    effectCommunicator: DetailsEffectCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<DetailsViewState, DetailsEvent, DetailsAction, DetailsEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun init() {
        if (!isInitialize.get()) {
            super.init()
            dispatchEvent(DetailsEvent.Init)
        }
    }

    override suspend fun WorkScope<DetailsViewState, DetailsAction, DetailsEffect>.handleEvent(
        event: DetailsEvent,
    ) {
        when (event) {
            is DetailsEvent.Init -> launchBackgroundWork(DetailsWorkCommand.LoadSchedules) {
                val command = DetailsWorkCommand.LoadSchedules
                workProcessor.work(command).collectAndHandleWork()
            }
            is DetailsEvent.OpenSchedule -> {
                val screen = HomeScreens.Home(event.schedule.date)
                navigationManager.navigateToLocal(screen, false)
            }
            is DetailsEvent.PressBackButton -> {
                navigationManager.navigateToLocalBack()
            }
        }
    }

    override suspend fun reduce(
        action: DetailsAction,
        currentState: DetailsViewState,
    ) = when (action) {
        is DetailsAction.UpdateSchedules -> currentState.copy(
            isLoading = false,
            currentSchedule = action.schedules.find { it.date == action.date },
            schedules = action.schedules,
        )
        is DetailsAction.UpdateLoading -> currentState.copy(
            isLoading = action.isLoading,
        )
    }
}

@Composable
internal fun Screen.rememberDetailsScreenModel(): DetailsScreenModel {
    val component = HomeComponentHolder.fetchComponent()
    return rememberScreenModel { component.fetchDetailsScreenModel() }
}
