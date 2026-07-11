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
package ru.aleshin.features.editor.impl.domain.interactors

import ru.aleshin.core.domain.entities.tasks.UndefinedTask
import ru.aleshin.core.domain.repository.UndefinedTaskRepository
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
internal interface UndefinedTasksInteractor {

    suspend fun fetchAllUndefinedTasks(): FlowDomainResult<EditorFailures, List<UndefinedTask>>
    suspend fun fetchUndefinedTaskById(taskId: Long): DomainResult<EditorFailures, UndefinedTask?>
    suspend fun deleteUndefinedTask(taskId: Long): UnitDomainResult<EditorFailures>

    class Base @Inject constructor(
        private val undefinedTaskRepository: UndefinedTaskRepository,
        private val eitherWrapper: EditorEitherWrapper,
    ) : UndefinedTasksInteractor {

        override suspend fun fetchAllUndefinedTasks() = eitherWrapper.wrapFlow {
            undefinedTaskRepository.fetchUndefinedTasks()
        }

        override suspend fun fetchUndefinedTaskById(taskId: Long) = eitherWrapper.wrap {
            undefinedTaskRepository.fetchUndefinedTaskByIdOnce(taskId)
        }

        override suspend fun deleteUndefinedTask(taskId: Long) = eitherWrapper.wrap {
            undefinedTaskRepository.deleteUndefinedTask(taskId)
        }
    }
}
