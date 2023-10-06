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
package ru.aleshin.features.settings.impl.presentation.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.material.ThemeUiType
import ru.aleshin.core.ui.views.SegmentedButtonItem
import ru.aleshin.core.ui.views.SegmentedButtons
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes

/**
 * @author Stanislav Aleshin on 23.02.2023.
 */
@Composable
internal fun ThemeColorsChooser(
    modifier: Modifier = Modifier,
    themeColors: ThemeUiType,
    onThemeColorUpdate: (ThemeUiType) -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = TimePlannerRes.elevations.levelTwo,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = SettingsThemeRes.strings.mainSettingsThemeTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            SegmentedButtons(
                modifier = Modifier.fillMaxWidth(),
                items = ThemeColorsTypeSegmentedItems.values(),
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
