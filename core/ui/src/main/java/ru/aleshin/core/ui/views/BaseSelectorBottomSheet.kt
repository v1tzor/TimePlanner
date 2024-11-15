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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.full

/**
 * @author Stanislav Aleshin on 04.09.2024.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T> BaseSelectorBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    confirmEnabled: Boolean = true,
    selected: T?,
    items: List<T>,
    itemKeys: ((T) -> Any)? = null,
    header: String,
    title: String?,
    itemView: @Composable LazyItemScope.(T) -> Unit,
    notSelectedItem: @Composable (LazyItemScope.() -> Unit)? = null,
    addItemView: @Composable (LazyItemScope.() -> Unit)? = null,
    searchBar: @Composable (() -> Unit)? = null,
    filters: @Composable (RowScope.() -> Unit)? = null,
    itemsListState: LazyListState = rememberLazyListState(),
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    onDismissRequest: () -> Unit,
    onConfirm: (T?) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = containerColor,
        dragHandle = { MediumDragHandle() },
        properties = properties,
    ) {
        Column {
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column {
                    Text(
                        text = header,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    if (title != null) {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                searchBar?.invoke()
                if (filters != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        content = filters,
                    )
                }
            }
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.height(350.dp).padding(start = 16.dp, end = 16.dp, top = 12.dp),
                state = itemsListState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (notSelectedItem != null) {
                    item(content = notSelectedItem, key = "NotSelectedItem")
                }
                items(items = items, key = itemKeys, itemContent = itemView)
                if (addItemView != null) {
                    item(content = addItemView, key = "AddItem")
                }
            }
            Row(
                modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                ) {
                    Text(text = TimePlannerRes.strings.cancelTitle)
                }
                Button(
                    onClick = { onConfirm(selected) },
                    modifier = Modifier.weight(1f),
                    enabled = confirmEnabled
                ) {
                    Text(text = TimePlannerRes.strings.confirmTitle)
                }
            }

            var isShowedFirstItem by rememberSaveable { mutableStateOf(false) }
            LaunchedEffect(true) {
                if (!isShowedFirstItem && selected != null) {
                    val itemIndex = items.indexOf(selected)
                    if (itemIndex != -1) itemsListState.animateScrollToItem(itemIndex)
                }
                isShowedFirstItem = true
            }
        }
    }
}

@Composable
fun MediumDragHandle(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Surface(
            modifier = modifier.padding(vertical = 16.dp),
            color = color,
            shape = MaterialTheme.shapes.full,
        ) {
            Box(Modifier.size(width = 32.dp, height = 4.dp))
        }
    }
}