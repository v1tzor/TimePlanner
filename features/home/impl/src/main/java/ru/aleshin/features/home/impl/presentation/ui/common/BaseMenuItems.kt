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
package ru.aleshin.features.home.impl.presentation.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 04.08.2023.
 */
@Composable
internal fun CheckedMenuItem(
    modifier: Modifier = Modifier,
    text: String,
    check: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) = DropdownMenuItem(
    modifier = modifier,
    onClick = {},
    enabled = false,
    leadingIcon = {
        Checkbox(
            modifier = Modifier.size(32.dp),
            checked = check,
            onCheckedChange = onCheckedChange,
        )
    },
    text = {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
    },
)

@Composable
internal fun BackMenuItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) = DropdownMenuItem(
    modifier = modifier.alpha(if (enabled) 1f else 0.6f),
    enabled = enabled,
    onClick = onClick,
    leadingIcon = {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = HomeThemeRes.strings.navToBackTitle,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    },
    text = {
        Text(
            text = HomeThemeRes.strings.navToBackTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
    },
)

@Composable
internal fun NavMenuItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onClick: () -> Unit,
) = DropdownMenuItem(
    modifier = modifier.alpha(if (enabled) 1f else 0.6f),
    enabled = enabled,
    onClick = onClick,
    trailingIcon = {
        Icon(
            painter = painterResource(HomeThemeRes.icons.menuNavArrow),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    },
    text = {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
    },
)
