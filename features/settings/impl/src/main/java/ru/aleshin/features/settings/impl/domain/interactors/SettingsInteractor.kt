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
package ru.aleshin.features.settings.impl.domain.interactors

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.aleshin.core.domain.entities.settings.Settings
import ru.aleshin.core.domain.entities.settings.TasksSettings
import ru.aleshin.core.domain.entities.settings.ThemeSettings
import ru.aleshin.core.domain.repository.TasksSettingsRepository
import ru.aleshin.core.domain.repository.ThemeSettingsRepository
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.settings.impl.domain.common.SettingsEitherWrapper
import ru.aleshin.features.settings.impl.domain.common.SettingsFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
internal interface SettingsInteractor {

    suspend fun updateThemeSettings(settings: ThemeSettings): UnitDomainResult<SettingsFailures>
    suspend fun updateTasksSettings(settings: TasksSettings): UnitDomainResult<SettingsFailures>
    suspend fun fetchAllSettings(): FlowDomainResult<SettingsFailures, Settings>
    suspend fun resetAllSettings(): DomainResult<SettingsFailures, Settings>

    class Base @Inject constructor(
        private val themeSettingsRepository: ThemeSettingsRepository,
        private val tasksSettingsRepository: TasksSettingsRepository,
        private val eitherWrapper: SettingsEitherWrapper,
    ) : SettingsInteractor {

        override suspend fun updateThemeSettings(settings: ThemeSettings) = eitherWrapper.wrap {
            themeSettingsRepository.updateSettings(settings)
        } 
        
        override suspend fun updateTasksSettings(settings: TasksSettings) = eitherWrapper.wrap {
            tasksSettingsRepository.updateSettings(settings)
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        override suspend fun fetchAllSettings() = eitherWrapper.wrapFlow {
            themeSettingsRepository.fetchSettingsFlow().flatMapLatest { themeSettings ->
                tasksSettingsRepository.fetchSettings().map { tasksSettings ->
                    return@map Settings(
                        themeSettings = themeSettings,
                        tasksSettings = tasksSettings,
                    )
                }
            }
        }

        override suspend fun resetAllSettings() = eitherWrapper.wrap {
            val themeSettings = ThemeSettings().apply { themeSettingsRepository.updateSettings(this) }
            val tasksSettings = TasksSettings().apply { tasksSettingsRepository.updateSettings(this) }

            return@wrap Settings(themeSettings, tasksSettings)
        }
    }
}
