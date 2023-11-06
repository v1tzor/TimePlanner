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
package ru.aleshin.features.home.api.data.datasources.schedules

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.aleshin.features.home.api.data.datasources.categories.MainCategoriesDao
import ru.aleshin.features.home.api.data.datasources.subcategories.SubCategoriesDao
import ru.aleshin.features.home.api.data.datasources.templates.TemplatesDao
import ru.aleshin.features.home.api.data.datasources.undefinedtasks.UndefinedTasksDao
import ru.aleshin.features.home.api.data.models.categories.MainCategoryEntity
import ru.aleshin.features.home.api.data.models.categories.SubCategoryEntity
import ru.aleshin.features.home.api.data.models.schedules.DailyScheduleEntity
import ru.aleshin.features.home.api.data.models.tasks.TimeTaskEntity
import ru.aleshin.features.home.api.data.models.tasks.UndefinedTaskEntity
import ru.aleshin.features.home.api.data.models.template.RepeatTimeEntity
import ru.aleshin.features.home.api.data.models.template.TemplateEntity
import ru.aleshin.features.home.api.domain.entities.schedules.UndefinedTask

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Database(
    version = 9,
    entities = [
        TemplateEntity::class,
        RepeatTimeEntity::class,
        DailyScheduleEntity::class,
        TimeTaskEntity::class,
        UndefinedTaskEntity::class,
        MainCategoryEntity::class,
        SubCategoryEntity::class,
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 8, to = 9),
    ],
)
abstract class SchedulesDataBase : RoomDatabase() {

    abstract fun fetchSchedulesDao(): SchedulesDao
    abstract fun fetchMainCategoriesDao(): MainCategoriesDao
    abstract fun fetchSubCategoriesDao(): SubCategoriesDao
    abstract fun fetchTemplatesDao(): TemplatesDao
    abstract fun fetchUndefinedTasksDao(): UndefinedTasksDao

    companion object {
        const val NAME = "SchedulesDataBase.db"

        val MIGRATE_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                try {
                    copyAndModifiedTable(database)
                    rebaseTable(database)
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }

            private fun copyAndModifiedTable(database: SupportSQLiteDatabase) {
                val values = ContentValues()
                database.execSQL(
                    "CREATE TEMPORARY TABLE IF NOT EXISTS `mainCategories_new` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`custom_name` TEXT, " +
                        "`default_category_type` TEXT)",
                )
                database.query("SELECT * FROM mainCategories").apply {
                    while (moveToNext()) {
                        values.clear()
                        val id = getInt(getColumnIndexOrThrow("id"))
                        val nameCategoryName = getString(getColumnIndexOrThrow("main_category_name"))
                        val engCategoryName = getString(getColumnIndexOrThrow("main_category_name_eng"))
                        val type = getString(getColumnIndexOrThrow("main_icon"))
                        values.put("id", id)
                        values.put("custom_name", nameCategoryName ?: engCategoryName ?: null)
                        values.put("default_category_type", type)
                        database.insert("mainCategories_new", CONFLICT_REPLACE, values)
                    }
                    close()
                }
            }

            private fun rebaseTable(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE mainCategories")
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `mainCategories` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`custom_name` TEXT, " +
                        "`default_category_type` TEXT)",
                )
                database.execSQL(
                    "INSERT INTO mainCategories " +
                        "SELECT id, custom_name, default_category_type " +
                        "FROM mainCategories_new",
                )
                database.execSQL("DROP TABLE mainCategories_new")
            }
        }
        val MIGRATE_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                try {
                    val healthValues = ContentValues().apply {
                        put("id", -1)
                        putNull("custom_name")
                        put("default_category_type", "HEALTH")
                    }
                    val shoppingValues = ContentValues().apply {
                        put("id", -2)
                        putNull("custom_name")
                        put("default_category_type", "SHOPPING")
                    }
                    database.insert("mainCategories", CONFLICT_REPLACE, healthValues)
                    database.insert("mainCategories", CONFLICT_REPLACE, shoppingValues)
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }
        }
        val MIGRATE_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                try {
                    database.execSQL(
                        "ALTER TABLE timeTaskTemplates " +
                            "ADD COLUMN repeat_enabled INTEGER NOT NULL DEFAULT 0",
                    )
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }
        }
        val MIGRATE_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                try {
                    database.execSQL("ALTER TABLE timeTasks ADD COLUMN created_at INTEGER")
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }
        }
    }
}
