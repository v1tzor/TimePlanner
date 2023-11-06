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
package ru.aleshin.features.home.impl.presentation.ui.overview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import ru.aleshin.core.ui.views.ErrorSnackbar
import ru.aleshin.core.utils.managers.LocalDrawerManager
import ru.aleshin.core.utils.platform.screen.ScreenContent
import ru.aleshin.features.home.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.home.impl.presentation.theme.HomeTheme
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewEffect
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewEvent
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewViewState
import ru.aleshin.features.home.impl.presentation.ui.overview.screenmodel.rememberOverviewScreenModel
import ru.aleshin.features.home.impl.presentation.ui.overview.views.OverviewTopAppBar
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023
 */
internal class OverviewScreen @Inject constructor() : Screen {

    @Composable
    override fun Content() = ScreenContent(
        screenModel = rememberOverviewScreenModel(),
        initialState = OverviewViewState(),
    ) { state ->
        HomeTheme {
            val scope = rememberCoroutineScope()
            val snackbarState = remember { SnackbarHostState() }
            val drawerManager = LocalDrawerManager.current
            val strings = HomeThemeRes.strings

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { paddingValues ->
                    OverviewContent(
                        state = state,
                        modifier = Modifier.padding(paddingValues),
                        onRefresh = { dispatchEvent(OverviewEvent.Refresh) },
                        onOpenSchedule = { dispatchEvent(OverviewEvent.OpenSchedule(it)) },
                        onOpenAllSchedules = { dispatchEvent(OverviewEvent.OpenAllSchedules) },
                        onAddOrUpdateTask = { dispatchEvent(OverviewEvent.CreateOrUpdateUndefinedTask(it)) },
                        onDeleteTask = { dispatchEvent(OverviewEvent.DeleteUndefinedTask(it)) },
                        onExecuteTask = { date, task -> dispatchEvent(OverviewEvent.ExecuteUndefinedTask(date, task)) },
                    )
                },
                topBar = {
                    OverviewTopAppBar(
                        onMenuIconClick = { scope.launch { drawerManager?.openDrawer() } },
                        onOpenSchedule = { dispatchEvent(OverviewEvent.OpenSchedule(null)) },
                    )
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarState,
                        snackbar = { ErrorSnackbar(it) },
                    )
                },
            )

            handleEffect { effect ->
                when (effect) {
                    is OverviewEffect.ShowError -> {
                        snackbarState.showSnackbar(
                            message = effect.failures.mapToMessage(strings),
                            withDismissAction = true,
                        )
                    }
                }
            }
        }
    }
}
