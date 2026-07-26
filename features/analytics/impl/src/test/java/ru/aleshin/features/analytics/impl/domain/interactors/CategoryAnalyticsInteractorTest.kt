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
package ru.aleshin.features.analytics.impl.domain.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.MainCategoryDetails
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.repository.MainCategoryRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.firstRightOrNull
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsBucketCalculator
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsEitherWrapper
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsErrorHandler
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsIntervalSplitter
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsRangeCalculator
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsTaskClassifier
import ru.aleshin.features.analytics.impl.domain.common.CategoryAnalyticsCalculator
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
internal class CategoryAnalyticsInteractorTest {

    @Test
    fun categoryAnalyticsUsesOneUnionReadAndDoesNotUseTickerOrWrites() = runTest {
        val selection = AnalyticsRangeCalculator.Base().calculate(
            period = TimePeriod.WEEK,
            anchorDate = token(2026, Calendar.JULY, 21),
            customRange = null,
            locale = Locale.US,
            timeZone = UTC,
        )
        val category = MainCategory(id = CATEGORY_ID)
        val sourceDate = selection.civilRange.from
        val task = TimeTask(
            key = 1L,
            date = sourceDate,
            timeRange = TimeRange(
                from = Date(selection.ranges.current.from.time + HOUR),
                to = Date(selection.ranges.current.from.time + 2L * HOUR),
            ),
            category = category,
            isCompleted = true,
            isConsiderInStatistics = true,
        )
        val taskRepository = RecordingTimeTaskRepository(listOf(task))
        val categoryRepository = RecordingCategoryRepository(
            MainCategoryDetails(category, emptyList()),
        )
        val interactor = CategoryAnalyticsInteractor.Base(
            timeTaskRepository = taskRepository,
            mainCategoryRepository = categoryRepository,
            dateManager = FixedDateManager(selection.ranges.current.from),
            taskClassifier = AnalyticsTaskClassifier.Base(),
            categoryCalculator = CategoryAnalyticsCalculator.Base(
                bucketCalculator = AnalyticsBucketCalculator.Base(),
                intervalSplitter = AnalyticsIntervalSplitter.Base(),
            ),
            eitherWrapper = AnalyticsEitherWrapper.Base(AnalyticsErrorHandler.Base()),
        )

        val analytics = interactor.fetchCategoryAnalytics(
            mainCategoryId = CATEGORY_ID,
            selection = selection,
        ).firstRightOrNull()

        assertNotNull(analytics)
        assertEquals(category, analytics?.category)
        assertEquals(HOUR, analytics?.summary?.durationMillis)
        assertEquals(1, analytics?.keyMetrics?.taskCount)
        assertEquals(1, taskRepository.rangeReadCount)
        assertEquals(
            TimeRange(selection.ranges.comparison.from, selection.ranges.current.to),
            taskRepository.queriedRange,
        )
        assertEquals(1, categoryRepository.readCount)
        assertEquals(0, taskRepository.writeCount)
        assertEquals(0, categoryRepository.writeCount)
    }

    private fun token(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance(UTC).apply {
            clear()
            set(year, month, day, 0, 0, 0)
        }.time
    }

    private class RecordingTimeTaskRepository(
        private val tasks: List<TimeTask>,
    ) : TimeTaskRepository {

        var queriedRange: TimeRange? = null
        var rangeReadCount = 0
        var writeCount = 0

        override suspend fun addOrUpdateTimeTask(timeTask: TimeTask): Long {
            writeCount++
            return timeTask.key
        }

        override suspend fun addOrUpdateTimeTasks(timeTasks: List<TimeTask>) {
            writeCount++
        }

        override suspend fun fetchAllTimeTasksByDate(date: Date): Flow<List<TimeTask>> = flowOf(tasks)

        override suspend fun fetchTimeTasksByScheduleDateRange(timeRange: TimeRange): Flow<List<TimeTask>> {
            queriedRange = timeRange
            rangeReadCount++
            return flowOf(tasks)
        }

        override suspend fun fetchTimeTaskById(id: Long): TimeTask? = tasks.find { it.key == id }

        override suspend fun fetchTimeTaskByTemplate(templateId: Long, date: Date): TimeTask? = null

        override suspend fun deleteTimeTasksByIds(ids: List<Long>) {
            writeCount++
        }
    }

    private class RecordingCategoryRepository(
        private val category: MainCategoryDetails,
    ) : MainCategoryRepository {

        var readCount = 0
        var writeCount = 0

        override suspend fun addOrUpdateCategory(category: MainCategory): Long {
            writeCount++
            return category.id
        }

        override suspend fun addOrUpdateCategories(categories: List<MainCategory>) {
            writeCount++
        }

        override suspend fun fetchAllCategoriesDetails(): Flow<List<MainCategoryDetails>> {
            readCount++
            return flowOf(listOf(category))
        }

        override suspend fun fetchCategoryDetailsById(categoryId: Long): Flow<MainCategoryDetails?> {
            readCount++
            return flowOf(category)
        }

        override suspend fun deleteCategoryById(categoryId: Long) {
            writeCount++
        }

        override suspend fun deleteAllCategories() {
            writeCount++
        }
    }

    private class FixedDateManager(
        private val currentDate: Date,
    ) : DateManager {

        override fun fetchCurrentDate(): Date = currentDate
        override fun fetchBeginningCurrentDay(): Date = currentDate
        override fun fetchEndCurrentDay(): Date = currentDate
        override fun fetchTicker(): Flow<Date> = error("Ticker must not be used")
        override fun fetchMinuteTicker(): Flow<Date> = error("Minute ticker must not be used")
        override fun calculateLeftTime(endTime: Date): Long = endTime.time - currentDate.time
        override fun calculateProgress(startTime: Date, endTime: Date): Float = 0f
        override fun setCurrentHMS(date: Date): Date = date
    }

    private companion object Companion {
        val UTC: TimeZone = TimeZone.getTimeZone("UTC")
        const val CATEGORY_ID = 1L
        const val HOUR = 60L * 60L * 1_000L
    }
}
