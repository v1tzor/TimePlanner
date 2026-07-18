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
package ru.aleshin.features.home.impl.domain.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.BaseDailySchedule
import ru.aleshin.core.domain.entities.schedules.DailyScheduleStatus
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.schedules.ScheduleDetails
import ru.aleshin.core.domain.entities.tasks.TimeTask
import ru.aleshin.core.domain.entities.tasks.TimeTaskDetails
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.setHoursAndMinutes
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.common.HomeErrorHandler
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
internal class TimelineInteractorTest {

    private lateinit var interactor: TimelineInteractor
    private lateinit var timeTaskRepository: FakeTimelineTimeTaskRepository
    private lateinit var currentDate: Date

    @Before
    fun setUp() {
        currentDate = Calendar.getInstance().apply {
            set(2026, Calendar.JULY, 17)
            setStartDay()
        }.time
        timeTaskRepository = FakeTimelineTimeTaskRepository()
        interactor = TimelineInteractor.Base(
            scheduleRepository = FakeTimelineScheduleRepository(timeTaskRepository),
            timeTaskRepository = timeTaskRepository,
            overlayManager = TimeOverlayManager.Base(),
            dateManager = FakeTimelineDateManager(currentDate.at(13, 42)),
            eitherWrapper = HomeEitherWrapper.Base(HomeErrorHandler.Base()),
        )
    }

    @Test
    fun fetchTimelineSchedulePreparesSortedTasksAndFreeRanges() {
        val schedule = schedule(
            timeTasks = listOf(
                details(key = 2L, start = currentDate.at(9, 5), end = currentDate.at(9, 15)),
                details(key = 1L, start = currentDate.at(9, 0), end = currentDate.at(9, 5)),
            ),
        )

        val timeline = interactor.fetchTimelineSchedule(currentDate, schedule)

        assertEquals(listOf(1L, 2L), timeline.timeTasks.map { it.timeTask.key })
        assertEquals(currentDate.at(13, 42), timeline.initialTime)
        assertEquals(currentDate, timeline.timeTasks.first().minimumStartTime)
        assertEquals(currentDate.at(9, 5), timeline.timeTasks.first().maximumEndTime)
        assertEquals(
            listOf(
                TimeRange(currentDate, currentDate.at(9, 0)),
                TimeRange(currentDate.at(9, 15), currentDate.shiftDay(1)),
            ),
            timeline.freeTimeRanges,
        )
    }

    @Test
    fun fetchTimelineScheduleClipsOvernightTaskAndLimitsEditing() {
        val previousDate = currentDate.shiftDay(-1)
        val task = details(
            key = 1L,
            date = previousDate,
            start = previousDate.at(23, 30),
            end = currentDate.at(1, 0),
        )

        val timelineTask = interactor.fetchTimelineSchedule(
            date = currentDate,
            schedule = schedule(timeTasks = listOf(task)),
        ).timeTasks.single()

        assertEquals(TimeRange(currentDate, currentDate.at(1, 0)), timelineTask.visibleTimeRange)
        assertFalse(timelineTask.canMove)
        assertFalse(timelineTask.canResizeStart)
        assertTrue(timelineTask.canResizeEnd)
        assertEquals(previousDate, timelineTask.timeTask.date)
    }

    @Test
    fun updateTimeTaskDetachesTemplateAndPreservesOtherData() = runBlocking {
        val task = task(key = 1L, linkedTemplateId = 12L)
        timeTaskRepository.tasks.add(task)
        val updatedRange = TimeRange(currentDate.at(11, 0), currentDate.at(12, 0))

        val result = interactor.updateTimeTask(task.key, updatedRange)

        assertTrue(result is Either.Right)
        assertEquals(updatedRange, timeTaskRepository.tasks.single().timeRange)
        assertNull(timeTaskRepository.tasks.single().linkedTemplateId)
        assertEquals(task.category, timeTaskRepository.tasks.single().category)
    }

    @Test
    fun updateTimeTaskRejectsOverlap() = runBlocking {
        val task = task(key = 1L, linkedTemplateId = null)
        val occupiedTask = task(
            key = 2L,
            linkedTemplateId = null,
            timeRange = TimeRange(currentDate.at(11, 0), currentDate.at(12, 0)),
        )
        timeTaskRepository.tasks.addAll(listOf(task, occupiedTask))

        val result = interactor.updateTimeTask(
            timeTaskId = task.key,
            timeRange = TimeRange(currentDate.at(11, 30), currentDate.at(12, 30)),
        )

        assertTrue(result is Either.Left)
        assertEquals(task, timeTaskRepository.tasks.first { it.key == task.key })
    }

