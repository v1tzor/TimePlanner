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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * @author Stanislav Aleshin on 18.07.2026.
 */
@Composable
internal fun SettingsItemIcon(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
) {
    Surface(
        modifier = modifier.size(48.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = containerColor,
    ) {
        Icon(
            modifier = Modifier.padding(12.dp),
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = iconColor,
        )
    }
}

@Composable
internal fun SettingsItemDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}
