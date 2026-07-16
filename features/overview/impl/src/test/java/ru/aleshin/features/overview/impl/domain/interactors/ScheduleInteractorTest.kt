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
package ru.aleshin.features.overview.impl.domain.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.common.TimeTaskStatusChecker
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.hoursToMillis
import ru.aleshin.core.utils.extensions.setHoursAndMinutes
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.firstRightOrNull
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper
import ru.aleshin.features.overview.impl.domain.common.OverviewErrorHandler
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
internal class ScheduleInteractorTest {

    private lateinit var currentDate: Date
    private lateinit var interactor: ScheduleInteractor

    @Before
    fun setUp() {
        currentDate = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, 16)
            setStartDay()
        }.time
        val dateManager = FakeDateManager(
            currentDate = currentDate.at(hour = 15),
        )
        interactor = ScheduleInteractor.Base(
            scheduleRepository = FakeScheduleRepository(
                schedules = listOf(
                    Schedule(
                        date = currentDate,
                        timeTasks = listOf(
                            task(key = 1L, fromHour = 12, toHour = 14, isCompleted = false),
                            task(key = 2L, fromHour = 9, toHour = 10),
                            task(key = 3L, fromHour = 18, toHour = 20),
                        ),
                        overlayTimeTasks = listOf(
                            task(key = 2L, fromHour = 9, toHour = 10),
                        ),
                    ),
                ),
            ),
            timeTaskRepository = FakeTimeTaskRepository(),
            templatesRepository = FakeTemplatesRepository(),
            scheduleStatusChecker = ScheduleStatusChecker.Base(dateManager),
            timeTaskStatusChecker = TimeTaskStatusChecker.Base(dateManager),
            dateManager = dateManager,
            overlayManager = TimeOverlayManager.Base(),
            eitherWrapper = OverviewEitherWrapper.Base(OverviewErrorHandler.Base()),
        )
    }

    @Test
    fun fetchWeekOverview_preparesSchedulesAndSummary() = runBlocking {
        val result = checkNotNull(interactor.fetchWeekOverview().firstRightOrNull())

        assertEquals(7, result.schedules.size)
        assertEquals(3, result.tasksCount)
        assertEquals(listOf(2L, 1L, 3L), result.schedules.first().timeTasks.map { task -> task.key })
        assertEquals(5.hoursToMillis(), result.schedules.first().summary.workload)
        assertEquals(19.hoursToMillis(), result.schedules.first().summary.freeTime)
        assertEquals(1f / 3f, result.schedules.first().summary.progress, 0.001f)
        assertEquals(24.hoursToMillis(), result.schedules.last().summary.freeTime)
        assertEquals(0L, result.schedules.last().summary.workload)
        assertEquals(0f, result.schedules.last().summary.progress)
    }

    private fun task(
        key: Long,
        fromHour: Int,
        toHour: Int,
        isCompleted: Boolean = true,
    ) = TimeTask(
        key = key,
        date = currentDate,
        timeRange = TimeRange(
            from = currentDate.at(fromHour),
            to = currentDate.at(toHour),
        ),
        category = MainCategory(id = key),
        isCompleted = isCompleted,
    )
}

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
private class FakeScheduleRepository(
    private val schedules: List<Schedule>,
) : ScheduleRepository {

    override suspend fun addOrUpdateSchedule(schedule: BaseDailySchedule): Long {
        return schedule.date.time
    }

    override suspend fun addOrUpdateSchedules(schedules: List<BaseDailySchedule>) = Unit

    override suspend fun fetchSchedulesByRange(timeRange: TimeRange?): Flow<List<Schedule>> {
        return flowOf(schedules)
    }

    override suspend fun fetchScheduleByDate(date: Date): Flow<Schedule?> {
        return flowOf(schedules.find { schedule -> schedule.date == date })
    }

    override suspend fun deleteAllSchedules(): List<Schedule> {
        return emptyList()
    }
}

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
private class FakeTimeTaskRepository : TimeTaskRepository {

    override suspend fun addOrUpdateTimeTask(timeTask: TimeTask): Long {
        return timeTask.key
    }

    override suspend fun addOrUpdateTimeTasks(timeTasks: List<TimeTask>) = Unit

    override suspend fun fetchAllTimeTasksByDate(date: Date): Flow<List<TimeTask>> {
        return flowOf(emptyList())
    }

    override suspend fun fetchTimeTaskById(id: Long): TimeTask? {
        return null
    }

    override suspend fun fetchTimeTaskByTemplate(templateId: Long, date: Date): TimeTask? {
        return null
    }

    override suspend fun deleteTimeTasksByIds(ids: List<Long>) = Unit
}

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
private class FakeTemplatesRepository : TemplatesRepository {

    override suspend fun addOrUpdateTemplate(template: Template): Long {
        return template.templateId.toLong()
    }

    override suspend fun addOrUpdateTemplates(templates: List<Template>) = Unit

    override suspend fun fetchTemplatesByIdOnce(templateId: Long): Template? {
        return null
    }

    override suspend fun fetchAllTemplates(): Flow<List<Template>> {
        return flowOf(emptyList())
    }

    override suspend fun deleteTemplateById(id: Long) = Unit

    override suspend fun deleteAllTemplates(): List<Template> {
        return emptyList()
    }
}

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
private class FakeDateManager(
    private val currentDate: Date,
) : DateManager {

    override fun fetchCurrentDate(): Date {
        return currentDate
    }

    override fun fetchBeginningCurrentDay(): Date {
        return currentDate.applyStartDay()
    }

    override fun fetchEndCurrentDay(): Date {
        return currentDate.applyStartDay().at(hour = 23, minute = 59)
    }

    override fun fetchTicker(): Flow<Date> {
        return flowOf(currentDate)
    }

    override fun calculateLeftTime(endTime: Date): Long {
        return endTime.time - currentDate.time
    }

    override fun calculateProgress(startTime: Date, endTime: Date): Float {
        return (currentDate.time - startTime.time).toFloat() / (endTime.time - startTime.time)
    }

    override fun setCurrentHMS(date: Date): Date {
        return date
    }
}

private fun Date.at(hour: Int, minute: Int = 0): Date {
    return Calendar.getInstance().apply {
        time = this@at
        setHoursAndMinutes(hour, minute)
    }.time
}

private fun Date.applyStartDay(): Date {
    return Calendar.getInstance().apply {
        time = this@applyStartDay
        setStartDay()
    }.time
}
