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
package ru.aleshin.features.editor.impl.presentation.ui.editor.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.material.surfaceThree
import ru.aleshin.core.ui.theme.material.surfaceTwo
import ru.aleshin.core.ui.views.CategoryIconMonogram
import ru.aleshin.core.ui.views.CategoryTextMonogram
import ru.aleshin.core.ui.views.toDaysTitle
import ru.aleshin.features.editor.impl.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.home.api.presentation.mappers.mapToIconPainter
import java.util.Date

/**
* @author Stanislav Aleshin on 04.11.2023.
*/
@Composable
@ExperimentalMaterial3Api
internal fun UndefinedTasksBottomSheet(
    modifier: Modifier = Modifier,
    isShow: Boolean,
    undefinedTasks: List<UndefinedTaskUi>?,
    currentUndefinedTaskId: Long?,
    onDismiss: () -> Unit,
    onChooseUndefinedTask: (UndefinedTaskUi) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val containerColor = MaterialTheme.colorScheme.surfaceThree()

    if (isShow) {
        ModalBottomSheet(
            modifier = modifier.height(462.dp),
            sheetState = sheetState,
            containerColor = containerColor,
            onDismissRequest = onDismiss,
        ) {
            UndefinedTasksBottomSheetHeader(tasksCount = undefinedTasks?.size)
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (undefinedTasks != null) {
                    if (undefinedTasks.isNotEmpty()) {
                        items(items = undefinedTasks, key = { it.id }) { task ->
                            UndefinedTaskBottomSheetItem(
                                enable = task.id != currentUndefinedTaskId,
                                model = task,
                                onChoose = { onChooseUndefinedTask(task) },
                            )
                        }
                    } else {
                        item {
                            Text(
                                modifier = Modifier.fillMaxSize(),
                                text = EditorThemeRes.strings.emptyTemplatesTitle,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    }
                }
            }
        }
    }
    BottomSheetSystemUi(
        isShow = isShow,
        containerColor = containerColor,
    )
}

@Composable
@ExperimentalMaterial3Api
internal fun UndefinedTasksBottomSheetHeader(
    modifier: Modifier = Modifier,
    tasksCount: Int?,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = EditorThemeRes.strings.undefinedTasksSheetTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
        )
        Badge(modifier = Modifier.align(Alignment.CenterVertically).width(22.dp)) {
            Text(tasksCount?.toString() ?: "-")
        }
    }
}

@Composable
internal fun UndefinedTaskBottomSheetItem(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    model: UndefinedTaskUi,
    onChoose: () -> Unit,
) {
    var expandedNote by rememberSaveable { mutableStateOf(false) }

    Surface(
        onClick = onChoose,
        modifier = modifier.alpha(if (enable) 1f else 0.6f),
        enabled = enable,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(modifier = Modifier.align(Alignment.Top)) {
                    val categoryIcon = model.mainCategory.defaultType?.mapToIconPainter()
                    if (categoryIcon != null) {
                        CategoryIconMonogram(
                            icon = categoryIcon,
                            iconDescription = null,
                            iconColor = MaterialTheme.colorScheme.primary,
                            badgeEnabled = model.isImportant,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    } else {
                        CategoryTextMonogram(
                            text = model.mainCategory.customName?.first().toString(),
                            textColor = MaterialTheme.colorScheme.primary,
                            badgeEnabled = model.isImportant,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    }
                }
                Column {
                    Text(
                        text = model.mainCategory.fetchName() ?: "",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (model.subCategory != null) {
                        Text(
                            text = model.subCategory.name ?: "",
                            modifier = Modifier.padding(top = 2.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (model.deadline != null) {
                    DeadlineView(deadline = model.deadline)
                }
            }
            if (!model.note.isNullOrEmpty()) {
                UndefinedTaskNoteView(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    onClick = { expandedNote = !expandedNote },
                    text = model.note,
                    expanded = expandedNote,
                    container = MaterialTheme.colorScheme.surfaceVariant,
                    content = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}

@Composable
internal fun DeadlineView(
    modifier: Modifier = Modifier,
    deadline: Date,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceTwo(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = EditorThemeRes.icons.deadline),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = (deadline.time - Date().time).toDaysTitle(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun UndefinedTaskNoteView(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    expanded: Boolean,
    container: Color = MaterialTheme.colorScheme.primary,
    content: Color = MaterialTheme.colorScheme.onPrimary,
) {
    var isOverflow by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && isOverflow || enabled && expanded,
        shape = MaterialTheme.shapes.medium,
        color = container,
    ) {
        AnimatedContent(
            targetState = expanded,
            label = "Note",
        ) { isExpanded ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = EditorThemeRes.icons.notes),
                    contentDescription = null,
                    tint = content,
                )
                Text(
                    text = text,
                    color = content,
                    maxLines = when (isExpanded) {
                        true -> 4
                        false -> 1
                    },
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { result -> isOverflow = result.didOverflowHeight },
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
