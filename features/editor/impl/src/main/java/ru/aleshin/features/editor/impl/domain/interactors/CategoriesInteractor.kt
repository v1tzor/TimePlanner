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
package ru.aleshin.features.editor.impl.domain.interactors

import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.home.api.domains.entities.categories.Categories
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.repository.CategoriesRepository
import ru.aleshin.features.home.api.domains.repository.SubCategoriesRepository
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
internal interface CategoriesInteractor {

    suspend fun fetchCategories(): DomainResult<EditorFailures, List<Categories>>
    suspend fun addSubCategory(subCategory: SubCategory): DomainResult<EditorFailures, Unit>

    class Base @Inject constructor(
        private val categoriesRepository: CategoriesRepository,
        private val subCategoriesRepository: SubCategoriesRepository,
        private val eitherWrapper: EditorEitherWrapper,
    ) : CategoriesInteractor {

        override suspend fun fetchCategories() = eitherWrapper.wrap {
            categoriesRepository.fetchCategories()
        }

        override suspend fun addSubCategory(subCategory: SubCategory) = eitherWrapper.wrap {
            subCategoriesRepository.addSubCategories(listOf(subCategory))
        }
    }
}
