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
package ru.aleshin.features.home.impl.navigation

import ru.aleshin.core.utils.navigation.Router
import ru.aleshin.features.editor.api.domain.EditModel
import ru.aleshin.features.editor.api.navigations.EditorFeatureStarter
import ru.aleshin.features.home.impl.di.annontation.LocalRouter
import ru.aleshin.features.home.impl.presentation.ui.categories.CategoriesScreen
import ru.aleshin.features.home.impl.presentation.ui.home.HomeScreen
import ru.aleshin.features.home.impl.presentation.ui.templates.TemplatesScreen
import javax.inject.Inject
import javax.inject.Provider

/**
 * @author Stanislav Aleshin on 23.02.2023.
 */
internal interface NavigationManager {

    fun navigateToEditorFeature(editModel: EditModel)
    fun navigateToHomeScreen()
    fun navigateToTemplatesScreen()
    fun navigateToCategoriesScreen()
    fun navigateToLocalBack()

    class Base @Inject constructor(
        private val editorFeatureStarter: Provider<EditorFeatureStarter>,
        private val globalRouter: Router,
        @LocalRouter private val localRouter: Router,
    ) : NavigationManager {

        override fun navigateToEditorFeature(editModel: EditModel) {
            val screen = editorFeatureStarter.get().provideMainScreen(editModel)
            globalRouter.navigateTo(screen)
        }

        override fun navigateToHomeScreen() {
            val screen = HomeScreen()
            localRouter.navigateTo(screen)
        }

        override fun navigateToTemplatesScreen() {
            val screen = TemplatesScreen()
            localRouter.navigateTo(screen)
        }

        override fun navigateToCategoriesScreen() {
            val screen = CategoriesScreen()
            localRouter.navigateTo(screen)
        }

        override fun navigateToLocalBack() = localRouter.navigateBack()
    }
}
