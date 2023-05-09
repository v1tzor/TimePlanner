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
 * imitations under the License.
 */
package ru.aleshin.features.home.api.data.models.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.aleshin.features.home.api.domains.common.MainIcon

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
@Entity(tableName = "mainCategories")
data class MainCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo("main_category_name") val categoryName: String,
    @ColumnInfo("main_category_name_eng") val categoryNameEng: String?,
    @ColumnInfo("main_icon") val mainIcon: MainIcon?,
    @ColumnInfo("is_not_deleted") val isNotDeleted: Boolean = true,
)
