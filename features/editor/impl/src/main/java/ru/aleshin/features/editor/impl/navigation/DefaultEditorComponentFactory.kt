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
package ru.aleshin.features.editor.impl.navigation

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.features.editor.api.EditorFeatureComponent
import ru.aleshin.features.editor.api.EditorFeatureComponent.EditorConfig
import ru.aleshin.features.editor.api.EditorFeatureComponent.EditorOutput
import ru.aleshin.features.editor.api.EditorFeatureComponentFactory
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.EditorComposeStore
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.InternalEditorFeatureComponent
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal class DefaultEditorComponentFactory @Inject constructor(
    private val editorStoreFactory: EditorComposeStore.Factory,
) : EditorFeatureComponentFactory {

    override fun createComponent(
        componentContext: ComponentContext,
        startConfig: StartFeatureConfig<EditorConfig>,
        outputConsumer: OutputConsumer<EditorOutput>
    ): EditorFeatureComponent {
        return InternalEditorFeatureComponent.Default(
            componentContext = componentContext,
            startConfig = startConfig,
            outputConsumer = outputConsumer,
            editorStoreFactory = editorStoreFactory,
        )
    }
}