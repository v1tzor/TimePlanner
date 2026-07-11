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
package ru.aleshin.core.domain.entities.schedules

import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.Mapper
import java.util.Date

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Serializable
data class BaseDailySchedule(
    @Serializable(DateSerializer::class) val date: Date,
) {
    fun <T> map(mapper: Mapper<BaseDailySchedule, T>) = mapper.map(this)
}

fun BaseDailySchedule.convertToDetails(
    timeTasks: List<TimeTask> = emptyList(),
    overlayTimeTasks: List<TimeTask> = emptyList(),
) = Schedule(
    date = date,
    timeTasks = timeTasks,
    overlayTimeTasks = overlayTimeTasks,
)