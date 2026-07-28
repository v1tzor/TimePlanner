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
package ru.aleshin.timeplanner.widgets.presentation.models

import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.domain.entities.tasks.TaskPriority
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Serializable
data class WidgetTaskUi(
    val id: Long,
    val date: Long,
    val startTime: Long,
    val endTime: Long,
    val title: String,
    val subtitle: String?,
    val categoryType: DefaultCategoryType?,
    val priority: TaskPriority,
    val status: TimeTaskStatus,
    val isCompleted: Boolean,
)
