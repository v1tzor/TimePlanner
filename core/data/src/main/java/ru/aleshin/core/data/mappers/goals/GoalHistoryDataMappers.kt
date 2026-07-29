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
package ru.aleshin.core.data.mappers.goals

import ru.aleshin.core.data.models.goals.GoalHistoryEntity
import ru.aleshin.core.domain.entities.goals.GoalHistory
import ru.aleshin.core.utils.extensions.mapToDate

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
fun GoalHistoryEntity.mapToDomain() = GoalHistory(
    id = id,
    goalId = goalId,
    goalTitle = goalTitle,
    metric = metric,
    direction = direction,
    targetValue = targetValue,
    actualValue = actualValue,
    periodStart = periodStart.mapToDate(),
    periodEnd = periodEnd.mapToDate(),
    isAchieved = isAchieved,
    createdAt = createdAt.mapToDate(),
)

fun GoalHistory.mapToData() = GoalHistoryEntity(
    id = id,
    goalId = goalId,
    goalTitle = goalTitle,
    metric = metric,
    direction = direction,
    targetValue = targetValue,
    actualValue = actualValue,
    periodStart = periodStart.time,
    periodEnd = periodEnd.time,
    isAchieved = isAchieved,
    createdAt = createdAt.time,
)
