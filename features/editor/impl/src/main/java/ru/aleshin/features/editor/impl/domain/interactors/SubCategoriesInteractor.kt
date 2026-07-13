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
package ru.aleshin.features.editor.impl.domain.interactors

import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.core.domain.repository.SubCategoryRepository
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
internal interface SubCategoriesInteractor {

    suspend fun addOrUpdateSubCategory(subCategory: SubCategory): DomainResult<EditorFailures, Long>
    suspend fun deleteSubCategoryById(subCategoryId: Long): UnitDomainResult<EditorFailures>

    class Base @Inject constructor(
        private val subCategoryRepository: SubCategoryRepository,
        private val eitherWrapper: EditorEitherWrapper,
    ) : SubCategoriesInteractor {

        override suspend fun addOrUpdateSubCategory(subCategory: SubCategory) = eitherWrapper.wrap {
            subCategoryRepository.addOrUpdateSubCategory(subCategory)
        }

        override suspend fun deleteSubCategoryById(subCategoryId: Long) = eitherWrapper.wrap {
            subCategoryRepository.deleteSubCategoryById(subCategoryId)
        }
    }
}
