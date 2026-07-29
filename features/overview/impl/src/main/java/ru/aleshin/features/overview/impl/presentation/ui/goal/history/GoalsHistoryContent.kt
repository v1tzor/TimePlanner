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
package ru.aleshin.features.overview.impl.presentation.ui.goal.history

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.overview.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryEffect
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryEvent
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryComponent
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.views.GoalsHistoryTopAppBar
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.Scaffold
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun GoalsHistoryContent(
    modifier: Modifier = Modifier,
    component: GoalsHistoryComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = component.store
    val state by store.stateAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val strings = OverviewThemeRes.strings

    Scaffold(
        modifier = modifier,
        topBar = {
            GoalsHistoryTopAppBar(
                onBackClick = { store.dispatchEvent(GoalsHistoryEvent.PressBack) },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData -> ErrorSnackbar(snackbarData) },
            )
        },
    ) { contentPadding ->
        GoalsHistoryLayout(
            modifier = Modifier.padding(contentPadding),
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onEvent = store::dispatchEvent,
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is GoalsHistoryEffect.ShowError -> snackbarHostState.showSnackbar(
                message = effect.failure.mapToMessage(strings),
                withDismissAction = true,
            )
        }
    }
}
