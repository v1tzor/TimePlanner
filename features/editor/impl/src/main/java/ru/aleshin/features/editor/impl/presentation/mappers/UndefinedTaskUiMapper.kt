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
package ru.aleshin.features.editor.impl.presentation.mappers

import ru.aleshin.features.editor.impl.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.features.home.api.domain.entities.schedules.UndefinedTask

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
internal fun UndefinedTaskUi.mapToDomain() = UndefinedTask(
    id = id,
    createdAt = createdAt,
    deadline = deadline,
    mainCategory = mainCategory.mapToDomain(),
    subCategory = subCategory?.mapToDomain(),
    isImportant = isImportant,
    note = note,
)

internal fun UndefinedTask.mapToUi() = UndefinedTaskUi(
    id = id,
    createdAt = createdAt,
    deadline = deadline,
    mainCategory = mainCategory.mapToUi(),
    subCategory = subCategory?.mapToUi(),
    isImportant = isImportant,
    note = note,
)
