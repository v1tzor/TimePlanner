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
package ru.aleshin.timeplanner.presentation.ui.tabs.views

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.BottomBarItem
import ru.aleshin.core.ui.views.BottomNavigationBar

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
        items = TabsBottomBarItems.values(),
        showLabel = true,
        onItemSelected = onItemSelected,
    )
}

enum class TabsBottomBarItems : BottomBarItem {
    HOME {
        override val label: String @Composable get() = TimePlannerRes.strings.homeTabTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.enabledHomeIcon
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.disabledHomeIcon
    },
    ANALYTICS {
        override val label: String @Composable get() = TimePlannerRes.strings.analyticsTabTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.enabledAnalyticsIcon
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.disabledAnalyticsIcon
    },
    SETTINGS {
        override val label: String @Composable get() = TimePlannerRes.strings.settingsTabTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.enabledSettingsIcon
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.disabledSettingsIcon
    },
}
