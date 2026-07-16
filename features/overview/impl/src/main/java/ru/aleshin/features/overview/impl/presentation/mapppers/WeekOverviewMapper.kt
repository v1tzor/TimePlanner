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
package ru.aleshin.features.overview.impl.presentation.mapppers

import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.features.overview.impl.domain.entities.DaySummary
import ru.aleshin.features.overview.impl.domain.entities.WeekOverview
import ru.aleshin.features.overview.impl.domain.entities.WeekSchedule
import ru.aleshin.features.overview.impl.presentation.models.overview.DaySummaryUi
import ru.aleshin.features.overview.impl.presentation.models.overview.WeekOverviewUi
import ru.aleshin.features.overview.impl.presentation.models.overview.WeekScheduleUi

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
internal fun WeekOverview.mapToUi() = WeekOverviewUi(
    tasksCount = tasksCount,
    schedules = schedules.map { schedule -> schedule.mapToUi() },
)

internal fun WeekSchedule.mapToUi() = WeekScheduleUi(
    date = date,
    timeTasks = timeTasks.map { task -> task.mapToUi() },
    summary = summary.mapToUi(),
)

internal fun DaySummary.mapToUi() = DaySummaryUi(
    freeTime = freeTime,
    workload = workload,
    progress = progress,
)
