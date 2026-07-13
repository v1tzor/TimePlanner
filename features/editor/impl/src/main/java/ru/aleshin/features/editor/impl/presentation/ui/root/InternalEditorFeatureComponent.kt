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
package ru.aleshin.features.editor.impl.presentation.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import ru.aleshin.core.utils.architecture.component.FeatureComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.editor.api.EditorOutput
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesInput
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesOutput
import ru.aleshin.features.editor.impl.presentation.ui.categories.store.CategoriesComponent
import ru.aleshin.features.editor.impl.presentation.ui.categories.store.CategoriesComposeStore
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskInput
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskOutput
import ru.aleshin.features.editor.impl.presentation.ui.task.store.TaskComponent
import ru.aleshin.features.editor.impl.presentation.ui.task.store.TaskComposeStore

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal abstract class InternalEditorFeatureComponent(
    componentContext: ComponentContext,
) : FeatureComponent<EditorConfig, EditorOutput>(
    componentContext = componentContext
) {

    abstract val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class TaskChild(val component: TaskComponent) : Child()
        data class CategoriesChild(val component: CategoriesComponent) : Child()
    }

    class Default(
        startConfig: EditorConfig,
        componentContext: ComponentContext,
        private val outputConsumer: OutputConsumer<EditorOutput>,
        private val taskStoreFactory: TaskComposeStore.Factory,
        private val categoriesStoreFactory: CategoriesComposeStore.Factory,
    ) : InternalEditorFeatureComponent(
        componentContext = componentContext
    ) {

        private val stackNavigation = StackNavigation<EditorConfig>()

        override val stack: Value<ChildStack<*, Child>> = childStack(
            source = stackNavigation,
            serializer = EditorConfig.serializer(),
            initialConfiguration = startConfig,
            key = STACK_KEY,
            handleBackButton = true,
            childFactory = ::createChild,
        )

        private companion object Companion {
            const val STACK_KEY = "EDITOR_ROOT_STACK"
        }

        override fun navigateToBack() {
            stackNavigation.pop { isPop ->
                if (!isPop) outputConsumer.consume(EditorOutput.NavigateToBack)
            }
        }

        private fun createChild(
            config: EditorConfig,
            componentContext: ComponentContext
        ): Child {
            return when (config) {
                is EditorConfig.Task -> Child.TaskChild(
                    component = TaskComponent.Default(
                        storeFactory = taskStoreFactory,
                        componentContext = componentContext,
                        inputData = TaskInput(
                            timeTaskId = config.timeTaskId,
                            timeRange = config.timeRange,
                            date = config.date,
                            undefinedTaskId = config.undefinedTaskId,
                        ),
                        outputConsumer = taskOutputConsumer(),
                    )
                )
                is EditorConfig.Categories -> Child.CategoriesChild(
                    component = CategoriesComponent.Default(
                        storeFactory = categoriesStoreFactory,
                        componentContext = componentContext,
                        inputData = CategoriesInput(mainCategoryId = config.mainCategoryId),
                        outputConsumer = categoriesOutputConsumer(),
                    )
                )
            }
        }

        private fun taskOutputConsumer() = OutputConsumer<TaskOutput> { output ->
            when (output) {
                is TaskOutput.NavigateToCategories -> {
                    stackNavigation.pushToFront(output.config)
                }
                is TaskOutput.NavigateToTemplates -> {
                    outputConsumer.consume(EditorOutput.NavigateToTemplates)
                }
                is TaskOutput.NavigateToBack -> navigateToBack()
            }
        }

        private fun categoriesOutputConsumer() = OutputConsumer<CategoriesOutput> { output ->
            when (output) {
                is CategoriesOutput.NavigateToBack -> navigateToBack()
            }
        }
    }
}
