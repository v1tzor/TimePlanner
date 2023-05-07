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
package ru.aleshin.features.home.impl.domain.interactors

import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.api.domains.common.ScheduleStatusManager
import ru.aleshin.features.home.api.domains.entities.schedules.Schedule
import ru.aleshin.features.home.api.domains.repository.ScheduleRepository
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import java.util.*
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal interface ScheduleInteractor {

    suspend fun fetchScheduleByDate(date: Long): Either<HomeFailures, Schedule?>

    suspend fun createSchedule(requiredDay: Date): Either<HomeFailures, Unit>

    class Base @Inject constructor(
        private val scheduleRepository: ScheduleRepository,
        private val statusManager: ScheduleStatusManager,
        private val dateManager: DateManager,
        private val eitherWrapper: HomeEitherWrapper,
    ) : ScheduleInteractor {

        override suspend fun fetchScheduleByDate(date: Long) = eitherWrapper.wrap {
            scheduleRepository.fetchScheduleByDate(date)?.let { schedule ->
                val timeTasks = schedule.timeTasks.sortedBy { timeTask -> timeTask.timeRanges.to }
                schedule.copy(timeTasks = timeTasks)
            }
        }

        override suspend fun createSchedule(requiredDay: Date) = eitherWrapper.wrap {
            val currentDate = dateManager.fetchBeginningCurrentDay()
            val status = statusManager.fetchState(requiredDay, currentDate)
            val schedule = Schedule(date = requiredDay.time, status = status)
            scheduleRepository.createSchedule(schedule)
        }
    }
}
