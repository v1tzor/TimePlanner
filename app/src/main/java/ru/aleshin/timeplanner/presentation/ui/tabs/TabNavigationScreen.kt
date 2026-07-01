/*
 * Copyright 2025 Stanislav Aleshin
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.aleshin.core.ui.views.AdaptiveContent
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponent
import ru.aleshin.timeplanner.presentation.ui.tabs.views.TabsBottomBarItems
import ru.aleshin.timeplanner.presentation.ui.tabs.views.TabsBottomNavigationBar
import ru.aleshin.timeplanner.presentation.ui.tabs.views.mapToBottomItem

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Composable
@OptIn(ExperimentalDecomposeApi::class, ExperimentalMaterial3Api::class)
fun TabNavigationContent(
    tabNavigationComponent: TabNavigationComponent,
    modifier: Modifier = Modifier,
) {
    AdaptiveContent(
        modifier = modifier,
        defaultContent = {
            Column(modifier = Modifier.fillMaxSize()) {
                val stack by tabNavigationComponent.stack.subscribeAsState()
                ChildStack(
                    modifier = Modifier.weight(1f),
                    stack = stack
                ) { child ->
                    when (val instance = child.instance) {
                        is TabNavigationComponent.TabNavigationChild.HomeChild -> {
                            instance.contentProvider.invoke(Modifier)
                        }

                        is TabNavigationComponent.TabNavigationChild.AnalyticsChild -> {
                            instance.contentProvider.invoke(Modifier)
                        }

                        is TabNavigationComponent.TabNavigationChild.SettingsChild -> {
                            instance.contentProvider.invoke(Modifier)
                        }
                    }
                }
                TabsBottomNavigationBar(
                    selectedItem = remember(stack.active) {
                        stack.active.instance.mapToBottomItem()
                    },
                    onItemSelected = { tab ->
                        when (tab) {
                            TabsBottomBarItems.HOME -> {
                                tabNavigationComponent.clickHomeTab()
                            }

                            TabsBottomBarItems.ANALYTICS -> {
                                tabNavigationComponent.clickAnalyticsTab()
                            }

                            TabsBottomBarItems.SETTINGS -> {
                                tabNavigationComponent.clickSettingsTab()
                            }
                        }
                    },
                )
            }
        }
    )
}
