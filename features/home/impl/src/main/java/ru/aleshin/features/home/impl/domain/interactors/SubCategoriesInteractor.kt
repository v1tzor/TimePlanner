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
package ru.aleshin.features.home.impl.domain.interactors

import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.repository.SubCategoriesRepository
import ru.aleshin.features.home.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
internal interface SubCategoriesInteractor {

    suspend fun addSubCategory(subCategory: SubCategory): UnitDomainResult<HomeFailures>
    suspend fun updateSubCategory(subCategory: SubCategory): UnitDomainResult<HomeFailures>
    suspend fun deleteSubCategory(subCategory: SubCategory): UnitDomainResult<HomeFailures>

    class Base @Inject constructor(
        private val subCategoriesRepository: SubCategoriesRepository,
        private val eitherWrapper: HomeEitherWrapper,
    ) : SubCategoriesInteractor {

        override suspend fun addSubCategory(subCategory: SubCategory) = eitherWrapper.wrap {
            subCategoriesRepository.addSubCategories(listOf(subCategory))
        }

        override suspend fun deleteSubCategory(subCategory: SubCategory) = eitherWrapper.wrap {
            subCategoriesRepository.deleteSubCategory(subCategory)
        }

        override suspend fun updateSubCategory(subCategory: SubCategory) = eitherWrapper.wrap {
            subCategoriesRepository.updateSubCategory(subCategory)
        }
    }
}
