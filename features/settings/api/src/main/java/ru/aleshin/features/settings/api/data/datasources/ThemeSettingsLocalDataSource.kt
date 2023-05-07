package ru.aleshin.features.settings.api.data.datasources

import kotlinx.coroutines.flow.Flow
import ru.aleshin.features.settings.api.data.models.ThemeSettingsEntity
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface ThemeSettingsLocalDataSource {

    fun fetchSettingsFlow(): Flow<ThemeSettingsEntity>
    suspend fun fetchSettings(): ThemeSettingsEntity
    suspend fun updateSettings(settings: ThemeSettingsEntity)

    class Base @Inject constructor(
        private val settingsDao: ThemeSettingsDao,
    ) : ThemeSettingsLocalDataSource {

        override fun fetchSettingsFlow(): Flow<ThemeSettingsEntity> {
            return settingsDao.fetchSettingsFlow()
        }

        override suspend fun fetchSettings(): ThemeSettingsEntity {
            return settingsDao.fetchSettings()
        }

        override suspend fun updateSettings(settings: ThemeSettingsEntity) {
            settingsDao.updateSettings(settings)
        }
    }
}
