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
package ru.aleshin.timeplanner.presentation.notifications

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.core.domain.entities.tasks.TaskNotificationType
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerStrings
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreStrings
import java.text.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 14.07.2026.
 */
class NotificationContentProviderTest {

    private lateinit var provider: NotificationContentProvider
    private lateinit var strings: TimePlannerStrings
    private lateinit var startTime: Date
    private lateinit var endTime: Date
    private lateinit var previousLocale: Locale
    private lateinit var previousTimeZone: TimeZone

    @Before
    fun setUp() {
        previousLocale = Locale.getDefault()
        previousTimeZone = TimeZone.getDefault()
        Locale.setDefault(Locale.forLanguageTag("ru-RU"))
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"))

        provider = NotificationContentProvider.Base()
        strings = fetchCoreStrings(TimePlannerLanguage.RU)
        startTime = dateAt(hour = 14, minute = 0)
        endTime = dateAt(hour = 15, minute = 30)
    }

    @After
    fun tearDown() {
        Locale.setDefault(previousLocale)
        TimeZone.setDefault(previousTimeZone)
    }

    @Test
    fun beforeAlertUsesSubcategoryAsNameAndCategoryAsContext() {
        val content = provider.fetchAlertContent(
            timeTask = timeTask(
                subCategoryName = "Подготовить отчёт",
                note = "Проверить цифры перед отправкой",
            ),
            notificationType = TaskNotificationType.FIFTEEN_MINUTES_BEFORE,
            strings = strings,
        )
        val expectedText = "${fetchTimeRange()} · Работа"

        assertEquals("Подготовить отчёт — через 15 минут", content.title)
        assertEquals(expectedText, content.text)
        assertEquals("$expectedText\nПроверить цифры перед отправкой", content.expandedText)
    }

    @Test
    fun startAlertKeepsCollapsedTextUsefulWithoutNote() {
        val content = provider.fetchAlertContent(
            timeTask = timeTask(),
            notificationType = TaskNotificationType.START,
            strings = strings,
        )

        assertEquals("Работа — Начало события", content.title)
        assertEquals(fetchTimeRange(), content.text)
        assertNull(content.expandedText)
    }

    @Test
    fun ongoingContentUsesOngoingStateAndExpandedNote() {
        val content = provider.fetchOngoingContent(
            timeTask = timeTask(
                subCategoryName = "Подготовить отчёт",
                note = "  Проверить цифры перед отправкой  ",
            ),
            strings = strings,
        )
        val expectedText = "${fetchTimeRange()} · Работа"

        assertEquals("Подготовить отчёт — Сейчас выполняется", content.title)
        assertEquals(expectedText, content.text)
        assertEquals("$expectedText\nПроверить цифры перед отправкой", content.expandedText)
    }

    @Test
    fun blankNamesFallBackToLocalizedAppName() {
        val content = provider.fetchAlertContent(
            timeTask = timeTask(categoryName = " ", subCategoryName = " "),
            notificationType = TaskNotificationType.ONE_HOUR_BEFORE,
            strings = strings,
        )

        assertEquals("${strings.appName} — через 1 час", content.title)
        assertEquals(fetchTimeRange(), content.text)
    }

    @Test
    fun serviceEndTypeCannotBeBuiltAsVisibleAlert() {
        assertThrows(IllegalArgumentException::class.java) {
            provider.fetchAlertContent(
                timeTask = timeTask(),
                notificationType = TaskNotificationType.END_ONGOING,
                strings = strings,
            )
        }
    }

    private fun timeTask(
        categoryName: String = "Работа",
        subCategoryName: String? = null,
        note: String? = null,
    ) = TimeTask(
        key = 1L,
        date = startTime,
        timeRange = TimeRange(startTime, endTime),
        category = MainCategory(
            id = 1L,
            customName = categoryName,
            default = null,
        ),
        subCategory = subCategoryName?.let { name ->
            SubCategory(
                id = 2L,
                mainCategoryId = 1L,
                name = name,
            )
        },
        note = note,
    )

    private fun fetchTimeRange(): String {
        val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
        return strings.notificationTimeRangeFormat.format(
            timeFormat.format(startTime),
            timeFormat.format(endTime),
        )
    }

    private fun dateAt(hour: Int, minute: Int): Date {
        return Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, 14, hour, minute)
        }.time
    }
}
