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
package ru.aleshin.features.home.impl.presentation.mapppers

import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.features.home.impl.domain.entities.TimelineSchedule
import ru.aleshin.features.home.impl.domain.entities.TimelineTimeTask
import ru.aleshin.features.home.impl.presentation.models.TimelineScheduleUi
import ru.aleshin.features.home.impl.presentation.models.TimelineTimeTaskUi

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
internal fun TimelineSchedule.mapToUi() = TimelineScheduleUi(
    date = date,
    dayTimeRange = dayTimeRange,
    initialTime = initialTime,
    timeStep = timeStep,
    minimumTaskDuration = minimumTaskDuration,
    timeTasks = timeTasks.map { timeTask -> timeTask.mapToUi() },
    freeTimeRanges = freeTimeRanges,
)

internal fun TimelineTimeTask.mapToUi() = TimelineTimeTaskUi(
    timeTask = timeTask.mapToUi(),
    executionStatus = executionStatus,
    visibleTimeRange = visibleTimeRange,
    minimumStartTime = minimumStartTime,
    maximumEndTime = maximumEndTime,
    canMove = canMove,
    canResizeStart = canResizeStart,
    canResizeEnd = canResizeEnd,
)
