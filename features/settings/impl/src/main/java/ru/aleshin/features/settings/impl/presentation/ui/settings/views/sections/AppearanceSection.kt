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
package ru.aleshin.features.settings.impl.presentation.ui.settings.views.sections

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes
import ru.aleshin.features.settings.impl.presentation.ui.common.SettingsItemIcon
import ru.aleshin.features.settings.impl.presentation.ui.common.SettingsSection
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.LanguageChooser
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.ThemeColorsChooser
import ru.aleshin.timeplanner.core.ui.theme.material.ColorsUiType
import ru.aleshin.timeplanner.core.ui.theme.material.ThemeUiType
import ru.aleshin.timeplanner.core.ui.theme.tokens.LanguageUiType

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
@Composable
internal fun AppearanceSection(
    modifier: Modifier = Modifier,
    languageType: LanguageUiType,
    themeColors: ThemeUiType,
    colorsType: ColorsUiType,
    dynamicColor: Boolean,
    onLanguageChange: (LanguageUiType) -> Unit,
    onThemeColorUpdate: (ThemeUiType) -> Unit,
    onColorsTypeUpdate: (ColorsUiType) -> Unit,
    onDynamicColorsChange: (Boolean) -> Unit,
) {
    SettingsSection(
        modifier = modifier,
        title = SettingsThemeRes.strings.mainSettingsTitle,
    ) {
        ThemeColorsChooser(
            modifier = Modifier.fillMaxWidth(),
            themeColors = themeColors,
            onThemeColorUpdate = onThemeColorUpdate,
        )
        HorizontalDivider()
        ColorsTypeChooser(
            modifier = Modifier.fillMaxWidth(),
            colorsType = colorsType,
            onChoose = onColorsTypeUpdate,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            HorizontalDivider()
            DynamicColorChooser(
                dynamicColor = dynamicColor,
                onChange = onDynamicColorsChange,
            )
        }
        HorizontalDivider()
        LanguageChooser(
            language = languageType,
            onLanguageChanged = onLanguageChange,
        )
    }
}

@Composable
private fun ColorsTypeChooser(
    modifier: Modifier = Modifier,
    colorsType: ColorsUiType,
    onChoose: (ColorsUiType) -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsItemIcon(
            icon = SettingsThemeRes.icons.colorize,
            contentDescription = null,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = SettingsThemeRes.strings.mainSettingsColorsTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                for (color in ColorsUiType.entries) {
                    ColorTypeItem(
                        model = color,
                        selected = colorsType == color,
                        onClick = { if (colorsType != color) onChoose(color) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorTypeItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    model: ColorsUiType,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        enabled = enabled,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            shape = CircleShape,
            color = model.seed(),
        ) {
            if (selected) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = model.fetchLightColorScheme().onPrimary,
                    )
                }
            }
        }
    }
}


@Composable
private fun DynamicColorChooser(
    modifier: Modifier = Modifier,
    dynamicColor: Boolean,
    onChange: (Boolean) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Row(
            modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsItemIcon(
                icon = SettingsThemeRes.icons.waterDrop,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 16.dp).weight(1f),
                text = SettingsThemeRes.strings.mainSettingsDynamicColorTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Switch(checked = dynamicColor, onCheckedChange = onChange)
        }
    }
}