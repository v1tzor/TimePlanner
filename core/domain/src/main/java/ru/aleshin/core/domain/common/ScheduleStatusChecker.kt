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
package ru.aleshin.core.domain.common

import ru.aleshin.core.domain.entities.schedules.DailyScheduleStatus
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.managers.DateManager
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 24.03.2023.
 */
interface ScheduleStatusChecker {

    fun fetchStatus(scheduleDate: Date): DailyScheduleStatus

    fun fetchProgress(timeTasks: List<TimeTask>): Float

    class Base @Inject constructor(
        private val dateManager: DateManager,
    ) : ScheduleStatusChecker {

        override fun fetchStatus(scheduleDate: Date): DailyScheduleStatus {
            val currentDate = dateManager.fetchBeginningCurrentDay()

            return if (scheduleDate.time > currentDate.time) {
                DailyScheduleStatus.PLANNED
            } else if (scheduleDate.time < currentDate.time) {
                DailyScheduleStatus.REALIZED
            } else {
                DailyScheduleStatus.ACCOMPLISHMENT
            }
        }

        override fun fetchProgress(timeTasks: List<TimeTask>): Float {
            val currentTime = dateManager.fetchCurrentDate().time

            return when (timeTasks.isEmpty()) {
                true -> 0f
                false -> timeTasks.count { currentTime > it.timeRange.to.time && it.isCompleted } / timeTasks.size.toFloat()
            }
        }
    }
}
