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

import ru.aleshin.core.domain.entities.tasks.TimeTaskDetails
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.utils.managers.DateManager
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 04.08.2023.
 */
interface TimeTaskProgressManager {

    fun updateProgress(timeTask: TimeTaskDetails): TimeTaskDetails

    class Base @Inject constructor(
        private val statusManager: TimeTaskStatusChecker,
        private val dateManager: DateManager,
    ) : TimeTaskProgressManager {

        override fun updateProgress(timeTask: TimeTaskDetails) = with(timeTask) {
            when (val status = statusManager.fetchStatus(timeRange)) {
                TimeTaskStatus.COMPLETED -> copy(
                    executionStatus = status,
                    progress = 1f,
                    leftTime = 0,
                    isCompleted = !(executionStatus == TimeTaskStatus.COMPLETED && !isCompleted),
                )
                TimeTaskStatus.PLANNED -> copy(
                    executionStatus = status,
                    progress = 0f,
                    leftTime = -1,
                    isCompleted = true,
                )
                TimeTaskStatus.RUNNING -> copy(
                    executionStatus = status,
                    progress = dateManager.calculateProgress(startTime, endTime),
                    leftTime = dateManager.calculateLeftTime(endTime),
                    isCompleted = true,
                )
            }
        }
    }
}