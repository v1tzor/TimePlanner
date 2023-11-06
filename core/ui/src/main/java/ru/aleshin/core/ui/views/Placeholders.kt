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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

/**
 * @author Stanislav Aleshin on 10.07.2023.
 */
@Composable
fun PlaceholderBox(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
) = Box(
    modifier = modifier.fillMaxWidth().placeholder(
        visible = true,
        color = color,
        shape = shape,
        highlight = PlaceholderHighlight.shimmer(
            highlightColor = MaterialTheme.colorScheme.highlightColorFor(color),
        ),
    ),
)

fun ColorScheme.highlightColorFor(mainColor: Color) = when (mainColor) {
    primary -> primaryContainer
    secondary -> secondaryContainer
    tertiary -> tertiaryContainer
    background -> surfaceContainer
    error -> errorContainer
    primaryContainer -> primary
    secondaryContainer -> secondary
    tertiaryContainer -> tertiary
    errorContainer -> error
    inverseSurface -> surfaceContainer
    surface -> surfaceContainer
    surfaceVariant -> surface
    surfaceBright -> surfaceVariant
    surfaceContainer -> surfaceContainerHighest
    surfaceContainerHigh -> surfaceContainerLow
    surfaceContainerHighest -> surfaceContainer
    surfaceContainerLow -> surfaceContainerHigh
    surfaceContainerLowest -> surfaceVariant
    else -> Color.Unspecified
}
