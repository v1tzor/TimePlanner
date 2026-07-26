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
package ru.aleshin.core.data.mappers.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import ru.aleshin.core.data.models.settings.TasksSettingsEntity
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.core.domain.entities.settings.TasksSettings
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class TasksSettingsDataMapperTest {

    @Test
    fun allAnalyticsPeriodsRoundTripWithOtherFields() {
        TimePeriod.entries.forEach { period ->
            val settings = TasksSettings(
                homeViewMode = HomeViewMode.TIMELINE,
                taskAnalyticsRange = period,
                taskAnalyticsAnchorDate = Date(10L),
                customAnalyticsDateRange = TimeRange(Date(20L), Date(30L)),
                durationPresets = listOf(600_000L, 1_800_000L),
            )

            assertEquals(settings, settings.mapToData().mapToDomain())
        }
    }

    @Test
    fun nullAnalyticsDatesRoundTrip() {
        val settings = TasksSettings(
            taskAnalyticsRange = TimePeriod.CUSTOM,
            taskAnalyticsAnchorDate = null,
            customAnalyticsDateRange = null,
        )

        val mapped = settings.mapToData().mapToDomain()

        assertNull(mapped.taskAnalyticsAnchorDate)
        assertNull(mapped.customAnalyticsDateRange)
    }

    @Test
    fun incompleteCustomPairMapsToNull() {
        val mapped = TasksSettingsEntity(
            customAnalyticsDateFrom = 20L,
            customAnalyticsDateTo = null,
        ).mapToDomain()

        assertNull(mapped.customAnalyticsDateRange)
    }

    @Test
    fun reversedCustomPairRemainsStructuralForInteractorValidation() {
        val mapped = TasksSettingsEntity(
            customAnalyticsDateFrom = 30L,
            customAnalyticsDateTo = 20L,
        ).mapToDomain()

        assertEquals(TimeRange(Date(30L), Date(20L)), mapped.customAnalyticsDateRange)
    }

    @Test
    fun unknownPeriodFallsBackSafely() {
        val mapped = TasksSettingsEntity(taskAnalyticsRange = "UNKNOWN").mapToDomain()

        assertEquals(TimePeriod.WEEK, mapped.taskAnalyticsRange)
    }
}
