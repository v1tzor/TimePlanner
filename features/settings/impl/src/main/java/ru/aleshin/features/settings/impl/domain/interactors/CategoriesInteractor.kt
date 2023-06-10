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
package ru.aleshin.features.settings.impl.domain.interactors

import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.home.api.domains.entities.categories.Categories
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.repository.CategoriesRepository
import ru.aleshin.features.home.api.domains.repository.SubCategoriesRepository
import ru.aleshin.features.settings.impl.domain.common.SettingsEitherWrapper
import ru.aleshin.features.settings.impl.domain.common.SettingsFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 09.06.2023.
 */
internal interface CategoriesInteractor {

    suspend fun removeAllCategories(): UnitDomainResult<SettingsFailures>
    suspend fun fetchAllCategories(): DomainResult<SettingsFailures, List<Categories>>
    suspend fun addCategories(categories: List<Categories>): UnitDomainResult<SettingsFailures>

    class Base @Inject constructor(
        private val categoriesRepository: CategoriesRepository,
        private val subCategoriesRepository: SubCategoriesRepository,
        private val eitherWrapper: SettingsEitherWrapper,
    ) : CategoriesInteractor {

        override suspend fun removeAllCategories() = eitherWrapper.wrap {
            categoriesRepository.deleteAllCategories()
            subCategoriesRepository.deleteAllSubCategories()
        }

        override suspend fun fetchAllCategories() = eitherWrapper.wrap {
            categoriesRepository.fetchCategories()
        }

        override suspend fun addCategories(categories: List<Categories>) = eitherWrapper.wrap {
            val subCategories = mutableListOf<SubCategory>().apply {
                categories.map { it.subCategories }.forEach { addAll(it) }
            }
            categoriesRepository.addMainCategories(categories.map { it.mainCategory })
            subCategoriesRepository.addSubCategories(subCategories)
        }
    }
}
