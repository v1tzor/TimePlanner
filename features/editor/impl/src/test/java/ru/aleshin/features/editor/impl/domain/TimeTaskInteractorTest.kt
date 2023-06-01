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
 * imitations under the License.
 */
package ru.aleshin.features.editor.impl.domain

import android.database.sqlite.SQLiteException
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.extensions.shiftMinutes
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.TimeOverlayManager
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.common.EditorErrorHandler
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.interactors.TimeTaskInteractor
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domains.repository.TimeTaskRepository
import java.lang.NullPointerException
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 27.05.2023.
 */
internal class TimeTaskInteractorTest {

    private lateinit var interactor: TimeTaskInteractor
    private lateinit var timeTaskRepository: FakeTimeTaskRepository
    private lateinit var overlayManager: TimeOverlayManager
    private lateinit var eitherWrapper: EditorEitherWrapper
    private lateinit var errorHandler: EditorErrorHandler

    @Before
    fun setUp() {
        timeTaskRepository = FakeTimeTaskRepository()
        errorHandler = EditorErrorHandler.Base()
        eitherWrapper = EditorEitherWrapper.Base(errorHandler = errorHandler)
        overlayManager = TimeOverlayManager.Base()

        interactor = TimeTaskInteractor.Base(
            timeTaskRepository = timeTaskRepository,
            eitherWrapper = eitherWrapper,
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
            timeRanges = TimeRange(fakeTime.startThisDay(), fakeTime.endThisDay()),
            category = MainCategory.absent(),
        )

        val result = interactor.addTimeTask(fakeTask)

        assertEquals(true, result.isRight)

        assertEquals(1, timeTaskRepository.addedTaskCount)
        assertEquals(1, timeTaskRepository.fetchTaskCount)

        assertEquals(1, timeTaskRepository.timeTasksList.size)
        // Interactor replace key on new unique value
        assertEquals(
            fakeTask.copy(key = timeTaskRepository.timeTasksList[0].key),
            timeTaskRepository.timeTasksList[0],
        )
    }

    @Test
    fun test_add_first_time_task_with_error() = runBlocking {
        timeTaskRepository.timeTasksList.clear()
        timeTaskRepository.errorWhileAction = true

        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        val fakeTask = TimeTask(
            key = 0L,
            date = fakeTime,
            timeRanges = TimeRange(fakeTime.startThisDay(), fakeTime.endThisDay()),
            category = MainCategory.absent(),
        )

        val result = interactor.addTimeTask(fakeTask)

        assertEquals(true, result.isLeft)

        assertEquals(1, timeTaskRepository.fetchTaskCount)
        assertEquals(0, timeTaskRepository.addedTaskCount)

        assertEquals(0, timeTaskRepository.timeTasksList.size)
    }

