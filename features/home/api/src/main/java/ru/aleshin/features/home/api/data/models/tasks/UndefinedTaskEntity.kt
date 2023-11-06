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
package ru.aleshin.features.home.api.data.models.tasks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.aleshin.features.home.api.data.models.categories.MainCategoryEntity
import ru.aleshin.features.home.api.data.models.categories.SubCategoryEntity

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Entity(
    tableName = "undefinedTasks",
    foreignKeys = [
        ForeignKey(
            entity = MainCategoryEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("main_category_id"),
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SubCategoryEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sub_category_id"),
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
)
data class UndefinedTaskEntity(
    @PrimaryKey(autoGenerate = false) val key: Long = 0L,
    @ColumnInfo("created_at") val createdAt: Long? = null,
    @ColumnInfo("deadline") val deadline: Long? = null,
    @ColumnInfo("main_category_id", index = true) val mainCategoryId: Int,
    @ColumnInfo("sub_category_id", index = true) val subCategoryId: Int?,
    @ColumnInfo("is_important") val isImportant: Boolean,
    @ColumnInfo("note") val note: String? = null,
)
