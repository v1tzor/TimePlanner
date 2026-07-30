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
package ru.aleshin.timeplanner.presentation.ui.main.contract

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.overview.api.OverviewConfig
import ru.aleshin.timeplanner.core.ui.theme.material.ColorsUiType
import ru.aleshin.timeplanner.core.ui.theme.material.ThemeUiType
import ru.aleshin.timeplanner.core.ui.theme.tokens.LanguageUiType
import ru.aleshin.timeplanner.presentation.ui.tabs.component.TabNavigationComponent.TabNavigationConfig

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Immutable
@Serializable
data class MainState(
    val language: LanguageUiType = LanguageUiType.DEFAULT,
    val theme: ThemeUiType = ThemeUiType.DEFAULT,
    val colors: ColorsUiType = ColorsUiType.PINK,
    val isEnableDynamicColors: Boolean = false,
    val secureMode: Boolean = false,
) : StoreState

sealed class MainEvent : StoreEvent {
    data class Init(val input: MainInput, val isRestore: Boolean) : MainEvent()
    data class ProcessDeepLink(val screenTarget: DeepLinkTarget) : MainEvent()
    data class ProcessShare(val shareTarget: ShareTarget) : MainEvent()
}

sealed class MainEffect : StoreEffect

sealed class MainAction : StoreAction {
    data class ChangeSettings(
        val language: LanguageUiType,
        val theme: ThemeUiType,
        val colors: ColorsUiType,
        val enableDynamicColors: Boolean,
        val secureMode: Boolean,
    ) : MainAction()
}

data class MainInput(
    val initialDeepLinkTarget: DeepLinkTarget?,
    val initialShareTarget: ShareTarget?,
) : BaseInput

sealed class MainOutput : BaseOutput {
    data class NavigateToEditor(val config: EditorConfig) : MainOutput()
    data class NavigateToOverview(val config: OverviewConfig) : MainOutput()
    data class NavigateToTabNavigation(val config: TabNavigationConfig) : MainOutput()
}
