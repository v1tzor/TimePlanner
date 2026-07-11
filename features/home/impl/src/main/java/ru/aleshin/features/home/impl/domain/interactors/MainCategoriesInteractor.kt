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

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.MainCategoryDetails
import ru.aleshin.core.domain.repository.MainCategoryRepository
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 16.04.2023.
 */
internal interface MainCategoriesInteractor {

    suspend fun fetchCategories(): FlowDomainResult<HomeFailures, List<MainCategoryDetails>>
    suspend fun addOrUpdateMainCategory(mainCategory: MainCategory): DomainResult<HomeFailures, Long>
    suspend fun deleteMainCategoryById(mainCategoryId: Long): UnitDomainResult<HomeFailures>
    suspend fun restoreDefaultCategories(): UnitDomainResult<HomeFailures>

    class Base @Inject constructor(
        private val mainCategoryRepository: MainCategoryRepository,
        private val eitherWrapper: HomeEitherWrapper,
    ) : MainCategoriesInteractor {

        override suspend fun fetchCategories() = eitherWrapper.wrapFlow {
            mainCategoryRepository.fetchAllCategoriesDetails().map { categories ->
                categories.sortedBy { it.category.id != 0L }
            }
        }

        override suspend fun addOrUpdateMainCategory(mainCategory: MainCategory) = eitherWrapper.wrap {
            mainCategoryRepository.addOrUpdateCategory(mainCategory)
        }

        override suspend fun deleteMainCategoryById(mainCategoryId: Long) = eitherWrapper.wrap {
            mainCategoryRepository.deleteCategoryById(mainCategoryId)
        }

        override suspend fun restoreDefaultCategories() = eitherWrapper.wrap {
            val categories = mainCategoryRepository.fetchAllCategoriesDetails().first()
            categories.forEach {
                if (it.category.default != null) {
                    mainCategoryRepository.addOrUpdateCategory(it.category.copy(customName = null))
                }
            }
        }
    }
}
