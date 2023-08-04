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
package ru.aleshin.features.home.api.domain.common

import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTaskStatus
import java.util.*
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 24.03.2023.
 */
interface TimeTaskStatusChecker {

    fun fetchStatus(timeRange: TimeRange, currentDate: Date): TimeTaskStatus

    class Base @Inject constructor() : TimeTaskStatusChecker {

        override fun fetchStatus(timeRange: TimeRange, currentDate: Date): TimeTaskStatus {
            return if (currentDate.time > timeRange.from.time && currentDate.time < timeRange.to.time) {
                TimeTaskStatus.RUNNING
            } else if (currentDate.time > timeRange.to.time) {
                TimeTaskStatus.COMPLETED
            } else {
                TimeTaskStatus.PLANNED
            }
        }
    }
}
