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
package ru.aleshin.features.editor.impl.presentation.ui.goal.store

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.ChildComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.component.saveableStore
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalInput
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalOutput
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalState

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal abstract class GoalComponent(
    componentContext: ComponentContext,
) : ChildComponent(componentContext) {

    abstract val store: GoalComposeStore

    class Default(
        storeFactory: GoalComposeStore.Factory,
        inputData: GoalInput,
        componentContext: ComponentContext,
        outputConsumer: OutputConsumer<GoalOutput>,
    ) : GoalComponent(componentContext) {

        override val store by saveableStore(
            storeFactory = storeFactory,
            input = inputData,
            defaultState = GoalState(),
            stateSerializer = GoalState.serializer(),
            outputConsumer = outputConsumer,
            storeKey = COMPONENT_KEY,
        )
    }

    private companion object {
        const val COMPONENT_KEY = "GOAL_EDITOR_STORE_KEY"
    }
}
