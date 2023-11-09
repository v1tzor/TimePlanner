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
package ru.aleshin.features.home.impl.domain.interactors

import kotlinx.coroutines.flow.first
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.shiftMinutes
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.TimeShiftException
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domain.repository.TemplatesRepository
import ru.aleshin.features.home.api.domain.repository.TimeTaskRepository
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.domain.entities.TimeTaskImportanceException
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 04.04.2023.
 */
internal interface TimeShiftInteractor {

    suspend fun shiftUpTimeTask(task: TimeTask, shiftValue: Int): DomainResult<HomeFailures, TimeTask?>

    suspend fun shiftDownTimeTask(task: TimeTask, shiftValue: Int): UnitDomainResult<HomeFailures>

    class Base @Inject constructor(
        private val timeTaskRepository: TimeTaskRepository,
        private val templatesRepository: TemplatesRepository,
        private val eitherWrapper: HomeEitherWrapper,
    ) : TimeShiftInteractor {

        override suspend fun shiftUpTimeTask(
            task: TimeTask,
            shiftValue: Int,
        ) = eitherWrapper.wrap {
            val allTimeTasks = timeTaskRepository.fetchAllTimeTaskByDate(task.date).sortedBy {
                it.timeRange.from
            }
            val nextTimeTask = allTimeTasks.firstOrNull { it.timeRange.from >= task.timeRange.to }
            val nextTimeTaskTemplate = templatesRepository.fetchAllTemplates().first().find { template ->
                nextTimeTask?.let { template.equalsIsTemplate(it) } ?: false
            }
            val nextTime = nextTimeTask?.timeRange
            val shiftTime = task.timeRange.to.shiftMinutes(shiftValue)

            return@wrap if (nextTime == null || nextTime.from.time - shiftTime.time >= shiftValue) {
                when (shiftTime.isCurrentDay(task.timeRange.to)) {
                    true -> null.apply {
                        val shiftTask = task.copy(timeRange = task.timeRange.copy(to = shiftTime))
                        timeTaskRepository.updateTimeTask(timeTask = shiftTask)
                    }
                    false -> throw TimeShiftException()
                }
            } else {
                when (nextTime.to.time - shiftTime.time > 0) {
                    true -> {
                        if (nextTimeTask.isImportant || nextTimeTaskTemplate?.checkDateIsRepeat(Date()) == true) {
                            throw TimeTaskImportanceException()
                        }
                        val shiftTask = task.copy(timeRange = task.timeRange.copy(to = shiftTime))
                        val nextShiftTask = nextTimeTask.copy(timeRange = nextTimeTask.timeRange.copy(from = shiftTime))
                        timeTaskRepository.updateTimeTaskList(listOf(shiftTask, nextShiftTask))
                        return@wrap nextShiftTask
                    }
                    false -> throw TimeShiftException()
                }
            }
        }

        override suspend fun shiftDownTimeTask(
            task: TimeTask,
            shiftValue: Int,
        ) = eitherWrapper.wrap {
            val shiftTime = task.timeRange.to.shiftMinutes(-shiftValue)
            if (shiftTime.time - task.timeRange.from.time > 0) {
                val timeRanges = task.timeRange.copy(to = shiftTime)
                timeTaskRepository.updateTimeTask(task.copy(timeRange = timeRanges))
            } else {
                throw TimeShiftException()
            }
        }
    }
}
