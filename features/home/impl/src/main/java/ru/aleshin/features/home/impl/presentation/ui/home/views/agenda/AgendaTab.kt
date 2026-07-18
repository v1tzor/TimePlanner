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
package ru.aleshin.features.home.impl.presentation.ui.home.views.agenda

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.aleshin.core.presentation.models.tasks.TimeTaskDetailsUi
import ru.aleshin.features.home.impl.presentation.ui.home.TimeTasksSection
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeState
import java.util.Date

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
@Composable
internal fun AgendaTab(
    state: HomeState,
    modifier: Modifier = Modifier,
    onCreateSchedule: () -> Unit,
    onTimeTaskEdit: (TimeTaskDetailsUi) -> Unit,
    onTaskDoneChange: (TimeTaskDetailsUi) -> Unit,
    onTimeTaskAdd: (Date, Date) -> Unit,
    onTimeTaskIncrease: (TimeTaskDetailsUi) -> Unit,
    onTimeTaskReduce: (TimeTaskDetailsUi) -> Unit,
) {
    TimeTasksSection(
        modifier = modifier,
        selectedDate = state.selectedDate,
        dateStatus = state.schedule?.dateStatus,
        timeTasks = state.schedule?.timeTasks ?: emptyList(),
        timeTaskViewStatus = state.taskViewStatus,
        onCreateSchedule = onCreateSchedule,
        onTimeTaskEdit = onTimeTaskEdit,
        onTaskDoneChange = onTaskDoneChange,
        onTimeTaskAdd = onTimeTaskAdd,
        onTimeTaskIncrease = onTimeTaskIncrease,
        onTimeTaskReduce = onTimeTaskReduce,
    )
}
