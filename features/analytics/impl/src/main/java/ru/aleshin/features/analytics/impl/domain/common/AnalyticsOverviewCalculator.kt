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

import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCategoryBucket
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCategoryDistribution
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCategorySort
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparison
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparisonState
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCompletion
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCreationBucket
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCreationBucketType
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCreationDistribution
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsDurationBucket
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsDurationBucketType
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsDurationDistribution
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsKeyMetrics
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsLoadBucket
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsLoadDistribution
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsOverview
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsPlanSourceBucket
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsPlanSourceDistribution
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsPlanSourceType
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsRegularity
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsSummary
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTask
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTaskStatus
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsWeekdayHourCell
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsWeekdayHourLoad
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsWeekdayHourRow
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface AnalyticsOverviewCalculator {

    fun calculate(
        currentTasks: List<AnalyticsTask>,
        comparisonTasks: List<AnalyticsTask>,
        currentCivilRange: AnalyticsCivilDateRange,
        comparisonCivilRange: AnalyticsCivilDateRange,
        categorySort: AnalyticsCategorySort,
        locale: Locale,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): AnalyticsOverview

    class Base @Inject constructor(
        private val bucketCalculator: AnalyticsBucketCalculator,
        private val intervalSplitter: AnalyticsIntervalSplitter,
    ) : AnalyticsOverviewCalculator {

        override fun calculate(
            currentTasks: List<AnalyticsTask>,
            comparisonTasks: List<AnalyticsTask>,
            currentCivilRange: AnalyticsCivilDateRange,
            comparisonCivilRange: AnalyticsCivilDateRange,
            categorySort: AnalyticsCategorySort,
            locale: Locale,
            timeZone: TimeZone,
        ): AnalyticsOverview {
            val totalDuration = currentTasks.sumOf { it.safeDurationMillis }
            return AnalyticsOverview(
                summary = calculateSummary(currentTasks, comparisonTasks),
                categories = calculateCategories(
                    currentTasks = currentTasks,
                    comparisonTasks = comparisonTasks,
                    totalDuration = totalDuration,
                    sort = categorySort,
                ),
                load = calculateLoad(currentTasks, currentCivilRange, locale, timeZone),
                creation = calculateCreation(currentTasks, timeZone),
                durations = calculateDurations(currentTasks, comparisonTasks),
                planSource = calculatePlanSource(currentTasks, totalDuration),
                keyMetrics = calculateKeyMetrics(currentTasks, totalDuration, timeZone),
                regularity = calculateRegularity(
                    currentTasks,
                    comparisonTasks,
                    currentCivilRange,
                    comparisonCivilRange,
                    timeZone,
                ),
                weekdayHourLoad = calculateWeekdayHourLoad(
                    tasks = currentTasks,
                    range = currentCivilRange,
                    locale = locale,
                    timeZone = timeZone,
                ),
            )
        }

        private fun calculateSummary(
            currentTasks: List<AnalyticsTask>,
            comparisonTasks: List<AnalyticsTask>,
        ): AnalyticsSummary {
            val currentCompleted = currentTasks.count { it.status == AnalyticsTaskStatus.COMPLETED }
            val previousCompleted = comparisonTasks.count { it.status == AnalyticsTaskStatus.COMPLETED }
            val currentShare = currentTasks.size.takeIf { it > 0 }?.let { currentCompleted.toDouble() / it }
            val previousShare = comparisonTasks.size.takeIf { it > 0 }?.let { previousCompleted.toDouble() / it }
            return AnalyticsSummary(
                plannedDurationMillis = currentTasks.sumOf { it.safeDurationMillis },
                completedDurationMillis = currentTasks.filter { it.status == AnalyticsTaskStatus.COMPLETED }.sumOf { it.safeDurationMillis },
                skippedDurationMillis = currentTasks.filter { it.status == AnalyticsTaskStatus.SKIPPED }.sumOf { it.safeDurationMillis },
                unfinishedDurationMillis = currentTasks.filter { it.status == AnalyticsTaskStatus.UNFINISHED }.sumOf { it.safeDurationMillis },
                completion = AnalyticsCompletion(
                    completedTaskCount = currentCompleted,
                    allTaskCount = currentTasks.size,
                    share = currentShare,
                    comparison = compareNullableShares(currentShare, previousShare),
                ),
            )
        }

        private fun calculateCategories(
            currentTasks: List<AnalyticsTask>,
            comparisonTasks: List<AnalyticsTask>,
            totalDuration: Long,
            sort: AnalyticsCategorySort,
        ): AnalyticsCategoryDistribution {
            val previousDurations = comparisonTasks.groupBy { it.timeTask.category.id }
                .mapValues { (_, tasks) -> tasks.sumOf { it.safeDurationMillis } }
            val buckets = currentTasks.groupBy { it.timeTask.category.id }.map { (id, tasks) ->
                val duration = tasks.sumOf { it.safeDurationMillis }
                val previous = previousDurations[id] ?: 0L
                AnalyticsCategoryBucket(
                    category = tasks.first().timeTask.category,
                    durationMillis = duration,
                    taskCount = tasks.size,
                    share = share(duration, totalDuration),
                    previousDurationMillis = previous,
                    comparison = compareValues(duration, previous),
                )
            }
            val sortedBuckets = when (sort) {
                AnalyticsCategorySort.BY_TIME -> buckets.sortedWith(
                    compareByDescending<AnalyticsCategoryBucket> { it.durationMillis }
                        .thenBy { it.category?.id },
                )
                AnalyticsCategorySort.BY_TASKS -> buckets.sortedWith(
                    compareByDescending<AnalyticsCategoryBucket> { it.taskCount }
                        .thenBy { it.category?.id },
                )
            }
            val collapsedBucketCount = sortedBuckets.size.coerceAtMost(COLLAPSED_CATEGORY_COUNT)
            return AnalyticsCategoryDistribution(
                buckets = sortedBuckets,
                collapsedBucketCount = collapsedBucketCount,
                otherBucket = calculateOtherBucket(
                    buckets = sortedBuckets.drop(collapsedBucketCount),
                    totalDuration = totalDuration,
                ),
            )
        }

        private fun calculateOtherBucket(
            buckets: List<AnalyticsCategoryBucket>,
            totalDuration: Long,
        ): AnalyticsCategoryBucket? {
            val duration = buckets.sumOf { it.durationMillis }
            if (duration <= 0L) return null
            val previous = buckets.sumOf { it.previousDurationMillis }
            return AnalyticsCategoryBucket(
                category = null,
                durationMillis = duration,
                taskCount = buckets.sumOf { it.taskCount },
                share = share(duration, totalDuration),
                previousDurationMillis = previous,
                comparison = compareValues(duration, previous),
                isOther = true,
            )
        }

        private fun calculateLoad(
            tasks: List<AnalyticsTask>,
            range: AnalyticsCivilDateRange,
            locale: Locale,
            timeZone: TimeZone,
        ): AnalyticsLoadDistribution {
            val (granularity, sourceBuckets) = bucketCalculator.calculate(range, locale)
            val buckets = sourceBuckets.map { bucket ->
                val bucketTasks = tasks.filter { task ->
                    val sourceDate = localDateToToken(task.timeTask.date, timeZone)
                    !sourceDate.before(bucket.range.from) && !sourceDate.after(bucket.range.to)
                }
                AnalyticsLoadBucket(
                    range = bucket.range,
                    durationMillis = bucketTasks.sumOf { it.safeDurationMillis },
                    taskCount = bucketTasks.size,
                )
            }
            return AnalyticsLoadDistribution(granularity, buckets)
        }

        private fun calculateCreation(
            tasks: List<AnalyticsTask>,
            timeZone: TimeZone,
        ): AnalyticsCreationDistribution {
            val qualifying = tasks.filter { it.timeTask.linkedTemplateId == null && it.timeTask.createdAt != null }
            val totalDuration = qualifying.sumOf { it.safeDurationMillis }
            val grouped = qualifying.groupBy { task ->
                val createdAt = checkNotNull(task.timeTask.createdAt)
                val dayDifference = civilDayDifference(createdAt, task.timeTask.date, timeZone)
                when {
                    createdAt.after(task.timeTask.timeRange.from) -> AnalyticsCreationBucketType.AFTER_START
                    dayDifference >= 3 -> AnalyticsCreationBucketType.EARLY
                    dayDifference >= 1 -> AnalyticsCreationBucketType.ONE_OR_TWO_DAYS
                    else -> AnalyticsCreationBucketType.SAME_DAY
                }
            }
            val buckets = AnalyticsCreationBucketType.entries.map { type ->
                val bucketTasks = grouped[type].orEmpty()
                val duration = bucketTasks.sumOf { it.safeDurationMillis }
                AnalyticsCreationBucket(type, duration, bucketTasks.size, share(duration, totalDuration))
            }
            val leadTimes = qualifying.map { task ->
                task.timeTask.timeRange.from.time - checkNotNull(task.timeTask.createdAt).time
            }.sorted()
            return AnalyticsCreationDistribution(
                buckets = buckets,
                totalDurationMillis = totalDuration,
                medianLeadTimeMillis = median(leadTimes),
                qualifyingTaskCount = qualifying.size,
            )
        }

        private fun calculateDurations(
            tasks: List<AnalyticsTask>,
            comparisonTasks: List<AnalyticsTask>,
        ): AnalyticsDurationDistribution {
            val durations = tasks.map { it.safeDurationMillis }.filter { it > 0L }.sorted()
            val comparisonDurations = comparisonTasks.map { it.safeDurationMillis }.filter { it > 0L }.sorted()
            val buckets = AnalyticsDurationBucketType.entries.map { type ->
                val count = durations.count { duration ->
                    when (type) {
                        AnalyticsDurationBucketType.SHORT -> duration <= THIRTY_MINUTES
                        AnalyticsDurationBucketType.MEDIUM -> duration > THIRTY_MINUTES && duration <= TWO_HOURS
                        AnalyticsDurationBucketType.LONG -> duration > TWO_HOURS
                    }
                }
                AnalyticsDurationBucket(
                    type = type,
                    taskCount = count,
                    share = if (durations.isEmpty()) 0.0 else count.toDouble() / durations.size,
                )
            }
            val total = durations.sum()
            val average = durations.takeIf { it.isNotEmpty() }?.let { total / it.size }
            val comparisonAverage = comparisonDurations.takeIf { it.isNotEmpty() }?.let { it.sum() / it.size }
            val durationMedian = median(durations)
            val comparisonMedian = median(comparisonDurations)
            return AnalyticsDurationDistribution(
                buckets = buckets,
                averageDurationMillis = average,
                medianDurationMillis = durationMedian,
                totalDurationMillis = total,
                averageComparison = compareNullableValues(average, comparisonAverage),
                medianComparison = compareNullableValues(durationMedian, comparisonMedian),
            )
        }

        private fun calculatePlanSource(
            tasks: List<AnalyticsTask>,
            totalDuration: Long,
        ): AnalyticsPlanSourceDistribution {
            val grouped = tasks.groupBy { it.timeTask.linkedTemplateId != null }
            return AnalyticsPlanSourceDistribution(
                buckets = listOf(
                    AnalyticsPlanSourceType.LINKED to grouped[true].orEmpty(),
                    AnalyticsPlanSourceType.UNLINKED to grouped[false].orEmpty(),
                ).map { (type, sourceTasks) ->
                    val duration = sourceTasks.sumOf { it.safeDurationMillis }
                    AnalyticsPlanSourceBucket(type, duration, sourceTasks.size, share(duration, totalDuration))
                },
            )
        }

        private fun calculateKeyMetrics(
            tasks: List<AnalyticsTask>,
            totalDuration: Long,
            timeZone: TimeZone,
        ): AnalyticsKeyMetrics {
            val importantDuration = tasks.filter { it.timeTask.priority.isImportant() }.sumOf { it.safeDurationMillis }
            val slices = tasks.flatMap { intervalSplitter.splitByHour(it.timeTask.timeRange, timeZone) }
            val weekendDuration = slices.filter {
                it.dayOfWeek == Calendar.SATURDAY || it.dayOfWeek == Calendar.SUNDAY
            }.sumOf { it.durationMillis }
            val busiest = slices.groupBy { it.civilDate }.mapValues { (_, daySlices) -> daySlices.sumOf { it.durationMillis } }
                .entries.sortedWith(compareByDescending<Map.Entry<Date, Long>> { it.value }.thenBy { it.key }).firstOrNull()
            return AnalyticsKeyMetrics(
                importantDurationMillis = importantDuration,
                importantShare = share(importantDuration, totalDuration),
                weekendDurationMillis = weekendDuration,
                weekendShare = share(weekendDuration, totalDuration),
                longestBlock = calculateLongestBlock(tasks),
                busiestDay = busiest?.key,
                busiestDayDurationMillis = busiest?.value ?: 0L,
            )
        }

        private fun calculateLongestBlock(tasks: List<AnalyticsTask>): TimeRange? {
            val ranges = tasks.filter { it.safeDurationMillis > 0L }.map { it.timeTask.timeRange }
                .sortedWith(compareBy<TimeRange> { it.from }.thenBy { it.to })
            if (ranges.isEmpty()) return null
            val unions = mutableListOf<TimeRange>()
            var current = ranges.first()
            ranges.drop(1).forEach { range ->
                if (range.from.time <= current.to.time) {
                    if (range.to.after(current.to)) current = current.copy(to = range.to)
                } else {
                    unions.add(current)
                    current = range
                }
            }
            unions.add(current)
            return unions.sortedWith(
                compareByDescending<TimeRange> { it.to.time - it.from.time }.thenBy { it.from },
            ).first()
        }

        private fun calculateRegularity(
            currentTasks: List<AnalyticsTask>,
            comparisonTasks: List<AnalyticsTask>,
            currentRange: AnalyticsCivilDateRange,
            comparisonRange: AnalyticsCivilDateRange,
            timeZone: TimeZone,
        ): AnalyticsRegularity {
            val activeDates = currentTasks.map { localDateToToken(it.timeTask.date, timeZone) }.distinct().sorted()
            val comparisonActive = comparisonTasks.map { localDateToToken(it.timeTask.date, timeZone) }.distinct().size
            return AnalyticsRegularity(
                activeDates = activeDates,
                activeDayCount = activeDates.size,
                totalDayCount = inclusiveDays(currentRange),
                comparisonActiveDayCount = comparisonActive,
                activeDayDelta = activeDates.size - comparisonActive,
            )
        }

        private fun calculateWeekdayHourLoad(
            tasks: List<AnalyticsTask>,
            range: AnalyticsCivilDateRange,
            locale: Locale,
            timeZone: TimeZone,
        ): AnalyticsWeekdayHourLoad {
            val occurrences = (0 until inclusiveDays(range)).map { addCivilDays(range.from, it) }
                .groupingBy { civilCalendar(it).get(Calendar.DAY_OF_WEEK) }.eachCount()
            val slices = tasks.flatMap { intervalSplitter.splitByHour(it.timeTask.timeRange, timeZone) }
            val totals = slices.groupBy {
                it.dayOfWeek to it.hourOfDay / HOURS_PER_BUCKET * HOURS_PER_BUCKET
            }.mapValues { (_, bucketSlices) ->
                bucketSlices.sumOf { it.durationMillis }.toDouble() / MILLIS_IN_MINUTE
            }
            val averages = totals.mapValues { (key, total) ->
                total / maxOf(1, occurrences[key.first] ?: 0) / HOURS_PER_BUCKET
            }
            val maximum = averages.values.maxOrNull() ?: 0.0
            val firstDayOfWeek = Calendar.getInstance(locale).firstDayOfWeek
            val weekdays = List(WEEKDAYS_IN_WEEK) { index ->
                ((firstDayOfWeek - 1 + index) % WEEKDAYS_IN_WEEK) + 1
            }
            val rows = weekdays.map { day ->
                val cells = (0 until HOURS_IN_DAY step HOURS_PER_BUCKET).map { fromHour ->
                    val total = totals[day to fromHour] ?: 0.0
                    val average = averages[day to fromHour] ?: 0.0
                    AnalyticsWeekdayHourCell(
                        dayOfWeek = day,
                        fromHour = fromHour,
                        toHour = fromHour + HOURS_PER_BUCKET,
                        totalMinutes = total,
                        averageMinutes = average,
                        level = quantize(average, maximum),
                    )
                }
                val busiestCell = cells.withIndex().filter {
                    it.value.averageMinutes > 0.0
                }.sortedWith(
                    compareByDescending<IndexedValue<AnalyticsWeekdayHourCell>> { it.value.averageMinutes }
                        .thenBy { it.value.fromHour },
                ).firstOrNull()
                AnalyticsWeekdayHourRow(
                    dayOfWeek = day,
                    cells = cells,
                    busiestCellIndex = busiestCell?.index,
                )
            }
            return AnalyticsWeekdayHourLoad(rows = rows)
        }

        private fun compareNullableShares(current: Double?, previous: Double?): AnalyticsComparison {
            if (current == null || previous == null) return AnalyticsComparison(null, AnalyticsComparisonState.UNAVAILABLE)
            return compareDoubles(current, previous)
        }

        private fun compareNullableValues(current: Long?, previous: Long?): AnalyticsComparison {
            if (current == null || previous == null) return AnalyticsComparison(null, AnalyticsComparisonState.UNAVAILABLE)
            return compareValues(current, previous)
        }

        private fun compareValues(current: Long, previous: Long): AnalyticsComparison {
            return compareDoubles(current.toDouble(), previous.toDouble())
        }

        private fun compareDoubles(current: Double, previous: Double): AnalyticsComparison {
            return when {
                previous == 0.0 && current == 0.0 -> AnalyticsComparison(0.0, AnalyticsComparisonState.UNCHANGED)
                previous == 0.0 -> AnalyticsComparison(null, AnalyticsComparisonState.PREVIOUS_ZERO)
                current == previous -> AnalyticsComparison(0.0, AnalyticsComparisonState.UNCHANGED)
                else -> AnalyticsComparison((current - previous) / previous, AnalyticsComparisonState.VALUE)
            }
        }

        private fun share(value: Long, total: Long): Double {
            return if (total == 0L) 0.0 else value.toDouble() / total
        }

        private fun median(values: List<Long>): Long? {
            if (values.isEmpty()) return null
            val middle = values.size / 2
            if (values.size % 2 == 1) return values[middle]
            val left = values[middle - 1]
            val right = values[middle]
            return left / 2L + right / 2L + (left % 2L + right % 2L) / 2L
        }

        private fun civilDayDifference(from: Date, to: Date, timeZone: TimeZone): Int {
            val fromToken = localDateToToken(from, timeZone)
            val toToken = localDateToToken(to, timeZone)
            return ((toToken.time - fromToken.time) / MILLIS_IN_DAY).toInt()
        }

        private fun localDateToToken(date: Date, timeZone: TimeZone): Date {
            val local = Calendar.getInstance(timeZone).apply { time = date }
            return Calendar.getInstance(UTC).apply {
                clear()
                set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            }.time
        }

        private fun inclusiveDays(range: AnalyticsCivilDateRange): Int {
            return ((range.to.time - range.from.time) / MILLIS_IN_DAY).toInt() + 1
        }

        private fun addCivilDays(date: Date, days: Int): Date {
            return civilCalendar(date).apply { add(Calendar.DAY_OF_YEAR, days) }.time
        }

        private fun civilCalendar(date: Date): Calendar {
            return Calendar.getInstance(UTC).apply { time = date }
        }

        private fun quantize(value: Double, maximum: Double): Int {
            if (value <= 0.0 || maximum <= 0.0) return 0
            return kotlin.math.ceil(value / maximum * HEAT_LEVELS).toInt().coerceIn(1, HEAT_LEVELS)
        }

        companion object {
            private val UTC = TimeZone.getTimeZone("UTC")
            private const val COLLAPSED_CATEGORY_COUNT = 5
            private const val HOURS_IN_DAY = 24
            private const val HOURS_PER_BUCKET = 3
            private const val WEEKDAYS_IN_WEEK = 7
            private const val HEAT_LEVELS = 4
            private const val THIRTY_MINUTES = 30L * 60L * 1_000L
            private const val TWO_HOURS = 2L * 60L * 60L * 1_000L
            private const val MILLIS_IN_MINUTE = 60_000L
            private const val MILLIS_IN_DAY = 86_400_000L
        }
    }
}
