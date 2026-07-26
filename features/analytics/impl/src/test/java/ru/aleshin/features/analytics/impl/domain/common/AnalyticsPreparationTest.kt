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
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsBucketGranularity
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTaskStatus
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class AnalyticsPreparationTest {

    private val classifier = AnalyticsTaskClassifier.Base()
    private val splitter = AnalyticsIntervalSplitter.Base()
    private val bucketCalculator = AnalyticsBucketCalculator.Base()

    @Test
    fun classifierUsesSourceDateDeduplicatesAndKeepsInvalidDurationInCounts() {
        val now = local(2026, Calendar.JULY, 21, 12, 0)
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val tasks = listOf(
            task(1L, source, local(2026, Calendar.JULY, 21, 10, 0), now, true),
            task(1L, source, local(2026, Calendar.JULY, 21, 10, 0), now, true),
            task(2L, source, local(2026, Calendar.JULY, 21, 13, 0), local(2026, Calendar.JULY, 21, 12, 0), true),
            task(3L, source, local(2026, Calendar.JULY, 21, 12, 0), local(2026, Calendar.JULY, 21, 13, 0), false),
            task(4L, local(2026, Calendar.JULY, 20, 0, 0), source, now, true),
        )
        val range = TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59))

        val prepared = classifier.prepare(tasks, range, now, UTC)

        assertEquals(listOf(1L, 2L, 3L), prepared.map { it.timeTask.key })
        assertEquals(AnalyticsTaskStatus.COMPLETED, prepared[0].status)
        assertEquals(AnalyticsTaskStatus.COMPLETED, prepared[1].status)
        assertEquals(0L, prepared[1].safeDurationMillis)
        assertEquals(AnalyticsTaskStatus.UNFINISHED, prepared[2].status)
    }

    @Test
    fun classifierUsesEndBoundaryAndCompletionFlagForExclusiveStatuses() {
        val source = local(2026, Calendar.JULY, 21, 0, 0)
        val now = local(2026, Calendar.JULY, 21, 12, 0)
        val tasks = listOf(
            task(1L, source, local(2026, Calendar.JULY, 21, 10, 0), Date(now.time - 1L), true),
            task(2L, source, local(2026, Calendar.JULY, 21, 10, 0), Date(now.time - 1L), false),
            task(3L, source, local(2026, Calendar.JULY, 21, 11, 0), now, true),
            task(4L, source, local(2026, Calendar.JULY, 21, 11, 0), now, false),
            task(5L, source, local(2026, Calendar.JULY, 21, 12, 0), Date(now.time + 1L), true),
            task(6L, source, local(2026, Calendar.JULY, 21, 12, 0), Date(now.time + 1L), false),
        )

        val statuses = classifier.prepare(
            tasks,
            TimeRange(source, local(2026, Calendar.JULY, 21, 23, 59)),
            now,
            UTC,
        ).associate { it.timeTask.key to it.status }

        assertEquals(AnalyticsTaskStatus.COMPLETED, statuses[1L])
        assertEquals(AnalyticsTaskStatus.SKIPPED, statuses[2L])
        assertEquals(AnalyticsTaskStatus.COMPLETED, statuses[3L])
        assertEquals(AnalyticsTaskStatus.SKIPPED, statuses[4L])
        assertEquals(AnalyticsTaskStatus.UNFINISHED, statuses[5L])
        assertEquals(AnalyticsTaskStatus.UNFINISHED, statuses[6L])
    }

    @Test
    fun overnightSplitterAttributesActualHoursWithoutMidnightLeak() {
        val range = TimeRange(
            local(2026, Calendar.JULY, 19, 23, 0),
            local(2026, Calendar.JULY, 20, 3, 0),
        )

        val slices = splitter.splitByHour(range, UTC)

        assertEquals(listOf(23, 0, 1, 2), slices.map { it.hourOfDay })
        assertEquals(listOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.MONDAY, Calendar.MONDAY), slices.map { it.dayOfWeek })
        assertTrue(slices.all { it.durationMillis == 60L * 60L * 1_000L })
    }

    @Test
    fun splitterUsesRealDstDuration() {
        val newYork = TimeZone.getTimeZone("America/New_York")
        val range = TimeRange(
            local(2026, Calendar.MARCH, 8, 0, 0, newYork),
            local(2026, Calendar.MARCH, 8, 4, 0, newYork),
        )

        val slices = splitter.splitByHour(range, newYork)

        assertEquals(listOf(0, 1, 3), slices.map { it.hourOfDay })
        assertEquals(3L * 60L * 60L * 1_000L, slices.sumOf { it.durationMillis })
    }

    @Test
    fun splitterKeepsRepeatedHourDuringDstEnd() {
        val newYork = TimeZone.getTimeZone("America/New_York")
        val range = TimeRange(
            local(2026, Calendar.NOVEMBER, 1, 0, 0, newYork),
            local(2026, Calendar.NOVEMBER, 1, 4, 0, newYork),
        )

        val slices = splitter.splitByHour(range, newYork)

        assertEquals(listOf(0, 1, 1, 2, 3), slices.map { it.hourOfDay })
        assertEquals(5L * 60L * 60L * 1_000L, slices.sumOf { it.durationMillis })
    }

    @Test
    fun bucketsSwitchAtExactThresholdsAndClipCalendarBuckets() {
        val day14 = range(2026, Calendar.JANUARY, 1, 14)
        val day15 = range(2026, Calendar.JANUARY, 1, 15)
        val day186 = range(2026, Calendar.JANUARY, 1, 186)
        val day187 = range(2026, Calendar.JANUARY, 1, 187)

        assertEquals(AnalyticsBucketGranularity.DAY, bucketCalculator.calculate(day14, Locale.US).first)
        assertEquals(AnalyticsBucketGranularity.WEEK, bucketCalculator.calculate(day15, Locale.US).first)
        assertEquals(AnalyticsBucketGranularity.WEEK, bucketCalculator.calculate(day186, Locale.US).first)
        assertEquals(AnalyticsBucketGranularity.MONTH, bucketCalculator.calculate(day187, Locale.US).first)
        assertEquals(day15.from, bucketCalculator.calculate(day15, Locale.forLanguageTag("ru")).second.first().range.from)
        assertEquals(day15.to, bucketCalculator.calculate(day15, Locale.forLanguageTag("ru")).second.last().range.to)
    }

    private fun task(
        key: Long,
        sourceDate: Date,
        from: Date,
        to: Date,
        isCompleted: Boolean,
    ) = TimeTask(
        key = key,
        date = sourceDate,
        timeRange = TimeRange(from, to),
        category = MainCategory(id = 1L),
        isCompleted = isCompleted,
        isConsiderInStatistics = true,
    )

    private fun range(year: Int, month: Int, day: Int, count: Int): AnalyticsCivilDateRange {
        val from = token(year, month, day)
        val to = Calendar.getInstance(UTC).apply {
            time = from
            add(Calendar.DAY_OF_YEAR, count - 1)
        }.time
        return AnalyticsCivilDateRange(from, to)
    }

    private fun token(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance(UTC).apply {
            clear()
            set(year, month, day, 0, 0, 0)
        }.time
    }

    private fun local(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        timeZone: TimeZone = UTC,
    ): Date {
        return Calendar.getInstance(timeZone).apply {
            clear()
            set(year, month, day, hour, minute, 0)
        }.time
    }

    companion object {
        private val UTC = TimeZone.getTimeZone("UTC")
    }
}
