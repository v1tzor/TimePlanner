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
package ru.aleshin.features.home.api.data.mappers.schedules

import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.functional.Mapper
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.api.data.models.schedules.ScheduleDetails
import ru.aleshin.features.home.api.domain.common.ScheduleStatusChecker
import ru.aleshin.features.home.api.domain.entities.schedules.Schedule
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
interface ScheduleDataToDomainMapper : Mapper<ScheduleDetails, Schedule> {
    class Base @Inject constructor(
        private val scheduleStatusChecker: ScheduleStatusChecker,
        private val dateManager: DateManager,
    ) : ScheduleDataToDomainMapper {
        override fun map(input: ScheduleDetails) = Schedule(
            date = input.dailySchedule.date,
            status = scheduleStatusChecker.fetchState(
                requiredDate = input.dailySchedule.date.mapToDate(),
                currentDate = dateManager.fetchBeginningCurrentDay(),
            ),
            timeTasks = input.timeTasks.map { timeTaskDetails ->
                timeTaskDetails.mapToDomain()
            },
            overlayTimeTasks = input.overlayTimeTasks.map { timeTaskDetails ->
                timeTaskDetails.mapToDomain()
            },
        )
    }
}
