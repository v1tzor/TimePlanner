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
package ru.aleshin.timeplanner.navigation

import ru.aleshin.core.utils.navigation.Router
import ru.aleshin.features.editor.api.navigations.EditorFeatureStarter
import ru.aleshin.features.editor.api.navigations.EditorScreens
import ru.aleshin.timeplanner.presentation.ui.tabs.TabsScreen
import javax.inject.Inject
import javax.inject.Provider

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface GlobalNavigationManager {

    fun showTabScreen()

    fun navigateToEditorFeature(screen: EditorScreens)

    class Base @Inject constructor(
        private val globalRouter: Router,
        private val editorFeatureStarter: Provider<EditorFeatureStarter>,
    ) : GlobalNavigationManager {

        override fun showTabScreen() {
            globalRouter.replaceTo(TabsScreen())
        }

        override fun navigateToEditorFeature(screen: EditorScreens) {
            val editorNavScreen = editorFeatureStarter.get().provideEditorScreen(screen)
            globalRouter.navigateTo(editorNavScreen)
        }
    }
}
