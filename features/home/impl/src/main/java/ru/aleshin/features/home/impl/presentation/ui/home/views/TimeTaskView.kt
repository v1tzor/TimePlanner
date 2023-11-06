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
package ru.aleshin.features.home.impl.presentation.ui.home.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.CategoryIconMonogram
import ru.aleshin.core.ui.views.CategoryTextMonogram
import ru.aleshin.core.ui.views.ExpandedIcon
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 22.02.2023.
 */
@Composable
internal fun PlannedTimeTask(
    modifier: Modifier = Modifier,
    onViewClicked: () -> Unit,
    taskTitle: String,
    taskSubTitle: String?,
    taskDurationTitle: String,
    categoryIcon: Painter?,
    isImportant: Boolean,
    note: String?,
) {
    var expandedNote by rememberSaveable { mutableStateOf(false) }

    Surface(
        onClick = onViewClicked,
        modifier = modifier,
        enabled = true,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(modifier = Modifier.align(Alignment.Top)) {
                    if (categoryIcon != null) {
                        CategoryIconMonogram(
                            icon = categoryIcon,
                            iconDescription = taskTitle,
                            iconColor = MaterialTheme.colorScheme.primary,
                            badgeEnabled = isImportant,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    } else {
                        CategoryTextMonogram(
                            text = taskTitle.first().toString(),
                            textColor = MaterialTheme.colorScheme.primary,
                            badgeEnabled = isImportant,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    }
                }
                TimeTaskTitles(
                    modifier = Modifier.weight(1f),
                    title = taskTitle,
                    titleColor = MaterialTheme.colorScheme.onSurface,
                    subTitle = taskSubTitle,
                )
                Box(
                    modifier = Modifier.align(
                        alignment = when (taskSubTitle == null) {
                            true -> Alignment.CenterVertically
                            false -> Alignment.Top
                        },
                    ),
                ) {
                    TimeTaskDurationTitle(title = taskDurationTitle)
                }
            }
            if (!note.isNullOrEmpty()) {
                TimeTaskNoteView(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    onClick = { expandedNote = !expandedNote },
                    text = note,
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
internal fun RunningTimeTask(
    modifier: Modifier = Modifier,
    onMoreButtonClick: () -> Unit,
    onIncreaseTime: () -> Unit,
    onReduceTime: () -> Unit,
    taskTitle: String,
    taskSubTitle: String?,
    categoryIcon: Painter?,
    isImportant: Boolean,
    note: String?,
) {
    var expandedTask by rememberSaveable { mutableStateOf(false) }
    var expandedNote by rememberSaveable { mutableStateOf(false) }

    Surface(
        onClick = { expandedTask = !expandedTask },
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp, top = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(modifier = Modifier.align(Alignment.Top)) {
                    if (categoryIcon != null) {
                        CategoryIconMonogram(
                            icon = categoryIcon,
                            iconDescription = taskTitle,
                            iconColor = MaterialTheme.colorScheme.onPrimary,
                            badgeEnabled = isImportant,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        CategoryTextMonogram(
                            text = taskTitle.first().toString(),
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            badgeEnabled = isImportant,
                        )
                    }
                }
                TimeTaskTitles(
                    modifier = Modifier.weight(1f),
                    title = taskTitle,
                    titleColor = MaterialTheme.colorScheme.onSurface,
                    subTitle = taskSubTitle,
                )
                Box(
                    modifier = Modifier.size(36.dp).animateContentSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    ExpandedIcon(
                        isExpanded = expandedTask,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        description = null,
                    )
                }
            }
            if (!note.isNullOrEmpty()) {
                TimeTaskNoteView(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    onClick = { expandedNote = !expandedNote },
                    text = note,
                    expanded = expandedNote,
                )
            } else {
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }
            if (expandedTask) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
                Row(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onIncreaseTime) {
                        Icon(
                            painter = painterResource(HomeThemeRes.icons.add),
                            contentDescription = HomeThemeRes.strings.timeTaskAddIconDesc,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = HomeThemeRes.strings.timeTaskIncreaseTimeTitle,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                    TextButton(onClick = onReduceTime) {
                        Icon(
                            painter = painterResource(HomeThemeRes.icons.remove),
                            contentDescription = HomeThemeRes.strings.timeTaskRemoveIconDesc,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = HomeThemeRes.strings.timeTaskReduceTimeTitle,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(modifier = Modifier.size(36.dp), onClick = onMoreButtonClick) {
                        Icon(
                            painter = painterResource(HomeThemeRes.icons.more),
                            contentDescription = HomeThemeRes.strings.timeTaskMoreIconDesc,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CompletedTimeTask(
    modifier: Modifier = Modifier,
    onViewClicked: () -> Unit,
    onDoneChange: () -> Unit,
    taskTitle: String,
    taskSubTitle: String?,
    categoryIcon: Painter?,
    isCompleted: Boolean,
    note: String?,
) {
    Surface(
        modifier = modifier,
        onClick = onViewClicked,
        enabled = true,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.align(Alignment.Top)) {
                if (categoryIcon != null) {
                    CategoryIconMonogram(
                        icon = categoryIcon,
                        iconDescription = taskTitle,
                        iconColor = MaterialTheme.colorScheme.onTertiary,
                        backgroundColor = MaterialTheme.colorScheme.tertiary,
                    )
                } else {
                    CategoryTextMonogram(
                        text = taskTitle.first().toString(),
                        textColor = MaterialTheme.colorScheme.onTertiary,
                        backgroundColor = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            TimeTaskTitles(
                modifier = Modifier.weight(1f),
                title = taskTitle,
                titleColor = MaterialTheme.colorScheme.onSurface,
                subTitle = taskSubTitle,
            )
            IconButton(
                modifier = Modifier.size(36.dp),
                onClick = onDoneChange,
            ) {
                if (isCompleted) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(HomeThemeRes.icons.check),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(HomeThemeRes.icons.cancel),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
internal fun AddTimeTaskView(
    modifier: Modifier = Modifier,
    onViewClicked: () -> Unit,
    remainingTimeTitle: String,
    isFreeTime: Boolean,
) {
    Surface(
        onClick = onViewClicked,
        modifier = modifier.height(46.dp),
        enabled = true,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Filled.Add,
                contentDescription = HomeThemeRes.strings.timeTaskIncreaseTimeTitle,
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (isFreeTime) HomeThemeRes.strings.addFreeTimeTaskTitle else HomeThemeRes.strings.addTaskTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isFreeTime) {
                Text(
                    text = remainingTimeTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun TimeTaskTitles(
    modifier: Modifier = Modifier,
    title: String,
    titleColor: Color,
    subTitle: String?,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            color = titleColor,
            style = MaterialTheme.typography.titleMedium,
        )
        if (subTitle != null) {
            Text(
                text = subTitle,
                modifier = Modifier.padding(top = 2.dp),
                color = titleColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun TimeTaskDurationTitle(
    modifier: Modifier = Modifier,
    title: String,
) = Text(
    modifier = modifier,
    text = title,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign = TextAlign.End,
    maxLines = 1,
    style = MaterialTheme.typography.bodyLarge,
)

@Composable
private fun TimeTaskNoteView(
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
                    painter = painterResource(id = HomeThemeRes.icons.notes),
                    contentDescription = HomeThemeRes.strings.noteTitle,
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

/* ----------------------- Release Preview -----------------------
@Preview
@Composable
private fun PlannedTimeTaskView_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.LIGHT,
        language = LanguageUiType.RU,
    ) {
        HomeTheme {
            PlannedTimeTask(
                modifier = Modifier.width(330.dp),
                onViewClicked = {},
                taskTitle = "Дела",
                taskSubTitle = null,
                taskDurationTitle = "4ч",
                categoryIcon = painterResource(id = HomeThemeRes.icons.settingsIcon),
                categoryIconDescription = "",
            )
        }
    }
}

@Preview
@Composable
private fun RunningTimeTaskView_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.LIGHT,
        language = LanguageUiType.RU,
    ) {
        HomeTheme {
            RunningTimeTask(
                modifier = Modifier.width(330.dp),
                taskTitle = "Дела",
                taskSubTitle = "Выполнить домашнее задание",
                categoryIcon = painterResource(id = HomeThemeRes.icons.settingsIcon),
                categoryIconDescription = "",
                onMoreButtonClick = {},
                onIncreaseTime = {},
                onReduceTime = {},
            )
        }
    }
}

@Preview
@Composable
private fun CompletedTimeTaskView_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.LIGHT,
        language = LanguageUiType.RU,
    ) {
        HomeTheme {
            CompletedTimeTask(
                modifier = Modifier.width(330.dp),
                onViewClicked = {},
                taskTitle = "Дела",
                taskSubTitle = "Выполнить домашнее задание",
                categoryIcon = painterResource(id = HomeThemeRes.icons.settingsIcon),
                categoryIconDescription = "",
            )
        }
    }
}
*/
