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
package ru.aleshin.features.home.impl.presentation.ui.details.store

import ru.aleshin.core.utils.architecture.component.EmptyInput
import ru.aleshin.core.utils.architecture.store.BaseOnlyOutComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.home.api.HomeFeatureComponent.HomeConfig
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsAction
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsEffect
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsEvent
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsOutput
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 06.11.2023
 */
internal class DetailsComposeStore @Inject constructor(
    private val workProcessor: DetailsWorkProcessor,
    stateCommunicator: StateCommunicator<DetailsState>,
    effectCommunicator: EffectCommunicator<DetailsEffect>,
    coroutineManager: CoroutineManager,
) : BaseOnlyOutComposeStore<DetailsState, DetailsEvent, DetailsAction, DetailsEffect, DetailsOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: EmptyInput, isRestore: Boolean) {
        dispatchEvent(DetailsEvent.Init)
    }

    override suspend fun WorkScope<DetailsState, DetailsAction, DetailsEffect, DetailsOutput>.handleEvent(
        event: DetailsEvent,
    ) {
        when (event) {
            is DetailsEvent.Init -> launchBackgroundWork(BackgroundKey.LOAD_SCHEDULES) {
                val command = DetailsWorkCommand.LoadSchedules
                workProcessor.work(command).collectAndHandleWork()
            }
            is DetailsEvent.OpenSchedule -> {
                val config = HomeConfig.Home(event.schedule.date)
                consumeOutput(DetailsOutput.NavigateToHome(config))
            }
            is DetailsEvent.PressBackButton -> {
                consumeOutput(DetailsOutput.NavigateToBack)
            }
        }
    }

    override suspend fun reduce(
        action: DetailsAction,
        currentState: DetailsState,
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

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_SCHEDULES
    }

     class Factory @Inject constructor(
         private val workProcessor: DetailsWorkProcessor,
         private val coroutineManager: CoroutineManager,
     ) : BaseOnlyOutComposeStore.Factory<DetailsComposeStore, DetailsState> {

         override fun create(savedState: DetailsState): DetailsComposeStore {
             return DetailsComposeStore(
                 workProcessor = workProcessor,
                 stateCommunicator = StateCommunicator.Default(savedState),
                 effectCommunicator = EffectCommunicator.Default(),
                 coroutineManager = coroutineManager,
             )
         }
     }
}