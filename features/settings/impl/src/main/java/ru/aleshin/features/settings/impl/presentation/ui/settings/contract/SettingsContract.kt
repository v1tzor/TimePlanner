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
package ru.aleshin.features.settings.impl.presentation.ui.settings.contract

import android.net.Uri
import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.features.settings.impl.domain.common.SettingsFailures
import ru.aleshin.features.settings.impl.presentation.models.SettingsUi
import ru.aleshin.features.settings.impl.presentation.models.TasksSettingsUi
import ru.aleshin.features.settings.impl.presentation.models.ThemeSettingsUi

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
@Serializable
internal data class SettingsState(
    val themeSettings: ThemeSettingsUi? = null,
    val tasksSettings: TasksSettingsUi? = null,
    val isBackupLoading: Boolean = false,
) : StoreState

internal sealed class SettingsEvent : StoreEvent {
    data object Init : SettingsEvent()
    data object PressResetButton : SettingsEvent()
    data object PressClearDataButton : SettingsEvent()
    data object PressDonateButton : SettingsEvent()
    data object PressBackIcon : SettingsEvent()
    data class PressSaveBackupData(val uri: Uri) : SettingsEvent()
    data class PressRestoreBackupData(val uri: Uri) : SettingsEvent()
    data class ChangedThemeSettings(val themeSettings: ThemeSettingsUi) : SettingsEvent()
    data class ChangedTasksSettings(val tasksSettings: TasksSettingsUi) : SettingsEvent()
}

internal sealed class SettingsEffect : StoreEffect {
    data class ShowError(val failures: SettingsFailures) : SettingsEffect()
}

internal sealed class SettingsAction : StoreAction {
    data class ShowLoadingBackup(val isLoading: Boolean) : SettingsAction()
    data class ChangeAllSettings(val settings: SettingsUi) : SettingsAction()
}

internal sealed class SettingsOutput : BaseOutput {
    data object NavigateToBack : SettingsOutput()
    data object NavigateToDonate : SettingsOutput()
}