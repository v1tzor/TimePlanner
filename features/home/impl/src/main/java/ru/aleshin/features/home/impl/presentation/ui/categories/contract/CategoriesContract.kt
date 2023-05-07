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
package ru.aleshin.features.home.impl.presentation.ui.categories.contract

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseAction
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseEvent
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseUiEffect
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseViewState
import ru.aleshin.features.home.api.domains.entities.categories.Categories
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.impl.domain.entities.HomeFailures

/**
 * @author Stanislav Aleshin on 05.04.2023.
 */
@Parcelize
internal data class CategoriesViewState(
    val selectedMainCategory: MainCategory? = null,
    val categories: List<Categories> = emptyList(),
) : BaseViewState

internal sealed class CategoriesEvent : BaseEvent {
    data class Init(val initCategory: MainCategory) : CategoriesEvent()
    data class AddSubCategory(val name: String, val mainCategory: MainCategory) : CategoriesEvent()
    data class AddMainCategory(val name: String) : CategoriesEvent()
    data class ChangeMainCategory(val mainCategory: MainCategory) : CategoriesEvent()
    data class UpdateMainCategory(val mainCategory: MainCategory) : CategoriesEvent()
    data class UpdateSubCategory(val subCategory: SubCategory) : CategoriesEvent()
    data class DeleteMainCategory(val mainCategory: MainCategory) : CategoriesEvent()
    data class DeleteSubCategory(val subCategory: SubCategory) : CategoriesEvent()
}

internal sealed class CategoriesEffect : BaseUiEffect {
    data class ShowError(val failure: HomeFailures) : CategoriesEffect()
}

internal sealed class CategoriesAction : BaseAction {
    data class SetUp(val categories: List<Categories>, val category: MainCategory) : CategoriesAction()
    data class ChangeMainCategory(val category: MainCategory) : CategoriesAction()
}
