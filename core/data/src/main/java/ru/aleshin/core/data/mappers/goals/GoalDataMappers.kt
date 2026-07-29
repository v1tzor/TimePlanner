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

import ru.aleshin.core.data.mappers.categories.mapToDomain
import ru.aleshin.core.data.models.goals.GoalDetailsEntity
import ru.aleshin.core.data.models.goals.GoalEntity
import ru.aleshin.core.domain.entities.goals.Goal
import ru.aleshin.core.utils.extensions.mapToDate

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
fun GoalDetailsEntity.mapToDomain() = Goal(
    id = goal.id,
    title = goal.title,
    scopeType = goal.scopeType,
    mainCategory = mainCategory?.mapToDomain(),
    subCategory = subCategory?.mapToDomain(),
    metric = goal.metric,
    direction = goal.direction,
    targetValue = goal.targetValue,
    createdAt = goal.createdAt.mapToDate(),
    deadline = goal.deadline.mapToDate(),
)

fun Goal.mapToData() = GoalEntity(
    id = id,
    title = title,
    scopeType = scopeType,
    mainCategoryId = mainCategory?.id,
    subCategoryId = subCategory?.id,
    metric = metric,
    direction = direction,
    targetValue = targetValue,
    createdAt = createdAt.time,
    deadline = deadline.time,
)
