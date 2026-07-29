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
package ru.aleshin.core.data.models.goals

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.aleshin.core.data.models.categories.MainCategoryEntity
import ru.aleshin.core.data.models.categories.SubCategoryEntity
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalScopeType

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = MainCategoryEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("main_category_id"),
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = SubCategoryEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sub_category_id"),
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("main_category_id"),
        Index("sub_category_id"),
        Index("created_at"),
    ],
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("scope_type") val scopeType: GoalScopeType,
    @ColumnInfo("main_category_id") val mainCategoryId: Long? = null,
    @ColumnInfo("sub_category_id") val subCategoryId: Long? = null,
    @ColumnInfo("metric") val metric: GoalMetric,
    @ColumnInfo("direction") val direction: GoalDirection,
    @ColumnInfo("target_value") val targetValue: Long,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("deadline") val deadline: Long,
)
