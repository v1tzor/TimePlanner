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
package ru.aleshin.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.core.data.datasources.settings.TasksSettingsLocalDataSource
import ru.aleshin.core.data.models.settings.TasksSettingsEntity
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class TasksSettingsRepositoryImplTest {

    @Test
    fun updateAnalyticsRangeWritesOnlyAtomicAnalyticsFields() = runBlocking {
        val initial = TasksSettingsEntity(
            taskViewStatus = "EXPANDED",
            homeViewMode = HomeViewMode.TIMELINE.name,
            secureMode = true,
            durationPresets = "15,45",
        )
        val dataSource = FakeTasksSettingsLocalDataSource(initial)
        val repository = TasksSettingsRepositoryImpl(dataSource)
        val customRange = TimeRange(Date(20L), Date(30L))

        repository.updateAnalyticsRange(TimePeriod.CUSTOM, Date(10L), customRange)

        assertEquals(
            initial.copy(
                taskAnalyticsRange = TimePeriod.CUSTOM.name,
                taskAnalyticsAnchorDate = 10L,
                customAnalyticsDateFrom = 20L,
                customAnalyticsDateTo = 30L,
            ),
            dataSource.settings.value,
        )
    }
}

private class FakeTasksSettingsLocalDataSource(
    initialSettings: TasksSettingsEntity,
) : TasksSettingsLocalDataSource {

    val settings = MutableStateFlow(initialSettings)

    override fun fetchSettings(): Flow<TasksSettingsEntity> = settings

    override suspend fun updateSettings(settings: TasksSettingsEntity) {
        this.settings.value = settings
    }

    override suspend fun updateAnalyticsRange(
        period: String,
        anchorDate: Long?,
        customDateFrom: Long?,
        customDateTo: Long?,
    ) {
        settings.value = settings.value.copy(
            taskAnalyticsRange = period,
            taskAnalyticsAnchorDate = anchorDate,
            customAnalyticsDateFrom = customDateFrom,
            customAnalyticsDateTo = customDateTo,
        )
    }
}
