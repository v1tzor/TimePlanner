/*
 * Copyright 2026 Stanislav Aleshin
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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.router.stack.ChildStack as DecomposeChildStack
import ru.aleshin.timeplanner.core.ui.views.BottomBarIcon
import ru.aleshin.timeplanner.core.ui.views.BottomBarLabel
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponent
import ru.aleshin.timeplanner.presentation.ui.tabs.views.TabsBottomBarItems
import ru.aleshin.timeplanner.presentation.ui.tabs.views.TabsBottomNavigationBar

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
@Composable
@OptIn(ExperimentalDecomposeApi::class, ExperimentalMaterial3Api::class)
internal fun TabNavigationLayout(
    modifier: Modifier = Modifier,
    stack: DecomposeChildStack<*, TabNavigationComponent.TabNavigationChild>,
    selectedItem: TabsBottomBarItems,
    useNavigationRail: Boolean,
    onItemSelect: (TabsBottomBarItems) -> Unit,
) {
    val layoutType = if (useNavigationRail) {
        NavigationSuiteType.NavigationRail
    } else {
        NavigationSuiteType.None
    }
    val navigationItemColors = NavigationSuiteDefaults.itemColors(
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    )

    NavigationSuiteScaffold(
        modifier = modifier,
        layoutType = layoutType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationRailContainerColor = selectedItem.containerColor,
        ),
        navigationSuiteItems = {
            TabsBottomBarItems.entries.forEach { destination ->
                item(
                    selected = selectedItem == destination,
                    onClick = { onItemSelect(destination) },
                    icon = {
                        BottomBarIcon(
                            selected = selectedItem == destination,
                            enabledIcon = painterResource(destination.enabledIcon),
                            disabledIcon = painterResource(destination.disabledIcon),
                            description = destination.label,
                        )
                    },
                    label = {
                        BottomBarLabel(
                            selected = selectedItem == destination,
                            title = destination.label,
                        )
                    },
                    colors = navigationItemColors,
                )
            }
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ChildStack(
                modifier = Modifier.weight(1f),
                stack = stack,
            ) { child ->
                when (val instance = child.instance) {
                    is TabNavigationComponent.TabNavigationChild.HomeChild -> {
                        instance.contentProvider.invoke(Modifier)
                    }
                    is TabNavigationComponent.TabNavigationChild.OverviewChild -> {
                        instance.contentProvider.invoke(Modifier)
                    }
                    is TabNavigationComponent.TabNavigationChild.TemplatesChild -> {
                        instance.contentProvider.invoke(Modifier)
                    }
                    is TabNavigationComponent.TabNavigationChild.AnalyticsChild -> {
                        instance.contentProvider.invoke(Modifier)
                    }
                }
            }
            if (!useNavigationRail) {
                TabsBottomNavigationBar(
                    selectedItem = selectedItem,
                    onItemSelected = onItemSelect,
                )
            }
        }
    }
}
