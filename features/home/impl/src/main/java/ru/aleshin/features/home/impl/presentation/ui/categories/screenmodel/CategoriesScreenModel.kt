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

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.EmptyDeps
import ru.aleshin.core.utils.platform.screenmodel.work.BackgroundWorkKey
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
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
) : BaseScreenModel<CategoriesViewState, CategoriesEvent, CategoriesAction, CategoriesEffect, EmptyDeps>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun init(deps: EmptyDeps) {
        if (!isInitialize.get()) {
            super.init(deps)
            dispatchEvent(CategoriesEvent.Init)
        } else {
            dispatchEvent(CategoriesEvent.CheckSelectedCategory)
        }
    }

    override suspend fun WorkScope<CategoriesViewState, CategoriesAction, CategoriesEffect>.handleEvent(
        event: CategoriesEvent,
    ) {
        when (event) {
            is CategoriesEvent.Init -> launchBackgroundWork(BackgroundKey.LOAD_CATEGORIES) {
                val command = CategoriesWorkCommand.LoadCategories
                categoriesWorkProcessor.work(command).collectAndHandleWork()
            }
            is CategoriesEvent.CheckSelectedCategory -> launchBackgroundWork(BackgroundKey.CHECK_CATEGORIES) {
                val command = CategoriesWorkCommand.CheckSelectedCategory(state().categories)
                categoriesWorkProcessor.work(command).collectAndHandleWork()
            }
            is CategoriesEvent.ChangeMainCategory -> {
                sendAction(CategoriesAction.ChangeMainCategory(event.mainCategory))
            }
            is CategoriesEvent.UpdateSubCategory -> launchBackgroundWork(BackgroundKey.CATEGORY_ACTION){
                val command = CategoriesWorkCommand.UpdateSubCategory(event.subCategory)
                categoriesWorkProcessor.work(command).collectAndHandleWork()
            }
            is CategoriesEvent.DeleteSubCategory -> launchBackgroundWork(BackgroundKey.CATEGORY_ACTION){
                val command = CategoriesWorkCommand.DeleteSubCategory(event.subCategory)
                categoriesWorkProcessor.work(command).collectAndHandleWork()
            }
            is CategoriesEvent.AddSubCategory -> launchBackgroundWork(BackgroundKey.CATEGORY_ACTION){
                val command = CategoriesWorkCommand.AddSubCategory(event.name, event.mainCategory)
                categoriesWorkProcessor.work(command).collectAndHandleWork()
            }
            is CategoriesEvent.AddMainCategory -> launchBackgroundWork(BackgroundKey.CATEGORY_ACTION){
                val command = CategoriesWorkCommand.AddMainCategory(event.name)
                categoriesWorkProcessor.work(command).collectAndHandleWork()
            }
            is CategoriesEvent.DeleteMainCategory -> launchBackgroundWork(BackgroundKey.CATEGORY_ACTION){
                val command = CategoriesWorkCommand.DeleteMainCategory(event.mainCategory)
                categoriesWorkProcessor.work(command).collectAndHandleWork()
            }
            is CategoriesEvent.UpdateMainCategory -> launchBackgroundWork(BackgroundKey.CATEGORY_ACTION){
                val command = CategoriesWorkCommand.UpdateMainCategory(event.mainCategory)
                categoriesWorkProcessor.work(command).collectAndHandleWork()
            }
            is CategoriesEvent.RestoreDefaultCategories -> launchBackgroundWork(BackgroundKey.CATEGORY_ACTION){
                val command = CategoriesWorkCommand.RestoreDefaultCategories
                categoriesWorkProcessor.work(command).collectAndHandleWork()
            }
        }
    }

    override suspend fun reduce(
        action: CategoriesAction,
        currentState: CategoriesViewState,
    ) = when (action) {
        is CategoriesAction.SetUp -> currentState.copy(
            categories = action.categories,
            selectedMainCategory = action.selected,
        )
        is CategoriesAction.UpdateCategories -> currentState.copy(
            categories = action.categories,
        )
        is CategoriesAction.ChangeMainCategory -> currentState.copy(
            selectedMainCategory = action.category,
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_CATEGORIES, CHECK_CATEGORIES, CATEGORY_ACTION
    }
}

@Composable
internal fun Screen.rememberCategoriesScreenModel() = rememberScreenModel {
    HomeComponentHolder.fetchComponent().fetchCategoriesScreenModel()
}
