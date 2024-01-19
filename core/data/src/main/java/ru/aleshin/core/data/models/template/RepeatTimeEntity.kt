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
package ru.aleshin.core.data.models.template

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.aleshin.core.domain.entities.template.RepeatTimeType
import ru.aleshin.core.utils.functional.Month
import ru.aleshin.core.utils.functional.WeekDay

/**
 * @author Stanislav Aleshin on 03.08.2023.
 */
@Entity(tableName = "repeatTimes")
data class RepeatTimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo("template_id", index = true) val templateId: Int,
    @ColumnInfo("type") val type: RepeatTimeType,
    @ColumnInfo("day") val day: WeekDay? = null,
    @ColumnInfo("day_number") val dayNumber: Int? = null,
    @ColumnInfo("month") val month: Month? = null,
    @ColumnInfo("week_number") val weekNumber: Int? = null,
)
