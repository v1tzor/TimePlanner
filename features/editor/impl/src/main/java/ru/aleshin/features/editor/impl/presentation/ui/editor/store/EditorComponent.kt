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
package ru.aleshin.features.editor.impl.presentation.ui.editor.store

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.component.saveableStore
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.features.editor.api.EditorFeatureComponent
import ru.aleshin.features.editor.impl.di.holder.EditorFeatureManager
import ru.aleshin.features.editor.impl.presentation.ui.editor.EditorContentProvider
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorInput
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorState

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal abstract class InternalEditorFeatureComponent(
    componentContext: ComponentContext,
    startConfig: StartFeatureConfig<EditorConfig>,
    outputConsumer: OutputConsumer<EditorOutput>,
) : EditorFeatureComponent(
    componentContext = componentContext,
    startConfig = startConfig,
    outputConsumer = outputConsumer,
) {

    abstract val store: EditorComposeStore

    class Default(
        startConfig: StartFeatureConfig<EditorConfig>,
        componentContext: ComponentContext,
        outputConsumer: OutputConsumer<EditorOutput>,
        editorStoreFactory: EditorComposeStore.Factory,
    ) : InternalEditorFeatureComponent(
        componentContext = componentContext,
        startConfig = startConfig,
        outputConsumer = outputConsumer,
    ) {

        override val contentProvider = EditorContentProvider(this)

        override val store by saveableStore(
            storeFactory = editorStoreFactory,
            defaultState = EditorState(),
            stateSerializer = EditorState.serializer(),
            input = (startConfig.backstack?.getOrNull(0) as? EditorConfig.Editor).let {
                checkNotNull(it) { "EditorConfig is null" }
                EditorInput(
                    timeTask = it.timeTask,
                    template = it.template,
                    undefinedTaskId = it.undefinedTaskId,
                )
            },
            outputConsumer = outputConsumer,
            storeKey = STORE_KEY,
        )

        private val backCallback = BackCallback { navigateToBack() }

        private companion object {
            const val STORE_KEY = "EDITOR_STORE_STACK"
        }

        init {
            backHandler.register(backCallback)
        }

        override fun navigateToBack() {
            outputConsumer.consume(EditorOutput.NavigateToBack)
        }

        override fun onDestroyInstance() {
            EditorFeatureManager.finish()
        }
    }
}