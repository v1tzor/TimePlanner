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
package ru.aleshin.core.data.datasources.schedules

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

/**
 * @author Stanislav Aleshin on 07.07.2026.
 */
@RunWith(AndroidJUnit4::class)
class SchedulesDataBaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SchedulesDataBase::class.java,
    )

    @Test
    fun migrate15To16BackfillsOnlyCanonicalRepeatTasks() {
        val scheduleDate = Calendar.getInstance().apply {
            set(2100, Calendar.JANUARY, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val startTime = scheduleDate + HOURS_9
        val endTime = scheduleDate + HOURS_10

        helper.createDatabase(TEST_DB, 15).apply {
            insertBaseScheduleData(scheduleDate, startTime, endTime)
            close()
        }

        val database = helper.runMigrationsAndValidate(
            TEST_DB,
            16,
            true,
            SchedulesDataBase.MIGRATE_15_16,
        )

        database.query("SELECT `linked_template_id`, `priority` FROM `timeTasks` WHERE `key` = 100").use { cursor ->
            cursor.moveToFirst()
            assertEquals(10L, cursor.getLong(0))
            assertEquals("STANDARD", cursor.getString(1))
        }
        database.query("SELECT `linked_template_id` FROM `timeTasks` WHERE `key` = 101").use { cursor ->
            cursor.moveToFirst()
            assertNull(cursor.getString(0))
        }
        database.query(
            "SELECT COUNT(*) FROM `sqlite_master` WHERE `type` = 'table' AND `name` = 'templateTaskExceptions'",
        ).use { cursor ->
            cursor.moveToFirst()
            assertEquals(0, cursor.getInt(0))
        }
        database.close()
    }

    @Test
    fun migrate16To17CreatesGoalsAndPreservesHistoryAfterGoalDeletion() {
        helper.createDatabase(GOALS_TEST_DB, 16).close()

        val database = helper.runMigrationsAndValidate(
            GOALS_TEST_DB,
            17,
            true,
            SchedulesDataBase.MIGRATE_16_17,
        )
        database.execSQL("PRAGMA foreign_keys=ON")
        database.execSQL(
            "INSERT INTO `goals` (" +
                "`id`, `title`, `scope_type`, `main_category_id`, `sub_category_id`, " +
                "`metric`, `direction`, `target_value`, `period`, `start_date`, `created_at`) " +
                "VALUES (1, 'Study', 'ALL', NULL, NULL, 'DURATION', 'AT_LEAST', " +
                "3600000, 'WEEK', 1000, 1000)",
        )
        database.execSQL(
            "INSERT INTO `goalPeriods` (" +
                "`id`, `goal_id`, `goal_title`, `metric`, `direction`, `target_value`, " +
                "`actual_value`, `period`, `period_start`, `period_end`, `is_achieved`, " +
                "`created_at`) VALUES (" +
                "1, 1, 'Study', 'DURATION', 'AT_LEAST', 3600000, 3600000, " +
                "'WEEK', 1000, 2000, 1, 2000)",
        )
        database.execSQL("DELETE FROM `goals` WHERE `id` = 1")

        database.query(
            "SELECT `goal_id`, `goal_title`, `actual_value` FROM `goalPeriods` WHERE `id` = 1",
        ).use { cursor ->
            cursor.moveToFirst()
            assertEquals(1L, cursor.getLong(0))
            assertEquals("Study", cursor.getString(1))
            assertEquals(3_600_000L, cursor.getLong(2))
        }
        database.close()
    }

    @Test
    fun migrate17To18ConvertsPeriodToDeadlineAndPreservesHistory() {
        helper.createDatabase(GOAL_DEADLINE_TEST_DB, 17).apply {
            execSQL(
                "INSERT INTO `goals` (" +
                    "`id`, `title`, `scope_type`, `main_category_id`, `sub_category_id`, " +
                    "`metric`, `direction`, `target_value`, `period`, `start_date`, " +
                    "`created_at`) VALUES (" +
                    "1, 'Study', 'ALL', NULL, NULL, 'DURATION', 'AT_LEAST', " +
                    "3600000, 'WEEK', 1000, 2000)",
            )
            execSQL(
                "INSERT INTO `goalPeriods` (" +
                    "`id`, `goal_id`, `goal_title`, `metric`, `direction`, `target_value`, " +
                    "`actual_value`, `period`, `period_start`, `period_end`, `is_achieved`, " +
                    "`created_at`) VALUES (" +
                    "1, 1, 'Study', 'DURATION', 'AT_LEAST', 3600000, 3600000, " +
                    "'WEEK', 1000, 2000, 1, 2000)",
            )
            close()
        }

        val database = helper.runMigrationsAndValidate(
            GOAL_DEADLINE_TEST_DB,
            18,
            true,
            SchedulesDataBase.MIGRATE_17_18,
        )

        database.query(
            "SELECT `created_at`, `deadline` FROM `goals` WHERE `id` = 1",
        ).use { cursor ->
            cursor.moveToFirst()
            assertEquals(2_000L, cursor.getLong(0))
            assertEquals(518_401_000L, cursor.getLong(1))
        }
        database.query(
            "SELECT `goal_id`, `goal_title`, `actual_value` FROM `goalHistory` WHERE `id` = 1",
        ).use { cursor ->
            cursor.moveToFirst()
            assertEquals(1L, cursor.getLong(0))
            assertEquals("Study", cursor.getString(1))
            assertEquals(3_600_000L, cursor.getLong(2))
        }
        database.close()
    }

    private fun SupportSQLiteDatabase.insertBaseScheduleData(
        scheduleDate: Long,
        startTime: Long,
        endTime: Long,
    ) {
        execSQL(
            "INSERT INTO `mainCategories` (`id`, `custom_name`, `default_category_type`) " +
                "VALUES (1, 'Work', NULL)",
        )
        execSQL("INSERT INTO `dailySchedules` (`date`) VALUES ($scheduleDate)")
        execSQL(
            "INSERT INTO `timeTaskTemplates` (" +
                "`id`, `start_time`, `end_time`, `main_category_id`, `sub_category_id`, " +
                "`is_important`, `is_medium_important`, `is_enable_notification`, " +
                "`is_consider_in_statistics`, `repeat_enabled`) VALUES (" +
                "10, $startTime, $endTime, 1, NULL, 0, 0, 1, 1, 1)",
        )
        execSQL(
            "INSERT INTO `repeatTimes` (" +
                "`id`, `template_id`, `type`, `day`, `day_number`, `month`, `week_number`) " +
                "VALUES (20, 10, 'MONTH_DAY', NULL, 1, NULL, NULL)",
        )
        insertTimeTask(
            key = 100,
            scheduleDate = scheduleDate,
            startTime = startTime,
            endTime = endTime,
            note = null,
        )
        insertTimeTask(
            key = 101,
            scheduleDate = scheduleDate,
            startTime = startTime,
            endTime = endTime,
            note = "Manual task",
        )
    }

    private fun SupportSQLiteDatabase.insertTimeTask(
        key: Long,
        scheduleDate: Long,
        startTime: Long,
        endTime: Long,
        note: String?,
    ) {
        val noteValue = note?.let { "'$it'" } ?: "NULL"
        execSQL(
            "INSERT INTO `timeTasks` (" +
                "`key`, `daily_schedule_date`, `next_schedule_date`, `start_time`, `end_time`, " +
                "`created_at`, `main_category_id`, `sub_category_id`, `is_completed`, " +
                "`is_important`, `is_medium_important`, `is_enable_notification`, " +
                "`fifteen_minutes_before_notify`, `one_hour_before_notify`, " +
                "`three_hour_before_notify`, `one_day_before_notify`, `one_week_before_notify`, " +
                "`before_end_notify`, `is_consider_in_statistics`, `note`) VALUES (" +
                "$key, $scheduleDate, NULL, $startTime, $endTime, $scheduleDate, 1, NULL, 1, " +
                "0, 0, 1, 0, 0, 0, 0, 0, 0, 1, $noteValue)",
        )
    }

    companion object {
        private const val TEST_DB = "schedules-migration-test"
        private const val GOALS_TEST_DB = "goals-migration-test"
        private const val GOAL_DEADLINE_TEST_DB = "goal-deadline-migration-test"
        private const val HOURS_9 = 9L * 60L * 60L * 1000L
        private const val HOURS_10 = 10L * 60L * 60L * 1000L
    }
}
