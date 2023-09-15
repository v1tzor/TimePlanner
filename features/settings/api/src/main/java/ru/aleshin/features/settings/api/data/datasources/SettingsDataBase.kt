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
package ru.aleshin.features.settings.api.data.datasources

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.aleshin.features.settings.api.data.datasources.tasks.TasksSettingsDao
import ru.aleshin.features.settings.api.data.datasources.theme.ThemeSettingsDao
import ru.aleshin.features.settings.api.data.models.TasksSettingsEntity
import ru.aleshin.features.settings.api.data.models.ThemeSettingsEntity

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
@Database(
    version = 3,
    entities = [ThemeSettingsEntity::class, TasksSettingsEntity::class],
    exportSchema = true,
)
abstract class SettingsDataBase : RoomDatabase() {

    abstract fun fetchThemeSettingsDao(): ThemeSettingsDao
    abstract fun fetchTasksSettingsDao(): TasksSettingsDao

    companion object {
        const val NAME = "SettingsDataBase.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE ThemeSettings " +
                        "ADD COLUMN isDynamicColorEnable INTEGER DEFAULT 0 NOT NULL",
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `TasksSettings` (" +
                        "`id` INTEGER PRIMARY KEY NOT NULL, " +
                        "`task_view_status` TEXT NOT NULL)",
                )
                try {
                    val values = ContentValues().apply {
                        put("id", 0)
                        put("task_view_status", "COMPACT")
                    }
                    database.insert("TasksSettings", SQLiteDatabase.CONFLICT_REPLACE, values)
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }
        }
    }
}
