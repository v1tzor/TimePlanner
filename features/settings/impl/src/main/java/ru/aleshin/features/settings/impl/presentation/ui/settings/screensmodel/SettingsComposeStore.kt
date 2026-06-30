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

import ru.aleshin.core.utils.architecture.component.EmptyInput
import ru.aleshin.core.utils.architecture.store.BaseOnlyOutComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsAction
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEffect
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEvent
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsOutput
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
internal class SettingsComposeStore @Inject constructor(
    private val settingsWorkProcessor: SettingsWorkProcessor,
    private val dataWorkProcessor: DataWorkProcessor,
    stateCommunicator: StateCommunicator<SettingsState>,
    effectCommunicator: EffectCommunicator<SettingsEffect>,
    coroutineManager: CoroutineManager,
) : BaseOnlyOutComposeStore<SettingsState, SettingsEvent, SettingsAction, SettingsEffect, SettingsOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: EmptyInput, isRestore: Boolean) {
        dispatchEvent(SettingsEvent.Init)
    }

    override suspend fun WorkScope<SettingsState, SettingsAction, SettingsEffect, SettingsOutput>.handleEvent(
        event: SettingsEvent,
    ) {
        when (event) {
            is SettingsEvent.Init -> launchBackgroundWork(BackgroundKey.LOAD_SETTINGS) {
                val command = SettingsWorkCommand.LoadAllSettings
                settingsWorkProcessor.work(command).collectAndHandleWork()
            }
            is SettingsEvent.ChangedThemeSettings -> launchBackgroundWork(BackgroundKey.SETTINGS_ACTION) {
                val command = SettingsWorkCommand.UpdateThemeSettings(event.themeSettings)
                settingsWorkProcessor.work(command).collectAndHandleWork()
            } 
            is SettingsEvent.ChangedTasksSettings -> launchBackgroundWork(BackgroundKey.SETTINGS_ACTION) {
                val command = SettingsWorkCommand.UpdateTasksSettings(event.tasksSettings)
                settingsWorkProcessor.work(command).collectAndHandleWork()
            }
            is SettingsEvent.PressResetButton -> launchBackgroundWork(BackgroundKey.SETTINGS_ACTION) {
                val command = SettingsWorkCommand.ResetSettings
                settingsWorkProcessor.work(command).collectAndHandleWork()
            }
            is SettingsEvent.PressClearDataButton -> launchBackgroundWork(BackgroundKey.DATA_WORK) {
                val command = DataWorkCommand.ClearAllData
                dataWorkProcessor.work(command).collectAndHandleWork()
            }
            is SettingsEvent.PressRestoreBackupData -> launchBackgroundWork(BackgroundKey.DATA_WORK) {
                val command = DataWorkCommand.RestoreBackupData(event.uri)
                dataWorkProcessor.work(command).collectAndHandleWork()
            }
            is SettingsEvent.PressSaveBackupData -> launchBackgroundWork(BackgroundKey.DATA_WORK) {
                val command = DataWorkCommand.SaveBackupData(event.uri)
                dataWorkProcessor.work(command).collectAndHandleWork()
            } 
            is SettingsEvent.PressDonateButton -> {
                consumeOutput(SettingsOutput.NavigateToDonate)
            }
        }
    }

    override suspend fun reduce(
        action: SettingsAction,
        currentState: SettingsState,
    ) = when (action) {
        is SettingsAction.ChangeAllSettings -> currentState.copy(
            themeSettings = action.settings.themeSettings,
            tasksSettings = action.settings.tasksSettings,
            isBackupLoading = false,
        )
        is SettingsAction.ShowLoadingBackup -> currentState.copy(
            isBackupLoading = action.isLoading,
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_SETTINGS, SETTINGS_ACTION, DATA_WORK,
    }

     class Factory @Inject constructor(
         private val settingsWorkProcessor: SettingsWorkProcessor,
         private val dataWorkProcessor: DataWorkProcessor,
         private val coroutineManager: CoroutineManager,
     ) : BaseOnlyOutComposeStore.Factory<SettingsComposeStore, SettingsState> {

         override fun create(savedState: SettingsState): SettingsComposeStore {
             return SettingsComposeStore(
                 settingsWorkProcessor = settingsWorkProcessor,
                 dataWorkProcessor = dataWorkProcessor,
                 stateCommunicator = StateCommunicator.Default(savedState),
                 effectCommunicator = EffectCommunicator.Default(),
                 coroutineManager = coroutineManager,
             )
         }
     }
}