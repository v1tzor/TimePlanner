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
package ru.aleshin.features.analytics.impl.presenatiton.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.HorizontalTabsPager
import ru.aleshin.timeplanner.core.ui.views.Scaffold
import ru.aleshin.timeplanner.core.ui.views.TabItem
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.features.analytics.impl.presenatiton.mappers.mapToMessage
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsTheme
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsEffect
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsEvent
import ru.aleshin.features.analytics.impl.presenatiton.ui.store.InternalAnalyticsFeatureComponent
import ru.aleshin.features.analytics.impl.presenatiton.ui.tabs.TimeTab
import ru.aleshin.features.analytics.impl.presenatiton.ui.tabs.WorkLoadTab
import ru.aleshin.features.analytics.impl.presenatiton.ui.views.AnalyticsTopAppBar

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal class AnalyticsContentProvider(
    private val analyticsComponent: InternalAnalyticsFeatureComponent,
) : FeatureContentProvider {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun invoke(modifier: Modifier) {
        AnalyticsTheme {
            val store = analyticsComponent.store
            val state by store.stateAsState()
            val snackbarHostState = remember { SnackbarHostState() }
            val string = AnalyticsThemeRes.strings

            Scaffold(
                content = { paddingValues ->
                    HorizontalTabsPager(
                        modifier = Modifier.padding(paddingValues),
                        tabs = AnalyticsTabItem.entries,
                    ) { tab ->
                        when (tab) {
                            AnalyticsTabItem.TIME -> TimeTab(
                                state = state,
                                onTimePeriodChanged = { timePeriod ->
                                    store.dispatchEvent(AnalyticsEvent.ChangeTimePeriod(timePeriod))
                                },
                            )
                            AnalyticsTabItem.WORKLOAD -> WorkLoadTab(
                                state = state,
                                onTimePeriodChanged = { timePeriod ->
                                    store.dispatchEvent(AnalyticsEvent.ChangeTimePeriod(timePeriod))
                                },
                            )
                        }
                    }
                },
                topBar = {
                    AnalyticsTopAppBar()
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) {
                        ErrorSnackbar(snackbarData = it)
                    }
                },
            )

            store.handleEffects { effect ->
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
