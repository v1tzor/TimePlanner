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
package ru.aleshin.features.home.impl.domain

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.Categories
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.TaskPriority
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.extensions.toMinutes
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.common.HomeErrorHandler
import ru.aleshin.features.home.impl.domain.interactors.ShareTextInteractor
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
internal class ShareTextInteractorTest {

    private lateinit var shareTextInteractor: ShareTextInteractor
    private lateinit var dateManager: ShareTextFakeDateManager

    @Before
    fun setUp() {
        dateManager = ShareTextFakeDateManager(
            Calendar.getInstance().apply {
                set(2026, Calendar.JULY, 1, 12, 30, 0)
            }.time,
        )
        shareTextInteractor = ShareTextInteractor.Base(
            dateManager = dateManager,
            eitherWrapper = HomeEitherWrapper.Base(HomeErrorHandler.Base()),
        )
    }

    @Test
    fun test_fetch_shared_text_tasks_parses_list_prefixes() = runBlocking {
        val categories = listOf(
            Categories(MainCategory(id = 0, default = DefaultCategoryType.EMPTY)),
            Categories(MainCategory(id = 15, default = DefaultCategoryType.OTHER)),
        )
        val sharedText = """
            - [ ] Buy milk
            1. Call mom
            * Read book
            • Walk
            [x] Pay bills
        """.trimIndent()

        val actual = shareTextInteractor.fetchSharedTextTasks(sharedText, categories)
        val tasks = (actual as Either.Right).data

        assertEquals(listOf("Buy milk", "Call mom", "Read book", "Walk", "Pay bills"), tasks.map { it.note })
        assertTrue(tasks.all { it.mainCategory.default == DefaultCategoryType.OTHER })
        assertTrue(tasks.all { it.subCategory == null && it.deadline == null })
        assertTrue(tasks.all { it.priority == TaskPriority.STANDARD })
        assertTrue(tasks.all { it.createdAt == dateManager.fetchCurrentDate() })
        assertEquals(tasks.size, tasks.map { it.id }.toSet().size)
    }

    @Test
    fun test_fetch_shared_text_tasks_skips_empty_lines_and_truncates_note() = runBlocking {
        val categories = listOf(Categories(MainCategory(id = 15, default = DefaultCategoryType.OTHER)))
        val note = "a".repeat(Constants.Text.MAX_NOTE_LENGTH + 20)
        val sharedText = "$note\n\n-   \n*"

        val actual = shareTextInteractor.fetchSharedTextTasks(sharedText, categories)
        val tasks = (actual as Either.Right).data

        assertEquals(1, tasks.size)
        assertEquals(Constants.Text.MAX_NOTE_LENGTH, tasks.first().note?.length)
    }

    @Test
    fun test_fetch_shared_text_tasks_fallbacks_to_empty_category() = runBlocking {
        val categories = listOf(
            Categories(MainCategory(id = 0, default = DefaultCategoryType.EMPTY)),
            Categories(MainCategory(id = 1, default = DefaultCategoryType.WORK)),
        )

        val actual = shareTextInteractor.fetchSharedTextTasks("Inbox task", categories)
        val task = (actual as Either.Right).data.first()

        assertEquals(DefaultCategoryType.EMPTY, task.mainCategory.default)
    }
}

private class ShareTextFakeDateManager(
    private val currentDate: Date,
) : DateManager {

    override fun fetchCurrentDate() = currentDate

    override fun fetchBeginningCurrentDay() = currentDate.startThisDay()

    override fun fetchEndCurrentDay() = currentDate.endThisDay()

    override fun calculateLeftTime(endTime: Date) = endTime.time - currentDate.time

    override fun calculateProgress(startTime: Date, endTime: Date): Float {
        val pastTime = (currentDate.time - startTime.time).toMinutes().toFloat()
        val duration = (endTime.time - startTime.time).toMinutes().toFloat()
        val progress = pastTime / duration
        return if (progress < 0f) 0f else if (progress > 1f) 1f else progress
    }

    override fun setCurrentHMS(date: Date): Date {
        return date
    }
}
