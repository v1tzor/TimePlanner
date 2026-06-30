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
package ru.aleshin.features.editor.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.editor.api.EditorFeatureComponentFactory
import ru.aleshin.features.editor.impl.navigation.DefaultEditorComponentFactory
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorState
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.EditorWorkProcessor
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.TimeTaskWorkProcessor
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.CategoryValidator
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.EditorComposeStore
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.TimeRangeValidator

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Module
internal interface PresentationModule {

    @Binds
    @FeatureScope
    fun bindEditorComponentFactory(factory: DefaultEditorComponentFactory): EditorFeatureComponentFactory

    @Binds
    @FeatureScope
    fun bindEditorStoreFactory(factory: EditorComposeStore.Factory): BaseComposeStore.Factory<EditorComposeStore, EditorState>

    @Binds
    @FeatureScope
    fun bindTimeTaskWorkProcessor(processor: TimeTaskWorkProcessor.Base): TimeTaskWorkProcessor

    @Binds
    @FeatureScope
    fun bindEditorWorkProcessor(processor: EditorWorkProcessor.Base): EditorWorkProcessor

    @Binds
    @FeatureScope
    fun bindTimeRangeValidator(validator: TimeRangeValidator.Base): TimeRangeValidator

    @Binds
    @FeatureScope
    fun bindCategoryValidator(validator: CategoryValidator.Base): CategoryValidator
}
