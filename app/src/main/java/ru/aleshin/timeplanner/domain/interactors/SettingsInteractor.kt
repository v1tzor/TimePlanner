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
package ru.aleshin.timeplanner.domain.interactors

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.aleshin.core.domain.entities.settings.Settings
import ru.aleshin.core.domain.repository.TasksSettingsRepository
import ru.aleshin.core.domain.repository.ThemeSettingsRepository
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.timeplanner.domain.common.MainEitherWrapper
import ru.aleshin.timeplanner.domain.common.MainFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface SettingsInteractor {

    suspend fun fetchSettings(): Flow<Either<MainFailures, Settings>>

    class Base @Inject constructor(
        private val themeRepository: ThemeSettingsRepository,
        private val tasksRepository: TasksSettingsRepository,
        private val eitherWrapper: MainEitherWrapper,
    ) : SettingsInteractor {

        @OptIn(ExperimentalCoroutinesApi::class)
        override suspend fun fetchSettings() = eitherWrapper.wrapFlow {
            themeRepository.fetchSettingsFlow().flatMapLatest { themeSettings ->
                tasksRepository.fetchSettings().map { tasksSettings ->
                    Settings(themeSettings, tasksSettings)
                }
            }
        }
    }
}
