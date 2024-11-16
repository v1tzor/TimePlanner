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
package ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel

import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.EffectResult
import ru.aleshin.core.utils.platform.screenmodel.work.FlowWorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.features.home.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.home.impl.domain.interactors.SubCategoriesInteractor
import ru.aleshin.features.home.impl.presentation.mapppers.categories.mapToDomain
import ru.aleshin.features.home.impl.presentation.mapppers.categories.mapToUi
import ru.aleshin.features.home.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.home.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.home.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesAction
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesEffect
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 06.04.2023.
 */
internal interface CategoriesWorkProcessor : FlowWorkProcessor<CategoriesWorkCommand, CategoriesAction, CategoriesEffect> {

    class Base @Inject constructor(
        private val categoriesInteractor: CategoriesInteractor,
        private val subCategoriesInteractor: SubCategoriesInteractor,
    ) : CategoriesWorkProcessor {

        override suspend fun work(command: CategoriesWorkCommand) = when (command) {
            is CategoriesWorkCommand.LoadCategories -> loadCategoriesWork()
            is CategoriesWorkCommand.CheckSelectedCategory -> checkSelectedCategoryWork(command.categories)
            is CategoriesWorkCommand.RestoreDefaultCategories -> restoreDefaultCategories()
            is CategoriesWorkCommand.AddMainCategory -> addMainCategory(command.name)
            is CategoriesWorkCommand.AddSubCategory -> addSubCategory(command.name, command.mainCategory)
            is CategoriesWorkCommand.UpdateMainCategory -> updateMainCategory(command.mainCategory)
            is CategoriesWorkCommand.UpdateSubCategory -> updateSubCategory(command.subCategory)
            is CategoriesWorkCommand.DeleteMainCategory -> deleteMainCategory(command.mainCategory)
            is CategoriesWorkCommand.DeleteSubCategory -> deleteSubCategory(command.subCategory)
        }

        private suspend fun loadCategoriesWork() = flow {
            var isSetUp = false
            val selectedCategoryId = categoriesInteractor.fetchFeatureMainCategory()
            categoriesInteractor.fetchCategories().collect { categoryEither ->
                categoryEither.handle(
                    onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
                    onRightAction = { domainCategories ->
                        val categories = domainCategories.map { it.mapToUi() }
                        if (!isSetUp) {
                            isSetUp = true
                            val emptyCategory = categories.find { it.mainCategory.id == 0 }
                            val selectedCategories = categories.find { it.mainCategory.id == selectedCategoryId }
                            val selectedCategory = selectedCategories?.mainCategory ?: emptyCategory?.mainCategory
                            emit(ActionResult(CategoriesAction.SetUp(categories, selectedCategory)))
                        } else {
                            emit(ActionResult(CategoriesAction.UpdateCategories(categories)))
                        }
                    },
                )
            }
        }

        private fun checkSelectedCategoryWork(categories: List<CategoriesUi>) = flow {
            val selectedCategoryId = categoriesInteractor.fetchFeatureMainCategory()
            val selectedCategories = categories.find { it.mainCategory.id == selectedCategoryId }
            val selectedCategory = selectedCategories?.mainCategory
            if (selectedCategory != null) {
                emit(ActionResult(CategoriesAction.ChangeMainCategory(selectedCategory)))
            }
        }

        private fun restoreDefaultCategories() = flow {
            categoriesInteractor.restoreDefaultCategories().handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }

        private suspend fun addSubCategory(name: String, mainCategory: MainCategoryUi) = flow {
            val subCategory = SubCategoryUi(name = name, mainCategory = mainCategory)
            subCategoriesInteractor.addSubCategory(subCategory.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }

        private suspend fun addMainCategory(categoryName: String) = flow {
            val mainCategory = MainCategoryUi(customName = categoryName, defaultType = null)
            categoriesInteractor.addMainCategory(mainCategory.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
                onRightAction = { id ->
                    emit(ActionResult(CategoriesAction.ChangeMainCategory(mainCategory.copy(id = id))))
                },
            )
        }

        private suspend fun deleteSubCategory(subCategory: SubCategoryUi) = flow {
            subCategoriesInteractor.deleteSubCategory(subCategory.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }

        private suspend fun updateSubCategory(subCategory: SubCategoryUi) = flow {
            subCategoriesInteractor.updateSubCategory(subCategory.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }

        private suspend fun deleteMainCategory(category: MainCategoryUi) = flow {
            categoriesInteractor.deleteMainCategory(category.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }

        private suspend fun updateMainCategory(category: MainCategoryUi) = flow {
            categoriesInteractor.updateMainCategory(category.mapToDomain()).handle(
                onRightAction = { emit(ActionResult(CategoriesAction.ChangeMainCategory(category))) },
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }
    }
}

internal sealed class CategoriesWorkCommand : WorkCommand {
    data object LoadCategories : CategoriesWorkCommand()
    data class CheckSelectedCategory(val categories: List<CategoriesUi>) : CategoriesWorkCommand()
    data object RestoreDefaultCategories : CategoriesWorkCommand()
    data class AddSubCategory(val name: String, val mainCategory: MainCategoryUi) : CategoriesWorkCommand()
    data class AddMainCategory(val name: String) : CategoriesWorkCommand()
    data class UpdateSubCategory(val subCategory: SubCategoryUi) : CategoriesWorkCommand()
    data class UpdateMainCategory(val mainCategory: MainCategoryUi) : CategoriesWorkCommand()
    data class DeleteSubCategory(val subCategory: SubCategoryUi) : CategoriesWorkCommand()
    data class DeleteMainCategory(val mainCategory: MainCategoryUi) : CategoriesWorkCommand()
}
