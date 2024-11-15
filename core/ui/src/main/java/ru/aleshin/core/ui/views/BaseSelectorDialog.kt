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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.utils.extensions.alphaByEnabled

/**
 * @author Stanislav Aleshin on 02.06.2024.
 */
@Composable
@ExperimentalMaterial3Api
fun <T> BaseSelectorDialog(
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    selected: T?,
    items: List<T>,
    itemKeys: ((T) -> Any)? = null,
    header: String,
    title: String?,
    itemView: @Composable LazyItemScope.(T) -> Unit,
    notSelectedItem: @Composable (LazyItemScope.() -> Unit)? = null,
    addItemView: @Composable (LazyItemScope.() -> Unit)? = null,
    filters: @Composable (RowScope.() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
    sizes: SelectorDialogSizes = SelectorDialogSizes(),
    itemsListState: LazyListState = rememberLazyListState(),
    shadowElevation: Dp = 4.dp,
    onDismiss: () -> Unit,
    onConfirm: (T?) -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = properties,
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.width(sizes.dialogWidth).wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer,
            shadowElevation = shadowElevation,
        ) {
            Column {
                DialogHeader(
                    header = header,
                    title = title,
                )
                if (filters != null) {
                    Row(
                        modifier = Modifier.padding(sizes.filtersPaddings),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        content = filters,
                    )
                }
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier.height(sizes.contentHeight).padding(sizes.itemsListPaddings),
                    state = itemsListState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (notSelectedItem != null) {
                        item(content = notSelectedItem)
                    }
                    items(items = items, key = itemKeys, itemContent = itemView)
                    if (addItemView != null) {
                        item(content = addItemView)
                    }
                }
                DialogButtons(
                    enabledConfirm = confirmEnabled,
                    confirmTitle = TimePlannerRes.strings.confirmTitle,
                    onCancelClick = onDismiss,
                    onConfirmClick = { onConfirm(selected) },
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun LazyItemScope.SelectorItemView(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean,
    title: String,
    label: String?,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    Surface(
        onClick = { if (!selected) onClick() },
        enabled = enabled,
        modifier = modifier
            .alphaByEnabled(enabled)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .animateItemPlacement(),
        shape = MaterialTheme.shapes.large,
        color = when (selected) {
            true -> MaterialTheme.colorScheme.primaryContainer
            false -> MaterialTheme.colorScheme.surfaceContainerHigh
        }
    ) {
        Row(
            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Text(
                    text = title,
                    color = when (selected) {
                        true -> MaterialTheme.colorScheme.onPrimaryContainer
                        false -> MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = Modifier.size(24.dp),
                enabled = enabled,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

@Composable
fun LazyItemScope.SelectorSwipeItemView(
    onClick: () -> Unit,
    state: SwipeToDismissBoxState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean,
    title: String,
    label: String?,
    leadingIcon: (@Composable () -> Unit)? = null,
    gesturesEnabled: Boolean = true,
    enableDismissFromStartToEnd: Boolean = true,
    enableDismissFromEndToStart: Boolean = true,
    backgroundContent: @Composable RowScope.() -> Unit,
) {
    SwipeToDismissBox(
        modifier = modifier.animateItem(),
        state = state,
        backgroundContent = backgroundContent,
        enableDismissFromEndToStart = enableDismissFromEndToStart,
        enableDismissFromStartToEnd = enableDismissFromStartToEnd,
        gesturesEnabled = gesturesEnabled,
    ) {
        SelectorItemView(
            onClick = onClick,
            enabled = enabled,
            selected = selected,
            title = title,
            label = label,
            leadingIcon = leadingIcon,
        )
    }
}

@Composable
fun LazyItemScope.SelectorAddItemView(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.alphaByEnabled(enabled).fillMaxWidth().height(40.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
fun LazyItemScope.SelectorTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    BasicTextField(
        modifier = modifier.alphaByEnabled(enabled).height(40.dp),
        enabled = enabled,
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
    ) { innerTextField ->
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = Color.Transparent,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Row(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 8.dp,
                    end = 8.dp,
                    bottom = 8.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f)) {
                    innerTextField()
                }
                IconButton(
                    onClick = onConfirm,
                    modifier = Modifier.size(24.dp),
                    enabled = enabled,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Green,
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp),
                    enabled = enabled,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Red,
                    )
                }
            }
        }
    }
}

@Composable
fun LazyItemScope.SelectorNotSelectedItemView(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    selected: Boolean,
    onClick: () -> Unit,
) = SelectorItemView(
    modifier = modifier,
    enabled = enabled,
    onClick = onClick,
    selected = selected,
    title = text,
    label = null,
)

data class SelectorDialogSizes(
    val dialogWidth: Dp = 350.dp,
    val contentHeight: Dp = 300.dp,
    val filtersPaddings: PaddingValues = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
    val itemsListPaddings: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
)