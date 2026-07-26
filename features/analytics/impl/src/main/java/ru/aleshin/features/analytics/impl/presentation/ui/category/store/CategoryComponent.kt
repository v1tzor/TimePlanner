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
package ru.aleshin.features.analytics.impl.presentation.ui.category.store

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.ChildComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.component.saveableStore
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryInput
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryOutput
import ru.aleshin.features.analytics.impl.presentation.ui.category.contract.CategoryState

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal abstract class CategoryComponent(componentContext: ComponentContext) : ChildComponent(componentContext) {

    abstract val store: CategoryComposeStore

    class Default(
        input: CategoryInput,
        storeFactory: CategoryComposeStore.Factory,
        componentContext: ComponentContext,
        outputConsumer: OutputConsumer<CategoryOutput>,
    ) : CategoryComponent(componentContext) {

        override val store by saveableStore(
            storeFactory = storeFactory,
            defaultState = CategoryState(),
            input = input,
            stateSerializer = CategoryState.serializer(),
            outputConsumer = outputConsumer,
            storeKey = COMPONENT_KEY,
        )

        private companion object Companion {
            const val COMPONENT_KEY = "ANALYTICS_CATEGORY_STORE_KEY"
        }
    }
}
