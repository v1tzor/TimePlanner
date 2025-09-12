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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.extensions.isIncludeTime
import ru.aleshin.features.home.impl.presentation.models.schedules.UndefinedTaskUi
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewViewState
import ru.aleshin.features.home.impl.presentation.ui.overview.views.CurrentTimeTaskSection
import ru.aleshin.features.home.impl.presentation.ui.overview.views.SchedulesSection
import ru.aleshin.features.home.impl.presentation.ui.overview.views.UndefinedTaskSection
import java.util.Date

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun OverviewContent(
    modifier: Modifier = Modifier,
    state: OverviewViewState,
    onRefresh: () -> Unit,
    onOpenSchedule: (Date?) -> Unit,
    onOpenAllSchedules: () -> Unit,
    onAddOrUpdateTask: (UndefinedTaskUi) -> Unit,
    onDeleteTask: (UndefinedTaskUi) -> Unit,
    onExecuteTask: (Date, UndefinedTaskUi) -> Unit,
) {
    val scrollState = rememberScrollState()
    val refreshState: PullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = modifier,
        state = refreshState,
        onRefresh = onRefresh,
        isRefreshing = state.currentDate == null && state.schedules.isEmpty(),
    ) {
        Column(
            modifier = Modifier.verticalScroll(state = scrollState, enabled = !state.isLoading),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            SchedulesSection(
                isLoading = state.isLoading,
                currentSchedule = state.currentSchedule,
                schedules = state.schedules,
                onOpenSchedule = { onOpenSchedule(it.date) },
                onOpenAllSchedules = onOpenAllSchedules,
            )
            CurrentTimeTaskSection(
                isLoading = state.isLoading,
                task = state.currentSchedule?.timeTasks?.find {
                    it.timeToTimeRange().isIncludeTime(Date())
                },
                onOpenTask = { onOpenSchedule(null) },
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
