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
package ru.aleshin.features.home.impl.presentation.ui.categories.contract

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseAction
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseEvent
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseUiEffect
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseViewState
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.home.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.home.impl.presentation.models.categories.SubCategoryUi

/**
 * @author Stanislav Aleshin on 05.04.2023.
 */
@Parcelize
internal data class CategoriesViewState(
    val selectedMainCategory: MainCategoryUi? = null,
    val categories: List<CategoriesUi> = emptyList(),
) : BaseViewState

internal sealed class CategoriesEvent : BaseEvent {
    object Init : CategoriesEvent()
    object RestoreDefaultCategories : CategoriesEvent()
    data class AddSubCategory(val name: String, val mainCategory: MainCategoryUi) : CategoriesEvent()
    data class AddMainCategory(val name: String) : CategoriesEvent()
    data class ChangeMainCategory(val mainCategory: MainCategoryUi) : CategoriesEvent()
    data class UpdateMainCategory(val mainCategory: MainCategoryUi) : CategoriesEvent()
    data class UpdateSubCategory(val subCategory: SubCategoryUi) : CategoriesEvent()
    data class DeleteMainCategory(val mainCategory: MainCategoryUi) : CategoriesEvent()
    data class DeleteSubCategory(val subCategory: SubCategoryUi) : CategoriesEvent()
}

internal sealed class CategoriesEffect : BaseUiEffect {
    data class ShowError(val failure: HomeFailures) : CategoriesEffect()
}

internal sealed class CategoriesAction : BaseAction {
    data class SetUp(val categories: List<CategoriesUi>) : CategoriesAction()
    data class ChangeMainCategory(val category: MainCategoryUi) : CategoriesAction()
}
