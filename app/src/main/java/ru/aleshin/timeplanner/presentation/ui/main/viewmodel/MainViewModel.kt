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
package ru.aleshin.timeplanner.presentation.ui.main.viewmodel

import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.communications.state.EffectCommunicator
import ru.aleshin.core.utils.platform.screenmodel.BaseViewModel
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainAction
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEffect
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEvent
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainViewState
import javax.inject.Inject
import javax.inject.Provider

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
class MainViewModel @Inject constructor(
    private val settingsWorkProcessor: SettingsWorkProcessor,
    private val navigationWorkProcessor: NavigationWorkProcessor,
    communicator: MainStateCommunicator,
    coroutineManager: CoroutineManager,
) : BaseViewModel<MainViewState, MainEvent, MainAction, MainEffect>(
    stateCommunicator = communicator,
    effectCommunicator = EffectCommunicator.Empty(),
    coroutineManager = coroutineManager,
) {

    override fun init() {
        if (!isInitialize.get()) {
            super.init()
            dispatchEvent(MainEvent.Init)
        }
    }

    override suspend fun WorkScope<MainViewState, MainAction, MainEffect>.handleEvent(
        event: MainEvent,
    ) {
        when (event) {
            is MainEvent.Init -> {
                launchBackgroundWork(SettingsWorkCommand.LoadThemeSettings) {
                    settingsWorkProcessor.work(SettingsWorkCommand.LoadThemeSettings).collectAndHandleWork()
                }
                launchBackgroundWork(NavWorkCommand.NavigateToTab()) {
                    navigationWorkProcessor.work(NavWorkCommand.NavigateToTab())
                }
            }
        }
    }

    override suspend fun reduce(
        action: MainAction,
        currentState: MainViewState,
    ) = when (action) {
        is MainAction.Navigate -> currentState
        is MainAction.ChangeThemeSettings -> currentState.copy(
            language = action.language,
            colors = action.themeColors,
            isEnableDynamicColors = action.enableDynamicColors,
        )
    }

    class Factory @Inject constructor(viewModel: Provider<MainViewModel>) :
        BaseViewModel.Factory(viewModel)
}
