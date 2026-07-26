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
package ru.aleshin.features.analytics.impl.presentation.mappers

import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCategoryBucket
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCategoryDistribution
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCreationBucket
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCreationDistribution
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsDurationBucket
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsDurationDistribution
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsLoadBucket
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsLoadDistribution
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsOverview
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsPlanSourceBucket
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsPlanSourceDistribution
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsRangeSelection
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsSummary
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsWeekdayHourCell
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsWeekdayHourRow
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsCategoryBucketUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsCategoryDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsCreationBucketUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsCreationDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsDurationBucketUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsDurationDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsKeyMetricsUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsLoadBucketUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsLoadDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsOverviewUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsPlanSourceBucketUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsPlanSourceDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRegularityUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsSummaryUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsWeekdayHourCellUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsWeekdayHourLoadUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsWeekdayHourRowUi

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal fun AnalyticsRangeSelection.mapToUi() = AnalyticsRangeUi(
    period = period,
    anchorDate = anchorDate,
    from = civilRange.from,
    to = civilRange.to,
    comparisonFrom = comparisonCivilRange.from,
    comparisonTo = comparisonCivilRange.to,
    customFrom = customRange?.from,
    customTo = customRange?.to,
)

internal fun AnalyticsOverview.mapToUi() = AnalyticsOverviewUi(
    summary = summary.mapToUi(),
    categories = categories.mapToUi(),
    load = load.mapToUi(),
    creation = creation.mapToUi(),
    durations = durations.mapToUi(),
    planSource = planSource.mapToUi(),
    keyMetrics = AnalyticsKeyMetricsUi(
        importantDurationMillis = keyMetrics.importantDurationMillis,
        importantShare = keyMetrics.importantShare,
        weekendDurationMillis = keyMetrics.weekendDurationMillis,
        weekendShare = keyMetrics.weekendShare,
        longestBlock = keyMetrics.longestBlock,
        busiestDay = keyMetrics.busiestDay,
        busiestDayDurationMillis = keyMetrics.busiestDayDurationMillis,
    ),
    regularity = AnalyticsRegularityUi(
        activeDates = regularity.activeDates,
        activeDayCount = regularity.activeDayCount,
        totalDayCount = regularity.totalDayCount,
        activeDayDelta = regularity.activeDayDelta,
    ),
    weekdayHourLoad = AnalyticsWeekdayHourLoadUi(
        rows = weekdayHourLoad.rows.map(AnalyticsWeekdayHourRow::mapToUi),
    ),
)

private fun AnalyticsSummary.mapToUi() = AnalyticsSummaryUi(
    plannedDurationMillis = plannedDurationMillis,
    completedDurationMillis = completedDurationMillis,
    skippedDurationMillis = skippedDurationMillis,
    unfinishedDurationMillis = unfinishedDurationMillis,
    completedTaskCount = completion.completedTaskCount,
    allTaskCount = completion.allTaskCount,
    completionShare = completion.share,
    completionComparison = completion.comparison,
)

private fun AnalyticsCategoryDistribution.mapToUi() = AnalyticsCategoryDistributionUi(
    buckets = buckets.map(AnalyticsCategoryBucket::mapToUi),
    collapsedBucketCount = collapsedBucketCount,
    otherBucket = otherBucket?.mapToUi(),
)

private fun AnalyticsCategoryBucket.mapToUi() = AnalyticsCategoryBucketUi(
    category = category?.mapToUi(),
    durationMillis = durationMillis,
    taskCount = taskCount,
    share = share,
    comparison = comparison,
    isOther = isOther,
)

private fun AnalyticsLoadDistribution.mapToUi() = AnalyticsLoadDistributionUi(
    granularity = granularity,
    buckets = buckets.map(AnalyticsLoadBucket::mapToUi),
)

private fun AnalyticsLoadBucket.mapToUi() = AnalyticsLoadBucketUi(
    from = range.from,
    to = range.to,
    durationMillis = durationMillis,
    taskCount = taskCount,
)

private fun AnalyticsCreationDistribution.mapToUi() = AnalyticsCreationDistributionUi(
    buckets = buckets.map(AnalyticsCreationBucket::mapToUi),
    totalDurationMillis = totalDurationMillis,
    medianLeadTimeMillis = medianLeadTimeMillis,
    qualifyingTaskCount = qualifyingTaskCount,
)

private fun AnalyticsCreationBucket.mapToUi() = AnalyticsCreationBucketUi(
    type = type,
    durationMillis = durationMillis,
    taskCount = taskCount,
    share = share,
)

private fun AnalyticsDurationDistribution.mapToUi() = AnalyticsDurationDistributionUi(
    buckets = buckets.map(AnalyticsDurationBucket::mapToUi),
    averageDurationMillis = averageDurationMillis,
    medianDurationMillis = medianDurationMillis,
    averageComparison = averageComparison,
    medianComparison = medianComparison,
)

private fun AnalyticsDurationBucket.mapToUi() = AnalyticsDurationBucketUi(
    type = type,
    taskCount = taskCount,
    share = share,
)

private fun AnalyticsPlanSourceDistribution.mapToUi() = AnalyticsPlanSourceDistributionUi(
    buckets = buckets.map(AnalyticsPlanSourceBucket::mapToUi),
)

private fun AnalyticsPlanSourceBucket.mapToUi() = AnalyticsPlanSourceBucketUi(
    type = type,
    durationMillis = durationMillis,
    taskCount = taskCount,
    share = share,
)

private fun AnalyticsWeekdayHourCell.mapToUi() = AnalyticsWeekdayHourCellUi(
    dayOfWeek = dayOfWeek,
    fromHour = fromHour,
    toHour = toHour,
    averageMinutes = averageMinutes,
    level = level,
)

private fun AnalyticsWeekdayHourRow.mapToUi() = AnalyticsWeekdayHourRowUi(
    dayOfWeek = dayOfWeek,
    cells = cells.map(AnalyticsWeekdayHourCell::mapToUi),
    busiestCellIndex = busiestCellIndex,
)
