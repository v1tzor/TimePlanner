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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/**
 * @author Stanislav Aleshin on 20.02.2023.
 */
@Composable
fun TopAppBarTitle(
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    text: String,
    subText: String? = null,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            textAlign = textAlign,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )
        if (subText != null) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = subText,
                textAlign = textAlign,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
fun TopAppBarEmptyButton(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.size(48.dp))
}

@Composable
fun TopAppBarButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imageVector: ImageVector,
    imageDescription: String?,
    onButtonClick: () -> Unit,
    onDoubleButtonClick: (() -> Unit)? = null,
    onLongButtonClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ExtendedIconButton(
        modifier = modifier.size(48.dp),
        enabled = enabled,
        onClick = onButtonClick,
        onDoubleClick = onDoubleButtonClick,
        onLongClick = onLongButtonClick,
        interactionSource = interactionSource,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = imageDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun TopAppBarButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    imagePainter: Painter,
    imageDescription: String?,
    badge: (@Composable () -> Unit)? = null,
    onButtonClick: () -> Unit,
    onDoubleButtonClick: (() -> Unit)? = null,
    onLongButtonClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Box {
        if (badge != null) {
            Box(modifier = Modifier.padding(top = 4.dp, end = 2.dp).align(Alignment.TopEnd)) { badge() }
        }
        ExtendedIconButton(
            modifier = modifier.size(48.dp),
            enabled = enabled,
            onClick = onButtonClick,
            onDoubleClick = onDoubleButtonClick,
            onLongClick = onLongButtonClick,
            interactionSource = interactionSource,
        ) {
            Icon(
                painter = imagePainter,
                contentDescription = imageDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun <T : TopAppBarAction> TopAppBarMoreActions(
    modifier: Modifier = Modifier,
    items: Array<T>,
    onItemClick: (T) -> Unit,
    moreIconDescription: String?,
) {
    val expanded = rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = modifier.wrapContentSize(Alignment.TopEnd),
    ) {
        IconButton(onClick = { expanded.value = true }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = moreIconDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        DropdownMenu(
            expanded = expanded.value,
            offset = DpOffset(0.dp, 10.dp),
            onDismissRequest = { expanded.value = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.defaultMinSize(minWidth = 200.dp),
                            text = item.title,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    leadingIcon = if (item.icon != null) { {
                        Icon(
                            painter = painterResource(checkNotNull(item.icon)),
                            contentDescription = item.title,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } } else {
                        null
                    },
                    onClick = {
                        expanded.value = false
                        onItemClick.invoke(item)
                    },
                )
            }
        }
    }
}
