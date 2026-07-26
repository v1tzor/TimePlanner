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
package ru.aleshin.core.data.datasources.tasks

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.aleshin.core.data.datasources.schedules.SchedulesDataBase
import ru.aleshin.core.data.models.categories.MainCategoryEntity
import ru.aleshin.core.data.models.schedules.DailyScheduleEntity
import ru.aleshin.core.data.models.tasks.TimeTaskEntity
import ru.aleshin.core.domain.entities.tasks.TaskPriority

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@RunWith(AndroidJUnit4::class)
class TimeTaskDaoTest {

    private lateinit var database: SchedulesDataBase
    private lateinit var dao: TimeTaskDao

    @Before
    fun setUp() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            SchedulesDataBase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.fetchTimeTaskDao()
        database.fetchMainCategoryDao().addOrUpdateCategory(
            MainCategoryEntity(id = CATEGORY_ID, customName = "Test", defaultType = null),
        )
        database.fetchScheduleDao().addOrUpdateSchedules(
            listOf(
                DailyScheduleEntity(date = DAY_BEFORE),
                DailyScheduleEntity(date = DAY_FROM),
                DailyScheduleEntity(date = DAY_TO),
                DailyScheduleEntity(date = DAY_AFTER),
            ),
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun rangeUsesSourceDateAndDeterministicOrdering() = runBlocking {
        dao.addOrUpdateTimeTasks(
            listOf(
                task(key = 5L, sourceDate = DAY_BEFORE, nextDate = DAY_FROM, startTime = 30L),
                task(key = 3L, sourceDate = DAY_TO, startTime = 20L),
                task(key = 2L, sourceDate = DAY_FROM, startTime = 20L),
                task(key = 1L, sourceDate = DAY_FROM, startTime = 20L),
                task(key = 4L, sourceDate = DAY_AFTER, startTime = 10L),
            ),
        )

        val result = dao.fetchTimeTasksByScheduleDateRange(DAY_FROM, DAY_TO).first()

        assertEquals(listOf(1L, 2L, 3L), result.map { it.timeTask.key })
    }

    @Test
    fun rangeFlowEmitsAfterInsertUpdateAndDelete() = runBlocking {
        val emissions = Channel<List<Long>>(Channel.UNLIMITED)
        val collection = launch {
            dao.fetchTimeTasksByScheduleDateRange(DAY_FROM, DAY_TO)
                .take(4)
                .collect { tasks -> emissions.send(tasks.map { it.timeTask.key }) }
        }

        assertEquals(emptyList<Long>(), withTimeout(5_000L) { emissions.receive() })
        dao.addOrUpdateTimeTask(task(key = 1L, sourceDate = DAY_FROM, startTime = 20L))
        assertEquals(listOf(1L), withTimeout(5_000L) { emissions.receive() })
        dao.addOrUpdateTimeTask(task(key = 1L, sourceDate = DAY_FROM, startTime = 30L))
        assertEquals(listOf(1L), withTimeout(5_000L) { emissions.receive() })
        dao.deleteTimeTasksByIds(listOf(1L))
        assertEquals(emptyList<Long>(), withTimeout(5_000L) { emissions.receive() })

        withTimeout(5_000L) { collection.join() }
        emissions.close()
        Unit
    }

    private fun task(
        key: Long,
        sourceDate: Long,
        startTime: Long,
        nextDate: Long? = null,
    ) = TimeTaskEntity(
        key = key,
        dailyScheduleDate = sourceDate,
        nextScheduleDate = nextDate,
        startTime = startTime,
        endTime = startTime + 10L,
        mainCategoryId = CATEGORY_ID,
        subCategoryId = null,
        linkedTemplateId = null,
        isCompleted = true,
        priority = TaskPriority.STANDARD,
        isEnableNotification = false,
        isConsiderInStatistics = true,
    )

    companion object {
        private const val CATEGORY_ID = 1L
        private const val DAY_BEFORE = 1_000L
        private const val DAY_FROM = 2_000L
        private const val DAY_TO = 3_000L
        private const val DAY_AFTER = 4_000L
    }
}
