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
package ru.aleshin.features.overview.impl.presentation.ui.goal.history.store

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.ChildComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.component.saveableStore
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryOutput
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryState

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal abstract class GoalsHistoryComponent(
    componentContext: ComponentContext,
) : ChildComponent(componentContext) {

    abstract val store: GoalsHistoryComposeStore

    class Default(
        storeFactory: GoalsHistoryComposeStore.Factory,
        componentContext: ComponentContext,
        outputConsumer: OutputConsumer<GoalsHistoryOutput>,
    ) : GoalsHistoryComponent(componentContext) {

        override val store by saveableStore(
            storeFactory = storeFactory,
            defaultState = GoalsHistoryState(),
            stateSerializer = GoalsHistoryState.serializer(),
            outputConsumer = outputConsumer,
            storeKey = COMPONENT_KEY,
        )
    }

    private companion object {
        const val COMPONENT_KEY = "GOALS_HISTORY_STORE_KEY"
    }
}
