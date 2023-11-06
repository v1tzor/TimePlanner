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

import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.home.api.domain.entities.schedules.UndefinedTask
import ru.aleshin.features.home.api.domain.repository.UndefinedTasksRepository
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
internal interface UndefinedTasksInteractor {

    suspend fun addOrUpdateUndefinedTask(task: UndefinedTask): UnitDomainResult<HomeFailures>
    suspend fun fetchAllUndefinedTasks(): FlowDomainResult<HomeFailures, List<UndefinedTask>>
    suspend fun deleteUndefinedTask(task: UndefinedTask): UnitDomainResult<HomeFailures>

    class Base @Inject constructor(
        private val undefinedTasksRepository: UndefinedTasksRepository,
        private val eitherWrapper: HomeEitherWrapper,
    ) : UndefinedTasksInteractor {

        override suspend fun addOrUpdateUndefinedTask(task: UndefinedTask) = eitherWrapper.wrap {
            undefinedTasksRepository.addOrUpdateUndefinedTasks(listOf(task))
        }

        override suspend fun fetchAllUndefinedTasks() = eitherWrapper.wrapFlow {
            undefinedTasksRepository.fetchUndefinedTasks()
        }

        override suspend fun deleteUndefinedTask(task: UndefinedTask) = eitherWrapper.wrap {
            undefinedTasksRepository.removeUndefinedTask(task.id)
        }
    }
}
