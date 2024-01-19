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
package ru.aleshin.core.domain.entities.schedules

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.extensions.extractAllItem
import ru.aleshin.core.utils.functional.Mapper
import java.util.Collections.emptyList

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Serializable
data class Schedule(
    val date: Long,
    val status: DailyScheduleStatus,
    val timeTasks: List<TimeTask> = emptyList(),
    val overlayTimeTasks: List<TimeTask> = emptyList(),
) {
    fun <T> map(mapper: Mapper<Schedule, T>) = mapper.map(this)
}

fun List<Schedule>.fetchAllTimeTasks() = map { it.overlayTimeTasks + it.timeTasks }.extractAllItem()
