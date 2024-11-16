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

import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.EffectResult
import ru.aleshin.core.utils.platform.screenmodel.work.FlowWorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.features.settings.impl.domain.interactors.SettingsInteractor
import ru.aleshin.features.settings.impl.presentation.mappers.mapToDomain
import ru.aleshin.features.settings.impl.presentation.mappers.mapToUi
import ru.aleshin.features.settings.impl.presentation.models.TasksSettingsUi
import ru.aleshin.features.settings.impl.presentation.models.ThemeSettingsUi
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsAction
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEffect
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
internal interface SettingsWorkProcessor : FlowWorkProcessor<SettingsWorkCommand, SettingsAction, SettingsEffect> {

    class Base @Inject constructor(
        private val settingsInteractor: SettingsInteractor,
    ) : SettingsWorkProcessor {

        override suspend fun work(command: SettingsWorkCommand) = when (command) {
            is SettingsWorkCommand.LoadAllSettings -> loadAllSettingsWork()
            is SettingsWorkCommand.UpdateThemeSettings -> updateThemeSettingsWork(command.settings)
            is SettingsWorkCommand.UpdateTasksSettings -> updateTasksSettingsWork(command.settings)
            is SettingsWorkCommand.ResetSettings -> resetSettingsWork()
        }

        private suspend fun loadAllSettingsWork() = flow {
            settingsInteractor.fetchAllSettings().collectAndHandle(
                onLeftAction = { emit(EffectResult(SettingsEffect.ShowError(it))) },
                onRightAction = { emit(ActionResult(SettingsAction.ChangeAllSettings(it.mapToUi()))) }
            )
        }

        private suspend fun updateThemeSettingsWork(settings: ThemeSettingsUi) = flow {
            settingsInteractor.updateThemeSettings(settings.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(SettingsEffect.ShowError(it))) }
            )
        }

        private suspend fun updateTasksSettingsWork(settings: TasksSettingsUi) = flow {
            settingsInteractor.updateTasksSettings(settings.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(SettingsEffect.ShowError(it))) }
            )
        }

        private suspend fun resetSettingsWork() = flow {
            settingsInteractor.resetAllSettings().handle(
                onLeftAction = { emit(EffectResult(SettingsEffect.ShowError(it))) }
            )
        }
    }
}

internal sealed class SettingsWorkCommand : WorkCommand {
    data object LoadAllSettings : SettingsWorkCommand()
    data object ResetSettings : SettingsWorkCommand()
    data class UpdateThemeSettings(val settings: ThemeSettingsUi) : SettingsWorkCommand()
    data class UpdateTasksSettings(val settings: TasksSettingsUi) : SettingsWorkCommand()
}
