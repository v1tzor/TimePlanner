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
package ru.aleshin.features.home.impl.presentation.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
@Immutable
@Serializable
internal data class TimelineScheduleUi(
    @Serializable(DateSerializer::class) val date: Date,
    val dayTimeRange: TimeRange,
    @Serializable(DateSerializer::class) val initialTime: Date,
    val timeStep: Long,
    val minimumTaskDuration: Long,
    val timeTasks: List<TimelineTimeTaskUi>,
    val freeTimeRanges: List<TimeRange>,
)
