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
import ru.aleshin.features.analytics.impl.domain.entities.CategoryAnalytics
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryAnalyticsUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryDayPartCellUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryDayPartSummaryUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryKeyMetricsUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryLoadBucketUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryLoadDistributionUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryObservationUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategorySummaryUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryTaskRowUi
import ru.aleshin.features.analytics.impl.presentation.models.category.SubCategoryBucketUi
import ru.aleshin.features.analytics.impl.presentation.models.category.SubCategoryDistributionUi

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal fun CategoryAnalytics.mapToUi() = CategoryAnalyticsUi(
    category = category?.mapToUi(),
    summary = summary?.let {
        CategorySummaryUi(
            durationMillis = it.durationMillis,
            allPlanDurationMillis = it.allPlanDurationMillis,
            share = it.share,
            comparison = it.comparison,
        )
    },
    keyMetrics = keyMetrics?.let {
        CategoryKeyMetricsUi(
            taskCount = it.taskCount,
            averageDurationMillis = it.averageDurationMillis,
            completedTaskCount = it.completion.completedTaskCount,
            allTaskCount = it.completion.allTaskCount,
            completionShare = it.completion.share,
            completionComparison = it.completion.comparison,
            taskCountDelta = it.taskCountDelta,
            averageDurationDeltaMillis = it.averageDurationDeltaMillis,
            completedCountDelta = it.completedCountDelta,
        )
    },
    subCategories = subCategories?.let { distribution ->
        SubCategoryDistributionUi(
            buckets = distribution.buckets.map { bucket ->
                SubCategoryBucketUi(
                    subCategory = bucket.subCategory?.mapToUi(),
                    durationMillis = bucket.durationMillis,
                    taskCount = bucket.taskCount,
                    share = bucket.share,
                    isOther = bucket.isOther,
                )
            },
            isSingleUnassigned = distribution.isSingleUnassigned,
        )
    },
    load = load?.let { distribution ->
        CategoryLoadDistributionUi(
            granularity = distribution.granularity,
            buckets = distribution.buckets.map {
                CategoryLoadBucketUi(
                    from = it.range.from,
                    to = it.range.to,
                    categoryDurationMillis = it.categoryDurationMillis,
                    allPlanDurationMillis = it.allPlanDurationMillis,
                )
            },
            busiestDay = distribution.busiestDay,
            busiestDayDurationMillis = distribution.busiestDayDurationMillis,
        )
    },
    dayParts = dayParts.map {
        CategoryDayPartCellUi(
            dayOfWeek = it.dayOfWeek,
            dayPart = it.dayPart,
            averageMinutes = it.averageMinutes,
            level = it.level,
        )
    },
    dayPartSummaries = dayPartSummaries.map {
        CategoryDayPartSummaryUi(
            dayPart = it.dayPart,
            busiestDayOfWeek = it.busiestDayOfWeek,
            busiestAverageMinutes = it.busiestAverageMinutes,
        )
    },
    taskRows = taskRows.map {
        CategoryTaskRowUi(
            task = it.task.mapToUi(),
            status = it.status,
            safeDurationMillis = it.safeDurationMillis,
        )
    },
    observation = observation?.let {
        CategoryObservationUi(
            type = it.type,
            valuePercent = it.valuePercent,
            day = it.day,
            subCategory = it.subCategory?.mapToUi(),
        )
    },
)
