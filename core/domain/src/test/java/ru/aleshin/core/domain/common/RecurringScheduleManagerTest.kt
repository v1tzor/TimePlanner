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
package ru.aleshin.core.domain.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class RecurringScheduleManagerTest {

    @Test
    fun `creates one missing schedule and one linked repeated task`() = runBlocking {
        val targetDate = Date(1_800_000_000_000L).startThisDay()
        val scheduleRepository = RecordingScheduleRepository()
        val timeTaskRepository = RecordingTimeTaskRepository()
        val dayNumber = Calendar.getInstance().apply { time = targetDate }.get(Calendar.DAY_OF_MONTH)
        val template = Template(
            templateId = 42L,
            startTime = Date(targetDate.time + 9 * MILLIS_IN_HOUR),
            endTime = Date(targetDate.time + 10 * MILLIS_IN_HOUR),
            category = MainCategory(id = 1L),
            repeatEnabled = true,
            repeatTimes = listOf(RepeatTime.MonthDay(dayNumber)),
        )
        val manager = RecurringScheduleManager.Base(
            scheduleRepository = scheduleRepository,
            timeTaskRepository = timeTaskRepository,
            templatesRepository = FixedTemplatesRepository(template),
            overlayManager = TimeOverlayManager.Base(),
            dateManager = FixedDateManager(targetDate),
        )

        manager.createMissingSchedules(listOf(targetDate, targetDate))

        assertEquals(listOf(targetDate), scheduleRepository.createdSchedules.map { it.date })
        assertEquals(1, timeTaskRepository.createdTasks.size)
        assertEquals(42L, timeTaskRepository.createdTasks.single().linkedTemplateId)
        assertEquals(targetDate, timeTaskRepository.createdTasks.single().date)
    }
}

private class RecordingScheduleRepository : ScheduleRepository {

    val createdSchedules = mutableListOf<BaseDailySchedule>()

    override suspend fun addOrUpdateSchedule(schedule: BaseDailySchedule): Long {
        createdSchedules.add(schedule)
        return schedule.date.time
    }

    override suspend fun addOrUpdateSchedules(schedules: List<BaseDailySchedule>) {
        createdSchedules.addAll(schedules)
    }

    override suspend fun fetchSchedulesByRange(timeRange: TimeRange?): Flow<List<Schedule>> {
        return flowOf(emptyList())
    }

    override suspend fun fetchScheduleByDate(date: Date): Flow<Schedule?> = flowOf(null)
    override suspend fun deleteAllSchedules(): List<Schedule> = emptyList()
}

private class RecordingTimeTaskRepository : TimeTaskRepository {

    val createdTasks = mutableListOf<TimeTask>()

    override suspend fun addOrUpdateTimeTask(timeTask: TimeTask): Long {
        createdTasks.add(timeTask)
        return timeTask.key
    }

    override suspend fun addOrUpdateTimeTasks(timeTasks: List<TimeTask>) {
        createdTasks.addAll(timeTasks)
    }

    override suspend fun fetchAllTimeTasksByDate(date: Date): Flow<List<TimeTask>> = flowOf(emptyList())
    override suspend fun fetchTimeTasksByScheduleDateRange(timeRange: TimeRange): Flow<List<TimeTask>> {
        return flowOf(emptyList())
    }
    override suspend fun fetchTimeTaskById(id: Long): TimeTask? = null
    override suspend fun fetchTimeTaskByTemplate(templateId: Long, date: Date): TimeTask? = null
    override suspend fun deleteTimeTasksByIds(ids: List<Long>) = Unit
}

private class FixedTemplatesRepository(
    private val template: Template,
) : TemplatesRepository {
    override suspend fun addOrUpdateTemplate(template: Template): Long = template.templateId
    override suspend fun addOrUpdateTemplates(templates: List<Template>) = Unit
    override suspend fun fetchTemplatesByIdOnce(templateId: Long): Template? = template
    override suspend fun fetchAllTemplates(): Flow<List<Template>> = flowOf(listOf(template))
    override suspend fun deleteTemplateById(id: Long) = Unit
    override suspend fun deleteAllTemplates(): List<Template> = emptyList()
}

private class FixedDateManager(
    private val date: Date,
) : DateManager {
    override fun fetchCurrentDate(): Date = date
    override fun fetchBeginningCurrentDay(): Date = date.startThisDay()
    override fun fetchEndCurrentDay(): Date = Date(date.startThisDay().time + MILLIS_IN_DAY - 1)
    override fun fetchTicker(): Flow<Date> = flowOf(date)
    override fun fetchMinuteTicker(): Flow<Date> = flowOf(date)
    override fun calculateLeftTime(endTime: Date): Long = endTime.time - date.time
    override fun calculateProgress(startTime: Date, endTime: Date): Float = 0f
    override fun setCurrentHMS(date: Date): Date = date
}

private const val MILLIS_IN_HOUR = 3_600_000L
private const val MILLIS_IN_DAY = 86_400_000L
