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
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalMetric

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
@Entity(
    tableName = "goalHistory",
    indices = [
        Index("goal_id"),
        Index(value = ["goal_id", "period_start", "period_end"], unique = true),
        Index("period_end"),
        Index(value = ["is_achieved", "period_end"]),
    ],
)
data class GoalHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo("goal_id") val goalId: Long,
    @ColumnInfo("goal_title") val goalTitle: String,
    @ColumnInfo("metric") val metric: GoalMetric,
    @ColumnInfo("direction") val direction: GoalDirection,
    @ColumnInfo("target_value") val targetValue: Long,
    @ColumnInfo("actual_value") val actualValue: Long,
    @ColumnInfo("period_start") val periodStart: Long,
    @ColumnInfo("period_end") val periodEnd: Long,
    @ColumnInfo("is_achieved") val isAchieved: Boolean,
    @ColumnInfo("created_at") val createdAt: Long,
)
