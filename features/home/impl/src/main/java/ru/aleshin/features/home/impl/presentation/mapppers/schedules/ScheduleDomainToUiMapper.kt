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
package ru.aleshin.features.home.impl.presentation.mapppers.schedules

import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.functional.Mapper
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.api.domain.entities.schedules.Schedule
import ru.aleshin.features.home.impl.presentation.models.schedules.ScheduleUi
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 11.03.2023.
 */
internal interface ScheduleDomainToUiMapper : Mapper<Schedule, ScheduleUi> {

    class Base @Inject constructor(
        private val timeTaskMapperToUi: TimeTaskDomainToUiMapper,
        private val dateManager: DateManager,
    ) : ScheduleDomainToUiMapper {
        override fun map(input: Schedule) = ScheduleUi(
            date = input.date.mapToDate(),
            dateStatus = input.status,
            timeTasks = input.timeTasks.map { timeTaskMapperToUi.map(it, false) },
            progress = when (input.timeTasks.isEmpty()) {
                true -> 0f
                false -> input.timeTasks.count { dateManager.fetchCurrentDate().time > it.timeRange.to.time && it.isCompleted } /
                    input.timeTasks.size.toFloat()
            },
        )
    }
}

internal fun ScheduleUi.mapToDomain() = Schedule(
    date = date.time,
    status = dateStatus,
    timeTasks = timeTasks.map { it.mapToDomain() },
)
