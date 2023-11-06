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

import ru.aleshin.core.utils.navigation.Router
import ru.aleshin.core.utils.navigation.TabRouter
import ru.aleshin.features.home.api.navigation.HomeFeatureStarter
import ru.aleshin.features.home.api.navigation.HomeScreens
import javax.inject.Inject
import javax.inject.Provider

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal interface NavigationManager {

    fun navigateToHomeScreen()
    fun navigateToTemplatesScreen()
    fun navigateToBack()

    class Base @Inject constructor(
        private val tabRouter: TabRouter,
        private val globalRouter: Router,
        private val homeFeatureStarter: Provider<HomeFeatureStarter>,
    ) : NavigationManager {

        override fun navigateToHomeScreen() {
            globalRouter.navigateBack()
        }

        override fun navigateToTemplatesScreen() {
            val screen = homeFeatureStarter.get().provideHomeScreen(HomeScreens.Templates, false)
            tabRouter.showTab(screen)
            globalRouter.navigateBack()
        }

        override fun navigateToBack() {
            globalRouter.navigateBack()
        }
    }
}
