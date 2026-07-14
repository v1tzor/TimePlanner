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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.overview.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewEffect
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewEvent
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewState
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewComponent
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.CurrentTimeTaskSection
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.OverviewTopAppBar
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.SchedulesSection
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.UndefinedTaskSection
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.UndefinedTasksBatchEditorDialog
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import java.util.Date

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Composable
internal fun OverviewContent(
    overviewComponent: OverviewComponent,
    modifier: Modifier = Modifier,
) {
    val store = overviewComponent.store
    val state by store.stateAsState()
    val snackbarState = remember { SnackbarHostState() }
    val strings = OverviewThemeRes.strings

    Scaffold(
        modifier = modifier.fillMaxSize(),
        content = { paddingValues ->
            BaseOverviewContent(
                state = state,
                modifier = Modifier.padding(paddingValues),
                onRefresh = { store.dispatchEvent(OverviewEvent.Refresh) },
                onOpenSchedule = { store.dispatchEvent(OverviewEvent.OpenSchedule(it)) },
                onOpenAllSchedules = { store.dispatchEvent(OverviewEvent.OpenAllSchedules) },
                onAddOrUpdateTask = { store.dispatchEvent(OverviewEvent.CreateOrUpdateUndefinedTask(it)) },
                onDeleteTask = { store.dispatchEvent(OverviewEvent.DeleteUndefinedTask(it)) },
                onExecuteTask = { date, task -> store.dispatchEvent(OverviewEvent.ExecuteUndefinedTask(date, task)) },
            )
        },
        topBar = {
            OverviewTopAppBar()
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarState,
                snackbar = { ErrorSnackbar(it) },
            )
        },
    )

    val sharedTextTasks = state.sharedTextTasks
    if (sharedTextTasks != null) {
        UndefinedTasksBatchEditorDialog(
            tasks = sharedTextTasks,
            categories = state.categories,
            onDismiss = { store.dispatchEvent(OverviewEvent.DismissBatchUndefinedTasks) },
            onConfirm = { store.dispatchEvent(OverviewEvent.ConfirmBatchUndefinedTasks(it)) }
        )
    }

    store.handleEffects { effect ->
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BaseOverviewContent(
    modifier: Modifier = Modifier,
    state: OverviewState,
    onRefresh: () -> Unit,
    onOpenSchedule: (Date?) -> Unit,
    onOpenAllSchedules: () -> Unit,
    onAddOrUpdateTask: (UndefinedTaskUi) -> Unit,
    onDeleteTask: (UndefinedTaskUi) -> Unit,
    onExecuteTask: (Date, UndefinedTaskUi) -> Unit,
) {
    val scrollState = rememberScrollState()
    val refreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = modifier,
        state = refreshState,
        onRefresh = onRefresh,
        isRefreshing = state.isLoading,
    ) {
        Column(
            modifier = Modifier.verticalScroll(state = scrollState, enabled = !state.isLoading),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            SchedulesSection(
                isLoading = state.isLoading,
                currentDate = state.currentDate,
                schedules = state.schedules,
                onOpenSchedule = { onOpenSchedule(it.date) },
                onOpenAllSchedules = onOpenAllSchedules,
            )
            CurrentTimeTaskSection(
                isLoading = state.isLoading,
                task = state.currentTask,
                onOpenTask = { onOpenSchedule(it.date) },
            )
            UndefinedTaskSection(
                isLoading = state.isLoading,
                categories = state.categories,
                tasks = state.undefinedTasks,
                onAddOrUpdateTask = onAddOrUpdateTask,
                onDeleteTask = onDeleteTask,
                onExecuteTask = onExecuteTask,
            )
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

