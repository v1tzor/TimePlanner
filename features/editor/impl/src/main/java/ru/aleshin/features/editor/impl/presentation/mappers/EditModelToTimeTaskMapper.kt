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
package ru.aleshin.features.editor.impl.presentation.mappers

import ru.aleshin.core.utils.functional.Mapper
import ru.aleshin.features.editor.api.domain.EditModel
import ru.aleshin.features.home.api.domains.entities.schedules.TimeTask
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
internal interface EditModelToTimeTaskMapper : Mapper<EditModel, TimeTask> {
    class Base @Inject constructor() : EditModelToTimeTaskMapper {
        override fun map(input: EditModel) = TimeTask(
            key = input.key,
            date = input.date,
            timeRanges = input.timeRanges,
            category = checkNotNull(input.mainCategory),
            subCategory = input.subCategory,
            isImportant = input.isImportant,
            isEnableNotification = input.isEnableNotification,
            isConsiderInStatistics = input.isConsiderInStatistics,
        )
    }
}
