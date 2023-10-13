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
package ru.aleshin.features.settings.impl.presentation.ui.donate.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.communications.state.EffectCommunicator
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.features.settings.impl.di.holder.SettingsComponentHolder
import ru.aleshin.features.settings.impl.navigation.NavigationManager
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateAction
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateEffect
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateEvent
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateViewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 13.10.2023
 */
internal class DonateScreenModel @Inject constructor(
    private val navigationManager: NavigationManager,
    stateCommunicator: DonateStateCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<DonateViewState, DonateEvent, DonateAction, DonateEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = EffectCommunicator.Empty(),
    coroutineManager = coroutineManager,
) {

    override suspend fun WorkScope<DonateViewState, DonateAction, DonateEffect>.handleEvent(
        event: DonateEvent,
    ) = when (event) {
        is DonateEvent.PressBackButton -> navigationManager.navigateToBack()
    }

    override suspend fun reduce(action: DonateAction, currentState: DonateViewState) = currentState
}

@Composable
internal fun Screen.rememberDonateScreenModel(): DonateScreenModel {
    val component = SettingsComponentHolder.fetchComponent()
    return rememberScreenModel { component.fetchDonateScreenModel() }
}
