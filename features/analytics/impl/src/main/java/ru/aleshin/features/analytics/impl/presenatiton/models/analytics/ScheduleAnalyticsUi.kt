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
package ru.aleshin.features.analytics.impl.presenatiton.models.analytics

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.functional.TimeRange

/**
 * @author Stanislav Aleshin on 25.07.2023.
 */
@Immutable
@Serializable
internal data class ScheduleAnalyticsUi(
    val dateWorkLoadMap: WorkLoadMapUi,
    val hourlyWorkLoadAnalytics: List<HourlyWorkLoadAnalyticUi>,
    val categoriesAnalytics: CategoriesAnalyticsUi,
    val planningAnalytic: Map<Int, List<PlanningAnalyticUi>>,
    val totalTasksCount: Int,
    val totalTasksTime: Long,
    val averageDayLoad: Int,
    val averageTaskTime: Long,
)

internal typealias WorkLoadMapUi = Map<TimeRange, List<TimeTaskUi>>

/**
 * @author Stanislav Aleshin on 03.07.2026.
 */
@Immutable
@Serializable
internal data class HourlyWorkLoadAnalyticUi(
    val fromHour: Int,
    val toHour: Int,
    val duration: Long,
)
