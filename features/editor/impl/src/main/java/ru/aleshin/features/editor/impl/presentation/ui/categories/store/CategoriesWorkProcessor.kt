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
package ru.aleshin.features.editor.impl.presentation.ui.categories.store

import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.features.editor.impl.domain.interactors.MainCategoriesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.SubCategoriesInteractor
import ru.aleshin.core.presentation.mappers.mapToDomain
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesAction
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesEffect
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 06.04.2023.
 */
internal interface CategoriesWorkProcessor :
    FlowWorkProcessor<CategoriesWorkCommand, CategoriesAction, CategoriesEffect, CategoriesOutput> {

    class Base @Inject constructor(
        private val categoriesInteractor: MainCategoriesInteractor,
        private val subCategoriesInteractor: SubCategoriesInteractor,
    ) : CategoriesWorkProcessor {

        override suspend fun work(command: CategoriesWorkCommand) = when (command) {
            is CategoriesWorkCommand.LoadCategories -> loadCategoriesWork(command.selectedCategoryId)
            is CategoriesWorkCommand.RestoreDefaultCategories -> restoreDefaultCategories()
            is CategoriesWorkCommand.AddMainCategory -> addMainCategory(command.name)
            is CategoriesWorkCommand.AddSubCategory -> addSubCategory(command.name, command.mainCategory)
            is CategoriesWorkCommand.UpdateMainCategory -> updateMainCategory(command.mainCategory)
            is CategoriesWorkCommand.UpdateSubCategory -> updateSubCategory(command.subCategory)
            is CategoriesWorkCommand.DeleteMainCategory -> deleteMainCategory(command.mainCategory)
            is CategoriesWorkCommand.DeleteSubCategory -> deleteSubCategory(command.subCategory)
        }

        private fun loadCategoriesWork(selectedCategoryId: Long?) = flow {
            var isSetUp = false
            categoriesInteractor.fetchCategories().collect { categoryEither ->
                categoryEither.handle(
                    onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
                    onRightAction = { domainCategories ->
                        val categories = domainCategories.map { it.mapToUi() }
                        if (!isSetUp) {
                            isSetUp = true
                            val emptyCategory = categories.find { it.mainCategory.id == 0L }
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

        private fun restoreDefaultCategories() = flow {
            categoriesInteractor.restoreDefaultCategories().handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }

        private fun addSubCategory(name: String, mainCategory: MainCategoryUi) = flow {
            val subCategory = SubCategoryUi(name = name, mainCategoryId = mainCategory.id)
            subCategoriesInteractor.addOrUpdateSubCategory(subCategory.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }

        private fun addMainCategory(categoryName: String) = flow {
            val mainCategory = MainCategoryUi(customName = categoryName, defaultType = null)
            categoriesInteractor.addOrUpdateMainCategory(mainCategory.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
                onRightAction = { id ->
                    emit(ActionResult(CategoriesAction.ChangeMainCategory(mainCategory.copy(id = id))))
                },
            )
        }

        private fun deleteSubCategory(subCategory: SubCategoryUi) = flow {
            subCategoriesInteractor.deleteSubCategoryById(subCategory.id).handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }

        private fun updateSubCategory(subCategory: SubCategoryUi) = flow {
            subCategoriesInteractor.addOrUpdateSubCategory(subCategory.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }

        private fun deleteMainCategory(category: MainCategoryUi) = flow {
            categoriesInteractor.deleteMainCategoryById(category.id).handle(
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }

        private fun updateMainCategory(category: MainCategoryUi) = flow {
            categoriesInteractor.addOrUpdateMainCategory(category.mapToDomain()).handle(
                onRightAction = { emit(ActionResult(CategoriesAction.ChangeMainCategory(category))) },
                onLeftAction = { emit(EffectResult(CategoriesEffect.ShowError(it))) },
            )
        }
    }
}

internal sealed class CategoriesWorkCommand : WorkCommand {
    data class LoadCategories(val selectedCategoryId: Long?) : CategoriesWorkCommand()
    data object RestoreDefaultCategories : CategoriesWorkCommand()
    data class AddSubCategory(val name: String, val mainCategory: MainCategoryUi) : CategoriesWorkCommand()
    data class AddMainCategory(val name: String) : CategoriesWorkCommand()
    data class UpdateSubCategory(val subCategory: SubCategoryUi) : CategoriesWorkCommand()
    data class UpdateMainCategory(val mainCategory: MainCategoryUi) : CategoriesWorkCommand()
    data class DeleteSubCategory(val subCategory: SubCategoryUi) : CategoriesWorkCommand()
    data class DeleteMainCategory(val mainCategory: MainCategoryUi) : CategoriesWorkCommand()
}

