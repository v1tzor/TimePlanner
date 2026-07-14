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
package ru.aleshin.timeplanner.presentation.ui.tabs.views

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.views.BottomBarItem
import ru.aleshin.timeplanner.core.ui.views.BottomNavigationBar
import ru.aleshin.timeplanner.presentation.ui.tabs.store.TabNavigationComponent

/**
 * @author Stanislav Aleshin on 06.05.2023.
 */
@Composable
fun TabsBottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedItem: TabsBottomBarItems,
    onItemSelected: (TabsBottomBarItems) -> Unit,
) {
    BottomNavigationBar(
        modifier = modifier.height(80.dp),
        selectedItem = selectedItem,
        items = TabsBottomBarItems.entries.toTypedArray(),
        showLabel = true,
        onItemSelected = onItemSelected,
    )
}

fun TabNavigationComponent.TabNavigationChild.mapToBottomItem() = when (this) {
    is TabNavigationComponent.TabNavigationChild.AnalyticsChild -> TabsBottomBarItems.ANALYTICS
    is TabNavigationComponent.TabNavigationChild.HomeChild -> TabsBottomBarItems.HOME
    is TabNavigationComponent.TabNavigationChild.OverviewChild -> TabsBottomBarItems.OVERVIEW
    is TabNavigationComponent.TabNavigationChild.TemplatesChild -> TabsBottomBarItems.TEMPLATES
}

enum class TabsBottomBarItems : BottomBarItem {
    HOME {
        override val label: String @Composable get() = TimePlannerRes.strings.homeTabTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.enabledHomeIcon
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.disabledHomeIcon
        override val containerColor: Color @Composable get() = MaterialTheme.colorScheme.surfaceContainerLow
    },
    OVERVIEW {
        override val label: String @Composable get() = TimePlannerRes.strings.overviewDrawerTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.overview
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.overview
    },
    TEMPLATES {
        override val label: String @Composable get() = TimePlannerRes.strings.templateDrawerTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.template
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.template
    },
    ANALYTICS {
        override val label: String @Composable get() = TimePlannerRes.strings.analyticsTabTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.enabledAnalyticsIcon
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.disabledAnalyticsIcon
    },
}
