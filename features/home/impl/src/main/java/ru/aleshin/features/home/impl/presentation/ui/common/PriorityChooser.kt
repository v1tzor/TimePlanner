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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.features.home.api.domain.entities.schedules.TaskPriority
import ru.aleshin.features.home.impl.presentation.mapppers.mapToString
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 02.11.2023
 */
@Composable
internal fun PriorityChooser(
    modifier: Modifier = Modifier,
    priority: TaskPriority,
    onPriorityChange: (TaskPriority) -> Unit,
) {
    var isOpenPriorityMenu by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.priority),
            contentDescription = HomeThemeRes.strings.priorityLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = HomeThemeRes.strings.priorityLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(contentAlignment = Alignment.TopEnd) {
            SuggestionChip(
                label = { Text(text = priority.mapToString()) },
                onClick = { isOpenPriorityMenu = true },
            )
            PriorityMenu(
                isExpanded = isOpenPriorityMenu,
                selected = priority,
                onDismiss = { isOpenPriorityMenu = false },
                onChoose = { mainCategory ->
                    isOpenPriorityMenu = false
                    onPriorityChange(mainCategory)
                },
            )
        }
    }
}

@Composable
internal fun PriorityMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    selected: TaskPriority,
    onDismiss: () -> Unit,
    onChoose: (TaskPriority) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 200.dp),
        offset = DpOffset(0.dp, 6.dp),
    ) {
        TaskPriority.values().forEach { priority ->
            DropdownMenuItem(
                enabled = selected != priority,
                onClick = { onChoose(priority) },
                text = {
                    Text(
                        text = priority.mapToString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
            )
        }
    }
}
