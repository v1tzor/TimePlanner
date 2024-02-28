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
package ru.aleshin.features.editor.impl.domain

import android.database.sqlite.SQLiteException
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.DailyScheduleStatus
import ru.aleshin.core.domain.entities.schedules.Schedule
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.extensions.shiftMinutes
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.extensions.toMinutes
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.managers.TimeOverlayManager
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.common.EditorErrorHandler
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.interactors.TimeTaskInteractor
import java.lang.NullPointerException
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 27.05.2023.
 */
internal class TimeTaskInteractorTest {

    private lateinit var timeTaskInteractor: TimeTaskInteractor
    private lateinit var timeTaskRepository: FakeTimeTaskRepository
    private lateinit var scheduleRepository: FakeScheduleRepository
    private lateinit var overlayManager: TimeOverlayManager
    private lateinit var dateManager: FakeDateManager
    private lateinit var eitherWrapper: EditorEitherWrapper
    private lateinit var errorHandler: EditorErrorHandler
    private lateinit var statusChecker: ScheduleStatusChecker

    @Before
    fun setUp() {
        timeTaskRepository = FakeTimeTaskRepository()
        scheduleRepository = FakeScheduleRepository()
        errorHandler = EditorErrorHandler.Base()
        eitherWrapper = EditorEitherWrapper.Base(errorHandler = errorHandler)
        overlayManager = TimeOverlayManager.Base()
        statusChecker = ScheduleStatusChecker.Base()
        dateManager = FakeDateManager()

        timeTaskInteractor = TimeTaskInteractor.Base(
            scheduleRepository = scheduleRepository,
            timeTaskRepository = timeTaskRepository,
            eitherWrapper = eitherWrapper,
            statusChecker = statusChecker,
            dateManager = dateManager,
            overlayManager = overlayManager,
        )
    }

    @Test
    fun test_add_first_time_task() = runBlocking {
        timeTaskRepository.timeTasksList.clear()

        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time

        val fakeTask = TimeTask(
            key = 0L,
            date = fakeTime,
            timeRange = TimeRange(fakeTime.startThisDay(), fakeTime.endThisDay()),
            category = MainCategory(),
        )

        val result = timeTaskInteractor.addTimeTask(fakeTask)

        assertEquals(true, result.isRight)

        assertEquals(1, scheduleRepository.fetchSchedulesCount)
        assertEquals(1, timeTaskRepository.addedTaskCount)
        assertEquals(1, scheduleRepository.createdScheduleCount)

        assertEquals(1, timeTaskRepository.timeTasksList.size)
        // Interactor replace key on new unique value
        assertEquals(
            fakeTask.copy(key = timeTaskRepository.timeTasksList[0].key),
            timeTaskRepository.timeTasksList[0],
        )
        assertEquals(
            fakeTime.time,
            scheduleRepository.scheduleList[0].date,
        )
    }

    @Test
    fun test_add_first_time_task_with_error() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time

        timeTaskRepository.errorWhileAction = true
        timeTaskRepository.timeTasksList.clear()
        scheduleRepository.scheduleList.add(
            Schedule(
                date = fakeTime.time,
                status = DailyScheduleStatus.ACCOMPLISHMENT,
            )
        )

        val fakeTask = TimeTask(
            key = 0L,
            date = fakeTime,
            timeRange = TimeRange(fakeTime.startThisDay(), fakeTime.endThisDay()),
            category = MainCategory(),
        )

        val result = timeTaskInteractor.addTimeTask(fakeTask)

        assertEquals(true, result.isLeft)

        assertEquals(0, timeTaskRepository.timeTasksList.size)

