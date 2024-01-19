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
package ru.aleshin.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.aleshin.core.data.datasources.settings.ThemeSettingsLocalDataSource
import ru.aleshin.core.data.mappers.settings.mapToData
import ru.aleshin.core.data.mappers.settings.mapToDomain
import ru.aleshin.core.domain.entities.settings.ThemeSettings
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
class ThemeSettingsRepositoryImpl @Inject constructor(
    private val localDataSource: ThemeSettingsLocalDataSource,
) : ru.aleshin.core.domain.repository.ThemeSettingsRepository {

    override fun fetchSettingsFlow(): Flow<ThemeSettings> {
        return localDataSource.fetchSettingsFlow().map { it.mapToDomain() }
    }

    override suspend fun fetchSettings(): ThemeSettings {
        return localDataSource.fetchSettings().mapToDomain()
    }

    override suspend fun updateSettings(settings: ThemeSettings) {
        localDataSource.updateSettings(settings.mapToData())
    }
}