    @Test
    fun test_add_time_task_with_start_overlay() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        // TimeTask first -> 00:00 - 00:10
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRanges = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                category = MainCategory.absent(),
            ),
        )

        // TimeTask first -> 00:08 - 23:59
        val fakeTask = TimeTask(
            key = 0L,
            date = fakeTime,
            timeRanges = TimeRange(from = fakeTime.shiftMinutes(8), to = fakeTime.endThisDay()),
            category = MainCategory.absent(),
        )

        val actual = interactor.addTimeTask(fakeTask)
        val expected = EditorFailures.TimeOverlayError(
            startOverlay = fakeTime.shiftMinutes(10),
            endOverlay = null,
        )

        assertEquals(true, actual.isLeft)
        assertEquals(expected, (actual as Either.Left).data)

        assertEquals(1, timeTaskRepository.fetchTaskCount)
        assertEquals(0, timeTaskRepository.addedTaskCount)

        assertEquals(1, timeTaskRepository.timeTasksList.size)
    }

    @Test
    fun test_add_time_task_with_end_overlay() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        // TimeTask first -> 00:10 - 23:59
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRanges = TimeRange(from = fakeTime.shiftMinutes(10), to = fakeTime.endThisDay()),
                category = MainCategory.absent(),
            ),
        )

        // TimeTask added -> 00:00 - 00:12
        val fakeTask = TimeTask(
            key = 0L,
            date = fakeTime,
            timeRanges = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(12)),
            category = MainCategory.absent(),
        )

        val actual = interactor.addTimeTask(fakeTask)
        val expected = EditorFailures.TimeOverlayError(
            startOverlay = null,
            endOverlay = fakeTime.shiftMinutes(10),
        )

        assertEquals(true, actual.isLeft)
        assertEquals(expected, (actual as Either.Left).data)

        assertEquals(1, timeTaskRepository.fetchTaskCount)
        assertEquals(0, timeTaskRepository.addedTaskCount)

        assertEquals(1, timeTaskRepository.timeTasksList.size)
    }

    @Test
    fun test_add_time_task_with_overlay() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        // TimeTask first -> 00:00 - 00:10
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRanges = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                category = MainCategory.absent(),
            ),
        )
        // TimeTask second -> 00:20 - 23:59
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 200L,
                date = fakeTime,
                timeRanges = TimeRange(from = fakeTime.shiftMinutes(20), to = fakeTime.endThisDay()),
                category = MainCategory.absent(),
            ),
        )

        // TimeTask added -> 00:00 - 00:30
        val fakeTask = TimeTask(
            key = 0L,
            date = fakeTime,
            timeRanges = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(30)),
            category = MainCategory.absent(),
        )

        val actual = interactor.addTimeTask(fakeTask)
        val expected = EditorFailures.TimeOverlayError(
            startOverlay = fakeTime.shiftMinutes(10),
            endOverlay = fakeTime.shiftMinutes(20),
        )

        assertEquals(true, actual.isLeft)
        assertEquals(expected, (actual as Either.Left).data)

        assertEquals(1, timeTaskRepository.fetchTaskCount)
        assertEquals(0, timeTaskRepository.addedTaskCount)

        assertEquals(2, timeTaskRepository.timeTasksList.size)
    }

    @Test
    fun test_update_time_task() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        // TimeTask first -> 00:00 - 00:10
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRanges = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                category = MainCategory.absent(),
            ),
        )
        // TimeTask second -> 00:10 - 00:20
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 200L,
                date = fakeTime,
                timeRanges = TimeRange(from = fakeTime.shiftMinutes(10), to = fakeTime.shiftMinutes(20)),
                category = MainCategory.absent(),
            ),
        )

        // TimeTask updated(200) -> 00:10 - 00:30
        val fakeTask = TimeTask(
            key = 200L,
            date = fakeTime,
            timeRanges = TimeRange(from = fakeTime.shiftMinutes(20), to = fakeTime.shiftMinutes(30)),
            category = MainCategory.absent(),
        )

        val actual = interactor.updateTimeTask(fakeTask)

        assertEquals(true, actual.isRight)

        assertEquals(1, timeTaskRepository.fetchTaskCount)
        assertEquals(1, timeTaskRepository.updateTaskCount)

        assertEquals(2, timeTaskRepository.timeTasksList.size)
        assertEquals(fakeTask, timeTaskRepository.timeTasksList[1])
    }

    @Test
    fun test_update_time_task_with_overlay() = runBlocking {
        val calendar = Calendar.getInstance().apply { setStartDay() }
        val fakeTime = calendar.time
        // TimeTask first -> 00:00 - 00:10
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 100L,
                date = fakeTime,
                timeRanges = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(10)),
                category = MainCategory.absent(),
            ),
        )
        // TimeTask second -> 00:20 - 23:59
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 200L,
                date = fakeTime,
                timeRanges = TimeRange(from = fakeTime.shiftMinutes(20), to = fakeTime.endThisDay()),
                category = MainCategory.absent(),
            ),
        )
        // TimeTask thirty -> 00:10 - 00:20
        timeTaskRepository.timeTasksList.add(
            TimeTask(
                key = 300L,
                date = fakeTime,
                timeRanges = TimeRange(from = fakeTime.shiftMinutes(10), to = fakeTime.shiftMinutes(20)),
                category = MainCategory.absent(),
            ),
        )

        // TimeTask updated(300) -> 00:00 - 00:30
        val fakeTask = TimeTask(
            key = 300L,
            date = fakeTime,
            timeRanges = TimeRange(from = fakeTime, to = fakeTime.shiftMinutes(30)),
            category = MainCategory.absent(),
        )

        val actual = interactor.updateTimeTask(fakeTask)
        val expected = EditorFailures.TimeOverlayError(
            startOverlay = fakeTime.shiftMinutes(10),
            endOverlay = fakeTime.shiftMinutes(20),
        )

        assertEquals(true, actual.isLeft)
        assertEquals(expected, (actual as Either.Left).data)

        assertEquals(1, timeTaskRepository.fetchTaskCount)
        assertEquals(0, timeTaskRepository.updateTaskCount)

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
                timeRanges = TimeRange(from = fakeTime, to = fakeTime.endThisDay()),
                category = MainCategory.absent(),
            ),
        )

        val result = interactor.deleteTimeTask(100L)

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

    override suspend fun updateTimeTask(timeTask: TimeTask) {
        updateTaskCount++
        if (!errorWhileAction) {
            val index = timeTasksList.indexOfFirst { it.key == timeTask.key }
            timeTasksList[index] = timeTask
        } else {
            throw SQLiteException()
        }
    }

    override suspend fun deleteTimeTask(key: Long) {
        deleteTaskCount++
        if (!errorWhileAction) {
            timeTasksList.removeAt(timeTasksList.indexOfFirst { it.key == key })
        } else {
            throw SQLiteException()
        }
    }
}
