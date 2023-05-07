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
package ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel

import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.platform.screenmodel.work.*
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.home.impl.domain.interactors.SubCategoriesInteractor
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesAction
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesEffect
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 06.04.2023.
 */
internal interface CategoriesWorkProcessor : WorkProcessor<CategoriesWorkCommand, CategoriesAction, CategoriesEffect> {

    class Base @Inject constructor(
        private val categoriesInteractor: CategoriesInteractor,
        private val subCategoriesInteractor: SubCategoriesInteractor,
    ) : CategoriesWorkProcessor {

        override suspend fun work(command: CategoriesWorkCommand) = when (command) {
            is CategoriesWorkCommand.LoadCategories -> loadCategoriesWork(command.mainCategory)
            is CategoriesWorkCommand.AddMainCategory -> addMainCategory(command.name)
            is CategoriesWorkCommand.AddSubCategory -> addSubCategory(command.name, command.mainCategory)
            is CategoriesWorkCommand.UpdateMainCategory -> updateMainCategory(command.mainCategory)
            is CategoriesWorkCommand.UpdateSubCategory -> updateSubCategory(command.subCategory)
            is CategoriesWorkCommand.DeleteMainCategory -> deleteMainCategory(command.mainCategory)
            is CategoriesWorkCommand.DeleteSubCategory -> deleteSubCategory(command.subCategory)
        }

        private suspend fun loadCategoriesWork(
            selectedCategory: MainCategory,
        ): WorkResult<CategoriesAction, CategoriesEffect> {
            val loadResult = categoriesInteractor.fetchAllCategories()
            return when (loadResult) {
                is Either.Right -> ActionResult(
                    CategoriesAction.SetUp(categories = loadResult.data, category = selectedCategory),
                )
                is Either.Left -> EffectResult(CategoriesEffect.ShowError(loadResult.data))
            }
        }

        private suspend fun addSubCategory(
            name: String,
            mainCategory: MainCategory,
        ): WorkResult<CategoriesAction, CategoriesEffect> {
            val subCategory = SubCategory(name = name, mainCategory = mainCategory)
            val addResult = subCategoriesInteractor.addSubCategory(subCategory)
            return when (addResult) {
                is Either.Right -> loadCategoriesWork(mainCategory)
                is Either.Left -> EffectResult(CategoriesEffect.ShowError(addResult.data))
            }
        }

        private suspend fun addMainCategory(
            categoryName: String,
        ): WorkResult<CategoriesAction, CategoriesEffect> {
            val mainCategory = MainCategory(name = categoryName)
            val addResult = categoriesInteractor.addMainCategory(mainCategory)
            return when (addResult) {
                is Either.Right -> loadCategoriesWork(mainCategory)
                is Either.Left -> EffectResult(CategoriesEffect.ShowError(addResult.data))
            }
        }

        private suspend fun deleteSubCategory(
            subCategory: SubCategory,
        ): WorkResult<CategoriesAction, CategoriesEffect> {
            val deleteResult = subCategoriesInteractor.deleteSubCategory(subCategory)
            return when (deleteResult) {
                is Either.Right -> loadCategoriesWork(subCategory.mainCategory)
                is Either.Left -> EffectResult(CategoriesEffect.ShowError(deleteResult.data))
            }
        }

        private suspend fun updateSubCategory(
            subCategory: SubCategory,
        ): WorkResult<CategoriesAction, CategoriesEffect> {
            val updateResult = subCategoriesInteractor.updateSubCategory(subCategory)
            return when (updateResult) {
                is Either.Right -> loadCategoriesWork(subCategory.mainCategory)
                is Either.Left -> EffectResult(CategoriesEffect.ShowError(updateResult.data))
            }
        }

        private suspend fun deleteMainCategory(
            category: MainCategory,
        ): WorkResult<CategoriesAction, CategoriesEffect> {
            val deleteResult = categoriesInteractor.deleteMainCategory(category)
            return when (deleteResult) {
                is Either.Right -> loadCategoriesWork(category)
                is Either.Left -> EffectResult(CategoriesEffect.ShowError(deleteResult.data))
            }
        }

        private suspend fun updateMainCategory(
            category: MainCategory,
        ): WorkResult<CategoriesAction, CategoriesEffect> {
            val updateResult = categoriesInteractor.updateMainCategory(category)
            return when (updateResult) {
                is Either.Right -> loadCategoriesWork(category)
                is Either.Left -> EffectResult(CategoriesEffect.ShowError(updateResult.data))
            }
        }
    }
}

internal sealed class CategoriesWorkCommand : WorkCommand {
    data class LoadCategories(val mainCategory: MainCategory) : CategoriesWorkCommand()
    data class AddSubCategory(val name: String, val mainCategory: MainCategory) : CategoriesWorkCommand()
    data class AddMainCategory(val name: String) : CategoriesWorkCommand()
    data class UpdateSubCategory(val subCategory: SubCategory) : CategoriesWorkCommand()
    data class UpdateMainCategory(val mainCategory: MainCategory) : CategoriesWorkCommand()
    data class DeleteSubCategory(val subCategory: SubCategory) : CategoriesWorkCommand()
    data class DeleteMainCategory(val mainCategory: MainCategory) : CategoriesWorkCommand()
}
