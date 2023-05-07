package ru.aleshin.features.home.impl.presentation.ui.home.views
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
 * imitations under the License.
 */
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.TimePlannerTheme
import ru.aleshin.core.ui.theme.material.ThemeColorsUiType
import ru.aleshin.core.ui.theme.tokens.LanguageUiType
import ru.aleshin.core.ui.views.CategoryIconMonogram
import ru.aleshin.core.ui.views.CategoryTextMonogram
import ru.aleshin.core.ui.views.ExpandedIcon
import ru.aleshin.features.home.impl.presentation.theme.HomeTheme
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
    categoryIconDescription: String?,
) {
    Surface(
        onClick = onViewClicked,
        modifier = modifier,
        enabled = true,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (categoryIcon != null) {
                CategoryIconMonogram(
                    icon = categoryIcon,
                    iconDescription = categoryIconDescription,
                    iconColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                )
            } else {
                CategoryTextMonogram(
                    text = taskTitle.first().toString(),
                    textColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                )
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
    categoryIconDescription: String?,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Surface(
        onClick = { expanded = !expanded },
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(modifier = Modifier.align(Alignment.Top)) {
                    if (categoryIcon != null) {
                        CategoryIconMonogram(
                            icon = categoryIcon,
                            iconDescription = categoryIconDescription,
                            iconColor = MaterialTheme.colorScheme.onPrimary,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        CategoryTextMonogram(
                            text = taskTitle.first().toString(),
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            backgroundColor = MaterialTheme.colorScheme.primary,
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
                        isExpanded = expanded,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        description = categoryIconDescription,
                    )
                }
            }
            if (expanded) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Divider(modifier = Modifier.fillMaxWidth())
                }
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
    taskTitle: String,
    taskSubTitle: String?,
    categoryIcon: Painter?,
    categoryIconDescription: String?,
) {
    Surface(
        modifier = modifier,
        onClick = onViewClicked,
        enabled = true,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.align(Alignment.Top)) {
                if (categoryIcon != null) {
                    CategoryIconMonogram(
                        icon = categoryIcon,
                        iconDescription = categoryIconDescription,
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
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(HomeThemeRes.icons.check),
                contentDescription = HomeThemeRes.strings.timeTaskCheckIconDesc,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun AddTimeTaskView(
    modifier: Modifier = Modifier,
    onViewClicked: () -> Unit,
    remainingTimeTitle: String,
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
                text = HomeThemeRes.strings.addTimeTaskTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = remainingTimeTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TimeTaskTitles(
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
