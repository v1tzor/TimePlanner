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

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.aleshin.core.data.datasources.categories.MainCategoryDao
import ru.aleshin.core.data.datasources.subcategories.SubCategoryDao
import ru.aleshin.core.data.datasources.tasks.TimeTaskDao
import ru.aleshin.core.data.datasources.tasks.UndefinedTaskDao
import ru.aleshin.core.data.datasources.templates.TemplateDao
import ru.aleshin.core.data.models.categories.MainCategoryEntity
import ru.aleshin.core.data.models.categories.SubCategoryEntity
import ru.aleshin.core.data.models.schedules.DailyScheduleEntity
import ru.aleshin.core.data.models.tasks.TimeTaskEntity
import ru.aleshin.core.data.models.tasks.UndefinedTaskEntity
import ru.aleshin.core.data.models.template.RepeatTimeEntity
import ru.aleshin.core.data.models.template.TemplateEntity

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Database(
    version = 16,
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
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(from = 11, to = 12),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14),
        AutoMigration(from = 14, to = 15),
    ],
)
abstract class SchedulesDataBase : RoomDatabase() {

    abstract fun fetchScheduleDao(): ScheduleDao
    abstract fun fetchTimeTaskDao(): TimeTaskDao
    abstract fun fetchMainCategoryDao(): MainCategoryDao
    abstract fun fetchSubCategoryDao(): SubCategoryDao
    abstract fun fetchTemplateDao(): TemplateDao
    abstract fun fetchUndefinedTaskDao(): UndefinedTaskDao

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
                        val nameCategoryName =
                            getString(getColumnIndexOrThrow("main_category_name"))
                        val engCategoryName =
                            getString(getColumnIndexOrThrow("main_category_name_eng"))
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
        val MIGRATE_15_16 = object : Migration(15, 16) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                try {
                    migrateTemplatesTable(database)
                    migrateTimeTasksTable(database)
                    migrateUndefinedTasksTable(database)
                    database.setTransactionSuccessful()
                } finally {
                    database.endTransaction()
                }
            }

            private fun migrateTemplatesTable(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS `repeatTimes_backup`")
                database.execSQL(
                    "CREATE TEMPORARY TABLE `repeatTimes_backup` AS " +
                            "SELECT * FROM `repeatTimes`",
                )
                database.execSQL("DROP TABLE IF EXISTS `timeTaskTemplates_new`")
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `timeTaskTemplates_new` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`start_time` INTEGER NOT NULL, " +
                            "`end_time` INTEGER NOT NULL, " +
                            "`main_category_id` INTEGER NOT NULL, " +
                            "`sub_category_id` INTEGER, " +
                            "`priority` TEXT NOT NULL, " +
                            "`is_enable_notification` INTEGER NOT NULL, " +
                            "`is_consider_in_statistics` INTEGER NOT NULL, " +
                            "`repeat_enabled` INTEGER NOT NULL DEFAULT 0, " +
                            "FOREIGN KEY(`main_category_id`) REFERENCES `mainCategories`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE, " +
                            "FOREIGN KEY(`sub_category_id`) REFERENCES `subCategories`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE SET NULL)",
                )
                database.execSQL(
                    "INSERT INTO `timeTaskTemplates_new` (" +
                            "`id`, `start_time`, `end_time`, `main_category_id`, `sub_category_id`, " +
                            "`priority`, `is_enable_notification`, `is_consider_in_statistics`, `repeat_enabled`) " +
                            "SELECT `id`, `start_time`, `end_time`, `main_category_id`, `sub_category_id`, " +
                            "CASE " +
                            "WHEN `is_important` = 1 THEN 'MAX' " +
                            "WHEN `is_medium_important` = 1 THEN 'MEDIUM' " +
                            "ELSE 'STANDARD' " +
                            "END, " +
                            "`is_enable_notification`, `is_consider_in_statistics`, `repeat_enabled` " +
                            "FROM `timeTaskTemplates`",
                )
                database.execSQL("DROP TABLE `timeTaskTemplates`")
                database.execSQL("ALTER TABLE `timeTaskTemplates_new` RENAME TO `timeTaskTemplates`")
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_timeTaskTemplates_main_category_id` " +
                            "ON `timeTaskTemplates` (`main_category_id`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_timeTaskTemplates_sub_category_id` " +
                            "ON `timeTaskTemplates` (`sub_category_id`)",
                )
                database.execSQL(
                    "INSERT OR REPLACE INTO `repeatTimes` " +
                            "SELECT * FROM `repeatTimes_backup`",
                )
                database.execSQL("DROP TABLE `repeatTimes_backup`")
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_repeatTimes_template_id` " +
                            "ON `repeatTimes` (`template_id`)",
                )
            }

            private fun migrateTimeTasksTable(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS `timeTasks_new`")
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `timeTasks_new` (" +
                            "`key` INTEGER NOT NULL, " +
                            "`daily_schedule_date` INTEGER NOT NULL, " +
                            "`next_schedule_date` INTEGER DEFAULT null, " +
                            "`start_time` INTEGER NOT NULL, " +
                            "`end_time` INTEGER NOT NULL, " +
                            "`created_at` INTEGER, " +
                            "`main_category_id` INTEGER NOT NULL, " +
                            "`sub_category_id` INTEGER, " +
                            "`linked_template_id` INTEGER, " +
                            "`is_completed` INTEGER NOT NULL DEFAULT 1, " +
                            "`priority` TEXT NOT NULL, " +
                            "`is_enable_notification` INTEGER NOT NULL, " +
                            "`fifteen_minutes_before_notify` INTEGER NOT NULL DEFAULT 0, " +
                            "`one_hour_before_notify` INTEGER NOT NULL DEFAULT 0, " +
                            "`three_hour_before_notify` INTEGER NOT NULL DEFAULT 0, " +
                            "`one_day_before_notify` INTEGER NOT NULL DEFAULT 0, " +
                            "`one_week_before_notify` INTEGER NOT NULL DEFAULT 0, " +
                            "`before_end_notify` INTEGER NOT NULL DEFAULT 0, " +
                            "`is_consider_in_statistics` INTEGER NOT NULL, " +
                            "`note` TEXT, " +
                            "PRIMARY KEY(`key`), " +
                            "FOREIGN KEY(`daily_schedule_date`) REFERENCES `dailySchedules`(`date`) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE, " +
                            "FOREIGN KEY(`main_category_id`) REFERENCES `mainCategories`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE, " +
                            "FOREIGN KEY(`sub_category_id`) REFERENCES `subCategories`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE SET NULL, " +
                            "FOREIGN KEY(`linked_template_id`) REFERENCES `timeTaskTemplates`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE SET NULL)",
                )
                database.execSQL(
                    "INSERT INTO `timeTasks_new` (" +
                            "`key`, `daily_schedule_date`, `next_schedule_date`, `start_time`, `end_time`, " +
                            "`created_at`, `main_category_id`, `sub_category_id`, `linked_template_id`, " +
                            "`is_completed`, `priority`, " +
                            "`is_enable_notification`, `fifteen_minutes_before_notify`, " +
                            "`one_hour_before_notify`, `three_hour_before_notify`, `one_day_before_notify`, " +
                            "`one_week_before_notify`, `before_end_notify`, `is_consider_in_statistics`, `note`) " +
                            "SELECT `key`, `daily_schedule_date`, `next_schedule_date`, `start_time`, `end_time`, " +
                            "`created_at`, `main_category_id`, `sub_category_id`, NULL, `is_completed`, " +
                            "CASE " +
                            "WHEN `is_important` = 1 THEN 'MAX' " +
                            "WHEN `is_medium_important` = 1 THEN 'MEDIUM' " +
                            "ELSE 'STANDARD' " +
                            "END, " +
                            "`is_enable_notification`, `fifteen_minutes_before_notify`, " +
                            "`one_hour_before_notify`, `three_hour_before_notify`, `one_day_before_notify`, " +
                            "`one_week_before_notify`, `before_end_notify`, `is_consider_in_statistics`, `note` " +
                            "FROM `timeTasks`",
                )
                database.execSQL("DROP TABLE `timeTasks`")
                database.execSQL("ALTER TABLE `timeTasks_new` RENAME TO `timeTasks`")
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_timeTasks_daily_schedule_date` " +
                            "ON `timeTasks` (`daily_schedule_date`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_timeTasks_next_schedule_date` " +
                            "ON `timeTasks` (`next_schedule_date`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_timeTasks_main_category_id` " +
                            "ON `timeTasks` (`main_category_id`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_timeTasks_sub_category_id` " +
                            "ON `timeTasks` (`sub_category_id`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_timeTasks_linked_template_id` " +
                            "ON `timeTasks` (`linked_template_id`)",
                )
                database.execSQL(
                    "UPDATE `timeTasks` SET `linked_template_id` = (" +
                            "SELECT `timeTaskTemplates`.`id` FROM `timeTaskTemplates` " +
                            "WHERE `timeTaskTemplates`.`repeat_enabled` = 1 " +
                            "AND `timeTaskTemplates`.`main_category_id` = `timeTasks`.`main_category_id` " +
                            "AND (`timeTaskTemplates`.`sub_category_id` IS `timeTasks`.`sub_category_id`) " +
                            "AND `timeTaskTemplates`.`priority` = `timeTasks`.`priority` " +
                            "AND `timeTaskTemplates`.`is_enable_notification` = `timeTasks`.`is_enable_notification` " +
                            "AND `timeTaskTemplates`.`is_consider_in_statistics` = `timeTasks`.`is_consider_in_statistics` " +
                            "AND strftime('%H:%M', `timeTaskTemplates`.`start_time` / 1000, 'unixepoch', 'localtime') = " +
                            "strftime('%H:%M', `timeTasks`.`start_time` / 1000, 'unixepoch', 'localtime') " +
                            "AND strftime('%H:%M', `timeTaskTemplates`.`end_time` / 1000, 'unixepoch', 'localtime') = " +
                            "strftime('%H:%M', `timeTasks`.`end_time` / 1000, 'unixepoch', 'localtime') " +
                            "AND `timeTasks`.`note` IS NULL " +
                            "ORDER BY `timeTaskTemplates`.`id` LIMIT 1" +
                            ") WHERE `linked_template_id` IS NULL"
                )
            }

            private fun migrateUndefinedTasksTable(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS `undefinedTasks_new`")
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `undefinedTasks_new` (" +
                            "`key` INTEGER NOT NULL, " +
                            "`created_at` INTEGER, " +
                            "`deadline` INTEGER, " +
                            "`main_category_id` INTEGER NOT NULL, " +
                            "`sub_category_id` INTEGER, " +
                            "`priority` TEXT NOT NULL, " +
                            "`note` TEXT, " +
                            "PRIMARY KEY(`key`), " +
                            "FOREIGN KEY(`main_category_id`) REFERENCES `mainCategories`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE, " +
                            "FOREIGN KEY(`sub_category_id`) REFERENCES `subCategories`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE SET NULL)",
                )
                database.execSQL(
                    "INSERT INTO `undefinedTasks_new` (" +
                            "`key`, `created_at`, `deadline`, `main_category_id`, " +
                            "`sub_category_id`, `priority`, `note`) " +
                            "SELECT `key`, `created_at`, `deadline`, `main_category_id`, " +
                            "`sub_category_id`, " +
                            "CASE " +
                            "WHEN `is_important` = 1 THEN 'MAX' " +
                            "WHEN `is_medium_important` = 1 THEN 'MEDIUM' " +
                            "ELSE 'STANDARD' " +
                            "END, " +
                            "`note` " +
                            "FROM `undefinedTasks`",
                )
                database.execSQL("DROP TABLE `undefinedTasks`")
                database.execSQL("ALTER TABLE `undefinedTasks_new` RENAME TO `undefinedTasks`")
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_undefinedTasks_main_category_id` " +
                            "ON `undefinedTasks` (`main_category_id`)",
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_undefinedTasks_sub_category_id` " +
                            "ON `undefinedTasks` (`sub_category_id`)",
                )
            }
        }

        fun create(context: Context) = Room.databaseBuilder(
            context = context,
            klass = SchedulesDataBase::class.java,
            name = NAME,
        ).createFromAsset("database/categories_prepopulate.db")
            .addMigrations(MIGRATE_2_3)
            .addMigrations(MIGRATE_4_5)
            .addMigrations(MIGRATE_5_6)
            .addMigrations(MIGRATE_7_8)
            .addMigrations(MIGRATE_15_16)
            .build()
    }
}
