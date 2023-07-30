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

import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.navigation.TabRouter
import ru.aleshin.features.analytics.api.navigation.AnalyticsFeatureStarter
import ru.aleshin.features.home.api.navigation.HomeFeatureStarter
import ru.aleshin.features.home.api.navigation.HomeScreens
import ru.aleshin.features.settings.api.navigation.SettingsFeatureStarter
import javax.inject.Inject
import javax.inject.Provider

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
interface TabNavigationManager {

    fun showHomeFeature(screen: HomeScreens?, isRoot: Boolean = false)
    fun showAnalyticsFeature()
    fun showSettingsFeature()

    class Base @Inject constructor(
        private val homeFeatureStarter: Provider<HomeFeatureStarter>,
        private val analyticsFeatureStarter: Provider<AnalyticsFeatureStarter>,
        private val settingsFeatureStarter: Provider<SettingsFeatureStarter>,
        private val router: TabRouter,
    ) : TabNavigationManager {

        override fun showHomeFeature(screen: HomeScreens?, isRoot: Boolean) = showTab(
            screen = homeFeatureStarter.get().provideHomeScreen(screen, isRoot),
        )

        override fun showAnalyticsFeature() = showTab(
            screen = analyticsFeatureStarter.get().provideMainScreen(),
        )

        override fun showSettingsFeature() = showTab(
            screen = settingsFeatureStarter.get().provideMainScreen(),
        )

        private fun showTab(screen: Screen) = router.showTab(screen)
    }
}
