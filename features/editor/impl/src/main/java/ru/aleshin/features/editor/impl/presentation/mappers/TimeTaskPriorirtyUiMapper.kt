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

import ru.aleshin.core.domain.entities.tasks.TaskPriority
import ru.aleshin.features.editor.impl.presentation.models.tasks.TaskPriorityItemUi

/**
 * @author Stanislav Aleshin on 16.01.2024.
 */
internal fun TaskPriority.convertToItem() = when (this) {
    TaskPriority.STANDARD -> TaskPriorityItemUi.STANDARD
    TaskPriority.MEDIUM -> TaskPriorityItemUi.MEDIUM
    TaskPriority.MAX -> TaskPriorityItemUi.MAX
}

internal fun TaskPriorityItemUi.convertToModel() = when (this) {
    TaskPriorityItemUi.STANDARD -> TaskPriority.STANDARD
    TaskPriorityItemUi.MEDIUM -> TaskPriority.MEDIUM
    TaskPriorityItemUi.MAX -> TaskPriority.MAX
}