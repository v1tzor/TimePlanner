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
package ru.aleshin.core.ui.views

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import ru.aleshin.core.ui.theme.TimePlannerRes

/**
 * @author Stanislav Aleshin on 19.02.2023.
 */
@Composable
fun <Item : BottomBarItem> BottomNavigationBar(
    modifier: Modifier,
    items: Array<Item>,
    selectedItem: Item,
    onItemSelected: (Item) -> Unit,
    showLabel: Boolean,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = TimePlannerRes.elevations.levelZero,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item,
                onClick = { onItemSelected.invoke(item) },
                icon = {
                    BottomBarIcon(
                        selected = selectedItem == item,
                        enabledIcon = painterResource(id = item.enabledIcon),
                        disabledIcon = painterResource(id = item.disabledIcon),
                        description = item.label,
                    )
                },
                label = if (showLabel) {
                    {
                        BottomBarLabel(
                            selected = selectedItem == item,
                            title = item.label,
                        )
                    }
                } else {
                    null
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}

@Composable
fun BottomBarIcon(
    selected: Boolean,
    enabledIcon: Painter,
    disabledIcon: Painter,
    description: String,
) {
    val icon = if (selected) enabledIcon else disabledIcon
    val tint = when (selected) {
        true -> MaterialTheme.colorScheme.onSecondaryContainer
        false -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Icon(
        painter = icon,
        contentDescription = description,
        tint = tint,
    )
}

@Composable
fun BottomBarLabel(
    selected: Boolean,
    title: String,
) {
    val color = when (selected) {
        true -> MaterialTheme.colorScheme.onSurface
        false -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = title,
        color = color,
        style = MaterialTheme.typography.labelMedium,
    )
}

interface BottomBarItem {
    val label: String @Composable get
    val enabledIcon: Int @Composable get
    val disabledIcon: Int @Composable get
}
