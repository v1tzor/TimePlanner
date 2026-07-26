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
package ru.aleshin.features.analytics.impl.presentation.theme.tokens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 22.07.2026.
 */
internal class AnalyticsStringsTest {

    @Test
    fun everySupportedLanguageUsesItsOwnCompleteTokenSet() {
        val translations = TimePlannerLanguage.entries.associateWith(::fetchAnalyticsStrings)

        assertEquals(TimePlannerLanguage.entries.size, translations.size)
        translations.filterKeys { it != TimePlannerLanguage.EN }.forEach { (_, strings) ->
            assertNotEquals(englishAnalyticsString.topAppBarTitle, strings.topAppBarTitle)
            assertNotEquals(englishAnalyticsString.otherError, strings.otherError)
            assertNotEquals(englishAnalyticsString.summaryTitle, strings.summaryTitle)
            assertNotEquals(englishAnalyticsString.noData, strings.noData)
            assertNotEquals(englishAnalyticsString.categoryUnavailable, strings.categoryUnavailable)
            strings.completedCountFormat.format(1, 2)
            strings.completionIncreaseFormat.format("10%")
            strings.completionDecreaseFormat.format("10%")
            strings.activeDaysFormat.format(1, 2, "days")
            strings.periodDaysFormat.format(2, "days")
            strings.activeDayIncreaseFormat.format(2, "days")
            strings.activeDayDecreaseFormat.format(2, "days")
            strings.heatmapRowDescFormat.format("Day", 0, 3, 10)
            strings.heatmapCellFormat.format("Day", 1, 2, 3)
            strings.categoryLoadTooltipFormat.format("Day", "Category", "1 h", "2 h")
        }
    }
}
