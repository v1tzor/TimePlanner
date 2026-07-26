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
package ru.aleshin.core.data.datasources.settings

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
@RunWith(AndroidJUnit4::class)
class SettingsDataBaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SettingsDataBase::class.java,
    )

    @Test
    fun migrate7To8AddsAgendaHomeViewMode() {
        helper.createDatabase(TEST_DB, 7).apply {
            execSQL(
                "INSERT INTO `TasksSettings` " +
                    "(`id`, `task_view_status`, `task_analytics_range`, " +
                    "`calendar_button_behavior`, `secure_mode`, `duration_presets`) " +
                    "VALUES (0, 'COMPACT', 'WEEK', 'SET_CURRENT_DATE', 0, '10,15,30')",
            )
            close()
        }

        val database = helper.runMigrationsAndValidate(
            TEST_DB,
            8,
            true,
            SettingsDataBase.MIGRATION_7_8,
        )

        database.query("SELECT `home_view_mode` FROM `TasksSettings` WHERE `id` = 0").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("AGENDA", cursor.getString(0))
        }
        database.close()
    }

    @Test
    fun migrate8To9AddsAnalyticsDatesAndPreservesRange() {
        helper.createDatabase(TEST_DB, 8).apply {
            execSQL(
                "INSERT INTO `TasksSettings` " +
                    "(`id`, `task_view_status`, `home_view_mode`, `task_analytics_range`, " +
                    "`calendar_button_behavior`, `secure_mode`, `duration_presets`) " +
                    "VALUES (0, 'COMPACT', 'TIMELINE', 'HALF_YEAR', " +
                    "'OPEN_CALENDAR', 1, '15,45')",
            )
            close()
        }

        val database = helper.runMigrationsAndValidate(
            TEST_DB,
            9,
            true,
            SettingsDataBase.MIGRATION_8_9,
        )

        database.query(
            "SELECT `task_analytics_range`, `task_analytics_anchor_date`, " +
                "`custom_analytics_date_from`, `custom_analytics_date_to`, " +
                "`home_view_mode`, `secure_mode`, `duration_presets` " +
                "FROM `TasksSettings` WHERE `id` = 0",
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("HALF_YEAR", cursor.getString(0))
            assertTrue(cursor.isNull(1))
            assertTrue(cursor.isNull(2))
            assertTrue(cursor.isNull(3))
            assertEquals("TIMELINE", cursor.getString(4))
            assertEquals(1, cursor.getInt(5))
            assertEquals("15,45", cursor.getString(6))
        }
        database.close()
    }

    companion object {
        private const val TEST_DB = "settings-migration-test"
    }
}
