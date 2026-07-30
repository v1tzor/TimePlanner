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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes
import ru.aleshin.features.settings.impl.presentation.ui.common.SettingsItemIcon
import ru.aleshin.features.settings.impl.presentation.ui.common.SettingsSection

/**
 * @author Stanislav Aleshin on 29.06.2026.
 */
@Composable
internal fun SecureSection(
    modifier: Modifier = Modifier,
    secureMode: Boolean,
    onUpdateSecureMode: (Boolean) -> Unit,
) {
    SettingsSection(
        modifier = modifier,
        title = SettingsThemeRes.strings.secureSectionHeader,
    ) {
        SecureScreenChooser(
            secureMode = secureMode,
            onUpdateSecureMode = onUpdateSecureMode
        )
    }
}

@Composable
private fun SecureScreenChooser(
    modifier: Modifier = Modifier,
    secureMode: Boolean,
    onUpdateSecureMode: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsItemIcon(
            icon = SettingsThemeRes.icons.lock,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(start = 16.dp).weight(1f),
            text = SettingsThemeRes.strings.secureModeTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
        Switch(checked = secureMode, onCheckedChange = onUpdateSecureMode)
    }
}