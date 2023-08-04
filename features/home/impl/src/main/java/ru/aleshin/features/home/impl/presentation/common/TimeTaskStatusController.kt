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
package ru.aleshin.features.home.impl.presentation.common

import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.api.domain.common.TimeTaskStatusChecker
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTaskStatus
import ru.aleshin.features.home.impl.presentation.models.schedules.TimeTaskUi
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 04.08.2023.
 */
internal interface TimeTaskStatusController {

    fun updateStatus(timeTask: TimeTaskUi): TimeTaskUi

    class Base @Inject constructor(
        private val statusManager: TimeTaskStatusChecker,
        private val dateManager: DateManager,
    ) : TimeTaskStatusController {

        override fun updateStatus(timeTask: TimeTaskUi) = with(timeTask) {
            val currentTime = dateManager.fetchCurrentDate()
            val status = statusManager.fetchStatus(timeToTimeRange(), currentTime)
            when (status) {
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
