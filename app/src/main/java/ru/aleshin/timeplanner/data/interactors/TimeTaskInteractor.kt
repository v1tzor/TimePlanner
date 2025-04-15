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
package ru.aleshin.timeplanner.data.interactors

import kotlinx.coroutines.flow.map
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.timeplanner.data.common.MainEitherWrapper
import ru.aleshin.timeplanner.data.common.MainFailures
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
interface TimeTaskInteractor {

    suspend fun fetchTimeTasksByDate(date: Date): FlowDomainResult<MainFailures, List<TimeTask>>

    class Base @Inject constructor(
        private val scheduleRepository: ScheduleRepository,
        private val eitherWrapper: MainEitherWrapper,
    ) : TimeTaskInteractor {
        override suspend fun fetchTimeTasksByDate(date: Date) = eitherWrapper.wrapFlow {
            scheduleRepository.fetchScheduleByDate(date.time).map {
                it?.timeTasks ?: emptyList()
            }
        }
    }
}
