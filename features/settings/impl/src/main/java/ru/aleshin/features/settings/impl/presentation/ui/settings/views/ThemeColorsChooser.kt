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
package ru.aleshin.features.settings.impl.presentation.ui.settings.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes
import ru.aleshin.timeplanner.core.ui.theme.material.ThemeUiType
import ru.aleshin.timeplanner.core.ui.views.SegmentedButtonItem
import ru.aleshin.timeplanner.core.ui.views.SegmentedButtons

/**
 * @author Stanislav Aleshin on 23.02.2023.
 */
@Composable
internal fun ThemeColorsChooser(
    modifier: Modifier = Modifier,
    themeColors: ThemeUiType,
    onThemeColorUpdate: (ThemeUiType) -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsItemIcon(
            icon = SettingsThemeRes.icons.palette,
            contentDescription = null,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = SettingsThemeRes.strings.mainSettingsThemeTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            SegmentedButtons(
                modifier = Modifier.fillMaxWidth(),
                items = ThemeColorsTypeSegmentedItems.entries.toTypedArray(),
                selectedItem = themeColors.toSegmentedItem(),
                onItemClick = { onThemeColorUpdate.invoke(it.toThemeColorsType()) },
            )
        }
    }
}

internal enum class ThemeColorsTypeSegmentedItems : SegmentedButtonItem {
    LIGHT {
        override val title: String @Composable get() = SettingsThemeRes.strings.lightThemeTitle
    },
    DEFAULT {
        override val title: String @Composable get() = SettingsThemeRes.strings.systemThemeTitle
    },
    DARK {
        override val title: String @Composable get() = SettingsThemeRes.strings.darkThemeTitle
    },
}

internal fun ThemeUiType.toSegmentedItem() = when (this) {
    ThemeUiType.DEFAULT -> ThemeColorsTypeSegmentedItems.DEFAULT
    ThemeUiType.LIGHT -> ThemeColorsTypeSegmentedItems.LIGHT
    ThemeUiType.DARK -> ThemeColorsTypeSegmentedItems.DARK
}

internal fun ThemeColorsTypeSegmentedItems.toThemeColorsType() = when (this) {
    ThemeColorsTypeSegmentedItems.LIGHT -> ThemeUiType.LIGHT
    ThemeColorsTypeSegmentedItems.DEFAULT -> ThemeUiType.DEFAULT
    ThemeColorsTypeSegmentedItems.DARK -> ThemeUiType.DARK
}
