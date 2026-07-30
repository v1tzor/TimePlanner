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
package ru.aleshin.features.home.impl.presentation.ui.home.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.timeplanner.core.ui.theme.material.full

/**
 * @author Stanislav Aleshin on 22.02.2023.
 */
@Composable
internal fun DateChooser(
    modifier: Modifier = Modifier,
    dateTitle: String,
    enabled: Boolean = true,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onChooseDate: () -> Unit,
) {
    Surface(
        modifier = modifier.height(38.dp),
        shape = MaterialTheme.shapes.full,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DateChooserIcon(
                icon = painterResource(HomeThemeRes.icons.previousDate),
                description = HomeThemeRes.strings.previousDateIconDesc,
                enabled = enabled,
                onClick = onPrevious,
            )
            DateChooserContent(
                modifier = Modifier.weight(1f),
                dateTitle = dateTitle,
                enabled = enabled,
                onClick = onChooseDate,
            )
            DateChooserIcon(
                icon = painterResource(HomeThemeRes.icons.nextDate),
                description = HomeThemeRes.strings.nextDateIconDesc,
                enabled = enabled,
                onClick = onNext,
            )
        }
    }
}

@Composable
private fun DateChooserIcon(
    modifier: Modifier = Modifier,
    icon: Painter,
    description: String?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier.size(38.dp),
        enabled = enabled,
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier
                .size(14.dp)
                .graphicsLayer(alpha = if (enabled) 1f else 0.5f),
            painter = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun DateChooserContent(
    modifier: Modifier = Modifier,
    dateTitle: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .graphicsLayer(alpha = if (enabled) 1f else 0.5f),
            text = dateTitle,
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
