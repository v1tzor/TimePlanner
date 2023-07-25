/*
 * Copyright 2023 Stanislav Aleshin
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
 * imitations under the License.
 */
package ru.aleshin.features.analytics.impl.presenatiton.mappers

import ru.aleshin.features.analytics.impl.domain.entities.CategoryAnalytic
import ru.aleshin.features.analytics.impl.domain.entities.ScheduleAnalytics
import ru.aleshin.features.analytics.impl.domain.entities.SubCategoryAnalytic
import ru.aleshin.features.analytics.impl.presenatiton.models.CategoryAnalyticUi
import ru.aleshin.features.analytics.impl.presenatiton.models.ScheduleAnalyticsUi
import ru.aleshin.features.analytics.impl.presenatiton.models.SubCategoryAnalyticUi

/**
 * @author Stanislav Aleshin on 25.07.2023.
 */
internal fun ScheduleAnalytics.mapToUi() = ScheduleAnalyticsUi(
    dateWorkLoadMap = dateWorkLoadMap,
    categoriesAnalytics = categoriesAnalytics.map { it.mapToUi() },
    totalTasksCount = totalTasksCount,
    totalTasksTime = totalTasksTime,
    averageDayLoad = averageDayLoad,
    averageTaskTime = averageTaskTime,
)

internal fun CategoryAnalytic.mapToUi() = CategoryAnalyticUi(
    mainCategory = mainCategory,
    duration = duration,
    subCategoriesInfo = subCategoriesInfo.map { it.mapToUi() },
)

internal fun SubCategoryAnalytic.mapToUi() = SubCategoryAnalyticUi(
    subCategory = subCategory,
    duration = duration,
)
