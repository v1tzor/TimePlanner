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
package ru.aleshin.features.overview.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.features.overview.impl.R

/**
 * @author Stanislav Aleshin on 22.02.2023.
 */
internal data class OverviewIcons(
    val category: Int,
    val subCategory: Int,
    val priority: Int,
    val duration: Int,
    val notes: Int,
    val schedule: Int,
    val completedTask: Int,
    val unexecutedTask: Int,
)

internal val baseOverviewIcons = OverviewIcons(
    category = R.drawable.ic_category,
    subCategory = R.drawable.ic_subcategory,
    priority = R.drawable.ic_priority_high,
    duration = R.drawable.ic_timer,
    notes = R.drawable.ic_notes,
    schedule = R.drawable.ic_schedule,
    completedTask = R.drawable.ic_complete_task,
    unexecutedTask = R.drawable.ic_not_complete_task,
)

internal val LocalOverviewIcons = staticCompositionLocalOf<OverviewIcons> {
    error("Overview icons are not provided")
}

internal fun fetchOverviewIcons() = baseOverviewIcons
