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
package ru.aleshin.features.editor.impl.navigation

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.editor.api.EditorContentProviderFactory
import ru.aleshin.features.editor.api.EditorOutput
import ru.aleshin.features.editor.impl.presentation.ui.categories.store.CategoriesComposeStore
import ru.aleshin.features.editor.impl.presentation.ui.root.EditorContentProvider
import ru.aleshin.features.editor.impl.presentation.ui.root.InternalEditorFeatureComponent
import ru.aleshin.features.editor.impl.presentation.ui.task.store.TaskComposeStore
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
internal class DefaultEditorContentProviderFactory @Inject constructor(
    private val taskStoreFactory: TaskComposeStore.Factory,
    private val categoriesStoreFactory: CategoriesComposeStore.Factory,
) : EditorContentProviderFactory {

    override fun createProvider(
        componentContext: ComponentContext,
        startConfig: EditorConfig,
        outputConsumer: OutputConsumer<EditorOutput>
    ): FeatureContentProvider {
        val component = InternalEditorFeatureComponent.Default(
            componentContext = componentContext,
            startConfig = startConfig,
            outputConsumer = outputConsumer,
            taskStoreFactory = taskStoreFactory,
            categoriesStoreFactory = categoriesStoreFactory,
        )

        return EditorContentProvider(component = component)
    }
}
