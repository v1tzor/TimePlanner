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
package ru.aleshin.features.settings.api.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.aleshin.features.settings.api.domain.entities.ThemeSettings

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Entity(tableName = "ThemeSettings")
data class ThemeSettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Embedded val settings: ThemeSettings,
)
