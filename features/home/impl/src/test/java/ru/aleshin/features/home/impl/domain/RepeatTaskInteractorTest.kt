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
package ru.aleshin.features.home.impl.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.setHoursAndMinutes
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.WeekDay
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.common.HomeErrorHandler
import ru.aleshin.features.home.impl.domain.interactors.RepeatTaskInteractor
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 07.07.2026.
 */
internal class RepeatTaskInteractorTest {

    private lateinit var interactor: RepeatTaskInteractor
    private lateinit var timeTaskRepository: FakeTimeTaskRepository
    private lateinit var scheduleRepository: FakeScheduleRepository
    private lateinit var dateManager: FakeDateManager

    @Before
    fun setUp() {
        val currentDate = Calendar.getInstance().apply {
            set(2026, Calendar.JUNE, 30)
            setStartDay()
        }.time
        timeTaskRepository = FakeTimeTaskRepository()
        scheduleRepository = FakeScheduleRepository(timeTaskRepository)
        dateManager = FakeDateManager(currentDate)
        interactor = RepeatTaskInteractor.Base(
            timeTaskRepository = timeTaskRepository,
            scheduleRepository = scheduleRepository,
            eitherWrapper = HomeEitherWrapper.Base(HomeErrorHandler.Base()),
            overlayManager = TimeOverlayManager.Base(),
            dateManager = dateManager,
        )
    }

    @Test
    fun addRepeatsTemplateCreatesMissingFutureScheduleAndLinkedTask() = runBlocking {
        val repeatTime = RepeatTime.WeekDays(WeekDay.WEDNESDAY)
        val targetDate = dateManager.fetchBeginningCurrentDay().shiftDay(1)
        val template = template(repeatTime = repeatTime)

        val result = interactor.addRepeatsTemplate(template, listOf(repeatTime))

        assertTrue(result is Either.Right)
        assertTrue(scheduleRepository.schedules.contains(targetDate.time))
        val targetTask = timeTaskRepository.tasks.first { it.date == targetDate }
        assertEquals(template.templateId, targetTask.linkedTemplateId)
        assertEquals(targetDate, targetTask.date)
    }

    @Test
    fun addRepeatsTemplateDoesNotCreateCanonicalTaskWhenDetachedTaskOverlays() = runBlocking {
        val repeatTime = RepeatTime.WeekDays(WeekDay.WEDNESDAY)
        val targetDate = dateManager.fetchBeginningCurrentDay().shiftDay(1)
        val template = template(repeatTime = repeatTime)
        scheduleRepository.schedules.add(targetDate.time)
        timeTaskRepository.tasks.add(
            template.copy(repeatEnabled = false).let {
                TimeTask(
                    key = 42L,
                    date = targetDate,
                    timeRange = TimeRange(targetDate.at(9, 0), targetDate.at(10, 0)),
                    category = MainCategory(id = 1),
                    linkedTemplateId = null,
                )
            },
        )

        val result = interactor.addRepeatsTemplate(template, listOf(repeatTime))

        assertTrue(result is Either.Right)
        val targetTasks = timeTaskRepository.tasks.filter { it.date == targetDate }
        assertEquals(1, targetTasks.size)
        assertNull(targetTasks.first().linkedTemplateId)
    }

    @Test
    fun updateRepeatTemplateReplacesOnlyCanonicalFutureTasks() = runBlocking {
        val repeatTime = RepeatTime.WeekDays(WeekDay.WEDNESDAY)
        val targetDate = dateManager.fetchBeginningCurrentDay().shiftDay(1)
        val oldTemplate = template(repeatTime = repeatTime)
        val newTemplate = oldTemplate.copy(
            startTime = dateManager.fetchBeginningCurrentDay().at(11, 0),
            endTime = dateManager.fetchBeginningCurrentDay().at(12, 0),
        )
        scheduleRepository.schedules.add(targetDate.time)
        timeTaskRepository.tasks.add(
            TimeTask(
                key = 10L,
                date = targetDate,
                timeRange = TimeRange(targetDate.at(9, 0), targetDate.at(10, 0)),
                category = MainCategory(id = 1),
                linkedTemplateId = oldTemplate.templateId,
            ),
        )
        timeTaskRepository.tasks.add(
            TimeTask(
                key = 20L,
                date = targetDate.shiftDay(1),
                timeRange = TimeRange(targetDate.shiftDay(1).at(9, 0), targetDate.shiftDay(1).at(10, 0)),
                category = MainCategory(id = 1),
                linkedTemplateId = null,
            ),
        )

        val result = interactor.updateRepeatTemplate(oldTemplate, newTemplate)

        assertTrue(result is Either.Right)
        assertEquals(10L, timeTaskRepository.tasks.first { it.linkedTemplateId == oldTemplate.templateId }.key)
        assertEquals(targetDate.at(11, 0), timeTaskRepository.tasks.first { it.key == 10L }.timeRange.from)
        assertNull(timeTaskRepository.tasks.first { it.key == 20L }.linkedTemplateId)
    }

