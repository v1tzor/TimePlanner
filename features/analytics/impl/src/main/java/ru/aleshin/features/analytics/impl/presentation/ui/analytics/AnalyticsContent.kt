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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.analytics.impl.presentation.mappers.mapToMessage
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsLayoutDefaults
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEffect
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.store.AnalyticsComponent
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsContentPlaceholder
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.AnalyticsTopAppBar
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsDateRangeDialog
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.Scaffold
import ru.aleshin.timeplanner.core.ui.views.animations.AnimatedLoadingContent
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AnalyticsContent(
    modifier: Modifier = Modifier,
    component: AnalyticsComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = component.store
    val state by store.stateAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val strings = AnalyticsThemeRes.strings
    var isOpenCalendar by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            AnalyticsTopAppBar(
                isCompact = adaptiveLayoutInfo.isCompactWidth,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                ErrorSnackbar(snackbarData = snackbarData)
            }
        },
    ) { contentPadding ->
        AnimatedLoadingContent(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            isLoading = state.isLoading,
            targetValue = state,
        ) { contentState ->
            if (contentState != null) {
                AnalyticsLayout(
                    modifier = Modifier.fillMaxSize(),
                    state = contentState,
                    adaptiveLayoutInfo = adaptiveLayoutInfo,
                    onOpenCalendar = { isOpenCalendar = true },
                    onEvent = store::dispatchEvent,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = AnalyticsLayoutDefaults.CompactContentPadding,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    userScrollEnabled = false,
                ) {
                    AnalyticsContentPlaceholder()
                }
            }
        }
    }

    val range = state.range
    if (isOpenCalendar && range != null) {
        AnalyticsDateRangeDialog(
            initialFrom = range.from.time,
            initialTo = range.to.time,
            onDismiss = { isOpenCalendar = false },
            onConfirm = { fromPickerToken, toPickerToken ->
                isOpenCalendar = false
                store.dispatchEvent(
                    AnalyticsEvent.ConfirmCalendar(
                        fromPickerToken = fromPickerToken,
                        toPickerToken = toPickerToken,
                    ),
                )
            },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is AnalyticsEffect.ShowFailure -> snackbarHostState.showSnackbar(
                message = effect.failure.mapToMessage(strings = strings),
                withDismissAction = true,
            )
        }
    }
}
