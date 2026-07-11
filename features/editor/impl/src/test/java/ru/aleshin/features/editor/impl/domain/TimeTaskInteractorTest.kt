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
package ru.aleshin.features.editor.impl.domain

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
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.setHoursAndMinutes
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.common.EditorErrorHandler
import ru.aleshin.features.editor.impl.domain.interactors.TimeTaskInteractor
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 07.07.2026.
 */
internal class TimeTaskInteractorTest {

    private lateinit var interactor: TimeTaskInteractor
    private lateinit var timeTaskRepository: FakeTimeTaskRepository
    private lateinit var scheduleRepository: FakeScheduleRepository
    private lateinit var currentDate: Date

    @Before
    fun setUp() {
        currentDate = Calendar.getInstance().apply {
            set(2026, Calendar.JUNE, 30)
            setStartDay()
        }.time
        timeTaskRepository = FakeTimeTaskRepository()
        scheduleRepository = FakeScheduleRepository(timeTaskRepository)
        interactor = TimeTaskInteractor.Base(
            scheduleRepository = scheduleRepository,
            timeTaskRepository = timeTaskRepository,
            overlayManager = TimeOverlayManager.Base(),
            eitherWrapper = EditorEitherWrapper.Base(EditorErrorHandler.Base()),
        )
    }

    @Test
    fun updateLinkedTaskDetachesItFromTemplate() = runBlocking {
        val task = task(key = 1L, linkedTemplateId = 12L)
        scheduleRepository.schedules.add(currentDate.time)
        timeTaskRepository.tasks.add(task)

        val result = interactor.updateTimeTask(task.copy(timeRange = TimeRange(currentDate.at(11, 0), currentDate.at(12, 0))))

        assertTrue(result is Either.Right)
        assertNull(timeTaskRepository.tasks.first().linkedTemplateId)
    }

    @Test
    fun deleteLinkedTaskRemovesOnlyTaskAndKeepsScheduleMarker() = runBlocking {
        val task = task(key = 1L, linkedTemplateId = 12L)
        scheduleRepository.schedules.add(currentDate.time)
        timeTaskRepository.tasks.add(task)

        val result = interactor.deleteTimeTaskById(task.key)

        assertTrue(result is Either.Right)
        assertEquals(emptyList<TimeTask>(), timeTaskRepository.tasks)
        assertTrue(scheduleRepository.schedules.contains(currentDate.time))
    }

    @Test
    fun addTimeTaskStoresTaskAsDetached() = runBlocking {
        scheduleRepository.schedules.add(currentDate.time)

        val result = interactor.addTimeTask(task(key = 0L, linkedTemplateId = 12L))

        assertTrue(result is Either.Right)
        assertEquals(1, timeTaskRepository.tasks.size)
        assertNull(timeTaskRepository.tasks.first().linkedTemplateId)
    }

    @Test
    fun addTimeTaskChecksOverlayAgainstNeighborSchedulesWhenTargetScheduleIsMissing() = runBlocking {
        val previousDate = currentDate.shiftDay(-1)
        val overnightTask = task(
            key = 1L,
            date = previousDate,
            timeRange = TimeRange(previousDate.at(23, 30), currentDate.at(1, 0)),
            linkedTemplateId = null,
        )
        val newTask = task(
            key = 0L,
            date = currentDate,
            timeRange = TimeRange(currentDate.at(0, 30), currentDate.at(0, 45)),
            linkedTemplateId = 12L,
        )
        scheduleRepository.schedules.add(previousDate.time)
        timeTaskRepository.tasks.add(overnightTask)

        val result = interactor.addTimeTask(newTask)

        assertTrue(result is Either.Left)
        assertEquals(listOf(overnightTask), timeTaskRepository.tasks)
        assertTrue(scheduleRepository.schedules.contains(currentDate.time))
    }

    private fun task(
        key: Long,
        linkedTemplateId: Long?,
        date: Date = currentDate,
        timeRange: TimeRange = TimeRange(currentDate.at(9, 0), currentDate.at(10, 0)),
    ) = TimeTask(
        key = key,
        date = date,
        timeRange = timeRange,
        category = MainCategory(id = 1L),
        linkedTemplateId = linkedTemplateId,
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
                        timeTasks = timeTaskRepository.tasks.filter { task -> task.date.startThisDay() == scheduleDate },
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
