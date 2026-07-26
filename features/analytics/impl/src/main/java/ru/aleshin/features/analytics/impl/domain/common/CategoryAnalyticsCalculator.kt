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

import ru.aleshin.core.domain.entities.categories.MainCategoryDetails
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparison
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparisonState
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCompletion
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTask
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTaskStatus
import ru.aleshin.features.analytics.impl.domain.entities.CategoryAnalytics
import ru.aleshin.features.analytics.impl.domain.entities.CategoryDayPart
import ru.aleshin.features.analytics.impl.domain.entities.CategoryDayPartCell
import ru.aleshin.features.analytics.impl.domain.entities.CategoryDayPartSummary
import ru.aleshin.features.analytics.impl.domain.entities.CategoryKeyMetrics
import ru.aleshin.features.analytics.impl.domain.entities.CategoryLoadBucket
import ru.aleshin.features.analytics.impl.domain.entities.CategoryLoadDistribution
import ru.aleshin.features.analytics.impl.domain.entities.CategoryObservation
import ru.aleshin.features.analytics.impl.domain.entities.CategoryObservationType
import ru.aleshin.features.analytics.impl.domain.entities.CategorySummary
import ru.aleshin.features.analytics.impl.domain.entities.CategoryTaskRow
import ru.aleshin.features.analytics.impl.domain.entities.SubCategoryBucket
import ru.aleshin.features.analytics.impl.domain.entities.SubCategoryDistribution
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface CategoryAnalyticsCalculator {

    fun calculate(
        category: MainCategoryDetails?,
        currentPlanTasks: List<AnalyticsTask>,
        comparisonPlanTasks: List<AnalyticsTask>,
        currentCivilRange: AnalyticsCivilDateRange,
        locale: Locale,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): CategoryAnalytics

    class Base @Inject constructor(
        private val bucketCalculator: AnalyticsBucketCalculator,
        private val intervalSplitter: AnalyticsIntervalSplitter,
    ) : CategoryAnalyticsCalculator {

        override fun calculate(
            category: MainCategoryDetails?,
            currentPlanTasks: List<AnalyticsTask>,
            comparisonPlanTasks: List<AnalyticsTask>,
            currentCivilRange: AnalyticsCivilDateRange,
            locale: Locale,
            timeZone: TimeZone,
        ): CategoryAnalytics {
            if (category == null) {
                return CategoryAnalytics(
                    category = null,
                    summary = null,
                    keyMetrics = null,
                    subCategories = null,
                    load = null,
                    dayParts = emptyList(),
                    taskRows = emptyList(),
                    observation = null,
                )
            }
            val currentTasks = currentPlanTasks.filter { it.timeTask.category.id == category.category.id }
            val comparisonTasks = comparisonPlanTasks.filter { it.timeTask.category.id == category.category.id }
            if (currentTasks.isEmpty()) {
                return CategoryAnalytics(
                    category = category.category,
                    summary = null,
                    keyMetrics = null,
                    subCategories = null,
                    load = null,
                    dayParts = emptyList(),
                    taskRows = emptyList(),
                    observation = null,
                )
            }
            val currentDuration = currentTasks.sumOf { it.safeDurationMillis }
            val comparisonDuration = comparisonTasks.sumOf { it.safeDurationMillis }
            val allPlanDuration = currentPlanTasks.sumOf { it.safeDurationMillis }
            val completion = calculateCompletion(currentTasks, comparisonTasks)
            val subCategories = calculateSubCategories(currentTasks, currentDuration)
            val load = calculateLoad(currentTasks, currentPlanTasks, currentCivilRange, locale, timeZone)
            val dayParts = calculateDayParts(currentTasks, currentCivilRange, timeZone)
            val rows = currentTasks.sortedWith(
                compareBy<AnalyticsTask> { it.timeTask.timeRange.from }.thenBy { it.timeTask.key },
            ).map { CategoryTaskRow(it.timeTask, it.status, it.safeDurationMillis) }
            return CategoryAnalytics(
                category = category.category,
                summary = CategorySummary(
                    durationMillis = currentDuration,
                    allPlanDurationMillis = allPlanDuration,
                    share = share(currentDuration, allPlanDuration),
                    comparison = compareValues(currentDuration, comparisonDuration),
                ),
                keyMetrics = calculateKeyMetrics(currentTasks, comparisonTasks, completion),
                subCategories = subCategories,
                load = load,
                dayParts = dayParts,
                taskRows = rows,
                observation = calculateObservation(completion, load, subCategories, currentDuration),
                dayPartSummaries = calculateDayPartSummaries(dayParts),
            )
        }

        private fun calculateCompletion(
            currentTasks: List<AnalyticsTask>,
            comparisonTasks: List<AnalyticsTask>,
        ): AnalyticsCompletion {
            val completed = currentTasks.count { it.status == AnalyticsTaskStatus.COMPLETED }
            val comparisonCompleted = comparisonTasks.count { it.status == AnalyticsTaskStatus.COMPLETED }
            val share = completed.toDouble() / currentTasks.size
            val comparisonShare = comparisonTasks.size.takeIf { it > 0 }?.let { comparisonCompleted.toDouble() / it }
            return AnalyticsCompletion(
                completedTaskCount = completed,
                allTaskCount = currentTasks.size,
                share = share,
                comparison = compareNullableShares(share, comparisonShare),
            )
        }

        private fun calculateKeyMetrics(
            tasks: List<AnalyticsTask>,
            comparisonTasks: List<AnalyticsTask>,
            completion: AnalyticsCompletion,
        ): CategoryKeyMetrics {
            val durations = tasks.map { it.safeDurationMillis }.filter { it > 0L }
            val comparisonDurations = comparisonTasks.map { it.safeDurationMillis }.filter { it > 0L }
            val average = durations.takeIf { it.isNotEmpty() }?.let { it.sum() / it.size }
            val comparisonAverage = comparisonDurations.takeIf { it.isNotEmpty() }?.let { it.sum() / it.size }
            return CategoryKeyMetrics(
                taskCount = tasks.size,
                averageDurationMillis = average,
                completion = completion,
                taskCountDelta = tasks.size - comparisonTasks.size,
                averageDurationDeltaMillis = if (average != null && comparisonAverage != null) average - comparisonAverage else null,
                completedCountDelta = completion.completedTaskCount - comparisonTasks.count {
                    it.status == AnalyticsTaskStatus.COMPLETED
                },
            )
        }

        private fun calculateSubCategories(
            tasks: List<AnalyticsTask>,
            totalDuration: Long,
        ): SubCategoryDistribution {
            val buckets = tasks.groupBy { it.timeTask.subCategory?.id }.map { (_, bucketTasks) ->
                val duration = bucketTasks.sumOf { it.safeDurationMillis }
                SubCategoryBucket(
                    subCategory = bucketTasks.first().timeTask.subCategory,
                    durationMillis = duration,
                    taskCount = bucketTasks.size,
                    share = share(duration, totalDuration),
                )
            }.sortedWith(
                compareByDescending<SubCategoryBucket> { it.durationMillis }
                    .thenBy { it.subCategory?.id ?: UNASSIGNED_TIE_KEY },
            )
            if (buckets.size <= MAX_SUBCATEGORY_ROWS) {
                return SubCategoryDistribution(buckets, buckets.size == 1 && buckets.single().subCategory == null)
            }
            val visible = buckets.take(MAX_SUBCATEGORY_ROWS)
            val remaining = buckets.drop(MAX_SUBCATEGORY_ROWS)
            val otherDuration = remaining.sumOf { it.durationMillis }
            return SubCategoryDistribution(
                buckets = visible + SubCategoryBucket(
                    subCategory = null,
                    durationMillis = otherDuration,
                    taskCount = remaining.sumOf { it.taskCount },
                    share = share(otherDuration, totalDuration),
                    isOther = true,
                ),
                isSingleUnassigned = false,
            )
        }

        private fun calculateLoad(
            categoryTasks: List<AnalyticsTask>,
            allTasks: List<AnalyticsTask>,
            range: AnalyticsCivilDateRange,
            locale: Locale,
            timeZone: TimeZone,
        ): CategoryLoadDistribution {
            val (granularity, sourceBuckets) = bucketCalculator.calculate(range, locale)
            val buckets = sourceBuckets.map { bucket ->
                val categoryDuration = categoryTasks.filter { isInBucket(it, bucket.range, timeZone) }.sumOf { it.safeDurationMillis }
                val allDuration = allTasks.filter { isInBucket(it, bucket.range, timeZone) }.sumOf { it.safeDurationMillis }
                CategoryLoadBucket(
                    range = bucket.range,
                    categoryDurationMillis = categoryDuration,
                    allPlanDurationMillis = allDuration,
                )
            }
            val actualDays = categoryTasks.flatMap { intervalSplitter.splitByHour(it.timeTask.timeRange, timeZone) }
                .groupBy { it.civilDate }.mapValues { (_, slices) -> slices.sumOf { it.durationMillis } }
                .entries.sortedWith(compareByDescending<Map.Entry<Date, Long>> { it.value }.thenBy { it.key })
            return CategoryLoadDistribution(
                granularity = granularity,
                buckets = buckets,
                busiestDay = actualDays.firstOrNull()?.key,
                busiestDayDurationMillis = actualDays.firstOrNull()?.value ?: 0L,
            )
        }

        private fun calculateDayParts(
            tasks: List<AnalyticsTask>,
            range: AnalyticsCivilDateRange,
            timeZone: TimeZone,
        ): List<CategoryDayPartCell> {
            val occurrences = (0 until inclusiveDays(range)).map { addCivilDays(range.from, it) }
                .groupingBy { civilCalendar(it).get(Calendar.DAY_OF_WEEK) }.eachCount()
            val slices = tasks.flatMap { intervalSplitter.splitByHour(it.timeTask.timeRange, timeZone) }
            val totals = slices.groupBy { slice -> slice.dayOfWeek to slice.hourOfDay.fetchDayPart() }
                .mapValues { (_, partSlices) -> partSlices.sumOf { it.durationMillis }.toDouble() / MILLIS_IN_MINUTE }
            val averages = totals.mapValues { (key, value) ->
                value / maxOf(1, occurrences[key.first] ?: 0) / HOURS_PER_DAY_PART
            }
            val maximum = averages.values.maxOrNull() ?: 0.0
            return CategoryDayPart.entries.flatMap { part ->
                (Calendar.SUNDAY..Calendar.SATURDAY).map { day ->
                    val total = totals[day to part] ?: 0.0
                    val average = averages[day to part] ?: 0.0
                    CategoryDayPartCell(day, part, total, average, quantize(average, maximum))
                }
            }
        }

        private fun calculateDayPartSummaries(cells: List<CategoryDayPartCell>): List<CategoryDayPartSummary> {
            return CategoryDayPart.entries.mapNotNull { dayPart ->
                val busiest = cells.filter {
                    it.dayPart == dayPart && it.averageMinutes > 0.0
                }.sortedWith(
                    compareByDescending<CategoryDayPartCell> { it.averageMinutes }.thenBy { it.dayOfWeek },
                ).firstOrNull()
                busiest?.let {
                    CategoryDayPartSummary(
                        dayPart = dayPart,
                        busiestDayOfWeek = busiest.dayOfWeek,
                        busiestAverageMinutes = busiest.averageMinutes,
                    )
                }
            }
        }

        private fun calculateObservation(
            completion: AnalyticsCompletion,
            load: CategoryLoadDistribution,
            subCategories: SubCategoryDistribution,
            totalDuration: Long,
        ): CategoryObservation? {
            val completionChange = completion.comparison.changePercent
            if (completion.comparison.state == AnalyticsComparisonState.VALUE && completionChange != null && completionChange <= -0.10) {
                return CategoryObservation(CategoryObservationType.COMPLETION_DROP, -completionChange)
            }
            val busiestShare = share(load.busiestDayDurationMillis, totalDuration)
            if (load.busiestDay != null && busiestShare >= BUSIEST_DAY_THRESHOLD) {
                return CategoryObservation(
                    type = CategoryObservationType.BUSIEST_DAY,
                    valuePercent = busiestShare,
                    day = load.busiestDay,
                )
            }
            val dominant = subCategories.buckets.firstOrNull {
                !it.isOther && it.share >= DOMINANT_SUBCATEGORY_THRESHOLD
            }
            return dominant?.let {
                CategoryObservation(
                    type = CategoryObservationType.DOMINANT_SUBCATEGORY,
                    valuePercent = it.share,
                    subCategory = it.subCategory,
                )
            }
        }

        private fun isInBucket(
            task: AnalyticsTask,
            range: AnalyticsCivilDateRange,
            timeZone: TimeZone,
        ): Boolean {
            val sourceDate = localDateToToken(task.timeTask.date, timeZone)
            return !sourceDate.before(range.from) && !sourceDate.after(range.to)
        }

        private fun compareNullableShares(current: Double, previous: Double?): AnalyticsComparison {
            if (previous == null) return AnalyticsComparison(null, AnalyticsComparisonState.UNAVAILABLE)
            return compareDoubles(current, previous)
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

        private fun Int.fetchDayPart(): CategoryDayPart = when (this) {
            in 6..11 -> CategoryDayPart.MORNING
            in 12..17 -> CategoryDayPart.DAY
            in 18..23 -> CategoryDayPart.EVENING
            else -> CategoryDayPart.NIGHT
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
            private const val MAX_SUBCATEGORY_ROWS = 4
            private const val UNASSIGNED_TIE_KEY = Long.MAX_VALUE
            private const val HEAT_LEVELS = 4
            private const val HOURS_PER_DAY_PART = 6
            private const val BUSIEST_DAY_THRESHOLD = 0.4
            private const val DOMINANT_SUBCATEGORY_THRESHOLD = 0.6
            private const val MILLIS_IN_MINUTE = 60_000L
            private const val MILLIS_IN_DAY = 86_400_000L
        }
    }
}
