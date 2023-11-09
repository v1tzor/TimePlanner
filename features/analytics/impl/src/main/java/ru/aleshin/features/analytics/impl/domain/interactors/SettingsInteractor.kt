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
package ru.aleshin.features.analytics.impl.domain.interactors

import kotlinx.coroutines.flow.first
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsEitherWrapper
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsFailure
import ru.aleshin.features.settings.api.domain.entities.TasksSettings
import ru.aleshin.features.settings.api.domain.repositories.TasksSettingsRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 09.11.2023.
 */
internal interface SettingsInteractor {

    suspend fun fetchTasksSettings(): DomainResult<AnalyticsFailure, TasksSettings>

    suspend fun updateTasksSettings(settings: TasksSettings): UnitDomainResult<AnalyticsFailure>

    class Base @Inject constructor(
        private val settingsRepository: TasksSettingsRepository,
        private val eitherWrapper: AnalyticsEitherWrapper,
    ) : SettingsInteractor {

        override suspend fun fetchTasksSettings() = eitherWrapper.wrap {
            settingsRepository.fetchSettings().first()
        }

        override suspend fun updateTasksSettings(settings: TasksSettings) = eitherWrapper.wrap {
            settingsRepository.updateSettings(settings)
        }
    }
}
