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
package ru.aleshin.features.overview.impl.presentation.ui.goal.details

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.overview.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsEffect
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsEvent
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsComponent
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.views.GoalDetailsTopAppBar
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.Scaffold
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun GoalDetailsContent(
    modifier: Modifier = Modifier,
    component: GoalDetailsComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = component.store
    val state by store.stateAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }
    val strings = OverviewThemeRes.strings

    Scaffold(
        modifier = modifier,
        topBar = {
            GoalDetailsTopAppBar(
                onBackClick = { store.dispatchEvent(GoalDetailsEvent.PressBack) },
                onEditClick = { store.dispatchEvent(GoalDetailsEvent.PressEdit) },
                onDeleteClick = { isDeleteDialogOpen = true },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData -> ErrorSnackbar(snackbarData) },
            )
        },
        contentWindowInsets = WindowInsets(),
    ) { contentPadding ->
        GoalDetailsLayout(
            modifier = Modifier.padding(contentPadding),
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onEvent = store::dispatchEvent,
        )
    }

    if (isDeleteDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDeleteDialogOpen = false },
            title = {
                Text(text = OverviewThemeRes.strings.deleteGoalTitle)
            },
            text = {
                Text(text = OverviewThemeRes.strings.deleteGoalConfirmation)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleteDialogOpen = false
                        store.dispatchEvent(GoalDetailsEvent.DeleteGoal)
                    },
                ) {
                    Text(text = OverviewThemeRes.strings.deleteGoalTitle)
                }
            },
            dismissButton = {
                TextButton(onClick = { isDeleteDialogOpen = false }) {
                    Text(text = OverviewThemeRes.strings.navToBackTitle)
                }
            },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is GoalDetailsEffect.ShowError -> snackbarHostState.showSnackbar(
                message = effect.failure.mapToMessage(strings),
                withDismissAction = true,
            )
            is GoalDetailsEffect.ShowGoalDeleted -> {
                val result = snackbarHostState.showSnackbar(
                    message = strings.goalDeletedMessage,
                    actionLabel = strings.undoTitle,
                    withDismissAction = true,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    store.dispatchEvent(GoalDetailsEvent.RestoreGoal(effect.goal))
                } else {
                    store.dispatchEvent(GoalDetailsEvent.PressBack)
                }
            }
        }
    }
}