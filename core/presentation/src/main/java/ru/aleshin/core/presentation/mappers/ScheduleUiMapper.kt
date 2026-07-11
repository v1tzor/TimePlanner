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
package ru.aleshin.core.presentation.mappers

import ru.aleshin.core.domain.entities.schedules.OverviewSchedule
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.schedules.ScheduleDetails
import ru.aleshin.core.presentation.models.schedules.OverviewScheduleUi
import ru.aleshin.core.presentation.models.schedules.ScheduleDetailsUi

/**
 * @author Stanislav Aleshin on 11.03.2023.
 */
fun ScheduleDetails.mapToUi() = ScheduleDetailsUi(
    date = date,
    dateStatus = dateStatus,
    timeTasks = timeTasks.map { it.mapToUi() },
    progress = progress
)

fun OverviewSchedule.mapToUi() = OverviewScheduleUi(
    date = date,
    dateStatus = dateStatus,
    unexecutedTask = unexecutedTask,
    completedTask = completedTask,
    plannedTask = plannedTask,
    progress = progress
)

fun ScheduleDetailsUi.mapToDomain() = Schedule(
    date = date,
    timeTasks = timeTasks.map { it.mapToDomain() },
)
