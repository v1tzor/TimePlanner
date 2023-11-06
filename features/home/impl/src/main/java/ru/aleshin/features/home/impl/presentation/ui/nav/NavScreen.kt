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
package ru.aleshin.features.home.impl.presentation.ui.nav

import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import kotlinx.coroutines.launch
import ru.aleshin.core.utils.managers.LocalDrawerManager
import ru.aleshin.core.utils.navigation.navigator.AppNavigator
import ru.aleshin.core.utils.navigation.navigator.rememberNavigatorManager
import ru.aleshin.features.home.impl.di.holder.HomeComponentHolder
import ru.aleshin.features.home.impl.presentation.theme.HomeTheme
import ru.aleshin.features.home.impl.presentation.ui.categories.CategoriesScreen
import ru.aleshin.features.home.impl.presentation.ui.details.DetailsScreen
import ru.aleshin.features.home.impl.presentation.ui.home.HomeScreen
import ru.aleshin.features.home.impl.presentation.ui.overview.OverviewScreen
import ru.aleshin.features.home.impl.presentation.ui.templates.TemplatesScreen
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 27.02.2023.
 */
internal class NavScreen @Inject constructor() : Screen {

    @Composable
    override fun Content() = HomeTheme {
        val component = HomeComponentHolder.fetchComponent()
        val navigatorManager = rememberNavigatorManager { component.fetchLocalNavigatorManager() }
        val scope = rememberCoroutineScope()
        val drawerManager = LocalDrawerManager.current

        AppNavigator(
            navigatorManager = navigatorManager,
            onBackPressed = {
                when (drawerManager?.drawerValue?.value == DrawerValue.Open) {
                    true -> { scope.launch { drawerManager?.closeDrawer() }; false }
                    false -> { true }
                }
            },
        ) { navigator ->
            val screenIndex = fetchFeatureScreenIndex(navigator.lastItem)
            drawerManager?.selectedItem?.tryEmit(screenIndex)
            CurrentScreen()
        }
    }
}

internal fun fetchFeatureScreenIndex(screen: Screen) = when (screen) {
    is HomeScreen -> 0
    is OverviewScreen -> 1
    is DetailsScreen -> 1
    is TemplatesScreen -> 2
    is CategoriesScreen -> 3
    else -> 0
}
