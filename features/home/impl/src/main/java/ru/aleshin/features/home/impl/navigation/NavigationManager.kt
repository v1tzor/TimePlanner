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
package ru.aleshin.features.home.impl.navigation

import ru.aleshin.core.utils.navigation.Router
import ru.aleshin.features.editor.api.navigations.EditorFeatureStarter
import ru.aleshin.features.editor.api.navigations.EditorScreens
import ru.aleshin.features.home.api.navigation.HomeScreens
import ru.aleshin.features.home.impl.di.annontation.LocalRouter
import ru.aleshin.features.home.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.home.impl.presentation.ui.categories.CategoriesScreen
import ru.aleshin.features.home.impl.presentation.ui.details.DetailsScreen
import ru.aleshin.features.home.impl.presentation.ui.home.HomeScreen
import ru.aleshin.features.home.impl.presentation.ui.overview.OverviewScreen
import ru.aleshin.features.home.impl.presentation.ui.templates.TemplatesScreen
import javax.inject.Inject
import javax.inject.Provider

/**
 * @author Stanislav Aleshin on 23.02.2023.
 */
internal interface NavigationManager {

    fun navigateToLocal(screen: HomeScreens?, isRoot: Boolean = true)
    fun navigateToEditorFeature(screen: EditorScreens)
    fun navigateToLocalBack()

    class Base @Inject constructor(
        private val scheduleInteractor: ScheduleInteractor,
        @LocalRouter private val localRouter: Router,
        private val globalRouter: Router,
        private val editorFeatureStarter: Provider<EditorFeatureStarter>,
    ) : NavigationManager {

        override fun navigateToLocal(screen: HomeScreens?, isRoot: Boolean) = with(localRouter) {
            val screenInstance = when (screen) {
                is HomeScreens.Overview -> OverviewScreen()
                is HomeScreens.Home -> HomeScreen().apply {
                    scheduleInteractor.setFeatureScheduleDate(screen.scheduleDate)
                }
                is HomeScreens.Details -> DetailsScreen()
                is HomeScreens.Templates -> TemplatesScreen()
                is HomeScreens.Categories -> CategoriesScreen()
                else -> return@with
            }
            if (isRoot) replaceTo(screenInstance, true) else navigateTo(screenInstance)
        }

        override fun navigateToEditorFeature(screen: EditorScreens) {
            val editorNavScreen = editorFeatureStarter.get().provideEditorScreen(screen)
            globalRouter.navigateTo(editorNavScreen)
        }

        override fun navigateToLocalBack() = localRouter.navigateBack()
    }
}
