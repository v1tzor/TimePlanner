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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import ru.aleshin.timeplanner.core.ui.views.BottomBarIcon
import ru.aleshin.timeplanner.core.ui.views.BottomBarLabel
import ru.aleshin.timeplanner.presentation.ui.tabs.component.TabNavigationComponent.TabNavigationChild
import ru.aleshin.timeplanner.presentation.ui.tabs.views.TabsBottomBarItems
import com.arkivanov.decompose.router.stack.ChildStack as DecomposeChildStack

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
@Composable
@OptIn(ExperimentalDecomposeApi::class, ExperimentalMaterial3Api::class)
internal fun TabNavigationLayout(
    modifier: Modifier = Modifier,
    stack: DecomposeChildStack<*, TabNavigationChild>,
    selectedItem: TabsBottomBarItems,
    useNavigationRail: Boolean,
    onItemSelect: (TabsBottomBarItems) -> Unit,
) {
    val navigationItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
        )
    )

    NavigationSuiteScaffold(
        modifier = modifier,
        layoutType = when (useNavigationRail) {
            true -> NavigationSuiteType.NavigationRail
            false -> NavigationSuiteType.NavigationBar
        },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = selectedItem.containerColor
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
                            description = destination.label
                        )
                    },
                    label = {
                        BottomBarLabel(
                            selected = selectedItem == destination,
                            title = destination.label
                        )
                    },
                    colors = navigationItemColors
                )
            }
        }
    ) {
        ChildStack(
            modifier = Modifier.fillMaxSize(),
            stack = stack,
        ) { child ->
            when (val instance = child.instance) {
                is TabNavigationChild.HomeChild -> {
                    instance.contentProvider.invoke(Modifier)
                }
                is TabNavigationChild.OverviewChild -> {
                    instance.contentProvider.invoke(Modifier)
                }
                is TabNavigationChild.TemplatesChild -> {
                    instance.contentProvider.invoke(Modifier)
                }
                is TabNavigationChild.AnalyticsChild -> {
                    instance.contentProvider.invoke(Modifier)
                }
            }
        }
    }
}

fun TabNavigationChild.mapToBottomItem() = when (this) {
    is TabNavigationChild.AnalyticsChild -> TabsBottomBarItems.ANALYTICS
    is TabNavigationChild.HomeChild -> TabsBottomBarItems.HOME
    is TabNavigationChild.OverviewChild -> TabsBottomBarItems.OVERVIEW
    is TabNavigationChild.TemplatesChild -> TabsBottomBarItems.TEMPLATES
}