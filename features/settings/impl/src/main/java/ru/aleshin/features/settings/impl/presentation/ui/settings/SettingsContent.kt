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
package ru.aleshin.features.settings.impl.presentation.ui.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.settings.impl.presentation.mappers.mapToMessage
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEffect
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEvent
import ru.aleshin.features.settings.impl.presentation.ui.settings.store.SettingsComponent
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.SettingsTopAppBar
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
@Composable
internal fun SettingsContent(
    modifier: Modifier = Modifier,
    settingsComponent: SettingsComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = settingsComponent.store
    val state by store.stateAsState()
    val strings = SettingsThemeRes.strings
    val snackbarState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SettingsTopAppBar(
                isCompact = adaptiveLayoutInfo.isCompactWidth,
                onResetToDefaultClick = { store.dispatchEvent(SettingsEvent.PressResetButton) },
                onBackIconClick = { store.dispatchEvent(SettingsEvent.PressBackIcon) },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarState)
        },
        contentWindowInsets = WindowInsets()
    ) { contentPadding ->
        SettingsLayout(
            modifier = Modifier.padding(contentPadding),
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onEvent = store::dispatchEvent,
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is SettingsEffect.ShowError -> snackbarState.showSnackbar(
                message = effect.failures.mapToMessage(strings),
                withDismissAction = true,
            )
        }
    }
}
