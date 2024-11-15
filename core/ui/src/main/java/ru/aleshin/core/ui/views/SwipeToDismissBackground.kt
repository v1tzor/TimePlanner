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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * @author Stanislav Aleshin on 19.06.2024.
 */
@Composable
@ExperimentalMaterial3Api
fun SwipeToDismissBackground(
    modifier: Modifier = Modifier,
    dismissState: SwipeToDismissBoxState,
    startToEndContent: @Composable (RowScope.() -> Unit)? = null,
    endToStartContent: @Composable (RowScope.() -> Unit)? = null,
    startToEndColor: Color? = null,
    endToStartColor: Color? = null,
    settledColor: Color = Color.Transparent,
    shape: Shape = MaterialTheme.shapes.large,
    contentPadding: PaddingValues = PaddingValues(12.dp, 8.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(12.dp),
) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> startToEndColor ?: settledColor
        SwipeToDismissBoxValue.EndToStart -> endToStartColor ?: settledColor
        SwipeToDismissBoxValue.Settled -> settledColor
    }
    val contentColor = MaterialTheme.colorScheme.contentColorFor(color)
    val textStyle = MaterialTheme.typography.titleMedium

    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalTextStyle provides textStyle,
    ) {
        Row(
            modifier = modifier.fillMaxSize().clip(shape).background(color).padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement,
        ) {
            startToEndContent?.invoke(this)
            Spacer(modifier = Modifier.weight(1f))
            endToStartContent?.invoke(this)
        }
    }
}