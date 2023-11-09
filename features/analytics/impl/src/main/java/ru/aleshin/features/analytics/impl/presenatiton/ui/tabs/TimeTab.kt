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
package ru.aleshin.features.analytics.impl.presenatiton.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsViewState
import ru.aleshin.features.analytics.impl.presenatiton.ui.views.CategoriesAnalyticsSection
import ru.aleshin.features.analytics.impl.presenatiton.ui.views.PlanningAnalyticsSection

/**
 * @author Stanislav Aleshin on 20.04.2023.
 */
@Composable
internal fun TimeTab(
    state: AnalyticsViewState,
    onTimePeriodChanged: (TimePeriod) -> Unit,
    onRefresh: () -> Unit,
) {
    val analytics = state.scheduleAnalytics
    val scrollState = rememberScrollState()
    // Pullrefresh not available for Material Design 3
    val refreshState = rememberSwipeRefreshState(
        isRefreshing = state.scheduleAnalytics?.categoriesAnalytics == null,
    )
    SwipeRefresh(
        state = refreshState,
        onRefresh = onRefresh,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 24.dp).verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PlanningAnalyticsSection(
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isLoading,
                planningAnalytics = analytics?.planningAnalytic,
            )
            Divider()
            CategoriesAnalyticsSection(
                isLoading = state.isLoading,
                timePeriod = state.timePeriod,
                categoriesAnalytics = analytics?.categoriesAnalytics,
                onTimePeriodChanged = onTimePeriodChanged,
            )
        }
    }
}
