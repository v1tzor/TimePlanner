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
package ru.aleshin.features.editor.impl.domain.interactors

import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.TimeOverlayException
import ru.aleshin.core.utils.managers.TimeOverlayManager
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domains.repository.TimeTaskRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.03.2023.
 */
internal interface TimeTaskInteractor {

    suspend fun addTimeTask(timeTask: TimeTask): Either<EditorFailures, Unit>
    suspend fun updateTimeTask(timeTask: TimeTask): Either<EditorFailures, Unit>
    suspend fun deleteTimeTask(key: Long): Either<EditorFailures, Unit>

    class Base @Inject constructor(
        private val timeTaskRepository: TimeTaskRepository,
        private val eitherWrapper: EditorEitherWrapper,
        private val overlayManager: TimeOverlayManager,
    ) : TimeTaskInteractor {

        override suspend fun addTimeTask(timeTask: TimeTask) = eitherWrapper.wrap {
            val allTimeTask = timeTaskRepository.fetchAllTimeTaskByDate(timeTask.date)

            checkIsOverlay(allTimeTask.map { it.timeRanges }, timeTask.timeRanges) {
                timeTaskRepository.addTimeTasks(listOf(timeTask.copy(key = generateUniqueKey())))
            }
        }

        override suspend fun updateTimeTask(timeTask: TimeTask) = eitherWrapper.wrap {
            val allTimeTask = timeTaskRepository.fetchAllTimeTaskByDate(timeTask.date).toMutableList()
            allTimeTask.removeAll { it.key == timeTask.key }

            checkIsOverlay(allTimeTask.map { it.timeRanges }, timeTask.timeRanges) {
                timeTaskRepository.updateTimeTask(timeTask)
            }
        }

        override suspend fun deleteTimeTask(key: Long) = eitherWrapper.wrap {
            timeTaskRepository.deleteTimeTask(key)
        }

        private suspend fun checkIsOverlay(
            allRanges: List<TimeRange>,
            range: TimeRange,
            block: suspend () -> Unit,
        ) = overlayManager.isOverlay(range, allRanges).let { result ->
            if (result.isOverlay) {
                throw TimeOverlayException(result.leftTimeBorder, result.rightTimeBorder)
            } else {
                block()
            }
        }
    }
}
