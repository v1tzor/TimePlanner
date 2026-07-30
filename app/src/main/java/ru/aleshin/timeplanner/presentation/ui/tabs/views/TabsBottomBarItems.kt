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

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.views.BottomBarItem

/**
 * @author Stanislav Aleshin on 19.02.2023.
 */
enum class TabsBottomBarItems : BottomBarItem {
    HOME {
        override val label: String @Composable get() = TimePlannerRes.strings.homeTabTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.selectedHomeTab
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.homeTab
        override val containerColor: Color @Composable get() = MaterialTheme.colorScheme.surfaceContainerLow
    },
    OVERVIEW {
        override val label: String @Composable get() = TimePlannerRes.strings.overviewDrawerTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.selectedOverviewTab
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.overviewTab
    },
    TEMPLATES {
        override val label: String @Composable get() = TimePlannerRes.strings.templateDrawerTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.selectedTemplateTab
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.templateTab
    },
    ANALYTICS {
        override val label: String @Composable get() = TimePlannerRes.strings.analyticsTabTitle
        override val enabledIcon: Int @Composable get() = TimePlannerRes.icons.selectedAnalyticsTab
        override val disabledIcon: Int @Composable get() = TimePlannerRes.icons.analyticsTab
    },
}