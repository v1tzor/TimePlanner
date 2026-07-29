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
package ru.aleshin.features.editor.impl.presentation.mappers

import ru.aleshin.core.domain.entities.goals.Goal
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.presentation.mappers.mapToDomain
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalEditUi

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal fun Goal.mapToEditUi() = GoalEditUi(
    id = id,
    title = title,
    scopeType = scopeType,
    mainCategory = mainCategory?.mapToUi(),
    subCategory = subCategory?.mapToUi(),
    metric = metric,
    direction = direction,
    targetValue = when (metric) {
        GoalMetric.DURATION -> (targetValue / MILLIS_IN_MINUTE).toString()
        GoalMetric.TASK_COUNT -> targetValue.toString()
    },
    createdAt = createdAt,
    deadline = deadline,
)

internal fun GoalEditUi.mapToDomain() = Goal(
    id = id,
    title = title.trim(),
    scopeType = scopeType,
    mainCategory = mainCategory?.mapToDomain(),
    subCategory = subCategory?.mapToDomain(),
    metric = metric,
    direction = direction,
    targetValue = when (metric) {
        GoalMetric.DURATION -> checkNotNull(targetValue.toLongOrNull()) * MILLIS_IN_MINUTE
        GoalMetric.TASK_COUNT -> checkNotNull(targetValue.toLongOrNull())
    },
    createdAt = createdAt,
    deadline = deadline,
)

private const val MILLIS_IN_MINUTE = 60_000L
