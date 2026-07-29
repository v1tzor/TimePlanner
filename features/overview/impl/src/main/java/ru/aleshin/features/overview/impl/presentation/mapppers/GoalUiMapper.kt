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
package ru.aleshin.features.overview.impl.presentation.mapppers

import ru.aleshin.core.domain.entities.goals.GoalDetails
import ru.aleshin.core.domain.entities.goals.GoalProgress
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.features.overview.impl.presentation.models.GoalDetailsUi
import ru.aleshin.core.presentation.mappers.mapToDomain
import ru.aleshin.features.overview.impl.presentation.models.GoalProgressUi
import ru.aleshin.features.overview.impl.presentation.models.GoalUi

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal fun ru.aleshin.core.domain.entities.goals.Goal.mapToUi() = GoalUi(
    id = id,
    title = title,
    scopeType = scopeType,
    mainCategory = mainCategory?.mapToUi(),
    subCategory = subCategory?.mapToUi(),
    metric = metric,
    direction = direction,
    targetValue = targetValue,
    createdAt = createdAt,
    deadline = deadline,
)

internal fun GoalUi.mapToDomain() = ru.aleshin.core.domain.entities.goals.Goal(
    id = id,
    title = title,
    scopeType = scopeType,
    mainCategory = mainCategory?.mapToDomain(),
    subCategory = subCategory?.mapToDomain(),
    metric = metric,
    direction = direction,
    targetValue = targetValue,
    createdAt = createdAt,
    deadline = deadline,
)

internal fun GoalProgress.mapToUi() = GoalProgressUi(
    goal = goal.mapToUi(),
    actualValue = actualValue,
    plannedValue = plannedValue,
    remainingValue = remainingValue,
    progressFraction = progressFraction,
    goalRange = goalRange,
    status = status,
)

internal fun GoalDetails.mapToUi() = GoalDetailsUi(
    progress = progress.mapToUi(),
    contributingTasks = contributingTasks.map { task -> task.mapToUi() },
)
