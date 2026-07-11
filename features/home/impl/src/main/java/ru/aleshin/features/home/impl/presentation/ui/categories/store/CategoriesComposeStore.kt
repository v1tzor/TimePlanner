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
package ru.aleshin.features.home.impl.presentation.ui.categories.store

import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.BaseOnlyOutComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesAction
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesEffect
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesEvent
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesInput
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesOutput
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 06.04.2023.
 */
internal class CategoriesComposeStore @Inject constructor(
    private val categoriesWorkProcessor: CategoriesWorkProcessor,
    stateCommunicator: StateCommunicator<CategoriesState>,
    effectCommunicator: EffectCommunicator<CategoriesEffect>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<CategoriesState, CategoriesEvent, CategoriesAction, CategoriesEffect, CategoriesInput, CategoriesOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: CategoriesInput, isRestore: Boolean) {
        super.initialize(input, isRestore)
        dispatchEvent(CategoriesEvent.Init(input, isRestore))
    }

    override suspend fun WorkScope<CategoriesState, CategoriesAction, CategoriesEffect, CategoriesOutput>.handleEvent(
        event: CategoriesEvent,
    ) {
        when (event) {
            is CategoriesEvent.Init -> with(event) {
                launchBackgroundWork(BackgroundKey.LOAD_CATEGORIES) {
                    val selectedCategoryId = if (!isRestore) input.mainCategoryId else state.selectedMainCategory?.id ?: input.mainCategoryId
                    val command = CategoriesWorkCommand.LoadCategories(selectedCategoryId)
                    categoriesWorkProcessor.work(command).collectAndHandleWork()
                }
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
        currentState: CategoriesState,
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

     class Factory @Inject constructor(
         private val categoriesWorkProcessor: CategoriesWorkProcessor,
         private val coroutineManager: CoroutineManager,
     ) : BaseOnlyOutComposeStore.Factory<CategoriesComposeStore, CategoriesState> {

         override fun create(savedState: CategoriesState): CategoriesComposeStore {
             return CategoriesComposeStore(
                 categoriesWorkProcessor = categoriesWorkProcessor,
                 stateCommunicator = StateCommunicator.Default(savedState),
                 effectCommunicator = EffectCommunicator.Default(),
                 coroutineManager = coroutineManager,
             )
         }
     }
}