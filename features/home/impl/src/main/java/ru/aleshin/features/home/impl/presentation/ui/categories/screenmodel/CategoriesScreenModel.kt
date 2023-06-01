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

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.impl.di.holder.HomeComponentHolder
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesAction
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesEffect
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesEvent
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesViewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 06.04.2023.
 */
internal class CategoriesScreenModel @Inject constructor(
    private val categoriesWorkProcessor: CategoriesWorkProcessor,
    stateCommunicator: CategoriesStateCommunicator,
    effectCommunicator: CategoriesEffectCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<CategoriesViewState, CategoriesEvent, CategoriesAction, CategoriesEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun init() {
        if (!isInitialize.get()) {
            super.init()
            dispatchEvent(CategoriesEvent.Init(MainCategory.absent()))
        }
    }

    override suspend fun WorkScope<CategoriesViewState, CategoriesAction, CategoriesEffect>.handleEvent(
        event: CategoriesEvent,
    ) = when (event) {
        is CategoriesEvent.Init -> {
            val command = CategoriesWorkCommand.LoadCategories(event.initCategory)
            categoriesWorkProcessor.work(command).handleWork()
        }
        is CategoriesEvent.ChangeMainCategory -> {
            sendAction(CategoriesAction.ChangeMainCategory(event.mainCategory))
        }
        is CategoriesEvent.UpdateSubCategory -> {
            val command = CategoriesWorkCommand.UpdateSubCategory(event.subCategory)
            categoriesWorkProcessor.work(command).handleWork()
        }
        is CategoriesEvent.DeleteSubCategory -> {
            val command = CategoriesWorkCommand.DeleteSubCategory(event.subCategory)
            categoriesWorkProcessor.work(command).handleWork()
        }
        is CategoriesEvent.AddSubCategory -> {
            val command = CategoriesWorkCommand.AddSubCategory(event.name, event.mainCategory)
            categoriesWorkProcessor.work(command).handleWork()
        }
        is CategoriesEvent.AddMainCategory -> {
            val command = CategoriesWorkCommand.AddMainCategory(event.name)
            categoriesWorkProcessor.work(command).handleWork()
        }
        is CategoriesEvent.DeleteMainCategory -> {
            val command = CategoriesWorkCommand.DeleteMainCategory(event.mainCategory)
            categoriesWorkProcessor.work(command).handleWork()
        }
        is CategoriesEvent.UpdateMainCategory -> {
            val command = CategoriesWorkCommand.UpdateMainCategory(event.mainCategory)
            categoriesWorkProcessor.work(command).handleWork()
        }
    }

    override suspend fun reduce(
        action: CategoriesAction,
        currentState: CategoriesViewState,
    ) = when (action) {
        is CategoriesAction.ChangeMainCategory -> currentState.copy(
            selectedMainCategory = action.category,
        )
        is CategoriesAction.SetUp -> currentState.copy(
            selectedMainCategory = action.category,
            categories = action.categories,
        )
    }
}

@Composable
internal fun Screen.rememberCategoriesScreenModel() = rememberScreenModel {
    HomeComponentHolder.fetchComponent().fetchCategoriesScreenModel()
}
