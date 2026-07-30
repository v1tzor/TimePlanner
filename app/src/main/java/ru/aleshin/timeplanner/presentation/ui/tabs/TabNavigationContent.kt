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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo
import ru.aleshin.timeplanner.presentation.ui.tabs.component.TabNavigationComponent
import ru.aleshin.timeplanner.presentation.ui.tabs.views.TabsBottomBarItems
import android.view.KeyEvent as AndroidKeyEvent

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Composable
fun TabNavigationContent(
    modifier: Modifier = Modifier,
    component: TabNavigationComponent,
) {
    val adaptiveLayoutInfo = rememberAdaptiveLayoutInfo()
    val stack by component.stack.subscribeAsState()
    val selectedItem = stack.active.instance.mapToBottomItem()

    TabNavigationLayout(
        modifier = modifier.onPreviewKeyEvent { event ->
            handleTabShortcut(event, component)
        },
        stack = stack,
        selectedItem = selectedItem,
        useNavigationRail = adaptiveLayoutInfo.useNavigationRail,
        onItemSelect = { item ->
            when (item) {
                TabsBottomBarItems.HOME -> component.clickHomeTab()
                TabsBottomBarItems.OVERVIEW -> component.clickOverviewTab()
                TabsBottomBarItems.TEMPLATES -> component.clickTemplatesTab()
                TabsBottomBarItems.ANALYTICS -> component.clickAnalyticsTab()
            }
        }
    )
}

private fun handleTabShortcut(
    event: KeyEvent,
    component: TabNavigationComponent,
): Boolean {
    if (event.type != KeyEventType.KeyUp || !event.isAltPressed) return false
    return when (event.nativeKeyEvent.keyCode) {
        AndroidKeyEvent.KEYCODE_1 -> true.also { component.clickHomeTab() }
        AndroidKeyEvent.KEYCODE_2 -> true.also { component.clickOverviewTab() }
        AndroidKeyEvent.KEYCODE_3 -> true.also { component.clickTemplatesTab() }
        AndroidKeyEvent.KEYCODE_4 -> true.also { component.clickAnalyticsTab() }
        else -> false
    }
}
