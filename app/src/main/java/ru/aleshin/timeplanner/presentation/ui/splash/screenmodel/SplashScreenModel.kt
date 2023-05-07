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
package ru.aleshin.timeplanner.presentation.ui.splash.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.communications.state.EffectCommunicator
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.timeplanner.application.fetchAppComponent
import ru.aleshin.timeplanner.navigation.GlobalNavigationManager
import ru.aleshin.timeplanner.presentation.ui.splash.contract.SplashAction
import ru.aleshin.timeplanner.presentation.ui.splash.contract.SplashEffect
import ru.aleshin.timeplanner.presentation.ui.splash.contract.SplashEvent
import ru.aleshin.timeplanner.presentation.ui.splash.contract.SplashViewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
class SplashScreenModel @Inject constructor(
    private val navigationManager: GlobalNavigationManager,
    communicator: SplashStateCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<SplashViewState, SplashEvent, SplashAction, SplashEffect>(
    stateCommunicator = communicator,
    effectCommunicator = EffectCommunicator.Empty(),
    coroutineManager = coroutineManager,
) {

    override suspend fun WorkScope<SplashViewState, SplashAction, SplashEffect>.handleEvent(
        event: SplashEvent,
    ) = when (event) {
        is SplashEvent.Init -> navigationManager.showTabScreen()
    }

    override suspend fun reduce(
        action: SplashAction,
        currentState: SplashViewState,
    ) = SplashViewState.Default
}

@Composable
fun Screen.rememberSplashScreenModel(): SplashScreenModel {
    val component = fetchAppComponent()
    return rememberScreenModel { component.fetchSplashScreenModel() }
}
