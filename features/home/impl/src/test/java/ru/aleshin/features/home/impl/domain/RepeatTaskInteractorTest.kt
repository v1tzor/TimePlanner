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

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.DailyScheduleStatus
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.setHoursAndMinutes
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.extensions.shiftHours
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.extensions.toMinutes
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.WeekDay
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.managers.TimeOverlayManager
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.common.HomeErrorHandler
import ru.aleshin.features.home.impl.domain.interactors.RepeatTaskInteractor
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
internal class RepeatTaskInteractorTest {

    private lateinit var repeatTaskInteractor: RepeatTaskInteractor
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
        scheduleRepository = FakeScheduleRepository()
        dateManager = FakeDateManager(currentDate)
        repeatTaskInteractor = RepeatTaskInteractor.Base(
            timeTaskRepository = timeTaskRepository,
            scheduleRepository = scheduleRepository,
            eitherWrapper = HomeEitherWrapper.Base(HomeErrorHandler.Base()),
            overlayManager = TimeOverlayManager.Base(),
            dateManager = dateManager,
        )
    }

    @Test
    fun test_add_repeats_template_applies_to_current_day() = runBlocking {
        val currentDate = dateManager.fetchBeginningCurrentDay()
        val repeatTime = RepeatTime.WeekDays(WeekDay.TUESDAY)
        val template = Template(
            templateId = 1,
            startTime = Calendar.getInstance().setHoursAndMinutes(9, 0).time,
            endTime = Calendar.getInstance().setHoursAndMinutes(10, 0).time,
            category = MainCategory(id = 1),
            repeatEnabled = true,
            repeatTimes = listOf(repeatTime),
        )
        scheduleRepository.scheduleList.add(
            Schedule(
                date = currentDate.time,
                status = DailyScheduleStatus.ACCOMPLISHMENT,
            )
        )

        val actual = repeatTaskInteractor.addRepeatsTemplate(template, listOf(repeatTime))

        assertTrue(actual.isRight)
        assertEquals(1, timeTaskRepository.timeTasksList.size)
        assertEquals(currentDate.time, timeTaskRepository.timeTasksList.first().date.time)
        assertEquals(currentDate.shiftHours(9), timeTaskRepository.timeTasksList.first().timeRange.from)
        assertEquals(currentDate.shiftHours(10), timeTaskRepository.timeTasksList.first().timeRange.to)
    }
}

private class FakeTimeTaskRepository : TimeTaskRepository {

    val timeTasksList = mutableListOf<TimeTask>()

    override suspend fun addTimeTasks(timeTasks: List<TimeTask>) {
        timeTasksList.addAll(timeTasks)
    }

    override suspend fun fetchAllTimeTaskByDate(date: Date): List<TimeTask> {
        return timeTasksList
    }

    override suspend fun fetchTimeTaskByKey(key: Long): TimeTask? {
        return timeTasksList.find { timeTask -> timeTask.key == key }
    }

    override suspend fun updateTimeTaskList(timeTaskList: List<TimeTask>) {
        timeTaskList.forEach { timeTask ->
            val index = timeTasksList.indexOfFirst { it.key == timeTask.key }
            timeTasksList[index] = timeTask
        }
    }

    override suspend fun updateTimeTask(timeTask: TimeTask) {
        val index = timeTasksList.indexOfFirst { it.key == timeTask.key }
        timeTasksList[index] = timeTask
    }

    override suspend fun deleteTimeTasks(keys: List<Long>) {
        keys.forEach { key ->
            timeTasksList.removeAt(timeTasksList.indexOfFirst { it.key == key })
        }
    }
}

private class FakeScheduleRepository : ScheduleRepository {

    val scheduleList = mutableListOf<Schedule>()

    override suspend fun createSchedules(schedules: List<Schedule>) {
        scheduleList.addAll(schedules)
    }

    override suspend fun fetchSchedulesByRange(timeRange: TimeRange?): Flow<List<Schedule>> {
        return flowOf(scheduleList)
    }

    override fun fetchScheduleByDate(date: Long): Flow<Schedule?> {
        return flowOf(scheduleList.find { it.date == date })
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        val index = scheduleList.indexOfFirst { it.date == schedule.date }
        scheduleList[index] = schedule
    }

    override suspend fun deleteAllSchedules(): List<Schedule> {
        return scheduleList.apply { scheduleList.clear() }
    }
}

private class FakeDateManager(
    private val currentDate: Date,
) : DateManager {

    override fun fetchCurrentDate() = currentDate

    override fun fetchBeginningCurrentDay() = currentDate.startThisDay()

    override fun fetchEndCurrentDay() = currentDate.endThisDay()

    override fun calculateLeftTime(endTime: Date) = endTime.time - currentDate.time

    override fun calculateProgress(startTime: Date, endTime: Date): Float {
        val pastTime = (currentDate.time - startTime.time).toMinutes().toFloat()
        val duration = (endTime.time - startTime.time).toMinutes().toFloat()
        val progress = pastTime / duration
        return if (progress < 0f) 0f else if (progress > 1f) 1f else progress
    }

    override fun setCurrentHMS(date: Date): Date {
        val currentCalendar = Calendar.getInstance().apply { time = currentDate }
        val targetCalendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
            set(Calendar.SECOND, currentCalendar.get(Calendar.SECOND))
            set(Calendar.MILLISECOND, currentCalendar.get(Calendar.MILLISECOND))
        }
        return targetCalendar.time
    }
}
