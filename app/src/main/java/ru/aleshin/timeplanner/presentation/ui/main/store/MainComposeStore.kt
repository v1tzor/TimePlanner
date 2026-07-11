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
package ru.aleshin.timeplanner.presentation.ui.main.store

import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.timeplanner.presentation.ui.main.contract.DeepLinkTarget
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainAction
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEffect
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEvent
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainInput
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainOutput
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 12.09.2025
 */
class MainComposeStore(
    private val settingsWorkProcessor: SettingsWorkProcessor,
    private val navigationWorkProcessor: NavigationWorkProcessor,
    stateCommunicator: StateCommunicator<MainState>,
    effectCommunicator: EffectCommunicator<MainEffect>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<MainState, MainEvent, MainAction, MainEffect, MainInput, MainOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: MainInput, isRestore: Boolean) {
        dispatchEvent(MainEvent.Init(isRestore, input.initialDeepLinkTarget, input.initialShareTarget))
    }

    override suspend fun WorkScope<MainState, MainAction, MainEffect, MainOutput>.handleEvent(
        event: MainEvent,
    ) {
        when (event) {
            is MainEvent.Init -> with(event) {
                launchBackgroundWork(BackgroundKey.LOAD_SETTINGS) {
                    val command = SettingsWorkCommand.LoadSettings
                    settingsWorkProcessor.work(command).collectAndHandleWork()
                }
                if (!isRestore) {
                    launchBackgroundWork(BackgroundKey.NAVIGATE) {
                        val navigationCommand = NavWorkCommand.InitialNavigation
                        navigationWorkProcessor.work(navigationCommand).handleWork()

                        if (initialDeepLinkTarget != null) {
                            val deepLinkCommand = NavWorkCommand.ProcessDeepLink(initialDeepLinkTarget)
                            navigationWorkProcessor.work(deepLinkCommand).handleWork()
                        }
                        if (initialShareTarget != null) {
                            val shareCommand = NavWorkCommand.ProcessShare(initialShareTarget)
                            navigationWorkProcessor.work(shareCommand).handleWork()
                        }
                    }
                }
            }
            is MainEvent.ProcessDeepLink -> {
                val deepLinkCommand = NavWorkCommand.ProcessDeepLink(event.screenTarget)
                navigationWorkProcessor.work(deepLinkCommand).handleWork()
            }
            is MainEvent.ProcessShare -> {
                val shareCommand = NavWorkCommand.ProcessShare(event.shareTarget)
                navigationWorkProcessor.work(shareCommand).handleWork()
            }
        }
    }

    override suspend fun reduce(
        action: MainAction,
        currentState: MainState,
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

    class Factory @Inject constructor(
        private val settingsWorkProcessor: SettingsWorkProcessor,
        private val navigationWorkProcessor: NavigationWorkProcessor,
        private val coroutineManager: CoroutineManager,
    ) : BaseComposeStore.Factory<MainComposeStore, MainState> {

        override fun create(savedState: MainState): MainComposeStore {
            return MainComposeStore(
                settingsWorkProcessor = settingsWorkProcessor,
                navigationWorkProcessor = navigationWorkProcessor,
                stateCommunicator = StateCommunicator.Default(savedState),
                effectCommunicator = EffectCommunicator.Default(),
                coroutineManager = coroutineManager,
            )
        }
    }
}
