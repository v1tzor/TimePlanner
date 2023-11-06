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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.material.badge

@Composable
fun CategoryIconMonogram(
    modifier: Modifier = Modifier,
    icon: Painter,
    iconDescription: String?,
    iconColor: Color,
    badgeEnabled: Boolean = false,
    backgroundColor: Color,
) = Box(
    modifier = modifier.size(40.dp),
    contentAlignment = Alignment.Center,
) {
    Box(
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(40.dp)).background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = icon,
            contentDescription = iconDescription,
            tint = iconColor,
        )
    }
    if (badgeEnabled) {
        ImportanceBadge(
            modifier = Modifier.align(Alignment.TopEnd),
            color = badge,
        )
    }
}

@Composable
fun CategoryTextMonogram(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    backgroundColor: Color,
    badgeEnabled: Boolean = false,
) = Box(
    modifier = modifier.size(40.dp),
    contentAlignment = Alignment.Center,
) {
    Box(
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(40.dp)).background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.toUpperCase(Locale.current),
            modifier = Modifier.align(Alignment.Center),
            color = textColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )
    }
    if (badgeEnabled) {
        ImportanceBadge(
            modifier = Modifier.align(Alignment.TopEnd),
            color = badge,
        )
    }
}

@Composable
fun ImportanceBadge(
    modifier: Modifier = Modifier,
    color: Color = badge,
) {
    Box(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .size(7.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(color),
    )
}
