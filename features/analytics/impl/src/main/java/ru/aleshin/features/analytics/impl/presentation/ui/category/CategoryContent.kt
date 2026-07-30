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
package ru.aleshin.features.analytics.impl.presentation.ui.category

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.analytics.impl.presentation.mappers.mapToMessage
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEffect
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryEvent
import ru.aleshin.features.analytics.impl.presentation.ui.category.store.CategoryComponent
import ru.aleshin.features.analytics.impl.presentation.ui.category.views.CategoryTopAppBar
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsDateRangeDialog
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.Scaffold
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CategoryContent(
    modifier: Modifier = Modifier,
    component: CategoryComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = component.store
    val state by store.stateAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val strings = AnalyticsThemeRes.strings
    var isDateRangeDialogOpen by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CategoryTopAppBar(
                isCompact = adaptiveLayoutInfo.isCompactWidth,
                title = state.category?.fetchName().orEmpty(),
                categoryId = state.category?.id,
                defaultType = state.category?.defaultType,
                isLoading = state.isLoading && state.category == null,
                onBack = { store.dispatchEvent(CategoryEvent.NavigateBack) },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                ErrorSnackbar(snackbarData = snackbarData)
            }
        },
        contentWindowInsets = WindowInsets(),
    ) { contentPadding ->
        CategoryLayout(
            modifier = Modifier.padding(contentPadding),
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onOpenCalendar = { isDateRangeDialogOpen = true },
            onEvent = store::dispatchEvent,
        )
    }

    val range = state.range
    if (isDateRangeDialogOpen && range != null) {
        AnalyticsDateRangeDialog(
            initialFrom = range.from.time,
            initialTo = range.to.time,
            onDismiss = { isDateRangeDialogOpen = false },
            onConfirm = { fromPickerToken, toPickerToken ->
                isDateRangeDialogOpen = false
                store.dispatchEvent(
                    CategoryEvent.ConfirmCalendar(
                        fromPickerToken = fromPickerToken,
                        toPickerToken = toPickerToken,
                    ),
                )
            },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is CategoryEffect.ShowFailure -> snackbarHostState.showSnackbar(
                message = effect.failure.mapToMessage(strings = strings),
                withDismissAction = true,
            )
        }
    }
}
