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
package ru.aleshin.features.home.impl.domain.interactors

import ru.aleshin.core.domain.entities.categories.Categories
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.UndefinedTask
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
internal interface ShareTextInteractor {

    suspend fun fetchSharedTextTasks(
        text: String,
        categories: List<Categories>,
    ): DomainResult<HomeFailures, List<UndefinedTask>>

    class Base @Inject constructor(
        private val dateManager: DateManager,
        private val eitherWrapper: HomeEitherWrapper,
    ) : ShareTextInteractor {

        private val taskPrefixRegex = Regex("""^\s*(?:(?:[-*•])|(?:\[(?: |x|X)])|(?:\d+[\.)]))\s*""")

        override suspend fun fetchSharedTextTasks(
            text: String,
            categories: List<Categories>,
        ) = eitherWrapper.wrap {
            val defaultCategory = fetchDefaultCategory(categories)
            text.lineSequence().mapNotNull { line ->
                val note = line.parseTaskNote()
                note?.let {
                    UndefinedTask(
                        id = generateUniqueKey(),
                        createdAt = dateManager.fetchCurrentDate(),
                        mainCategory = defaultCategory,
                        note = note,
                    )
                }
            }.toList()
        }

        private fun fetchDefaultCategory(categories: List<Categories>): MainCategory {
            return categories.find { it.category.default == DefaultCategoryType.OTHER }?.category
                ?: categories.find { it.category.default == DefaultCategoryType.EMPTY }?.category
                ?: categories.firstOrNull()?.category
                ?: MainCategory()
        }

        private fun String.parseTaskNote(): String? {
            var value = trim()
            var nextValue = value.replace(taskPrefixRegex, "").trim()
            while (nextValue != value) {
                value = nextValue
                nextValue = value.replace(taskPrefixRegex, "").trim()
            }
            return value.take(Constants.Text.MAX_NOTE_LENGTH).takeIf { it.isNotBlank() }
        }
    }
}
