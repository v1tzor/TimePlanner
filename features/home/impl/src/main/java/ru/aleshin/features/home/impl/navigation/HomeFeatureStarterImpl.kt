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

import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.features.home.api.navigation.HomeFeatureStarter
import ru.aleshin.features.home.api.navigation.HomeScreens
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
internal class HomeFeatureStarterImpl @Inject constructor(
    private val localNavScreen: Screen,
    private val navigationManager: NavigationManager,
) : HomeFeatureStarter {

    override fun provideHomeScreen(
        navScreen: HomeScreens?,
        isRoot: Boolean,
    ) = navigationManager.navigateToLocal(screen = navScreen, isRoot = isRoot).let {
        return@let localNavScreen
    }
}
