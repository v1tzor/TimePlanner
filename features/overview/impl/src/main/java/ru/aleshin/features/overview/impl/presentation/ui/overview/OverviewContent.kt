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
package ru.aleshin.features.overview.impl.presentation.ui.overview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.overview.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewEffect
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewEvent
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewComponent
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.OverviewTopAppBar
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.UndefinedTasksBatchEditorDialog
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun OverviewContent(
    modifier: Modifier = Modifier,
    component: OverviewComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = component.store
    val state by store.stateAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val mainScrollState = rememberScrollState()
    val supportingScrollState = rememberScrollState()
    val pullToRefreshState = rememberPullToRefreshState()
    val strings = OverviewThemeRes.strings
    val layoutMode = OverviewLayoutMode.from(adaptiveLayoutInfo)

    Scaffold(
        modifier = modifier,
        topBar = {
            OverviewTopAppBar(
                isCompact = adaptiveLayoutInfo.isCompactWidth,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData -> ErrorSnackbar(snackbarData) },
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            state = pullToRefreshState,
            onRefresh = { store.dispatchEvent(OverviewEvent.Refresh) },
            isRefreshing = state.isLoading,
        ) {
            OverviewLayout(
                modifier = Modifier.fillMaxSize(),
                state = state,
                adaptiveLayoutInfo = adaptiveLayoutInfo,
                layoutMode = layoutMode,
                mainScrollState = mainScrollState,
                supportingScrollState = supportingScrollState,
                onEvent = store::dispatchEvent,
            )
        }
    }

    val sharedTextTasks = state.sharedTextTasks
    if (sharedTextTasks != null) {
        UndefinedTasksBatchEditorDialog(
            tasks = sharedTextTasks,
            categories = state.categories,
            onDismiss = { store.dispatchEvent(OverviewEvent.DismissBatchUndefinedTasks) },
            onConfirm = { store.dispatchEvent(OverviewEvent.ConfirmBatchUndefinedTasks(it)) },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is OverviewEffect.ShowError -> snackbarHostState.showSnackbar(
                message = effect.failures.mapToMessage(strings),
                withDismissAction = true,
            )
        }
    }
}
