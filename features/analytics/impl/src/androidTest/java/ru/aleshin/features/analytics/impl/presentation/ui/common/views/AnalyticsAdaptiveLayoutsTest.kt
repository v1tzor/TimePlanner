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
package ru.aleshin.features.analytics.impl.presentation.ui.common.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.domain.entities.CategoryDayPart
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsWeekdayHourCellUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsWeekdayHourLoadUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsWeekdayHourRowUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryDayPartCellUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryDayPartSummaryUi
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.LocalAnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.englishAnalyticsString
import ru.aleshin.timeplanner.core.ui.theme.tokens.LocalTimePlannerLanguage
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 22.07.2026.
 */
internal class AnalyticsAdaptiveLayoutsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun heatmapsExposeOnlyLogicalRowDescriptionsAtCompactWidth() {
        val weekdays = Calendar.SUNDAY..Calendar.SATURDAY
        val hourRows = weekdays.map { day ->
            AnalyticsWeekdayHourRowUi(
                dayOfWeek = day,
                cells = (0 until 24 step 3).map { fromHour ->
                    AnalyticsWeekdayHourCellUi(
                        dayOfWeek = day,
                        fromHour = fromHour,
                        toHour = fromHour + 3,
                        averageMinutes = 0.0,
                        level = 0,
                    )
                },
                busiestCellIndex = 0,
            )
        }
        val weekdayHourLoad = AnalyticsWeekdayHourLoadUi(rows = hourRows)
        val dayPartCells = CategoryDayPart.entries.flatMap { part ->
            weekdays.map { day -> CategoryDayPartCellUi(day, part, 0.0, 0) }
        }
        val dayPartRows = CategoryDayPart.entries.map { part ->
            CategoryDayPartSummaryUi(part, Calendar.SUNDAY, 0.0)
        }

        composeRule.setContent {
            CompositionLocalProvider(
                LocalTimePlannerLanguage provides TimePlannerLanguage.EN,
                LocalAnalyticsStrings provides englishAnalyticsString,
            ) {
                MaterialTheme {
                    Column(Modifier.width(320.dp)) {
                        AnalyticsWeekdayHourHeatmap(weekdayHourLoad = weekdayHourLoad)
                        CategoryDayPartHeatmap(
                            cells = dayPartCells,
                            summaries = dayPartRows,
                            categoryColor = Color.Blue,
                            locale = Locale.US,
                            strings = englishAnalyticsString,
                        )
                    }
                }
            }
        }

        weekdays.forEach { day ->
            val dayName = DateFormatSymbols(Locale.US).weekdays[day]
            val description = englishAnalyticsString.heatmapRowDescFormat.format(dayName, 0, 3, 0)
            composeRule.onNodeWithContentDescription(description).assertExists()
        }
        CategoryDayPart.entries.forEach { part ->
            val title = when (part) {
                CategoryDayPart.MORNING -> englishAnalyticsString.dayPartMorning
                CategoryDayPart.DAY -> englishAnalyticsString.dayPartDay
                CategoryDayPart.EVENING -> englishAnalyticsString.dayPartEvening
                CategoryDayPart.NIGHT -> englishAnalyticsString.dayPartNight
            }
            val description = englishAnalyticsString.dayPartRowDescFormat.format(
                title,
                DateFormatSymbols(Locale.US).weekdays[Calendar.SUNDAY],
                0,
            )
            composeRule.onNodeWithContentDescription(description).assertExists()
        }
        composeRule.onNodeWithText("12–15").assertIsDisplayed()
        composeRule.onNodeWithText("21–24").assertIsDisplayed()
    }

    @Test
    fun regularityCalendarFitsCompactWidthAndExposesOneGridDescription() {
        val from = token(2026, Calendar.JULY, 1)
        val to = token(2026, Calendar.JULY, 31)
        composeRule.setContent {
            MaterialTheme {
                Box(Modifier.width(320.dp)) {
                    AnalyticsRegularityCalendar(
                        range = AnalyticsRangeUi(
                            period = TimePeriod.MONTH,
                            anchorDate = from,
                            from = from,
                            to = to,
                            comparisonFrom = from,
                            comparisonTo = to,
                        ),
                        activeDates = listOf(from),
                        locale = Locale.US,
                        strings = englishAnalyticsString,
                        today = from,
                    )
                }
            }
        }

        composeRule.onNodeWithContentDescription("July 2026, 1 of 31 days").assertIsDisplayed()
    }

    @Test
    fun donutCenterValueIsConstrainedInsideTheHole() {
        composeRule.setContent {
            MaterialTheme {
                Box(Modifier.size(136.dp), contentAlignment = Alignment.Center) {
                    AnalyticsDonutCenterLabel(
                        value = "23 h 59 min",
                        label = "included",
                    )
                }
            }
        }

        composeRule.onNodeWithText("23 h 59 min")
            .assertIsDisplayed()
            .assertWidthIsEqualTo(72.dp)
    }

    private fun token(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance(UTC).apply {
            clear()
            set(year, month, day, 0, 0, 0)
        }.time
    }

    private companion object Companion {
        val UTC: TimeZone = TimeZone.getTimeZone("UTC")
    }
}
