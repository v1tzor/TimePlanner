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
import com.arkivanov.essenty.instancekeeper.getOrCreate
import ru.aleshin.features.editor.api.EditorDecomposeFeatureFactory
import ru.aleshin.features.editor.api.EditorFeatureApi
import ru.aleshin.features.editor.impl.di.EditorFeatureDependencies
import ru.aleshin.features.editor.impl.di.component.EditorComponent
import ru.aleshin.features.editor.impl.di.holder.EditorFeatureController

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
public class DefaultEditorFeatureFactory(
    private val dependenciesFactory: () -> EditorFeatureDependencies,
) : EditorDecomposeFeatureFactory {

    override fun createOrGetFeature(context: ComponentContext): EditorFeatureApi {
        return context.instanceKeeper.getOrCreate(key = EDITOR_FEATURE_CONTROLLER_KEY) {
            EditorFeatureController(
                component = EditorComponent.create(dependenciesFactory())
            )
        }.fetchApi()
    }

    private companion object {
        const val EDITOR_FEATURE_CONTROLLER_KEY = "EDITOR_FEATURE_CONTROLLER_KEY"
    }
}