        assertEquals(1, timeTaskRepository.addedTaskCount)
        assertEquals(1, scheduleRepository.fetchSchedulesCount)
        assertEquals(0, scheduleRepository.createdScheduleCount)
    }

    @Test
    fun test_add_time_task_with_start_overlay() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time

        scheduleRepository.scheduleList.add(
            Schedule(
                date = fakeTime.time,
                status = DailyScheduleStatus.ACCOMPLISHMENT,
                timeTasks = listOf(
                    TimeTask(
                        key = 100L,
                        date = fakeTime,
                        timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                        category = MainCategory(),
                    ),
                )
            )
        )
        // TimeTask first -> 00:00 - 00:10
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                category = MainCategory(),
            ),
        )

        // TimeTask first -> 00:08 - 23:59
        val fakeTask = TimeTask(
            key = 0L,
            date = fakeTime,
            timeRange = TimeRange(from = fakeTime.shiftMinutes(8), to = fakeTime.endThisDay()),
            category = MainCategory(),
        )

        val actual = timeTaskInteractor.addTimeTask(fakeTask)
        val expected = EditorFailures.TimeOverlayError(
            startOverlay = fakeTime.shiftMinutes(10),
            endOverlay = null,
        )

        assertEquals(true, actual.isLeft)
        assertEquals(expected, (actual as Either.Left).data)

        assertEquals(0, timeTaskRepository.addedTaskCount)
        assertEquals(1, scheduleRepository.fetchSchedulesCount)
        assertEquals(0, scheduleRepository.createdScheduleCount)

        assertEquals(1, timeTaskRepository.timeTasksList.size)
        assertEquals(1, scheduleRepository.scheduleList[0].timeTasks.size)
    }

    @Test
    fun test_add_time_task_with_end_overlay() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time

        scheduleRepository.scheduleList.add(
            Schedule(
                date = fakeTime.time,
                status = DailyScheduleStatus.ACCOMPLISHMENT,
                timeTasks = listOf(
                    TimeTask(
                        key = 100L,
                        date = fakeTime,
                        timeRange = TimeRange(from = fakeTime.shiftMinutes(10), to = fakeTime.endThisDay()),
                        category = MainCategory(),
                    ),
                )
            )
        )
        // TimeTask first -> 00:10 - 23:59
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRange = TimeRange(from = fakeTime.shiftMinutes(10), to = fakeTime.endThisDay()),
                category = MainCategory(),
            ),
        )

        // TimeTask added -> 00:00 - 00:12
        val fakeTask = TimeTask(
            key = 0L,
            date = fakeTime,
            timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(12)),
            category = MainCategory(),
        )

        val actual = timeTaskInteractor.addTimeTask(fakeTask)
        val expected = EditorFailures.TimeOverlayError(
            startOverlay = null,
            endOverlay = fakeTime.shiftMinutes(10),
        )

        assertEquals(true, actual.isLeft)
        assertEquals(expected, (actual as Either.Left).data)

        assertEquals(0, timeTaskRepository.addedTaskCount)
        assertEquals(1, scheduleRepository.fetchSchedulesCount)
        assertEquals(0, scheduleRepository.createdScheduleCount)

        assertEquals(1, timeTaskRepository.timeTasksList.size)
        assertEquals(1, scheduleRepository.scheduleList[0].timeTasks.size)
    }

    @Test
    fun test_add_time_task_with_overlay() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time

        scheduleRepository.scheduleList.add(
            Schedule(
                date = fakeTime.time,
                status = DailyScheduleStatus.ACCOMPLISHMENT,
                timeTasks = listOf(
                    TimeTask(
                        key = 100L,
                        date = fakeTime,
                        timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                        category = MainCategory(),
                    ),
                    TimeTask(
                        key = 200L,
                        date = fakeTime,
                        timeRange = TimeRange(from = fakeTime.shiftMinutes(20), to = fakeTime.endThisDay()),
                        category = MainCategory(),
                    ),
                )
            )
        )
        // TimeTask first -> 00:00 - 00:10
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                category = MainCategory(),
            ),
        )
        // TimeTask second -> 00:20 - 23:59
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 200L,
                date = fakeTime,
                timeRange = TimeRange(from = fakeTime.shiftMinutes(20), to = fakeTime.endThisDay()),
                category = MainCategory(),
            ),
        )

        // TimeTask added -> 00:00 - 00:30
        val fakeTask = TimeTask(
            key = 0L,
            date = fakeTime,
            timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(30)),
            category = MainCategory(),
        )

        val actual = timeTaskInteractor.addTimeTask(fakeTask)
        val expected = EditorFailures.TimeOverlayError(
            startOverlay = fakeTime.shiftMinutes(10),
            endOverlay = fakeTime.shiftMinutes(20),
        )

        assertEquals(true, actual.isLeft)
        assertEquals(expected, (actual as Either.Left).data)

        assertEquals(0, timeTaskRepository.addedTaskCount)
        assertEquals(1, scheduleRepository.fetchSchedulesCount)
        assertEquals(0, scheduleRepository.createdScheduleCount)

        assertEquals(2, timeTaskRepository.timeTasksList.size)
        assertEquals(2, scheduleRepository.scheduleList[0].timeTasks.size)
    }

    @Test
    fun test_update_time_task() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time

        scheduleRepository.scheduleList.add(
            Schedule(
                date = fakeTime.time,
                status = DailyScheduleStatus.ACCOMPLISHMENT,
                timeTasks = listOf(
                    TimeTask(
                        key = 100L,
                        date = fakeTime,
                        timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                        category = MainCategory(),
                    ),
                    TimeTask(
                        key = 200L,
                        date = fakeTime,
                        timeRange = TimeRange(
                            from = fakeTime.shiftMinutes(10),
                            to = fakeTime.shiftMinutes(20)
                        ),
                        category = MainCategory(),
                    ),
                )
            )
        )
        // TimeTask first -> 00:00 - 00:10
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                category = MainCategory(),
            ),
        )
        // TimeTask second -> 00:10 - 00:20
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 200L,
                date = fakeTime,
                timeRange = TimeRange(
                    from = fakeTime.shiftMinutes(10),
                    to = fakeTime.shiftMinutes(20)
                ),
                category = MainCategory(),
            ),
        )

        // TimeTask updated(200) -> 00:10 - 00:30
        val fakeTask = TimeTask(
            key = 200L,
            date = fakeTime,
            timeRange = TimeRange(from = fakeTime.shiftMinutes(20), to = fakeTime.shiftMinutes(30)),
            category = MainCategory(),
        )

        val actual = timeTaskInteractor.updateTimeTask(fakeTask)

        assertEquals(true, actual.isRight)

        assertEquals(1, scheduleRepository.fetchSchedulesCount)
        assertEquals(0, scheduleRepository.createdScheduleCount)
        assertEquals(1, timeTaskRepository.updateTaskCount)

        assertEquals(2, timeTaskRepository.timeTasksList.size)
        assertEquals(2, scheduleRepository.scheduleList[0].timeTasks.size)
        assertEquals(fakeTask, timeTaskRepository.timeTasksList[1])
    }

    @Test
    fun test_update_time_task_with_overlay() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time

        scheduleRepository.scheduleList.add(
            Schedule(
                date = fakeTime.time,
                status = DailyScheduleStatus.ACCOMPLISHMENT,
                timeTasks = listOf(
                    TimeTask(
                        key = 100L,
                        date = fakeTime,
                        timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                        category = MainCategory(),
                    ),
                    TimeTask(
                        key = 200L,
                        date = fakeTime,
                        timeRange = TimeRange(from = fakeTime.shiftMinutes(20), to = fakeTime.endThisDay()),
                        category = MainCategory(),
                    ),
                    TimeTask(
                        key = 300L,
                        date = fakeTime,
                        timeRange = TimeRange(
                            from = fakeTime.shiftMinutes(10),
                            to = fakeTime.shiftMinutes(20)
                        ),
                        category = MainCategory(),
                    ),
                )
            )
        )
        // TimeTask first -> 00:00 - 00:10
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                category = MainCategory(),
            ),
        )
        // TimeTask second -> 00:20 - 23:59
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 200L,
                date = fakeTime,
                timeRange = TimeRange(from = fakeTime.shiftMinutes(20), to = fakeTime.endThisDay()),
                category = MainCategory(),
            ),
        )
        // TimeTask thirty -> 00:10 - 00:20
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 300L,
                date = fakeTime,
                timeRange = TimeRange(
                    from = fakeTime.shiftMinutes(10),
                    to = fakeTime.shiftMinutes(20)
                ),
                category = MainCategory(),
            ),
        )

        // TimeTask updated(300) -> 00:00 - 00:30
        val fakeTask = TimeTask(
            key = 300L,
            date = fakeTime,
            timeRange = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(30)),
            category = MainCategory(),
        )

        val actual = timeTaskInteractor.updateTimeTask(fakeTask)
        val expected = EditorFailures.TimeOverlayError(
            startOverlay = fakeTime.shiftMinutes(10),
            endOverlay = fakeTime.shiftMinutes(20),
        )

        assertEquals(true, actual.isLeft)
        assertEquals(expected, (actual as Either.Left).data)

        assertEquals(1, scheduleRepository.fetchSchedulesCount)
        assertEquals(0, scheduleRepository.createdScheduleCount)
        assertEquals(0, timeTaskRepository.updateTaskCount)

        assertEquals(3, scheduleRepository.scheduleList[0].timeTasks.size)
        assertEquals(3, timeTaskRepository.timeTasksList.size)
    }

    @Test
    fun test_delete_time_task() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRange = TimeRange(from = fakeTime, to = fakeTime.endThisDay()),
                category = MainCategory(),
            ),
        )

        val result = timeTaskInteractor.deleteTimeTask(100L)

        assertEquals(true, result.isRight)

        assertEquals(1, timeTaskRepository.deleteTaskCount)
        assertEquals(0, timeTaskRepository.timeTasksList.size)
    }
}

