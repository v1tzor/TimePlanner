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
package ru.aleshin.features.settings.impl.presentation.ui.settings.screensmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.EmptyDeps
import ru.aleshin.core.utils.platform.screenmodel.work.BackgroundWorkKey
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.features.settings.impl.di.holder.SettingsComponentHolder
import ru.aleshin.features.settings.impl.navigation.NavigationManager
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsAction
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEffect
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEvent
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsViewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
internal class SettingsScreenModel @Inject constructor(
    private val settingsWorkProcessor: SettingsWorkProcessor,
    private val dataWorkProcessor: DataWorkProcessor,
    private val navigationManager: NavigationManager,
    stateCommunicator: SettingsStateCommunicator,
    effectCommunicator: SettingsEffectCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<SettingsViewState, SettingsEvent, SettingsAction, SettingsEffect, EmptyDeps>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun init(deps: EmptyDeps) {
        if (!isInitialize.get()) {
            super.init(deps)
            dispatchEvent(SettingsEvent.Init)
        }
    }

    override suspend fun WorkScope<SettingsViewState, SettingsAction, SettingsEffect>.handleEvent(
        event: SettingsEvent,
    ) {
        when (event) {
            is SettingsEvent.Init -> launchBackgroundWork(BackgroundKey.LOAD_SETTINGS) {
                settingsWorkProcessor.work(SettingsWorkCommand.LoadAllSettings).collectAndHandleWork()
            }
            is SettingsEvent.ChangedThemeSettings -> launchBackgroundWork(BackgroundKey.SETTINGS_ACTION) {
                settingsWorkProcessor.work(SettingsWorkCommand.UpdateThemeSettings(event.themeSettings)).collectAndHandleWork()
            } 
            is SettingsEvent.ChangedTasksSettings -> launchBackgroundWork(BackgroundKey.SETTINGS_ACTION) {
                settingsWorkProcessor.work(SettingsWorkCommand.UpdateTasksSettings(event.tasksSettings)).collectAndHandleWork()
            }
            is SettingsEvent.PressResetButton -> launchBackgroundWork(BackgroundKey.SETTINGS_ACTION) {
                settingsWorkProcessor.work(SettingsWorkCommand.ResetSettings).collectAndHandleWork()
            }
            is SettingsEvent.PressClearDataButton -> launchBackgroundWork(BackgroundKey.DATA_WORK) {
                dataWorkProcessor.work(DataWorkCommand.ClearAllData).collectAndHandleWork()
            }
            is SettingsEvent.PressRestoreBackupData -> launchBackgroundWork(BackgroundKey.DATA_WORK) {
                dataWorkProcessor.work(DataWorkCommand.RestoreBackupData(event.uri)).collectAndHandleWork()
            }
            is SettingsEvent.PressSaveBackupData -> launchBackgroundWork(BackgroundKey.DATA_WORK) {
                dataWorkProcessor.work(DataWorkCommand.SaveBackupData(event.uri)).collectAndHandleWork()
            } 
            is SettingsEvent.PressDonateButton -> {
                navigationManager.navigateToDonate()
            }
        }
    }

    override suspend fun reduce(
        action: SettingsAction, 
        currentState: SettingsViewState,
    ) = when (action) {
        is SettingsAction.ChangeAllSettings -> currentState.copy(
            failure = null,
            themeSettings = action.settings.themeSettings,
            tasksSettings = action.settings.tasksSettings,
            isBackupLoading = false,
        )
        is SettingsAction.ShowLoadingBackup -> currentState.copy(
            isBackupLoading = action.isLoading,
        )
    }

    override fun onDispose() {
        super.onDispose()
        SettingsComponentHolder.clear()
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_SETTINGS, SETTINGS_ACTION, DATA_WORK,
    }
}

@Composable
internal fun Screen.rememberSettingsScreenModel() = rememberScreenModel {
    SettingsComponentHolder.fetchComponent().fetchSettingsScreenModel()
}
