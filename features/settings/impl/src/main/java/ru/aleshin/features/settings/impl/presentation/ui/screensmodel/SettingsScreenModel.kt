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
 * imitations under the License.
 */
package ru.aleshin.features.settings.impl.presentation.ui.screensmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.features.settings.impl.di.holder.SettingsComponentHolder
import ru.aleshin.features.settings.impl.presentation.ui.contract.SettingsAction
import ru.aleshin.features.settings.impl.presentation.ui.contract.SettingsEffect
import ru.aleshin.features.settings.impl.presentation.ui.contract.SettingsEvent
import ru.aleshin.features.settings.impl.presentation.ui.contract.SettingsViewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
internal class SettingsScreenModel @Inject constructor(
    private val settingsWorkProcessor: SettingsWorkProcessor,
    stateCommunicator: SettingsStateCommunicator,
    effectCommunicator: SettingsEffectCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<SettingsViewState, SettingsEvent, SettingsAction, SettingsEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    init {
        dispatchEvent(SettingsEvent.Init)
    }

    override suspend fun WorkScope<SettingsViewState, SettingsAction, SettingsEffect>.handleEvent(
        event: SettingsEvent,
    ) = when (event) {
        is SettingsEvent.Init -> {
            settingsWorkProcessor.loadAllSettings().handleWork()
        }
        is SettingsEvent.ChangedThemeSettings -> {
            settingsWorkProcessor.updateThemeSettings(event.themeSettings).handleWork()
        }
        SettingsEvent.PressResetButton -> {
            settingsWorkProcessor.resetSettings().handleWork()
        }
    }

    override suspend fun reduce(action: SettingsAction, currentState: SettingsViewState) =
        when (action) {
            is SettingsAction.ChangeAllSettings -> currentState.copy(
                themeSettings = action.settings.themeSettings,
                failure = null,
            )
            is SettingsAction.ChangeThemeSettings -> currentState.copy(
                themeSettings = action.settings,
                failure = null,
            )
        }

    override fun onDispose() {
        super.onDispose()
        SettingsComponentHolder.clear()
    }
}

@Composable
internal fun Screen.rememberSettingsScreenModel() = rememberScreenModel {
    SettingsComponentHolder.fetchComponent().fetchSettingsScreenModel()
}
