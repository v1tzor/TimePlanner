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
import ru.aleshin.core.domain.entities.tasks.TaskPriority
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCategorySort
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparisonState
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsDurationBucketType
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsPlanSourceType
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class AnalyticsOverviewCalculatorTest {

    private val classifier = AnalyticsTaskClassifier.Base()
    private val calculator = AnalyticsOverviewCalculator.Base(
        bucketCalculator = AnalyticsBucketCalculator.Base(),
        intervalSplitter = AnalyticsIntervalSplitter.Base(),
    )

    @Test
    fun goldenOvernightUsesSourceAndActualIntervalAttribution() {
        val sunday = local(2026, Calendar.JULY, 19, 0, 0)
        val currentRange = TimeRange(sunday, local(2026, Calendar.JULY, 19, 23, 59))
        val task = task(
            key = 1L,
            source = sunday,
            from = local(2026, Calendar.JULY, 19, 23, 0),
            to = local(2026, Calendar.JULY, 20, 3, 0),
        )
        val current = classifier.prepare(listOf(task), currentRange, local(2026, Calendar.JULY, 19, 12, 0), UTC)

        val overview = calculator.calculate(
            currentTasks = current,
            comparisonTasks = emptyList(),
            currentCivilRange = AnalyticsCivilDateRange(token(2026, Calendar.JULY, 19), token(2026, Calendar.JULY, 19)),
            comparisonCivilRange = AnalyticsCivilDateRange(token(2026, Calendar.JULY, 12), token(2026, Calendar.JULY, 12)),
            categorySort = AnalyticsCategorySort.BY_TIME,
            locale = Locale.US,
            timeZone = UTC,
        )

        assertEquals(4L * HOUR, overview.summary.plannedDurationMillis)
        assertEquals(0, overview.summary.completion.completedTaskCount)
        assertEquals(1, overview.summary.completion.allTaskCount)
        assertEquals(0.0, overview.summary.completion.share)
        assertEquals(4L * HOUR, overview.load.buckets.single().durationMillis)
        assertEquals(1L * HOUR, overview.keyMetrics.weekendDurationMillis)
        assertEquals(0.25, overview.keyMetrics.weekendShare, 0.0001)
        assertEquals(token(2026, Calendar.JULY, 20), overview.keyMetrics.busiestDay)
        assertEquals(3L * HOUR, overview.keyMetrics.busiestDayDurationMillis)
        assertEquals(1, overview.regularity.activeDayCount)
        val sundayRow = overview.weekdayHourLoad.rows.single { it.dayOfWeek == Calendar.SUNDAY }
        val mondayRow = overview.weekdayHourLoad.rows.single { it.dayOfWeek == Calendar.MONDAY }
        val sundayBucket = sundayRow.cells.single { it.fromHour == 21 }
        val mondayBucket = mondayRow.cells.single { it.fromHour == 0 }
        assertEquals(8, sundayRow.cells.size)
        assertEquals(24, sundayBucket.toHour)
        assertEquals(3, mondayBucket.toHour)
        assertEquals(60.0, sundayBucket.totalMinutes, 0.0)
        assertEquals(180.0, mondayBucket.totalMinutes, 0.0)
        assertEquals(20.0, sundayBucket.averageMinutes, 0.0)
        assertEquals(60.0, mondayBucket.averageMinutes, 0.0)
    }

    @Test
    fun completionIncludesFutureAndUsesRelativePreviousZeroState() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val range = TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59))
        val currentTasks = listOf(
            task(1L, source, local(2026, Calendar.JULY, 21, 8, 0), local(2026, Calendar.JULY, 21, 9, 0)),
            task(2L, source, local(2026, Calendar.JULY, 21, 18, 0), local(2026, Calendar.JULY, 21, 19, 0)),
        )
        val previousTasks = listOf(
            task(3L, source, local(2026, Calendar.JULY, 21, 8, 0), local(2026, Calendar.JULY, 21, 9, 0), isCompleted = false),
        )
        val now = local(2026, Calendar.JULY, 21, 12, 0)
        val current = classifier.prepare(currentTasks, range, now, UTC)
        val previous = classifier.prepare(previousTasks, range, now, UTC)

        val overview = calculate(current, previous)

        assertEquals(1, overview.summary.completion.completedTaskCount)
        assertEquals(2, overview.summary.completion.allTaskCount)
        assertEquals(0.5, overview.summary.completion.share)
        assertEquals(AnalyticsComparisonState.PREVIOUS_ZERO, overview.summary.completion.comparison.state)
        assertNull(overview.summary.completion.comparison.changePercent)
    }

    @Test
    fun durationCreationAndSourceContractsUsePositiveAndUnlinkedTasks() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val now = local(2026, Calendar.JULY, 22, 0, 0)
        val tasks = listOf(
            task(1L, source, source, Date(source.time + 30L * MINUTE), createdAt = Date(source.time - 3L * DAY)),
            task(2L, source, source, Date(source.time + 2L * HOUR), createdAt = Date(source.time - DAY)),
            task(3L, source, source, Date(source.time + 3L * HOUR), linkedTemplateId = 9L, createdAt = Date(source.time - DAY)),
            task(4L, source, Date(source.time + HOUR), source, createdAt = source),
            task(5L, source, Date(source.time + 4L * HOUR), Date(source.time + 3L * HOUR)),
        )
        val prepared = classifier.prepare(
            tasks,
            TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59)),
            now,
            UTC,
        )

        val overview = calculate(prepared, emptyList())

        assertEquals(3, overview.creation.qualifyingTaskCount)
        assertEquals(4, overview.planSource.buckets.single { it.type == AnalyticsPlanSourceType.UNLINKED }.taskCount)
        assertEquals(1, overview.planSource.buckets.single { it.type == AnalyticsPlanSourceType.LINKED }.taskCount)
        assertEquals(1, overview.durations.buckets.single { it.type == AnalyticsDurationBucketType.SHORT }.taskCount)
        assertEquals(1, overview.durations.buckets.single { it.type == AnalyticsDurationBucketType.MEDIUM }.taskCount)
        assertEquals(1, overview.durations.buckets.single { it.type == AnalyticsDurationBucketType.LONG }.taskCount)
        assertEquals(2L * HOUR, overview.durations.medianDurationMillis)
        assertEquals(5, overview.summary.completion.allTaskCount)
    }

    @Test
    fun creationBucketsUseCivilLeadTimeBoundaries() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val prepared = classifier.prepare(
            tasks = listOf(
                task(
                    key = 1L,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 8, 0),
                    to = local(2026, Calendar.JULY, 21, 9, 0),
                    createdAt = local(2026, Calendar.JULY, 18, 8, 0),
                ),
                task(
                    key = 2L,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 8, 0),
                    to = local(2026, Calendar.JULY, 21, 9, 0),
                    createdAt = local(2026, Calendar.JULY, 20, 8, 0),
                ),
                task(
                    key = 3L,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 8, 0),
                    to = local(2026, Calendar.JULY, 21, 9, 0),
                    createdAt = local(2026, Calendar.JULY, 21, 7, 0),
                ),
                task(
                    key = 4L,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 8, 0),
                    to = local(2026, Calendar.JULY, 21, 9, 0),
                    createdAt = local(2026, Calendar.JULY, 21, 9, 0),
                ),
            ),
            sourceRange = TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59)),
            now = local(2026, Calendar.JULY, 22, 0, 0),
            timeZone = UTC,
        )

        val creation = calculate(prepared, emptyList()).creation

        assertEquals(4, creation.qualifyingTaskCount)
        assertEquals(4L * HOUR, creation.totalDurationMillis)
        assertEquals(12L * HOUR + 30L * MINUTE, creation.medianLeadTimeMillis)
        assertTrue(creation.buckets.all { it.taskCount == 1 })
        assertTrue(creation.buckets.all { it.durationMillis == HOUR })
        assertTrue(creation.buckets.all { it.share == 0.25 })
    }

    @Test
    fun zeroDurationTailDoesNotCreateOtherCategoryAndHourTieUsesEarlierHour() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val tasks = (1L..5L).map { key ->
            task(
                key = key,
                source = source,
                from = local(2026, Calendar.JULY, 21, (key + 5L).toInt(), 0),
                to = local(2026, Calendar.JULY, 21, (key + 6L).toInt(), 0),
                categoryId = key,
            )
        } + task(
            key = 6L,
            source = source,
            from = local(2026, Calendar.JULY, 21, 18, 0),
            to = local(2026, Calendar.JULY, 21, 17, 0),
            categoryId = 6L,
        )
        val prepared = classifier.prepare(
            tasks = tasks,
            sourceRange = TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59)),
            now = local(2026, Calendar.JULY, 22, 0, 0),
            timeZone = UTC,
        )

        val overview = calculate(prepared, emptyList())

        assertEquals(5, overview.categories.collapsedBucketCount)
        assertNull(overview.categories.otherBucket)
        assertEquals(6, overview.categories.buckets.size)
        assertEquals(6, overview.summary.completion.allTaskCount)
        assertEquals(0L, overview.categories.buckets.single { it.category?.id == 6L }.durationMillis)
        val tuesdayRow = overview.weekdayHourLoad.rows.single { it.dayOfWeek == Calendar.TUESDAY }
        val busiestBucket = tuesdayRow.cells[checkNotNull(tuesdayRow.busiestCellIndex)]
        assertEquals(6, busiestBucket.fromHour)
        assertEquals(9, busiestBucket.toHour)
        assertEquals(180.0, busiestBucket.totalMinutes, 0.0)
        assertEquals(60.0, busiestBucket.averageMinutes, 0.0)
    }

    @Test
    fun categorySortSelectsOneDomainPreparedOrder() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val prepared = classifier.prepare(
            tasks = listOf(
                task(
                    key = 1L,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 8, 0),
                    to = local(2026, Calendar.JULY, 21, 11, 0),
                    categoryId = 1L,
                ),
                task(
                    key = 2L,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 12, 0),
                    to = local(2026, Calendar.JULY, 21, 13, 0),
                    categoryId = 2L,
                ),
                task(
                    key = 3L,
                    source = source,
                    from = local(2026, Calendar.JULY, 21, 14, 0),
                    to = local(2026, Calendar.JULY, 21, 15, 0),
                    categoryId = 2L,
                ),
            ),
            sourceRange = TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59)),
            now = local(2026, Calendar.JULY, 22, 0, 0),
            timeZone = UTC,
        )

        val byTime = calculate(prepared, emptyList(), AnalyticsCategorySort.BY_TIME)
        val byTasks = calculate(prepared, emptyList(), AnalyticsCategorySort.BY_TASKS)

        assertEquals(1L, byTime.categories.buckets.first().category?.id)
        assertEquals(2L, byTasks.categories.buckets.first().category?.id)
        assertEquals(2, byTime.categories.buckets.size)
        assertEquals(2, byTasks.categories.buckets.size)
    }

    @Test
    fun completionComparisonUsesAllTasksAndRelativeChange() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val range = TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59))
        val now = local(2026, Calendar.JULY, 21, 12, 0)
        val current = classifier.prepare(
            listOf(
                task(1L, source, local(2026, Calendar.JULY, 21, 7, 0), local(2026, Calendar.JULY, 21, 8, 0)),
                task(2L, source, local(2026, Calendar.JULY, 21, 8, 0), local(2026, Calendar.JULY, 21, 9, 0)),
                task(3L, source, local(2026, Calendar.JULY, 21, 9, 0), local(2026, Calendar.JULY, 21, 10, 0)),
                task(4L, source, local(2026, Calendar.JULY, 21, 18, 0), local(2026, Calendar.JULY, 21, 19, 0)),
            ),
            range,
            now,
            UTC,
        )
        val previous = classifier.prepare(
            listOf(
                task(5L, source, local(2026, Calendar.JULY, 21, 7, 0), local(2026, Calendar.JULY, 21, 8, 0)),
                task(6L, source, local(2026, Calendar.JULY, 21, 8, 0), local(2026, Calendar.JULY, 21, 9, 0), isCompleted = false),
            ),
            range,
            now,
            UTC,
        )

        val overview = calculate(current, previous)

        assertEquals(3, overview.summary.completion.completedTaskCount)
        assertEquals(4, overview.summary.completion.allTaskCount)
        assertEquals(0.75, overview.summary.completion.share)
        assertEquals(AnalyticsComparisonState.VALUE, overview.summary.completion.comparison.state)
        assertEquals(0.5, overview.summary.completion.comparison.changePercent ?: -1.0, 0.0001)
        assertEquals(
            AnalyticsComparisonState.UNAVAILABLE,
            calculate(emptyList(), emptyList()).summary.completion.comparison.state,
        )
    }

    @Test
    fun keyMetricsMergeTouchingBlocksSplitWeekendsAndResolveTiesByEarlierDate() {
        val friday = local(2026, Calendar.JULY, 17, 0, 0)
        val saturday = local(2026, Calendar.JULY, 18, 0, 0)
        val sunday = local(2026, Calendar.JULY, 19, 0, 0)
        val tasks = listOf(
            task(
                key = 1L,
                source = friday,
                from = local(2026, Calendar.JULY, 17, 23, 30),
                to = local(2026, Calendar.JULY, 18, 0, 30),
                priority = TaskPriority.MEDIUM,
            ),
            task(
                key = 2L,
                source = saturday,
                from = local(2026, Calendar.JULY, 18, 0, 30),
                to = local(2026, Calendar.JULY, 18, 1, 0),
                priority = TaskPriority.MAX,
            ),
            task(
                key = 3L,
                source = sunday,
                from = local(2026, Calendar.JULY, 19, 12, 0),
                to = local(2026, Calendar.JULY, 19, 13, 0),
                priority = TaskPriority.STANDARD,
            ),
        )
        val prepared = classifier.prepare(
            tasks,
            TimeRange(friday, local(2026, Calendar.JULY, 19, 23, 59)),
            local(2026, Calendar.JULY, 20, 0, 0),
            UTC,
        )

        val overview = calculator.calculate(
            currentTasks = prepared,
            comparisonTasks = emptyList(),
            currentCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 17),
                token(2026, Calendar.JULY, 19),
            ),
            comparisonCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 14),
                token(2026, Calendar.JULY, 16),
            ),
            categorySort = AnalyticsCategorySort.BY_TIME,
            locale = Locale.US,
            timeZone = UTC,
        )

        assertEquals(90L * MINUTE, overview.keyMetrics.importantDurationMillis)
        assertEquals(2L * HOUR, overview.keyMetrics.weekendDurationMillis)
        assertEquals(0.8, overview.keyMetrics.weekendShare, 0.0001)
        assertEquals(local(2026, Calendar.JULY, 17, 23, 30), overview.keyMetrics.longestBlock?.from)
        assertEquals(local(2026, Calendar.JULY, 18, 1, 0), overview.keyMetrics.longestBlock?.to)
        assertEquals(token(2026, Calendar.JULY, 18), overview.keyMetrics.busiestDay)
        assertEquals(1L * HOUR, overview.keyMetrics.busiestDayDurationMillis)
    }

    @Test
    fun evenDurationMedianAndMissingOrLinkedCreationRowsAreHandledIndependently() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val prepared = classifier.prepare(
            listOf(
                task(
                    1L,
                    source,
                    local(2026, Calendar.JULY, 21, 8, 0),
                    local(2026, Calendar.JULY, 21, 8, 30),
                    createdAt = local(2026, Calendar.JULY, 20, 8, 0),
                ),
                task(
                    2L,
                    source,
                    local(2026, Calendar.JULY, 21, 9, 0),
                    local(2026, Calendar.JULY, 21, 11, 0),
                    createdAt = null,
                ),
                task(
                    3L,
                    source,
                    local(2026, Calendar.JULY, 21, 12, 0),
                    local(2026, Calendar.JULY, 21, 15, 0),
                    linkedTemplateId = 9L,
                    createdAt = local(2026, Calendar.JULY, 20, 12, 0),
                ),
                task(
                    4L,
                    source,
                    local(2026, Calendar.JULY, 21, 16, 0),
                    local(2026, Calendar.JULY, 21, 20, 0),
                    linkedTemplateId = 10L,
                    createdAt = local(2026, Calendar.JULY, 20, 14, 0),
                ),
            ),
            TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59)),
            local(2026, Calendar.JULY, 22, 0, 0),
            UTC,
        )

        val overview = calculate(prepared, emptyList())

        assertEquals(150L * MINUTE, overview.durations.medianDurationMillis)
        assertEquals(1, overview.creation.qualifyingTaskCount)
        assertEquals(2, overview.planSource.buckets.single { it.type == AnalyticsPlanSourceType.LINKED }.taskCount)
    }

    @Test
    fun averageAndMedianDurationComparisonsUsePositiveDurations() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val range = TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59))
        val now = local(2026, Calendar.JULY, 22, 0, 0)
        val current = classifier.prepare(
            listOf(
                task(1L, source, source, Date(source.time + HOUR)),
                task(2L, source, Date(source.time + 2L * HOUR), Date(source.time + 5L * HOUR)),
                task(3L, source, Date(source.time + 6L * HOUR), Date(source.time + 5L * HOUR)),
            ),
            range,
            now,
            UTC,
        )
        val previous = classifier.prepare(
            listOf(task(4L, source, source, Date(source.time + HOUR))),
            range,
            now,
            UTC,
        )

        val overview = calculate(current, previous)

        assertEquals(2L * HOUR, overview.durations.averageDurationMillis)
        assertEquals(2L * HOUR, overview.durations.medianDurationMillis)
        assertEquals(1.0, overview.durations.averageComparison.changePercent ?: -1.0, 0.0001)
        assertEquals(1.0, overview.durations.medianComparison.changePercent ?: -1.0, 0.0001)
        assertEquals(
            AnalyticsComparisonState.UNAVAILABLE,
            calculate(current, emptyList()).durations.averageComparison.state,
        )
    }

    @Test
    fun overviewDistributionsConservePreparedTaskTotals() {
        val monday = local(2026, Calendar.JULY, 20, 0, 0)
        val sunday = local(2026, Calendar.JULY, 26, 0, 0)
        val prepared = classifier.prepare(
            tasks = listOf(
                task(
                    key = 1L,
                    source = monday,
                    from = local(2026, Calendar.JULY, 20, 8, 0),
                    to = local(2026, Calendar.JULY, 20, 8, 30),
                    createdAt = local(2026, Calendar.JULY, 17, 8, 0),
                    categoryId = 1L,
                ),
                task(
                    key = 2L,
                    source = local(2026, Calendar.JULY, 21, 0, 0),
                    from = local(2026, Calendar.JULY, 21, 9, 0),
                    to = local(2026, Calendar.JULY, 21, 11, 0),
                    linkedTemplateId = 9L,
                    categoryId = 1L,
                ),
                task(
                    key = 3L,
                    source = local(2026, Calendar.JULY, 22, 0, 0),
                    from = local(2026, Calendar.JULY, 22, 22, 0),
                    to = local(2026, Calendar.JULY, 23, 1, 0),
                    createdAt = local(2026, Calendar.JULY, 22, 10, 0),
                    categoryId = 2L,
                ),
                task(
                    key = 4L,
                    source = local(2026, Calendar.JULY, 24, 0, 0),
                    from = local(2026, Calendar.JULY, 24, 14, 0),
                    to = local(2026, Calendar.JULY, 24, 16, 30),
                    createdAt = local(2026, Calendar.JULY, 23, 14, 0),
                    categoryId = 3L,
                ),
                task(
                    key = 5L,
                    source = sunday,
                    from = local(2026, Calendar.JULY, 26, 12, 0),
                    to = local(2026, Calendar.JULY, 26, 11, 0),
                    categoryId = 4L,
                ),
            ),
            sourceRange = TimeRange(monday, local(2026, Calendar.JULY, 26, 23, 59)),
            now = local(2026, Calendar.JULY, 27, 0, 0),
            timeZone = UTC,
        )
        val overview = calculator.calculate(
            currentTasks = prepared,
            comparisonTasks = emptyList(),
            currentCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 20),
                token(2026, Calendar.JULY, 26),
            ),
            comparisonCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 13),
                token(2026, Calendar.JULY, 19),
            ),
            categorySort = AnalyticsCategorySort.BY_TIME,
            locale = Locale.US,
            timeZone = UTC,
        )
        val totalDuration = 8L * HOUR

        assertEquals(totalDuration, overview.summary.plannedDurationMillis)
        assertEquals(
            totalDuration,
            overview.summary.completedDurationMillis +
                overview.summary.skippedDurationMillis +
                overview.summary.unfinishedDurationMillis,
        )
        assertEquals(totalDuration, overview.categories.buckets.sumOf { it.durationMillis })
        assertEquals(prepared.size, overview.categories.buckets.sumOf { it.taskCount })
        assertEquals(1.0, overview.categories.buckets.sumOf { it.share }, 0.0001)
        assertEquals(totalDuration, overview.load.buckets.sumOf { it.durationMillis })
        assertEquals(prepared.size, overview.load.buckets.sumOf { it.taskCount })
        assertEquals(6L * HOUR, overview.creation.totalDurationMillis)
        assertEquals(3, overview.creation.qualifyingTaskCount)
        assertEquals(overview.creation.totalDurationMillis, overview.creation.buckets.sumOf { it.durationMillis })
        assertEquals(overview.creation.qualifyingTaskCount, overview.creation.buckets.sumOf { it.taskCount })
        assertEquals(1.0, overview.creation.buckets.sumOf { it.share }, 0.0001)
        assertEquals(totalDuration, overview.durations.totalDurationMillis)
        assertEquals(4, overview.durations.buckets.sumOf { it.taskCount })
        assertEquals(1.0, overview.durations.buckets.sumOf { it.share }, 0.0001)
        assertEquals(totalDuration, overview.planSource.buckets.sumOf { it.durationMillis })
        assertEquals(prepared.size, overview.planSource.buckets.sumOf { it.taskCount })
        assertEquals(1.0, overview.planSource.buckets.sumOf { it.share }, 0.0001)
        assertEquals(
            totalDuration.toDouble() / MINUTE,
            overview.weekdayHourLoad.rows.flatMap { it.cells }.sumOf { it.totalMinutes },
            0.0001,
        )
        assertEquals(5, overview.regularity.activeDayCount)
        assertEquals(7, overview.regularity.totalDayCount)
        assertTrue(overview.weekdayHourLoad.rows.flatMap { it.cells }.all { it.level in 0..4 })
    }

    @Test
    fun emptyHeatmapRowsDoNotInventBusiestIntervals() {
        val tuesday = local(2026, Calendar.JULY, 21, 0, 0)
        val prepared = classifier.prepare(
            tasks = listOf(
                task(
                    key = 1L,
                    source = tuesday,
                    from = local(2026, Calendar.JULY, 21, 9, 0),
                    to = local(2026, Calendar.JULY, 21, 10, 0),
                ),
            ),
            sourceRange = TimeRange(tuesday, local(2026, Calendar.JULY, 21, 23, 59)),
            now = local(2026, Calendar.JULY, 22, 0, 0),
            timeZone = UTC,
        )

        val overview = calculate(prepared, emptyList())

        val tuesdayRow = overview.weekdayHourLoad.rows.single { it.dayOfWeek == Calendar.TUESDAY }
        assertEquals(9, tuesdayRow.cells[checkNotNull(tuesdayRow.busiestCellIndex)].fromHour)
        assertTrue(
            overview.weekdayHourLoad.rows
                .filterNot { it.dayOfWeek == Calendar.TUESDAY }
                .all { it.busiestCellIndex == null },
        )
        assertTrue(calculate(emptyList(), emptyList()).weekdayHourLoad.rows.all { it.busiestCellIndex == null })
    }

    @Test
    fun weekdayHourAverageUsesWeekdayOccurrencesAndBucketWidth() {
        val firstMonday = local(2026, Calendar.JULY, 20, 0, 0)
        val secondTuesday = local(2026, Calendar.JULY, 28, 0, 0)
        val prepared = classifier.prepare(
            tasks = listOf(
                task(
                    key = 1L,
                    source = local(2026, Calendar.JULY, 21, 0, 0),
                    from = local(2026, Calendar.JULY, 21, 9, 0),
                    to = local(2026, Calendar.JULY, 21, 12, 0),
                ),
                task(
                    key = 2L,
                    source = secondTuesday,
                    from = local(2026, Calendar.JULY, 28, 9, 0),
                    to = local(2026, Calendar.JULY, 28, 10, 30),
                ),
            ),
            sourceRange = TimeRange(firstMonday, local(2026, Calendar.AUGUST, 2, 23, 59)),
            now = local(2026, Calendar.AUGUST, 3, 0, 0),
            timeZone = UTC,
        )
        val overview = calculator.calculate(
            currentTasks = prepared,
            comparisonTasks = emptyList(),
            currentCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 20),
                token(2026, Calendar.AUGUST, 2),
            ),
            comparisonCivilRange = AnalyticsCivilDateRange(
                token(2026, Calendar.JULY, 6),
                token(2026, Calendar.JULY, 19),
            ),
            categorySort = AnalyticsCategorySort.BY_TIME,
            locale = Locale.US,
            timeZone = UTC,
        )

        val tuesdayRow = overview.weekdayHourLoad.rows.single { it.dayOfWeek == Calendar.TUESDAY }
        val cell = tuesdayRow.cells.single { it.fromHour == 9 }

        assertEquals(270.0, cell.totalMinutes, 0.0)
        assertEquals(45.0, cell.averageMinutes, 0.0)
        assertEquals(4, cell.level)
        assertEquals(9, tuesdayRow.cells[checkNotNull(tuesdayRow.busiestCellIndex)].fromHour)
    }

    private fun calculate(
        current: List<ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTask>,
        previous: List<ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTask>,
        categorySort: AnalyticsCategorySort = AnalyticsCategorySort.BY_TIME,
    ) = calculator.calculate(
        currentTasks = current,
        comparisonTasks = previous,
        currentCivilRange = AnalyticsCivilDateRange(token(2026, Calendar.JULY, 21), token(2026, Calendar.JULY, 21)),
        comparisonCivilRange = AnalyticsCivilDateRange(token(2026, Calendar.JULY, 20), token(2026, Calendar.JULY, 20)),
        categorySort = categorySort,
        locale = Locale.US,
        timeZone = UTC,
    )

    private fun task(
        key: Long,
        source: Date,
        from: Date,
        to: Date,
        isCompleted: Boolean = true,
        linkedTemplateId: Long? = null,
        createdAt: Date? = null,
        categoryId: Long = key % 2 + 1,
        priority: TaskPriority = if (key == 1L) TaskPriority.MAX else TaskPriority.STANDARD,
    ) = TimeTask(
        key = key,
        date = source,
        timeRange = TimeRange(from, to),
        createdAt = createdAt,
        category = MainCategory(id = categoryId),
        linkedTemplateId = linkedTemplateId,
        isCompleted = isCompleted,
        priority = priority,
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
        private const val MINUTE = 60_000L
        private const val HOUR = 60L * MINUTE
        private const val DAY = 24L * HOUR
    }
}
