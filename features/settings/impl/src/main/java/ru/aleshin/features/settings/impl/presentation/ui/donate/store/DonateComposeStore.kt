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
package ru.aleshin.features.settings.impl.presentation.ui.donate.store

import ru.aleshin.core.utils.architecture.store.BaseOnlyOutComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateAction
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateEffect
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateEvent
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateOutput
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 13.10.2023
 */
internal class DonateComposeStore @Inject constructor(
    stateCommunicator: StateCommunicator<DonateState>,
    effectCommunicator: EffectCommunicator<DonateEffect>,
    coroutineManager: CoroutineManager,
) : BaseOnlyOutComposeStore<DonateState, DonateEvent, DonateAction, DonateEffect, DonateOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override suspend fun WorkScope<DonateState, DonateAction, DonateEffect, DonateOutput>.handleEvent(
        event: DonateEvent,
    ) = when (event) {
        is DonateEvent.PressBackButton -> {
            consumeOutput(DonateOutput.NavigateToBack)
        }
    }

    override suspend fun reduce(action: DonateAction, currentState: DonateState) = currentState

     class Factory @Inject constructor(
         private val coroutineManager: CoroutineManager,
     ) : BaseOnlyOutComposeStore.Factory<DonateComposeStore, DonateState> {

         override fun create(savedState: DonateState): DonateComposeStore {
             return DonateComposeStore(
                 stateCommunicator = StateCommunicator.Default(savedState),
                 effectCommunicator = EffectCommunicator.Default(),
                 coroutineManager = coroutineManager,
             )
         }
     }
}