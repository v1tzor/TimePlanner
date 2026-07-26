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
package ru.aleshin.features.settings.impl.presentation.mappers

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.core.domain.entities.settings.TasksSettings
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class TaskSettingsUiMapperTest {

    @Test
    fun settingsUiRoundTripPreservesHomeModeAndAnalyticsRangeState() {
        val settings = TasksSettings(
            homeViewMode = HomeViewMode.TIMELINE,
            taskAnalyticsRange = TimePeriod.CUSTOM,
            taskAnalyticsAnchorDate = Date(10L),
            customAnalyticsDateRange = TimeRange(Date(20L), Date(30L)),
        )

        assertEquals(settings, settings.mapToUi().mapToDomain())
    }
}
