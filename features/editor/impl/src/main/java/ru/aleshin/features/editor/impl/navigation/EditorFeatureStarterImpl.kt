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

import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.features.editor.api.navigations.EditorFeatureStarter
import ru.aleshin.features.editor.api.navigations.EditorScreens
import ru.aleshin.features.editor.impl.domain.common.convertToEditModel
import ru.aleshin.features.editor.impl.domain.interactors.EditorInteractor
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal class EditorFeatureStarterImpl @Inject constructor(
    private val editorScreen: Screen,
    private val editorInteractor: EditorInteractor,
) : EditorFeatureStarter {

    override fun provideEditorScreen(navScreen: EditorScreens): Screen {
        if (navScreen is EditorScreens.Editor) {
            val editModel = navScreen.timeTask.convertToEditModel(navScreen.template, navScreen.undefinedTaskId)
            editorInteractor.sendEditModel(editModel)
        }
        return editorScreen
    }
}
