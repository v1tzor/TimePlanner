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
package ru.aleshin.features.home.impl.presentation.ui.categories.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi

/**
 * @author Stanislav Aleshin on 05.04.2023.
 */
@Serializable
internal data class CategoriesState(
    val selectedMainCategory: MainCategoryUi? = null,
    val categories: List<MainCategoryDetailsUi> = emptyList(),
) : StoreState

internal sealed class CategoriesEvent : StoreEvent {
    data class Init(val input: CategoriesInput, val isRestore: Boolean) : CategoriesEvent()
    data object RestoreDefaultCategories : CategoriesEvent()
    data class AddSubCategory(val name: String, val mainCategory: MainCategoryUi) : CategoriesEvent()
    data class AddMainCategory(val name: String) : CategoriesEvent()
    data class ChangeMainCategory(val mainCategory: MainCategoryUi) : CategoriesEvent()
    data class UpdateMainCategory(val mainCategory: MainCategoryUi) : CategoriesEvent()
    data class UpdateSubCategory(val subCategory: SubCategoryUi) : CategoriesEvent()
    data class DeleteMainCategory(val mainCategory: MainCategoryUi) : CategoriesEvent()
    data class DeleteSubCategory(val subCategory: SubCategoryUi) : CategoriesEvent()
}

internal sealed class CategoriesEffect : StoreEffect {
    data class ShowError(val failure: HomeFailures) : CategoriesEffect()
}

internal sealed class CategoriesAction : StoreAction {
    data class SetUp(val categories: List<MainCategoryDetailsUi>, val selected: MainCategoryUi?) : CategoriesAction()
    data class UpdateCategories(val categories: List<MainCategoryDetailsUi>) : CategoriesAction()
    data class ChangeMainCategory(val category: MainCategoryUi) : CategoriesAction()
}


internal sealed class CategoriesOutput : BaseOutput {
    data object NavigateToBack : CategoriesOutput()
}

internal data class CategoriesInput(
    val mainCategoryId: Long?
) : BaseInput