private class FakeTimeTaskRepository : TimeTaskRepository {

    val timeTasksList = mutableListOf<TimeTask>()

    var addedTaskCount = 0
    var fetchTaskCount = 0
    var updateTaskCount = 0
    var deleteTaskCount = 0

    var errorWhileAction = false

    override suspend fun addTimeTasks(timeTasks: List<TimeTask>) {
        addedTaskCount++
        if (!errorWhileAction) {
            timeTasksList.addAll(timeTasks)
        } else {
            throw SQLiteException()
        }
    }

    override suspend fun fetchAllTimeTaskByDate(date: Date): List<TimeTask> {
        fetchTaskCount++
        return if (!errorWhileAction) {
            timeTasksList
        } else {
            throw NullPointerException()
        }
    }

    override suspend fun updateTimeTaskList(timeTaskList: List<TimeTask>) {
        updateTaskCount++
        if (!errorWhileAction) {
            timeTaskList.forEach { timeTask ->
                val index = timeTasksList.indexOfFirst { it.key == timeTask.key }
                timeTasksList[index] = timeTask
            }
        } else {
            throw SQLiteException()
        }
    }

    override suspend fun updateTimeTask(timeTask: TimeTask) {
        updateTaskCount++
        if (!errorWhileAction) {
            val index = timeTasksList.indexOfFirst { it.key == timeTask.key }
            timeTasksList[index] = timeTask
        } else {
            throw SQLiteException()
        }
    }

