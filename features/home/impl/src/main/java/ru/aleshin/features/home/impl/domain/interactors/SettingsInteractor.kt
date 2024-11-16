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

import kotlinx.coroutines.flow.Flow
import ru.aleshin.core.domain.entities.settings.TasksSettings
import ru.aleshin.core.domain.repository.TasksSettingsRepository
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 15.09.2023.
 */
internal interface SettingsInteractor {

    suspend fun fetchTasksSettings(): Flow<DomainResult<HomeFailures, TasksSettings>>
    suspend fun updateTasksSettings(settings: TasksSettings): UnitDomainResult<HomeFailures>

    class Base @Inject constructor(
        private val tasksSettingsRepository: TasksSettingsRepository,
        private val eitherWrapper: HomeEitherWrapper,
    ) : SettingsInteractor {

        override suspend fun fetchTasksSettings() = eitherWrapper.wrapFlow {
            tasksSettingsRepository.fetchSettings()
        }

        override suspend fun updateTasksSettings(settings: TasksSettings) = eitherWrapper.wrap {
            tasksSettingsRepository.updateSettings(settings)
        }
    }
}