    private fun template(repeatTime: RepeatTime) = Template(
        templateId = 1L,
        startTime = dateManager.fetchBeginningCurrentDay().at(9, 0),
        endTime = dateManager.fetchBeginningCurrentDay().at(10, 0),
        category = MainCategory(id = 1L),
        repeatEnabled = true,
        repeatTimes = listOf(repeatTime),
    )
}

private class FakeTimeTaskRepository : TimeTaskRepository {

    val tasks = mutableListOf<TimeTask>()

    override suspend fun addOrUpdateTimeTask(timeTask: TimeTask): Long {
        tasks.removeAll { it.key == timeTask.key }
        tasks.add(timeTask)
        return timeTask.key
    }

    override suspend fun addOrUpdateTimeTasks(timeTasks: List<TimeTask>) {
        timeTasks.forEach { addOrUpdateTimeTask(it) }
    }

    override suspend fun fetchAllTimeTasksByDate(date: Date): Flow<List<TimeTask>> {
        return flowOf(tasks.filter { it.date.startThisDay() == date.startThisDay() })
    }

    override suspend fun fetchTimeTaskById(id: Long): TimeTask? {
        return tasks.find { it.key == id }
    }

    override suspend fun fetchTimeTaskByTemplate(templateId: Long, date: Date): TimeTask? {
        return tasks.find { task ->
            task.linkedTemplateId == templateId && task.date.startThisDay() == date.startThisDay()
        }
    }

    override suspend fun deleteTimeTasksByIds(ids: List<Long>) {
        tasks.removeAll { it.key in ids }
    }
}

private fun Date.at(hour: Int, minute: Int): Date {
    return Calendar.getInstance().apply {
        time = this@at
        setHoursAndMinutes(hour, minute)
    }.time
}

private class FakeScheduleRepository(
    private val timeTaskRepository: FakeTimeTaskRepository,
) : ScheduleRepository {

    val schedules = mutableSetOf<Long>()

    override suspend fun addOrUpdateSchedule(schedule: BaseDailySchedule): Long {
        schedules.add(schedule.date.time)
        return schedule.date.time
    }

    override suspend fun addOrUpdateSchedules(schedules: List<BaseDailySchedule>) {
        schedules.forEach { addOrUpdateSchedule(it) }
    }

    override suspend fun fetchSchedulesByRange(timeRange: TimeRange?): Flow<List<Schedule>> {
        val range = timeRange ?: TimeRange(Date(Long.MIN_VALUE), Date(Long.MAX_VALUE))
        return flowOf(
            schedules
                .filter { it in range.from.time..range.to.time }
                .map { date ->
                    val scheduleDate = Date(date)
                    Schedule(
                        date = scheduleDate,
                        timeTasks = timeTaskRepository.tasks.filter { it.date.startThisDay() == scheduleDate },
                    )
                },
        )
    }

    override suspend fun fetchScheduleByDate(date: Date): Flow<Schedule?> {
        return flowOf(
            schedules.find { it == date.time }?.let {
                Schedule(
                    date = date,
                    timeTasks = timeTaskRepository.tasks.filter { task -> task.date.startThisDay() == date.startThisDay() },
                )
            },
        )
    }

    override suspend fun deleteAllSchedules(): List<Schedule> {
        val deleted = schedules.map { Schedule(Date(it)) }
        schedules.clear()
        return deleted
    }
}

private class FakeDateManager(
    private val currentDate: Date,
) : DateManager {

    override fun fetchCurrentDate() = currentDate

    override fun fetchBeginningCurrentDay() = currentDate.startThisDay()

    override fun fetchEndCurrentDay() = currentDate.endThisDay()

    override fun fetchTicker(): Flow<Date> = flowOf(currentDate)

    override fun calculateLeftTime(endTime: Date) = endTime.time - currentDate.time

    override fun calculateProgress(startTime: Date, endTime: Date) = 0f

    override fun setCurrentHMS(date: Date) = date
}
