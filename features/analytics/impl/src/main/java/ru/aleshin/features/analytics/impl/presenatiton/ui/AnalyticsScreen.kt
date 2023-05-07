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
 * imitations under the License.
 */
package ru.aleshin.features.analytics.impl.presenatiton.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import ru.aleshin.core.ui.views.ErrorSnackbar
import ru.aleshin.core.ui.views.HorizontalTabsPager
import ru.aleshin.core.ui.views.Scaffold
import ru.aleshin.core.ui.views.TabItem
import ru.aleshin.core.utils.managers.LocalDrawerManager
import ru.aleshin.core.utils.platform.screen.ScreenContent
import ru.aleshin.features.analytics.impl.presenatiton.mappers.mapToMessage
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsTheme
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsEffect
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsViewState
import ru.aleshin.features.analytics.impl.presenatiton.ui.screenmodel.rememberAnalyticsScreenModel
import ru.aleshin.features.analytics.impl.presenatiton.ui.tabs.TimeTab
import ru.aleshin.features.analytics.impl.presenatiton.ui.tabs.WorkLoadTab
import ru.aleshin.features.analytics.impl.presenatiton.ui.views.AnalyticsTopAppBar
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
internal class AnalyticsScreen @Inject constructor() : Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun Content() = ScreenContent(
        screenModel = rememberAnalyticsScreenModel(),
        initialState = AnalyticsViewState(),
    ) { state ->
        AnalyticsTheme {
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            val drawerManager = LocalDrawerManager.current
            val string = AnalyticsThemeRes.strings

            Scaffold(
                content = { paddingValues ->
                    HorizontalTabsPager(
                        modifier = Modifier.padding(paddingValues),
                        tabs = AnalyticsTabItem.values().toList(),
                    ) { tab ->
                        when (tab) {
                            AnalyticsTabItem.TIME -> TimeTab(
                                state = state,
                                onRefresh = { dispatchEvent(AnalyticsEvent.PressRefreshAnalytics) },
                                onTimePeriodChanged = { timePeriod ->
                                    dispatchEvent(AnalyticsEvent.ChangeTimePeriod(timePeriod))
                                },
                            )
                            AnalyticsTabItem.WORKLOAD -> WorkLoadTab(
                                state = state,
                                onRefresh = { dispatchEvent(AnalyticsEvent.PressRefreshAnalytics) },
                                onTimePeriodChanged = { timePeriod ->
                                    dispatchEvent(AnalyticsEvent.ChangeTimePeriod(timePeriod))
                                },
                            )
                        }
                    }
                },
                topBar = {
                    AnalyticsTopAppBar(
                        onMenuButtonClick = { scope.launch { drawerManager?.openDrawer() } },
                    )
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) {
                        ErrorSnackbar(snackbarData = it)
                    }
                },
            )

            handleEffect { effect ->
                when (effect) {
                    is AnalyticsEffect.ShowFailure -> snackbarHostState.showSnackbar(
                        message = effect.failure.mapToMessage(string),
                        withDismissAction = true,
                    )
                }
            }
        }
    }
}

internal enum class AnalyticsTabItem : TabItem {
    TIME {
        override val title: String @Composable get() = AnalyticsThemeRes.strings.timeTabTitle
        override val leadingIcon: Int @Composable get() = AnalyticsThemeRes.icons.pieChart
    },
    WORKLOAD {
        override val title: String @Composable get() = AnalyticsThemeRes.strings.workLoadTabTitle
        override val leadingIcon: Int @Composable get() = AnalyticsThemeRes.icons.barChart
    },
}
