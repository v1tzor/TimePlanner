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
import ru.aleshin.core.utils.platform.screenmodel.work.BackgroundWorkKey
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.timeplanner.presentation.ui.main.contract.DeepLinkTarget
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainAction
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainDeps
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
) : BaseViewModel<MainViewState, MainEvent, MainAction, MainEffect, MainDeps>(
    stateCommunicator = communicator,
    effectCommunicator = EffectCommunicator.Empty(),
    coroutineManager = coroutineManager,
) {

    override fun init(deps: MainDeps) {
        if (!isInitialize.get()) {
            super.init(deps)
            dispatchEvent(MainEvent.Init(deps.screenTarget))
        }
    }

    override suspend fun WorkScope<MainViewState, MainAction, MainEffect>.handleEvent(
        event: MainEvent,
    ) {
        when (event) {
            is MainEvent.Init -> {
                launchBackgroundWork(BackgroundKey.LOAD_SETTINGS) {
                    settingsWorkProcessor.work(SettingsWorkCommand.LoadSettings).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.NAVIGATE) {
                    navigationWorkProcessor.work(NavWorkCommand.NavigateToTab()).handleWork()
                    if (event.screenTarget == DeepLinkTarget.EDITOR) {
                        navigationWorkProcessor.work(NavWorkCommand.NavigateToEditor).handleWork()
                    }
                }
            }
            is MainEvent.NavigateToEditor -> launchBackgroundWork(BackgroundKey.NAVIGATE) {
                navigationWorkProcessor.work(NavWorkCommand.NavigateToEditor).handleWork()
            }
        }
    }

    override suspend fun reduce(
        action: MainAction,
        currentState: MainViewState,
    ) = when (action) {
        is MainAction.Navigate -> currentState
        is MainAction.ChangeSettings -> currentState.copy(
            language = action.language,
            theme = action.theme,
            colors = action.colors,
            isEnableDynamicColors = action.enableDynamicColors,
            secureMode = action.secureMode,
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_SETTINGS, NAVIGATE
    }

    class Factory @Inject constructor(viewModel: Provider<MainViewModel>) :
        BaseViewModel.Factory(viewModel)
}
