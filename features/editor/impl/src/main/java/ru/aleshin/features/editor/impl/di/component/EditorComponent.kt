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
 * imitations under the License.
 */
package ru.aleshin.features.editor.impl.di.component

import dagger.Component
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.editor.api.di.EditorFeatureApi
import ru.aleshin.features.editor.impl.di.EditorFeatureDependencies
import ru.aleshin.features.editor.impl.di.modules.DataModule
import ru.aleshin.features.editor.impl.di.modules.DomainModule
import ru.aleshin.features.editor.impl.di.modules.PresentationModule
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.EditorScreenModel

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@FeatureScope
@Component(
    dependencies = [EditorFeatureDependencies::class],
    modules = [DataModule::class, DomainModule::class, PresentationModule::class],
)
internal interface EditorComponent : EditorFeatureApi {

    fun fetchEditorScreenModel(): EditorScreenModel

    @Component.Builder
    interface Builder {
        fun dependencies(deps: EditorFeatureDependencies): Builder
        fun build(): EditorComponent
    }

    companion object {
        fun create(deps: EditorFeatureDependencies): EditorComponent {
            return DaggerEditorComponent.builder()
                .dependencies(deps)
                .build()
        }
    }
}
