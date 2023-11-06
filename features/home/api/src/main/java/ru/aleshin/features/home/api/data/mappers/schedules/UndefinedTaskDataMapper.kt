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
 * limitations under the License.
 */
package ru.aleshin.features.home.api.data.mappers.schedules

import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.features.home.api.data.mappers.categories.mapToDomain
import ru.aleshin.features.home.api.data.models.tasks.UndefinedTaskDetails
import ru.aleshin.features.home.api.data.models.tasks.UndefinedTaskEntity
import ru.aleshin.features.home.api.domain.entities.schedules.UndefinedTask

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
fun UndefinedTaskDetails.mapToDomain() = UndefinedTask(
    id = task.key,
    createdAt = task.createdAt?.mapToDate(),
    deadline = task.deadline?.mapToDate(),
    mainCategory = mainCategory.mapToDomain(),
    subCategory = subCategory?.mapToDomain(mainCategory.mapToDomain()),
    isImportant = task.isImportant,
    note = task.note,
)

fun UndefinedTask.mapToData() = UndefinedTaskEntity(
    key = id,
    createdAt = createdAt?.time,
    deadline = deadline?.time,
    mainCategoryId = mainCategory.id,
    subCategoryId = subCategory?.id,
    isImportant = isImportant,
    note = note,
)
