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
package ru.aleshin.timeplanner.presentation.ui.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import ru.aleshin.core.utils.managers.rememberDrawerManager
import ru.aleshin.core.utils.navigation.navigator.TabNavigator
import ru.aleshin.core.utils.navigation.navigator.rememberNavigatorManager
import ru.aleshin.core.utils.platform.screen.ScreenContent
import ru.aleshin.timeplanner.application.fetchAppComponent
import ru.aleshin.timeplanner.presentation.ui.tabs.contract.TabsEvent
import ru.aleshin.timeplanner.presentation.ui.tabs.contract.TabsViewState
import ru.aleshin.timeplanner.presentation.ui.tabs.screenmodel.rememberTabsScreenModel
import ru.aleshin.timeplanner.presentation.ui.tabs.views.HomeDrawerItems
import ru.aleshin.timeplanner.presentation.ui.tabs.views.HomeNavigationDrawer
import ru.aleshin.timeplanner.presentation.ui.tabs.views.TabsBottomBarItems
import ru.aleshin.timeplanner.presentation.ui.tabs.views.TabsBottomNavigationBar

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
class TabsScreen : Screen {

    @Composable
    override fun Content() = ScreenContent(rememberTabsScreenModel(), TabsViewState()) {
        val state = fetchState()
        val appComponent = fetchAppComponent()
        val navigatorManager = rememberNavigatorManager { appComponent.fetchTabNavigatorManager() }
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val drawerManager = rememberDrawerManager(drawerState)

        TabNavigator(navigatorManager = navigatorManager) {
            HomeNavigationDrawer(
                drawerState = drawerState,
                drawerManager = drawerManager,
                isAlwaysSelected = state.bottomBarItem != TabsBottomBarItems.HOME,
                onItemSelected = { item ->
                    val event = when (item) {
                        HomeDrawerItems.MAIN -> TabsEvent.SelectedMainScreen
                        HomeDrawerItems.TEMPLATES -> TabsEvent.SelectedTemplateScreen
                        HomeDrawerItems.CATEGORIES -> TabsEvent.SelectedCategoriesScreen
                    }
                    dispatchEvent(event)
                },
            ) {
                Scaffold(
                    content = { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            CurrentScreen()
                        }
                    },
                    bottomBar = {
                        TabsBottomNavigationBar(
                            selectedItem = state.bottomBarItem,
                            onItemSelected = { tab ->
                                val event = when (tab) {
                                    TabsBottomBarItems.HOME -> TabsEvent.SelectedHomeTab
                                    TabsBottomBarItems.ANALYTICS -> TabsEvent.SelectedAnalyticsTab
                                    TabsBottomBarItems.SETTINGS -> TabsEvent.SelectedSettingsTab
                                }
                                dispatchEvent(event)
                            },
                        )
                    },
                )
            }
        }
    }
}
