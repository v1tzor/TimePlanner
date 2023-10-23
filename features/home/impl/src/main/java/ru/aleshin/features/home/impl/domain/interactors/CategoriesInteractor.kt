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
package ru.aleshin.features.home.impl.domain.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.home.api.domain.entities.categories.Categories
import ru.aleshin.features.home.api.domain.entities.categories.MainCategory
import ru.aleshin.features.home.api.domain.repository.CategoriesRepository
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 16.04.2023.
 */
internal interface CategoriesInteractor {

    suspend fun fetchCategories(): Flow<DomainResult<HomeFailures, List<Categories>>>
    suspend fun addMainCategory(mainCategory: MainCategory): DomainResult<HomeFailures, Int>
    suspend fun updateMainCategory(mainCategory: MainCategory): UnitDomainResult<HomeFailures>
    suspend fun deleteMainCategory(mainCategory: MainCategory): UnitDomainResult<HomeFailures>
    suspend fun restoreDefaultCategories(): UnitDomainResult<HomeFailures>

    class Base @Inject constructor(
        private val categoriesRepository: CategoriesRepository,
        private val eitherWrapper: HomeEitherWrapper,
    ) : CategoriesInteractor {

        override suspend fun fetchCategories() = eitherWrapper.wrapFlow {
            categoriesRepository.fetchCategories().map { categories ->
                categories.sortedBy { it.category.id != 0 }
            }
        }

        override suspend fun addMainCategory(mainCategory: MainCategory) = eitherWrapper.wrap {
            categoriesRepository.addMainCategories(listOf(mainCategory))[0]
        }

        override suspend fun deleteMainCategory(mainCategory: MainCategory) = eitherWrapper.wrap {
            categoriesRepository.deleteMainCategory(mainCategory)
        }

        override suspend fun updateMainCategory(mainCategory: MainCategory) = eitherWrapper.wrap {
            categoriesRepository.updateMainCategory(mainCategory)
        }

        override suspend fun restoreDefaultCategories() = eitherWrapper.wrap {
            val categories = categoriesRepository.fetchCategories().first()
            categories.forEach {
                if (it.category.default != null) {
                    categoriesRepository.updateMainCategory(it.category.copy(customName = null))
                }
            }
        }
    }
}