    @Test
    fun undoTimeTaskUpdateRestoresTemplateLink() = runBlocking {
        val task = task(key = 1L, linkedTemplateId = 12L)
        timeTaskRepository.tasks.add(task)
        val updateResult = interactor.updateTimeTask(
            timeTaskId = task.key,
            timeRange = TimeRange(currentDate.at(11, 0), currentDate.at(12, 0)),
        )
        val update = (updateResult as Either.Right).data

        val undoResult = interactor.undoTimeTaskUpdate(update)

        assertTrue(undoResult is Either.Right)
        assertEquals(task, timeTaskRepository.tasks.single())
    }

    private fun schedule(timeTasks: List<TimeTaskDetails>) = ScheduleDetails(
        date = currentDate,
        dateStatus = DailyScheduleStatus.ACCOMPLISHMENT,
        timeTasks = timeTasks,
        progress = 0f,
    )

    private fun details(
        key: Long,
        start: Date,
        end: Date,
        date: Date = currentDate,
    ) = TimeTaskDetails(
        key = key,
        executionStatus = TimeTaskStatus.PLANNED,
        date = date,
        startTime = start,
        endTime = end,
        duration = end.time - start.time,
        leftTime = -1L,
        progress = 0f,
        mainCategory = MainCategory(id = key),
    )

    private fun task(
        key: Long,
        linkedTemplateId: Long?,
        timeRange: TimeRange = TimeRange(currentDate.at(9, 0), currentDate.at(10, 0)),
    ) = TimeTask(
        key = key,
        date = currentDate,
        timeRange = timeRange,
        category = MainCategory(id = key),
        linkedTemplateId = linkedTemplateId,
        note = "Timeline",
    )
}

private class FakeTimelineTimeTaskRepository : TimeTaskRepository {

    val tasks = mutableListOf<TimeTask>()

    override suspend fun addOrUpdateTimeTask(timeTask: TimeTask): Long {
        tasks.removeAll { task -> task.key == timeTask.key }
        tasks.add(timeTask)
        return timeTask.key
    }

    override suspend fun addOrUpdateTimeTasks(timeTasks: List<TimeTask>) {
        timeTasks.forEach { timeTask -> addOrUpdateTimeTask(timeTask) }
    }

    override suspend fun fetchAllTimeTasksByDate(date: Date): Flow<List<TimeTask>> {
        return flowOf(tasks.filter { task -> task.date == date })
    }

    override suspend fun fetchTimeTaskById(id: Long): TimeTask? {
        return tasks.find { task -> task.key == id }
    }

    override suspend fun fetchTimeTaskByTemplate(templateId: Long, date: Date): TimeTask? {
        return tasks.find { task -> task.linkedTemplateId == templateId && task.date == date }
    }

    override suspend fun deleteTimeTasksByIds(ids: List<Long>) {
        tasks.removeAll { task -> task.key in ids }
    }
}

private class FakeTimelineScheduleRepository(
    private val timeTaskRepository: FakeTimelineTimeTaskRepository,
) : ScheduleRepository {

    override suspend fun addOrUpdateSchedule(schedule: BaseDailySchedule): Long {
        return schedule.date.time
    }

    override suspend fun addOrUpdateSchedules(schedules: List<BaseDailySchedule>) = Unit

    override suspend fun fetchSchedulesByRange(timeRange: TimeRange?): Flow<List<Schedule>> {
        val tasks = timeTaskRepository.tasks
        return flowOf(
            tasks.groupBy { task -> task.date }.map { (date, timeTasks) ->
                Schedule(date = date, timeTasks = timeTasks)
            },
        )
    }

    override suspend fun fetchScheduleByDate(date: Date): Flow<Schedule?> {
        return flowOf(null)
    }

    override suspend fun deleteAllSchedules(): List<Schedule> {
        return emptyList()
    }
}

private class FakeTimelineDateManager(
    private val currentDate: Date,
) : DateManager {

    override fun fetchCurrentDate() = currentDate
    override fun fetchBeginningCurrentDay() = currentDate.startDay()
    override fun fetchEndCurrentDay() = currentDate.startDay().shiftDay(1)
    override fun fetchTicker() = flowOf(currentDate)
    override fun fetchMinuteTicker() = flowOf(currentDate)
    override fun calculateLeftTime(endTime: Date) = endTime.time - currentDate.time
    override fun calculateProgress(startTime: Date, endTime: Date) = 0f
    override fun setCurrentHMS(date: Date) = date

    private fun Date.startDay(): Date {
        return Calendar.getInstance().apply {
            time = this@startDay
            setStartDay()
        }.time
    }
}

private fun Date.at(hour: Int, minute: Int): Date {
    return Calendar.getInstance().apply {
        time = this@at
        setHoursAndMinutes(hour, minute)
    }.time
}
