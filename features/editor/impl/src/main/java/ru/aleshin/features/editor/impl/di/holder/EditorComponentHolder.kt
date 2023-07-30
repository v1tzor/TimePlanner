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
package ru.aleshin.features.editor.impl.di.holder

import ru.aleshin.features.editor.api.di.EditorFeatureApi
import ru.aleshin.features.editor.impl.di.EditorFeatureDependencies
import ru.aleshin.features.editor.impl.di.component.EditorComponent
import ru.aleshin.module_injector.BaseComponentHolder

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
object EditorComponentHolder : BaseComponentHolder<EditorFeatureApi, EditorFeatureDependencies> {

    private var component: EditorComponent? = null

    override fun init(dependencies: EditorFeatureDependencies) {
        if (component == null) component = EditorComponent.create(dependencies)
    }

    override fun fetchApi(): EditorFeatureApi = fetchComponent()

    override fun clear() {
        component = null
    }

    internal fun fetchComponent() = checkNotNull(component) {
        "Editor Feature is not initialized"
    }
}
