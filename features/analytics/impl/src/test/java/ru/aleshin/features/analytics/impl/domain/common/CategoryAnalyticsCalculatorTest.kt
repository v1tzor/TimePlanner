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
package ru.aleshin.features.analytics.impl.domain.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.MainCategoryDetails
import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTaskStatus
import ru.aleshin.features.analytics.impl.domain.entities.CategoryDayPart
import ru.aleshin.features.analytics.impl.domain.entities.CategoryObservationType
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class CategoryAnalyticsCalculatorTest {

    private val classifier = AnalyticsTaskClassifier.Base()
    private val calculator = CategoryAnalyticsCalculator.Base(
        bucketCalculator = AnalyticsBucketCalculator.Base(),
        intervalSplitter = AnalyticsIntervalSplitter.Base(),
    )

    @Test
    fun categoryAggregateUsesAllPlanShareAndCategoryOnlyDetails() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val now = local(2026, Calendar.JULY, 21, 12, 0)
        val range = TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59))
        val current = classifier.prepare(
            listOf(
                task(2L, CATEGORY_ID, source, local(2026, Calendar.JULY, 21, 18, 0), local(2026, Calendar.JULY, 21, 19, 0), SUBCATEGORY),
                task(1L, CATEGORY_ID, source, local(2026, Calendar.JULY, 21, 8, 0), local(2026, Calendar.JULY, 21, 9, 0), null),
                task(3L, 2L, source, local(2026, Calendar.JULY, 21, 10, 0), local(2026, Calendar.JULY, 21, 12, 0), null),
            ),
            range,
            now,
            UTC,
        )
        val previous = classifier.prepare(
            listOf(
                task(4L, CATEGORY_ID, source, local(2026, Calendar.JULY, 21, 7, 0), local(2026, Calendar.JULY, 21, 8, 0), null),
                task(5L, CATEGORY_ID, source, local(2026, Calendar.JULY, 21, 9, 0), local(2026, Calendar.JULY, 21, 10, 0), SUBCATEGORY),
            ),
            range,
            local(2026, Calendar.JULY, 21, 20, 0),
            UTC,
        )

        val analytics = calculator.calculate(
            category = CATEGORY,
            currentPlanTasks = current,
            comparisonPlanTasks = previous,
            currentCivilRange = AnalyticsCivilDateRange(token(2026, Calendar.JULY, 21), token(2026, Calendar.JULY, 21)),
            locale = Locale.US,
            timeZone = UTC,
        )

        assertEquals(2L * HOUR, analytics.summary?.durationMillis)
        assertEquals(0.5, analytics.summary?.share ?: -1.0, 0.0001)
        assertEquals(2, analytics.keyMetrics?.taskCount)
        assertEquals(1, analytics.keyMetrics?.completion?.completedTaskCount)
        assertEquals(2, analytics.keyMetrics?.completion?.allTaskCount)
        assertEquals(listOf(1L, 2L), analytics.taskRows.map { it.task.key })
        assertEquals(
            listOf(AnalyticsTaskStatus.COMPLETED, AnalyticsTaskStatus.UNFINISHED),
            analytics.taskRows.map { it.status },
        )
        assertEquals(2, analytics.subCategories?.buckets?.size)
        assertEquals(2L * HOUR, analytics.load?.buckets?.single()?.categoryDurationMillis)
        assertEquals(4L * HOUR, analytics.load?.buckets?.single()?.allPlanDurationMillis)
        assertEquals(60.0, analytics.dayParts.single { it.dayPart == CategoryDayPart.MORNING && it.dayOfWeek == Calendar.TUESDAY }.totalMinutes, 0.0)
        assertEquals(CategoryObservationType.COMPLETION_DROP, analytics.observation?.type)
    }

    @Test
    fun missingAndEmptyCategoryAreDistinct() {
        val missing = calculator.calculate(
            category = null,
            currentPlanTasks = emptyList(),
            comparisonPlanTasks = emptyList(),
            currentCivilRange = AnalyticsCivilDateRange(token(2026, Calendar.JULY, 21), token(2026, Calendar.JULY, 21)),
            locale = Locale.US,
            timeZone = UTC,
        )
        val empty = calculator.calculate(
            category = CATEGORY,
            currentPlanTasks = emptyList(),
            comparisonPlanTasks = emptyList(),
            currentCivilRange = AnalyticsCivilDateRange(token(2026, Calendar.JULY, 21), token(2026, Calendar.JULY, 21)),
            locale = Locale.US,
            timeZone = UTC,
        )

        assertNull(missing.category)
        assertEquals(CATEGORY.category, empty.category)
        assertNull(empty.summary)
    }

    @Test
    fun unassignedBucketCanProduceDominantObservationAndDayPartTieIsDeterministic() {
        val firstDay = local(2026, Calendar.JULY, 20, 0, 0)
        val lastDay = local(2026, Calendar.JULY, 24, 23, 59)
        val tasks = (0 until 5).map { index ->
            val source = local(2026, Calendar.JULY, 20 + index, 0, 0)
            task(
                key = index.toLong() + 1L,
                categoryId = CATEGORY_ID,
                source = source,
                from = local(2026, Calendar.JULY, 20 + index, 8, 0),
                to = local(2026, Calendar.JULY, 20 + index, 9, 0),
                subCategory = if (index < 3) null else SUBCATEGORY,
            )
        }
        val prepared = classifier.prepare(tasks, TimeRange(firstDay, lastDay), lastDay, UTC)

        val analytics = calculator.calculate(
            category = CATEGORY,
            currentPlanTasks = prepared,
            comparisonPlanTasks = emptyList(),
            currentCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 20),
                token(2026, Calendar.JULY, 24),
            ),
            locale = Locale.US,
            timeZone = UTC,
        )

        assertEquals(CategoryObservationType.DOMINANT_SUBCATEGORY, analytics.observation?.type)
        assertNull(analytics.observation?.subCategory)
        assertEquals(0.6, analytics.observation?.valuePercent ?: -1.0, 0.0001)
        assertEquals(
            Calendar.MONDAY,
            analytics.dayPartSummaries.single { it.dayPart == CategoryDayPart.MORNING }.busiestDayOfWeek,
        )
    }

    @Test
    fun subCategoriesUseStableIdOrderAndCollapseAfterFourRows() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val subCategories = (1L..6L).map { id ->
            SubCategory(id = id, mainCategoryId = CATEGORY_ID, name = "Sub $id")
        }
        val tasks = subCategories.mapIndexed { index, subCategory ->
            task(
                key = index.toLong() + 1L,
                categoryId = CATEGORY_ID,
                source = source,
                from = local(2026, Calendar.JULY, 21, index, 0),
                to = local(2026, Calendar.JULY, 21, index + 1, 0),
                subCategory = subCategory,
            )
        }
        val prepared = classifier.prepare(
            tasks,
            TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59)),
            local(2026, Calendar.JULY, 22, 0, 0),
            UTC,
        )

        val analytics = calculator.calculate(
            category = MainCategoryDetails(MainCategory(id = CATEGORY_ID), subCategories),
            currentPlanTasks = prepared,
            comparisonPlanTasks = emptyList(),
            currentCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 21),
                token(2026, Calendar.JULY, 21),
            ),
            locale = Locale.US,
            timeZone = UTC,
        )
        val buckets = checkNotNull(analytics.subCategories).buckets

        assertEquals(listOf(1L, 2L, 3L, 4L), buckets.take(4).map { it.subCategory?.id })
        assertEquals(true, buckets.last().isOther)
        assertEquals(2, buckets.last().taskCount)
        assertEquals(2L * HOUR, buckets.last().durationMillis)
    }

    @Test
    fun categoryMetricsConserveTotalsAndComparisonDeltasAreExact() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val range = TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59))
        val now = local(2026, Calendar.JULY, 21, 12, 0)
        val current = classifier.prepare(
            tasks = listOf(
                task(
                    key = 1L,
                    categoryId = CATEGORY_ID,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 8, 0),
                    to = local(2026, Calendar.JULY, 21, 8, 30),
                    subCategory = SUBCATEGORY,
                ),
                task(
                    key = 2L,
                    categoryId = CATEGORY_ID,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 9, 0),
                    to = local(2026, Calendar.JULY, 21, 10, 30),
                    subCategory = null,
                ).copy(isCompleted = false),
                task(
                    key = 3L,
                    categoryId = CATEGORY_ID,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 18, 0),
                    to = local(2026, Calendar.JULY, 21, 21, 0),
                    subCategory = SUBCATEGORY,
                ),
                task(
                    key = 4L,
                    categoryId = 2L,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 12, 0),
                    to = local(2026, Calendar.JULY, 21, 14, 0),
                    subCategory = null,
                ),
            ),
            sourceRange = range,
            now = now,
            timeZone = UTC,
        )
        val comparison = classifier.prepare(
            tasks = listOf(
                task(
                    key = 5L,
                    categoryId = CATEGORY_ID,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 7, 0),
                    to = local(2026, Calendar.JULY, 21, 8, 0),
                    subCategory = SUBCATEGORY,
                ),
                task(
                    key = 6L,
                    categoryId = CATEGORY_ID,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 8, 0),
                    to = local(2026, Calendar.JULY, 21, 10, 0),
                    subCategory = null,
                ).copy(isCompleted = false),
            ),
            sourceRange = range,
            now = now,
            timeZone = UTC,
        )

        val analytics = calculator.calculate(
            category = CATEGORY,
            currentPlanTasks = current,
            comparisonPlanTasks = comparison,
            currentCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 21),
                token(2026, Calendar.JULY, 21),
            ),
            locale = Locale.US,
            timeZone = UTC,
        )
        val summary = checkNotNull(analytics.summary)
        val metrics = checkNotNull(analytics.keyMetrics)
        val subCategories = checkNotNull(analytics.subCategories)
        val load = checkNotNull(analytics.load)

        assertEquals(5L * HOUR, summary.durationMillis)
        assertEquals(7L * HOUR, summary.allPlanDurationMillis)
        assertEquals(5.0 / 7.0, summary.share, 0.0001)
        assertEquals(2.0 / 3.0, summary.comparison.changePercent ?: -1.0, 0.0001)
        assertEquals(3, metrics.taskCount)
        assertEquals(100L * MINUTE, metrics.averageDurationMillis)
        assertEquals(1, metrics.taskCountDelta)
        assertEquals(10L * MINUTE, metrics.averageDurationDeltaMillis)
        assertEquals(0, metrics.completedCountDelta)
        assertEquals(1, metrics.completion.completedTaskCount)
        assertEquals(3, metrics.completion.allTaskCount)
        assertEquals(1.0 / 3.0, metrics.completion.share ?: -1.0, 0.0001)
        assertEquals(-1.0 / 3.0, metrics.completion.comparison.changePercent ?: 0.0, 0.0001)
        assertEquals(5L * HOUR, subCategories.buckets.sumOf { it.durationMillis })
        assertEquals(3, subCategories.buckets.sumOf { it.taskCount })
        assertEquals(1.0, subCategories.buckets.sumOf { it.share }, 0.0001)
        assertEquals(5L * HOUR, load.buckets.sumOf { it.categoryDurationMillis })
        assertEquals(7L * HOUR, load.buckets.sumOf { it.allPlanDurationMillis })
        assertEquals(5L * HOUR, analytics.taskRows.sumOf { it.safeDurationMillis })
        assertEquals(300.0, analytics.dayParts.sumOf { it.totalMinutes }, 0.0001)
        assertEquals(
            setOf(CategoryDayPart.MORNING, CategoryDayPart.EVENING),
            analytics.dayPartSummaries.map { it.dayPart }.toSet(),
        )
        assertEquals(CategoryObservationType.COMPLETION_DROP, analytics.observation?.type)
        assertEquals(1.0 / 3.0, analytics.observation?.valuePercent ?: -1.0, 0.0001)
        assertTrue(analytics.dayParts.all { it.level in 0..4 })
    }

    @Test
    fun zeroDayPartsDoNotInventBusiestWeekdays() {
        val source = local(2026, Calendar.JULY, 20, 0, 0)
        val prepared = classifier.prepare(
            tasks = listOf(
                task(
                    key = 1L,
                    categoryId = CATEGORY_ID,
                    source = local(2026, Calendar.JULY, 21, 0, 0),
                    from = local(2026, Calendar.JULY, 21, 8, 0),
                    to = local(2026, Calendar.JULY, 21, 9, 0),
                    subCategory = null,
                ),
                task(
                    key = 2L,
                    categoryId = CATEGORY_ID,
                    source = local(2026, Calendar.JULY, 28, 0, 0),
                    from = local(2026, Calendar.JULY, 28, 8, 0),
                    to = local(2026, Calendar.JULY, 28, 10, 0),
                    subCategory = null,
                ),
            ),
            sourceRange = TimeRange(source, local(2026, Calendar.AUGUST, 2, 23, 59)),
            now = local(2026, Calendar.AUGUST, 3, 0, 0),
            timeZone = UTC,
        )

        val analytics = calculator.calculate(
            category = CATEGORY,
            currentPlanTasks = prepared,
            comparisonPlanTasks = emptyList(),
            currentCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 20),
                token(2026, Calendar.AUGUST, 2),
            ),
            locale = Locale.US,
            timeZone = UTC,
        )

        assertEquals(listOf(CategoryDayPart.MORNING), analytics.dayPartSummaries.map { it.dayPart })
        assertEquals(Calendar.TUESDAY, analytics.dayPartSummaries.single().busiestDayOfWeek)
        assertEquals(15.0, analytics.dayPartSummaries.single().busiestAverageMinutes, 0.0)
        assertEquals(
            15.0,
            analytics.dayParts.single {
                it.dayPart == CategoryDayPart.MORNING && it.dayOfWeek == Calendar.TUESDAY
            }.averageMinutes,
            0.0,
        )
    }

    @Test
    fun busiestDayObservationUsesActualIntervalsAndIncludesThreshold() {
        val monday = local(2026, Calendar.JULY, 20, 0, 0)
        val tasks = listOf(
            task(
                key = 1L,
                categoryId = CATEGORY_ID,
                source = monday,
                from = local(2026, Calendar.JULY, 20, 8, 0),
                to = local(2026, Calendar.JULY, 20, 10, 0),
                subCategory = SUBCATEGORY,
            ),
            task(
                key = 2L,
                categoryId = CATEGORY_ID,
                source = local(2026, Calendar.JULY, 21, 0, 0),
                from = local(2026, Calendar.JULY, 21, 8, 0),
                to = local(2026, Calendar.JULY, 21, 9, 30),
                subCategory = null,
            ),
            task(
                key = 3L,
                categoryId = CATEGORY_ID,
                source = local(2026, Calendar.JULY, 22, 0, 0),
                from = local(2026, Calendar.JULY, 22, 8, 0),
                to = local(2026, Calendar.JULY, 22, 9, 30),
                subCategory = null,
            ),
        )
        val prepared = classifier.prepare(
            tasks = tasks,
            sourceRange = TimeRange(monday, local(2026, Calendar.JULY, 22, 23, 59)),
            now = local(2026, Calendar.JULY, 23, 0, 0),
            timeZone = UTC,
        )

        val analytics = calculator.calculate(
            category = CATEGORY,
            currentPlanTasks = prepared,
            comparisonPlanTasks = emptyList(),
            currentCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 20),
                token(2026, Calendar.JULY, 22),
            ),
            locale = Locale.US,
            timeZone = UTC,
        )

        assertEquals(CategoryObservationType.BUSIEST_DAY, analytics.observation?.type)
        assertEquals(token(2026, Calendar.JULY, 20), analytics.observation?.day)
        assertEquals(0.4, analytics.observation?.valuePercent ?: -1.0, 0.0001)
    }

    private fun task(
        key: Long,
        categoryId: Long,
        source: Date,
        from: Date,
        to: Date,
        subCategory: SubCategory?,
    ) = TimeTask(
        key = key,
        date = source,
        timeRange = TimeRange(from, to),
        category = MainCategory(id = categoryId),
        subCategory = subCategory,
        isCompleted = true,
        isConsiderInStatistics = true,
    )

    private fun token(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance(UTC).apply {
            clear()
            set(year, month, day, 0, 0, 0)
        }.time
    }

    private fun local(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date {
        return Calendar.getInstance(UTC).apply {
            clear()
            set(year, month, day, hour, minute, 0)
        }.time
    }

    companion object {
        private val UTC = TimeZone.getTimeZone("UTC")
        private const val CATEGORY_ID = 1L
        private const val MINUTE = 60_000L
        private const val HOUR = 60L * 60L * 1_000L
        private val SUBCATEGORY = SubCategory(id = 1L, mainCategoryId = CATEGORY_ID, name = "Sub")
        private val CATEGORY = MainCategoryDetails(MainCategory(id = CATEGORY_ID), listOf(SUBCATEGORY))
    }
}