    override suspend fun deleteTimeTasks(keys: List<Long>) {
        deleteTaskCount++
        if (!errorWhileAction) {
            keys.forEach { key ->
                timeTasksList.removeAt(timeTasksList.indexOfFirst { it.key == key })
            }
        } else {
            throw SQLiteException()
        }
    }
}

private class FakeScheduleRepository : ScheduleRepository {

    val scheduleList = mutableListOf<Schedule>()

    var createdScheduleCount = 0
    var fetchSchedulesCount = 0
    var updateScheduleCount = 0
    var deleteScheduleCount = 0

    var errorWhileAction = false

    override suspend fun createSchedules(schedules: List<Schedule>) {
        createdScheduleCount++
        scheduleList.addAll(schedules)
    }

    override suspend fun fetchSchedulesByRange(timeRange: TimeRange?): Flow<List<Schedule>> {
        fetchSchedulesCount++
        return if (timeRange == null) {
            flowOf(scheduleList)
        } else {
            val fondedSchedules = mutableListOf<Schedule>().apply {
                scheduleList.forEach {
                    if (it.date >= timeRange.from.time && it.date <= timeRange.to.time) add(it)
                }
            }
            flowOf(fondedSchedules)
        }
    }

    override suspend fun fetchScheduleByDate(date: Long): Flow<Schedule?> {
        fetchSchedulesCount++
        return flowOf(scheduleList.find { it.date == date })
    }

    override suspend fun updateSchedule(schedule: Schedule) {
        updateScheduleCount++
        val index = scheduleList.indexOfFirst { it.date == schedule.date }.takeIf { it != -1 }
        scheduleList[index!!] = schedule
    }

    override suspend fun deleteAllSchedules(): List<Schedule> {
        deleteScheduleCount++
        return scheduleList.apply { scheduleList.clear() }
    }
}

private class FakeDateManager : DateManager {

    var currentDate: Date = Calendar.getInstance().time

    override fun fetchCurrentDate() = currentDate

    override fun fetchBeginningCurrentDay() = currentDate.startThisDay()

    override fun fetchEndCurrentDay() = currentDate.endThisDay()

    override fun calculateLeftTime(endTime: Date) = endTime.time - currentDate.time

    override fun calculateProgress(startTime: Date, endTime: Date): Float {
        val currentTime = fetchCurrentDate().time
        val pastTime = ((currentTime - startTime.time).toMinutes()).toFloat()
        val duration = ((endTime.time - startTime.time).toMinutes()).toFloat()
        val progress = pastTime / duration

        return if (progress < 0f) 0f else if (progress > 1f) 1f else progress
    }

    override fun setCurrentHMS(date: Date): Date {
        val currentCalendar = Calendar.getInstance().apply {
            time = currentDate
        }
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
