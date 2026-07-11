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
package ru.aleshin.core.presentation.models.tasks

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.tasks.TaskPriority
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 30.07.2023.
 */
@Immutable
@Serializable
data class TimeTaskUi(
    val key: Long = 0L,
    @Serializable(DateSerializer::class) val date: Date,
    @Serializable(DateSerializer::class) val createdAt: Date? = null,
    val timeRanges: TimeRange,
    val category: MainCategoryUi,
    val subCategory: SubCategoryUi? = null,
    val linkedTemplateId: Long? = null,
    val isCompleted: Boolean = true,
    val priority: TaskPriority = TaskPriority.STANDARD,
    val isEnableNotification: Boolean = true,
    val taskNotifications: TaskNotificationsUi = TaskNotificationsUi(),
    val isConsiderInStatistics: Boolean = true,
    val note: String? = null,
)
