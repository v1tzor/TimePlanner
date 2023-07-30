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

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.editor.api.navigations.EditorFeatureStarter
import ru.aleshin.features.editor.impl.navigation.EditorFeatureStarterImpl
import ru.aleshin.features.editor.impl.navigation.NavigationManager
import ru.aleshin.features.editor.impl.presentation.ui.editor.EditorScreen
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.EditorWorkProcessor
import ru.aleshin.features.editor.impl.presentation.ui.editor.processors.TimeTaskWorkProcessor
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.CategoryValidator
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.EditorEffectCommunicator
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.EditorScreenModel
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.EditorStateCommunicator
import ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel.TimeRangeValidator

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Module
internal interface PresentationModule {

    @Binds
    @FeatureScope
    fun bindEditorFeatureStarter(starter: EditorFeatureStarterImpl): EditorFeatureStarter

    @Binds
    @FeatureScope
    fun bindNavigationManager(manager: NavigationManager.Base): NavigationManager

    @Binds
    @FeatureScope
    fun bindEditorScreen(screen: EditorScreen): Screen

    @Binds
    fun bindEditorScreenModel(screenModel: EditorScreenModel): ScreenModel

    @Binds
    @FeatureScope
    fun bindEditorStateCommunicator(communicator: EditorStateCommunicator.Base): EditorStateCommunicator

    @Binds
    @FeatureScope
    fun bindEditorEffectCommunicator(communicator: EditorEffectCommunicator.Base): EditorEffectCommunicator

    @Binds
    fun bindTimeTaskWorkProcessor(processor: TimeTaskWorkProcessor.Base): TimeTaskWorkProcessor

    @Binds
    fun bindEditorWorkProcessor(processor: EditorWorkProcessor.Base): EditorWorkProcessor

    @Binds
    fun bindTimeRangeValidator(validator: TimeRangeValidator.Base): TimeRangeValidator

    @Binds
    fun bindCategoryValidator(validator: CategoryValidator.Base): CategoryValidator
}
